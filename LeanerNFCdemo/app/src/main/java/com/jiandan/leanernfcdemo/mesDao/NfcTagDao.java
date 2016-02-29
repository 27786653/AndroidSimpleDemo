package com.jiandan.leanernfcdemo.mesDao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jiandan.leanernfcdemo.db.NfcDbHelper;
import com.jiandan.leanernfcdemo.entity.TagInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李森林 on 2016/2/29.
 */
public class NfcTagDao {
    private NfcDbHelper dbHelp;
    private SQLiteDatabase db;

    public NfcTagDao(Context context){
        dbHelp = new NfcDbHelper(context);
    }

    public void save(TagInfo tagInfo){
        db = dbHelp.getWritableDatabase();
        String sql = "insert into "+NfcDbHelper.DATABASE_NAME+"(tagid,key,info,username,curtime) values(?,?,?,?,?)";
        db.execSQL(sql, new String[]{tagInfo.getTagid(), tagInfo.getKey(), tagInfo.getInfo(),tagInfo.getUsername(),tagInfo.getCurtime()+""});
        db.close();
    }

    //修改标签--暂不提供
    public void update(TagInfo tagInfo){
        db = dbHelp.getWritableDatabase();
        String sql = "update "+NfcDbHelper.DATABASE_NAME+" set tagid = ?,key = ?,info=?,username=?,curtime=? where _id = ?";
        db.execSQL(sql,new Object[]{tagInfo.getTagid(),tagInfo.getKey(),tagInfo.getInfo(),tagInfo.getUsername(),tagInfo.getCurtime(),tagInfo.getId()});
        db.close();
    }

    public void del(int id){
        db = dbHelp.getWritableDatabase();
        String sql = "delete from "+NfcDbHelper.DATABASE_NAME+" where _id = ?";
        db.execSQL(sql,new Object[]{id});
        db.close();
    }

    public List<TagInfo> findAll(String username){
        List<TagInfo> list = new ArrayList<TagInfo>();

        db = dbHelp.getReadableDatabase();
        String sql = "select _id,tagid,key,info,username,curtime from "+NfcDbHelper.DATABASE_NAME+" where username=?";
        Cursor cursor = db.rawQuery(sql, new String[]{username});
        while(cursor.moveToNext()){
            TagInfo user = new TagInfo();
            user.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            user.setTagid(cursor.getString(cursor.getColumnIndex("tagid")));
            user.setKey(cursor.getString(cursor.getColumnIndex("key")));
            user.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setCurtime(Long.valueOf(cursor.getString(cursor.getColumnIndex("curtime"))));
            list.add(user);
        }
        db.close();
        return list;
    }

    public TagInfo findByID(int id){
        TagInfo user = null;
        db = dbHelp.getReadableDatabase();
        String sql = "select _id,tagid,key,info,username,curtime from "+NfcDbHelper.DATABASE_NAME+" where _id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

        while(cursor.moveToNext()){
            user = new TagInfo();
            user.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            user.setTagid(cursor.getString(cursor.getColumnIndex("tagid")));
            user.setKey(cursor.getString(cursor.getColumnIndex("key")));
            user.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setCurtime(Long.valueOf(cursor.getString(cursor.getColumnIndex("curtime"))));
        }
        db.close();
        return user;
    }

}