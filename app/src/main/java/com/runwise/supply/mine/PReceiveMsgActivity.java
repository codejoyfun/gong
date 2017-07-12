package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;
import com.runwise.supply.mine.entity.UsMessageRequest;
import com.runwise.supply.mine.entity.UsMessageResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 发表留言
 */
public class PReceiveMsgActivity extends NetWorkActivity {
    private final int REQUEST_MAIN = 1;

    @ViewInject(R.id.ratingbar1)
    private RatingBar ratingbar1;
    @ViewInject(R.id.ratingbar2)
    private RatingBar ratingbar2;
    @ViewInject(R.id.ratingbar3)
    private RatingBar ratingbar3;
    @ViewInject(R.id.ratingbar4)
    private RatingBar ratingbar4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmesg);
        this.setTitleText(true,"反馈意见");
        this.setTitleLeftIcon(true,R.drawable.nav_closed);
        this.setTitleRigthIcon(true,R.drawable.nav_next);
    }
    @OnClick(R.id.right_layout)
    public void doNext(View view) {
        if(ratingbar1.getRating() == 0 || ratingbar2.getRating() == 0|| ratingbar3.getRating() == 0|| ratingbar4.getRating() == 0) {
            dialog.setModel(CustomDialog.RIGHT);
            dialog.setRightBtnListener("知道啦",null);
            dialog.setMessage("请评星之后在进行下一步");
            dialog.show();
        }
        else {
            Intent intent = new Intent(this, ReceiveMsgActivity.class);
            startActivity(intent);
        }
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

                break;
            case R.id.left_layout:
                doBackPress();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        doBackPress();
    }

    private void doBackPress() {
        if(ratingbar1.getRating() != 0 || ratingbar2.getRating() != 0|| ratingbar3.getRating() != 0|| ratingbar4.getRating() != 0) {
            dialog.setModel(CustomDialog.BOTH);
            dialog.setLeftBtnListener("退出反馈", new CustomDialog.DialogListener() {
                @Override
                public void doClickButton(Button btn, CustomDialog dialog) {
                    finish();
                }
            });
            dialog.setRightBtnListener("继续反馈",null);
            dialog.setMessage("当前反馈尚未完成");
            dialog.show();
        }
        else {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishActEvent(FinishActEvent event) {
        this.finish();
    }
}
