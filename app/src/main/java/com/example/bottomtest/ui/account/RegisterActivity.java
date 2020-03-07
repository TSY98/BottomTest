package com.example.bottomtest.ui.account;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;
import com.example.bottomtest.ui.home.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 注册Activity
 * 密码的正确性逻辑还未写
 */
public class RegisterActivity extends AppCompatActivity {

    private String userPhone;
    private TextView phoneText;
    private EditText nickName, pwd, pwdConfirm;
    private Button done;
    private String weather_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        weather_id = getIntent().getStringExtra("weather_id");

        userPhone = getIntent().getStringExtra("userPhone");
        phoneText = findViewById(R.id.registerPhone);
        phoneText.setText(userPhone);
        nickName = findViewById(R.id.registerNickName);
        pwd = findViewById(R.id.registerPsw);
        pwdConfirm = findViewById(R.id.registerPswConfirm);
        done = findViewById(R.id.registerDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setNickName(nickName.getText().toString());
                user.setUserId(phoneText.getText().toString());
                uptoServer(user);
            }
        });

    }

    /**
     * 账户信息注册成功后，上传到服务器
     * @param user
     */
    public void uptoServer(User user) {
        String address = "http://47.113.95.141:8080/oneday/user/add?userid=" + user.getUserId() + "&password=" + pwd.getText() + "&nickname=" + user.getNickName() + "&weatherid=" + weather_id;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(RegisterActivity.this, "连接服务器失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                finish();
            }
        });
    }


}
