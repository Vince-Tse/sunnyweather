package com.example.sunnyweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/11/1.
 */
public class SunnyWeatherOpenHelper extends SQLiteOpenHelper{

    /*
    Province表建表语句
     */
    public static final String CREATE_PROVINCE = "creat province table("
            +"id integer primary key autoincrement,"
            +"province_name text,"
            +"province_code text)";

    /*
    City表建表语句
     */
    public static final String CREATE_CITY = "create city table("
            +"id integer primary key autoincrement,"
            +"city_name text,"
            +"city_code text,"
            +"province_id integer)";

    /*
    County表建表语句
     */
    public static final String CREATE_COUNTY = "create county table("
            +"id integer primary key autoincrement"
            +"county_name text,"
            +"county_code text,"
            +"city_id integer)";

    public SunnyWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_PROVINCE);//创建province表
        db.execSQL(CREATE_CITY);//创建city表
        db.execSQL(CREATE_COUNTY);//创建county表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }
}