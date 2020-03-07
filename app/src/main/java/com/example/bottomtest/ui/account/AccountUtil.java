package com.example.bottomtest.ui.account;

import android.text.TextUtils;
import android.widget.Toast;

import com.example.bottomtest.ui.User;
import com.example.bottomtest.ui.dashboard.DiaryContent;
import com.example.bottomtest.ui.home.util.HttpUtil;
import com.example.bottomtest.ui.notifications.ScheduleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountUtil {
    public static boolean comfrim(String response) {
        if (!TextUtils.isEmpty(response)) {
            String result = response;
            if (result.equals("right")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void login(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                DataSupport.deleteAll(User.class);
                User user = new User();
                user.setUserId(jsonObject.getString("userid"));
                user.setWeatherId(jsonObject.getString("weatherid"));
                user.setNickName(jsonObject.getString("nickname"));
                user.setSynctime(jsonObject.getString("synctime"));
                user.save();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void syncDiary(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                DataSupport.deleteAll(DiaryContent.class);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    DiaryContent diaryContent = new DiaryContent();
                    diaryContent.setCount(jsonObject.getInt("imgcount"));
                    diaryContent.setContent(jsonObject.getString("diarycontent"));
                    diaryContent.setTitle(jsonObject.getString("diarytitle"));
                    diaryContent.setDate(jsonObject.getString("insertDate"));
                    diaryContent.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void syncSchedule(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                DataSupport.deleteAll(ScheduleInfo.class);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ScheduleInfo scheduleInfo = new ScheduleInfo();
                    scheduleInfo.setRemindTime(jsonObject.getString("remindtime"));
                    scheduleInfo.setMark(jsonObject.getString("markTime"));
                    scheduleInfo.setImportance(jsonObject.getInt("importance"));
                    scheduleInfo.setScheduleTitle(jsonObject.getString("event"));
                    scheduleInfo.setRemark(jsonObject.getString("remark"));
                    scheduleInfo.setDate(jsonObject.getString("eventdate"));
                    int isdone = jsonObject.getInt("isdone");
                    if (isdone == 1) {
                        scheduleInfo.setDone(true);
                    } else {
                        scheduleInfo.setDone(false);
                    }
                    int isremind = jsonObject.getInt("isremind");
                    if (isremind == 1) {
                        scheduleInfo.setRemind(true);
                    } else {
                        scheduleInfo.setRemind(false);
                    }
                    scheduleInfo.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
