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
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.ChangePwdRequest;
import com.runwise.supply.tools.AESCrypt;
import com.runwise.supply.tools.FingerprintHelper;
import com.runwise.supply.tools.SP_CONSTANTS;
import com.runwise.supply.tools.StatusBarUtil;

import java.security.GeneralSecurityException;


public class ChangePwdActivity extends NetWorkActivity {
	private static final int LOGIN = 1;
	public static Intent targerIntent;
	/**
	 * 登录按钮
	 */
	@ViewInject(R.id.login_btn)
	private Button login_btn;

	@ViewInject(R.id.change_password)
	private EditText mSuoucePassword;
	@ViewInject(R.id.teacher_reg_password)
	private EditText mPassword;

	@ViewInject(R.id.change_password_see)
	private CheckBox mPasswordSouceSee;
	@ViewInject(R.id.teacher_reg_password_see)
	private CheckBox mPasswordSee;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStatusBarEnabled();
		StatusBarUtil.StatusBarLightMode(this);
		Intent intent = this.getIntent();
		this.setContentView(R.layout.activity_changepwd);
		this.setTitleLeftIcon(true, R.drawable.marking);
		this.setTitleText(true, "修改密码");
//		String phoneNumber = SharePrenceUtil.getTempPhoneNumber(this);
//		if ( !TextUtils.isEmpty(phoneNumber) ) {
//			ed_userName.setText(phoneNumber);
//			ed_pass.requestFocus();
//		}
		mPasswordSouceSee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ) {
					mSuoucePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}
				else {
					mSuoucePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
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
		mSuoucePassword.addTextChangedListener(new TextWatchListener());
		mPassword.addTextChangedListener(new TextWatchListener());
//		setStatusBarEnabled();
	}


	private boolean isEmpty() {
		boolean empty = false;
		if (CommonUtils.isEmpty(mSuoucePassword.getText().toString())) {
			Toast.makeText(this, "原密码不能为空", Toast.LENGTH_SHORT).show();
			empty = true;
		}
		else if (CommonUtils.isEmpty(mPassword.getText().toString())) {
			Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
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
		String sourcePassowrd = mSuoucePassword.getText().toString().trim();
		String passowrd= mPassword.getText().toString();

		ChangePwdRequest loginRequest = new ChangePwdRequest();
		loginRequest.setRaw_pwd(sourcePassowrd);
		loginRequest.setNew_pwd(passowrd);
		sendConnection("members/change_pwd.json",loginRequest,LOGIN,true,BaseEntity.class);
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
//				SPUtils.put(this,"sign",loginResult.getUser_token());
//				UserLoginEvent loginEvent = new UserLoginEvent();
//				loginEvent.setUserInfo(loginRespone);
//				loginEvent.setIntent(targerIntent);
//				EventBus.getDefault().post(loginEvent);
				this.finish();
				ToastUtil.show(mContext,"成功");

				//更新指纹识别
				UserInfo userInfo = SampleApplicationLike.getInstance().loadUserInfo();
				String password = mPassword.getText().toString();
				String cipher = null;
				try{
					cipher = AESCrypt.encrypt(userInfo.getLogin(),password);
				}catch (GeneralSecurityException e){
					e.printStackTrace();
				}
				SPUtils.put(this,SP_CONSTANTS.SP_CUR_PW,cipher);
				if(FingerprintHelper.isUserFingerprintEnabled(this,userInfo.getLogin())){
					SPUtils.put(this,SP_CONSTANTS.SP_PW,cipher);
				}
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
		String phoneNumber = mSuoucePassword.getText().toString().trim();
		String pwssord = mPassword.getText().toString().trim();
		if ( !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(pwssord)) {
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
