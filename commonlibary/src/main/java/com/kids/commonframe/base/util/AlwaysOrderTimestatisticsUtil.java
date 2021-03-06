package com.kids.commonframe.base.util;

import android.content.Context;

import com.kids.commonframe.base.UserInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import static com.kids.commonframe.base.util.UmengUtil.COMPANY_TYPE;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ALWAYS_ORDER_TIME;
import static com.kids.commonframe.base.util.UmengUtil.ORDER_PRODUCTS_TYPE;
import static com.kids.commonframe.base.util.UmengUtil.ORDER_TIME_TYPE;

/**
 * Created by mike on 2018/2/8.
 */

public class AlwaysOrderTimestatisticsUtil {
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
//上传下单时间, 用户信息，下单内容
    public static void upload(Context context,String content) {
        //统计秒
        int duration = (int) (end(context) / 1000);
        Map<String, String> map_value = new HashMap<String, String>();
        map_value.put(COMPANY_TYPE, (String) SPUtils.get(context,SPUtils.FILE_KEY_COMPANY_NAME,"尚没有公司名"));
        map_value.put(ORDER_TIME_TYPE, String.valueOf(duration));
        Object o = SPUtils.readObject(context, SPUtils.FILE_KEY_USER_INFO);
        String menDianName = "";
        String userName = "";
        String mobile = "";
        if (o != null) {
            UserInfo userInfo = (UserInfo) o;
            menDianName = "门店名: " + userInfo.getMendian() + "\n";
            userName = "用户名: " + userInfo.getUsername() + "\n";
            mobile = "手机: " + userInfo.getMobile() + "\n";
            content = menDianName+userName+mobile+ content;
        }
        map_value.put(ORDER_PRODUCTS_TYPE, content);
        MobclickAgent.onEventValue(context, EVENT_ID_ALWAYS_ORDER_TIME, map_value, duration);
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
            mElapsedTime = (long) SPUtils.get(context,SPUtils.FILE_KEY_ALWAYS_ORDER_ELAPSED_TIME,0L);
            mElapsedTime += System.currentTimeMillis() - mStartTime;
            SPUtils.put(context,SPUtils.FILE_KEY_ALWAYS_ORDER_ELAPSED_TIME,mElapsedTime);
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
            mElapsedTime = (long) SPUtils.get(context,SPUtils.FILE_KEY_ALWAYS_ORDER_ELAPSED_TIME,0L);
            mElapsedTime = System.currentTimeMillis() - mStartTime + mElapsedTime;
            long mTempElapsedTime = mElapsedTime;
            //重置状态
            mElapsedTime = 0;
            mStartTime = 0;
            mStatus = STATUS_NONE;
            SPUtils.put(context,SPUtils.FILE_KEY_ALWAYS_ORDER_ELAPSED_TIME,mElapsedTime);
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
