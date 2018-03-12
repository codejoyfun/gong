package com.runwise.supply.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.runwise.supply.R;
import com.runwise.supply.fragment.OrderListFragmentV2;
import com.runwise.supply.fragment.ReturnListFragmentV2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderListActivityV2 extends NetWorkActivity {

    String[] mTitles = new String[]{"订单", "退货单"};
    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.title_layout)
    RelativeLayout mLeftLayout;
    @BindView(R.id.indicator)
    TabLayout mIndicator;
    @BindView(R.id.vp_product_fragments)
    ViewPager mVpProductFragments;

    TabPageIndicatorAdapter mTabPageIndicatorAdapter;

    public static final String INTENT_KEY_POSITION = "intent_key_position";

    public static Intent getStartIntent(int position, Context context){
        Intent intent = new Intent(context,OrderListActivityV2.class);
        intent.putExtra(INTENT_KEY_POSITION,position);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_v2);
        ButterKnife.bind(this);
        setUpViewPage();
    }

    private void setUpViewPage() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        OrderListFragmentV2 orderFragmentListV2 = new OrderListFragmentV2();
        fragments.add(orderFragmentListV2);
        ReturnListFragmentV2 returnListFragmentV2 = new ReturnListFragmentV2();
        fragments.add(returnListFragmentV2);
        mTabPageIndicatorAdapter = new TabPageIndicatorAdapter(getSupportFragmentManager(), mTitles, fragments);
        mVpProductFragments.setAdapter(mTabPageIndicatorAdapter);
        mIndicator.setupWithViewPager(mVpProductFragments);
        mVpProductFragments.setOffscreenPageLimit(mTitles.length);
        int position = getIntent().getIntExtra(INTENT_KEY_POSITION,0);
        mVpProductFragments.setCurrentItem(position);
    }


    @OnClick(R.id.title_iv_left)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    protected class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        protected String[] titleList;
        protected List<Fragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, String[] titles, List<Fragment> repertoryEntityFragmentList) {
            super(fm);
            fragmentList.addAll(repertoryEntityFragmentList);
            titleList = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList[position];
        }

        @Override
        public int getCount() {
            return titleList.length;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
//            fragmentList.get(position).onSelected();
        }
    }
}
