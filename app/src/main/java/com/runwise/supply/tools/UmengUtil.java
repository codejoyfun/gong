package com.runwise.supply.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.runwise.supply.GlobalApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Dong on 2017/12/19.
 */

public class UmengUtil {

    /**
     * 加上用户名，传给友盟
     * 由于需要用户名，所以放不了基类
     * @param errorMsg
     */
    public static void reportError(String tag,String errorMsg){
        MobclickAgent.reportError(GlobalApplication.getInstance(),tag+"--"+GlobalApplication.getInstance().getUserName()+"--"+errorMsg);
    }

    /**
     * 获取application中指定的meta-data。本例中，调用方法时key就是UMENG_CHANNEL
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    public static String getChannel(Context ctx){
       return getAppMetaData(ctx, "UMENG_CHANNEL");
    }

     public static final String EVENT_ID_DATE_OF_SERVICE  = "date_of_service";
     public static final String EVENT_ID_ORDER_SUBMIT  = "order_submit";
     public static final String EVENT_ID_RECEIVE_FINISH  = "receive_finish";
     public static final String EVENT_ID_START_THE_INVENTORY  = "start_the_inventory";
     public static final String EVENT_ID_SUBMIT_THE_INVENTORY  = "submit_the_inventory";
     public static final String EVENT_ID_XUAN_HAO_L  = "xuan_hao_l";
}
