package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.UserInfo;
import com.runwise.supply.mine.entity.EditUserInfoRequest;
import com.runwise.supply.mine.entity.UpdateUserInfoRep;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;


/**
 * 修改性别
 */
public class UpdateSexActivity extends NetWorkActivity implements OnClickListener{
	private final int REQUEST_UPDATE_SEX = 1 ;

	@ViewInject(R.id.ret_nan)
	private RelativeLayout ret_nan;
	@ViewInject(R.id.ret_nv)
	private RelativeLayout ret_nv;
	@ViewInject(R.id.img_n1)
	private ImageView img_n1;
	@ViewInject(R.id.img_n2)
	private ImageView img_n2;
	@ViewInject(R.id.title_iv_left)
	private ImageView sex_back;
	
	private String sex; //1男  其它为女
	private UserInfo userInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitvity_update_sex);

		this.setTitleLeftIcon(true, R.drawable.back_btn);
		this.setTitleText(true, "性别");
		userInfo = GlobalApplication.getInstance().loadUserInfo();

		initListener();

		sex = getIntent().getStringExtra("sex");
		if("1".equals(sex)){
			img_n2.setVisibility(View.GONE);
			img_n1.setVisibility(View.VISIBLE);
		}
		else{
			img_n1.setVisibility(View.GONE);
			img_n2.setVisibility(View.VISIBLE);
		}
	}
	private void initListener() {
		sex_back.setOnClickListener(this);
		ret_nan.setOnClickListener(this);
		ret_nv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {		
			case R.id.title_iv_left:
				finish();
				break;
			case R.id.ret_nan:
				img_n2.setVisibility(View.GONE);
				img_n1.setVisibility(View.VISIBLE);
				sex = "1";
				updateSex();
				break;
			case R.id.ret_nv:
				img_n1.setVisibility(View.GONE);
				img_n2.setVisibility(View.VISIBLE);
				sex = "2";
				updateSex();
				break;
		}
	}
	/**
	 * 更改性别
	 */
	public void updateSex() {
		EditUserInfoRequest request = new EditUserInfoRequest();
		request.setSex(sex);
//		request.setUid(userInfo.getMember_id());
		this.sendConnection("/Appapi/user/edit_info", request, REQUEST_UPDATE_SEX, true, UpdateUserInfoRep.class);
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
		case REQUEST_UPDATE_SEX:
			UpdateUserInfoRep updateResult = (UpdateUserInfoRep)result;
//			userInfo.setSex(sex);
			GlobalApplication.getInstance().saveUserInfo(userInfo);
			ToastUtil.show(mContext, "性别修改成功");
			Intent intent = new Intent();
			intent.putExtra("sex", sex);
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(this,errMsg);
	}
}
