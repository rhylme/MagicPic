package com.rhyme.magicpic.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rhyme.magicpic.entity.Main_Pic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhyme on 2017/7/7.
 */

public class SQLiteUtil {
    private static final int VERSION=1;
    private static final String Table_Name="mypicture";
    private static final String Pic_Id="Pic_Id";

    private static final String Pic_ture="path";

    private static SQLiteUtil sqlUtil;

    private static class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper{
        private static final String Create_Table="create table "+Table_Name+"(" +
                Pic_Id+" integer primary key autoincrement, " +
                Pic_ture+" text)";
        public SQLiteOpenHelper(Context context) {
            super(context, "Picture.db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(Create_Table);
        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
    public static SQLiteDatabase db;

    public static SQLiteUtil InstanceDataBase(Context context){
        if (sqlUtil==null){
            synchronized (SQLiteUtil.class){
                if (sqlUtil==null){
                   sqlUtil=new SQLiteUtil(context);
                }
            }
        }
        return sqlUtil;
    }
    public SQLiteUtil(Context context){
        db=new SQLiteOpenHelper(context).getWritableDatabase();
    }

    public void addData(Main_Pic path){
        ContentValues cv=new ContentValues();
        cv.put(Pic_ture,path.getPath());
        db.insert(Table_Name,null,cv);
    }
    public List<Main_Pic> selectData(){
        List<Main_Pic> mps=new ArrayList<>();
        Cursor cursor=db.query(Table_Name,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Main_Pic mp=new Main_Pic();
            mp.setPath(cursor.getString(cursor.getColumnIndex(Pic_ture)));
            mp.setId(cursor.getInt(cursor.getColumnIndex(Pic_Id)));
            mps.add(mp);
        }
        cursor.close();
        return mps;
    }
    public void delete(String path){
        db.beginTransaction();
        db.delete(Table_Name,Pic_ture+"=?",new String[]{path});
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
