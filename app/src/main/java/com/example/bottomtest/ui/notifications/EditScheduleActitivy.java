package com.example.bottomtest.ui.notifications;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bottomtest.R;
import com.example.bottomtest.utils.CustomDialog;
import com.suke.widget.SwitchButton;

import java.util.Calendar;
import java.util.Locale;

/**
 * 编辑功能和新建功能基本一致，需要把用户已设置的信息写入
 */
public class EditScheduleActitivy extends AppCompatActivity implements View.OnClickListener{

    private EditText title;
    private RadioButton imp, mid, easy;
    private EditText remark;
    private SwitchButton switchButton;
    private Button chooseDate;
    private Button back, done;
    Calendar calendar= Calendar.getInstance(Locale.CHINA);
    private int importance;
    private Boolean remind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        final Intent intent = getIntent();
        final ScheduleInfo scheduleInfo = (ScheduleInfo) intent.getSerializableExtra("editschedule");


        title = findViewById(R.id.edit_sche_title);
        imp = findViewById(R.id.edit_sche_radio_imp);
        mid = findViewById(R.id.edit_sche_radio_mid);
        easy = findViewById(R.id.edit_sche_radio_easy);
        remark = findViewById(R.id.edit_sche_remark);
        switchButton = findViewById(R.id.switch_button);
        chooseDate = findViewById(R.id.sche_edit_time);
        back = findViewById(R.id.edit_sche_back_button);
        done = findViewById(R.id.edit_sche_edit_done);

        title.setText(scheduleInfo.getScheduleTitle());
        remark.setText(scheduleInfo.getRemark());
        chooseDate.setText(scheduleInfo.getDate());
        switch (scheduleInfo.getImportance()) {
            case 1:
                imp.setChecked(true);
                break;
            case 2:
                mid.setChecked(true);
                break;
            case 3:
                easy.setChecked(true);
                break;
        }
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(EditScheduleActitivy.this, 3, chooseDate, calendar);
                Toast.makeText(EditScheduleActitivy.this, "成功选择" + chooseDate.getText(), Toast.LENGTH_LONG).show();
            }
        });

        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                remind=switchButton.isChecked();
            }
        });

        imp.setOnClickListener(this);
        mid.setOnClickListener(this);
        easy.setOnClickListener(this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=1;
                if (!title.getText().toString().isEmpty()) {
                    scheduleInfo.setScheduleTitle(title.getText().toString());
                } else {
                    Toast.makeText(EditScheduleActitivy.this, "请填写标题", Toast.LENGTH_LONG).show();
                    flag = 0;
                }
                if (!remark.getText().toString().isEmpty()) {
                    scheduleInfo.setRemark(remark.getText().toString());
                }
                scheduleInfo.setDate(chooseDate.getText().toString());
                scheduleInfo.setRemind(remind);
                scheduleInfo.setImportance(importance);
                if (flag == 1) {
                    //修改完成后直接返回详情页面
                    scheduleInfo.updateAll("mark=?", scheduleInfo.getMark());
                    Intent intent1 = new Intent(EditScheduleActitivy.this, DetailScheduleActivity.class);
                    intent1.putExtra("detailschedule", scheduleInfo);
                    startActivity(intent1);
                    finish();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog customDialog = new CustomDialog(EditScheduleActitivy.this);
                customDialog.setTitle("提示").setMassage("尚未保存，确定退出本次编辑吗？").setConfirm("确定", new CustomDialog.IonConfirmListener() {
                    @Override
                    public void OnConfirm(CustomDialog dialog) {
                        Intent intent2 = new Intent(EditScheduleActitivy.this, DetailScheduleActivity.class);
                        intent2.putExtra("detailschedule", scheduleInfo);
                        startActivity(intent2);
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


    }
    public static void showDatePickerDialog(Activity activity, int themeResId, final Button tv, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            // 绑定监听器(How the parent is notified that the date is set.)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                tv.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_sche_radio_imp:
                imp.setChecked(true);
                mid.setChecked(false);
                easy.setChecked(false);
                importance = 1;
                break;
            case R.id.edit_sche_radio_mid:
                imp.setChecked(false);
                mid.setChecked(true);
                easy.setChecked(false);
                importance = 2;
                break;
            case R.id.edit_sche_radio_easy:
                imp.setChecked(false);
                mid.setChecked(false);
                easy.setChecked(true);
                importance = 3;
                break;

        }
    }
}
