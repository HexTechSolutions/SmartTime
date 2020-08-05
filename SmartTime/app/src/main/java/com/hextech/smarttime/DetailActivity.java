package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hextech.smarttime.util.DBHelper;
import com.hextech.smarttime.util.ToDoItem;
import com.hextech.smarttime.util.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    TextView taskTitleTF, taskDescriptionTF, taskCategoryTF, taskDateTF;
    Button deleteTaskBtn;
    ToDoItem item;
    String title,description,category,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        taskTitleTF = findViewById(R.id.taskTitleTF);
        taskDescriptionTF = findViewById(R.id.taskDescriptionTF);
        taskCategoryTF = findViewById(R.id.taskCategoryTF);
        taskDateTF = findViewById(R.id.taskDateTF);
        deleteTaskBtn = findViewById(R.id.DeleteTaskBtn);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        //Only Runs when navigating from Notification
        if(category != null){
            ArrayList data = DBHelper.getAllData(getApplicationContext());
            ArrayList<ToDoItem> CategoryItems = new ArrayList<>();

            System.out.println(category);
            for (int i = 0;  i < data.size(); i++){
                ToDoItem td = (ToDoItem) data.get(i);

                if(td.getCategory().equals(category)){
                    CategoryItems.add(td);
                }
            }

            Collections.sort(CategoryItems);
            Log.i("SmartTime", "Sorted");

            taskTitleTF.setText(CategoryItems.get(0).getTitle());
            taskDescriptionTF.setText(CategoryItems.get(0).getDescription());
            taskCategoryTF.setText(CategoryItems.get(0).getCategory());
            taskDateTF.setText(CategoryItems.get(0).getDueDate().toString());
        }

        assert getSupportActionBar() != null; //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button



        final int recordId = getIntent().getIntExtra("itemNumber", -1);
        if (recordId != -1) {
            populateFields(recordId);
        }

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper.deleteRecord(getApplicationContext(), item.getRecordID());
                finish();
            }
        });
    }

    private void populateFields(int recordId) {
        ArrayList<ToDoItem> list = new DBHelper(getApplicationContext()).getAllData(getApplicationContext());
        item = list.get(recordId);
        if (item != null) {
            taskTitleTF.setText(item.getTitle());
            taskDescriptionTF.setText(item.getDescription());
            taskCategoryTF.setText(item.getCategory());
            taskDateTF.setText(Utilities.convertDateToString(item.getDueDate()));
        }
    }

    //Back to the main page
    @Override
    public boolean onSupportNavigateUp(){
        Intent myIntent = new Intent(DetailActivity.this, MainActivity.class);
        DetailActivity.this.startActivity(myIntent);
        return true;
    }
}