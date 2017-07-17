package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductListFragment;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/7/16.
 */

public class ReceiveActivity extends NetWorkActivity {
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.pbBar)
    private ProgressBar pbBar;
    @ViewInject(R.id.pbValue)
    private TextView pbValue;
    private TabPageIndicatorAdapter adapter;
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.receive_layout);
        setTitleText(true,"收货");
        setTitleLeftIcon(true,R.drawable.nav_back);
        setTitleRightText(true,"完成");
        Bundle bundle = getIntent().getExtras();
        OrderResponse.ListBean bean = bundle.getParcelable("order");
        if (bean != null && bean.getLines() != null){
            datas.addAll(bean.getLines());
        }
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        pbBar.setProgress(15);
    }
    @OnClick({R.id.title_iv_left})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
        }
    }
    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

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
            if (datas != null && datas.size() > 0){
                bundle.putParcelableArrayList("datas",datas);
            }
            ReceiveFragment allFragment = new ReceiveFragment();
            allFragment.type = DataType.ALL;
            allFragment.setArguments(bundle);
            ReceiveFragment coldFragment = new ReceiveFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            coldFragment.setArguments(bundle);
            ReceiveFragment freezeFragment = new ReceiveFragment();
            freezeFragment.type = DataType.FREEZE;
            freezeFragment.setArguments(bundle);
            ReceiveFragment dryFragment = new ReceiveFragment();
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
