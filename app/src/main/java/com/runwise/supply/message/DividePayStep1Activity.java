package com.runwise.supply.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomSelectDialog;
import com.kids.commonframe.base.view.widget.PickerClickListener;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;
import com.runwise.supply.entity.OptionPayCar;
import com.runwise.supply.entity.OptionPayData;
import com.runwise.supply.entity.OptionPayEntity;
import com.runwise.supply.entity.OptionPayList;
import com.runwise.supply.entity.OptionPayResult;
import com.runwise.supply.entity.Step1Request;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请分期第一步
 */
public class DividePayStep1Activity extends NetWorkActivity {
	private final int REQUEST_OPTION = 1;
	private List<OptionPayEntity> optionList;

	@ViewInject(R.id.paystep1_text1)
	private TextView paystep1_text1;
	@ViewInject(R.id.paystep1_text2)
	private TextView paystep1_text2;
	@ViewInject(R.id.paystep1_text3)
	private TextView paystep1_text3;
	@ViewInject(R.id.paystep1_text4)
	private TextView paystep1_text4;
	@ViewInject(R.id.paystep1_text5)
	private TextView paystep1_text5;
	@ViewInject(R.id.paystep1_text6)
	private TextView paystep1_text6;

	@ViewInject(R.id.paystep1_button)
	private Button paystep1_button;
	@ViewInject(R.id.paystep1_checkbox)
	private CheckBox paystep1_checkbox;

	private String carId,dealerId,dateId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paystep_1);
		this.setTitleText(true,"(1/4)申请");
		this.setTitleLeftIcon(true,R.drawable.back_btn);
		setFinishStatus();
		paystep1_text1.addTextChangedListener(new TextWatchListener());
		paystep1_text2.addTextChangedListener(new TextWatchListener());
		paystep1_text3.addTextChangedListener(new TextWatchListener());
		paystep1_text4.addTextChangedListener(new TextWatchListener());
		paystep1_text5.addTextChangedListener(new TextWatchListener());
		paystep1_text6.addTextChangedListener(new TextWatchListener());

        Intent intent = getIntent();
		carId = intent.getStringExtra("carId");
		dealerId = intent.getStringExtra("dealerId");
		Step1Request request = new Step1Request();
		request.setCar_id(carId);
		request.setDealer_id(dealerId);
		sendConnection("apply/period_list.json",request,REQUEST_OPTION,true, OptionPayResult.class);
	}

	private void addSelectPickerListener(final TextView textView,final List<String> options) {
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> items1 = new ArrayList<String>();
				items1.addAll(options);
				int selectInt1 = 0;
				CustomSelectDialog dataDialog1 = new CustomSelectDialog(mContext, items1);
				dataDialog1.setCurrentItem(selectInt1);
				dataDialog1.addPickerListener("确定", new PickerClickListener() {
					@Override
					public void doPickClick(String currentStr, int currentPosition) {
//						textView.setText(currentStr);
						setOptionChange(optionList.get(currentPosition));
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
			case REQUEST_OPTION:
				OptionPayResult optionPayResult = (OptionPayResult)result;
				OptionPayData data = optionPayResult.getData();
				OptionPayList payList = data.getEntities();
				OptionPayCar carInfo = payList.getCar();
				setTitleText(true,"(1/4)"+carInfo.getTitle()+"分期申请");
				optionList = payList.getPeriods();
				List<String> optionsStr = new ArrayList<>();
				for(OptionPayEntity bean:optionList) {
					optionsStr.add(bean.getName());
				}
				paystep1_text4.setText("预计下月" + payList.getNext_date()+"号为还款日");

				paystep1_text1.setText("¥"+carInfo.getMarket_price());
				paystep1_text5.setText("¥"+carInfo.getSale_price());
				if (optionList != null && !optionList.isEmpty()) {
					setOptionChange(optionList.get(0));
				}
				addSelectPickerListener(paystep1_text2,optionsStr);
				break;
		}
	}
	public void setOptionChange(OptionPayEntity payEntity) {
		if( payEntity != null ) {
			dateId = payEntity.getPeriod_id();
			paystep1_text2.setText("¥"+payEntity.getName());
			paystep1_text3.setText("¥"+payEntity.getFirst_period());
			paystep1_text6.setText("¥"+payEntity.getMonth_period());
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}

	@OnClick(R.id.paystep1_button)
	public void doNext (View view) {
//		if (!paystep1_checkbox.isChecked()) {
//			dialog.setModel(CustomDialog.LEFT);
//			dialog.setLeftBtnListener("知道啦", null);
//			dialog.setMessage("请同意《用户使用协议》");
//			dialog.show();
//			return;
//		}
		Intent intent = new Intent(this,DividePayStep2Activity.class);
		intent.putExtra("carId",carId);
		intent.putExtra("dealerId",dealerId);
		intent.putExtra("dateId",dateId);
        startActivity(intent);
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
		String paystep1_text1 = this.paystep1_text1.getText().toString().trim();
		String paystep1_text2 = this.paystep1_text2.getText().toString().trim();
		String paystep1_text3 = this.paystep1_text3.getText().toString().trim();
		String paystep1_text4 = this.paystep1_text4.getText().toString().trim();
		String paystep1_text5 = this.paystep1_text5.getText().toString().trim();
		String paystep1_text6 = this.paystep1_text6.getText().toString().trim();
		if ( !TextUtils.isEmpty(paystep1_text1) && !TextUtils.isEmpty(paystep1_text2)
				&& !TextUtils.isEmpty(paystep1_text3) && !TextUtils.isEmpty(paystep1_text4)
				&& !TextUtils.isEmpty(paystep1_text5) && !TextUtils.isEmpty(paystep1_text6)) {
			paystep1_button.setEnabled(true);
		}
		else {
			paystep1_button.setEnabled(false);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinishActEvent(FinishActEvent event) {
		this.finish();
	}

}
