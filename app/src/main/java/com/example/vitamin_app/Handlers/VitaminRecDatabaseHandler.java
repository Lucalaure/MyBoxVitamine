package com.example.vitamin_app.Handlers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class VitaminRecDatabaseHandler extends SQLiteOpenHelper {

    public static final String VITAMIN_REC_TABLE = "vitamin_rec_table";
    private static String PROBLEM_ID = "problem_id";
    private static String VITAMIN_NAME1 = "vitamin_name1";
    private static String VITAMIN_NAME2 = "vitamin_name2";
    private static String VITAMIN_NAME3 = "vitamin_name3";
    private static String VITAMIN_NAME4 = "vitamin_name4";
    public static final String COLUMN_ID  ="column_id";
    private static final int VERSION = 1;

    private SQLiteDatabase mReadableDB;

    public VitaminRecDatabaseHandler(@Nullable Context context) {
        super(context,"vitamin_rec.db", null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + VITAMIN_REC_TABLE);

        String query = "CREATE TABLE " + VITAMIN_REC_TABLE + " (" +
                PROBLEM_ID + " TEXT, " +
                VITAMIN_NAME1 + " TEXT, " +
                VITAMIN_NAME2 + " TEXT, " +
                VITAMIN_NAME3 + " TEXT, " +
                VITAMIN_NAME4 + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VITAMIN_REC_TABLE);
        onCreate(db);
    }

    public void addCSV(String id, String vit1, String vit2, String vit3, String vit4) throws IOException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PROBLEM_ID, id);
        cv.put(VITAMIN_NAME1, vit1);
        cv.put(VITAMIN_NAME2, vit2);
        cv.put(VITAMIN_NAME3, vit3);
        cv.put(VITAMIN_NAME4, vit4);
        db.insert(VITAMIN_REC_TABLE, null, cv);
    }
    @SuppressLint("Range")
    public ArrayList<String []> getData(){
        ArrayList<String []> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + VITAMIN_REC_TABLE, null);
        while(cursor.moveToNext()){
            String[] str = {cursor.getString(0),cursor.getString(1) ,cursor.getString(2),cursor.getString(3),cursor.getString(4)};
            list.add(str);
        }
        return list;
    }
}
