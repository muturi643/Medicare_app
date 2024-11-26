package com.example.medicare;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry1 = "create table users(username text,password Text)";
        db.execSQL(qry1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void Sign_up(String username,String password){
        ContentValues cv = new ContentValues();
        cv.put("username",username);
        cv.put("password",password);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("users",null,cv);
        db.close();
    }

    public int Login(String username, String password) {
        int result=0;
        String[] str = {username, password};
        // str[0] = username;
        //str[1] = password;
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("select * from users where username=? and password=?",str);
        if(c.moveToFirst()) {
            result=1;
        }
        return result;
    }
}
