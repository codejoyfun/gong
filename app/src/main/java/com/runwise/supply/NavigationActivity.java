package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * 新手引导页
 * */
public class NavigationActivity extends BaseActivity {
	@ViewInject(R.id.bgIv)
	private ImageView bgIv;
	@ViewInject(R.id.navigation_vp)
	private ViewPager viewPager;
	private LayoutInflater inflater;
	private NavAdapter adapter;
	
	private boolean isFirst;
	private boolean isLast;
	private int currentPosition;
	private int currentPageScrollStatus;
	private boolean isFromLeft = true;			//默认朝右滑
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_main);
		 inflater = LayoutInflater.from(this);
		final List<Integer> viewList = new ArrayList<Integer>();
		viewList.add(R.drawable.guidepage_1);
		viewList.add(R.drawable.guidepage_2);
		viewList.add(R.drawable.guidepage_3);
		bgIv.setImageResource(R.drawable.guidepage_1);
		 adapter = new NavAdapter(viewList);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
//				adapter.animateView();
				if (position == 0) {
					isFirst = true;
				}else if (position == adapter.getCount() - 1){
					isLast = true;
				}
				if (viewList.size() > position){
					ViewHelper.setAlpha(bgIv,0.5f);
					ViewPropertyAnimator.animate(bgIv).alpha(1).setDuration(700);
					bgIv.setImageResource(viewList.get(position));
				}
				currentPosition = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (currentPosition == 0) {
					//如果offsetPixels是0页面也被滑动了，代表在第一页还要往左划
					if (positionOffsetPixels == 0 && currentPageScrollStatus == 1){
						if(isFirst){
							isFirst = false;
						}
					}
				} else if (currentPosition == adapter.getCount() - 1){
					//已经在最后一页还想往右划
					if (positionOffsetPixels == 0 && currentPageScrollStatus == 1){
						if(isLast){
							isLast = false;
//							NavigationActivity.this.finish();
						}
					}
				}
				if (positionOffsetPixels < 0){
					isFromLeft = false;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				//记录page滑动状态，如果滑动了state就是1
				currentPageScrollStatus = state;
				if (currentPosition == 0 && currentPageScrollStatus == 1) {
					isFirst = true;
				}else if (currentPosition == adapter.getCount() - 1 && currentPageScrollStatus == 1){
					isLast = true;
				}
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			gotoMain();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void gotoMain() {
//		if (SPUtils.isLogin(mContext)) {
			startActivity(new Intent(mContext, MainActivity.class));
//		} else {
//			startActivity(new Intent(mContext, LoginActivity.class));
//		}
		finish();
	}
	private class NavAdapter extends PagerAdapter {
		TextView guidTitle;
		TextView guidSubTitle;
		private List<Integer> views = new ArrayList<Integer>();
		public NavAdapter(List<Integer> views ) {
			this.views = views;
		}
		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int arg1) {
			View mainView = inflater.inflate( R.layout.item_navigation, null);
			ImageView imageView = (ImageView)mainView.findViewById(R.id.guid_bg);
			TextView go = (TextView) mainView.findViewById(R.id.guid_go);
			guidTitle = (TextView) mainView.findViewById(R.id.guidTitle);
			guidSubTitle = (TextView) mainView.findViewById(R.id.guidSubTitle);
			if ( arg1 == views.size()-1 ) {
				go.setVisibility(View.VISIBLE);
				go.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						gotoMain();
					}
				});
			}
			else {
				go.setVisibility(View.GONE);
			}
			switch (arg1) {
				case 0:
					guidTitle.setText("集中采购食材");
					guidSubTitle.setText("质高价低");
					break;
				case 1:
					guidTitle.setText("中央厨房加工");
					guidSubTitle.setText("卫生安全");
					break;
				case 2:
					guidTitle.setText("统一配送到店");
					guidSubTitle.setText("稳定高效");
					break;
			}
//			imageView.setImageResource(views.get(arg1));
			container.addView(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			animateView();
			return mainView;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
		@Override
		public void finishUpdate(View arg0) {

		}
		public void animateView(){
			int toX = (CommonUtils.getScreenWidth(NavigationActivity.this) - CommonUtils.dip2px(mContext,100))/2;
			int toX2 = (CommonUtils.getScreenWidth(NavigationActivity.this) - CommonUtils.dip2px(mContext,200))/2;
			if (guidSubTitle != null && guidTitle != null){
				ObjectAnimator.ofFloat(guidSubTitle,"translationX",0F,toX).setDuration(600).start();
				ObjectAnimator.ofFloat(guidTitle,"translationX",0F,toX2).setDuration(400).start();
			}
			

		}
	}
}
