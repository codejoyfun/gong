package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.entity.ProcurementRequest;
import com.runwise.supply.mine.ProcurementAddActivity;
import com.runwise.supply.mine.ProcurementFragment;

import java.util.ArrayList;
import java.util.List;

public class ProcurementActivity extends NetWorkActivity {

    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurement);
        this.setTitleText(true, "自采记录" );
        showBackBtn();
        setTitleRigthIcon(true,R.drawable.nav_add);


        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
    }

    @OnClick(R.id.right_layout)
    public void rightClick(View view){
        startActivity(new Intent(ProcurementActivity.this,ProcurementAddActivity.class));
    }



    @Override
    public void onSuccess(BaseEntity result, int where) {
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<ProcurementFragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            titleList.add("全部");
            titleList.add("本周");
            titleList.add("上周");
            titleList.add("更早");
            Bundle bundle = new Bundle();
            ProcurementFragment allFragment = new ProcurementFragment();
            allFragment.type = ProcurementRequest.TYPE_ALL;
            allFragment.setArguments(bundle);
            ProcurementFragment coldFragment = new ProcurementFragment();
            coldFragment.type = ProcurementRequest.TYPE_THIS_WEEK;
            coldFragment.setArguments(bundle);
            ProcurementFragment freezeFragment = new ProcurementFragment();
            freezeFragment.type = ProcurementRequest.TYPE_LAST_WEEK;
            freezeFragment.setArguments(bundle);
            ProcurementFragment dryFragment = new ProcurementFragment();
            dryFragment.type = ProcurementRequest.TYPE_EARLIER;
            dryFragment.setArguments(bundle);

            fragmentList.add(allFragment);
            fragmentList.add(coldFragment);
            fragmentList.add(freezeFragment);
            fragmentList.add(dryFragment);
        }

        public List<ProcurementFragment> getFragmentList() {
            return fragmentList;
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
