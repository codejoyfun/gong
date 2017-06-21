package com.kids.commonframe.base.bean;

import android.content.Intent;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 16/7/4.
 */
public class UserLoginEvent {
    //用户信息
    private BaseEntity userInfo;
    //调整意图
    private Intent intent;

    public BaseEntity getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(BaseEntity userInfo) {
        this.userInfo = userInfo;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
