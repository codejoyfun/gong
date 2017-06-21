package com.kids.commonframe.base.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.R;
import com.kids.commonframe.base.view.widget.AbstractWheelTextAdapter;
import com.kids.commonframe.base.view.widget.OnWheelScrollListener;
import com.kids.commonframe.base.view.widget.PickerClickListener;
import com.kids.commonframe.base.view.widget.WheelView;

import java.util.List;


/**
 * 单行选择器
 */
public class CustomSelectDialog extends Dialog {
	private TextView ok;
	private PickerClickListener listener;
	private List<String> dataList;
	private WheelView wheelView;
	public CustomSelectDialog(Context context , final List<String> itemsList) {
		super(context, R.style.DialogNoBg);
		dataList = itemsList;
		Window window = this.getWindow();
		window.setWindowAnimations(R.style.MyPopwindow_anim_style);
		setContentView(R.layout.select_picker);
		window.getAttributes().gravity = Gravity.BOTTOM;
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setCancelable(true);

		wheelView = (WheelView) findViewById(R.id.select_sheel_view);
		final DataAdapter wheelAdapter = new DataAdapter(context);
		wheelView.setViewAdapter(wheelAdapter);
		wheelView.setWheelBackground(R.color.white);
		wheelView.setDrawShadows(false);
		wheelView.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = wheelAdapter.getItemText(wheel.getCurrentItem()).toString();
				LinearLayout itemLaout = wheelView.getItemsLayout();
				for (int i=0; i< itemLaout.getChildCount(); i++) {
					View child = itemLaout.getChildAt(i);
					if ( child instanceof  TextView) {			
						TextView item = (TextView) child;
						if (currentText.equals(item.getText())) {
							item.setTextColor(wheelAdapter.getSelectColor());
						}
						else {
							item.setTextColor(wheelAdapter.getUnSelectColor());
						}
					}
				}
			}
		});
		TextView channce = (TextView) this.findViewById(R.id.picker_channce);
		ok = (TextView) this.findViewById(R.id.picker_ok);
		channce.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomSelectDialog.this.dismiss();
			}
		});
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					int position = 	wheelView.getCurrentItem();
					if(dataList != null && dataList.size() > 0){
						listener.doPickClick(dataList.get(position) ,position);
					}
				}
				CustomSelectDialog.this.dismiss();
			}
		});
	}
	//TOD设置选中项
	public void setCurrentItem(int index){
		wheelView.setCurrentItem(index);
	}

	public void addPickerListener (String text,final PickerClickListener listener) {
		ok.setText(text);
		this.listener = listener;
	}

	private class DataAdapter extends AbstractWheelTextAdapter {
		protected DataAdapter(Context context) {
			super(context, R.layout.wheel_item_layout, NO_RESOURCE);
			setItemTextResource(R.id.item_name);
		}
		@Override
		public View getItem(int index, View cachedView, ViewGroup parent, int currentItem) {
			View view = super.getItem(index, cachedView, parent,currentItem);
			return view;
		}
		@Override
		public int getItemsCount() {
			return dataList.size();
		}
		@Override
		protected CharSequence getItemText(int index) {
			return dataList.get(index);
		}
		@Override
		public int getSelectColor() {
			return Color.parseColor("#333333");
		}
		@Override
		public int getUnSelectColor() {
			return Color.parseColor("#d9d9d9");
		}
	}
}
