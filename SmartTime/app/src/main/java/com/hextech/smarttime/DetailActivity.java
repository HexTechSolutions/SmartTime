package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        assert getSupportActionBar() != null; //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button
    }

    //Back to the main page
    @Override
    public boolean onSupportNavigateUp(){
        Intent myIntent = new Intent(DetailActivity.this, MainActivity.class);
        DetailActivity.this.startActivity(myIntent);
        return true;
    }
}