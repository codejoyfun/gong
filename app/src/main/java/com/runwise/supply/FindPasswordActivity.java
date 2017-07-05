package com.runwise.supply;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CheckUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.FindPwdRequest;
import com.runwise.supply.entity.GetCodeRequest;
import com.runwise.supply.tools.StatusBarUtil;

import java.io.Serializable;


/**
 *找回密码
 */
public class FindPasswordActivity extends NetWorkActivity {
	private static final int GET_CODE = 1;
	private static final int FIND_PASSWORD = 2;
	@ViewInject(R.id.teacher_reg_phone)
	private EditText mPhonenNmber;
	@ViewInject(R.id.teacher_reg_getcode)
	private TextView mGetCode;
	@ViewInject(R.id.teacher_reg_code)
	private EditText mCode;
	@ViewInject(R.id.teacher_reg_password)
	private EditText mPassword;
	@ViewInject(R.id.teacher_reg_password_rg)
	private EditText mPasswordrg;
	@ViewInject(R.id.teacher_reg_password_see)
	private CheckBox mPasswordSee;
	@ViewInject(R.id.teacher_reg_passwordrg_see)
	private CheckBox mRpasswordSee;
	@ViewInject(R.id.teacher_reg_next)
	private TextView finish;

	private boolean holdCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStatusBarEnabled();
		StatusBarUtil.StatusBarLightMode(this);
		setContentView(R.layout.activity_findpassword);
		this.setTitleText(true,"重置密码");
		this.setTitleLeftIcon(true,R.drawable.marking);
		String phoneNum = getIntent().getStringExtra("phoneNumber");
		mPhonenNmber.setText(phoneNum);
		if (TextUtils.isEmpty(phoneNum)) {
			mGetCode.setEnabled(false);
		}
		setFinishStatus();
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
		mRpasswordSee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ) {
					mPasswordrg.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}
				else {
					mPasswordrg.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
		mPhonenNmber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if ( s != null ) {
					String phoneNum = s.toString();
					if ( CheckUtil.isMobileNumber(phoneNum) && !holdCode) {
						mGetCode.setEnabled(true);
					}
					else {
						mGetCode.setEnabled(false);
					}
				}
			}
		});

		mPhonenNmber.addTextChangedListener(new TextWatchListener());
		mCode.addTextChangedListener(new TextWatchListener());
		mPassword.addTextChangedListener(new TextWatchListener());
		mPasswordrg.addTextChangedListener(new TextWatchListener());
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
			//获取验证码
			case GET_CODE:
				ToastUtil.show(mContext, result.getMsg());
				mGetCode.setEnabled(false);
				new WaitTimer(60).start();
				holdCode = true;
				break;
			case FIND_PASSWORD:
				ToastUtil.show(mContext, "成功");
				this.finish();
				break;
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		dialog.setModel(CustomDialog.LEFT);
		dialog.setMessage(errMsg);
		dialog.setLeftBtnListener("取消", null);
		dialog.show();
	}

	@OnClick(R.id.teacher_reg_next)
	public void doNext (View view) {
		String code = mCode.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		String passwordrg = mPasswordrg.getText().toString().trim();
		String phonNumber = mPhonenNmber.getText().toString().trim();
		if (TextUtils.isEmpty(phonNumber)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入手机号码");
			dialog.show();
			return;
		}
		else if (!CheckUtil.isMobileNumber(phonNumber)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入手机号码格式不正确哦");
			dialog.show();
			return;
		}
		if (TextUtils.isEmpty(code)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setMessage("请输入验证码");
			dialog.setLeftBtnListener("知道啦", null);
			dialog.show();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入密码");
			dialog.show();
			return;
		}
		else if (password.length() <=5 || password.length() > 16) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入6-16位的密码");
			dialog.show();
			return;
		}
		if (TextUtils.isEmpty(passwordrg)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入确认密码");
			dialog.show();
			return;
		}
		else if (passwordrg.length() <=5 || password.length() > 16) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入6-16位的确认密码");
			dialog.show();
			return;
		}
		if ( !password.equals(passwordrg)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("新密码与确认密码不一致");
			dialog.show();
			return;
		}
		FindPwdRequest paramBean = new FindPwdRequest(phonNumber,code,password);
		this.sendConnection("members/forget_pwd.json",paramBean ,FIND_PASSWORD, true, BaseEntity.class);
	}

	@OnClick(R.id.teacher_reg_getcode)
	public void doGetCode (View view) {
		String phonNumber = mPhonenNmber.getText().toString().trim();
		if (TextUtils.isEmpty(phonNumber)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入手机号码");
			dialog.show();
			return;
		}
		else if (!CheckUtil.isMobileNumber(phonNumber)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入手机号码格式不正确哦");
			dialog.show();
			return;
		}
		GetCodeRequest paramBean = new GetCodeRequest(phonNumber);
		this.sendConnection("/gongfu/get_captcha",paramBean ,GET_CODE, true, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick(R.id.left_layout)
	public void doBack (View view) {
		this.finish();
	}
	private class WaitTimer extends CountDownTimer implements Serializable {
		private static final long serialVersionUID = 1L;
		private int wait;
		public WaitTimer(int second) {
			super(second * 1000l, 1000l);
			wait = second;
		}
		@Override
		public void onFinish() {
			mGetCode.setEnabled(true);
			mGetCode.setText("获取验证码");
			holdCode = false;
			this.cancel();
		}
		@Override
		public void onTick(long millisUntilFinished) {
			wait --;
			mGetCode.setText(wait +"秒后重试");
		}
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
		String phoneNumber = mPhonenNmber.getText().toString().trim();
		String code = mCode.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		String passwrodrg = mPasswordrg.getText().toString().trim();
		if ( !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwrodrg)) {
			finish.setEnabled(true);
		}
		else {
			finish.setEnabled(false);
		}
	}

}
