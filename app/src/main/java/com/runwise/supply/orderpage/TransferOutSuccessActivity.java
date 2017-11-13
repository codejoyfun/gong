package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.TransferDetailActivity;
import com.runwise.supply.TransferListActivity;
import com.runwise.supply.entity.TransferEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.runwise.supply.TransferDetailActivity.EXTRA_TRANSFER_ENTITY;
import static com.runwise.supply.TransferDetailActivity.EXTRA_TRANSFER_ID;

public class TransferOutSuccessActivity extends NetWorkActivity {

    @BindView(R.id.left_layout)
    LinearLayout mLeftLayout;
    @BindView(R.id.receiveTv)
    TextView mReceiveTv;
    @BindView(R.id.orderBtn)
    TextView mOrderBtn;
    @BindView(R.id.tv_home_page)
    TextView mTvHomePage;

    private TransferEntity mTransferEntity;

    public static Intent getStartIntent(Context context,TransferEntity transferEntity){
        Intent intent =  new Intent(context,TransferOutSuccessActivity.class);
        intent.putExtra(EXTRA_TRANSFER_ENTITY,transferEntity);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_out_success);
        setTitleText(true,"出库成功");
        setTitleLeftIcon(true,R.drawable.nav_closed);
        ButterKnife.bind(this);
        mTransferEntity = getIntent().getParcelableExtra(EXTRA_TRANSFER_ENTITY);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(ActivityManager.getInstance().has(TransferListActivity.class)){
            Intent intent2 = new Intent(getActivityContext(), TransferListActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent2);
            return;
        }
        ActivityManager.getInstance().returnHomePage(MainActivity.class);
    }

    @OnClick({R.id.title_iv_left,R.id.orderBtn, R.id.tv_home_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.orderBtn:
                Intent intent = new Intent(getActivityContext(), TransferDetailActivity.class);
                intent.putExtra(EXTRA_TRANSFER_ID,mTransferEntity.getPickingID());
                startActivity(intent);
                finish();
                break;
            case R.id.tv_home_page:
                if(ActivityManager.getInstance().has(TransferListActivity.class)){
                    Intent intent2 = new Intent(getActivityContext(), TransferListActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    return;
                }
                ActivityManager.getInstance().returnHomePage(MainActivity.class);
                break;
            case R.id.title_iv_left:
                finish();
                break;
        }
    }
}
