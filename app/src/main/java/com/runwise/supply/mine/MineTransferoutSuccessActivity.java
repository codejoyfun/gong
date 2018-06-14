package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.TransferListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineTransferoutSuccessActivity extends BaseActivity {

    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.receiveTv)
    TextView mReceiveTv;
    @BindView(R.id.orderBtn)
    TextView mOrderBtn;
    @BindView(R.id.tv_home_page)
    TextView mTvHomePage;

    public static final String INTENT_KEY_PICKING_ID = "intent_key_picking_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_transferout_success);
        ButterKnife.bind(this);

        setTitleLeftIcon(true,R.drawable.nav_closed);
        setTitleText(true,"出库成功");

    }

    @OnClick(R.id.orderBtn)
    public void onMOrderBtnClicked() {
        if(ActivityManager.getInstance().has(MineTransferoutActivity.class)){
            Intent intent2 = new Intent(getActivityContext(), MineTransferoutActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent2);
            return;
        }
        Intent intent = new Intent(getActivityContext(),MineTransferoutActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.tv_home_page)
    public void onMTvHomePageClicked() {
        ActivityManager.getInstance().returnHomePage(MainActivity.class);
    }

    @OnClick(R.id.title_iv_left)
    public void onBack() {
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(ActivityManager.getInstance().has(MineTransferoutActivity.class)){
            Intent intent2 = new Intent(getActivityContext(), MineTransferoutActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent2);
            return;
        }
        ActivityManager.getInstance().returnHomePage(MainActivity.class);
    }
}
