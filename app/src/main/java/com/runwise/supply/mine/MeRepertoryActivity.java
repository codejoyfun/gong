package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseFragment;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

/**
 * 库存
 */
public class MeRepertoryActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repertory);
        this.setTitleText(true, "当前库存" );
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        FragmentManager manager = this.getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contextLayout,new RepertoryFragment()).commitAllowingStateLoss();
    }
    @OnClick(R.id.left_layout)
    public void rightClick(View view){
        finish();
    }
}

