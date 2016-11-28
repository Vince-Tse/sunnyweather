package com.example.sunnyweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunnyweather.R;
import com.example.sunnyweather.db.SunnyWeatherDB;
import com.example.sunnyweather.model.City;
import com.example.sunnyweather.model.County;
import com.example.sunnyweather.model.Province;
import com.example.sunnyweather.util.HttpCallbackListener;
import com.example.sunnyweather.util.HttpUtil;
import com.example.sunnyweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vince on 2016/11/17.
 */

public class ChooseAreaActivity extends Activity{

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private SunnyWeatherDB sunnyWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    /*
    省列表
     */
    private List<Province> provinceList;
    /*
    市列表
     */
    private List<City> cityList;
    /*
    县列表
     */
    private List<County> countyList;
    /*
    选中的省份
     */
    private Province selectedProvince;
    /*
    选中的城市
     */
    private City selectedCity;
    /*
    选中的级别
     */
    private int currentLevel;
    /*
    是否从WeatherAcitivty中跳转过来
     */
    private boolean isFromWeatherActivity;

    protected void onCreated(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isFromWeatherActivity = getIntent().getBooleanExtra("WeatherActivity",false);
        //已经选择了城市且不是从WeatherActivity中跳转过来的，才会跳回WeatherActivity中
        if (prefs.getBoolean("selected_city" ,false) && !isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        titleText = (TextView)findViewById(R.id.text_title);
        listView = (ListView)findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        sunnyWeatherDB = SunnyWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(index);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(index);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(index).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /*
    查询全国所有的省，优先从数据库查询，如果没有查询到就到服务器上查询
     */
    private void queryProvinces(){
        provinceList = sunnyWeatherDB.loadProvinces();
        if (provinceList.size() > 0 ){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            titleText.setText("中国");
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }

    /*
    查询选中省内的所有城市，优先从数据库查询，如果没有查询到就从服务器查询
     */
    private void queryCities(){
        cityList = sunnyWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0 ){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    /*
    查询选中城市所有的县，优先从数据库查询，如果没有查询到再到服务器查询
     */
    private void queryCounties(){
        countyList = sunnyWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0 ){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /*
    根据传入的代号和类型查询服务器的省市县数据
     */
    private void queryFromServer(final String code,final String type){
        String address;
        address = "https://api.heweather.com/x3/citylist?search=allchina&key=450f08bc221248e4910cee32275616f2";
//        if (!TextUtils.isEmpty(code)){
//            address = "http://mobile.weather.com.cn/js/citylist"+code+".xml";
//        }else{
//            address = "http://mobile.weather.com.cn/js/citylist.xml";
//        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result= false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponce(sunnyWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(sunnyWeatherDB,response,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(sunnyWeatherDB,response,selectedCity.getId());
                }
                if (result){
                    //通过runOnUiThread()方法回主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
    显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载···");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    关闭进度对话框
     */
    private  void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /*
    捕获Back按键，根据当前的内容级别，判断应该返回省列表、市列表，还是直接退出列表
     */
    @Override
    public void onBackPressed(){
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            if (isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

}
