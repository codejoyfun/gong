package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.umeng.analytics.MobclickAgent;

/**
 * “又”一个下单成功页
 *
 * Created by Dong on 2017/11/24.
 */

public class OrderSubmitSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        setContentView(R.layout.activity_order_submit_success_v2);
        setTitleLeftIcon(true,R.drawable.nav_closed);
        setTitleText(true,"提交成功");
    }

    @OnClick({R.id.title_iv_left,R.id.btn_gohome})
    public void onBtnClick(View v){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("提交订单成功页");
        MobclickAgent.onResume(this);          //统计时长
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("提交订单成功页");
        MobclickAgent.onPause(this);          //统计时长
    }
}
