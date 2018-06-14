package com.runwise.supply.mine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.SelfOrderTimeStatisticsUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.entity.CartCache;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.ProductListResponse;
import com.runwise.supply.entity.ProductValidateRequest;
import com.runwise.supply.entity.ProductValidateResponse;
import com.runwise.supply.entity.ProductVersionRequest;
import com.runwise.supply.entity.StockProductListResponse;
import com.runwise.supply.entity.SubmitTransferoutResponse;
import com.runwise.supply.entity.TransferoutSubmitRequest;
import com.runwise.supply.event.TransferoutProductCountUpdateEvent;
import com.runwise.supply.fragment.TransferoutProductSearchFragment;
import com.runwise.supply.orderpage.CartManager;
import com.runwise.supply.orderpage.OrderSubmitActivity;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.TransferOutSuccessActivity;
import com.runwise.supply.orderpage.TransferoutProductCategoryFragment;
import com.runwise.supply.orderpage.ProductSearchFragment;
import com.runwise.supply.orderpage.TransferProductValueDialog;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.TransferOutRequest;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.LongPressUtil;
import com.runwise.supply.tools.MyDbUtil;
import com.runwise.supply.tools.RunwiseService;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.ProductTypePopup;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.vov.vitamio.utils.NumberUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_PRODUCT_CATEGORY_LIST;
import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_VERSION_PRODUCT_LIST;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_CLICK_PRODUCT_CATEGORY;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_CONTINUE_TO_CHOOSE;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_SHOPPING_CART;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_XUAN_HAO_L;
import static com.kids.commonframe.base.util.UmengUtil.KEY_CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.orderpage.OrderSubmitActivity.INTENT_KEY_PLACE_ORDER_TYPE;
import static com.runwise.supply.orderpage.OrderSubmitActivity.INTENT_KEY_PRODUCTS;
import static com.runwise.supply.orderpage.OrderSubmitActivity.INTENT_KEY_SELF_HELP;
import static com.runwise.supply.orderpage.TransferoutProductCategoryFragment.INTENT_KEY_CATEGORY;
import static com.runwise.supply.orderpage.TransferoutProductCategoryFragment.INTENT_KEY_FIRST;
import static com.runwise.supply.tools.RunwiseService.INTENT_KEY_STATUS;

public class TransferoutProductListActivity extends NetWorkActivity implements View.OnClickListener, LoadingLayoutInterface {
    public static final String INTENT_KEY_ADDED_PRODUCTS = "ap_added_products";
    //商品数据获取
    protected static final int REQUEST_CATEGORY = 1;
    //检查购物车有效性
    protected static final int REQUEST_VALIDATE = 2;
    private static final int REQUEST_CODE_STOCK_PRODUCT_LIST = 1 << 3;
    private static final int REQUEST_CODE_SUBMIT_STOCK_PRODUCT_LIST = 1 << 4;

    @ViewInject(R.id.indicator)
    protected TabLayout smartTabLayout;
    @ViewInject(R.id.iv_open)
    protected ImageView ivOpen;
    @ViewInject(R.id.vp_product_fragments)
    protected ViewPager mViewPagerCategoryFrags;
    @ViewInject(R.id.iv_product_cart)
    protected ImageView mIvCart;//购物车图标
    @ViewInject(R.id.tv_order_commit)
    protected TextView mTvOrderCommit;//选好了
    @ViewInject(R.id.tv_product_total_count)
    protected TextView mTvProductTotalCount;
    protected TabPageIndicatorAdapter mAdapterVp;
    @ViewInject(R.id.rl_cart_container)
    protected RelativeLayout mRlCartContainer;
    @ViewInject(R.id.loadingLayout)
    protected LoadingLayout loadingLayout;

    protected ArrayList<AddedProduct> addedPros;       //从前面页面传来的数组。
    protected ProductTypePopup mTypeWindow;//商品类型弹出框


    public static final String INTENT_KEY_BACKAP = "backap";

    protected Map<StockProductListResponse.ListBean, Double> mMapCount = new HashMap<>();
    protected Map<StockProductListResponse.ListBean, String> mMapRemarks = new HashMap<>();

    DecimalFormat df = new DecimalFormat("#.##");

    protected boolean mFirstGetShopCartCache = true;

