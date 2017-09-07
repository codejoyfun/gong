package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.ProductData;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

/**
 * 价目表
 */
public class PriceActivity extends NetWorkActivity {
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    private TabPageIndicatorAdapter adapter;
    private static final int REQUEST_MAIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.acticity_tabs_layout);

        this.setTitleText(true, "价目表");
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        requestData(true, REQUEST_MAIN);
    }


    public void requestData(boolean showDialog, int where) {
        PageRequest request = null;
//        request.setLimit(limit);
//        request.setPz(page);
        sendConnection("/gongfu/v2/product/list", request, where, showDialog, ProductData.class);
    }

    @OnClick({R.id.left_layout, R.id.iv_open})
    public void doBack(View view) {
        switch (view.getId()) {
            case R.id.left_layout:
                finish();
                break;
            case R.id.iv_open:
                if (mProductTypeWindow == null){
                    return;
                }
                if (!mProductTypeWindow.isShowing()){
                    showPopWindow();
                }else{
                    mProductTypeWindow.dismiss();
                }
                break;
        }

    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                ProductData mainListResult = (ProductData) result.getResult().getData();
                List<ProductData.ListBean> listBeen = mainListResult.getList();
                setUpDataForViewPage(listBeen);
                break;
        }
    }

    private void setUpDataForViewPage(List<ProductData.ListBean> listBeen) {
        List<Fragment> productDataFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("全部");

        HashMap<String, ArrayList<ProductData.ListBean>> map = new HashMap<>();
        for (ProductData.ListBean listBean : listBeen) {
            ArrayList<ProductData.ListBean> tempListBeen = map.get(listBean.getStockType());
            if (tempListBeen == null) {
                tempListBeen = new ArrayList<>();
                map.put(listBean.getStockType(), tempListBeen);
            }
            tempListBeen.add(listBean);
        }
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            ArrayList<ProductData.ListBean> value = (ArrayList<ProductData.ListBean>) entry.getValue();
            titles.add(key);
            productDataFragmentList.add(newPriceListFragment(value));
            tabFragmentList.add(TabFragment.newInstance(key));
        }
        productDataFragmentList.add(0, newPriceListFragment((ArrayList<ProductData.ListBean>) listBeen));
        initUI(titles, productDataFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    private void initUI(List<String> titles, List<Fragment> priceFragmentList) {
        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager(), titles, priceFragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(priceFragmentList.size());
        smartTabLayout.setViewPager(viewPager);
        smartTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                viewPager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
            }
        });
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
        }
        int position = this.getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position, false);
    }

    public PriceListFragment newPriceListFragment(ArrayList<ProductData.ListBean> value) {
        PriceListFragment priceListFragment = new PriceListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(OrderProductFragment.BUNDLE_KEY_LIST, value);
        priceListFragment.setArguments(bundle);
        return priceListFragment;
    }

    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewPager.setCurrentItem(position);
                smartTabLayout.getTabAt(position).setSelected(true);
                for (int i = 0; i < mProductTypeAdapter.selectList.size(); i++) {
                    mProductTypeAdapter.selectList.set(i, new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position, new Boolean(true));
                mProductTypeAdapter.notifyDataSetChanged();
            }
        });
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductTypeWindow.dismiss();
            }
        });
        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    private void showPopWindow() {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mProductTypeWindow.showAtLocation(getRootView(PriceActivity.this), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, List<String> titles, List<Fragment> priceFragmentList) {
            super(fm);
            titleList.addAll(titles);
            fragmentList.addAll(priceFragmentList);
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
