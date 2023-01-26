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
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitamin_app.Activities.QuickFixActivity;
import com.example.vitamin_app.ToDoProblemList;
import com.example.vitamin_app.R;
import com.example.vitamin_app.Activities.ResultListActivity;
import com.example.vitamin_app.ToDoDatabaseHandler;
import com.example.vitamin_app.Users;
import com.example.vitamin_app.VitaminRecDatabaseHandler;
import com.firebase.ui.auth.data.model.User;
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

public class SurveySingleProblemFragment extends Fragment {

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    // Database for tasks on profile page
    private ToDoDatabaseHandler db;

    // creating a variable for
    // our object class
    Users user;
    String age;
    String gender;

    TextView header;

    Fragment triple = null;
    Fragment doublle = null;
    Fragment single = null;

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
        View v = inflater.inflate(R.layout.fragment_survey_single_problem, container, false);

        Bundle bundle = this.getArguments();
        header = (TextView) v.findViewById(R.id.problem_header);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Users");

        // Opening database for tasks
        db = new ToDoDatabaseHandler(getContext());
        db.openDatabase();

        // Retrieve username from SQL database to use for firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String username = currentUser.getDisplayName();
        String email = currentUser.getEmail();

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
                        user.setNum_problem(1);
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

        Button endSurvey = (Button) v.findViewById(R.id.endSurvey);
        endSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assign supplements based on which problem from the checkbox fragment was selected
                // Accounts for both age and gender of the user

                boolean check = true;
                db.deleteProblemTasks();

                // Create ID string to search in database
                String ID = "";
                if (gender.equals("Male")) {
                    ID += "M";
                } else {
                    ID += "F";
                }
                if (age.equals("12-20")) {
                    ID += "A";
                } else if (age.equals("20-60")) {
                    ID += "B";
                } else {
                    ID += "C";
                }

