package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.message.ActionSendActivity;
import com.runwise.supply.mine.entity.SystemInfoData;
import com.runwise.supply.mine.entity.SystemInfoResult;
import com.runwise.supply.mine.entity.UpdateUserInfo;
import com.runwise.supply.tools.UserUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by myChaoFile on 16/10/13.
 */

public class MineFragment extends NetWorkFragment {
    private final int REQUEST_SYSTEM = 1;
    //电话
    @ViewInject(R.id.minePhone)
    private TextView minePhone;
    //头像
    @ViewInject(R.id.mineHead)
    private SimpleDraweeView mineHead;
    private UserInfo userInfo;

    boolean isLogin;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin = SPUtils.isLogin(mContext);
        if (isLogin) {
            setLoginStatus();
        }
        else {
            setLogoutStatus();
        }
    }

    private void setLoginStatus() {
        isLogin = true;
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        if (userInfo != null) {
//            FrecoFactory.getInstance(mContext).disPlay(mineHead, userInfo.getAvatar());
//            minePhone.setText(userInfo.getPhone());
        }
        MainActivity mainActivity = (MainActivity)mContext;
    }

    private void setLogoutStatus() {
        isLogin = false;
        FrecoFactory.getInstance(mContext).disPlay(mineHead, "");
        minePhone.setText("点击登录");
        minePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SPUtils.isLogin(mContext)) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_SYSTEM:
                SystemInfoResult infoResult = (SystemInfoResult)result;
                SystemInfoData info = infoResult.getData();
                final String number = info.getEntity().getCompany_phone();
                dialog.setModel(CustomDialog.BOTH);
                dialog.setTitle("联系客服");
                dialog.setMessageGravity();
                dialog.setMessage(number);
                dialog.setLeftBtnListener("取消",null);
                dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        CommonUtils.callNumber(mContext,number);
                    }
                });
                dialog.show();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    @OnClick({R.id.mineHead,R.id.itemLayout_1,R.id.itemLayout_2, R.id.itemLayout_3,R.id.itemLayout_4,
            R.id.itemLayout_6})
    public void doClickHandler(View view) {
        Intent intent;
        switch (view.getId()) {
            //头像
            case R.id.mineHead:
                if(!SPUtils.isLogin(mContext)) {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_1:
                intent = new Intent(mContext, IActionListActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_2:
                intent = new Intent(mContext, OrderListActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_3:
//                sendConnection("welcome/system.json",null,REQUEST_SYSTEM,true, SystemInfoResult.class);
                final String number = "010-53675001";
                dialog.setModel(CustomDialog.BOTH);
                dialog.setTitle("联系客服");
                dialog.setMessageGravity();
                dialog.setMessage(number);
                dialog.setLeftBtnListener("取消",null);
                dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        CommonUtils.callNumber(mContext,number);
                    }
                });
                dialog.show();
                break;
            case R.id.itemLayout_4:
                intent = new Intent(mContext, ActionSendActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
//            case R.id.itemLayout_5:
//                ShareUtil.showShare(mContext,"分享给朋友","分享内容","http://ofwp5weyr.bkt.clouddn.com/71aafd87-5c6f-4629-9dce-9347e14a19a3.png","http://www.baidu.com");
//                break;
            case R.id.itemLayout_6:
                intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        setLoginStatus();
    }

    @Override
    public void onUserLoginout() {
        setLogoutStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUserInfo(UpdateUserInfo userLoginEvent) {
        setLoginStatus();
    }
}
