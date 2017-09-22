package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseManager;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.orderBtn)
    public void onViewClicked() {
        BaseManager.getInstance().finishAll();
        startActivity(new Intent(getActivityContext(), MainActivity.class));
    }
}
