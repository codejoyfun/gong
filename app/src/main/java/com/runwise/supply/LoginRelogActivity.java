package com.runwise.supply;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.ReLoginData;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.GetCodeRequest;
import com.runwise.supply.entity.ReLoginRequest;
import com.runwise.supply.entity.RegistrationRequest;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import cn.jpush.android.api.JPushInterface;


/**
 * 登陆冲突
 */
public class LoginRelogActivity extends NetWorkActivity {
    private static final int GET_CODE = 1;
    private static final int FIND_PASSWORD = 2;
    private static final int REQUEST_USERINFO = 3;
    private static final int REQUEST_REGISTRATION = 4;
    @ViewInject(R.id.teacher_reg_phone)
    private TextView mPhonenNmber;
    @ViewInject(R.id.teacher_reg_getcode)
    private TextView mGetCode;
    @ViewInject(R.id.teacher_reg_code)
    private EditText mCode;
    @ViewInject(R.id.teacher_reg_next)
    private TextView finish;

    private boolean holdCode;
    private String mobel;
    private String username;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_login_relog);
        mobel = this.getIntent().getStringExtra("mobel");
        username = this.getIntent().getStringExtra("username");
        pwd = this.getIntent().getStringExtra("pwd");

        mPhonenNmber.setText(CommonUtils.heandlerMobel(mobel));
        this.setTitleText(false, "登陆冲突");
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        setFinishStatus();
//		mPhonenNmber.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				if ( s != null ) {
//					String phoneNum = s.toString();
//					if ( CheckUtil.isMobileNumber(phoneNum) && !holdCode) {
//						mGetCode.setEnabled(true);
//					}
//					else {
//						mGetCode.setEnabled(false);
//					}
//				}
//			}
//		});

//		mPhonenNmber.addTextChangedListener(new TextWatchListener());
        mCode.addTextChangedListener(new TextWatchListener());
        mGetCode.performClick();
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            //获取验证码
            case GET_CODE:
                ToastUtil.show(mContext, "验证码已发送");
                mGetCode.setEnabled(false);
                new WaitTimer(60).start();
                holdCode = true;
                break;
            case FIND_PASSWORD:
                ReLoginData reLoginData = (ReLoginData) result.getResult().getData();
                if ("false".equals(reLoginData.getCheck())) {
                    ToastUtil.show(mContext, "验证码错误");
                } else {
                    requestRegistration();
                }
                break;
            case REQUEST_REGISTRATION:
                Object paramBean = null;
                this.sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USERINFO, true, UserInfo.class);
                break;
            case REQUEST_USERINFO:
                UserInfo userInfo = (UserInfo) result.getResult().getData();
                GlobalApplication.getInstance().saveUserInfo(userInfo);
                JPushInterface.setAliasAndTags(getApplicationContext(), CommonUtils.getDeviceId(this), null, null);
                SPUtils.setLogin(mContext, true);
                ToastUtil.show(mContext, "登录成功");
                EventBus.getDefault().post(new UserLoginEvent());
                this.finish();
                break;
        }
    }

    private void requestRegistration(){
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setIos_registration_id(JPushInterface.getRegistrationID(this));
        this.sendConnection("/gongfu/registration_id", registrationRequest, REQUEST_REGISTRATION, true, null);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        dialog.setModel(CustomDialog.LEFT);
        dialog.setMessage(errMsg);
        dialog.setLeftBtnListener("取消", null);
        dialog.show();
    }

    @OnClick(R.id.teacher_reg_next)
    public void doNext(View view) {
        String code = mCode.getText().toString().trim();
//		String phonNumber = mPhonenNmber.getText().toString().trim();
//		if (TextUtils.isEmpty(phonNumber)) {
//			dialog.setModel(CustomDialog.LEFT);
//			dialog.setLeftBtnListener("知道啦", null);
//			dialog.setMessage("请输入手机号码");
//			dialog.show();
//			return;
//		}
//		else if (!CheckUtil.isMobileNumber(phonNumber)) {
//			dialog.setModel(CustomDialog.LEFT);
//			dialog.setLeftBtnListener("知道啦", null);
//			dialog.setMessage("请输入手机号码格式不正确哦");
//			dialog.show();
//			return;
//		}
        if (TextUtils.isEmpty(code)) {
            dialog.setModel(CustomDialog.LEFT);
            dialog.setMessage("请输入验证码");
            dialog.setLeftBtnListener("知道啦", null);
            dialog.show();
            return;
        }
        ReLoginRequest paramBean = new ReLoginRequest(username, mobel, code, pwd);
        this.sendConnection("/gongfu/v2/check_captcha", paramBean, FIND_PASSWORD, true, ReLoginData.class);
    }

    @OnClick(R.id.teacher_reg_getcode)
    public void doGetCode(View view) {
//		String phonNumber = mPhonenNmber.getText().toString().trim();
//		if (TextUtils.isEmpty(phonNumber)) {
//			dialog.setModel(CustomDialog.LEFT);
//			dialog.setLeftBtnListener("知道啦", null);
//			dialog.setMessage("请输入手机号码");
//			dialog.show();
//			return;
//		}
//		else if (!CheckUtil.isMobileNumber(phonNumber)) {
//			dialog.setModel(CustomDialog.LEFT);
//			dialog.setLeftBtnListener("知道啦", null);
//			dialog.setMessage("请输入手机号码格式不正确哦");
//			dialog.show();
//			return;
//		}
        GetCodeRequest paramBean = new GetCodeRequest(mobel);
        this.sendConnection("/gongfu/get_captcha", paramBean, GET_CODE, true, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    private class WaitTimer extends CountDownTimer implements Serializable {
        private static final long serialVersionUID = 1L;
        private int wait;

        public WaitTimer(int second) {
            super(second * 1000l, 1000l);
            wait = second;
            mGetCode.setTextColor(Color.parseColor("#cbcbcb"));
        }

        @Override
        public void onFinish() {
            mGetCode.setEnabled(true);
            mGetCode.setTextColor(Color.parseColor("#6ec42d"));
            mGetCode.setText("获取验证码");
            holdCode = false;
            this.cancel();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            wait--;
            mGetCode.setText(wait + "秒后重试");
        }
    }

    private class TextWatchListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setFinishStatus();
        }
    }

    private void setFinishStatus() {
        String phoneNumber = mPhonenNmber.getText().toString().trim();
        String code = mCode.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code)) {
            finish.setEnabled(true);
        } else {
            finish.setEnabled(false);
        }
    }

}
