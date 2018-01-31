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
import com.runwise.supply.tools.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class CheckActivity extends BaseActivity {
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

        this.setTitleText(true,"盘点记录");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) smartTabLayout.getLayoutParams();
        layoutParams.setMargins(0,0,0,0);
        smartTabLayout.setLayoutParams(layoutParams);


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
            titleList.add("本周");
            titleList.add("上周");
            titleList.add("更早");
            Bundle bundle = new Bundle();
            CheckListFragment allFragment = new CheckListFragment();
            allFragment.orderDataType = OrderDataType.ALL;
            allFragment.setArguments(bundle);

            CheckListFragment coldFragment = new CheckListFragment();
            coldFragment.orderDataType = OrderDataType.BENZHOU;
            coldFragment.setArguments(bundle);

            CheckListFragment freezeFragment = new CheckListFragment();
            freezeFragment.orderDataType = OrderDataType.SHANGZHOU;
            freezeFragment.setArguments(bundle);

//            CheckListFragment yueFragment = new CheckListFragment();
//            yueFragment.orderDataType = OrderDataType.SHANGYUE;
//            yueFragment.setArguments(bundle);

            CheckListFragment dryFragment = new CheckListFragment();
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
        MobclickAgent.onPageStart("盘点记录页");
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("盘点记录页");
    }
}
