package com.runwise.supply.repertory;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.EditRepertoryResult;
import com.runwise.supply.repertory.entity.EditRequest;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.orderpage.ProductBasicUtils.getBasicMap;

/**
 * 库存盘点
 */
public class EditRepertoryListActivity extends NetWorkActivity{
	private final int PRODUCT_GET = 1;
	private final int PRODUCT_COMMIT = 2;

	private List<Fragment> fragments = new ArrayList<Fragment>();
	@ViewInject(R.id.tablayout)
	private TabLayout tablayout;
	@ViewInject(R.id.viewpager)
	private NoScrollViewPager viewpager;
	@ViewInject(R.id.tv_open)
	private ImageView ivOpen;

	@ViewInject(R.id.textView2)
	private TextView textView2;


	private PandianResult pandianResult;
	private int inventoryID;
	List<PandianResult.InventoryBean.LinesBean> linesBeanList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs_list_layout);
		setTitleText(true, "盘点");
		Object param = null;
		pandianResult = (PandianResult) this.getIntent().getSerializableExtra("bean");
		if (pandianResult != null) {
			textView2.setText("单号" + pandianResult.getInventory().getName());
			inventoryID = pandianResult.getInventory().getInventoryID();
			linesBeanList = pandianResult.getInventory().getLines();
		}
		else{
			CheckResult.ListBean checkBean = (CheckResult.ListBean )this.getIntent().getSerializableExtra("checkBean");
			textView2.setText("单号" + checkBean.getName());
			inventoryID = checkBean.getInventoryID();
			linesBeanList = checkBean.getLines();
		}
		for(PandianResult.InventoryBean.LinesBean bean : linesBeanList) {
			ProductBasicList.ListBean product = getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
			if( product == null) {
				product = new ProductBasicList.ListBean();
				product.setStockType("gege");
			}
			bean.setProduct(product);
		}


		GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
		getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
		sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
