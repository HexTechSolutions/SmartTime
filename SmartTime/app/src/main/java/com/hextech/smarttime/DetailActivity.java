package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
    Button deleteTaskBtn, getDirectionsBtn;
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
        getDirectionsBtn = findViewById(R.id.getDirectionsBtn);

        final int recordId = getIntent().getIntExtra("itemNumber", -1);
        String notificationCategory = getIntent().getStringExtra("category");
        double notificationLongitude = getIntent().getDoubleExtra("longitude", -1);
        double notificationLatitude = getIntent().getDoubleExtra("latitude", -1);

        if (notificationCategory != null && !notificationCategory.equals("")) {
            populateFromNotification(notificationCategory);
        }

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

        final String latitude = String.valueOf(getIntent().getDoubleExtra("latitude", -1));
        final String longitude = String.valueOf(getIntent().getDoubleExtra("longitude", -1));

        if(latitude.equals("-1.0") && longitude.equals("-1.0")){
            getDirectionsBtn.setVisibility(View.INVISIBLE);
        }else{
            getDirectionsBtn.setVisibility(View.VISIBLE);
            getDirectionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDirectionsOnGoogleMaps(latitude, longitude);
                }
            });
        }
    }

    private void openDirectionsOnGoogleMaps(String latitude, String longitude) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
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

    private void populateFromNotification(String category) {
        ArrayList<ToDoItem> toDoItems = DBHelper.getAllDataFromCategory(getApplicationContext(), category);
        if (toDoItems != null && toDoItems.size() > 0) {
            taskTitleTF.setText(toDoItems.get(0).getTitle());
            taskDescriptionTF.setText(toDoItems.get(0).getDescription());
            taskCategoryTF.setText(toDoItems.get(0).getCategory());
            taskDateTF.setText(Utilities.convertDateToString(toDoItems.get(0).getDueDate()));
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