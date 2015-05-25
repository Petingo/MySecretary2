package com.wish.mysecretary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class FBDBhelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME="FB";
    public static final int VERSION=200;

    public FBDBhelper(Context context){
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
