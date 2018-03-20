package com.runwise.supply.mine;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
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
import com.runwise.supply.fragment.SearchListFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.ProcurementAddResult;
import com.runwise.supply.mine.entity.ProcurenmentAddRequest;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

public class ProcurementAddActivity extends NetWorkActivity implements LoadingLayoutInterface {
    @ViewInject(R.id.searchET)
    private EditText searchET;
    private final int PRODUCT_GET = 1;
    private final int PRODUCT_ADD_1 = 2;
    private final int PRODUCT_ADD_2 = 3;

    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;


    private List<Fragment> fragmentList = new ArrayList<>();
    private Animation topShowAnim;
    private Animation topHideAnim;

    @ViewInject(R.id.bgView)
    private View bgView;
    @ViewInject(R.id.popView1)
    private View popView1;
    @ViewInject(R.id.popView2)
    private View popView2;
    @ViewInject(R.id.addRootView)
    private View addRootView;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private TimePickerView pvCustomTime;
    private WheelView wheelView;
    private ProductData.ListBean productBean;
    private ProductData.ListBean returnBean;
    //数量
    private String amount;
    private TabPageIndicatorAdapter adapter;

    @Override
    public void retryOnClick(View view) {
        Object param = null;
        sendConnection("/gongfu/v3/product/list/", param, PRODUCT_GET, true, ProductData.class);
    }

    /**
     * 供子fragment统一设置商品数量,隐藏细节
     */
    public interface ProductCountSetter {
        void setCount(ProductData.ListBean bean, double count);

        double getCount(ProductData.ListBean bean);
    }

    protected Map<ProductData.ListBean, Double> mMapCount = new HashMap<>();