//		sendConnection("/gongfu/shop/inventory/"+this.getIntent().getStringExtra("id")+"/list",param,PRODUCT_GET,true, EditRepertoryResult.class);
	}

	CategoryRespone categoryRespone;


	List<Fragment> orderProductFragmentList;
	private void setUpDataForViewPage() {
		orderProductFragmentList = new ArrayList<>();
		List<Fragment> tabFragmentList = new ArrayList<>();
		List<String> titles = new ArrayList<>();
		HashMap<String, ArrayList<PandianResult.InventoryBean.LinesBean>> map = new HashMap<>();
		titles.add("全部");
		for(String category:categoryRespone.getCategoryList()){
			titles.add(category);
			map.put(category,new ArrayList<PandianResult.InventoryBean.LinesBean>());
		}
		for (PandianResult.InventoryBean.LinesBean linesBean : linesBeanList) {
			ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(EditRepertoryListActivity.this).get(String.valueOf(linesBean.getProductID()));
			if (!TextUtils.isEmpty(listBean.getCategory())){
				ArrayList<PandianResult.InventoryBean.LinesBean> linesBeen = map.get(listBean.getCategory());
				if (linesBeen == null) {
					linesBeen = new ArrayList<>();
					map.put(listBean.getCategory(), linesBeen);
				}
				linesBeen.add(linesBean);
			}
		}

		for(String category:categoryRespone.getCategoryList()){
			ArrayList<PandianResult.InventoryBean.LinesBean> value = map.get(category);
			orderProductFragmentList.add(newProductFragment(value));
			tabFragmentList.add(TabFragment.newInstance(category));
		}
		orderProductFragmentList.add(0, newProductFragment((ArrayList<PandianResult.InventoryBean.LinesBean>) linesBeanList));

		FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
		viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
		tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
		tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int position = tab.getPosition();
				viewpager.setCurrentItem(position);
				mProductTypeWindow.dismiss();
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
		if(titles.size()<=TAB_EXPAND_COUNT){
			ivOpen.setVisibility(View.GONE);
			tablayout.setTabMode(TabLayout.MODE_FIXED);
		}else{
			ivOpen.setVisibility(View.VISIBLE);
			tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		}
		initPopWindow((ArrayList<String>) titles);
	}

	public EditRepertoryListFragment newProductFragment(ArrayList<PandianResult.InventoryBean.LinesBean> value) {
		EditRepertoryListFragment editRepertoryListFragment = new EditRepertoryListFragment();
		editRepertoryListFragment.setData(value);
		return editRepertoryListFragment;
	}

	private PopupWindow mProductTypeWindow;
	ProductTypeAdapter mProductTypeAdapter;
	private void initPopWindow(ArrayList<String> typeList) {
		View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_tab_type, null);
		GridView gridView = (GridView) dialog.findViewById(R.id.gv);
		mProductTypeAdapter = new ProductTypeAdapter(typeList);
		gridView.setAdapter(mProductTypeAdapter);
		final int[] location = new int[2];
		tablayout.getLocationOnScreen(location);
		int y = (int) (location[1] + tablayout.getHeight());
		mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getScreenH(getActivityContext()) - y, true);
		mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
		mProductTypeWindow.setFocusable(false);
		mProductTypeWindow.setOutsideTouchable(false);
		mProductTypeWindow.setContentView(dialog);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mProductTypeWindow.dismiss();
				viewpager.setCurrentItem(position);
				tablayout.getTabAt(position).select();
				for (int i = 0;i < mProductTypeAdapter.selectList.size();i++){
					mProductTypeAdapter.selectList.set(i,new Boolean(false));
				}
				mProductTypeAdapter.selectList.set(position,new Boolean(true));
				mProductTypeAdapter.notifyDataSetChanged();
			}
		});
		dialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mProductTypeWindow.dismiss();
			}
		});
		mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				ivOpen.setImageResource(R.drawable.arrow);
			}
		});
	}

	private void showPopWindow(){
		final int[] location = new int[2];
		tablayout.getLocationOnScreen(location);
		int y = (int) (location[1] + tablayout.getHeight());
		mProductTypeWindow.showAtLocation(getRootView(EditRepertoryListActivity.this), Gravity.NO_GRAVITY, 0, y);
		mProductTypeAdapter.setSelectIndex(viewpager.getCurrentItem());
		ivOpen.setImageResource(R.drawable.arrow_up);
	}
	@OnClick({R.id.tv_open,R.id.title_iv_left,R.id.title_iv_left1,R.id.right_layout})
	public void OnBack(View v) {
		switch (v.getId()) {
			case R.id.tv_open:
				if (mProductTypeWindow == null){
					return;
				}
				if (!mProductTypeWindow.isShowing()){
					showPopWindow();
				}else{
					mProductTypeWindow.dismiss();
				}
				break;
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
                if(orderProductFragmentList != null && orderProductFragmentList.size()>0) {
					dialog.setModel(CustomDialog.BOTH);
					dialog.setLeftBtnListener("取消",null);
					dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
						@Override
						public void doClickButton(Button btn, CustomDialog dialog) {
							EditRequest editRequest = new EditRequest();
							editRequest.setId(inventoryID);
							editRequest.setState("done");
							List<PandianResult.InventoryBean.LinesBean> finalDataList = ((EditRepertoryListFragment)orderProductFragmentList.get(0)).getFinalDataList();
							List<EditRequest.ProductBean> editListBean = new ArrayList<>();
							for(PandianResult.InventoryBean.LinesBean bean : finalDataList) {
								if(bean.getType() == 0) {
									EditRequest.ProductBean productBean = new EditRequest.ProductBean();
									productBean.setProduct_id(bean.getProductID());
									productBean.setId(bean.getInventoryLineID());
									productBean.setActual_qty(bean.getEditNum());
									productBean.setLot_id(bean.getLotID());
									productBean.setLot_num(bean.getLotNum());
									editListBean.add(productBean);
								}
							}
							editRequest.setInventory_lines(editListBean);

//							sendConnection("/gongfu/shop/inventory/state",editRequest,PRODUCT_COMMIT,true, EditRepertoryResult.class);
							sendConnection("/api/inventory/state",editRequest,PRODUCT_COMMIT,true, EditRepertoryResult.class);
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


				break;
			case PRODUCT_COMMIT:
				ToastUtil.show(mContext,"盘点成功");
				Intent intent = new Intent(mContext,EditRepertoryFinishActivity.class);
				startActivity(intent);
				finish();
				break;
			case CATEGORY:
				BaseEntity.ResultBean resultBean1 = result.getResult();
				categoryRespone = (CategoryRespone) resultBean1.getData();
				setUpDataForViewPage();
				break;
		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}
}
