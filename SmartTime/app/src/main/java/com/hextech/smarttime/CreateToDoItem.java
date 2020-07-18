package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class CreateToDoItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_to_do_item);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent myIntent = new Intent(CreateToDoItem.this, MainActivity.class);
        CreateToDoItem.this.startActivity(myIntent);
        return true;
    }

}