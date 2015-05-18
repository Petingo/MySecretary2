package com.wish.mysecretary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class LineDBhelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME="line";
    public static final int VERSION=92;

    private static SQLiteDatabase database;
    public LineDBhelper(Context context){
        super(context, DATABASE_NAME, null,VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "test"+ " ("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, name CHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
