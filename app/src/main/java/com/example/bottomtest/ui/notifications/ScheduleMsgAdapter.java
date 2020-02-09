package com.example.bottomtest.ui.notifications;

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

import java.util.List;

import static org.litepal.LitePalApplication.getContext;

/**
 * 使用recycleView来完成日程列表的显示，这是未完成列表的adapter
 */
public class ScheduleMsgAdapter extends RecyclerView.Adapter<ScheduleMsgAdapter.ViewHolder> {
    private List<ScheduleInfo> mScheduleMsgList;

    private OnitemClick onitemClick;

    //点击事件的接口，实现回调
    //一定要采取这方式，才可以既得到position的信息，又可以实现页面间的跳转
    public void setOnitemClickLintener (OnitemClick onitemClick) {
        this.onitemClick = onitemClick;
    }
    public interface OnitemClick {
        void onItemClick(int position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout total;
        TextView title;
        TextView date;
        TextView importance;
        Button done;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.show_sche_title);
            date = itemView.findViewById(R.id.show_sche_date);
            importance = itemView.findViewById(R.id.show_sche_importance);
            done = itemView.findViewById(R.id.show_sche_done);
            total = itemView.findViewById(R.id.show_sche_total);
        }
    }

    public ScheduleMsgAdapter(List<ScheduleInfo> msgList) {
        mScheduleMsgList = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_recycle_schedule, parent, false);

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
        //处理“已完成”按钮：更新数据库，删除list信息，更新recycleView
        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), ""+position, Toast.LENGTH_LONG).show();
                ScheduleInfo scheduleInfo = mScheduleMsgList.get(position);
                scheduleInfo.setDone(true);
                scheduleInfo.updateAll("mark=?", scheduleInfo.getMark());
                mScheduleMsgList.remove(position);
                notifyItemRemoved(position);//刷新被删除的地方
                notifyItemRangeChanged(position, getItemCount());
                Toast.makeText(getContext(), "完成", Toast.LENGTH_LONG).show();
            }
        });

        //设置好了每条信息的点击事件
        holder.total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onitemClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mScheduleMsgList.size();
    }
}
