package com.example.vitamin_app.Handlers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vitamin_app.Models.ToDoModel;
import com.example.vitamin_app.ToDoProblemList;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToDoDatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;

    public ToDoDatabaseHandler(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    //Checks to see if the task already exists inside the databse
    //If it exists, do nothing, otherwise add it to database.
    public void insertUniqueTask(ToDoModel task){
        Cursor c = null;
        try {
            String query = "select * from " + TODO_TABLE + " where " + TASK + " = \"" + task.getTask() + "\"";
            c = db.rawQuery(query, null);
            if (c.getCount()!=0) {
                return;
            }
            else {
               insertTask(task);
            }
        }
        finally {
            if (c != null) {
                c.close();
            }
        }

    }

    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }

    //Given a problem, insert the related tasks associated with it into the database.
    public void insertProblemTask(String problem){
        switch (problem){
            case ToDoProblemList.WEIGHT:
                insertTask(ToDoProblemList.WEIGHTTASK1);
                insertTask(ToDoProblemList.WEIGHTTASK2);
                break;
            case ToDoProblemList.SLEEP:
                insertTask(ToDoProblemList.SLEEPTASK1);
                insertTask(ToDoProblemList.SLEEPTASK2);
                break;
            case ToDoProblemList.ENERGY:
                insertTask(ToDoProblemList.ENERGYTASK1);
                insertTask(ToDoProblemList.ENERGYTASK2);
                break;
            case ToDoProblemList.IMMUNITY:
                insertTask(ToDoProblemList.IMMUNITYTASK1);
                insertTask(ToDoProblemList.IMMUNITYTASK2);
                break;
            case ToDoProblemList.SKIN:
                insertTask(ToDoProblemList.SKINTASK1);
                insertTask(ToDoProblemList.SKINTASK2);
                break;
            case ToDoProblemList.DETOX:
                insertTask(ToDoProblemList.DETOXTASK1);
                insertTask(ToDoProblemList.DETOXTASK2);
                break;
            case ToDoProblemList.EXERCISE:
                insertTask(ToDoProblemList.EXERCISETASK1);
                insertTask(ToDoProblemList.EXERCISETASK2);
                break;
            case ToDoProblemList.DIGESTION:
                insertTask(ToDoProblemList.DIGESTIONTASK1);
                insertTask(ToDoProblemList.DIGESTIONTASK2);
                break;
            case ToDoProblemList.ARTICULATION:
                insertTask(ToDoProblemList.ARTICULATIONTASK1);
                insertTask(ToDoProblemList.ARTICULATIONTASK2);
                break;
        }
    }

    //inserts a task into the database
    public void insertTask(String taskString){
        ToDoModel task = new ToDoModel();
        task.setTask(taskString);
        task.setStatus(0);
        insertUniqueTask(task);
    }

    public void deleteProblemTasks(){
        for (String task : ToDoProblemList.PROBLEMTASKLIST) {
            db.delete(TODO_TABLE, "task=?", new String[]{task});
        }
    }
}
