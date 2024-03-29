package com.example.vitamin_app.SurveyFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vitamin_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SurveyAgeGenderConfirmFragment extends Fragment {

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    String[] genderList;
    String[] ageList;

    String age;
    String gender;

    Spinner genderSpinner;
    Spinner ageSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_survey_age_gender_confirm, container, false);

        genderSpinner = (Spinner) v.findViewById(R.id.spinnerGender);
        ageSpinner = (Spinner) v.findViewById(R.id.spinnerAge);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Users");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // String username = currentUser.getDisplayName();
        String email = currentUser.getEmail().replace(".","1").replace("#","2")
                .replace("\\$","3").replace("\\[","4").replace("]","5");

        // Retrieving user data from firebase
        databaseReference.child(email).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        age = String.valueOf(dataSnapshot.child("age").getValue());
                        gender = String.valueOf(dataSnapshot.child("gender").getValue());
                        // Set spinners to age and gender of user
                        if (age.equals("12-20")) {
                            ageList = new String[] {"12-20", "20-60", "60+"};
                        } else if (age.equals("20-60")) {
                            ageList = new String[] {"20-60", "12-20", "60+"};
                        } else {
                            ageList = new String[] {"60+", "12-20", "20-60"};
                        }
                        if (gender.equals("Male")) {
                            genderList = new String[] {"Male", "Female"};
                        } else {
                            genderList = new String[] {"Female", "Male"};
                        }
                        initialiseSpinners(v);
                    } else {
                        Toast.makeText(v.getContext(), "User does not exist",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), "Failed to read data",Toast.LENGTH_LONG).show();
                }
            }
        });

        // When item is selected in spinner, change DB
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String new_age = ageSpinner.getSelectedItem().toString();
                databaseReference.child(email).child("age").setValue(new_age);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String new_gender = genderSpinner.getSelectedItem().toString();
                databaseReference.child(email).child("gender").setValue(new_gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button toSurvey = (Button) v.findViewById(R.id.toSurveyMain);
        toSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment surveyMain = new SurveyMainFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentLayout1, surveyMain);
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    public void initialiseSpinners(View v) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, ageList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter2);
    }
}