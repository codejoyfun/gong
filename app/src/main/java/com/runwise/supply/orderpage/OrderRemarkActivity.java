package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderRemarkActivity extends BaseActivity {

    @BindView(R.id.et_remark)
    EditText mEtRemark;
    @BindView(R.id.tv_remark_hint)
    TextView mTvRemarkHint;

    public static final String INTENT_KEY_REMARK = "intent_key_remark";

    public static Intent getStartIntent(Context context, String remark) {
        Intent intent = new Intent(context, OrderRemarkActivity.class);
        intent.putExtra(INTENT_KEY_REMARK, remark);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_remark);
        ButterKnife.bind(this);
        setTitleText(true,"订单备注");
        showBackBtn();
        setTitleRightText(true, "完成");
        mEtRemark.setText(getIntent().getStringExtra(INTENT_KEY_REMARK));
        mTvRemarkHint.setText(getIntent().getStringExtra(INTENT_KEY_REMARK).length() + "/50字");

        mEtRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvRemarkHint.setText(s.toString().length() + "/50字");
            }
        });
    }

    @OnClick(R.id.title_tv_rigth)
    public void onViewClicked() {
        if (TextUtils.isEmpty(mEtRemark.getText().toString())) {
            toast("备注不能为空!");
            return;
        }
//        返回订单页
        Intent intent = new Intent();
        intent.putExtra(INTENT_KEY_REMARK, mEtRemark.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
