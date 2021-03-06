package com.example.sunnyweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sunnyweather.model.City;
import com.example.sunnyweather.model.County;
import com.example.sunnyweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/1.
 */
public class SunnyWeatherDB {

    /*
    数据库名
     */
    public static final String DB_NAME = "sunny_weather";
    /*
    数据库版本
     */
    public static final int VERSION = 1;

    private static SunnyWeatherDB sunnyWeatherDB;
    private SQLiteDatabase db;

    /*
    将构造方法私有化
     */
    private SunnyWeatherDB(Context context){
        SunnyWeatherOpenHelper dbHelper = new SunnyWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /*
    获取SunnyWeather的实例
     */
    public synchronized static SunnyWeatherDB getInstance(Context context){
        if (sunnyWeatherDB == null){
            sunnyWeatherDB = new SunnyWeatherDB(context);
        }
        return sunnyWeatherDB;
    }

    /*
    将Province实例存储到数据库中
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /*
    从数据库中读取全国所有省份的信息
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            Province province = new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(province);
        }while(cursor.moveToNext());
        return list;
    }

    /*
    将City的实例存到数据库中
     */
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /*
    从数据库中读取某省所有城市的信息
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_id = ?",new String[] {String.valueOf("province_id")},null,null,null);
        if(cursor.moveToFirst()){
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceId(provinceId);
            list.add(city);
        }while(cursor.moveToNext());
        return list;
    }

    /*
    将County实例存到数据库中
     */
    public void saveCounty(County county){
        if (county != null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }

    /*
    从数据库中读取某城市下所有县的信息
     */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County",null,"city_id = ?",new String[] {String.valueOf("city_id")},null,null,null);
        if (cursor.moveToFirst()){
            County county = new County();
            county.setId(cursor.getInt(cursor.getColumnIndex("id")));
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            county.setCityId(cityId);
            list.add(county);
        }while(cursor.moveToNext());
        return list;
    }
}
