package com.example.bottomtest.ui.account;

import android.text.TextUtils;

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
}
