package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.DataType;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mychao on 2017/7/14.
 */

public class RepertoryFragment extends NetWorkFragment {
    private final int PRODUCT_GET = 1;

    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TabPageIndicatorAdapter(this.getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        sendConnection("/gongfu/shop/stock/list",null,PRODUCT_GET,true, RepertoryEntity.class);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_repertory_layout;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
         switch (where) {
             case PRODUCT_GET:
                 RepertoryEntity repertoryEntity = (RepertoryEntity)result.getResult();
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
            titleList.add("冷藏货");
            titleList.add("冻货");
            titleList.add("干货");
            Bundle bundle = new Bundle();
            RepertoryListFragment allFragment = new RepertoryListFragment();
            allFragment.type = DataType.ALL;
            allFragment.setArguments(bundle);
            RepertoryListFragment coldFragment = new RepertoryListFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            coldFragment.setArguments(bundle);
            RepertoryListFragment freezeFragment = new RepertoryListFragment();
            freezeFragment.type = DataType.FREEZE;
            freezeFragment.setArguments(bundle);
            RepertoryListFragment dryFragment = new RepertoryListFragment();
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
