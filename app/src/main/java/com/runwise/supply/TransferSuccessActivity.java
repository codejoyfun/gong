package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.ActivityManager;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.tools.StatusBarUtil;

import static com.runwise.supply.TransferDetailActivity.EXTRA_TRANSFER_ID;

/**
 * 入库成功页
 *
 * Created by Dong
 */

public class TransferSuccessActivity extends BaseActivity {

    public static final String INTENT_KEY_TRANSFER_ID = "transfer_id";
    private String pickingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_in_success);
        setTitleText(true,"入库成功");
        setTitleLeftIcon(true,R.drawable.nav_closed);
        pickingID = getIntent().getStringExtra(INTENT_KEY_TRANSFER_ID);
    }
    @OnClick({R.id.title_iv_left,R.id.tv_to_transfer_list,R.id.tv_to_main})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                break;
            case R.id.tv_to_transfer_list:
                Intent intent = new Intent(this,TransferListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Intent intent2 = new Intent(this,TransferDetailActivity.class);
                intent2.putExtra(EXTRA_TRANSFER_ID,pickingID);
                startActivity(intent2);
                break;
            case R.id.tv_to_main://回到首页
                ActivityManager.getInstance().finishAll();
                startActivity(new Intent(getActivityContext(), MainActivity.class));
                break;
        }
        finish();
    }
}
