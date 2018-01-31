package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.tools.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends BaseActivity {
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;
    private Map<Integer,String> titleList = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.acticity_tabs_layout);

        this.setTitleText(true,"我的订单");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        titleList.put(0,"全部");
        titleList.put(1,"本周");
        titleList.put(2,"上周");
        titleList.put(3,"更早");
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) smartTabLayout.getLayoutParams();
        layoutParams.setMargins(0,0,0,0);
        smartTabLayout.setLayoutParams(layoutParams);
        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        int position = this.getIntent().getIntExtra("position",0);
        viewPager.setCurrentItem(position,false);
        smartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    public Map<Integer,String> getTitleList() {
        return titleList;
    }

    public void setTabText(int position,String text) {
        TextView textView = (TextView)  smartTabLayout.getTabAt(position);
        textView.setText(text);
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        finish();
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<>();
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            Bundle bundle = new Bundle();
            OrderListFragment allFragment = new OrderListFragment();
            allFragment.orderDataType = OrderDataType.ALL;
            allFragment.setArguments(bundle);
            OrderListFragment coldFragment = new OrderListFragment();
            coldFragment.orderDataType = OrderDataType.BENZHOU;
            coldFragment.setArguments(bundle);
            OrderListFragment freezeFragment = new OrderListFragment();
            freezeFragment.orderDataType = OrderDataType.SHANGZHOU;
            freezeFragment.setArguments(bundle);
            OrderListFragment dryFragment = new OrderListFragment();
            dryFragment.orderDataType = OrderDataType.GENGZAO;
            dryFragment.setArguments(bundle);
            fragmentList.add(allFragment);
            fragmentList.add(coldFragment);
            fragmentList.add(freezeFragment);
            fragmentList.add(dryFragment);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("订单列表页");
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("订单列表页");
    }
}
