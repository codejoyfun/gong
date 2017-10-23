package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * Created by libin on 2017/7/19.
 */

public class ReceiveSuccessActivity extends BaseActivity {

    private OrderResponse.ListBean bean;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.receive_success_layout);
        setTitleText(true,"收货成功");
        setTitleLeftIcon(true,R.drawable.nav_closed);
        bundle = getIntent().getExtras();
        bean = bundle.getParcelable("order");
        if (bean.getOrderSettleName().contains("单次结算")){
            findViewById(R.id.uploadBtn).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.uploadBtn).setVisibility(View.GONE);
        }
        if (bean.getOrderSettleName().contains("先付款后收货")&&bean.getOrderSettleName().contains("单次结算")){
            findViewById(R.id.uploadBtn).setVisibility(View.GONE);
        }

    }
    @OnClick({R.id.title_iv_left,R.id.orderBtn,R.id.uploadBtn})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                break;
            case R.id.orderBtn:
                Intent intent = new Intent(mContext,EvaluateActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.uploadBtn:
                Intent uIntent = new Intent(mContext,UploadPayedPicActivity.class);
                uIntent.putExtra("orderid",bean.getOrderID());
                uIntent.putExtra("ordername",bean.getName());
                uIntent.putExtra("hasattachment",false);
                startActivity(uIntent);
                break;
        }
        finish();
    }
}
