package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 价目表
 */
public class PriceActivity extends BaseActivity {
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.acticity_tabs_layout);

        this.setTitleText(true,"价目表");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        int position = this.getIntent().getIntExtra("position",0);
        viewPager.setCurrentItem(position,false);


    }
    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        finish();
    }
    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            
            titleList.add("全部");
            titleList.add("冷藏货");
            titleList.add("冻货");
            titleList.add("干货");
            Bundle bundle = new Bundle();
            PriceListFragment allFragment = new PriceListFragment();
            allFragment.type = DataType.ALL;
            allFragment.setArguments(bundle);
            PriceListFragment coldFragment = new PriceListFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            coldFragment.setArguments(bundle);
            PriceListFragment freezeFragment = new PriceListFragment();
            freezeFragment.type = DataType.FREEZE;
            freezeFragment.setArguments(bundle);
            PriceListFragment dryFragment = new PriceListFragment();
            dryFragment.type = DataType.DRY;
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
    
}
