package com.runwise.supply.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.SignActivity;
import com.runwise.supply.message.entity.ProtocolData;
import com.runwise.supply.message.entity.ProtocolEntity;
import com.runwise.supply.message.entity.ProtocolRequest;
import com.runwise.supply.message.entity.ProtocolResult;

/**
 * 确认协议并签字
 */
public class DevidePayProtocolActivity extends NetWorkActivity {
    private final int REQUEST_DATA = 1;
    private final int RESPONE_DATA = 2;
    @ViewInject(R.id.protocolText)
    private TextView protocolText;
    @ViewInject(R.id.nextBtn)
    private Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devide_pay_protocol);
        ProtocolRequest request = new ProtocolRequest();
        request.setSlug("protocol");
        sendConnection("welcome/article.json",request,REQUEST_DATA,true, ProtocolResult.class);
        this.setTitleText(true,"确认协议");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_DATA:
                ProtocolResult protocolResult = (ProtocolResult)result;
                ProtocolData data =  protocolResult.getData();
                ProtocolEntity protocolEntity =  data.getEntity();
                protocolText.setText(protocolEntity.getContent());
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }

    @OnClick({R.id.nextBtn,R.id.left_layout})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.nextBtn:
                Intent intent = new Intent(this, SignActivity.class);
                startActivityForResult(intent,RESPONE_DATA);
                break;
            case R.id.left_layout:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESPONE_DATA:
                    Intent signData = new Intent();
                    signData.putExtra("signPath",data.getStringExtra("signPath"));
                    setResult(Activity.RESULT_OK,signData);
                    finish();
                    break;
            }
        }
    }
}
