package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hextech.smarttime.util.DBHelper;
import com.hextech.smarttime.util.ToDoItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateToDoItem extends AppCompatActivity {

    private Spinner spinner1;
    private Button btnSave, btnCancel;
    private EditText editDate, titleEditText, descriptionEditText;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_to_do_item);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        //Button Action Listners
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        addDate();

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // get the selected dropdown list value - strings.xml
    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner);
        btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String category = spinner1.getSelectedItem().toString();

                String dueDateString = editDate.getText().toString();
                DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                Date dueDate = null;
                try {
                    dueDate = format.parse(dueDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date createdDate = (new Date());

                ToDoItem toDoItem = new ToDoItem(title, description, category, createdDate, dueDate);

                boolean status = DBHelper.insertToDoListEntry(getApplicationContext(), toDoItem);

                if (status) {
                    Toast.makeText(CreateToDoItem.this, "Data Insertion Successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateToDoItem.this, "Data Insertion Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //Change Date picker
    public void addDate() {
        editDate  = (EditText)findViewById(R.id.editTextDate);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int  dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        editDate.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    new DatePickerDialog(CreateToDoItem.this, date,
                            myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
            }
        });
    }

    //Update the Date Pick
    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDate.setText(sdf.format(myCalendar.getTime()));
    }

    //Back Button to Main Page
    @Override
    public boolean onSupportNavigateUp(){
        Intent myIntent = new Intent(CreateToDoItem.this, MainActivity.class);
        CreateToDoItem.this.startActivity(myIntent);
        return true;
    }

}