package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineTransferoutSuccessActivity extends BaseActivity {

    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.title_iv_left2)
    ImageView mTitleIvLeft2;
    @BindView(R.id.title_tv_left)
    TextView mTitleTvLeft;
    @BindView(R.id.left_layout)
    LinearLayout mLeftLayout;
    @BindView(R.id.title_iv_rigth2)
    ImageView mTitleIvRigth2;
    @BindView(R.id.title_iv_rigth)
    ImageView mTitleIvRigth;
    @BindView(R.id.title_tv_rigth)
    TextView mTitleTvRigth;
    @BindView(R.id.right_layout)
    LinearLayout mRightLayout;
    @BindView(R.id.title_tv_title)
    TextView mTitleTvTitle;
    @BindView(R.id.title_iv_title)
    ImageView mTitleIvTitle;
    @BindView(R.id.mid_layout)
    LinearLayout mMidLayout;
    @BindView(R.id.title_bg)
    FrameLayout mTitleBg;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.receiveTv)
    TextView mReceiveTv;
    @BindView(R.id.orderBtn)
    TextView mOrderBtn;
    @BindView(R.id.tv_home_page)
    TextView mTvHomePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_transferout_success);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.orderBtn)
    public void onMOrderBtnClicked() {
        Intent intent = new Intent(getActivityContext(),MineTransferoutActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.tv_home_page)
    public void onMTvHomePageClicked() {
        Intent intent = new Intent(getActivityContext(),MineTransferoutActivity.class);
        startActivity(intent);
        finish();
    }
}
