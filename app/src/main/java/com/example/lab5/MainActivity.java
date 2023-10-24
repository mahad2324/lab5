package com.example.lab5;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText todoEditText;
    private Button addButton;
    private ListView listView;

    private TodoDatabaseHelper dbHelper;
    private ArrayAdapter<String> todoAdapter;
    private ArrayList<String> todoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TodoDatabaseHelper(this);

        todoEditText = findViewById(R.id.todoEditText);
        addButton = findViewById(R.id.addButton);
        listView = findViewById(R.id.listView);

        todoList = new ArrayList<>();
        todoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
        listView.setAdapter(todoAdapter);

        loadTodosFromDatabase();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodoToDatabase();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteTodoFromDatabase(position);
                return true;
            }
        });
    }

    private void loadTodosFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {TodoDatabaseHelper.COLUMN_TODO};

        Cursor cursor = db.query(
                TodoDatabaseHelper.TABLE_TODO,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        todoList.clear(); // Clear the current list
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String todo = cursor.getString(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COLUMN_TODO));
                todoList.add(todo);
            }
            cursor.close();
        }
        todoAdapter.notifyDataSetChanged();
        db.close();
    }

    private void addTodoToDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String todoText = todoEditText.getText().toString();

        if (!todoText.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(TodoDatabaseHelper.COLUMN_TODO, todoText);

            long newRowId = db.insert(TodoDatabaseHelper.TABLE_TODO, null, values);

            if (newRowId != -1) {
                todoEditText.setText(""); // Clear the input field
                loadTodosFromDatabase(); // Reload the list of todos
            } else {
                Toast.makeText(this, "Failed to add todo", Toast.LENGTH_SHORT).show();
            }
        }

        db.close();
    }

    private void deleteTodoFromDatabase(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String todoToDelete = todoList.get(position);

        String selection = TodoDatabaseHelper.COLUMN_TODO + " = ?";
        String[] selectionArgs = {todoToDelete};

        int deletedRows = db.delete(TodoDatabaseHelper.TABLE_TODO, selection, selectionArgs);
        if (deletedRows > 0) {
            todoList.remove(position);
            todoAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to delete todo", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
