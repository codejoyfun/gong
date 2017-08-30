package com.runwise.supply.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListView extends ListView {

	private float mDownX;
	private float mDownY;
	public boolean isFirstItem = false;
	public boolean isLastItem = false;

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = ev.getX();
			mDownY = ev.getY();
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(ev.getX() - mDownX) > Math.abs(ev.getY() - mDownY)
					|| (isFirstItem && ev.getY() > mDownY)
					|| (isLastItem && ev.getY() < mDownY)) {
				getParent().requestDisallowInterceptTouchEvent(false);
			} else {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// Log.e("info",
		// "----onScrollChanged----:l"+l+";t:"+t+";oldl:"+oldl+";oldt:"+oldt );
		if (t == 0) {
			getParent().requestDisallowInterceptTouchEvent(true);
		} else {
			getParent().requestDisallowInterceptTouchEvent(false);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
