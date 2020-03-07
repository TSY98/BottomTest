package com.example.bottomtest.ui.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bottomtest.MainActivity;
import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;
import com.example.bottomtest.ui.home.util.HttpUtil;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText phone;
    private EditText pwd;
    private Button done;
    private String weather_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        weather_id = getIntent().getStringExtra("weather_id");


        phone = findViewById(R.id.login_phone);
        pwd = findViewById(R.id.login_pwd);
        done = findViewById(R.id.login_done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm(phone.getText().toString(), pwd.getText().toString());
            }
        });

    }


    /**
     * 账号密码验证函数
     * @param userid
     * @param pwd
     */
    public void confirm(final String userid, String pwd) {
        final String address = "http://47.113.95.141:8080/oneday/user/confirm?userid=" + userid + "&password=" + pwd;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"服务器连接失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean b = AccountUtil.comfrim(response.body().string());
                            if (b) {

                                String address_diary = "http://47.113.95.141:8080/oneday/diary/all?userid=" + userid;
                                HttpUtil.sendOkHttpRequest(address_diary, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Toast.makeText(LoginActivity.this,"服务器连接失败",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        AccountUtil.syncDiary(response.body().string());
                                    }
                                });

                                String address_schedule="http://47.113.95.141:8080/oneday/schedule/all?userid="+ userid;
                                HttpUtil.sendOkHttpRequest(address_schedule, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Toast.makeText(LoginActivity.this,"服务器连接失败",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        AccountUtil.syncSchedule(response.body().string());
                                    }
                                });


                                String address = "http://47.113.95.141:8080/oneday/user/show?userid=" + userid;
                                HttpUtil.sendOkHttpRequest(address, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Toast.makeText(LoginActivity.this,"服务器连接失败",Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        AccountUtil.login(response.body().string());
                                        saveUserid(userid);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("userId", phone.getText().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                });



                            } else {

                                Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 把userid存到一个文件里面
     * @param userid
     */
    public void saveUserid(String userid) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("userid", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(userid);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

