package com.runwise.supply.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kids.commonframe.base.bean.ReceiverLogoutEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by mike on 2017/9/26.
 */

public class JPushCustomReceiver extends BroadcastReceiver {
    public static final String TAG = "JPushCustomReceiver";
    public static final String TYPE_LOGOUT = "logout";
    @Override
    public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
        if (bundle != null){
            String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
            if (!TextUtils.isEmpty(message)){
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String type = jsonObject.optString("type");
                    if (type.equals(TYPE_LOGOUT)){
                        //发送被迫下线通知
                        EventBus.getDefault().post(new ReceiverLogoutEvent());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            Log.i(TAG,message);
        }
    }
}
