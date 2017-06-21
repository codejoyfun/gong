package com.runwise.supply.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CheckUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.CustomSelectDialog;
import com.kids.commonframe.base.view.widget.PickerClickListener;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;
import com.runwise.supply.entity.GetCodeRequest;
import com.runwise.supply.entity.SelectOption;
import com.runwise.supply.entity.SelectOptionData;
import com.runwise.supply.entity.SelectOptionList;
import com.runwise.supply.entity.SelectOptionResult;
import com.runwise.supply.message.entity.ApplyResult;
import com.runwise.supply.message.entity.Step2Request;
import com.runwise.supply.view.CustomDatePickerDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 申请分期第二步
 */
public class DividePayStep2Activity extends NetWorkActivity {
	private final int GET_CODE = 1;
	private final int REQUEST_OPTION = 2;
	private final int REQUEST_STEP2_FINISH = 3;

	private String carId,dealerId,dateId;

	@ViewInject(R.id.paystep2_text1)
	private EditText paystep2_text1;
	@ViewInject(R.id.paystep2_text2)
	private EditText paystep2_text2;
	@ViewInject(R.id.paystep2_text3)
	private TextView paystep2_text3;
	@ViewInject(R.id.paystep2_text4)
	private TextView paystep2_text4;
	@ViewInject(R.id.paystep2_text5)
	private EditText paystep2_text5;
	@ViewInject(R.id.paystep2_text6)
	private EditText paystep2_text6;
	@ViewInject(R.id.paystep2_text7)
	private EditText paystep2_text7;
	@ViewInject(R.id.paystep2_text8)
	private EditText paystep2_text8;
	@ViewInject(R.id.paystep2_text9)
	private EditText paystep2_text9;
	@ViewInject(R.id.paystep2_text10)
	private TextView paystep2_text10;
	@ViewInject(R.id.paystep2_text11)
	private EditText paystep2_text11;
	@ViewInject(R.id.paystep2_text12)
	private TextView paystep2_text12;
	@ViewInject(R.id.paystep2_text13)
	private EditText paystep2_text13;
	@ViewInject(R.id.paystep2_text14)
	private TextView paystep2_text14;
	@ViewInject(R.id.paystep2_text15)
	private EditText paystep2_text15;
	@ViewInject(R.id.paystep2_text16)
	private EditText paystep2_text16;
	@ViewInject(R.id.paystep2_text17)
	private TextView paystep2_text17;

	@ViewInject(R.id.paystep2_button)
	private Button paystep2_button;

	@ViewInject(R.id.getcode)
	private TextView mGetCode;
	private boolean holdCode;

	private List<SelectOption> banksList;
	private List<SelectOption> worksList;
	private List<SelectOption> jobsList;
	private List<SelectOption> marrysList;
	private List<SelectOption> relationsList;

