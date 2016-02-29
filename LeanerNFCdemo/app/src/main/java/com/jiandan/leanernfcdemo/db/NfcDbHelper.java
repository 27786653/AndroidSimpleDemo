package com.jiandan.leanernfcdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 李森林 on 2016/2/29.
 */
public class NfcDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "NFC_TAG_INFO";

    public NfcDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table NFC_TAG_INFO(_id integer primary key autoincrement,tagid text,key text,info text,username text,curtime text)";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}