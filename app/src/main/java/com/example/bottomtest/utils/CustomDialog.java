package com.example.bottomtest.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bottomtest.R;

/**
 * 自定义dialog
 */

public class CustomDialog extends Dialog implements View.OnClickListener {

    private TextView titleText;
    private TextView massageText;
    private TextView posBtn;
    private TextView navBtn;

    private String title,massage,confirm,cancle;

    private IonCancleListener cancleListener;
    private IonConfirmListener confirmListener;

    public String getTitle() {
        return title;
    }

    public CustomDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMassage() {
        return massage;
    }

    public CustomDialog setMassage(String massage) {
        this.massage = massage;
        return this;
    }

    public String getConfirm() {
        return confirm;
    }

    public CustomDialog setConfirm(String confirm,IonConfirmListener confirmListener) {
        this.confirm = confirm;
        this.confirmListener = confirmListener;
        return this;
    }

    public String getCancle() {
        return cancle;
    }

    public CustomDialog setCancle(String cancle,IonCancleListener cancleListener) {
        this.cancle = cancle;
        this.cancleListener = cancleListener;
        return this;
    }

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_dialog);

        //设置宽度
        WindowManager windowManager = getWindow().getWindowManager();
        Display d = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        layoutParams.width = (int) (size.x * 0.8);
        getWindow().setAttributes(layoutParams);


        titleText=findViewById(R.id.dl_title);
        massageText = findViewById(R.id.dl_massage);
        posBtn = findViewById(R.id.dl_posbtn);
        navBtn = findViewById(R.id.dl_navbtn);

        if (!TextUtils.isEmpty(title)) {
            titleText.setText(title);
        }
        if (!TextUtils.isEmpty(massage)) {
            massageText.setText(massage);
        }
        if (!TextUtils.isEmpty(confirm)) {
            posBtn.setText(confirm);
        }
        if (!TextUtils.isEmpty(cancle)) {
            navBtn.setText(cancle);
        }
        navBtn.setOnClickListener(this);
        posBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dl_posbtn:
                if (confirmListener != null) {
                    confirmListener.OnConfirm(this);
                }
                break;
            case R.id.dl_navbtn:
                if (cancleListener != null) {
                    cancleListener.OnCancle(this);
                }
                break;
        }
    }

    public interface IonCancleListener{
        void OnCancle(CustomDialog dialog);
    }

    public interface IonConfirmListener{
        void OnConfirm(CustomDialog dialog);
    }

}
