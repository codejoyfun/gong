package com.runwise.supply;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CheckUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.RegisterData;
import com.runwise.supply.entity.RegisterRequest;
import com.runwise.supply.firstpage.RegisterSuccessActivity;
import com.runwise.supply.tools.StatusBarUtil;

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
//		overridePendingTransition(R.anim.slide_in_bottom,R.anim.activity_open_exit);
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
				finish();
				Intent intent = new Intent(getActivityContext(),RegisterSuccessActivity.class);
				startActivity(intent);
//				dialog.setCancelable(false);
//				dialog.setModel(CustomDialog.RIGHT);
//				dialog.setMessageGravity();
//				dialog.setMessage("非常感谢，您的注册申请已收到，供鲜生客服代表会尽快与您联系");
//				dialog.setRightBtnListener("我知道了", new CustomDialog.DialogListener() {
//					@Override
//					public void doClickButton(Button btn, CustomDialog dialog) {
//						finish();
//					}
//				});
//				dialog.show();
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
		this.sendConnection("/gongfu/v2/potential_users",paramBean , REGISTER, true, RegisterData.class);
	}


	@OnClick({R.id.closeBtn,R.id.gotoLogin})
	public void doBack (View view) {
		switch (view.getId()) {
			case R.id.closeBtn:
				this.finish();
				overridePendingTransition(R.anim.activity_close_enter,R.anim.slide_out_bottom);
				break;
			case R.id.gotoLogin:
				Activity act = ActivityManager.getInstance().getActivity("LoginActivity");
				if(act == null) {
					Intent intent = new Intent(mContext,LoginActivity.class);
					startActivity(intent);
				}
				finish();
				overridePendingTransition(R.anim.activity_close_enter,R.anim.slide_out_bottom);
				break;
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
