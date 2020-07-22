package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.hextech.smarttime.util.DBHelper;

import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity implements ToDoListViewAdapter.OnTodoListner {

    RecyclerView recyclerView;
    String listArray[];
    String descriptionArray[];
    DBHelper dataBase;
    ArrayList<String> item_id, item_title, item_description, item_category, item_creation_date, item_due_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        recyclerView = findViewById(R.id.recyclerView);

//        listArray = new String[]{"first item", "second item", "third item"};
//        descriptionArray = new String[]{"first desc", "second desc", "third desc"};


//        ToDoListViewAdapter toDoListViewAdapter = new ToDoListViewAdapter(this, listArray, descriptionArray, this);
//        recyclerView.setAdapter(toDoListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataBase = new DBHelper(ToDoListActivity.this);
        item_id = new ArrayList<>();
        item_title = new ArrayList<>();
        item_description = new ArrayList<>();
        item_category = new ArrayList<>();
        item_creation_date = new ArrayList<>();
        item_due_date = new ArrayList<>();

        storeDataInArrays();

        listArray = new String[item_title.size()];
        descriptionArray = new String[item_description.size()];

        listArray = item_title.toArray(listArray);
        descriptionArray = item_description.toArray(descriptionArray);

        ToDoListViewAdapter toDoListViewAdapter = new ToDoListViewAdapter(this, listArray, descriptionArray, this);
        recyclerView.setAdapter(toDoListViewAdapter);

    }

    void storeDataInArrays(){
        Cursor cursor =  dataBase.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                item_id.add(cursor.getString(0));
                item_title.add(cursor.getString(1));
                item_description.add(cursor.getString(2));
                item_category.add(cursor.getString(3));
                item_creation_date.add(cursor.getString(4));
                item_due_date.add(cursor.getString(5));
            }
        }
    }

    @Override
    public void onTodoClick(int position) {
        String title = listArray[position];
        openDialog(title);
    }

    private void openDialog(String content) {
        SampleDialog sampleDialog = new SampleDialog(content);
        sampleDialog.show(getSupportFragmentManager(), "sample dialog");
    }
}