package com.runwise.supply.repertory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.TransferDetailActivity;
import com.runwise.supply.mine.CheckActivity;
import com.runwise.supply.repertory.entity.UpdateRepertory;
import com.runwise.supply.tools.UserUtils;

import org.greenrobot.eventbus.EventBus;

import io.vov.vitamio.utils.NumberUtil;


public class EditRepertoryFinishActivity extends BaseActivity {

    public static final String INTENT_OLD_STOCK_COUNT = "old_count";
    public static final String INTENT_INVENTORY_COUNT = "new_count";

    @ViewInject(R.id.tv_stock_old_count)
    private TextView mTvOldCount;
    @ViewInject(R.id.tv_inventory_new_count)
    private TextView mTvNewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devide_pay_step_finish);
        setTitleText(true,"盘点成功");
        setTitleLeftIcon(true,R.drawable.nav_closed);
        mTvOldCount.setText("盘点前商品数量："+ NumberUtil.getIOrD(getIntent().getDoubleExtra(INTENT_OLD_STOCK_COUNT,0)));
        mTvNewCount.setText("盘点后商品数量："+NumberUtil.getIOrD(getIntent().getDoubleExtra(INTENT_INVENTORY_COUNT,0)));
    }

    @OnClick({R.id.stepPayFinish,R.id.stepPayFinish1,R.id.left_layout})
    public void doFinish(View view) {
        switch (view.getId()) {
            case R.id.stepPayFinish1:
                Intent intent2 = new Intent(EditRepertoryFinishActivity.this,MainActivity.class);
                intent2.putExtra(MainActivity.INTENT_KEY_TAB,2);
                intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new UpdateRepertory());
    }

    public static void start(Activity activity, double oldCount, double newCount){
        Intent intent = new Intent(activity,EditRepertoryFinishActivity.class);
        intent.putExtra(INTENT_OLD_STOCK_COUNT,oldCount);
        intent.putExtra(INTENT_INVENTORY_COUNT,newCount);
        activity.startActivity(intent);
    }
}
