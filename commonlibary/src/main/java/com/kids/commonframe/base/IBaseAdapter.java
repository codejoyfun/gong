package com.kids.commonframe.base;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 每一个adapter继承该适配器
 */
public abstract class IBaseAdapter<T> extends BaseAdapter {

	protected List<T> mList = new LinkedList<T>();
	public IBaseAdapter() {
	}

	public List<T> getList() {
		return mList;
	}
	
	public void appendData(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(list);
		notifyDataSetChanged();
	}

	public void setData(List<T> list) {
		if (list == null) {
			return;
		}
		mList.clear();
		mList.addAll(list);
		onSetData();
		notifyDataSetChanged();
	}

	/**
	 * 添加数据需要进行其他操作
	 */
	public void onSetData(){

	}
	public void removeList(List<T> list){
		if ( list == null ) {
			return;
		}
		mList.removeAll(list);
		notifyDataSetChanged();
	}

	public void appendToTop(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(0, list);
		notifyDataSetChanged();
	}

	public void appendToTop(T t) {
		if (t == null) {
			return;
		}
		mList.add(0, t);
		notifyDataSetChanged();
	}

	public void append(T t) {
		if (t == null) {
			return;
		}
		mList.add(t);
		notifyDataSetChanged();
	}

	public void removeToList(int positon) {
		if (mList.size() <= positon) {
			return;
		}
		mList.remove(positon);
		notifyDataSetChanged();
	}

	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position > mList.size() - 1) {
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = getExView(position, convertView, parent);
		// 绑定内部点击监听
		addInternalClickListener(convertView, position, mList.get(position));
		return convertView;
	}

	// adapter中的内部点击事件
	public Map<Integer, onInternalClickListener> canClickItem;

	private void addInternalClickListener(final View itemV,
			final Integer position, final Object valuesMap) {
		if (canClickItem != null) {
			for (Integer key : canClickItem.keySet()) {
				View inView = itemV.findViewById(key);
				final onInternalClickListener inviewListener = canClickItem
						.get(key);
				if (inView != null && inviewListener != null) {
					inView.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							inviewListener.OnClickListener(itemV, v, position,
									valuesMap);
						}
					});
				}
			}
		}
	}

	// 设置每个item内部的触发事件监听
	public void setOnInViewClickListener(Integer key,
			onInternalClickListener onClickListener) {
		if (canClickItem == null)
			canClickItem = new HashMap<Integer, onInternalClickListener>();
			canClickItem.put(key, onClickListener);
	}
	public interface onInternalClickListener {
		public void OnClickListener(View parentV, View v, Integer position,
									Object values);
	}

	protected abstract View getExView(int position, View convertView,
			ViewGroup parent);

}
