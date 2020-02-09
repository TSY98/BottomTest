package com.example.bottomtest.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomtest.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 已完成日程的Fragment
 * 和未完成不同点在于最后一个按钮的功能不同，并且没有增加新日程的按钮
 */
public class IsDoneFragment extends Fragment {
    private RecyclerView showSchedule;
    private List<ScheduleInfo> scheduleInfoList = new ArrayList<>();
    private ScheduleMsgIsDoneAdapter adapter;
    private List<ScheduleInfo> scheduleInfos;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_show_schedule_is_done, container, false);

        //从数据库只提取已完成的信息，存入的时候是true or false，取出的时候是0 or 1
        scheduleInfos = DataSupport.where("isDone=?", "1").find(ScheduleInfo.class);
        scheduleInfoList.addAll(scheduleInfos);
        Log.d("count", ""+scheduleInfos.size());
        //test();

        showSchedule = view.findViewById(R.id.recycle_showSch_isdone);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        showSchedule.setLayoutManager(layoutManager);
        adapter = new ScheduleMsgIsDoneAdapter(scheduleInfoList);
        showSchedule.setAdapter(adapter);

        adapter.setOnitemClickLintener(new ScheduleMsgIsDoneAdapter.OnitemClick() {
            @Override
            public void onItemClick(int position) {
                Intent intent1 = new Intent(getActivity(), DetailScheduleActivity.class);
                intent1.putExtra("detailschedule", scheduleInfos.get(position));
                startActivityForResult(intent1,1);
            }
        });


        return view;
    }


    //每次返回这个界面都需要更新
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scheduleInfos =  DataSupport.where("isDone=?", "1").find(ScheduleInfo.class);
        scheduleInfoList.clear();
        scheduleInfoList.addAll(scheduleInfos);
        adapter.notifyDataSetChanged();
    }

    private void test() {
//        ScheduleInfo scheduleInfo = new ScheduleInfo();
//        scheduleInfo.setScheduleTitle("dfsa");
//        scheduleInfo.setImportance(2);
        ScheduleInfo scheduleInfo1 = new ScheduleInfo();
        scheduleInfo1.setScheduleTitle("birthday");
        scheduleInfo1.setImportance(3);
        scheduleInfo1.setDate("2019年1月9号");
        scheduleInfo1.setRemind(false);
        scheduleInfo1.setDone(false);
        scheduleInfo1.setRemark("oneus");
        ScheduleInfo scheduleInfo2 = new ScheduleInfo();
        scheduleInfo2.setScheduleTitle("meeting");
        scheduleInfo2.setImportance(1);
        scheduleInfo1.setDate("2019年3月19号");
        scheduleInfo1.setRemind(false);
        scheduleInfo1.setRemark("day");
        scheduleInfo2.setDone(true);
        //scheduleInfo1.save();
        //scheduleInfo2.save();
        scheduleInfoList.add(scheduleInfo1);
        scheduleInfoList.add(scheduleInfo2);
    }
}
