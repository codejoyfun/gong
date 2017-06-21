package com.runwise.supply;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CheckUtil;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.GetCodeRequest;
import com.runwise.supply.entity.LoginRespone;
import com.runwise.supply.entity.LoginResult;
import com.runwise.supply.entity.RegisterRequest;
import com.runwise.supply.entity.UserInfo;
import com.runwise.supply.mine.entity.UrlResult;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import static com.runwise.supply.LoginActivity.targerIntent;

/**
 * 注册
 */
public class RegisterActivity extends NetWorkActivity {
	private static final int GET_CODE = 1;
	private static final int REGISTER = 2;
	private final int REQUEST_HELP = 3;

	@ViewInject(R.id.teacher_reg_phone)
	private EditText mPhonenNmber;
	@ViewInject(R.id.teacher_reg_getcode)
	private TextView mGetCode;
	@ViewInject(R.id.teacher_reg_code)
	private EditText mCode;
	@ViewInject(R.id.teacher_reg_password)
	private EditText mPassword;
	@ViewInject(R.id.teacher_reg_password_see)
	private CheckBox mPasswordSee;
	@ViewInject(R.id.teacher_reg_next)
	private TextView finish;
	@ViewInject(R.id.registerText)
	private CheckBox registerText;

	private boolean holdCode;
	String phonNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStatusBarEnabled();
		StatusBarUtil.StatusBarLightMode(this);
		setContentView(R.layout.activity_register);
		this.setTitleText(true,"注册");
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

		SpannableString spannableString = new SpannableString(registerText.getText());
		spannableString.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				ds.setColor(getResources().getColor(R.color.base_color));       //设置文件颜色
			}
			@Override
			public void onClick(View widget) {
//				Intent intent = new Intent(mContext,HhbbItemActivity.class);
//				startActivity(intent);
				sendConnection("protocol/user.json",REQUEST_HELP,true, UrlResult.class);
			}
		}, 9, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		registerText.setText(spannableString);
		registerText.setHighlightColor(Color.TRANSPARENT);
		registerText.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
			//获取验证码
			case GET_CODE:
				ToastUtil.show(mContext, "验证码发送成功，请注意查收");
				mGetCode.setEnabled(false);
				new WaitTimer(60).start();
				holdCode = true;
				break;
			case REGISTER:
				LoginRespone loginRespone = (LoginRespone) result;
				LoginResult loginResult = loginRespone.getData();
				SPUtils.put(this,"sign",loginResult.getUser_token());
				UserLoginEvent loginEvent = new UserLoginEvent();
				loginEvent.setUserInfo(loginRespone);
				loginEvent.setIntent(targerIntent);
				EventBus.getDefault().post(loginEvent);
				UserInfo userInfo = new UserInfo(loginResult.getMember_id());
				userInfo.setPhone(phonNumber);
				GlobalApplication.getInstance().saveUserInfo(userInfo);
				this.finish();
				break;
			case REQUEST_HELP:
				UrlResult helperResult = (UrlResult)result;
				Intent intent = new Intent(mContext, IWebViewActivity.class);
				intent.putExtra(WebViewActivity.WEB_TITLE,"用户使用协议");
				intent.putExtra(WebViewActivity.WEB_URL,helperResult.getData().getEntity().getUrl_addr());
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}

	@OnClick(R.id.teacher_reg_next)
	public void doNext (View view) {
		String code = mCode.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		phonNumber = mPhonenNmber.getText().toString().trim();
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
		if (!registerText.isChecked()) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请同意《用户使用协议》");
			dialog.show();
			return;
		}
		RegisterRequest paramBean = new RegisterRequest(phonNumber,code,password);
		this.sendConnection("members/register.json",paramBean , REGISTER, true, LoginRespone.class);
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
		this.sendConnection("members/send.json",paramBean ,GET_CODE, true, BaseEntity.class);
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
			mGetCode.setText("重新获取");
			holdCode = false;
			this.cancel();
		}
		@Override
		public void onTick(long millisUntilFinished) {
			wait --;
			mGetCode.setText("重新获取(" +wait+")");
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
		if ( !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(password)) {
			finish.setEnabled(true);
		}
		else {
			finish.setEnabled(false);
		}
	}

}
