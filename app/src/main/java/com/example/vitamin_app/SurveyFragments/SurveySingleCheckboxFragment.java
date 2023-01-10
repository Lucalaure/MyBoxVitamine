package com.example.vitamin_app.SurveyFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.vitamin_app.R;

public class SurveySingleCheckboxFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_survey_single_checkbox, container, false);

        Bundle bundle = this.getArguments();
        RadioButton problem1 = (RadioButton) v.findViewById(R.id.double_problem1);

        if (bundle.getBoolean("Energy")) {
            problem1.setText("Lack of mental energy");
        }

        return v;
    }
}