package com.example.mustvisit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void showResults(View v) {
        // TODO: collect:
        //  - the position (x,y) of the user
        //  - the range (range) selected
        //  - the categories ([categories]) selected
        //  and send these values to the other activity
        Intent i = new Intent(this, Results.class);
        startActivity(i);
    }
}