    protected int mPlaceOrderType = PLACE_ORDER_TYPE_SELF;
    public static final int PLACE_ORDER_TYPE_ALWAYS = 1 << 0;
    public static final int PLACE_ORDER_TYPE_SELF = 1 << 1;
    public static final int PLACE_ORDER_TYPE_SMART = 1 << 2;
    public static final int PLACE_ORDER_TYPE_AGAIN = 1 << 3;
    public static final int PLACE_ORDER_TYPE_MODIFY = 1 << 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setStatusBarEnabled();
        setContentView(R.layout.activity_transferout_product_list);
        init();
        setTitleText(true, "出库");
        showBackBtn();
        setTitleRightIcon2(true, R.drawable.ic_nav_search);
        //获取上一个页面传来的Parcelable
        addedPros = getIntent().getParcelableArrayListExtra(INTENT_KEY_ADDED_PRODUCTS);
        loadingLayout = (LoadingLayout) findViewById(R.id.loadingLayout);
        loadingLayout.setOnRetryClickListener(this);
        requestProductList();
    }

    public void requestProductList() {
        Object o = null;
        netWorkHelper.sendConnection("/api/stock/product/list", o, REQUEST_CODE_STOCK_PRODUCT_LIST, true, StockProductListResponse.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 在onCreate最后被调用
     */
    protected void startRequest() {
        //查询类别
        requestCategory();
    }

    /**
     * 获取缓存
     * 子类需要重写，否则会使用购物车的缓存
     */
    protected void getCache() {
        initChildBadges();
    }

    public HashMap<String, Long> getChildBadges() {
        return mChildBadges;
    }

    HashMap<String, Long> mChildBadges;

    public void initChildBadges() {
        mChildBadges = new HashMap<>();
        for (StockProductListResponse.ListBean listBean : mMapCount.keySet()) {
            double productCount = mMapCount.get(listBean);
            if (productCount == 0) {
                continue;
            }
            String key = listBean.getCategoryParent() + "&" + listBean.getCategoryChild();
            Long count = mChildBadges.get(key);
            if (count == null) {
                mChildBadges.put(key, 1L);
            } else {
                mChildBadges.put(key, count + 1);
            }
        }
        setupTabIconsNum();
    }


    List<StockProductListResponse.ListBean> mListToCheck;//记录需要查询有效性的商品，用于设置结果


    @Override
    public void onStop() {
        super.onStop();
    }


    HashMap<String, List<StockProductListResponse.ListBean>> mProductMap;

    public HashMap<String, List<StockProductListResponse.ListBean>> getProductMap() {
        return mProductMap;
    }

    public List<StockProductListResponse.ListBean> getListBeans() {
        return mListBeans;
    }

    protected List<StockProductListResponse.ListBean> mListBeans;

    /**
     * 查询类别
     */
    protected void requestCategory() {
        ///gongfu/v3/shop/product/list
        mProductMap = new HashMap<>();
        getCache();//获取缓存
        updateBottomBar();//更新底部bar
        for (int i = 0; i < mListBeans.size(); i++) {
            StockProductListResponse.ListBean bean = mListBeans.get(i);
            String categoryParent = bean.getCategoryParent();
            if (TextUtils.isEmpty(categoryParent)) {
                categoryParent = "其他";
                bean.setCategoryParent("其他");
            }


            List<StockProductListResponse.ListBean> beanList;
            if (mProductMap.containsKey(categoryParent)) {
                beanList = mProductMap.get(categoryParent);
                beanList.add(bean);
            } else {
                beanList = new ArrayList<>();
                beanList.add(bean);
                mProductMap.put(categoryParent, beanList);
            }
        }

        loadingLayout.onSuccess(mStockProductListResponse.getTypes().size(), "哎呀！这里是空哒~~", R.drawable.default_icon_goodsnone);
        setupViewPager();
    }

    ArrayList<String> mTitles;

    protected void setupViewPager() {
        List<TransferoutProductCategoryFragment> categoryFragmentList = new ArrayList<>();
        mTitles = new ArrayList<>();
        for (StockProductListResponse.CategoryBean category : mStockProductListResponse.getTypes()) {
            mTitles.add(category.getCategoryParent().getName());
            categoryFragmentList.add(newCategoryFragment(category));
        }
        categoryFragmentList.get(0).getArguments().putBoolean(INTENT_KEY_FIRST, true);
        initUI(mTitles, categoryFragmentList);
        initPopWindow((ArrayList<String>) mTitles);
    }

    /**
     * 新建一级类别fragment
     *
     * @param category
     * @return
     */
    protected TransferoutProductCategoryFragment newCategoryFragment(StockProductListResponse.CategoryBean category) {
        TransferoutProductCategoryFragment TransferoutProductCategoryFragment = new TransferoutProductCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_CATEGORY, category);
        TransferoutProductCategoryFragment.setArguments(bundle);
        return TransferoutProductCategoryFragment;
    }

    /**
     * 供子fragment共享设置商品数量
     */
//    public Map<StockProductListResponse.ListBean,Integer> getCountMap(){
//        return mMapCount;
//    }
    protected void initPopWindow(ArrayList<String> typeList) {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow = new ProductTypePopup(this,
                MATCH_PARENT,
                DensityUtil.getScreenH(getActivityContext()) - y,
                typeList, 0);
        mTypeWindow.setViewPager(mViewPagerCategoryFrags);
        mTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    protected void showPopWindow() {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow.showAtLocation(getRootView(this), Gravity.NO_GRAVITY, 0, y);
        mTypeWindow.setSelect(mViewPagerCategoryFrags.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    protected void initUI(List<String> titles, List<TransferoutProductCategoryFragment> repertoryEntityFragmentList) {
        if (mViewPagerCategoryFrags == null) {
            return;
        }
        mAdapterVp = new TabPageIndicatorAdapter(getSupportFragmentManager(), titles, repertoryEntityFragmentList);
        mViewPagerCategoryFrags.setAdapter(mAdapterVp);
        smartTabLayout.setupWithViewPager(mViewPagerCategoryFrags);
        mViewPagerCategoryFrags.setOffscreenPageLimit(titles.size());
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTabSelect(tab);
                int position = tab.getPosition();
                mViewPagerCategoryFrags.setCurrentItem(position);
                repertoryEntityFragmentList.get(position).setUserVisibleHint(true);
                mTypeWindow.dismiss();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_CATEGORY, titles.get(position));
                MobclickAgent.onEvent(getActivityContext(), EVENT_ID_CLICK_PRODUCT_CATEGORY, map);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();
        changeTabSelect(smartTabLayout.getTabAt(0));
        //统一不不显示下拉
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        setupTabIconsNum();
    }

    HashMap<String, Long> mBadges;

    private void setupTabIconsNum() {
        if (mChildBadges == null || mTitles == null) {
            return;
        }
        mBadges = new HashMap<>();
        Long totalCount = 0L;
        Iterator iter = mChildBadges.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            Long val = (Long) entry.getValue();
            totalCount += val;
            String[] keys = key.split("&");
            if (keys.length == 0) {
                continue;
            }
            key = keys[0];
            if (mBadges.get(key) == null) {
                mBadges.put(key, val);
            } else {
                mBadges.put(key, mBadges.get(key) + val);
            }
        }
//        mBadges.put("全部", totalCount);
        Long totalProductTagCount = 0L;
//        for (StockProductListResponse.ListBean listBean : mMapCount.keySet()) {
//            if (listBean.isInvalid()) {
//                continue;
//            }
//            if (listBean.getProductTag().equals("促销商品")){
//                totalProductTagCount++;
//            }
//        }
        mBadges.put("促销", totalProductTagCount);

        for (int i = 0; i < mTitles.size(); i++) {
            TextView itemBadge = (TextView) smartTabLayout.getTabAt(i).getCustomView().findViewById(R.id.item_badge);
            if (mBadges.containsKey(mTitles.get(i)) && mBadges.get(mTitles.get(i)) > 0) {
                itemBadge.setVisibility(View.VISIBLE);
                itemBadge.setText(String.valueOf(mBadges.get(mTitles.get(i))));
            } else {
                itemBadge.setVisibility(View.INVISIBLE);
            }

        }
    }

    private void setupTabIcons() {
        for (int i = 0; i < mTitles.size(); i++) {
            smartTabLayout.getTabAt(i).setCustomView(getTabView(i));
        }
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_category, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        TextView txt_title = (TextView) view.findViewById(R.id.tv_name);
        txt_title.setText(mTitles.get(position));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerCategoryFrags.setCurrentItem(position);
            }
        });
        return view;
    }

    private void changeTabSelect(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.tv_name);
        txt_title.setTextColor(Color.parseColor("#6BB400"));
    }

    private void changeTabNormal(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.tv_name);
        txt_title.setTextColor(Color.BLACK);
    }

    /**
     * 继承的话ViewInject不会inject父类的。。。？
     */
    public void init() {
        smartTabLayout = (TabLayout) findViewById(R.id.indicator);
        ivOpen = (ImageView) findViewById(R.id.iv_open);
        mViewPagerCategoryFrags = (ViewPager) findViewById(R.id.vp_product_fragments);
        mIvCart = (ImageView) findViewById(R.id.iv_product_cart);
        mTvOrderCommit = (TextView) findViewById(R.id.tv_order_commit);
        mTvProductTotalCount = (TextView) findViewById(R.id.tv_product_total_count);
        mRlCartContainer = (RelativeLayout) findViewById(R.id.rl_cart_container);


        findViewById(R.id.title_iv_left).setOnClickListener(this);
        findViewById(R.id.iv_open).setOnClickListener(this);
        findViewById(R.id.iv_product_cart).setOnClickListener(this);
        findViewById(R.id.rl_cart_container).setOnClickListener(this);
        findViewById(R.id.title_iv_rigth2).setOnClickListener(this);
        findViewById(R.id.tv_order_commit).setOnClickListener(this);
        findViewById(R.id.rl_bottom_bar).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        btnClick(view);
    }

    //    @OnClick({R.id.title_iv_left, R.id.addBtn, R.id.iv_open, R.id.iv_product_cart,
