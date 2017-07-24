package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

/**
 * Created by libin on 2017/7/19.
 */

public class ReceiveSuccessActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_success_layout);
        setTitleText(true,"收货成功");
        setTitleLeftIcon(true,R.drawable.nav_closed);
    }
    @OnClick({R.id.title_iv_left,R.id.orderBtn,R.id.uploadBtn})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.orderBtn:
                ToastUtil.show(mContext,"跳转到评价订单");
                break;
            case R.id.uploadBtn:
                ToastUtil.show(mContext,"跳转到上传凭证");
                break;
        }
    }
}
