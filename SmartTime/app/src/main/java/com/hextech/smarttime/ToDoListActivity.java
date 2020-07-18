package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ToDoListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String listArray[];
    String descriptionArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        recyclerView = findViewById(R.id.recyclerView);

        listArray = new String[]{"first item", "second item", "third item"};
        descriptionArray = new String[]{"first desc", "second desc", "third desc"};


        ToDoListViewAdapter toDoListViewAdapter = new ToDoListViewAdapter(this, listArray, descriptionArray);
        recyclerView.setAdapter(toDoListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}