package com.jainchiranjeev.instantdownloader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;
    public static DBHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "androiddb";
    private static final String TABLE_NAME = "table_of_url";
    private static final String col_url = "url";
    private static final String col_source = "source";

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + col_url + " TEXT, " + col_source + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String url, String source) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(col_url, url);
        values.put(col_source, source);

        long rowUpdated = db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean urlExists(String url) {
        boolean urlExist = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + col_url + " = '" + url + "'";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        if(count > 0) {
            urlExist = true;
        }
        db.close();
        return urlExist;
    }

    public Cursor getTable() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        int count = cursor.getCount();
        db.close();
        return cursor;
    }
}