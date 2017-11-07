package com.runwise.supply.tools;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.util.SPUtils;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.RegisterActivity;

import java.text.DecimalFormat;

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
//            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(loginIntent);
            return false;
        }
        return true;
    }

    public static String formatPrice(String price) {
        if(TextUtils.isEmpty(price)) {
            return "";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(Double.parseDouble(price));
    }
    /*
 根据状态设置订单图标和标签
 */
    public static void setOrderStatus(String status, TextView label, ImageView icon, boolean normalOrder) {
        //待确认
        if("draft".equals(status)) {
            label.setText("待确认");
            icon.setImageResource(R.drawable.state_restaurant_1_tocertain);
        }
        //已确认
        else if("sale".equals(status)) {
            label.setText("已确认");
            icon.setImageResource(R.drawable.state_restaurant_2_certain);
        }
        //已发货
        else if("peisong".equals(status)) {
            label.setText("已发货");
            icon.setImageResource(R.drawable.state_restaurant_3_delivering);
        }
        //已收货
        //退货returnOrders

        //已评价
        else if("rated".equals(status)) {
            label.setText("已评价");
            icon.setImageResource(R.drawable.state_restaurant_5_rated);
        }
        //退货中
        if("process".equals(status)) {
            label.setText("退货中");
            icon.setImageResource(R.drawable.state_delivery_7_return);
        }
        //已退货
        else if(!normalOrder &&"done".equals(status)) {
            label.setText("已退货");
            icon.setImageResource(R.drawable.state_delivery_7_return);
        }
        //已收货
        else if(normalOrder && "done".equals(status)) {
            label.setText("已收货");
            icon.setImageResource(R.drawable.state_restaurant_2_certain);
        }
        //已取消cancel
        else if("cancel".equals(status)){
            label.setText("订单关闭");
            icon.setImageResource(R.drawable.state_restaurant_6_closed);
        }
//        else {
//            label.setText("订单关闭");
//            icon.setImageResource(R.drawable.state_restaurant_6_closed);
//        }
    }
}
