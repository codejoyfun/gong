package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

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
}
