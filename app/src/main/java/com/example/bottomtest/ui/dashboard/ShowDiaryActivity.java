package com.example.bottomtest.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.bottomtest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 无用类
 */
public class ShowDiaryActivity extends AppCompatActivity {

    private RecyclerView showDiaryRV;
    private List<DiaryContent> mDiary = new ArrayList<>();
    private FloatingActionButton newDiary;
    private DiaryAdapter diaryAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);

        List<DiaryContent> diaryContents = DataSupport.findAll(DiaryContent.class);
        mDiary.addAll(diaryContents);
        showDiaryRV = findViewById(R.id.show_diary_recycle);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        showDiaryRV.setLayoutManager(staggeredGridLayoutManager);
        diaryAdapter = new DiaryAdapter(mDiary);
        showDiaryRV.setAdapter(diaryAdapter);

        newDiary = findViewById(R.id.show_diary_fab);
        newDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDiaryActivity.this, WriteDairyActivity.class);
                startActivity(intent);
            }
        });

        diaryAdapter.setOnitemClickLintener(new DiaryAdapter.OnitemClick() {
            @Override
            public void onItemClick(int position) {
                DiaryContent diaryContent = mDiary.get(position);
                Intent intent = new Intent(ShowDiaryActivity.this, DetailDiaryActivity.class);
                intent.putExtra("DiaryContent",diaryContent);
                startActivity(intent);
            }
        });
    }
}
