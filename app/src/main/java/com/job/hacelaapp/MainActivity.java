package com.job.hacelaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.job.hacelaapp.manageUsers.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TODO remove this intent
        Intent regiIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(regiIntent);

    }
}
