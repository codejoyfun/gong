package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.SendType;

import java.util.ArrayList;
import java.util.List;


/**
 * 种类
 */
public class NotiySettingTypeActivity extends NetWorkActivity implements AdapterView.OnItemClickListener{
	private final int REQUEST_UPDATE_SEX = 1 ;
	@ViewInject(R.id.typeListView)
	private ListView typeListView;
	private TypeListAdapter typeListAdapter;
	private SendType sendType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitvity_type_list);
		typeListAdapter = new TypeListAdapter();
		typeListView.setAdapter(typeListAdapter);
		typeListView.setOnItemClickListener(this);
		sendType = getIntent().getParcelableExtra("bean");
		this.setTitleLeftIcon(true, R.drawable.back_btn);
		this.setTitleText(true, "种类");
//		sendConnection("/Appapi/circle/getTypeList",null,REQUEST_UPDATE_SEX,true,SendTypeList.class);

		List<SendType> dataList = new ArrayList<>();
		SendType type1 = new SendType();
		type1.setId("0");
		type1.setName("日订");
		type1.setSelect(true);

		SendType type2 = new SendType();
		type2.setId("1");
		type2.setName("周订-4天");
		type2.setSelect(false);

		SendType type3 = new SendType();
		type3.setId("2");
		type3.setName("周订-8天");
		type3.setSelect(false);

		SendType type4 = new SendType();
		type4.setId("3");
		type4.setName("周订-12天");
		type4.setSelect(false);

		SendType type5 = new SendType();
		type5.setId("4");
		type5.setName("月订");
		type5.setSelect(false);

		dataList.add(type1);
		dataList.add(type2);
		dataList.add(type3);
		dataList.add(type4);
		dataList.add(type5);
		typeListAdapter.setData(dataList);
	}
	@OnClick({R.id.left_layout})
	public void onHandlerClick(View view) {
		switch (view.getId()) {
			case R.id.left_layout:
				finish();
				break;
		}
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
		case REQUEST_UPDATE_SEX:
//			if( sendType != null) {
//				for (SendType bean: sendTypeList.getData()) {
//					if(sendType.getId().equals(bean.getId())) {
//						bean.setSelect(true);
//						break;
//					}
//				}
//			}
//			else{
//				sendTypeList.getData().get(0).setSelect(true);
//			}

			break;
		default:
			break;
		}
	}
	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(this,errMsg);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SendType bean = (SendType)parent.getAdapter().getItem(position);
		Intent intent = new Intent();
		intent.putExtra("bean", bean);
		setResult(RESULT_OK, intent);
		finish();
	}


	public class TypeListAdapter extends IBaseAdapter<SendType> {
		public void setSelect(int position) {
			for (int i=0; i<mList.size(); i++) {
				SendType bean = mList.get(i);
				bean.setSelect(false);
				if (i == position) {
					bean.setSelect(true);
				}
			}
			this.notifyDataSetChanged();
		}
		@Override
		protected View getExView(int position, View convertView,
								 ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_type, null);
				holder = new ViewHolder();
				ViewUtils.inject(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			SendType bean = mList.get(position);
			holder.typeName.setText(bean.getName());
			if(bean.isSelect()) {
				holder.typeIcon.setVisibility(View.VISIBLE);
			}
			else {
				holder.typeIcon.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		class ViewHolder {
			@ViewInject(R.id.typeName)
			TextView typeName;
			@ViewInject(R.id.typeIcon)
			ImageView typeIcon;
		}

	}
}
