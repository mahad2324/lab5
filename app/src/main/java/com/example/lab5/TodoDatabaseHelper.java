package com.example.lab5; // Replace with your actual package name

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TodoDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TODO = "todos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TODO = "todo";
    public static final String COLUMN_URGENCY = "urgency";

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_TODO + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TODO + " TEXT, " +
                COLUMN_URGENCY + " INTEGER);";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public void addTodo(String todoText, int urgencyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TODO, todoText);
        values.put(COLUMN_URGENCY, urgencyLevel);

        db.insert(TABLE_TODO, null, values);
        db.close();
    }

    public void deleteTodo(int todoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(todoId)};
        db.delete(TABLE_TODO, whereClause, whereArgs);
        db.close();
    }

    public Cursor getAllTodos() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {COLUMN_ID, COLUMN_TODO, COLUMN_URGENCY};
        String sortOrder = COLUMN_URGENCY + " DESC"; // You can change the sorting as needed

        return db.query(TABLE_TODO, projection, null, null, null, null, sortOrder);
    }
}
