package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by libin on 2017/7/3.
 */

public class ProductActivity extends NetWorkActivity {
    //数据获取
    private static final int PRODUCT_GET = 1;
    //数据查询
    private static final int PRODUCT_QUERY = 2;

    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StatusBarUtil.StatusBarLightMode(this);
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.product_layout);
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
        sendRequest();
    }
    private void sendRequest() {
        ///gongfu/v3/shop/product/list
        Object request = null;
        sendConnection("/gongfu/v3/shop/product/list",request,PRODUCT_GET,true, ProductData.class);
    }
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case PRODUCT_GET:
                BaseEntity.ResultBean resultBean= result.getResult();
                ProductData products= (ProductData) resultBean.getData();

                break;
            case PRODUCT_QUERY:
                break;
            default:
                break;
        }

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
            ProductListFragment allFragment = new ProductListFragment();
            ProductListFragment coldFragment = new ProductListFragment();
            ProductListFragment freezeFragment = new ProductListFragment();
            ProductListFragment dryFragment = new ProductListFragment();
            Bundle bundle = new Bundle();
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
        public int getCount() {
            return titleList.size();
        }
    }
}
