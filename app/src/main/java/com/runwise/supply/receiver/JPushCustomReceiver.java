package com.runwise.supply.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.bean.ReceiverLogoutEvent;
import com.kids.commonframe.base.bean.SystemUpgradeNoticeEvent;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.runwise.supply.event.OrderStatusChangeEvent;
import com.runwise.supply.event.PlatformNotificationEvent;
import com.runwise.supply.tools.PlatformNotificationManager;
import com.runwise.supply.tools.SystemUpgradeHelper;

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
    public static final String TYPE_SYSTEM_UPGRADE = "update";
    public static final String TYPE_ORDER = "sale.order";
    public static final String TYPE_PLATFORM_NOTICE = "platform";//平台通知
    public static final String TYPE_ORDER_STATUS = "type_order_status";//跳转去订单状态页面


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
                        String strId = jsonObject.optString("dataid");
                        if(!TextUtils.isEmpty(strId) && TextUtils.isDigitsOnly(strId)){
                            EventBus.getDefault().post(new OrderStatusChangeEvent(Integer.valueOf(strId)));
                        }else{
                            EventBus.getDefault().post(new OrderStatusChangeEvent());
                        }
                    }
                    if (TYPE_SYSTEM_UPGRADE.equals(type)){
                        String dataid = jsonObject.optString("dataid");
                        SystemUpgradeHelper.getInstance(context).create(dataid);
                        EventBus.getDefault().post(new SystemUpgradeNoticeEvent());
                    }
                    if(TYPE_PLATFORM_NOTICE.equals(type)){//平台通知
                        JSONObject aps = jsonObject.optJSONObject("APS");
                        String msg = aps.optString("alerts");
                        long id = System.currentTimeMillis();
                        PlatformNotificationManager.getInstance(context).addMsg(id,msg,"");
                        EventBus.getDefault().post(new PlatformNotificationEvent());
                    }
                    if (TYPE_ORDER_STATUS.equals(type)){//跳去订单状态页面

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
