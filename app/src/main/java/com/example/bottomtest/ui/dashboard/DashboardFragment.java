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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.bottomtest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {


    public final int WRITEDNEWDIART = 4;

    private RecyclerView showDiaryRV;
    private List<DiaryContent> mDiary = new ArrayList<>();
    private FloatingActionButton newDiary;
    private DiaryAdapter diaryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_show_diary, container, false);

        List<DiaryContent> diaryContents = DataSupport.findAll(DiaryContent.class);
        mDiary.addAll(diaryContents);
        showDiaryRV = root.findViewById(R.id.show_diary_recycle);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        showDiaryRV.setLayoutManager(staggeredGridLayoutManager);
        diaryAdapter = new DiaryAdapter(mDiary);
        showDiaryRV.setAdapter(diaryAdapter);

        newDiary = root.findViewById(R.id.show_diary_fab);
        newDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteDairyActivity.class);
                startActivityForResult(intent,1);
            }
        });

        diaryAdapter.setOnitemClickLintener(new DiaryAdapter.OnitemClick() {
            @Override
            public void onItemClick(int position) {
                DiaryContent diaryContent = mDiary.get(position);
                Intent intent = new Intent(getActivity(), DetailDiaryActivity.class);
                intent.putExtra("DiaryContent",diaryContent);
                startActivityForResult(intent,1);
            }
        });
        return root;
    }

    @Override
   public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<DiaryContent> diaryContents = DataSupport.findAll(DiaryContent.class);
        mDiary.clear();
        mDiary.addAll(diaryContents);
        diaryAdapter.notifyDataSetChanged();
    }
}