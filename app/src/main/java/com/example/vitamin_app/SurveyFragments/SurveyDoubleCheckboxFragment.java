package com.example.vitamin_app.SurveyFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.vitamin_app.R;

public class SurveyDoubleCheckboxFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_survey_double_checkbox, container, false);

        Bundle bundle = this.getArguments();
        RadioButton problem1 = (RadioButton) v.findViewById(R.id.double_problem1);
        RadioButton problem2 = (RadioButton) v.findViewById(R.id.double_problem2);

        if (bundle.getBoolean("Energy")) {
            problem1.setText("Mentale");
            problem2.setText("Physique");
        } else if (bundle.getBoolean("Immunity")) {
            problem1.setText("Action");
            problem2.setText("Prevention");
        }else if (bundle.getBoolean("Exercise")) {
            problem1.setText("Recuperation");
            problem2.setText("Preparation");
        } else if (bundle.getBoolean("Digestion")) {
            problem1.setText("Acido Basique");
            problem2.setText("Probleme");
        } else if (bundle.getBoolean("Articulation")) {
            problem1.setText("Douleurs");
            problem2.setText("Movement");
        }
        return v;
    }
}