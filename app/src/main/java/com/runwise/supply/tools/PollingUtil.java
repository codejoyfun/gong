package com.runwise.supply.tools;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mike on 2017/10/9.
 */

public class PollingUtil {
    /**
     * 默认两秒间隔
     */
    public static long defaultInterval = 5 * 1000;

    private static class Polling {
        static PollingUtil mPollingUtil = new PollingUtil();
    }

    private PollingUtil() {
        mTimer = new Timer();
    }


    public static PollingUtil getInstance() {
        return Polling.mPollingUtil;
    }
    Timer mTimer;
    public void requestOrder(final NetWorkHelper<BaseEntity> netWorkHelper, final int requestCode) {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                requestReturnList(netWorkHelper,requestCode);
            }
        }, 0, defaultInterval);
    }

    public void stopRequestOrder(){
        if (mTimer != null){
            mTimer.cancel();
        }
    }
    //一次性加载全部，无分页,先加载退货单，然后跟着正常订单
    private void requestReturnList(NetWorkHelper<BaseEntity> netWorkHelper,int requestCode) {
        Object request = null;
        netWorkHelper.sendConnection("/gongfu/v2/return_order/undone/", request, requestCode, false, ReturnOrderBean.class);
    }



}
