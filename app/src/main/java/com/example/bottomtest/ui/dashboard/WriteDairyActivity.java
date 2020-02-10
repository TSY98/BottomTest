package com.example.bottomtest.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bottomtest.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 写日记
 */

public class WriteDairyActivity extends AppCompatActivity {

    private TextView curDate;
    private EditText titleEdit;
    private EditText diaryEdit;
    private Button done;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_dairy);

        //隐藏原始的标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        curDate = findViewById(R.id.dateText);
        titleEdit = findViewById(R.id.title_edit);
        diaryEdit = findViewById(R.id.diary_edit);
        done = findViewById(R.id.edit_done);
        back = findViewById(R.id.back_button);

        final DiaryContent diaryContent = new DiaryContent();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dateFormat = formatter.format(date);
        curDate.setText(dateFormat.substring(0,11));
        diaryContent.date = dateFormat;

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleEdit.getText().toString().isEmpty()){
                    //内容为空
                    Toast.makeText(WriteDairyActivity.this, "请添加标题", Toast.LENGTH_SHORT).show();
                }else if(diaryEdit.getText().toString().isEmpty()){
                    Toast.makeText(WriteDairyActivity.this, "没有填写日记内容", Toast.LENGTH_SHORT).show();
                }else {
                    diaryContent.setTitle(titleEdit.getText().toString());
                    diaryContent.setContent(diaryEdit.getText().toString());
                    diaryContent.save();
//                    Intent intent = new Intent(WriteDairyActivity.this, ShowDiaryActivity.class);
//                    startActivity(intent);
                    finish();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!titleEdit.getText().toString().isEmpty()) || (diaryEdit.getText().toString().isEmpty())){
                    AlertDialog.Builder prompt = new AlertDialog.Builder(WriteDairyActivity.this);
                    prompt.setTitle("提示");
                    prompt.setMessage("已编辑文字但是未保存，是否继续退出？");
                    prompt.setCancelable(false);
                    prompt.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    prompt.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    prompt.show();
                }
            }
        });

    }
}
