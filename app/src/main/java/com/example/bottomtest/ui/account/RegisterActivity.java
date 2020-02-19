package com.example.bottomtest.ui.account;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;


public class RegisterActivity extends AppCompatActivity {

    private String userPhone;
    private TextView phoneText;
    private EditText nickName, pwd, pwdConfirm;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        userPhone = getIntent().getStringExtra("userPhone");
        phoneText = findViewById(R.id.registerPhone);
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
                user.setUserPassword(pwd.getText().toString());
                user.save();
                finish();
            }
        });




    }


}
