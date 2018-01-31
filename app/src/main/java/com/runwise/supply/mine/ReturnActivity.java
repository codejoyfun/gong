package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.entity.ReturnActivityRefreshEvent;
import com.runwise.supply.mine.entity.ReturnData;
import com.runwise.supply.tools.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) smartTabLayout.getLayoutParams();
        layoutParams.setMargins(0,0,0,0);
        smartTabLayout.setLayoutParams(layoutParams);

        Object param = null;
        sendConnection("/API/v2/return_order/list",param,PRODUCT_GET,true, ReturnData.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ReturnActivityRefreshEvent returnActivityRefreshEvent) {
        Object param = null;
        sendConnection("/API/v2/return_order/list",param,PRODUCT_GET,true, ReturnData.class);
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        finish();
    }
    ReturnData repertoryEntity;
    @Override
    public void onSuccess(BaseEntity result, int where) {
      switch (where) {
          case PRODUCT_GET:
              repertoryEntity = (ReturnData)result.getResult().getData();
              adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
              viewPager.setAdapter(adapter);
              viewPager.setOffscreenPageLimit(4);
              smartTabLayout.setViewPager(viewPager);
              int position = this.getIntent().getIntExtra("position",0);
              viewPager.setCurrentItem(position,false);
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
            titleList.add("全部("+repertoryEntity.getAllList().size()+")");
            titleList.add("本周("+repertoryEntity.getThisWeekList().size()+")");
            titleList.add("上周("+repertoryEntity.getLastWeekList().size()+")");
            titleList.add("更早("+repertoryEntity.getEarlierList().size()+")");
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
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("退货记录页");
        MobclickAgent.onResume(this);          //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("退货记录页");
        MobclickAgent.onPause(this);          //统计时长
    }
}
