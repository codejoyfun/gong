package com.runwise.supply.tools;

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
    public static void reportError(String errorMsg){
        MobclickAgent.reportError(GlobalApplication.getInstance(),GlobalApplication.getInstance().getUserName()+"--"+errorMsg);
    }
}
