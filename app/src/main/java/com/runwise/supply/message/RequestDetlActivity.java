package com.runwise.supply.message;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

public class RequestDetlActivity extends BaseActivity {
    @ViewInject(R.id.cell_phone)
    private TextView cell_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_deal);
        setTitleText(true,"申请被拒");
        setTitleLeftIcon(true,R.drawable.back_btn);
    }

    @OnClick(R.id.stepPayFinish)
    public void doFinish(View view) {
        this.finish();
    }
    @OnClick(R.id.cell_phone)
    public void doCellPhone(View view) {
        CommonUtils.callNumber(this,cell_phone.getText().toString());
    }

}
