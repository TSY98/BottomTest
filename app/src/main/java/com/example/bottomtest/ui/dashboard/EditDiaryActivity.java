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

/**
 * 编辑日记
 * 未完成：没修改的情况无法识别
 */
public class EditDiaryActivity extends AppCompatActivity {

    private Button back;
    private Button done;
    private EditText title;
    private EditText massage;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        //隐藏原始的标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        back = findViewById(R.id.edit_back_button);
        done = findViewById(R.id.edit_edit_done);
        title = findViewById(R.id.edit_title_edit);
        massage = findViewById(R.id.edit_diary_edit);
        date = findViewById(R.id.edit_dateText);

        Intent intent = getIntent();
        final DiaryContent diaryContent= (DiaryContent)intent.getSerializableExtra("editDairy");

        title.setText(diaryContent.getTitle());
        massage.setText(diaryContent.getContent());
        date.setText(diaryContent.getDate());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!title.getText().toString().isEmpty()) || (massage.getText().toString().isEmpty())){
                    AlertDialog.Builder prompt = new AlertDialog.Builder(EditDiaryActivity.this);
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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改当前date下的相关信息
                diaryContent.setContent(massage.getText().toString());
                diaryContent.setTitle(title.getText().toString());
                diaryContent.updateAll("date=?", diaryContent.getDate());
                //Intent intent1 = new Intent(EditDiaryActivity.this, ShowDiaryActivity.class);
                Toast.makeText(EditDiaryActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                //startActivity(intent1);
                finish();
            }
        });

    }
}
