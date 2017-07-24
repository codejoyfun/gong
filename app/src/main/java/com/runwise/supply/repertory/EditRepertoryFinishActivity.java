package com.runwise.supply.repertory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.mine.CheckActivity;
import com.runwise.supply.tools.UserUtils;


public class EditRepertoryFinishActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devide_pay_step_finish);
        setTitleText(true,"盘点成功");
        setTitleLeftIcon(true,R.drawable.nav_closed);
    }

    @OnClick({R.id.stepPayFinish,R.id.stepPayFinish1,R.id.left_layout})
    public void doFinish(View view) {
        switch (view.getId()) {
            case R.id.stepPayFinish1:
            case R.id.left_layout:
                finish();
                break;
            case  R.id.stepPayFinish:
                Intent intent = new Intent(mContext, CheckActivity.class);
                if (UserUtils.checkLogin(intent,this)) {
                    startActivity(intent);
                }
                break;

        }
    }

}
