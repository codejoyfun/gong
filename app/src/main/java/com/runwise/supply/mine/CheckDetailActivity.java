package com.runwise.supply.mine;

import android.graphics.Color;
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
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 盘点记录详情
 */
public class CheckDetailActivity extends BaseActivity {
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;

    @ViewInject(R.id.text1)
    private TextView text1;
    @ViewInject(R.id.text2)
    private TextView text2;
    @ViewInject(R.id.text3)
    private TextView text3;
    @ViewInject(R.id.text4)
    private TextView text4;
    @ViewInject(R.id.text5)
    private TextView text5;
    private CheckResult.ListBean bean;

    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.acticity_checkdetail_layout);
        if (GlobalApplication.getInstance().getCanSeePrice()) {
            text4.setText("盘点结果(元)");
        }
        else{
            text4.setText("盘点结果(件)");
        }

        this.setTitleText(true,"盘点记录详情");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        bean = (CheckResult.ListBean)this.getIntent().getSerializableExtra("bean");
        for(PandianResult.InventoryBean.LinesBean lines: bean.getLines()) {
            ProductBasicList.ListBean product = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(lines.getProductID()));
            if( product == null) {
                product = new ProductBasicList.ListBean();
                product.setStockType("gege");
            }
            lines.setProduct(product);
        }

        titleList.add("全部");
        titleList.add("冷藏货");
        titleList.add("冻货");
        titleList.add("干货");
        CheckDetailListFragment allFragment = new CheckDetailListFragment();
        allFragment.type = DataType.ALL;
        allFragment.setData(bean);

        CheckDetailListFragment coldFragment = new CheckDetailListFragment();
        coldFragment.type = DataType.LENGCANGHUO;
        coldFragment.setData(bean);

        CheckDetailListFragment freezeFragment = new CheckDetailListFragment();
        freezeFragment.type = DataType.FREEZE;
        freezeFragment.setData(bean);

        CheckDetailListFragment dryFragment = new CheckDetailListFragment();
        dryFragment.type = DataType.DRY;
        dryFragment.setData(bean);

        fragmentList.add(allFragment);
        fragmentList.add(coldFragment);
        fragmentList.add(freezeFragment);
        fragmentList.add(dryFragment);

        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        int position = this.getIntent().getIntExtra("position",0);
        viewPager.setCurrentItem(position,false);

        text1.setText("盘点人员："+bean.getCreateUser());
        text2.setText("盘点单号："+bean.getName());
        text3.setText("盘点日期："+TimeUtils.getTimeStamps3(bean.getCreateDate()));
        if(GlobalApplication.getInstance().getCanSeePrice()) {
            text5.setText("¥"+bean.getValue()+"");
            if(bean.getValue() >= 0) {
                text5.setTextColor(Color.parseColor("#9cb62e"));
            }
            else{
                text5.setTextColor(Color.parseColor("#e75967"));
            }
        }
        else{
            text5.setText(bean.getNum()+"");
            if(bean.getNum() >= 0) {
                text5.setTextColor(Color.parseColor("#9cb62e"));
            }
            else{
                text5.setTextColor(Color.parseColor("#e75967"));
            }
        }
        if ("confirm".equals(bean.getState())) {
            text5.setTextColor(Color.parseColor("#999999"));
            text5.setText("--");
        }
    }
    public CheckResult.ListBean getDataBean() {
        return bean;
    }
    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        finish();
    }
    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            

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
