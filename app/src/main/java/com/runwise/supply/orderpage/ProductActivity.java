package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.ProductCountChangeEvent;
import com.kids.commonframe.base.bean.ProductGetEvent;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by libin on 2017/7/3.
 */

public class ProductActivity extends NetWorkActivity {
    //数据获取
    private static final int PRODUCT_GET = 1;
    @ViewInject(R.id.editText)
    private EditText editText;
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;
    private ArrayList<ProductData.ListBean> dataList = new ArrayList<>();

    public ArrayList<ProductData.ListBean> getDataList() {
        return dataList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StatusBarUtil.StatusBarLightMode(this);
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.product_layout);
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //通知子fragment更新list产品数量
                EventBus.getDefault().post(new ProductCountChangeEvent());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (TextUtils.isEmpty(s.toString())){
//                    //清空了，显示之前的全部
//                    EventBus.getDefault().post(new ProductGetEvent());
//                }else{
//                    //搜索基本商品信息过滤
//                    ProductQueryEvent event = new ProductQueryEvent(s.toString());
//                    EventBus.getDefault().post(event);
//                }
                //搜索基本商品信息过滤
                    ProductQueryEvent event = new ProductQueryEvent(s.toString());
                    EventBus.getDefault().post(event);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendRequest();
    }
    @OnClick(R.id.title_iv_left)
    public void btnClick(View view){
        switch(view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
            default:
                break;
        }
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
                if (products != null && products.getList() != null){
                    dataList.clear();
                    dataList.addAll(products.getList());
                    //传给子Fragment
                    ProductGetEvent event = new ProductGetEvent();
                    EventBus.getDefault().post(event);
                }
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
            allFragment.type = DataType.ALL;
            ProductListFragment coldFragment = new ProductListFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            ProductListFragment freezeFragment = new ProductListFragment();
            freezeFragment.type = DataType.FREEZE;
            ProductListFragment dryFragment = new ProductListFragment();
            dryFragment.type = DataType.DRY;
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
