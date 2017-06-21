package com.runwise.supply.business;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.runwise.supply.R;
import com.runwise.supply.business.entity.FilterItem;
import com.kids.commonframe.base.IBaseAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;



public class RightFilterAdapter extends IBaseAdapter<FilterItem> {
	private Context mContext;
	public RightFilterAdapter(Context context) {
		this.mContext = context;
	}
    
	public void setSelect(int position) {
		for (int i=0; i<mList.size(); i++) {
			FilterItem bean = mList.get(i);
			bean.setSelect(false);
			if (i == position) {
				bean.setSelect(true);
			}
		}
		this.notifyDataSetChanged();
	}
	@Override
	protected View getExView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.right_filter_item, null);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FilterItem city = mList.get(position);
		holder.selectItemName.setText(city.getName());
		if (city.isSelect()) {
			holder.selectItemIcon.setVisibility(View.VISIBLE);
			holder.selectItemBg.setBackgroundColor(Color.parseColor("#f2f2f2"));
			holder.selectItemName.setTextColor(Color.parseColor("#00afff"));
		}
		else {
			holder.selectItemIcon.setVisibility(View.INVISIBLE);
			holder.selectItemBg.setBackgroundColor(Color.parseColor("#ffffff"));
			holder.selectItemName.setTextColor(Color.parseColor("#212121"));
		}
		return convertView;
	}

	class ViewHolder {
		@ViewInject(R.id.selectItemName)
		TextView selectItemName;
		@ViewInject(R.id.selectItemIcon)
		ImageView selectItemIcon;
		@ViewInject(R.id.selectItemBg)
		View selectItemBg;
	}
}