    /**
     * 供子fragment统一设置商品数量的接口，向子fragment隐藏实现
     */
    ProductCountSetter mCountSetter = new ProductCountSetter() {
        @Override
        public void setCount(ProductData.ListBean bean, double count) {
            if (count == 0) {
                mMapCount.remove(bean);
            } else {
                mMapCount.put(bean, count);
            }

        }

        @Override
        public double getCount(ProductData.ListBean bean) {
            return mMapCount.get(bean) == null ? 0 : mMapCount.get(bean);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SystemUpgradeHelper.getInstance(this).check(this)) {
            finish();
            return;
        }
        setContentView(R.layout.activity_procurement_add);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString();
                SearchKeyWork bean = new SearchKeyWork();
                bean.setKeyWork(searchText);
                EventBus.getDefault().post(bean);
            }
        });
        loadingLayout.setOnRetryClickListener(this);
        Object param = null;
        sendConnection("/gongfu/v3/product/list/", param, PRODUCT_GET, true, ProductData.class);
    }

    @OnClick({R.id.cancelBtn, R.id.tv_add})
    public void btnClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.cancelBtn:
                this.finish();
                break;
            case R.id.tv_add:
                postData();
                break;
            default:
                break;
        }
    }

    public void postData() {
        if (mMapCount.size() == 0){
            toast("你尚未添加任何自采商品!");
            return;
        }
        ProcurenmentAddRequest procurenmentAddRequest = new ProcurenmentAddRequest();
        List<ProcurenmentAddRequest.ProductsBean> products = new ArrayList<ProcurenmentAddRequest.ProductsBean>();
        for (Map.Entry<ProductData.ListBean, Double> entry : mMapCount.entrySet()) {
            ProductData.ListBean listBean = entry.getKey();
            ProcurenmentAddRequest.ProductsBean productsBean = new ProcurenmentAddRequest.ProductsBean();
            productsBean.setProduct_id(listBean.getProductID());
            productsBean.setTracking("");
            productsBean.setLot_name("none");
            productsBean.setLife_datetime("");
            productsBean.setProduce_datetime("");
            productsBean.setQty(entry.getValue());
            products.add(productsBean);
        }
        procurenmentAddRequest.setProducts(products);
        sendConnection("/gongfu/shop/zicai", procurenmentAddRequest, PRODUCT_ADD_1, true, ProcurementAddResult.class);

    }

    @OnClick({R.id.colseIcon, R.id.colseIcon1, R.id.iv_open})
    public void closeIcon(View view) {
        switch (view.getId()) {
            case R.id.colseIcon:
                setCommontTopHide();
                break;
            case R.id.colseIcon1:
                setCommontTopHide();
                break;
            case R.id.iv_open:
                if (mProductTypeWindow == null) {
                    return;
                }
                if (!mProductTypeWindow.isShowing()) {
                    showPopWindow();
                } else {
                    mProductTypeWindow.dismiss();
                }
                break;
        }

    }

    CategoryRespone categoryRespone;
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    List<ProductData.ListBean> hotList;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case PRODUCT_GET:
                ProductData ProductData = (ProductData) result.getResult().getData();
                hotList = ProductData.getList();
                GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                getCategoryRequest.setUser_id(Integer.parseInt(SampleApplicationLike.getInstance().getUid()));
                sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
                loadingLayout.onSuccess(hotList.size(),"哎呀！这里是空哒~~",R.drawable.default_ico_none);
                break;
            case PRODUCT_ADD_1:
                ProcurementAddResult procurementAddResult = (ProcurementAddResult) result.getResult();
                EventBus.getDefault().post(procurementAddResult);
                finish();
                break;
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage(hotList);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        if (where == PRODUCT_GET){
            if (errMsg.equals(getResources().getString(R.string.network_error))){
                toast(getResources().getString(R.string.network_error));
                loadingLayout.onFailure(errMsg, R.drawable.default_icon_checkconnection);
            }else{
                loadingLayout.onSuccess(0,"哎呀！这里是空哒~~",R.drawable.default_ico_none);
            }
        }else{
            ToastUtil.show(mContext, errMsg);
        }
    }

    private void setUpDataForViewPage(List<ProductData.ListBean> listBeen) {
        List<Fragment> productDataFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<ProductData.ListBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<ProductData.ListBean>());
        }
        for (ProductData.ListBean listBean : listBeen) {
            if (!TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<ProductData.ListBean> tempListBeen = map.get(listBean.getCategory());
                if (tempListBeen == null) {
                    tempListBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), tempListBeen);
                }
                tempListBeen.add(listBean);
            }
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<ProductData.ListBean> value = map.get(category);
            productDataFragmentList.add(newSearchListFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        productDataFragmentList.add(0, newSearchListFragment((ArrayList<ProductData.ListBean>) listBeen));
        initUI(titles, productDataFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    private void initUI(List<String> titles, List<Fragment> priceFragmentList) {
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager(), titles, priceFragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(titles.size());
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
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        int position = this.getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position, false);
    }

    public SearchListFragment newSearchListFragment(ArrayList<ProductData.ListBean> value) {
        SearchListFragment searchListFragment = new SearchListFragment();
        searchListFragment.type = DataType.ALL;
        searchListFragment.setData(value);
        searchListFragment.setProductCountSetter(mCountSetter);
        return searchListFragment;
    }

    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    @SuppressLint("WrongConstant")
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
        mProductTypeWindow.showAtLocation(getRootView(ProcurementAddActivity.this), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }



    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

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
            return fragmentList.size();
        }
    }

    //显示弹窗
    public void setCommontTopShow() {
        if (topShowAnim == null) {
            topShowAnim = AnimationUtils.loadAnimation(mContext, com.kids.commonframe.R.anim.show_popwindow);
        }
        if (addRootView.getVisibility() == View.GONE) {
            addRootView.startAnimation(topShowAnim);
            topShowAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    addRootView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    addRootView.setVisibility(View.VISIBLE);
                }
            });
            bgView.setVisibility(View.VISIBLE);
        }
    }

    //影藏弹窗
    public void setCommontTopHide() {
        if (topHideAnim == null) {
            topHideAnim = AnimationUtils.loadAnimation(mContext, com.kids.commonframe.R.anim.hide_popwindow);
        }
        if (addRootView.getVisibility() == View.VISIBLE) {
            topHideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    addRootView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    addRootView.setVisibility(View.GONE);
                }
            });
            addRootView.startAnimation(topHideAnim);
            bgView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("自采商品添加页");
        MobclickAgent.onResume(this);          //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("自采商品添加页");
        MobclickAgent.onPause(this);          //统计时长
    }
}
