package com.example.bottomtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.bottomtest.ui.account.LoginActivity;
import com.example.bottomtest.ui.account.RegisterActivity;
import com.example.bottomtest.ui.home.db.City;
import com.example.bottomtest.ui.home.db.County;
import com.example.bottomtest.ui.home.db.Province;
import com.example.bottomtest.ui.home.util.HttpUtil;
import com.example.bottomtest.ui.home.util.Utility;
import com.mob.MobSDK;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StartActivity extends AppCompatActivity {

    private Button register, login;
    private TextView locationText;
    public LocationClient mLocationClient;
    private StringBuilder currentPosition = new StringBuilder();

    //省市县的列表
    private List<Province>provinceList;
    private List<City>cityList;
    private List<County>countyList;

    //选中的
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private String selectedProvinceName;
    private String selectedCityName;
    private String selectedCountyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }


        register = findViewById(R.id.start_registerbtn);
        login = findViewById(R.id.start_loginbtn);
        locationText = findViewById(R.id.start_location);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobSDK.init(StartActivity.this);
                sendCode(StartActivity.this);
            }
        });


        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(StartActivity.this, permissions, 1);

        } else {
            requestLocation();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                intent.putExtra("weather_id", selectedCounty.getWeatherId());
                startActivity(intent);
            }
        });


    }


    public void sendCode(Context context) {
        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    // TODO 利用国家代码和手机号码进行后续的操作
                    //Toast.makeText(StartActivity.this, phone, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                    intent.putExtra("userPhone", phone);
                    startActivity(intent);
                } else{
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(context);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentPosition.append(bdLocation.getProvince()).append("/");
                    currentPosition.append(bdLocation.getCity()).append("/");
                    currentPosition.append(bdLocation.getDistrict()).append("/");

                    currentPosition.append("定位方式：");
                    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                        currentPosition.append("GPS");
                    }else if (bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                        currentPosition.append("网络");
                    }
                    String[] splits = currentPosition.toString().split("/");
                    locationText.setText(splits[0] + splits[1] + splits[2]);
                    getWeatherId();
                }
            });
        }

    }


    public void getWeatherId() {
        String[] splits = currentPosition.toString().split("/");
        //weatheridText.setText(splits[0].substring(0,splits[0].length()-1));

        selectedProvinceName=splits[0].substring(0,splits[0].length()-1);
        selectedCityName = splits[1].substring(0, splits[1].length() - 1);
        selectedCountyName = splits[2].substring(0, splits[2].length() - 1);
//        selectedCity.setCityName(splits[1].substring(0, splits[1].length() - 1));
//        selectedCounty.setCountyName(splits[2].substring(0, splits[2].length() - 1));
        getProvince();
        getCity();
        getCounty();
        //weatheridText.setText(String.valueOf(selectedCity.getId()));
    }

    public void getProvince() {
        String provinceName = selectedProvinceName;
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            for (Province province : provinceList) {
                if (province.getProvinceName().equals(provinceName)) {
                    selectedProvince = new Province(province);
                    //Toast.makeText(StartActivity.this, ""+selectedProvince.getId(),Toast.LENGTH_LONG).show();
                    break;
                }
            }

        }else {
            String address = "http:guolin.tech/api/china";
            //数据没有就从服务器获取
            queryFromServer(address,"province");
        }
    }

    public void getCity() {
        String cityName = selectedCityName;
        cityList = DataSupport.where("provinceId=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            for (City city : cityList) {
                if (city.getCityName().equals(cityName)) {
                    selectedCity = new City(city);
                    //Toast.makeText(StartActivity.this, ""+city.getId(),Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http:guolin.tech/api/china/"+provinceCode;
            //数据没有就从服务器获取
            queryFromServer(address,"city");
        }
    }

    public void getCounty() {
        String countyName =  selectedCountyName;
        countyList = DataSupport.where("cityId=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            for (County county : countyList) {
                if (county.getCountyName().equals(countyName)) {
                    selectedCounty = new County(county);
                    break;
                }
            }
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http:guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            //数据没有就从服务器获取
            queryFromServer(address,"county");
        }

    }

    //从服务器上查找省市县的信息
    private void queryFromServer(String address, final String type) {
        //显示进度

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(StartActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示信息
                            if ("province".equals(type)){
                                getProvince();
                            }else if ("city".equals(type)){
                                getCity();
                            }else if("county".equals(type)){
                                getCounty();
                            }
                        }
                    });
                }
            }
        });
    }


}
