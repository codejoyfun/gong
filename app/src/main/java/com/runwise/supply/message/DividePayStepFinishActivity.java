package com.runwise.supply.message;

import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;

import org.greenrobot.eventbus.EventBus;

public class DividePayStepFinishActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devide_pay_step_finish);
        setTitleText(true,"申请");
    }

    @OnClick(R.id.stepPayFinish)
    public void doFinish(View view) {
        this.finish();
        finishHandler();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishHandler();
    }

    private void finishHandler() {
        EventBus.getDefault().post(new FinishActEvent());
    }
}
