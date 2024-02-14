package com.nnjtrading.whatzapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "userDatabase.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Userdetails(uid TEXT primary key, name Text, number TEXT, slogan TEXT, profile Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Userdetails");
    }

    public Boolean insertUser(String uid, String name, String number, String slogan, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("name", name);
        contentValues.put("number", number);
        contentValues.put("slogan", slogan);
        contentValues.put("profile", url);

        long results = db.insert("UserDetails", null, contentValues);
        if(results == -1) {
            return false;
        }

        return true;
    }
}