//            R.id.tv_order_resume, R.id.rl_cart_container, R.id.title_iv_rigth2})
    TransferoutProductSearchFragment mProductSearchFragment;

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                dialog.setMessageGravity();
                dialog.setMessage("还没保存哦,确认取消?");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        finish();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.iv_open:
                if (mTypeWindow == null) {
                    return;
                }
                if (!mTypeWindow.isShowing()) {
                    showPopWindow();
                } else {
                    mTypeWindow.dismiss();
                }
                break;
            case R.id.iv_product_cart://点购物车按钮
            case R.id.rl_bottom_bar:
                getCache();
//                if (mPlaceOrderType == PLACE_ORDER_TYPE_AGAIN){
//                    showCart(true);
//                }
                showCart(true);
                MobclickAgent.onEvent(getActivityContext(), EVENT_ID_SHOPPING_CART);
                break;
            case R.id.rl_cart_container:
                showCart(false);
                break;
            case R.id.title_iv_rigth2:
                if (mTypeWindow != null) mTypeWindow.dismiss();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.rl_content_container, new TransferoutProductSearchFragment())
                        .addToBackStack("product_search")
                        .commitAllowingStateLoss();
                break;
            case R.id.tv_order_commit:
                onOkClicked();
                break;
            default:
                break;
        }
    }

    /**
     * 点击选好了
     */
    protected void onOkClicked() {
        dialog.setTitleGone();
        dialog.setMessage("确认出库?");
        dialog.setMessageGravity();
        dialog.setModel(CustomDialog.BOTH);
        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
            @Override
            public void doClickButton(Button btn, CustomDialog dialog) {
                goToOrderSubmitActivity();
            }
        });
        dialog.show();

    }

    void goToOrderSubmitActivity() {
        //判断是否是自助下单
        ArrayList<TransferoutSubmitRequest.Product> list = new ArrayList<>();
        for (StockProductListResponse.ListBean bean : mMapCount.keySet()) {
            TransferoutSubmitRequest.Product product = new TransferoutSubmitRequest.Product();
            product.setProduct_id(bean.getProductID());
            product.setQty(mMapCount.get(bean));
            list.add(product);
        }
        TransferoutSubmitRequest transferoutSubmitRequest = new TransferoutSubmitRequest();
        transferoutSubmitRequest.setProducts(list);
        netWorkHelper.sendConnection("/api/self/transfer/create", transferoutSubmitRequest, REQUEST_CODE_SUBMIT_STOCK_PRODUCT_LIST, true, SubmitTransferoutResponse.class);
    }

    /*
     * 更新底部汇总
     * 只计算勾选的
     * 不计算下架的
     */
    protected void updateBottomBar() {
        if (mMapCount.size() == 0) {
            mIvCart.setEnabled(false);
            mTvOrderCommit.setEnabled(false);
            mTvProductTotalCount.setVisibility(View.INVISIBLE);
        } else {
            mIvCart.setEnabled(true);
            mTvOrderCommit.setEnabled(true);
            //计算总价,总量
            double totalPieces = 0;
            for (StockProductListResponse.ListBean bean : mMapCount.keySet()) {
                if (!mmSelected.contains(bean.getProductID()))
                    continue;//只计算勾选的和有效的
                totalPieces = totalPieces + mMapCount.get(bean);
            }

            if (totalPieces != 0) {
                mTvOrderCommit.setEnabled(true);
                mTvProductTotalCount.setVisibility(View.VISIBLE);
                mTvProductTotalCount.setText(NumberUtil.getIOrD(mmSelected.size()) + "种 " + NumberUtil.getIOrD(totalPieces) + "件商品");
            } else {
                mTvOrderCommit.setEnabled(false);
                mTvProductTotalCount.setVisibility(View.GONE);
            }

        }
    }

    StockProductListResponse mStockProductListResponse;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CODE_STOCK_PRODUCT_LIST:
                BaseEntity.ResultBean resultBean = result.getResult();
                mStockProductListResponse = (StockProductListResponse) resultBean.getData();
                if (mStockProductListResponse.getList() != null) {
                    mListBeans = mStockProductListResponse.getList();
                    requestCategory();
                }
                break;
            case REQUEST_CODE_SUBMIT_STOCK_PRODUCT_LIST:
                finish();
                Intent intent = new Intent(getActivityContext(), MineTransferoutSuccessActivity.class);
                startActivity(intent);
                break;
        }
    }


    private String getCategoryCountInfo(String categoryParent, String categoryChild) {
        String desc = "";
        int count = 0;
        int productCount = 0;
        for (StockProductListResponse.ListBean listBean : mmProductList) {
            if (categoryParent.equals(listBean.getCategoryParent())
                    && categoryChild.equals(listBean.getCategoryChild())) {
                count++;
                if (mMapCount.get(listBean) != null) {
                    productCount += mMapCount.get(listBean);
                }
            }
        }
        if (TextUtils.isEmpty(categoryChild)) {
            desc = categoryParent + "(" + count + "种,共" + productCount + "件)";
        } else {
            desc = categoryParent + "/" + categoryChild + "(" + count + "种,共" + productCount + "件)";
        }
        return desc;
    }

    /**
     * 在列表页更新商品选择数量
     *
     * @param event
     */
    @Subscribe
    public void updateProductCount(TransferoutProductCountUpdateEvent event) {
        //更新购物车选择框
        if (event.bean != null) {
            if (event.count != 0) mmSelected.add(event.bean.getProductID());
            else mmSelected.remove(event.bean.getProductID());
            notifySelectAll();
            if (event.count == 0) {
                mMapCount.remove(event.bean);
            } else {
                mMapCount.put(event.bean, event.count);
            }
        }
        updateBottomBar();
        if (mmProductList.size() == 1 && mmProductList.get(0).getProductID() == 0) {
            showCart(false);
        }
    }


    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CODE_SUBMIT_STOCK_PRODUCT_LIST:
                toast(errMsg);
                break;
        }
    }

    @Override
    public void retryOnClick(View view) {
//        startRequest();
    }

    protected class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        protected List<String> titleList = new ArrayList<>();
        protected List<TransferoutProductCategoryFragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, List<String> titles, List<TransferoutProductCategoryFragment> repertoryEntityFragmentList) {
            super(fm);
            fragmentList.addAll(repertoryEntityFragmentList);
            titleList = titles;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try {
                super.finishUpdate(container);
            } catch (Exception nullPointerException) {
                Log.e("finishUpdate", nullPointerException.toString());
            }
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
        public int getCount() {
            return titleList.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
//            fragmentList.get(position).onSelected();
        }
    }

    /**
     * 展示购物车dialog
     */
    protected void showCart(boolean isShow) {
        if (isShow) {
            if (mMapCount.size() == 0) return;
            final View view = findViewById(R.id.include_cart);
            view.setVisibility(View.VISIBLE);
            view.setAnimation(AnimationUtils.loadAnimation(getActivityContext(), R.anim.slide_in_from_bottom));
            mRlCartContainer.setVisibility(View.VISIBLE);
            initCartViews();
        } else {
            findViewById(R.id.include_cart).setVisibility(View.INVISIBLE);
            mRlCartContainer.setVisibility(View.GONE);
        }
    }

    /**
     * ************* 以下为弹出的购物车框 ******************
     */
    List<StockProductListResponse.ListBean> mmProductList = new ArrayList<>();//购物车显示用的数据，包含有效和无效商品
    @ViewInject(R.id.rv_cart)
    RecyclerView mmRvCart;
    CartAdapter mmCartAdapter;
    @ViewInject(R.id.stick_header)
    View mVStickHeader;
    @ViewInject(R.id.tv_header)
    TextView mTvHeader;

    protected HashSet<Integer> mmSelected = new HashSet<>();//记录购物车中勾选的项目

    private void setUpRvCart() {
        mmRvCart.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mVStickHeader == null) {
                    return;
                }
                View stickyInfoView = recyclerView.findChildViewUnder(mVStickHeader.getMeasuredWidth() / 2, 5);
                if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                    mTvHeader.setText(String.valueOf(stickyInfoView.getContentDescription()));
                }
                View transInfoView = recyclerView.findChildViewUnder(mVStickHeader.getMeasuredWidth() / 2, mVStickHeader.getMeasuredHeight
                        () + 1);
                if (transInfoView != null && transInfoView.getTag() != null) {
                    int transViewStatus = (int) transInfoView.getTag();
                    int dealtY = transInfoView.getTop() - mVStickHeader.getMeasuredHeight();
                    if (transViewStatus == CartAdapter.HAS_STICKY_VIEW) {
                        if (transInfoView.getTop() > 0) {
                            mVStickHeader.setTranslationY(dealtY);
                        } else {
                            mVStickHeader.setTranslationY(0);
                        }
                    } else if (transViewStatus == CartAdapter.NONE_STICKY_VIEW) {
                        mVStickHeader.setTranslationY(0);
                    }
                }
            }
        });
    }


    /**
     * 初始化购物车弹框
     */
    protected void initCartViews() {
        mmRvCart = (RecyclerView) findViewById(R.id.rv_cart);
        mTvHeader = (TextView) findViewById(R.id.tv_header);
        mVStickHeader = findViewById(R.id.stick_header);
        mmRvCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mmCartAdapter = new CartAdapter();
        mmRvCart.setAdapter(mmCartAdapter);
        //删除全部选择
        initProductListData();
        setUpRvCart();
    }

    /**
     * 生成购物车列表的使用的数据
     * 如果包含无效商品，先加入一个空商品，用来表示header
     */
    protected void initProductListData() {
        mmProductList = new ArrayList<>();
        //mmProductList.addAll(mMapCount.keySet());
        for (StockProductListResponse.ListBean bean : mMapCount.keySet()) {//先加入合法商品
            mmProductList.add(bean);
        }
        LinkedHashMap<String, List<StockProductListResponse.ListBean>> linkedHashMap = new LinkedHashMap<>();
        //按一级和二级分类分组
        for (StockProductListResponse.ListBean bean : mmProductList) {
            String key = bean.getCategoryParent() + "&" + bean.getCategoryChild();
            List<StockProductListResponse.ListBean> listBeans = linkedHashMap.get(key);
            if (listBeans == null) {
                listBeans = new ArrayList<>();
            }
            listBeans.add(bean);
            linkedHashMap.put(key, listBeans);
        }
        mmProductList.clear();
        for (List<StockProductListResponse.ListBean> listBeanList : linkedHashMap.values()) {
            mmProductList.addAll(listBeanList);
        }
    }

    /**
     * 更新全选多选框
     */
    protected void notifySelectAll() {
    }

    /**
     * 购物车的adapter
     */
    protected class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int FIRST_STICKY_VIEW = 1;
        public static final int HAS_STICKY_VIEW = 2;
        public static final int NONE_STICKY_VIEW = 3;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivityContext());
            if (viewType == 0) {
                return new ViewHolder(inflater.inflate(R.layout.list_item_transferout_cart, parent, false));
            } else {
                return new HeaderViewHolder(inflater.inflate(R.layout.item_cart_invalid_header, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
            if (getItemViewType(position) == 1) {
                return;
            }
            ViewHolder holder = (ViewHolder) viewholder;
            holder.listBean = mmProductList.get(position);
            holder.mmTvName.setText(holder.listBean.getName());
            double count = mMapCount.containsKey(holder.listBean) ? mMapCount.get(holder.listBean) : 0;
            holder.mmTvCount.setText(NumberUtil.getIOrD(count) + holder.listBean.getStockUom());
            StringBuilder sb = new StringBuilder();
            sb.append(holder.listBean.getUnit());
            holder.mmTvContent.setText(sb.toString());
            if (holder.listBean.getImage() != null) {
                FrecoFactory.getInstance(mContext).displayWithoutHost(holder.simpleDraweeView, holder.listBean.getImage().getImageSmall());
            }
            holder.mmTvInvalide.setVisibility(View.GONE);
            holder.mmTvCount.setBackgroundResource(R.drawable.order_input_mid);
            holder.mmTvCount.setTextColor(Color.BLACK);
            holder.mmTvName.setTextColor(Color.BLACK);
            holder.mmTvAdd.setEnabled(true);
            holder.mmTvReduce.setEnabled(true);
            holder.mmTvTag.setVisibility(View.GONE);

            String remark = mMapRemarks.get(holder.listBean);
            if (TextUtils.isEmpty(remark)) {
                holder.mmTvRemark.setVisibility(View.GONE);
            } else {
                holder.mmTvRemark.setVisibility(View.VISIBLE);
                holder.mmTvRemark.setText("备注：" + remark);
            }

            if (TextUtils.isEmpty(holder.listBean.getCategoryChild())) {
                holder.itemView.setContentDescription(holder.listBean.getCategoryParent());
            } else {
                holder.itemView.setContentDescription(holder.listBean.getCategoryParent() + "/" + holder.listBean.getCategoryChild());
            }

            if (position == 0) {
                holder.mVStickHeader.setVisibility(View.VISIBLE);
                holder.mVProductMain.setTag(FIRST_STICKY_VIEW);
                holder.mTvHeader.setText(getCategoryCountInfo(holder.listBean.getCategoryParent(), holder.listBean.getCategoryChild()));
            } else {
                if (!TextUtils.equals(holder.listBean.getCategoryParent(), mmProductList.get(position - 1).getCategoryParent()) ||
                        !TextUtils.equals(holder.listBean.getCategoryChild(), mmProductList.get(position - 1).getCategoryChild())) {
                    holder.mVStickHeader.setVisibility(View.VISIBLE);
                    holder.mVProductMain.setTag(HAS_STICKY_VIEW);
                    holder.mTvHeader.setText(getCategoryCountInfo(holder.listBean.getCategoryParent(), holder.listBean.getCategoryChild()));
                } else {
                    holder.mVProductMain.setTag(NONE_STICKY_VIEW);
                    holder.mVStickHeader.setVisibility(View.GONE);
                }
            }
            LongPressUtil longPressUtil = new LongPressUtil();
            longPressUtil.setUpEvent(holder.mmTvReduce, new LongPressUtil.CallBack() {
                @Override
                public void call() {
                    mCountSetter.setCount(holder.listBean, 0);
//                    mMapCount.put(listBean,count);
                    //从购物车中删除
                    mmProductList.remove(holder.listBean);
                    initProductListData();
                    mmCartAdapter.notifyChanged();
                    EventBus.getDefault().post(new TransferoutProductCountUpdateEvent(holder.listBean, 0));
                    holder.mmTvCount.setText(0 + holder.listBean.getStockUom());
                    mmCartAdapter.notifyChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mmProductList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mmProductList.get(position).getProductID() == 0) return 1;
            return 0;
        }

        /**
         * 更新购物车
         * 如果全部删除了，则关闭cart
         */
        public void notifyChanged() {
            if (mmProductList.size() == 0) {
                showCart(false);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 商品
     */
    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        StockProductListResponse.ListBean listBean;
        TextView mmTvName;
        TextView mmTvCount;
        TextView mmTvContent;
        TextView mmTvInvalide;
        TextView mmTvTag;
        TextView mmTvRemark;
        ImageView mmTvAdd;
        ImageView mmTvReduce;
        View mVStickHeader;
        TextView mTvHeader;
        View mVProductMain;
        SimpleDraweeView simpleDraweeView;


        public ViewHolder(View itemView) {
            super(itemView);
            mmTvName = (TextView) itemView.findViewById(R.id.tv_item_cart_name);
            mmTvCount = (TextView) itemView.findViewById(R.id.tv_item_cart_count);
            mmTvContent = (TextView) itemView.findViewById(R.id.tv_item_cart_content);
            mmTvInvalide = (TextView) itemView.findViewById(R.id.tv_invalid);
            mmTvTag = (TextView) itemView.findViewById(R.id.tv_item_cart_sale);
            mmTvRemark = (TextView) itemView.findViewById(R.id.tv_cart_remark);
            mmTvAdd = (ImageView) itemView.findViewById(R.id.iv_item_cart_add);
            mmTvAdd.setOnClickListener(this);
            mmTvReduce = (ImageView) itemView.findViewById(R.id.iv_item_cart_minus);
            mVStickHeader = itemView.findViewById(R.id.stick_header);
            mVProductMain = itemView.findViewById(R.id.product_main);
            mTvHeader = (TextView) itemView.findViewById(R.id.tv_header);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.sdv_cart_product_image);
            mmTvReduce.setOnClickListener(this);
            mmTvCount.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_item_cart_add://增加
                    double count = mMapCount.containsKey(listBean) ? mMapCount.get(listBean) : 0;
                    count = BigDecimal.valueOf(count).add(BigDecimal.ONE).doubleValue();
                    mMapCount.put(listBean, count);
                    EventBus.getDefault().post(new TransferoutProductCountUpdateEvent(listBean, count));
                    mmTvCount.setText(NumberUtil.getIOrD(count) + listBean.getStockUom());
                    mmCartAdapter.notifyChanged();
                    break;
                case R.id.iv_item_cart_minus://减少
                    count = mCountSetter.getCount(listBean);
                    count = BigDecimal.valueOf(count).subtract(BigDecimal.ONE).doubleValue();
                    if (count < 0) count = 0;
                    mCountSetter.setCount(listBean, count);
//                    mMapCount.put(listBean,count);
                    if (count == 0) {
                        //从购物车中删除
                        mmProductList.remove(listBean);
                        initProductListData();
                        mmCartAdapter.notifyChanged();
                    }
                    EventBus.getDefault().post(new TransferoutProductCountUpdateEvent(listBean, count));
                    mmTvCount.setText(NumberUtil.getIOrD(count) + listBean.getStockUom());
                    mmCartAdapter.notifyChanged();
                    break;
                case R.id.tv_item_cart_count:
                    double currentCount = mCountSetter.getCount(listBean);
                    new TransferProductValueDialog(mContext, listBean.getName(), currentCount, mCountSetter.getRemark(listBean), new TransferProductValueDialog.IProductDialogCallback() {
                        @Override
                        public void onInputValue(double value) {
                            if (value > listBean.getQty()) {
                                ToastUtil.show(mContext, "超过了库存数量!");
                                return;
                            }
                            mCountSetter.setCount(listBean, value);
                            mCountSetter.setRemark(listBean);
                            mMapCount.put(listBean, value);
                            if (value == 0) {
                                mMapCount.remove(listBean);
                                initProductListData();
                                mmCartAdapter.notifyChanged();
                            }
                            EventBus.getDefault().post(new TransferoutProductCountUpdateEvent(listBean, value));
                            mmTvCount.setText(NumberUtil.getIOrD(value) + listBean.getStockUom());
                            mmTvRemark.setText("");
                            mmTvRemark.setVisibility(View.GONE);
                            mmCartAdapter.notifyChanged();
                        }
                    }).show();
                    break;
            }
        }
    }

    /**
     * 无效商品的头部
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        HeaderViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.tv_item_cart_clear_invalid).setOnClickListener(this);
        }

        /**
         * 点击清空下架按钮
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            CustomDialog customDialog = new CustomDialog(getActivityContext());
            customDialog.setMessage("清空购物车中所有下架商品");
            customDialog.setRightBtnListener("清空", new CustomDialog.DialogListener() {
                @Override
                public void doClickButton(Button btn, CustomDialog dialog) {
                    clearInvalid();
                }
            });
            customDialog.show();
        }

        /**
         * 删除全部无效商品，并更新购物车列表
         */
        private void clearInvalid() {
            Iterator<Map.Entry<StockProductListResponse.ListBean, Double>> it = mMapCount.entrySet().iterator();
            initProductListData();
            mmCartAdapter.notifyChanged();
            updateBottomBar();
        }

    }

    /**
     * 初始化全选按钮
     */
    protected void initSelectAll() {
        for (StockProductListResponse.ListBean listBean : mMapCount.keySet()) {
            mmSelected.add(listBean.getProductID());
        }
    }

    /**
     * 供子fragment统一设置商品数量,隐藏细节
     */
    public interface ProductCountSetter {
        void setCount(StockProductListResponse.ListBean bean, double count);

        void setRemark(StockProductListResponse.ListBean bean);

        double getCount(StockProductListResponse.ListBean bean);

        String getRemark(StockProductListResponse.ListBean bean);
    }

    /**
     * 供子fragment统一设置商品数量的接口，向子fragment隐藏实现
     */
    ProductCountSetter mCountSetter = new ProductCountSetter() {
        @Override
        public void setCount(StockProductListResponse.ListBean bean, double count) {
            if (count == 0) {
                mMapCount.remove(bean);
            } else {
                //设置加入购物车的时间
                mMapCount.put(bean, count);
            }
        }

        @Override
        public double getCount(StockProductListResponse.ListBean bean) {
            if (mMapCount.get(bean) == null) {
                return 0;
            } else {
                return mMapCount.get(bean);
            }
        }

        @Override
        public void setRemark(StockProductListResponse.ListBean bean) {
            mMapRemarks.put(bean, bean.getRemark());
        }

        @Override
        public String getRemark(StockProductListResponse.ListBean bean) {
            return mMapRemarks.get(bean);
        }
    };

    /**
     * 供子fragment统一设置商品数量
     */
    public ProductCountSetter getProductCountSetter() {
        return mCountSetter;
    }


    protected String getPageName() {
        return "自助下单页面";
    }

    protected void statisticsOrderTimeOnResume() {
        SelfOrderTimeStatisticsUtil.onResume();
    }

    protected void statisticsOrderTimeOnPause() {
        SelfOrderTimeStatisticsUtil.onPause(getActivityContext());
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName());
        MobclickAgent.onResume(this);          //统计时长
        statisticsOrderTimeOnResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
        MobclickAgent.onPause(this);          //统计时长
        statisticsOrderTimeOnPause();
    }

    public static final String ACTION_TYPE_SERVICE = "action_type_service";

    ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            //todo 这里写你要在界面加载完成后执行的操作。
            dismissIProgressDialog();
            smartTabLayout.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
        }
    };

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_TYPE_SERVICE:
                    String status = intent.getStringExtra(INTENT_KEY_STATUS);
                    if (status.equals(getString(R.string.service_finish)) || status.equals(getString(R.string.service_fail_finish))) {
                        //刷新商品列表
                        startRequest();
                        smartTabLayout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    }
                    break;
            }
        }
    }


}
