package com.example.bottomtest.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bottomtest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {


    public final int WRITEDNEWDIART = 4;

    //只显示标题和日期
    public List<String> data = new ArrayList<>();
    public ListView showDiary;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_show_diary, container, false);

        FloatingActionButton fab=root.findViewById(R.id.fab);
        showDiary = root.findViewById(R.id.showDiary);

        //将历史内容显示出来
        final List<DiaryContent> contents = DataSupport.findAll(DiaryContent.class);
        for (DiaryContent content : contents) {
            data.add(content.getPointMassage());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
        showDiary.setAdapter(adapter);

        //list的点击事件
        showDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到详细界面
                DiaryContent diaryContent = contents.get(position);
                Intent intent = new Intent(getActivity(), DetailDiaryActivity.class);
                intent.putExtra("DiaryContent",diaryContent);
                startActivity(intent);
            }
        });


        //增加新日志
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到增加新日志的界面
                Intent intent = new Intent(getActivity(), WriteDairyActivity.class);
                startActivityForResult(intent,WRITEDNEWDIART);
            }
        });

        return root;
    }

    @Override
   public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}