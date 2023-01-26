package com.example.vitamin_app.SurveyFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitamin_app.ToDoProblemList;
import com.example.vitamin_app.R;
import com.example.vitamin_app.Activities.ResultListActivity;
import com.example.vitamin_app.ToDoDatabaseHandler;
import com.example.vitamin_app.Users;
import com.example.vitamin_app.VitaminRecDatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SurveyDoubleProblemFragment extends Fragment {

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    // Database for tasks on profile page
    ToDoDatabaseHandler db;

    // creating a variable for
    // our object class
    Users user;

    // to store data from user's database
    String age;
    String gender;

    TextView problem1;
    TextView problem2;

    int count = 0;

    Fragment triple1 = null;
    Fragment double1 = null;
    Fragment triple2 = null;
    Fragment double2 = null;
    Fragment single1 = null;
    Fragment single2 = null;

    String vit1;
    String vit2;
    String vit3;

    ArrayList<Fragment> array = new ArrayList<Fragment>();

    InputStream inputStream;
    static ArrayList<String[]> databaselist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_survey_double_problem, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Users");

        // Opening database for tasks
        db = new ToDoDatabaseHandler(getContext());
        db.openDatabase();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String username = currentUser.getDisplayName();
        String email = currentUser.getEmail();

        Bundle bundle = this.getArguments();
        problem1 = (TextView) v.findViewById(R.id.text_problem1);
        problem2 = (TextView) v.findViewById(R.id.text_problem2);
        SeekBar seek1 = (SeekBar) v.findViewById(R.id.seekBar);
        SeekBar seek2 = (SeekBar) v.findViewById(R.id.seekBar2);

        // Retrieving user data from firebase
        databaseReference.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        age = String.valueOf(dataSnapshot.child("age").getValue());
                        gender = String.valueOf(dataSnapshot.child("gender").getValue());
                        user = new Users(email, username, gender, age);
                        user.setNum_problem(2);
                        // Call helper function to initialise fragments AFTER having retrieved info from DB
                        checkboxFragment(bundle);
                    } else {
                        Toast.makeText(v.getContext(), "User does not exist",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), "Failed to read data",Toast.LENGTH_LONG).show();
                }
            }
        });

        //Toast.makeText(v.getContext(), " "+ age,Toast.LENGTH_LONG).show();

        // Creating the database for the vitamin recommendations
        inputStream = getResources().openRawResource(R.raw.database_recomendation);
        VitaminRecDatabaseHandler vitaminRecDatabaseHandler = new VitaminRecDatabaseHandler(getActivity());
        File file = new File("/data/data/com.example.vitamin_app/databases/vitamin_rec.db");
        file.delete();
        VitaminRecDatabaseHandler vitaminRecDatabaseHelper = new VitaminRecDatabaseHandler(getActivity());
        BufferedInputStream bf = new BufferedInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bf, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] str = line.split(",");
                vitaminRecDatabaseHandler.addCSV(str[0], str[1], str[2], str[3], str[4]);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        ArrayList<String[]> list = vitaminRecDatabaseHelper.getData();
        databaselist = list;

        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    seek1.setProgress(1);
                } else if (i == 1) {
                    seek2.setProgress(3);
                } else if (i == 2) {
                    seek2.setProgress(2);
                } else if (i == 3) {
                    seek2.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    seek2.setProgress(1);
                } else if (i == 1) {
                    seek1.setProgress(3);
                } else if (i == 2) {
                    seek1.setProgress(2);
                } else if (i == 3) {
                    seek1.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        Button endSurvey = (Button) v.findViewById(R.id.endSurvey2);
        endSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assign supplements based on which problem from the checkbox fragment was selected
                // Accounts for both age and gender of the user
                // Additionally, accounts for the ratio of supplements the user wants - determined by the sliders
                // Also code needs to account on whether the problem was checked first or second (the order) to make
                // sure it's assigning the ratio correctly based on seek bar 1 seek bar 2, and also assigning the supplements
                // correctly in the DB

                // Create ID string to search in database
                String ID = "";
                String ID2 = "";
                if (gender.equals("Male")) {
                    ID += "M";
                    ID2 += "M";
                } else {
                    ID += "F";
                    ID2 += "F";
                }
                if (age.equals("12-20")) {
                    ID += "A";
                    ID2 += "A";
                } else if (age.equals("20-60")) {
                    ID += "B";
                    ID2 += "B";
                } else {
                    ID += "C";
                    ID2 += "C";
                }

                int index;
                boolean check = true;
                db.deleteProblemTasks();
                int count = 0;
                if (bundle.getBoolean(ToDoProblemList.WEIGHT)) {
                    ID += "001";
                    user.setProblem(ToDoProblemList.WEIGHT);
                    db.insertProblemTask(ToDoProblemList.WEIGHT);
                    RadioButton triple_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem3);
                    if (triple_p1.isChecked()) {
                        ID += "W";
                    } else if (triple_p2.isChecked()) {
                        ID +="Y";
                    } else if (triple_p3.isChecked()) {
                        ID += "X";
                    } else {
                        Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    index = search(databaselist, ID, user);
                    user.setSupplement1(databaselist.get(index)[1]);
                    user.setSupplement2(databaselist.get(index)[2]);
                    user.setSupplement3(databaselist.get(index)[3]);
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.SLEEP)) {
                    db.insertProblemTask(ToDoProblemList.SLEEP);
                    RadioButton triple_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem3);
                    if (count == 0) {
                        ID += "002";
                        user.setProblem(ToDoProblemList.SLEEP);
                        if (triple_p1.isChecked()) {
                            ID += "Y";
                        } else if (triple_p2.isChecked()) {
                            ID +="X";
                        } else if (triple_p3.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID, user);
                        user.setSupplement1(databaselist.get(index)[1]);
                        user.setSupplement2(databaselist.get(index)[2]);
                        user.setSupplement3(databaselist.get(index)[3]);
                    } else {
                        ID2 += "002";
                        user.setProblem2(ToDoProblemList.SLEEP);
                        if (triple_p1.isChecked()) {
                            ID2 += "Y";
                        } else if (triple_p2.isChecked()) {
                            ID2 +="X";
                        } else if (triple_p3.isChecked()) {
                            ID2 += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID2, user);
                        if (seek2.getProgress() == 1) {
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else if (seek2.getProgress() == 2) {
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else {
                            user.setSupplement2(databaselist.get(index)[3]);
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        }
                    }
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.ENERGY)) {
                    db.insertProblemTask(ToDoProblemList.ENERGY);
                    RadioButton double_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem1);
                    if (!age.equals("20-60")) {
                        if (count == 0) {
                            ID += "003";
                            user.setProblem(ToDoProblemList.ENERGY);
                            if (double_p1.isChecked()) {
                                ID += "Y";
                            } else {
                                Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                                check = false;
                            }
                            index = search(databaselist, ID, user);
                            user.setSupplement1(databaselist.get(index)[1]);
                            user.setSupplement2(databaselist.get(index)[2]);
                            user.setSupplement3(databaselist.get(index)[3]);
                        } else {
                            ID2 += "003";
                            user.setProblem2(ToDoProblemList.ENERGY);
                            if (double_p1.isChecked()) {
                                ID2 += "Y";
                            } else {
                                Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                                check = false;
                            }
                            index = search(databaselist, ID2, user);
                            if (seek2.getProgress() == 1) {
                                user.setSupplement4(databaselist.get(index)[1]);
                            } else if (seek2.getProgress() == 2) {
                                user.setSupplement3(databaselist.get(index)[2]);
                                user.setSupplement4(databaselist.get(index)[1]);
                            } else {
                                user.setSupplement2(databaselist.get(index)[3]);
                                user.setSupplement3(databaselist.get(index)[2]);
                                user.setSupplement4(databaselist.get(index)[1]);
                            }
                        }
                    } else {
                        RadioButton double_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem2);
                        if (count == 0) {
                            ID += "003";
                            user.setProblem(ToDoProblemList.ENERGY);
                            if (double_p1.isChecked()) {
                                ID += "Y";
                            } else if (double_p2.isChecked()) {
                                ID += "W";
                            } else {
                                Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                                check = false;
                            }
                            index = search(databaselist, ID, user);
                            user.setSupplement1(databaselist.get(index)[1]);
                            user.setSupplement2(databaselist.get(index)[2]);
                            user.setSupplement3(databaselist.get(index)[3]);
                        } else {
                            ID2 += "003";
                            user.setProblem2(ToDoProblemList.ENERGY);
                            if (double_p1.isChecked()) {
                                ID2 += "Y";
                            } else if (double_p2.isChecked()) {
                                ID2 += "W";
                            } else {
                                Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                                check = false;
                            }
                            index = search(databaselist, ID2, user);
                            if (seek2.getProgress() == 1) {
                                user.setSupplement4(databaselist.get(index)[1]);
                            } else if (seek2.getProgress() == 2) {
                                user.setSupplement3(databaselist.get(index)[2]);
                                user.setSupplement4(databaselist.get(index)[1]);
                            } else {
                                user.setSupplement2(databaselist.get(index)[3]);
                                user.setSupplement3(databaselist.get(index)[2]);
                                user.setSupplement4(databaselist.get(index)[1]);
                            }
                        }
                    }
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.IMMUNITY)) {
                    db.insertProblemTask(ToDoProblemList.IMMUNITY);
                    RadioButton double_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem2);
                    if (count == 0) {
                        ID += "004";
                        user.setProblem(ToDoProblemList.IMMUNITY);
                        if (double_p1.isChecked()) {
                            ID += "Y";
                        } else if (double_p2.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID, user);
                        user.setSupplement1(databaselist.get(index)[1]);
                        user.setSupplement2(databaselist.get(index)[2]);
                        user.setSupplement3(databaselist.get(index)[3]);
                    } else {
                        ID2 += "004";
                        user.setProblem2(ToDoProblemList.IMMUNITY);
                        if (double_p1.isChecked()) {
                            ID2 += "Y";
                        } else if (double_p2.isChecked()) {
                            ID2 += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID2, user);
                        if (seek2.getProgress() == 1) {
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else if (seek2.getProgress() == 2) {
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else {
                            user.setSupplement2(databaselist.get(index)[3]);
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        }
                    }
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.SKIN)) {
                    db.insertProblemTask(ToDoProblemList.SKIN);
                    RadioButton triple_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem3);
                    if (count == 0) {
                        ID += "005";
                        user.setProblem(ToDoProblemList.SKIN);
                        if (triple_p1.isChecked()) {
                            ID += "Y";
                        } else if (triple_p2.isChecked()) {
                            ID += "X";
                        } else if (triple_p3.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID, user);
                        user.setSupplement1(databaselist.get(index)[1]);
                        user.setSupplement2(databaselist.get(index)[2]);
                        user.setSupplement3(databaselist.get(index)[3]);
                    } else {
                        ID2 += "005";
                        user.setProblem2(ToDoProblemList.SKIN);
                        if (triple_p1.isChecked()) {
                            ID2 += "Y";
                        } else if (triple_p2.isChecked()) {
                            ID2 += "X";
                        } else if (triple_p3.isChecked()) {
                            ID2 += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID2, user);
                        if (seek2.getProgress() == 1) {
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else if (seek2.getProgress() == 2) {
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else {
                            user.setSupplement2(databaselist.get(index)[3]);
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        }
                    }
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.DETOX)) {
                    db.insertProblemTask(ToDoProblemList.DETOX);
                    RadioButton triple_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) array.get(count).getView().findViewById(R.id.triple_problem3);
                    if (count == 0) {
                        ID += "007";
                        user.setProblem(ToDoProblemList.DETOX);
                        if (triple_p1.isChecked()) {
                            ID += "Y";
                        } else if (triple_p2.isChecked()) {
                            ID += "X";
                        } else if (triple_p3.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID, user);
                        user.setSupplement1(databaselist.get(index)[1]);
                        user.setSupplement2(databaselist.get(index)[2]);
                        user.setSupplement3(databaselist.get(index)[3]);
                    } else {
                        ID2 += "007";
                        user.setProblem2(ToDoProblemList.DETOX);
                        if (triple_p1.isChecked()) {
                            ID2 += "Y";
                        } else if (triple_p2.isChecked()) {
                            ID2 += "X";
                        } else if (triple_p3.isChecked()) {
                            ID2 += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID2, user);
                        if (seek2.getProgress() == 1) {
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else if (seek2.getProgress() == 2) {
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else {
                            user.setSupplement2(databaselist.get(index)[3]);
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        }
                    }
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.EXERCISE)) {
                    db.insertProblemTask(ToDoProblemList.EXERCISE);
                    RadioButton double_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem2);
                    if (count == 0) {
                        ID += "006";
                        user.setProblem(ToDoProblemList.EXERCISE);
                        if (double_p1.isChecked()) {
                            ID += "Y";
                        } else if (double_p2.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID, user);
                        user.setSupplement1(databaselist.get(index)[1]);
                        user.setSupplement2(databaselist.get(index)[2]);
                        user.setSupplement3(databaselist.get(index)[3]);
                    } else {
                        ID2 += "006";
                        user.setProblem2(ToDoProblemList.EXERCISE);
                        if (double_p1.isChecked()) {
                            ID2 += "Y";
                        } else if (double_p2.isChecked()) {
                            ID2 += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID2, user);
                        if (seek2.getProgress() == 1) {
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else if (seek2.getProgress() == 2) {
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else {
                            user.setSupplement2(databaselist.get(index)[3]);
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        }
                    }
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.DIGESTION)) {
                    db.insertProblemTask(ToDoProblemList.DIGESTION);
                    RadioButton double_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem2);
                    if (count == 0) {
                        ID += "008";
                        user.setProblem(ToDoProblemList.DIGESTION);
                        if (double_p1.isChecked()) {
                            ID += "Y";
                        } else if (double_p2.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID, user);
                        user.setSupplement1(databaselist.get(index)[1]);
                        user.setSupplement2(databaselist.get(index)[2]);
                        user.setSupplement3(databaselist.get(index)[3]);
                    } else {
                        ID2 += "008";
                        user.setProblem2(ToDoProblemList.DIGESTION);
                        if (double_p1.isChecked()) {
                            ID2 += "Y";
                        } else if (double_p2.isChecked()) {
                            ID2 += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID2, user);
                        if (seek2.getProgress() == 1) {
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else if (seek2.getProgress() == 2) {
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else {
                            user.setSupplement2(databaselist.get(index)[3]);
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        }
                    }
                    count++;
                } if (bundle.getBoolean(ToDoProblemList.ARTICULATION)) {
                    db.insertProblemTask(ToDoProblemList.ARTICULATION);
                    RadioButton double_p1 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) array.get(count).getView().findViewById(R.id.double_problem2);
                    if (count == 0) {
                        ID += "009";
                        user.setProblem(ToDoProblemList.ARTICULATION);
                        if (double_p1.isChecked()) {
                            ID += "Y";
                        } else if (double_p2.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID, user);
                        user.setSupplement1(databaselist.get(index)[1]);
                        user.setSupplement2(databaselist.get(index)[2]);
                        user.setSupplement3(databaselist.get(index)[3]);
                    } else {
                        ID2 += "009";
                        user.setProblem2(ToDoProblemList.ARTICULATION);
                        if (double_p1.isChecked()) {
                            ID2 += "Y";
                        } else if (double_p2.isChecked()) {
                            ID2 += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                        index = search(databaselist, ID2, user);
                        if (seek2.getProgress() == 1) {
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else if (seek2.getProgress() == 2) {
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        } else {
                            user.setSupplement2(databaselist.get(index)[3]);
                            user.setSupplement3(databaselist.get(index)[2]);
                            user.setSupplement4(databaselist.get(index)[1]);
                        }
                    }
                    count++;
                }

                if (check) {
                    databaseReference.child(username).setValue(user);

                    Intent intent = new Intent(view.getContext(), ResultListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    view.getContext().startActivity(intent);
                }
            }
        });

        return v;
    }

    public void checkboxFragment(Bundle bundle) {
        // Initialise checkbox fragments depending on problem previously selected
        // Needs to take into account the order of the problems selected
        if (bundle.getBoolean("Weight")) {
            triple1 = new SurveyTripleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            triple1.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout1, triple1);
            fragmentTransaction.commit();
            problem1.setText("Weightloss");
            array.add(triple1);
            count++;
        } if (bundle.getBoolean("Sleep")) {
            if (count == 0) {
                triple1 = new SurveyTripleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                triple1.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout1, triple1);
                problem1.setText("Sleep");
                array.add(triple1);
                fragmentTransaction.commit();
            } else {
                triple2 = new SurveyTripleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle frag_bundle = new Bundle();
                frag_bundle.putBoolean("Sleep", true);
                triple2.setArguments(frag_bundle);
                fragmentTransaction.replace(R.id.checkboxLayout2, triple2);
                problem2.setText("Sleep");
                array.add(triple2);
                fragmentTransaction.commit();
            }
            count++;
        } if (bundle.getBoolean("Energy")) {
            if (count == 0) {
                if (!age.equals("20-60")) {
                    single1 = new SurveySingleCheckboxFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    single1.setArguments(bundle);
                    fragmentTransaction.replace(R.id.checkboxLayout1, single1);
                    problem1.setText("Energy");
                    array.add(single1);
                    fragmentTransaction.commit();
                } else {
                    double1 = new SurveyDoubleCheckboxFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    double1.setArguments(bundle);
                    fragmentTransaction.replace(R.id.checkboxLayout1, double1);
                    problem1.setText("Energy");
                    array.add(double1);
                    fragmentTransaction.commit();
                }
            } else {
                if (!age.equals("20-60")) {
                    single2 = new SurveySingleCheckboxFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Bundle frag_bundle = new Bundle();
                    frag_bundle.putBoolean("Energy", true);
                    single2.setArguments(frag_bundle);
                    fragmentTransaction.replace(R.id.checkboxLayout2, single2);
                    problem2.setText("Energy");
                    array.add(single2);
                    fragmentTransaction.commit();
                } else {
                    double2 = new SurveyDoubleCheckboxFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Bundle frag_bundle = new Bundle();
                    frag_bundle.putBoolean("Energy", true);
                    double2.setArguments(frag_bundle);
                    fragmentTransaction.replace(R.id.checkboxLayout2, double2);
                    problem2.setText("energy");
                    array.add(double2);
                    fragmentTransaction.commit();
                }
            }
            count++;
        } if (bundle.getBoolean("Immunity")) {
            if (count == 0) {
                double1 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                double1.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout1, double1);
                problem1.setText("Immunity");
                array.add(double1);
                fragmentTransaction.commit();
            } else {
                double2 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle frag_bundle = new Bundle();
                frag_bundle.putBoolean("Immunity", true);
                double2.setArguments(frag_bundle);
                fragmentTransaction.replace(R.id.checkboxLayout2, double2);
                problem2.setText("Immunity");
                array.add(double2);
                fragmentTransaction.commit();
            }
            count++;
        } if (bundle.getBoolean("Skin")) {
            if (count == 0) {
                triple1 = new SurveyTripleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                triple1.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout1, triple1);
                problem1.setText("Skin");
                array.add(triple1);
                fragmentTransaction.commit();
            } else {
                triple2 = new SurveyTripleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle frag_bundle = new Bundle();
                frag_bundle.putBoolean("Skin", true);
                triple2.setArguments(frag_bundle);
                fragmentTransaction.replace(R.id.checkboxLayout2, triple2);
                problem2.setText("Skin");
                array.add(triple2);
                fragmentTransaction.commit();
            }
            count++;
        } if (bundle.getBoolean("Detox")) {
            if (count == 0) {
                triple1 = new SurveyTripleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                triple1.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout1, triple1);
                problem1.setText("Detox");
                array.add(triple1);
                fragmentTransaction.commit();
            } else {
                triple2 = new SurveyTripleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle frag_bundle = new Bundle();
                frag_bundle.putBoolean("Detox", true);
                triple2.setArguments(frag_bundle);
                fragmentTransaction.replace(R.id.checkboxLayout2, triple2);
                problem2.setText("Detox");
                array.add(triple2);
                fragmentTransaction.commit();
            }
            count++;
        } if (bundle.getBoolean("Exercise")) {
            if (count == 0) {
                double1 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                double1.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout1, double1);
                problem1.setText("Exercise");
                array.add(double1);
                fragmentTransaction.commit();
            } else {
                double2 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle frag_bundle = new Bundle();
                frag_bundle.putBoolean("Exercise", true);
                double2.setArguments(frag_bundle);
                fragmentTransaction.replace(R.id.checkboxLayout2, double2);
                problem2.setText("Exercise");
                array.add(double2);
                fragmentTransaction.commit();
            }
            count++;
        } if (bundle.getBoolean("Digestion")) {
            if (count == 0) {
                double1 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                double1.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout1, double1);
                problem1.setText("Digestion");
                array.add(double1);
                fragmentTransaction.commit();
            } else {
                double2 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle frag_bundle = new Bundle();
                frag_bundle.putBoolean("Digestion", true);
                double2.setArguments(frag_bundle);
                fragmentTransaction.replace(R.id.checkboxLayout2, double2);
                problem2.setText("Digestion");
                array.add(double2);
                fragmentTransaction.commit();
            }
            count++;
        } if (bundle.getBoolean("Articulation")) {
            if (count == 0) {
                double1 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                double1.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout1, double1);
                problem1.setText("Articulation");
                array.add(double1);
                fragmentTransaction.commit();
            } else {
                double2 = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle frag_bundle = new Bundle();
                frag_bundle.putBoolean("Articulation", true);
                double2.setArguments(frag_bundle);
                fragmentTransaction.replace(R.id.checkboxLayout2, double2);
                problem2.setText("Articulation");
                array.add(double2);
                fragmentTransaction.commit();
            }
            count++;
        }
    }

    public static int search(ArrayList<String[]> db, String ID, Users user) {
        int index = -1;
        for(int i = 0; i < db.size(); i++){
            if(db.get(i)[0].equals(ID)){
                return i;
            }
        }
        return -1;
    }
}