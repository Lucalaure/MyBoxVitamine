package com.example.vitamin_app.SurveyFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.vitamin_app.R;

public class SurveyTripleCheckboxFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  v = inflater.inflate(R.layout.fragment_survey_triple_checkbox, container, false);

        Bundle bundle = this.getArguments();
        RadioButton problem1 = (RadioButton) v.findViewById(R.id.triple_problem1);
        RadioButton problem2 = (RadioButton) v.findViewById(R.id.triple_problem2);
        RadioButton problem3 = (RadioButton) v.findViewById(R.id.triple_problem3);

        if (bundle.getBoolean("Weight")) {
            problem1.setText("Anti-capiton");
            problem2.setText("Drainage");
            problem3.setText("Coupe Faim");
        } else if (bundle.getBoolean("Sleep")) {
            problem1.setText("Endormissement");
            problem2.setText("Agitation");
            problem3.setText("Reveils Nocturnes");
        } else if (bundle.getBoolean("Skin")) {
            problem1.setText("Anti Age");
            problem2.setText("Problem");
            problem3.setText("Eclat");
        } else if (bundle.getBoolean("Detox")) {
            problem1.setText("Hepatique");
            problem2.setText("Repas Charge");
            problem3.setText("Flore");
        }

        return v;
    }
}