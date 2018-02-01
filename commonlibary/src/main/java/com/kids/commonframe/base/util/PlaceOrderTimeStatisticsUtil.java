package com.kids.commonframe.base.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import static com.kids.commonframe.base.util.UmengUtil.COMPANY_TYPE;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ORDER_TIME;
import static com.kids.commonframe.base.util.UmengUtil.ORDER_TYPE;
import static com.kids.commonframe.base.util.UmengUtil.TYPE_SELF_ORDER;

/**
 * Created by mike on 2018/1/31.
 * 统计下单时间（分五类:1 常购清单 2 自助下单 3 智能下单 4 再来一单 5 修改订单）
 */

public class PlaceOrderTimeStatisticsUtil {
    private static long mStartTime;
    private static long mElapsedTime;
    private static int mStatus = 1 << 2;
    public static final int STATUS_START = 1 << 0;
    public static final int STATUS_PAUSE = 1 << 1;
    public static final int STATUS_NONE = 1 << 2;

    public static void onResume() {
        start();
    }

    public static void onPause(Context context) {
        pause(context);
    }

    public static void upload(Context context) {
        //统计秒
        int duration = (int) (end(context) / 1000);
        Map<String, String> map_value = new HashMap<String, String>();
        map_value.put(ORDER_TYPE, TYPE_SELF_ORDER);
        map_value.put(COMPANY_TYPE, (String) SPUtils.get(context,SPUtils.FILE_KEY_COMPANY_NAME,"尚没有公司名"));
        MobclickAgent.onEventValue(context, EVENT_ID_ORDER_TIME, map_value, duration);
    }


    public static void start() {
        if (STATUS_NONE == mStatus||STATUS_PAUSE == mStatus) {
            mStatus = STATUS_START;
            mStartTime = System.currentTimeMillis();
        } else {
            try {
                throw new Exception("统计时间状态不是none或pause");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void pause(Context context) {
        if (mStatus == STATUS_START) {
            mElapsedTime = (long) SPUtils.get(context,SPUtils.FILE_KEY_SELF_ORDER_ELAPSED_TIME,0L);
            mElapsedTime += System.currentTimeMillis() - mStartTime;
            SPUtils.put(context,SPUtils.FILE_KEY_SELF_ORDER_ELAPSED_TIME,mElapsedTime);
            mStatus = STATUS_PAUSE;
        } else {
            try {
                throw new Exception("统计时间状态不是start");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long end(Context context) {
        if (mStatus == STATUS_START) {
            mElapsedTime = (long) SPUtils.get(context,SPUtils.FILE_KEY_SELF_ORDER_ELAPSED_TIME,0L);
            mElapsedTime = System.currentTimeMillis() - mStartTime + mElapsedTime;
            long mTempElapsedTime = mElapsedTime;
            //重置状态
            mElapsedTime = 0;
            mStartTime = 0;
            mStatus = STATUS_NONE;
            SPUtils.put(context,SPUtils.FILE_KEY_SELF_ORDER_ELAPSED_TIME,mElapsedTime);
            return mTempElapsedTime;
        } else {
            try {
                throw new Exception("统计时间状态不是start");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static void clear(){
        mElapsedTime = 0;
        mStartTime = 0;
        mStatus = STATUS_NONE;
    }


}