                if (bundle.getBoolean(ToDoProblemList.WEIGHT)) {
                    ID += "001";
                    user.setProblem(ToDoProblemList.WEIGHT);
                    db.insertProblemTask(ToDoProblemList.WEIGHT);
                    RadioButton triple_p1 = (RadioButton) triple.getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) triple.getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) triple.getView().findViewById(R.id.triple_problem3);
                    if (triple_p1.isChecked()) {
                        ID += "W";
                        //Toast.makeText(v.getContext(),ID,Toast.LENGTH_SHORT).show();
                    } else if (triple_p2.isChecked()) {
                        ID +="Y";
                    } else if (triple_p3.isChecked()) {
                        ID += "X";
                    } else {
                        Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.SLEEP)) {
                    ID += "002";
                    user.setProblem(ToDoProblemList.SLEEP);
                    db.insertProblemTask(ToDoProblemList.SLEEP);
                    RadioButton triple_p1 = (RadioButton) triple.getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) triple.getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) triple.getView().findViewById(R.id.triple_problem3);
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
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.ENERGY)) {
                    ID += "003";
                    user.setProblem(ToDoProblemList.ENERGY);
                    db.insertProblemTask(ToDoProblemList.ENERGY);
                    // Only show one option for people over the age of 60
                    if (!age.equals("20-60")) {
                        RadioButton double_p1 = (RadioButton) single.getView().findViewById(R.id.double_problem1);
                        if (double_p1.isChecked()) {
                            ID += "Y";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                    } else {
                        RadioButton double_p1 = (RadioButton) doublle.getView().findViewById(R.id.double_problem1);
                        RadioButton double_p2 = (RadioButton) doublle.getView().findViewById(R.id.double_problem2);
                        if (double_p1.isChecked()) {
                            ID += "Y";
                        } else if (double_p2.isChecked()) {
                            ID += "W";
                        } else {
                            Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                    }
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.IMMUNITY)) {
                    ID += "004";
                    user.setProblem(ToDoProblemList.IMMUNITY);
                    db.insertProblemTask(ToDoProblemList.IMMUNITY);
                    RadioButton double_p1 = (RadioButton) doublle.getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) doublle.getView().findViewById(R.id.double_problem2);
                    if (double_p1.isChecked()) {
                        ID += "Y";
                    } else if (double_p2.isChecked()) {
                        ID += "W";
                    } else {
                        Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.SKIN)) {
                    ID += "005";
                    user.setProblem(ToDoProblemList.SKIN);
                    db.insertProblemTask(ToDoProblemList.SKIN);
                    RadioButton triple_p1 = (RadioButton) triple.getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) triple.getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) triple.getView().findViewById(R.id.triple_problem3);
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
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.DETOX)) {
                    ID += "007";
                    user.setProblem(ToDoProblemList.DETOX);
                    db.insertProblemTask(ToDoProblemList.DETOX);
                    RadioButton triple_p1 = (RadioButton) triple.getView().findViewById(R.id.triple_problem1);
                    RadioButton triple_p2 = (RadioButton) triple.getView().findViewById(R.id.triple_problem2);
                    RadioButton triple_p3 = (RadioButton) triple.getView().findViewById(R.id.triple_problem3);
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
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.EXERCISE)) {
                    ID += "006";
                    user.setProblem(ToDoProblemList.EXERCISE);
                    db.insertProblemTask(ToDoProblemList.EXERCISE);
                    RadioButton double_p1 = (RadioButton) doublle.getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) doublle.getView().findViewById(R.id.double_problem2);
                    if (double_p1.isChecked()) {
                        ID += "Y";
                    } else if (double_p2.isChecked()) {
                        ID += "W";
                    } else {
                        Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.DIGESTION)) {
                    ID += "008";
                    user.setProblem(ToDoProblemList.DIGESTION);
                    db.insertProblemTask(ToDoProblemList.DIGESTION);
                    RadioButton double_p1 = (RadioButton) doublle.getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) doublle.getView().findViewById(R.id.double_problem2);
                    if (double_p1.isChecked()) {
                        ID += "Y";
                    } else if (double_p2.isChecked()) {
                        ID += "W";
                    } else {
                        Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    search(databaselist, ID, user);
                } else if (bundle.getBoolean(ToDoProblemList.ARTICULATION)) {
                    ID += "009";
                    user.setProblem(ToDoProblemList.ARTICULATION);
                    db.insertProblemTask(ToDoProblemList.ARTICULATION);
                    RadioButton double_p1 = (RadioButton) doublle.getView().findViewById(R.id.double_problem1);
                    RadioButton double_p2 = (RadioButton) doublle.getView().findViewById(R.id.double_problem2);
                    if (double_p1.isChecked()) {
                        ID += "Y";
                    } else if (double_p2.isChecked()) {
                        ID += "W";
                    } else {
                        Toast.makeText(v.getContext(),"Must select a problem from the available options",Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    search(databaselist, ID, user);
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
        if (bundle.getBoolean("Weight")) {
            header.setText("Weight Loss");
            triple = new SurveyTripleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            triple.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, triple);
            fragmentTransaction.commit();
        } else if (bundle.getBoolean("Sleep")) {
            header.setText("Sleep");
            triple = new SurveyTripleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            triple.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, triple);
            fragmentTransaction.commit();
        } else if (bundle.getBoolean("Energy")) {
            header.setText("Energy");
            if (!age.equals("20-60")) {
                single = new SurveySingleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                single.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout, single);
                fragmentTransaction.commit();
            } else {
                doublle = new SurveyDoubleCheckboxFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                doublle.setArguments(bundle);
                fragmentTransaction.replace(R.id.checkboxLayout, doublle);
                fragmentTransaction.commit();
            }
        } else if (bundle.getBoolean("Immunity")) {
            header.setText("Immunity");
            doublle = new SurveyDoubleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            doublle.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, doublle);
            fragmentTransaction.commit();
        } else if (bundle.getBoolean("Skin")) {
            header.setText("Skin");
            triple = new SurveyTripleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            triple.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, triple);
            fragmentTransaction.commit();
        } else if (bundle.getBoolean("Detox")) {
            header.setText("Detox");
            triple = new SurveyTripleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            triple.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, triple);
            fragmentTransaction.commit();
        } else if (bundle.getBoolean("Exercise")) {
            header.setText("Exercise");
            doublle = new SurveyDoubleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            doublle.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, doublle);
            fragmentTransaction.commit();
        } else if (bundle.getBoolean("Digestion")) {
            header.setText("Digestion");
            doublle = new SurveyDoubleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            doublle.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, doublle);
            fragmentTransaction.commit();
        } else if (bundle.getBoolean("Articulation")) {
            header.setText("Articulation");
            doublle = new SurveyDoubleCheckboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            doublle.setArguments(bundle);
            fragmentTransaction.replace(R.id.checkboxLayout, doublle);
            fragmentTransaction.commit();
        }
    }

    public static void search(ArrayList<String[]> db, String ID, Users user) {
        int index = -1;
        for(int i = 0; i < db.size(); i++){
            if(db.get(i)[0].equals(ID)){
                index = i;
            }
        }
        user.setSupplement1(db.get(index)[1]);
        user.setSupplement2(db.get(index)[2]);
        user.setSupplement3(db.get(index)[3]);
        user.setSupplement4(db.get(index)[4]);
    }
}