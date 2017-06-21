package com.runwise.supply.tools;

import android.app.Activity;
import android.content.Intent;

import com.runwise.supply.LoginActivity;
import com.kids.commonframe.base.util.SPUtils;

/**
 * Created by myChaoFile on 16/12/1.
 */

public class UserUtils {

    /**
     * 验证登录
     *
     * @param targerIntent
     * @return
     */
    public static boolean checkLogin(Intent targerIntent, Activity mContext) {
        boolean isLogin = SPUtils.isLogin(mContext);
        if (!isLogin) {
            LoginActivity.targerIntent = targerIntent;
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(loginIntent);
            return false;
        }
        return true;
    }
}
