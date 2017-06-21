package com.runwise.supply.mine;

import android.os.Bundle;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.MsgDetailData;
import com.runwise.supply.mine.entity.MsgDetailEntity;
import com.runwise.supply.mine.entity.MsgDetailRequest;
import com.runwise.supply.mine.entity.MsgDetailResult;
import com.runwise.supply.tools.StatusBarUtil;

public class MsgDetailActivity extends NetWorkActivity {
    private static final int REQUEST_MAIN = 1;
    @ViewInject(R.id.msgTitle)
    private TextView msgTitle;
    @ViewInject(R.id.msgTime)
    private TextView msgTime;
    @ViewInject(R.id.msgCotext)
    private TextView msgCotext;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_msg_detail);
        loadingLayout.setStatusLoading();
        MsgDetailRequest detailRequest = new MsgDetailRequest(this.getIntent().getStringExtra("msgId"));
        sendConnection("message/detail.json",detailRequest,REQUEST_MAIN,false,MsgDetailResult.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                MsgDetailResult msgDetailResult = (MsgDetailResult)result;
                MsgDetailData msgDetailData = msgDetailResult.getData();
                MsgDetailEntity detailEntity = msgDetailData.getEntity();
                msgTitle.setText(detailEntity.getTitle());
                msgTime.setText(detailEntity.getCreated_at());
                msgCotext.setText(detailEntity.getContent());
                loadingLayout.onSuccess(1,"没有数据");
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(this,errMsg);
    }
}
