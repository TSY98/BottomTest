package com.example.bottomtest.ui.notifications;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;
import com.example.bottomtest.ui.home.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalApplication.getContext;

/**
 * 已完成日程的recycleView的adapter
 * 问题：是否可以采取复用性跟高的方法
 */

public class ScheduleMsgIsDoneAdapter extends RecyclerView.Adapter<ScheduleMsgIsDoneAdapter.ViewHolder> {
    private List<ScheduleInfo> mScheduleMsgList;
    private OnitemClick onitemClick;

    //定义设置点击事件监听的方法
    public void setOnitemClickLintener (OnitemClick onitemClick) {
        this.onitemClick = onitemClick;
    }
    //定义一个点击事件的接口
    public interface OnitemClick {
        void onItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date;
        TextView importance;
        RelativeLayout total;
        Button gotodone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.show_sche_title_isdone);
            date = itemView.findViewById(R.id.show_sche_date_isdone);
            importance = itemView.findViewById(R.id.show_sche_importance_isdone);
            total = itemView.findViewById(R.id.show_sche_total_isdone);
            gotodone = itemView.findViewById(R.id.show_sche_isdone);
        }
    }

    public ScheduleMsgIsDoneAdapter(List<ScheduleInfo> msgList) {
        mScheduleMsgList = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_recycle_schedule_isdone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ScheduleInfo scheduleInfo = mScheduleMsgList.get(position);
        if (scheduleInfo.getImportance()==1){
            holder.importance.setText("重要");
        }else if (scheduleInfo.getImportance()==2){
            holder.importance.setText("一般");
        }else {
            holder.importance.setText("悠闲");
        }

        holder.date.setText(scheduleInfo.getDate());
        holder.title.setText(scheduleInfo.getScheduleTitle());
        holder.total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onitemClick.onItemClick(position);
            }
        });

        holder.gotodone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleInfo scheduleInfo = mScheduleMsgList.get(position);
                //false是Boolean的默认值，不能使用set直接设置
                scheduleInfo.setToDefault("isDone");
                scheduleInfo.updateAll("mark=?", scheduleInfo.getMark());
                uptoServer(scheduleInfo);
                mScheduleMsgList.remove(position);
                notifyItemRemoved(position);//刷新被删除的地方
                notifyItemRangeChanged(position, getItemCount());
                Toast.makeText(getContext(), "完成", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mScheduleMsgList.size();
    }

    public void uptoServer(ScheduleInfo scheduleInfo) {
        List<User> all = DataSupport.findAll(User.class);
        String userId = all.get(0).getUserId();
        int done=0;
        String address = "http://47.113.95.141:8080/oneday/schedule/changedone?markTime=" + scheduleInfo.getMark() + "&userid=" + userId + "&isdone=" + done;
        //Log.d("ad", address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
