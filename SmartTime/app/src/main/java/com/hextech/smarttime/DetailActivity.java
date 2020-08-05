package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hextech.smarttime.util.DBHelper;
import com.hextech.smarttime.util.ToDoItem;
import com.hextech.smarttime.util.Utilities;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView taskTitleTF, taskDescriptionTF, taskCategoryTF, taskDateTF;
    Button deleteTaskBtn;
    ToDoItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        assert getSupportActionBar() != null; //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button

        taskTitleTF = findViewById(R.id.taskTitleTF);
        taskDescriptionTF = findViewById(R.id.taskDescriptionTF);
        taskCategoryTF = findViewById(R.id.taskCategoryTF);
        taskDateTF = findViewById(R.id.taskDateTF);

        deleteTaskBtn = findViewById(R.id.DeleteTaskBtn);

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