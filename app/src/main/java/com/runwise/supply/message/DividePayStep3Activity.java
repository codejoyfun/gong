package com.runwise.supply.message;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.runwise.supply.entity.SelectOption;
import com.runwise.supply.message.entity.ApplyData;
import com.runwise.supply.message.entity.ApplyResult;
import com.runwise.supply.message.entity.Step3Request;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请分期第三步
 */
public class DividePayStep3Activity extends NetWorkActivity {
	private final int REQUEST_STEP3_FINISH = 1;

	@ViewInject(R.id.paystep3_text1)
	private EditText paystep3_text1;
	@ViewInject(R.id.paystep3_text2)
	private EditText paystep3_text2;
	@ViewInject(R.id.paystep3_text3)
	private TextView paystep3_text3;
	@ViewInject(R.id.paystep3_text4)
	private EditText paystep3_text4;
	@ViewInject(R.id.paystep3_text5)
	private EditText paystep3_text5;
	@ViewInject(R.id.paystep3_text6)
	private TextView paystep3_text6;

	@ViewInject(R.id.paystep3_button)
	private Button paystep3_button;

	private List<SelectOption> relationsList;
	private String applyId;
	private String ismerry;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paystep_3);
		this.setTitleText(true,"(3/4)联系人信息");
		this.setTitleLeftIcon(true,R.drawable.back_btn);
		Intent intent = getIntent();
		relationsList = intent.getParcelableArrayListExtra("relationsList");
		applyId = intent.getStringExtra("applyId");
		ismerry = intent.getStringExtra("ismerry");
		setFinishStatus();
		paystep3_text1.addTextChangedListener(new TextWatchListener());
		paystep3_text2.addTextChangedListener(new TextWatchListener());
		paystep3_text3.addTextChangedListener(new TextWatchListener());
		paystep3_text4.addTextChangedListener(new TextWatchListener());
		paystep3_text5.addTextChangedListener(new TextWatchListener());
		paystep3_text6.addTextChangedListener(new TextWatchListener());

		List<String> relationStrList1 = new ArrayList<>();
		if ( relationsList != null )
			for(SelectOption selectOption:relationsList) {
				relationStrList1.add(selectOption.getOption_name());
			}
		addSelectPickerListener(paystep3_text3,relationStrList1);

		List<String> relationStrList2 = new ArrayList<>();
		if ( relationsList != null )
			for(SelectOption selectOption:relationsList) {
				relationStrList2.add(selectOption.getOption_name());
			}
		addSelectPickerListener(paystep3_text6,relationStrList2);
	}

	private void addSelectPickerListener(final TextView textView,final List<String> options) {
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomSelectDialog dataDialog1 = new CustomSelectDialog(mContext, options);
//				dataDialog1.setCurrentItem(selectInt1);
				dataDialog1.addPickerListener("确定", new PickerClickListener() {
					@Override
					public void doPickClick(String currentStr, int currentPosition) {
						textView.setText(currentStr);
						textView.setTag(relationsList.get(currentPosition).getOption_value());
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
			case REQUEST_STEP3_FINISH:
				ApplyResult applyResult = (ApplyResult)result;
				ApplyData applyData = applyResult.getData();
				Intent intent = new Intent(this,DividePayStep4Activity.class);
				intent.putExtra("applyId",applyId);
				intent.putExtra("ismerry",ismerry);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}

	@OnClick(R.id.paystep3_button)
	public void doNext (View view) {
		String paystep3_text1 = this.paystep3_text1.getText().toString().trim();
		String paystep3_text2 = this.paystep3_text2.getText().toString().trim();
//		String paystep3_text3 = this.paystep3_text3.getText().toString().trim();
		String paystep3_text4 = this.paystep3_text4.getText().toString().trim();
		String paystep3_text5 = this.paystep3_text5.getText().toString().trim();
//		String paystep3_text6 = this.paystep3_text6.getText().toString().trim();


		Step3Request step3Request = new Step3Request();
		step3Request.setRealname(paystep3_text1);
		step3Request.setPhone(paystep3_text2);
		step3Request.setRelation(this.paystep3_text3.getTag().toString());

		step3Request.setRealname_two(paystep3_text4);
		step3Request.setPhone_two(paystep3_text5);
		step3Request.setRelation_two(this.paystep3_text6.getTag().toString());

		step3Request.setApply_id(applyId);

		sendConnection("apply/contact_info.json",step3Request,REQUEST_STEP3_FINISH,true, ApplyResult.class);
	}
	@OnClick(R.id.selectContact1)
	public void selectContact1(View view) {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		Intent intent = new Intent(Intent.ACTION_PICK, uri);
		startActivityForResult(intent,5);
	}
	@OnClick(R.id.selectContact2)
	public void selectContact2(View view) {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		Intent intent = new Intent(Intent.ACTION_PICK, uri);
		startActivityForResult(intent,6);
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
		String paystep3_text1 = this.paystep3_text1.getText().toString().trim();
		String paystep3_text2 = this.paystep3_text2.getText().toString().trim();
		String paystep3_text3 = this.paystep3_text3.getText().toString().trim();
		String paystep3_text4 = this.paystep3_text4.getText().toString().trim();
		String paystep3_text5 = this.paystep3_text5.getText().toString().trim();
		String paystep3_text6 = this.paystep3_text6.getText().toString().trim();
		if ( !TextUtils.isEmpty(paystep3_text1) && !TextUtils.isEmpty(paystep3_text2)
				&& !TextUtils.isEmpty(paystep3_text3) && !TextUtils.isEmpty(paystep3_text4)
				&& !TextUtils.isEmpty(paystep3_text5) && !TextUtils.isEmpty(paystep3_text6)) {
			paystep3_button.setEnabled(true);
		}
		else {
			paystep3_button.setEnabled(false);
		}
	}
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinishActEvent(FinishActEvent event) {
		this.finish();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case 5:
			case 6:
				if(data==null) {
					return;
				}
//处理返回的data,获取选择的联系人信息
				Uri uri=data.getData();
//得到ContentResolver对象
				ContentResolver cr = getContentResolver();
//取得电话本中开始一项的光标
				Cursor cursor=cr.query(uri,null,null,null,null);
				if(cursor!=null)
				{
					cursor.moveToFirst();
//取得联系人姓名
					int nameFieldColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
					String mName =cursor.getString(nameFieldColumnIndex);
//取得电话号码
					String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
					String mPhone = "";
					if(phone != null){
						phone.moveToFirst();
						mPhone = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					phone.close();
					cursor.close();
					if(requestCode == 5) {
						paystep3_text1.setText(mName);
						paystep3_text2.setText(mPhone);
					}
					else {
						paystep3_text4.setText(mName);
						paystep3_text5.setText(mPhone);
					}
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
