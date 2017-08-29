package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReturnSuccessActivity extends BaseActivity {

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

    public static final String INTENT_KEY_RESULTBEAN = "intent_key_resultbean";
    FinishReturnResponse finishReturnResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_success);
        ButterKnife.bind(this);

        finishReturnResponse = (FinishReturnResponse) getIntent().getSerializableExtra(INTENT_KEY_RESULTBEAN);
        tvReturnCount.setText("退货数量: " +
                finishReturnResponse.getReturnOrder().getLines().size() +
                "件\\n" +
                "退货金额: " +
                finishReturnResponse.getReturnOrder().getAmountTotal() +
                "\\n退货成功时间: " +
                "2017-0828 17:34");

    }

    @OnClick({R.id.orderBtn, R.id.uploadBtn})
        public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.orderBtn:
                Bundle bundle = new Bundle();
                bundle.putInt("orderid", finishReturnResponse.getReturnOrder().getOrderID());
                Intent intent = new Intent(ReturnSuccessActivity.this,OrderDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.uploadBtn:

                break;
        }
    }
}
