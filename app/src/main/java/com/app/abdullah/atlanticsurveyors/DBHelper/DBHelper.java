package com.app.abdullah.atlanticsurveyors.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;


import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHelper extends SQLiteAssetHelper {

    private static  final  String DB_Name = "newdatabase.db";
    private static  final  int DB_VER = 1;

    private static  final String TBL_Name= "data";



    private static  final String COL_User="uid";
    private static  final String COL_Site="sid";
    private static  final String COL_Off="oid";

    private static  final String COL_Kprsta="kprsta";
    private static  final String COL_Quasta="quasta";
    private static  final String COL_Kprrem="kprrem";
    private static  final String COL_Quarem="quarem";


    private static  final String COL_Stkimg="stkimg";
    private static  final String COL_Binimg="binimg";
    private static  final String COL_Regimg="regimg";
    private static  final String COL_Spyimg="spyimg";






    public DBHelper(Context context) {
        super(context, DB_Name, null, DB_VER);



    }

    public void addData(String uid,String sid,String oid,String kprsta,String quasta,String kprrem,String quarem ,byte[] stkimg,byte[] binimg,byte[] regimg,byte[] spyimg ) throws SQLiteException
    {


        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_User,uid);
        cv.put(COL_Site,sid);
        cv.put(COL_Off,oid);
        cv.put(COL_Kprsta,kprsta);
        cv.put(COL_Quasta,quasta);
        cv.put(COL_Kprrem,kprrem);
        cv.put(COL_Quarem,quarem);
        cv.put(COL_Stkimg,stkimg);
        cv.put(COL_Binimg,binimg);
        cv.put(COL_Regimg,regimg);
        cv.put(COL_Spyimg,spyimg);

        database.insert(TBL_Name,null,cv);


    }




}
