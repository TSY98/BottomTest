package com.example.bottomtest.ui.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bottomtest.MainActivity;
import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText phone;
    private EditText pwd;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        final String weather_id = getIntent().getStringExtra("weather_id");


        phone = findViewById(R.id.login_phone);
        pwd = findViewById(R.id.login_pwd);
        done = findViewById(R.id.login_done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> users = DataSupport.where("userId=?", phone.getText().toString()).find(User.class);
                users.get(0).setWeatherId(weather_id);
                users.get(0).updateAll("userId=?", phone.getText().toString());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userId", phone.getText().toString());
                startActivity(intent);
                finish();
            }
        });

    }
}

