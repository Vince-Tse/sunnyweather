package com.example.sunnyweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.sunnyweather.db.SunnyWeatherDB;
import com.example.sunnyweather.model.City;
import com.example.sunnyweather.model.County;
import com.example.sunnyweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/11/3.
 */
public class Utility {
    /*
    解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponce(SunnyWeatherDB sunnyWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length>0){
                for (String p: allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存到Province表
                    sunnyWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /*
    解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(SunnyWeatherDB sunnyWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存到City表
                    sunnyWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /*
    解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(SunnyWeatherDB sunnyWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0){
                for (String c : allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的信息存到County表
                    sunnyWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /*
    解析服务器返回的JSON数据，并将数据存储到本地
     */
    public static void handleWeatherResponse(Context context,String response){
       try {
           JSONObject jsonObject = new JSONObject(response);
           JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
           String cityName = weatherInfo.getString("city");
           String weatherCode = weatherInfo.getString("cityId");
           String temp1 = weatherInfo.getString("temp1");
           String temp2 = weatherInfo.getString("temp2");
           String weatherDesp = weatherInfo.getString("weather");
           String publishTime = weatherInfo.getString("ptime");
           saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
       }catch(JSONException E){
           E.printStackTrace();
       }
    }

    /*
    将服务器返回的所有天气信息存储到sharedPreferences文件中
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年m月d日",Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("selected_city",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_time",sdf.format(new Date()));
        editor.commit();
    }
}
