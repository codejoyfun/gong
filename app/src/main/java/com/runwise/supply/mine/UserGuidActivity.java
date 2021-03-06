package com.runwise.supply.mine;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 新手引导页
 * */
public class UserGuidActivity extends BaseActivity {
	@ViewInject(R.id.navigation_vp)
	private ViewPager viewPager;
	private LayoutInflater inflater;
	private NavAdapter adapter;
	
	private boolean isFirst;
	private boolean isLast;
	private int currentPosition;
	private int currentPageScrollStatus;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_main);
		StatusBarUtil.StatusBarLightMode(this);

		inflater = LayoutInflater.from(this);
		List<Integer> viewList = new ArrayList<Integer>();
		viewList.add(R.drawable.restaurant_onboarding_1);
		viewList.add(R.drawable.restaurant_onboarding_2);
		viewList.add(R.drawable.restaurant_onboarding_3);
		 adapter = new NavAdapter(viewList);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentPosition = position;
				if (currentPosition == 0) {
					isFirst = true;
				}else if (currentPosition == adapter.getCount() - 1){
					isLast = true;
				}
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
	private class NavAdapter extends PagerAdapter {
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
			View mainView = inflater.inflate( R.layout.item_guid, null);
			ImageView imageView = (ImageView)mainView.findViewById(R.id.guid_bg);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			if ( arg1 == views.size()-1 ) {
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
			}
			imageView.setImageResource(views.get(arg1));
			container.addView(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
	}
}
