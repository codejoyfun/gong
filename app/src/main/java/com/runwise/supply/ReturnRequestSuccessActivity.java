package com.runwise.supply;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.firstpage.ReturnDetailActivity;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 申请退货中间成功页（注意不是退货成功页）
 *
 * Created by Dong on 2017/11/3.
 */

public class ReturnRequestSuccessActivity extends BaseActivity {
    private static final String INTENT_KEY_RETURN_ORDER = "return_order";
    private FinishReturnResponse.ReturnOrder mReturnOrder;
    @ViewInject(R.id.tv_return_desc)
    private TextView mTvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_return_request_success);
        setTitleLeftIcon(true,R.drawable.nav_closed);
        setTitleText(true,"提交成功");
        mReturnOrder = (FinishReturnResponse.ReturnOrder)getIntent().getSerializableExtra(INTENT_KEY_RETURN_ORDER);
        mTvDesc.setText(getString(R.string.return_order_request_success,mReturnOrder.getName()));
    }

    @OnClick(R.id.tv_order)
    public void viewOrderDetail(View v){
        Intent intent = new Intent(mContext, ReturnDetailActivity.class);
        intent.putExtra("rid", mReturnOrder.getReturnOrderID()+"");
        startActivity(intent);
    }

    @OnClick({R.id.tv_back,R.id.title_iv_left})
    public void backToHome(View v){
        ActivityManager.getInstance().finishAll();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public static void start(Activity activity, FinishReturnResponse.ReturnOrder returnOrder){
        Intent intent = new Intent(activity, ReturnRequestSuccessActivity.class);
        intent.putExtra(INTENT_KEY_RETURN_ORDER, returnOrder);
        activity.startActivity(intent);
    }

}
