package com.byted.camp.todolist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new TodoDbHelper(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                //删除后也应该更新recycler view
                recyclerView.setAdapter(notesAdapter);

                try {
                    notesAdapter.refresh(loadNotesFromDatabase());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                //更新后也应该更新recycler view
                try {
                    notesAdapter.refresh(loadNotesFromDatabase());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(notesAdapter);

        try {
            notesAdapter.refresh(loadNotesFromDatabase());
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            try {
                notesAdapter.refresh(loadNotesFromDatabase());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Note> loadNotesFromDatabase() throws ParseException {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        List<Note> ret = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                TodoContract.ToDoEntry.COLUMN_NAME_TODONAME,
                TodoContract.ToDoEntry.COLUMN_NAME_TODOTIME,
                TodoContract.ToDoEntry.COLUMN_NAME_TODOSTATE,
                TodoContract.ToDoEntry.COLUMN_NAME_TODOPRIORITY
        };

        Cursor cursor = db.query(
                TodoContract.ToDoEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                TodoContract.ToDoEntry.COLUMN_NAME_TODOPRIORITY + " DESC"
        );

        while (cursor.moveToNext()){
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.ToDoEntry._ID));
            String todoname = cursor.getString(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_TODONAME));
            String todotime = cursor.getString(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_TODOTIME));
            int todostate = cursor.getInt(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_TODOSTATE));
            int todopriority = cursor.getInt(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_TODOPRIORITY));
            Note temp = new Note(itemId);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
            
            temp.setContent(todoname);
            temp.setDate(formatter.parse(todotime));
            temp.setState(State.from(todostate));
            temp.setPriority(Priority.from(todopriority));

            ret.add(temp);
        }
        cursor.close();
        return ret;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = TodoContract.ToDoEntry._ID + "=" + note.id;
        // Specify arguments in placeholder order.
        String[] selectionArgs = {};
        // Issue SQL statement.
        int deletedRows = db.delete(TodoContract.ToDoEntry.TABLE_NAME, selection, selectionArgs);
    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TodoContract.ToDoEntry._ID + "=" + note.id;
        String[] selectionArgs = {};
        ContentValues values = new ContentValues();
        values.put(TodoContract.ToDoEntry.COLUMN_NAME_TODOSTATE, note.getState().intValue);//相等返回1，即done，不等返回0，即undo
        db.update(TodoContract.ToDoEntry.TABLE_NAME, values, selection, selectionArgs);
    }

}
