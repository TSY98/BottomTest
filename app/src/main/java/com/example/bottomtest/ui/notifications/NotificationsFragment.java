package com.example.bottomtest.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bottomtest.R;

/**
 * 日程管理的主界面
 * 这个fragment主要完成对内嵌fragment的处理
 */
public class NotificationsFragment extends Fragment implements View.OnClickListener{

    private IsDoneFragment isDoneFragment;
    private NoDoneFragment noDoneFragment;

    private TextView btn_nodone;
    private TextView btn_isdone;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_show_schedule_in_all, container, false);

        btn_isdone = root.findViewById(R.id.isDone_button);
        btn_nodone = root.findViewById(R.id.noDone_button);
        btn_isdone.setOnClickListener(this);
        btn_nodone.setOnClickListener(this);

        //初始情况是显示未完成日程界面
        initNoDoneFragement();
        btn_nodone.setActivated(true);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.isDone_button:
                initIsDoneFragement();
                btn_nodone.setActivated(false);
                btn_isdone.setActivated(true);
                break;
            case R.id.noDone_button:
                initNoDoneFragement();
                btn_nodone.setActivated(true);
                btn_isdone.setActivated(false);
                break;
        }
    }

    //初始化未完成日程页面
    private void initNoDoneFragement() {
        //这里是内嵌的fragment，使用使用getChildFragmentManager而不是getSupportFragmentManager
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (noDoneFragment == null) {
            noDoneFragment = new NoDoneFragment();
            //fragmentTransaction.add(R.id.main_fragment, noDoneFragment);
        }
        noDoneFragment = new NoDoneFragment();
        //使用replace完成fragment之间的转换
        fragmentTransaction.replace(R.id.main_fragment, noDoneFragment);
        //hideFragement(fragmentTransaction);
        //fragmentTransaction.show(noDoneFragment);
        fragmentTransaction.commit();
    }

    //初始化已完成日程界面
    private void initIsDoneFragement() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (isDoneFragment == null) {
            isDoneFragment = new IsDoneFragment();
            //fragmentTransaction.add((R.id.main_fragment), isDoneFragment);
        }
        //hideFragement(fragmentTransaction);
        isDoneFragment = new IsDoneFragment();
        fragmentTransaction.replace(R.id.main_fragment, isDoneFragment);
        //fragmentTransaction.show(isDoneFragment);
        fragmentTransaction.commit();
    }
}