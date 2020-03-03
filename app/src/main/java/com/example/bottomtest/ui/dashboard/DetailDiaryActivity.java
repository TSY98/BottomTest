package com.example.bottomtest.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;
import com.example.bottomtest.ui.home.util.HttpUtil;
import com.example.bottomtest.utils.CustomDialog;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 查看日记详情，内设删除和编辑跳转按钮
 */
public class DetailDiaryActivity extends AppCompatActivity {

    private TextView date;
    private TextView theme;
    private TextView detail;
    private Button btn_back;
    private Button btn_delete;
    private Button btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial_diary);

        //隐藏原始的标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        final DiaryContent diaryContent= (DiaryContent) intent.getSerializableExtra("DiaryContent");

        date = findViewById(R.id.dairyDate);
        theme = findViewById(R.id.dairyTheme);
        detail = findViewById(R.id.detailText);

        date.setText(diaryContent.getDate());
        theme.setText(diaryContent.getTitle());

        String str=diaryContent.getContent();
        SpannableString spannableString = new SpannableString(str);

        for (int i = 0; i < str.length();i++) {
            String temp = "";

            if (str.charAt(i) == '[') {
                int j;
                for (j = i + 1; j < str.length(); j++) {
                    if (str.charAt(j) == ']') {
                        break;
                    }
                }
                temp = str.substring(i + 1, j);
                File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + diaryContent.getDate()+temp + ".jpg");
                Uri uri = Uri.fromFile(file);
                ImageSpan imageSpan = new ImageSpan(this, uri);
                spannableString.setSpan(imageSpan,i,j+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                i = j + 1;
            }
        }
        detail.setText(spannableString);

        btn_back = findViewById(R.id.back_button);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //删除日记
        btn_delete = findViewById(R.id.delete_button);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomDialog customDialog = new CustomDialog(DetailDiaryActivity.this);
                customDialog.setMassage("确认删除该条日记吗？").setConfirm("确认", new CustomDialog.IonConfirmListener() {
                    @Override
                    public void OnConfirm(CustomDialog dialog) {
                        DataSupport.deleteAll(DiaryContent.class,"date=?",diaryContent.getDate());
                        Toast.makeText(DetailDiaryActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                        uptoServer(diaryContent);
//                        Intent intent1 = new Intent(DetailDiaryActivity.this, ShowDiaryActivity.class);
//                        startActivity(intent1);
                    }
                }).setCancle("取消", new CustomDialog.IonCancleListener() {
                    @Override
                    public void OnCancle(CustomDialog dialog) {
                        customDialog.cancel();
                    }
                }).show();



            }
        });

        //编辑日记
        btn_edit = findViewById(R.id.Edit_button);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DetailDiaryActivity.this, EditDiaryActivity.class);
                intent1.putExtra("editDairy", diaryContent);
                startActivity(intent1);
                finish();
            }
        });

    }

    public void uptoServer(DiaryContent diaryContent) {
        String userid = load();
        String address = "http://47.113.95.141:8080/oneday/diary/delete?insertdate=" + diaryContent.getDate() + "&userid=" + userid;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(DetailDiaryActivity.this, "同步失败，可以在个人中心手动同步", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                finish();
            }
        });
    }


    public String load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("userid");
            reader = new BufferedReader(new InputStreamReader(in));
            content.append(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (content.toString().isEmpty()) {
            List<User> all = DataSupport.findAll(User.class);
            return all.get(0).getUserId();
        } else {
            return content.toString();
        }

    }
}
