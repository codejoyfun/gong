package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.LoginRequest;
import com.runwise.supply.entity.LoginRespone;
import com.runwise.supply.entity.LoginResult;
import com.runwise.supply.entity.UserInfoData;
import com.runwise.supply.entity.UserInfoResult;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;


public class LoginActivity extends NetWorkActivity {
	private static final int LOGIN = 1;
	private static final int USER_INFO = 2;
	public static Intent targerIntent;
	/**
	 * 登录按钮
	 */
	@ViewInject(R.id.login_btn)
	private Button login_btn;

	private boolean holdCode;
	@ViewInject(R.id.teacher_reg_phone)
	private EditText mPhone;
	@ViewInject(R.id.teacher_reg_password)
	private EditText mPassword;
	@ViewInject(R.id.teacher_reg_password_see)
	private CheckBox mPasswordSee;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		setStatusBarEnabled();
		StatusBarUtil.StatusBarLightMode(this);
		this.setContentView(R.layout.activity_login);
		this.setTitleLeftIcon(true, R.drawable.marking);
		this.setTitleText(true, "登录");
//		String phoneNumber = SharePrenceUtil.getTempPhoneNumber(this);
//		if ( !TextUtils.isEmpty(phoneNumber) ) {
//			ed_userName.setText(phoneNumber);
//			ed_pass.requestFocus();
//		}
		mPasswordSee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ) {
					mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}
				else {
					mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
		login_btn.setEnabled(false);
		mPhone.addTextChangedListener(new TextWatchListener());
		mPassword.addTextChangedListener(new TextWatchListener());
	}


	private boolean isEmpty() {
		boolean empty = false;
		if (CommonUtils.isEmpty(mPhone.getText().toString())) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			empty = true;
		}
		else if (CommonUtils.isEmpty(mPassword.getText().toString())) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			empty = true;
		}
		return empty;
	}


	@OnClick(R.id.login_btn)
	public void onLogin(View v) {
		if (isEmpty()) {
			return;
		}
//		if (!ParamsUtil.isPhoneNumberValid(userName)) {
//			ToastUtil.show(mContext, "用户名必须是手机号码或邮箱");
//
		String phone = mPhone.getText().toString().trim();
		String passowrd= mPassword.getText().toString();

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setPhone(phone);
		loginRequest.setPassword(passowrd);
		sendConnection("members/login.json",loginRequest,LOGIN,true,LoginRespone.class);
	}
	private void loadUserInfo() {
		sendConnection("members/profile.json",USER_INFO,true,UserInfoResult.class);
	}
	/*
	 * 注册
	 */
	@OnClick(R.id.login_register)
	public void onReg(View v) {
		Intent intent = new Intent(this, RegisterActivity.class);
		this.startActivity(intent);
	}
	/*
	 * 忘记密码
	 */
	@OnClick(R.id.login_find)
	public void onFind(View v) {
		this.startActivity(new Intent(this, FindPasswordActivity.class));
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
			case LOGIN:
				LoginRespone loginRespone = (LoginRespone) result;
				LoginResult loginResult = loginRespone.getData();
				SPUtils.put(this,"sign",loginResult.getUser_token());
				loadUserInfo();
				break;
			case USER_INFO:
				UserInfoResult userInfoResult = (UserInfoResult) result;
				UserInfoData userInfoData = userInfoResult.getData();

				UserLoginEvent loginEvent = new UserLoginEvent();
				loginEvent.setIntent(targerIntent);
				EventBus.getDefault().post(loginEvent);
				GlobalApplication.getInstance().saveUserInfo(userInfoData.getEntity());

				ToastUtil.show(mContext,"登录成功");
				if (targerIntent != null) {
					startActivity(targerIntent);
				}
				this.finish();
				break;
		}
	}
	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext, errMsg);
	}

	@OnClick(R.id.left_layout)
	public void doBack(View v) {
		this.finish();
	}

	private class TextWatchListener implements TextWatcher{
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

	private void setFinishStatus () {
		String phoneNumber = mPhone.getText().toString().trim();
		String code = mPassword.getText().toString().trim();
		if ( !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code)) {
			login_btn.setEnabled(true);
		}
		else {
			login_btn.setEnabled(false);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		targerIntent = null;
	}

	@Override
	public void onUserLogin(UserLoginEvent userLoginEvent) {
		super.onUserLogin(userLoginEvent);
		this.finish();
	}
}
