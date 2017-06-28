package com.runwise.supply.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.UserInfo;
import com.runwise.supply.mine.entity.UsMessageRequest;
import com.runwise.supply.mine.entity.UsMessageResult;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;


/**
 * 发表留言
 */
public class ReceiveMsgActivity extends NetWorkActivity {
    private final int REQUEST_MAIN = 1;

    @ViewInject(R.id.giveMessage)
    private EditText giveMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivemsg);
        this.setTitleText(true,"给我们留言");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                UsMessageResult usResult = (UsMessageResult) result;
                ToastUtil.show(mContext,"留言成功");
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }
    @OnClick({R.id.commitMsg,R.id.left_layout})
    public void doClickHandler(View view) {
        switch (view.getId()) {
            case R.id.commitMsg:
                String message = giveMessage.getText().toString();
                if(TextUtils.isEmpty(message)) {
                    ToastUtil.show(mContext,"请输入留言");
                    return;
                }
                UserInfo userInfo = GlobalApplication.getInstance().loadUserInfo();
                UsMessageRequest messageRequest = new UsMessageRequest();
                messageRequest.setMsg(message);
//                messageRequest.setUid(userInfo.getMember_id());
                sendConnection("/Appapi/Guestbook/addmsg",messageRequest,REQUEST_MAIN,true,UsMessageResult.class);

                break;
            case R.id.left_layout:
                this.finish();
                break;
        }
    }
}
