package com.runwise.supply.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.bean.ReceiverLogoutEvent;
import com.kids.commonframe.base.util.net.NetWorkHelper;

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
    public static final String TYPE_LOGIN_CONFICT = "login_confict";
    public static final String TYPE_ORDER = "order";

    private static final int REQUEST_LOGINOUT = 1 << 0;
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
                    if (type.equals(TYPE_LOGIN_CONFICT)){
                        resetLoginStatus(context);
                    }
                    if (type.equals(TYPE_ORDER)){
                        //刷新订单列表

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    private void resetLoginStatus(Context context){
        //执行登出接口
        Object param = null;
        NetWorkHelper<BaseEntity> netWorkHelper = new NetWorkHelper<BaseEntity>(context, new NetWorkHelper.NetWorkCallBack<BaseEntity>() {
            @Override
            public BaseEntity onParse(int where, Class<?> targerClass, String result) {
                return null;
            }

            @Override
            public void onSuccess(BaseEntity result, int where) {
                switch(where){
                    case REQUEST_LOGINOUT:
                        break;
                }
            }

            @Override
            public void onFailure(String errMsg, BaseEntity result, int where) {

            }
        });
        netWorkHelper.sendConnection("/gongfu/v2/reset_login_status",param,REQUEST_LOGINOUT,true,null);
    }

}
