package com.runwise.supply.mine;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.ProductData;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

/**
 * 价目表
 */
public class PriceActivity extends NetWorkActivity implements LoadingLayoutInterface {
    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private TabPageIndicatorAdapter adapter;
    private static final int REQUEST_MAIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_price);
        loadingLayout.setOnRetryClickListener(this);

        this.setTitleText(true, "价目表");
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        requestData(true, REQUEST_MAIN);
    }


    public void requestData(boolean showDialog, int where) {
        PageRequest request = null;
//        request.setLimit(limit);
//        request.setPz(page);
        sendConnection("/gongfu/v3/product/list", request, where, showDialog, ProductData.class);
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
    CategoryRespone categoryRespone;
    List<ProductData.ListBean> listBeen;
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                ProductData mainListResult = (ProductData) result.getResult().getData();
               listBeen = mainListResult.getList();
                GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                getCategoryRequest.setUser_id(Integer.parseInt(SampleApplicationLike.getInstance().getUid()));
                sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
                break;
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                loadingLayout.onSuccess(categoryRespone.getCategoryList().size(), "哎呀！这里是空哒~~", R.drawable.default_icon_ordernone);
                setUpDataForViewPage(listBeen);
                break;
        }
    }

    private void setUpDataForViewPage(List<ProductData.ListBean> listBeen) {
        List<Fragment> productDataFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<ProductData.ListBean>> map = new HashMap<>();
        titles.add("全部");
        for(String category:categoryRespone.getCategoryList()){
            titles.add(category);
            map.put(category,new ArrayList<ProductData.ListBean>());
        }

        for (ProductData.ListBean listBean : listBeen) {
            if (!TextUtils.isEmpty(listBean.getCategory())){
                ArrayList<ProductData.ListBean> tempListBeen = map.get(listBean.getCategory());
                if (tempListBeen == null) {
                    tempListBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), tempListBeen);
                }
                tempListBeen.add(listBean);
            }
        }

        for(String category:categoryRespone.getCategoryList()){
            ArrayList<ProductData.ListBean> value = map.get(category);
            productDataFragmentList.add(newPriceListFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }

        productDataFragmentList.add(0, newPriceListFragment((ArrayList<ProductData.ListBean>) listBeen));
        initPopWindow((ArrayList<String>) titles);
        initUI(titles, productDataFragmentList);
    }

    private void initUI(List<String> titles, List<Fragment> priceFragmentList) {
        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager(), titles, priceFragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(priceFragmentList.size());
        smartTabLayout.setupWithViewPager(viewPager);
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(titles.size()<=TAB_EXPAND_COUNT){
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }else{
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
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
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getScreenH(getActivityContext()) - y, true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewPager.setCurrentItem(position);
                smartTabLayout.getTabAt(position).select();
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
        if (where == REQUEST_MAIN){
            ToastUtil.show(getActivityContext(),errMsg);
            loadingLayout.onFailure(errMsg, R.drawable.default_icon_checkconnection);
        }
    }

    @Override
    public void retryOnClick(View view) {
        requestData(true, REQUEST_MAIN);
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
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("价目表");
        MobclickAgent.onResume(this);          //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("价目表");
        MobclickAgent.onPause(this);          //统计时长
    }
}