	private String applyId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paystep_2);
		this.setTitleText(true,"(2/4)申请人信息");
		this.setTitleLeftIcon(true,R.drawable.back_btn);
		sendConnection("apply/option_list.json",REQUEST_OPTION,true, SelectOptionResult.class);

		setFinishStatus();
		paystep2_text1.addTextChangedListener(new TextWatchListener());
		paystep2_text2.addTextChangedListener(new TextWatchListener());
		paystep2_text3.addTextChangedListener(new TextWatchListener());
		paystep2_text4.addTextChangedListener(new TextWatchListener());
		paystep2_text5.addTextChangedListener(new TextWatchListener());
		paystep2_text6.addTextChangedListener(new TextWatchListener());
		paystep2_text7.addTextChangedListener(new TextWatchListener());
		paystep2_text6.addTextChangedListener(new TextWatcher() {
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
		paystep2_text9.addTextChangedListener(new TextWatchListener());
		paystep2_text10.addTextChangedListener(new TextWatchListener());
		paystep2_text11.addTextChangedListener(new TextWatchListener());
		paystep2_text12.addTextChangedListener(new TextWatchListener());
		paystep2_text13.addTextChangedListener(new TextWatchListener());
		paystep2_text14.addTextChangedListener(new TextWatchListener());
		paystep2_text15.addTextChangedListener(new TextWatchListener());
		paystep2_text16.addTextChangedListener(new TextWatchListener());
		paystep2_text17.addTextChangedListener(new TextWatchListener());
		setSelectListener();
	}

	private void setSelectListener() {
		addDataPickerListener(paystep2_text3);
		addDataPickerListener(paystep2_text4);


		addSelectPickerListener(paystep2_text14,null);
		addSelectPickerListener(paystep2_text17,null);
	}

	private void addDataPickerListener(final TextView textView) {
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomDatePickerDialog dateDialog = new CustomDatePickerDialog(mContext, "");
				dateDialog.addPickerListener("完成", new CustomDatePickerDialog.PickerClickListener() {
					@Override
					public void doPickClick(String currentStr, String currentStr1,
											int currentPosition) {
						try {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date date = format.parse(currentStr1);
							Date currDate = new Date();
							textView.setText(currentStr1);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				});
				dateDialog.show();
			}
		});
	}
	private void addSelectPickerListener(final TextView textView,final List<String> options) {
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int selectInt1 = 0;
				CustomSelectDialog dataDialog1 = new CustomSelectDialog(mContext, options);
				dataDialog1.setCurrentItem(selectInt1);
				dataDialog1.addPickerListener("确定", new PickerClickListener() {
					@Override
					public void doPickClick(String currentStr, int currentPosition) {
						textView.setText(currentStr);
						switch(textView.getId()) {
							//银行
							case R.id.paystep2_text10:
								SelectOption bankOption = banksList.get(currentPosition);
								paystep2_text10.setTag(bankOption.getOption_value());
								break;
							//工作状态
							case R.id.paystep2_text12:
								SelectOption workOption = worksList.get(currentPosition);
								paystep2_text12.setTag(workOption.getOption_value());
								break;
							//工作
							case R.id.paystep2_text14:
								SelectOption jobsOption = jobsList.get(currentPosition);
								paystep2_text14.setTag(jobsOption.getOption_value());
								break;
							//婚姻
							case R.id.paystep2_text17:
								SelectOption merrysOption = marrysList.get(currentPosition);
								paystep2_text17.setTag(merrysOption.getOption_value());
								break;
						}
					}
				});
				dataDialog1.show();
			}
		});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
			case GET_CODE:
				ToastUtil.show(mContext, "验证码发送成功，请注意查收");
				mGetCode.setEnabled(false);
				new WaitTimer(60).start();
				holdCode = true;
				break;
			case REQUEST_OPTION:
				SelectOptionResult optionResult = (SelectOptionResult)result;
				SelectOptionData optionData = optionResult.getData();
				SelectOptionList optionList = optionData.getEntities();
				banksList = optionList.getBanks();
				worksList = optionList.getWorks();
				jobsList = optionList.getJobs();
				marrysList = optionList.getMarrys();
				relationsList = optionList.getRelations();
				List<String> bankStrList = new ArrayList<>();
				for(SelectOption selectOption:banksList) {
					bankStrList.add(selectOption.getOption_name());
				}
				addSelectPickerListener(paystep2_text10,bankStrList);

				List<String> worksStrList = new ArrayList<>();
				for(SelectOption selectOption:worksList) {
					worksStrList.add(selectOption.getOption_name());
				}
				addSelectPickerListener(paystep2_text12,worksStrList);

				List<String> jobStrList = new ArrayList<>();
				for(SelectOption selectOption:jobsList) {
					jobStrList.add(selectOption.getOption_name());
				}
				addSelectPickerListener(paystep2_text14,jobStrList);

				List<String> merryStrList = new ArrayList<>();
				for(SelectOption selectOption:marrysList) {
					merryStrList.add(selectOption.getOption_name());
				}
				addSelectPickerListener(paystep2_text17,merryStrList);
				break;
			case REQUEST_STEP2_FINISH:
				ApplyResult applyResult = (ApplyResult)result;
				Intent intent = new Intent(this,DividePayStep3Activity.class);
				intent.putParcelableArrayListExtra("relationsList",(ArrayList<? extends Parcelable>) relationsList);
				applyId = applyResult.getData().getApply_id();
				intent.putExtra("applyId",applyId);
				intent.putExtra("ismerry",paystep2_text17.getTag().toString());
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}

	@OnClick(R.id.paystep2_button)
	public void doNext (View view) {
		String paystep2_text1 = this.paystep2_text1.getText().toString().trim();
		String paystep2_text2 = this.paystep2_text2.getText().toString().trim();
		String paystep2_text3 = this.paystep2_text3.getText().toString().trim();
		String paystep2_text4 = this.paystep2_text4.getText().toString().trim();
		String paystep2_text5 = this.paystep2_text5.getText().toString().trim();
		String paystep2_text6 = this.paystep2_text6.getText().toString().trim();
		String paystep2_text7 = this.paystep2_text7.getText().toString().trim();
		String paystep2_text8 = this.paystep2_text8.getText().toString().trim();
		String paystep2_text9 = this.paystep2_text9.getText().toString().trim();
//		String paystep2_text10 = this.paystep2_text10.getText().toString().trim();
		String paystep2_text11 = this.paystep2_text11.getText().toString().trim();
//		String paystep2_text12 = this.paystep2_text12.getText().toString().trim();
		String paystep2_text13 = this.paystep2_text13.getText().toString().trim();
//		String paystep2_text14 = this.paystep2_text14.getText().toString().trim();
		String paystep2_text15 = this.paystep2_text15.getText().toString().trim();
		String paystep2_text16 = this.paystep2_text16.getText().toString().trim();

//		if(!new IdentityCardHelper().isValidate18Idcard(paystep2_text2)) {
//			ToastUtil.show(mContext,"身份证号格式错误");
//			return;
//		}
		Step2Request step2Request = new Step2Request();
		step2Request.setRealname(paystep2_text1);
		step2Request.setIdentity_id(paystep2_text2);
		step2Request.setIdentity_start_at(paystep2_text3);
		step2Request.setIdentity_end_at(paystep2_text4);
		step2Request.setCompany(paystep2_text5);
		step2Request.setPhone(paystep2_text6);
		step2Request.setWechat_id(paystep2_text8);
		step2Request.setEmail(paystep2_text9);
		step2Request.setBank_name(this.paystep2_text10.getTag().toString());
		step2Request.setCard_id(paystep2_text11);
		step2Request.setWork_status(this.paystep2_text12.getTag().toString());
		step2Request.setCompany_phone(paystep2_text13);
		step2Request.setProfession(this.paystep2_text14.getTag().toString());
		step2Request.setIs_marry(this.paystep2_text17.getTag().toString());
		step2Request.setCompany_address(paystep2_text15);
		step2Request.setLive_address(paystep2_text16);
		if(!TextUtils.isEmpty(applyId)) {
			step2Request.setApply_id(applyId);
		}
		step2Request.setSms_code(paystep2_text7);
		Intent intent = this.getIntent();
		step2Request.setDealer_id(intent.getStringExtra("dealerId"));
		step2Request.setCar_id(intent.getStringExtra("carId"));
		step2Request.setPeriod_id(intent.getStringExtra("dateId"));
		sendConnection("apply/main_info.json",step2Request,REQUEST_STEP2_FINISH,true, ApplyResult.class);
	}

	@OnClick(R.id.left_layout)
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
		String paystep2_text1 = this.paystep2_text1.getText().toString().trim();
		String paystep2_text2 = this.paystep2_text2.getText().toString().trim();
		String paystep2_text3 = this.paystep2_text3.getText().toString().trim();
		String paystep2_text4 = this.paystep2_text4.getText().toString().trim();
		String paystep2_text5 = this.paystep2_text5.getText().toString().trim();
		String paystep2_text6 = this.paystep2_text6.getText().toString().trim();
		String paystep2_text7 = this.paystep2_text7.getText().toString().trim();
		String paystep2_text8 = this.paystep2_text7.getText().toString().trim();
		String paystep2_text9 = this.paystep2_text9.getText().toString().trim();
		String paystep2_text10 = this.paystep2_text10.getText().toString().trim();
		String paystep2_text11 = this.paystep2_text11.getText().toString().trim();
		String paystep2_text12 = this.paystep2_text12.getText().toString().trim();
		String paystep2_text13 = this.paystep2_text13.getText().toString().trim();
		String paystep2_text14 = this.paystep2_text14.getText().toString().trim();
		String paystep2_text15 = this.paystep2_text15.getText().toString().trim();
		String paystep2_text16 = this.paystep2_text16.getText().toString().trim();
		if ( !TextUtils.isEmpty(paystep2_text1) && !TextUtils.isEmpty(paystep2_text2)
				&& !TextUtils.isEmpty(paystep2_text3) && !TextUtils.isEmpty(paystep2_text4)
				&& !TextUtils.isEmpty(paystep2_text5) && !TextUtils.isEmpty(paystep2_text6)
				&& !TextUtils.isEmpty(paystep2_text7) && !TextUtils.isEmpty(paystep2_text9)
				&& !TextUtils.isEmpty(paystep2_text10) && !TextUtils.isEmpty(paystep2_text11)
				&& !TextUtils.isEmpty(paystep2_text12) && !TextUtils.isEmpty(paystep2_text13)
				&& !TextUtils.isEmpty(paystep2_text14) && !TextUtils.isEmpty(paystep2_text15)
				&& !TextUtils.isEmpty(paystep2_text16) && !TextUtils.isEmpty(paystep2_text8)
				) {
			paystep2_button.setEnabled(true);
		}
		else {
			paystep2_button.setEnabled(false);
		}
	}

	@OnClick(R.id.getcode)
	public void doGetCode (View view) {
		String phonNumber = paystep2_text6.getText().toString().trim();
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
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinishActEvent(FinishActEvent event) {
		this.finish();
	}

}
