package com.example.bottomtest.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottomtest.R;
import com.example.bottomtest.utils.CustomDialog;

import org.litepal.crud.DataSupport;

/**
 * 查看详细内容功能
 */

public class DetailScheduleActivity extends AppCompatActivity {

    private Button back;
    private TextView title;
    private TextView date;
    private ImageView imp, mid, easy ,donetag, nodonetag;
    private TextView remark;
    private TextView importance;
    private TextView donePosition;
    private Button edit, delete, gonodone;
    private TextView editText, gonodoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        final Intent intent = getIntent();
        final ScheduleInfo scheduleInfo = (ScheduleInfo) intent.getSerializableExtra("detailschedule");

        back = findViewById(R.id.detail_sche_back);
        title = findViewById(R.id.detail_sche_title);
        date = findViewById(R.id.detail_sche_date);
        imp = findViewById(R.id.img_imp);
        mid = findViewById(R.id.img_mid);
        easy = findViewById(R.id.img_easy);
        importance = findViewById(R.id.detail_sche_importance);
        remark = findViewById(R.id.detail_sche_remark);
        donetag = findViewById(R.id.show_sche_isdone);
        nodonetag = findViewById(R.id.show_sche_nodone);
        donePosition = findViewById(R.id.show_sche_donetag);

        edit = findViewById(R.id.detail_sche_edit);
        delete = findViewById(R.id.detail_sche_delete);
        gonodone = findViewById(R.id.detail_sche_gonodone);
        gonodoneText = findViewById(R.id.gonodoneText);
        editText = findViewById(R.id.eidtText);

        //根据日程的完成与否显示不同的界面
        //完成的显示完成图标，并且没有编辑按钮，有设置成未完成按钮
        //未完成的显示未完成的图标，有编辑按钮
        if (scheduleInfo.isDone()) {
            edit.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            nodonetag.setVisibility(View.GONE);
            donePosition.setText("已完成");
        } else {
            edit.setVisibility(View.VISIBLE);
            gonodone.setVisibility(View.GONE);
            gonodoneText.setVisibility(View.GONE);
            donetag.setVisibility(View.GONE);
            donePosition.setText("进行中");
        }

        title.setText(scheduleInfo.getScheduleTitle());
        date.setText(scheduleInfo.getDate());
        remark.setText(scheduleInfo.getRemark());

        //根据重要程度设置图标颜色的不同
        switch (scheduleInfo.getImportance()) {
            case 1:
                importance.setText("重要");
                imp.setVisibility(View.VISIBLE);
                mid.setVisibility(View.INVISIBLE);
                easy.setVisibility(View.INVISIBLE);
                break;
            case 2:
                importance.setText("一般");
                imp.setVisibility(View.INVISIBLE);
                mid.setVisibility(View.VISIBLE);
                easy.setVisibility(View.INVISIBLE);
                break;
            case 3:
                importance.setText("悠闲");
                imp.setVisibility(View.INVISIBLE);
                mid.setVisibility(View.INVISIBLE);
                easy.setVisibility(View.VISIBLE);
                break;
        }

        //删除日程
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //自定义的提示框
                final CustomDialog customDialog = new CustomDialog(DetailScheduleActivity.this);
                customDialog.setTitle("提示").setMassage("确认删除该日程吗？").setConfirm("确定", new CustomDialog.IonConfirmListener() {
                    @Override
                    public void OnConfirm(CustomDialog dialog) {
                        DataSupport.deleteAll(ScheduleInfo.class, "mark=?", scheduleInfo.getMark());
                        Toast.makeText(DetailScheduleActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).setCancle("取消", new CustomDialog.IonCancleListener() {
                    @Override
                    public void OnCancle(CustomDialog dialog) {
                        customDialog.cancel();
                    }
                }).show();
            }
        });

        //编辑按钮的跳转逻辑
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DetailScheduleActivity.this, EditScheduleActitivy.class);
                intent1.putExtra("editschedule", scheduleInfo);
                startActivity(intent1);
                finish();
            }
        });

        //重新设置成未完成按钮逻辑
        gonodone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(DetailScheduleActivity.this);
                dialog.setTitle("提示").setMassage("是否把该日程重新标为未完成？").setConfirm("确定", new CustomDialog.IonConfirmListener() {
                    @Override
                    public void OnConfirm(CustomDialog dialog) {
                        //注意使用setToDefault
                        scheduleInfo.setToDefault("isDone");
                        scheduleInfo.updateAll("mark=?", scheduleInfo.getMark());
                        Intent intent1 = new Intent(DetailScheduleActivity.this, DetailScheduleActivity.class);
                        intent1.putExtra("detailschedule", scheduleInfo);
                        startActivity(intent1);
                        finish();
                    }
                }).setCancle("取消", new CustomDialog.IonCancleListener() {
                    @Override
                    public void OnCancle(CustomDialog dialog) {
                        dialog.cancel();
                    }
                }).show();
            }
        });

        //返回上个界面按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
