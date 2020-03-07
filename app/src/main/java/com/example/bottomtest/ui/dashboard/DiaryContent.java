package com.example.bottomtest.ui.dashboard;

import androidx.annotation.NonNull;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日记类
 */

public class DiaryContent extends DataSupport implements Serializable {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
    //内容
    private String content;
    //标题
    public String title;
    //日期
    private Date temp = new Date(System.currentTimeMillis());
    public String date = format.format(temp);
    //图片数量
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPointMassage(){
        return date + '\n' + title;
    }

    @NonNull
    @Override
    public String toString() {
        return "日期： "+date + '\n' + "主题： " + title +"\n\n"+ "内容：\n"+content;
    }
}
