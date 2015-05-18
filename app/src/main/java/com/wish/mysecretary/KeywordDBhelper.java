package com.wish.mysecretary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class KeywordDBhelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME="keyword.db";
    public static final String TABLE_NAME="keyword";
    public static final int VERSION=1;

    private static SQLiteDatabase database;
    public KeywordDBhelper(Context context){
        super(context, DATABASE_NAME, null,VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "word CHAR" + "content CHAR"+ "weight INTEGER" + "replace CHAR"+ "type INTEGER" + ")");
        db.execSQL("CREATE TABLE count (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Keyword" );
        onCreate(db);
    }



}
