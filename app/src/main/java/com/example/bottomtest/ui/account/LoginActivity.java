package com.example.bottomtest.ui.account;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.IOException;
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


    public boolean confirm(String userid, String pwd) {
        boolean result = false;
        String address = "http://47.113.95.141:8080/oneday/user/confirm?userid=" + userid + "&password=" + pwd;
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
                                List<User> users = DataSupport.where("userId=?", phone.getText().toString()).find(User.class);
                                users.get(0).setWeatherId(weather_id);
                                users.get(0).updateAll("userId=?", phone.getText().toString());


                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userId", phone.getText().toString());
                                startActivity(intent);
                                finish();
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
        return result;
    }
}

