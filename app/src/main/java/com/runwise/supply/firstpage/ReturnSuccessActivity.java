package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.message.OrderMsgDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.NumberUtil;

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

    public static final int REQUEST_CODE_UPLOAD = 1 << 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_success);
        ButterKnife.bind(this);
        setTitleText(true, "退货成功");
        showBackBtn();
        finishReturnResponse = (FinishReturnResponse) getIntent().getSerializableExtra(INTENT_KEY_RESULTBEAN);
        String text;
        if (SampleApplicationLike.getInstance().getCanSeePrice()) {
            text = "退货数量: " +
                    NumberUtil.getIOrD(finishReturnResponse.getReturnOrder().getAmount()) +
                    "件\n" +
                    "退货金额: " +
                    finishReturnResponse.getReturnOrder().getAmountTotal() +
                    "\n退货成功时间: " +
                    finishReturnResponse.getReturnOrder().getDoneDate();
        } else {
            text = "退货数量: " +
                    NumberUtil.getIOrD(finishReturnResponse.getReturnOrder().getAmount()) +
                    "件\n" +
                    "退货成功时间: " +
                    finishReturnResponse.getReturnOrder().getDoneDate();
        }


        tvReturnCount.setText(text);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPLOAD) {
                finish();
            }
        }
    }

    @OnClick({R.id.orderBtn, R.id.uploadBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.orderBtn:
                Intent intent = new Intent(mContext, OrderMsgDetailActivity.class);
                intent.putExtra("title", finishReturnResponse.getReturnOrder().getName());
                intent.putExtra("normalOrder", false);
                intent.putExtra("orderId", finishReturnResponse.getReturnOrder().getReturnOrderID() + "");
                startActivity(intent);
                break;
            case R.id.uploadBtn:
//                Intent uIntent = new Intent(mContext, UploadReturnPicActivity.class);
//                uIntent.putExtra("orderid", finishReturnResponse.getReturnOrder().getReturnOrderID());
//                uIntent.putExtra("ordername", finishReturnResponse.getReturnOrder().getName());
//                startActivityForResult(uIntent, REQUEST_CODE_UPLOAD);
                ActivityManager.getInstance().finishAll();
                Intent intent1 = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra(INTENT_KEY_TAB,0);
                startActivity(intent1);
                break;
        }
    }
}
