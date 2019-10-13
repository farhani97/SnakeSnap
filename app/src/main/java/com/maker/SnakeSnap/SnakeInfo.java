package com.maker.SnakeSnap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SnakeInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake_information);
    }
}
