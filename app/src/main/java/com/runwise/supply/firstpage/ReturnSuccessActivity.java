package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReturnSuccessActivity extends BaseActivity {

    @BindView(R.id.title_iv_left)
    ImageView titleIvLeft;
    @BindView(R.id.title_tv_left)
    TextView titleTvLeft;
    @BindView(R.id.left_layout)
    LinearLayout leftLayout;
    @BindView(R.id.title_iv_rigth2)
    ImageView titleIvRigth2;
    @BindView(R.id.title_iv_rigth)
    ImageView titleIvRigth;
    @BindView(R.id.title_tv_rigth)
    TextView titleTvRigth;
    @BindView(R.id.right_layout)
    LinearLayout rightLayout;
    @BindView(R.id.title_tv_title)
    TextView titleTvTitle;
    @BindView(R.id.title_iv_title)
    ImageView titleIvTitle;
    @BindView(R.id.mid_layout)
    LinearLayout midLayout;
    @BindView(R.id.title_bg)
    FrameLayout titleBg;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.receiveTv)
    TextView receiveTv;
    @BindView(R.id.tv_return_count)
    TextView tvReturnCount;
    @BindView(R.id.orderBtn)
    TextView orderBtn;
    @BindView(R.id.uploadBtn)
    TextView uploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_success);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.orderBtn, R.id.uploadBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.orderBtn:

                break;
            case R.id.uploadBtn:
                break;
        }
    }
}
