package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.ActivityManager;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EvaluateSuccessActivity extends BaseActivity {

    int orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_success);
        ButterKnife.bind(this);
        setTitleText(true,"评价成功");
        orderid = getIntent().getIntExtra("orderid",0);
    }

    @OnClick({R.id.title_iv_left, R.id.orderBtn, R.id.uploadBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.orderBtn:
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("orderid",orderid);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.uploadBtn:
                ActivityManager.getInstance().finishAll();
                startActivity(new Intent(getActivityContext(), MainActivity.class));
                break;
        }
    }
}
