package com.example.bottomtest.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomtest.R;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private List<DiaryContent> mDiary;

    private OnitemClick onitemClick;

    public void setOnitemClickLintener (OnitemClick onitemClick) {
        this.onitemClick = onitemClick;
    }
    public interface OnitemClick {
        void onItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date;
        LinearLayout total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.show_diary_title);
            content = itemView.findViewById(R.id.show_diary_content);
            date = itemView.findViewById(R.id.show_diary_date);
            total = itemView.findViewById(R.id.show_diary_total);
        }
    }

    public DiaryAdapter(List<DiaryContent> diaryContents) {
        mDiary = diaryContents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_diary_show, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        DiaryContent diaryContent = mDiary.get(position);
        holder.title.setText(diaryContent.getTitle());
        holder.content.setText(diaryContent.getContent());
        holder.date.setText(diaryContent.getDate());
        holder.total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onitemClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiary.size();
    }




}
