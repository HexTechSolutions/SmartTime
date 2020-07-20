package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button viewListButton, addToDoItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewListButton = findViewById(R.id.viewEvents);
        addToDoItemButton = findViewById(R.id.addEvent);

        viewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 openViewTodoList();
            }
        });

        addToDoItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateToDoItem.class));
            }
        });
    }

    public void openViewTodoList(){
        Intent intent = new Intent(this, ToDoListActivity.class);
        startActivity(intent);
    }
}