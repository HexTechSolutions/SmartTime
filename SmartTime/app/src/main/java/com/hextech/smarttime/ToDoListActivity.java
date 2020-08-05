package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.hextech.smarttime.util.DBHelper;

import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity implements ToDoListViewAdapter.OnTodoListner {

    RecyclerView recyclerView;
    DBHelper dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataBase = new DBHelper(ToDoListActivity.this);

        ToDoListViewAdapter toDoListViewAdapter = new ToDoListViewAdapter(this, this, dataBase);
        recyclerView.setAdapter(toDoListViewAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataBase = new DBHelper(ToDoListActivity.this);

        ToDoListViewAdapter toDoListViewAdapter = new ToDoListViewAdapter(this, this, dataBase);
        recyclerView.setAdapter(toDoListViewAdapter);
    }

    @Override
    public void onTodoClick(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("itemNumber", position);
        startActivity(intent);
    }

}