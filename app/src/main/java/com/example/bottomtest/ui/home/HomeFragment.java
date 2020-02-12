package com.example.bottomtest.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.bottomtest.MainActivity;
import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;
import com.example.bottomtest.ui.home.db.County;
import com.example.bottomtest.ui.home.gson.Forecast;
import com.example.bottomtest.ui.home.gson.Weather;
import com.example.bottomtest.ui.home.service.AutoUpdateService;
import com.example.bottomtest.ui.home.util.HttpUtil;
import com.example.bottomtest.ui.home.util.Utility;
import com.google.android.material.navigation.NavigationView;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {

    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private LinearLayout forecastLayout;
    private ScrollView weatherLayout;

    private ImageView bcImg;

    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button navButton;
    private String bc_img;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_weather, container, false);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (getActivity().getActionBar() != null){
            getActivity().getActionBar().hide();
        }

        titleCity=root.findViewById(R.id.title_city);
        titleUpdateTime = root.findViewById(R.id.title_update_time);
        degreeText = root.findViewById(R.id.degree_text);
        weatherInfoText = root.findViewById(R.id.weather_info_text);
        forecastLayout = root.findViewById(R.id.forecast_layout);
        aqiText = root.findViewById(R.id.aqi_text);
        pm25Text = root.findViewById(R.id.pm25_text);
        comfortText = root.findViewById(R.id.comfort_text);
        carWashText = root.findViewById(R.id.car_wash_text);
        sportText = root.findViewById(R.id.sport_text);
        weatherLayout = root.findViewById(R.id.weather_layout);

        bcImg = root.findViewById(R.id.bc_img);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        //下拉进度条的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //nav界面
        navButton=root.findViewById(R.id.home_button);
        drawerLayout = root.findViewById(R.id.drawer_layout);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = root.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_change);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                //跳转
                jumpPage();
                return true;
            }
        });


        List<User> all = DataSupport.findAll(User.class);
        if (all.size()==0 || all.get(0).getWeatherId()==null) {
            Intent intent = new Intent(getActivity(), WeatherMainActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String weatherString = prefs.getString("weather", null);
            if (weatherString != null) {
                //缓存中有则直接解析
                Weather weather = Utility.handleWeatherResponse(weatherString);
                //为刷新准备
                mWeatherId = weather.basic.weatherId;
                //显示解析的信息
                showWeatherMassage(weather);
            }else {
                //为刷新准备
                mWeatherId = all.get(all.size()-1).getWeatherId();
                weatherLayout.setVisibility(View.INVISIBLE);
                //向服务器请求

                requestWeatherMassage(mWeatherId);


            }
            bc_img = prefs.getString("bc_Img", null);
        }



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeatherMassage(mWeatherId);
            }
        });

        //背景图片的加载

        if (bc_img != null) {
            Glide.with(this).load(bc_img).into(bcImg);
        }else {
            loadBingPic();
        }


        return root;
    }




    public void requestWeatherMassage(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=11923c5a12234e07b873c4907151560a";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseString);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (weather != null && "ok".equals(weather.status)) {
                            //放入缓存
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            edit.putString("weather", responseString);
                            edit.apply();
                            mWeatherId = weather.basic.weatherId;
                            //显示
                            showWeatherMassage(weather);
                        }else {

                            Toast.makeText(getActivity(), "获取天气信息失败!!", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
        loadBingPic();
    }


    //显示天气信息
    private void showWeatherMassage(Weather weather) {

        //天气信息赋值
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String temperature = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(temperature);
        weatherInfoText.setText(weatherInfo);

        //预测信息赋值
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastsList) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dataText = view.findViewById(R.id.data_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        //空气质量赋值
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        //出行指南
        String comfort = "舒适度" + weather.suggestion.comfort.info;
        String carWash = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动指数" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);
    }


    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                edit.putString("bc_Img", bingPic);
                edit.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(bingPic).into(bcImg);
                    }
                });

            }
        });
    }

    public void jumpPage(){
        Intent intent = new Intent(getActivity(), WeatherMainActivity.class);
        //intent.putExtra("showChoose", "1");
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        edit.remove("weather");
        edit.apply();
        startActivity(intent);
        getActivity().finish();
    }


}