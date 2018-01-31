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
     public static final String EVENT_ID_ORDER_SUBMIT_SELF  = "order_submit";
     public static final String EVENT_ID_ORDER_SUBMIT_ALWAY  = "order_submit_alway";
     public static final String EVENT_ID_ORDER_SUBMIT_SMART  = "order_submit_smart";
     public static final String EVENT_ID_RECEIVE_FINISH  = "receive_finish";
    /**
     * 创建盘点单
     */
     public static final String EVENT_ID_START_THE_INVENTORY  = "start_the_inventory";
     public static final String EVENT_ID_SUBMIT_THE_INVENTORY  = "submit_the_inventory";
     public static final String EVENT_ID_XUAN_HAO_L  = "xuan_hao_l";
    /**
     * 轮播栏
     */
    public static final String EVENT_ID_CAROUSEL_BAR  = "carousel_bar";
    /**
     * 上周采购量
     */
     public static final String EVENT_ID_LAST_WEEK_PURCHASE  = "last_week_purchase";
    /**
     * 商品数量修改
     */
    public static final String EVENT_ID_PRODUCT_COUNT_MODIFY  = "product_count_modify";
    /**
     * 购物车
     */
    public static final String EVENT_ID_SHOPPING_CART  = "shopping_cart";
    /**
     * 继续选择
     */
    public static final String EVENT_ID_CONTINUE_TO_CHOOSE  = "continue_to_choose";
    /**
     * 评价
     */
    public static final String EVENT_ID_EVALUATE  = "evaluate";

    /**
     * 盘点单添加商品
     */
    public static final String EVENT_ID_ADD_INVENTORY_PRODUCT  = "add_inventory_product";
    /**
     * 用户指南
     */
    public static final String EVENT_ID_USER_GUIDE  = "user_guide";

    /**
     * 再来一单
     */
    public static final String EVENT_ID_ORDER_AGAIN  = "order_again";
    /**
     * 修改订单
     */
    public static final String EVENT_ID_ORDER_MODIFY  = "order_modify";
}
