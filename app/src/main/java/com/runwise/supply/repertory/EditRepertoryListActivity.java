package com.runwise.supply.repertory;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.repertory.entity.EditRepertoryResult;
import com.runwise.supply.repertory.entity.EditRequest;

/**
 * 库存盘点
 */
public class EditRepertoryListActivity extends NetWorkActivity{
	private final int PRODUCT_GET = 1;
	private final int PRODUCT_COMMIT = 2;

	private List<Fragment> fragments = new ArrayList<Fragment>();
	@ViewInject(R.id.tabs_rg)
	private RadioGroup rgs;

	@ViewInject(R.id.tabImage1)
	private ImageView tabImage1;
	@ViewInject(R.id.tabImage2)
	private ImageView tabImage2;
	@ViewInject(R.id.tabImage3)
	private ImageView tabImage3;
	@ViewInject(R.id.tabImage4)
	private ImageView tabImage4;

	@ViewInject(R.id.tab1)
	private RadioButton tab1;
	@ViewInject(R.id.tab2)
	private RadioButton tab2;
	@ViewInject(R.id.tab3)
	private RadioButton tab3;
	@ViewInject(R.id.tab4)
	private RadioButton tab4;
	FragmentTabAdapter tabAdapter;

	private EditRepertoryListFragment allFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs_list_layout);
		setTitleText(true, "盘点");
		Object param = null;

		sendConnection("/gongfu/shop/inventory/"+this.getIntent().getStringExtra("id")+"/list",param,PRODUCT_GET,true, EditRepertoryResult.class);
	}

	@OnClick({R.id.title_iv_left,R.id.title_iv_left1,R.id.right_layout})
	public void OnBack(View v) {
		switch (v.getId()) {
			case R.id.title_iv_left:
				finish();
				break;
			//收索
			case R.id.title_iv_left1:
				Intent intent = new Intent(mContext,EditRepertoryAddActivity.class);
				startActivity(intent);
				break;
			//提交
			case R.id.right_layout:
                if(allFragment != null) {
					dialog.setModel(CustomDialog.BOTH);
					dialog.setLeftBtnListener("取消",null);
					dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
						@Override
						public void doClickButton(Button btn, CustomDialog dialog) {
							EditRequest editRequest = new EditRequest();
							editRequest.setId(Integer.parseInt(getIntent().getStringExtra("id")));
							editRequest.setState("done");
							List<EditRepertoryResult.InventoryBean.ListBean> finalDataList = allFragment.getFinalDataList();
							List<EditRequest.ProductBean> editListBean = new ArrayList<>();
							for(EditRepertoryResult.InventoryBean.ListBean bean : finalDataList) {
								if(bean.getType() == 0) {
									EditRequest.ProductBean productBean = new EditRequest.ProductBean();
									productBean.setProduct_id(bean.getProduct().getId());
									productBean.setId(bean.getId());
									productBean.setActual_qty(bean.getEditNum());
									productBean.setLot_id(bean.getLot_id()+"");
									productBean.setLot_num(bean.getLot_num());
									editListBean.add(productBean);
								}

							}
							editRequest.setInventory_lines(editListBean);
							sendConnection("/gongfu/shop/inventory/state",editRequest,PRODUCT_COMMIT,true, EditRepertoryResult.class);
						}
					});
					dialog.setMessage("盘点成功，确认更新库存?");
					dialog.show();
				}
				break;
		}

	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
			case PRODUCT_GET:
				EditRepertoryResult repertoryResult = (EditRepertoryResult)result.getResult();
				List<EditRepertoryResult.InventoryBean.ListBean> list = repertoryResult.getInventory().getList();

				Bundle bundle = new Bundle();
				allFragment = new EditRepertoryListFragment();
				allFragment.type = DataType.ALL;
				allFragment.setArguments(bundle);
				allFragment.setData(list);

				EditRepertoryListFragment coldFragment = new EditRepertoryListFragment();
				coldFragment.type = DataType.LENGCANGHUO;
				coldFragment.setArguments(bundle);
				coldFragment.setData(list);
				EditRepertoryListFragment freezeFragment = new EditRepertoryListFragment();
				freezeFragment.type = DataType.FREEZE;
				freezeFragment.setArguments(bundle);
				freezeFragment.setData(list);
				EditRepertoryListFragment dryFragment = new EditRepertoryListFragment();
				dryFragment.type = DataType.DRY;
				dryFragment.setArguments(bundle);
				dryFragment.setData(list);

				fragments.add(allFragment);
				fragments.add(coldFragment);
				fragments.add(freezeFragment);
				fragments.add(dryFragment);

				tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, rgs);
				tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
					@Override
					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
						tabImage1.setVisibility(View.INVISIBLE);
						tabImage2.setVisibility(View.INVISIBLE);
						tabImage3.setVisibility(View.INVISIBLE);
						tabImage4.setVisibility(View.INVISIBLE);
						tab1.setTextColor(Color.parseColor("#666666"));
						tab2.setTextColor(Color.parseColor("#666666"));
						tab3.setTextColor(Color.parseColor("#666666"));
						tab4.setTextColor(Color.parseColor("#666666"));
						if (index == 0) {
							tab1.setTextColor(Color.parseColor("#6bb400"));
							tabImage1.setVisibility(View.VISIBLE);
						}
						else if (index == 1) {
							tab2.setTextColor(Color.parseColor("#6bb400"));
							tabImage2.setVisibility(View.VISIBLE);
						}
						else if (index == 2) {
							tab3.setTextColor(Color.parseColor("#6bb400"));
							tabImage3.setVisibility(View.VISIBLE);
						}
						else if (index == 3) {
							tab4.setTextColor(Color.parseColor("#6bb400"));
							tabImage4.setVisibility(View.VISIBLE);
						}
					}
				});
				break;
			case PRODUCT_COMMIT:
				ToastUtil.show(mContext,"盘点成功");
				Intent intent = new Intent(mContext,EditRepertoryFinishActivity.class);
				startActivity(intent);
				finish();
				break;
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}
}
