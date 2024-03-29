package com.example.vitamin_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.vitamin_app.R;
import com.example.vitamin_app.Handlers.VitaminDetailDatabaseHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class QuickFixActivity extends AppCompatActivity {
    InputStream inputStream;
    static ArrayList<String[]> databaselist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputStream = getResources().openRawResource(R.raw.supplement_sheet_final);
        VitaminDetailDatabaseHandler vitaminDetailDatabaseHandler = new VitaminDetailDatabaseHandler(QuickFixActivity.this);
        File file = new File("/data/data/com.example.vitamin_app/databases/vitamin.db");
        file.delete();
        VitaminDetailDatabaseHandler vitaminDatabaseHelper = new VitaminDetailDatabaseHandler(QuickFixActivity.this);
        BufferedInputStream bf = new BufferedInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bf, StandardCharsets.UTF_8));
        String line;
        try{
            while ((line = reader.readLine()) != null) {
                String[] str = line.split(",");
                vitaminDetailDatabaseHandler.addCSV(str[1], str[2], str[3],str[5],str[4]);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        ArrayList<String[]> list = vitaminDatabaseHelper.getData();
        databaselist = list;

        setContentView(R.layout.activity_general_list);

        // set custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Get menu item at the bottom and redirect to relevant intent if clicked on.
        ImageButton toHome = (ImageButton) findViewById(R.id.toHome);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                view.getContext().startActivity(intent);
            }
        });

        ImageButton toList = (ImageButton) findViewById(R.id.toList);
        toList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ResultListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                view.getContext().startActivity(intent);
            }
        });

        ImageButton toSearch = (ImageButton) findViewById(R.id.toSearch);
        toSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), QuickFixActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                view.getContext().startActivity(intent);
            }
        });

        //If user is not logged in, do not show the menu.
        if (!LoginActivity.signedIn) {
            toHome.setVisibility(View.GONE);
            toSearch.setVisibility(View.GONE);
            toList.setVisibility(View.GONE);

            LinearLayout lin = (LinearLayout) findViewById(R.id.linearLayout);
            lin.setVisibility(View.GONE);
        }
    }

    public static ArrayList<String []> getDatabaselist(){
        return databaselist;
    }
}