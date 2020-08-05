package com.hextech.smarttime.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SuperTimeDatabase.db";
    public static final String TABLE_NAME = "ToDoListTable";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + DatabaseTableColumns.RECORD_ID.toString() + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + DatabaseTableColumns.TITLE.toString() + " VARCHAR(30), " + DatabaseTableColumns.DESCRIPTION.toString() + " VARCHAR(50), " + DatabaseTableColumns.CATEGORY.toString() + " VARCHAR(10), " + DatabaseTableColumns.CREATION_DATE.toString() + " DATE, " + DatabaseTableColumns.DUE_DATE.toString() + " DATE)";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public static ArrayList<ToDoItem> getAllData(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseTableColumns.RECORD_ID.toString(), DatabaseTableColumns.TITLE.toString(), DatabaseTableColumns.DESCRIPTION.toString(), DatabaseTableColumns.CATEGORY.toString(), DatabaseTableColumns.CREATION_DATE.toString(), DatabaseTableColumns.DUE_DATE.toString()};
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
        ArrayList<ToDoItem> todoList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int recordId = cursor.getInt(cursor.getColumnIndex(DatabaseTableColumns.RECORD_ID.toString()));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseTableColumns.TITLE.toString()));
            String description = cursor.getString(cursor.getColumnIndex(DatabaseTableColumns.DESCRIPTION.toString()));
            String category = cursor.getString(cursor.getColumnIndex(DatabaseTableColumns.CATEGORY.toString()));

            String creationDateString = cursor.getString(cursor.getColumnIndex(DatabaseTableColumns.CREATION_DATE.toString()));
            String dueDateString = cursor.getString(cursor.getColumnIndex(DatabaseTableColumns.DUE_DATE.toString()));

            Date creationDate = Utilities.convertStringToDate(creationDateString);
            Date dueDate = Utilities.convertStringToDate(dueDateString);

            ToDoItem toDoItem = new ToDoItem(title, description, category, creationDate, dueDate);
            toDoItem.setRecordID(recordId);
            todoList.add(toDoItem);
        }
        cursor.close();
        return todoList;
    }

    public static boolean insertToDoListEntry(Context context, ToDoItem toDoItem) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseTableColumns.TITLE.toString(), toDoItem.getTitle());
        values.put(DatabaseTableColumns.DESCRIPTION.toString(), toDoItem.getDescription());
        values.put(DatabaseTableColumns.CATEGORY.toString(), toDoItem.getCategory());

        String dueDateString = Utilities.convertDateToString(toDoItem.getDueDate());
        String createdDateString = Utilities.convertDateToString(toDoItem.getCreatedDate());

        values.put(DatabaseTableColumns.CREATION_DATE.toString(), createdDateString);
        values.put(DatabaseTableColumns.DUE_DATE.toString(), dueDateString);
        long newRowId = db.insert(TABLE_NAME, null, values);

        return newRowId != -1;

    }

    public static void deleteRecord(Context context, int recordId) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseTableColumns.RECORD_ID.toString() + " LIKE " + recordId;
        String[] selectionArgs = null;
        int deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
        Log.i("SmartTime", "Record deleted!");
    }

    public enum DatabaseTableColumns {
        RECORD_ID, TITLE, DESCRIPTION, CATEGORY, CREATION_DATE, DUE_DATE
    }

}