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
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.GetCodeRequest;
import com.runwise.supply.entity.LoginRespone;
import com.runwise.supply.entity.RegisterData;
import com.runwise.supply.entity.RegisterRequest;
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

	@ViewInject(R.id.teacher_reg_name)
	private EditText mName;
	@ViewInject(R.id.teacher_reg_phone)
	private TextView mPhone;
	@ViewInject(R.id.teacher_register_email)
	private EditText mEmail;
	@ViewInject(R.id.teacher_reg_compony)
	private EditText mCompony;
	@ViewInject(R.id.registerBtn)
	private TextView registerBtn;

	private boolean holdCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStatusBarEnabled();
		StatusBarUtil.StatusBarLightMode(this);
		setContentView(R.layout.activity_register);
		setFinishStatus();

		mName.addTextChangedListener(new TextWatchListener());
		mPhone.addTextChangedListener(new TextWatchListener());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
			case REGISTER:
//				LoginRespone loginRespone = (LoginRespone) result;
//				LoginResult loginResult = loginRespone.getData();
//				SPUtils.put(this,"sign",loginResult.getUser_token());
				UserLoginEvent loginEvent = new UserLoginEvent();
//				loginEvent.setUserInfo(loginRespone);
				loginEvent.setIntent(targerIntent);
				EventBus.getDefault().post(loginEvent);
//				UserInfo userInfo = new UserInfo(loginResult.getMember_id());
//				userInfo.setPhone(phonNumber);
//				GlobalApplication.getInstance().saveUserInfo(userInfo);
				ToastUtil.show(mContext,"注册成功");
				this.finish();
				break;
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}

	@OnClick(R.id.registerBtn)
	public void doNext (View view) {
		String name = mName.getText().toString().trim();
		String phone = mPhone.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setMessage("请输入姓名");
			dialog.setLeftBtnListener("知道啦", null);
			dialog.show();
			return;
		}
		if (TextUtils.isEmpty(phone)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入手机号码");
			dialog.show();
			return;
		}
		else if (!CheckUtil.isMobileNumber(phone)) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请输入手机号码格式不正确哦");
			dialog.show();
			return;
		}
		RegisterRequest paramBean = new RegisterRequest(phone,name,mEmail.getText().toString(),mCompony.getText().toString());
		this.sendConnection("/gongfu/v2/authenticate",paramBean , REGISTER, true, RegisterData.class);
	}


	@OnClick({R.id.closeBtn,R.id.gotoLogin})
	public void doBack (View view) {
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
		String phoneNumber = mName.getText().toString().trim();
		String code = mPhone.getText().toString().trim();
		if ( !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code) ) {
			registerBtn.setEnabled(true);
		}
		else {
			registerBtn.setEnabled(false);
		}
	}

}
