package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.mine.entity.ReturnData;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ReturnActivity extends NetWorkActivity {
    private final int PRODUCT_GET = 1;

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

        this.setTitleText(true,"退货记录");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        int position = this.getIntent().getIntExtra("position",0);
        viewPager.setCurrentItem(position,false);
        Object param = null;
        sendConnection("/API/v2/return_order/list",param,PRODUCT_GET,true, ReturnData.class);
    }
    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        finish();
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
      switch (where) {
          case PRODUCT_GET:
              ReturnData repertoryEntity = (ReturnData)result.getResult().getData();
              EventBus.getDefault().post(repertoryEntity);
              break;
      }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
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
            ReturnListFragment allFragment = new ReturnListFragment();
            allFragment.orderDataType = OrderDataType.ALL;
            allFragment.setArguments(bundle);

            ReturnListFragment coldFragment = new ReturnListFragment();
            coldFragment.orderDataType = OrderDataType.BENZHOU;
            coldFragment.setArguments(bundle);

            ReturnListFragment freezeFragment = new ReturnListFragment();
            freezeFragment.orderDataType = OrderDataType.SHANGZHOU;
            freezeFragment.setArguments(bundle);

            ReturnListFragment dryFragment = new ReturnListFragment();
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
    
}
