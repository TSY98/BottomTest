package com.example.bottomtest.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bottomtest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * 显示日记摘要
 * 未完成：服务器部分
 */

public class ShowDiaryActivity extends AppCompatActivity {
    public final int WRITEDNEWDIART = 4;

    //只显示标题和日期
    public List<String> data = new ArrayList<>();
    public ListView showDiary;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);

        FloatingActionButton fab=findViewById(R.id.fab);
        showDiary = findViewById(R.id.showDiary);

        //将历史内容显示出来
        final List<DiaryContent> contents = DataSupport.findAll(DiaryContent.class);
        for (DiaryContent content : contents) {
            data.add(content.getPointMassage());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ShowDiaryActivity.this, android.R.layout.simple_list_item_1, data);
        showDiary.setAdapter(adapter);

        //list的点击事件
        showDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到详细界面
                DiaryContent diaryContent = contents.get(position);
                Intent intent = new Intent(ShowDiaryActivity.this, DetailDiaryActivity.class);
                intent.putExtra("DiaryContent",diaryContent);
                startActivity(intent);
            }
        });


        //增加新日志
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到增加新日志的界面
                Intent intent = new Intent(ShowDiaryActivity.this, WriteDairyActivity.class);
                startActivityForResult(intent,WRITEDNEWDIART);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITEDNEWDIART) {
            recreate();
        }
    }
}
