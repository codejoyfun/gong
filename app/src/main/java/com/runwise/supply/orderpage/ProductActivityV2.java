package com.runwise.supply.orderpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.CartCache;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.CategoryResponseV2;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.ProductTypePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.orderpage.OrderSubmitActivity.INTENT_KEY_PRODUCTS;
import static com.runwise.supply.orderpage.ProductCategoryFragment.INTENT_KEY_CATEGORY;

/**
 * 分页/二级分类的商品选择页
 * 注意要区分有含有二级分类和完全没有二级分类两种显示
 *
 * Created by Dong on 2017/7/3.
 */

public class ProductActivityV2 extends NetWorkActivity implements View.OnClickListener{
    public static final String INTENT_KEY_ADDED_PRODUCTS = "ap_added_products";
    //商品数据获取
    protected static final int REQUEST_CATEGORY = 1;

    @ViewInject(R.id.indicator)
    protected TabLayout smartTabLayout;
    @ViewInject(R.id.iv_open)
    protected ImageView ivOpen;
    @ViewInject(R.id.vp_product_fragments)
    protected ViewPager mViewPagerCategoryFrags;
    @ViewInject(R.id.iv_product_cart)
    protected ImageView mIvCart;//购物车图标
    @ViewInject(R.id.tv_order_resume)
    protected TextView mTvResume;//继续选择
    @ViewInject(R.id.tv_order_commit)
    protected TextView mTvOrderCommit;//选好了
    @ViewInject(R.id.tv_cart_count)
    protected TextView mTvCartCount;//红色小标数量
    @ViewInject(R.id.tv_product_total_price)
    protected TextView mTvTotalPrice;
    protected TabPageIndicatorAdapter mAdapterVp;
    @ViewInject(R.id.rl_cart_container)
    protected RelativeLayout mRlCartContainer;

    protected ArrayList<AddedProduct> addedPros;       //从前面页面传来的数组。
    protected ProductTypePopup mTypeWindow;//商品类型弹出框

    CategoryResponseV2 categoryResponse;
    public static final String INTENT_KEY_BACKAP = "backap";

    protected Map<ProductData.ListBean, Integer> mMapCount = new HashMap<>();
    protected Set<ProductData.ListBean> mSetInvalid = new HashSet<>();

    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setStatusBarEnabled();
        setContentView(R.layout.activity_product_selection);
        init();
        setTitleText(true,"全部商品");
        showBackBtn();
        setTitleRightIcon2(true,R.drawable.ic_nav_search);
        //获取上一个页面传来的Parcelable
        addedPros = getIntent().getParcelableArrayListExtra(INTENT_KEY_ADDED_PRODUCTS);
        getCache();//获取缓存
        updateBottomBar();//更新底部bar
        startRequest();//查询接口
    }

    /**
     * 在onCreate最后被调用
     */
    protected void startRequest(){
        //查询类别
        requestCategory();
    }

    /**
     * 获取缓存
     * 子类需要重写，否则会使用购物车的缓存
     */
    protected void getCache(){
        CartCache cartCache = CartManager.getInstance(this).loadCart();
        if(cartCache!=null && cartCache.getListBeans()!=null){
            for(ProductData.ListBean bean:cartCache.getListBeans()){
//                //TODO:检查购物车有效性
//                ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(this).get(bean.getProductID()+"");
//                if(basicBean==null){
//                    //记录失效
//                    bean.setInvalid(true);
//                    mSetInvalid.add(bean);
//                }else{
//                    bean.setInvalid(false);
//                }
                mMapCount.put(bean,bean.getCacheCount());

                if(bean.isCacheSelected())mmSelected.add(bean.getProductID());
            }
        }
    }

    /**
     * 保存缓存
     * 子类需要重写，否则会写入购物车的缓存
     */
    protected void saveCache(){
        if(mMapCount.size()==0){
            CartManager.getInstance(this).clearCart();
            return;
        }
        CartManager.getInstance(this).saveCart(mMapCount,mmSelected);
//        if(mMapCount.size()==0){
//            SPUtils.put(this,SP_KEY_CART,"");
//            return;
//        }
//        ArrayList<ProductData.ListBean> list = new ArrayList<>();
//        for(ProductData.ListBean bean:mMapCount.keySet()){
//            bean.setCacheCount(mMapCount.get(bean));
//        }
//        list.addAll(mMapCount.keySet());
//        CartCache cartCache = new CartCache();
//        cartCache.setListBeans(list);
//        cartCache.setSelected(mmSelected);
//        SPUtils.saveObject(this,SP_KEY_CART,cartCache);
    }

    @Override
    public void onStop(){
        super.onStop();
        saveCache();
    }

    /**
     * 查询类别
     */
    protected void requestCategory() {
        ///gongfu/v3/shop/product/list
        GetCategoryRequest request = new GetCategoryRequest();
        request.setUser_id(Integer.valueOf(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/v2/product/category", request, REQUEST_CATEGORY, true, CategoryResponseV2.class);
    }

    protected void setupViewPager() {
        List<ProductCategoryFragment> categoryFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (CategoryResponseV2.Category category : categoryResponse.getCategoryList()) {
            titles.add(category.getCategoryParent());
            categoryFragmentList.add(newCategoryFragment(category));
        }

        initUI(titles, categoryFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    /**
     * 新建一级类别fragment
     * @param category
     * @return
     */
    protected ProductCategoryFragment newCategoryFragment(CategoryResponseV2.Category category) {
        ProductCategoryFragment productCategoryFragment = new ProductCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(INTENT_KEY_CATEGORY, category);
        productCategoryFragment.setArguments(bundle);
        return productCategoryFragment;
    }

    /**
     * 供子fragment共享设置商品数量
     */
    public Map<ProductData.ListBean,Integer> getCountMap(){
        return mMapCount;
    }

    protected void initPopWindow(ArrayList<String> typeList) {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow = new ProductTypePopup(this,
                MATCH_PARENT,
                DensityUtil.getScreenH(getActivityContext()) - y,
                typeList,0);
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
        mTypeWindow.showAtLocation(getRootView(ProductActivityV2.this), Gravity.NO_GRAVITY, 0, y);
        mTypeWindow.setSelect(mViewPagerCategoryFrags.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    protected void initUI(List<String> titles, List<ProductCategoryFragment> repertoryEntityFragmentList) {
        mAdapterVp = new TabPageIndicatorAdapter(getSupportFragmentManager(), titles, repertoryEntityFragmentList);
        mViewPagerCategoryFrags.setAdapter(mAdapterVp);
        smartTabLayout.setupWithViewPager(mViewPagerCategoryFrags);
        mViewPagerCategoryFrags.setOffscreenPageLimit(titles.size());
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPagerCategoryFrags.setCurrentItem(position);
                mTypeWindow.dismiss();

                //刷新当前fragment
                ProductCategoryFragment fragment = mAdapterVp.fragmentList.get(position);
                fragment.onSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //如果有二级分类，不显示下拉按钮
        boolean hasSubcategory = false;
        for(CategoryResponseV2.Category category: categoryResponse.getCategoryList()){
            if(category.getCategoryChild()!=null && category.getCategoryChild().length>0){
                hasSubcategory = true;
                break;
            }
        }
        if(hasSubcategory){//有二级分类，不显示
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) smartTabLayout.getLayoutParams();
            params.rightMargin = 0;
            ivOpen.setVisibility(View.GONE);
        }
        else if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        //手动选择第一个类别fragment
        repertoryEntityFragmentList.get(0).onSelected();
    }

    /**
     * 继承的话ViewInject不会inject父类的。。。？
     */
    public void init(){
        smartTabLayout = (TabLayout) findViewById(R.id.indicator);
        ivOpen = (ImageView) findViewById(R.id.iv_open);
        mViewPagerCategoryFrags = (ViewPager) findViewById(R.id.vp_product_fragments);
        mIvCart = (ImageView) findViewById(R.id.iv_product_cart);
        mTvResume = (TextView) findViewById(R.id.tv_order_resume);
        mTvOrderCommit = (TextView)findViewById(R.id.tv_order_commit);
        mTvCartCount = (TextView)findViewById(R.id.tv_cart_count);
        mTvTotalPrice = (TextView)findViewById(R.id.tv_product_total_price);
        mRlCartContainer = (RelativeLayout) findViewById(R.id.rl_cart_container);
        mmCbSelectAll = (CheckBox)findViewById(R.id.cb_cart_select_all);
        findViewById(R.id.title_iv_left).setOnClickListener(this);
        findViewById(R.id.iv_open).setOnClickListener(this);
        findViewById(R.id.iv_product_cart).setOnClickListener(this);
        findViewById(R.id.tv_order_resume).setOnClickListener(this);
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
            case R.id.addBtn://TODO:deprecated
                //回值给调用的页面
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //当前选中的商品信息
                ArrayList<AddedProduct> addedList = new ArrayList<>();
                HashMap<String, Integer> countMap = ProductListFragment.getCountMap();
                Iterator iter = countMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry) iter.next();
                    String key = entry.getKey();
                    Integer count = entry.getValue();
                    Parcel parcel = Parcel.obtain();
                    AddedProduct pro = AddedProduct.CREATOR.createFromParcel(parcel);
                    if (count != 0) {
                        pro.setCount(count);
                        pro.setProductId(key);
                        addedList.add(pro);
                    }
                }
                bundle.putParcelableArrayList(INTENT_KEY_BACKAP, addedList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
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
                showCart(true);
                break;
            case R.id.tv_order_resume:
                showCart(false);
                break;
            case R.id.rl_cart_container:
                showCart(false);
                break;
            case R.id.title_iv_rigth2:
                if(mTypeWindow!=null)mTypeWindow.dismiss();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.rl_content_container,new ProductSearchFragment())
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
    protected void onOkClicked(){
        if(mmSelected.size()==0){
            Toast.makeText(this,"请在购物车中勾选商品",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this,OrderSubmitActivity.class);
        ArrayList<ProductData.ListBean> list = new ArrayList<>();
        for(ProductData.ListBean bean:mMapCount.keySet()){
            if(!mmSelected.contains(bean.getProductID()))continue;//木有在购物车中打勾，跳过
            if(bean.isInvalid() || mMapCount.get(bean)==0)continue;
            bean.setActualQty(mMapCount.get(bean));
            list.add(bean);
        }
        intent.putParcelableArrayListExtra(INTENT_KEY_PRODUCTS,list);
        startActivity(intent);
    }

    /*
     * 更新底部汇总
     * 只计算勾选的
     */
    protected void updateBottomBar(){
        if(mMapCount.size()==0){
            mIvCart.setEnabled(false);
            mTvOrderCommit.setEnabled(false);
            mTvCartCount.setVisibility(View.INVISIBLE);
            mTvTotalPrice.setVisibility(View.INVISIBLE);
        }else{
            mIvCart.setEnabled(true);
            mTvOrderCommit.setEnabled(true);
            mTvCartCount.setVisibility(View.VISIBLE);
            //计算总价,总量
            double totalMoney = 0;
            int totalPieces = 0;
            for(ProductData.ListBean bean:mMapCount.keySet()){
                if(!mmSelected.contains(bean.getProductID()))continue;//只计算勾选的
                totalMoney = totalMoney + mMapCount.get(bean) * bean.getPrice();
                totalPieces = totalPieces + mMapCount.get(bean);
            }

            if(totalPieces!=0){
                mTvCartCount.setText(totalPieces+"");
                mTvCartCount.setVisibility(View.VISIBLE);
                mTvOrderCommit.setEnabled(true);
            }else{
                mTvCartCount.setVisibility(View.GONE);
                mTvOrderCommit.setEnabled(false);
            }

            if(GlobalApplication.getInstance().getCanSeePrice()){
                mTvTotalPrice.setVisibility(View.VISIBLE);
                mTvTotalPrice.setText("￥"+df.format(totalMoney));//TODO:format
            }
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryResponse = (CategoryResponseV2) resultBean1.getData();
                setupViewPager();
                break;
            default:
                break;
        }
    }

    /**
     * 在列表页更新商品选择数量
     * @param event
     */
    @Subscribe
    public void updateProductCount(ProductCountUpdateEvent event){
        //更新购物车选择框
        if(event.bean!=null){
            if(event.count!=0)mmSelected.add(event.bean.getProductID());
            else mmSelected.remove(event.bean.getProductID());
            notifySelectAll();
        }
        updateBottomBar();
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        if(!TextUtils.isEmpty(errMsg))toast(errMsg);
    }

    protected class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        protected List<String> titleList = new ArrayList<>();
        protected List<ProductCategoryFragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, List<String> titles, List<ProductCategoryFragment> repertoryEntityFragmentList) {
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
            return titleList.get(position);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }
    }

    /**
     * 展示购物车dialog
     */
    protected void showCart(boolean isShow){
        if(isShow){
            if(mTvResume.getVisibility()==View.VISIBLE){
                //已经在显示，收起
                showCart(false);
                return;
            }
            if(mMapCount.size()==0)return;
            mTvResume.setVisibility(View.VISIBLE);
            final View view = findViewById(R.id.include_cart);
            view.setVisibility(View.VISIBLE);
            view.setAnimation(AnimationUtils.loadAnimation(ProductActivityV2.this,R.anim.slide_in_from_bottom));
            mRlCartContainer.setVisibility(View.VISIBLE);
            initCartViews();
        }else{
            mTvResume.setVisibility(View.GONE);
            findViewById(R.id.include_cart).setVisibility(View.INVISIBLE);
            mRlCartContainer.setVisibility(View.GONE);
        }
    }

    /**
     * ************* 以下为弹出的购物车框 ******************
     */
    List<ProductData.ListBean> mmProductList;//购物车显示用的数据，包含有效和无效商品
    @ViewInject(R.id.rv_cart)
    RecyclerView mmRvCart;
    CartAdapter mmCartAdapter;
    @ViewInject(R.id.cb_cart_select_all)
    CheckBox mmCbSelectAll;
    @ViewInject(R.id.tv_cart_del)
    TextView mmTvDelete;
    protected HashSet<Integer> mmSelected = new HashSet<>();//记录购物车中勾选的项目

    /**
     * 初始化购物车弹框
     */
    protected void initCartViews(){
        mmRvCart = (RecyclerView)findViewById(R.id.rv_cart);
        mmTvDelete = (TextView)findViewById(R.id.tv_cart_del);
        mmRvCart.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mmCartAdapter = new CartAdapter();
        mmRvCart.setAdapter(mmCartAdapter);
        //全选
        mmCbSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mmCbSelectAll.isChecked()){
                    for(ProductData.ListBean listBean:mMapCount.keySet()){
                        if(listBean.isInvalid())continue;//全选跳过无效商品
                        mmSelected.add(listBean.getProductID());
                    }
                }else{
                    mmSelected.clear();
                }
                updateBottomBar();
                mmCartAdapter.notifyDataSetChanged();
            }
        });
        if(mmSelected.size()==mMapCount.size())mmCbSelectAll.setChecked(true);//全选按钮初始化
        //删除全部选择
        mmTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mmSelected.size()==0)return;
                CustomDialog customDialog = new CustomDialog(ProductActivityV2.this);
                customDialog.setMessage("删除购物车中所选商品");
                customDialog.setRightBtnListener("删除", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {

                        Iterator<ProductData.ListBean> it = mMapCount.keySet().iterator();
                        while (it.hasNext())
                        {
                            ProductData.ListBean item = it.next();
                            if(mmSelected.contains(item.getProductID())){
                                it.remove();
                                mmProductList.remove(item);
                                mmSelected.remove(item.getProductID());
                            }
                        }
                        mmCartAdapter.notifyChanged();
                        ToastUtil.show(ProductActivityV2.this,"删除成功");
                        EventBus.getDefault().post(new ProductCountUpdateEvent());
                    }
                });
                customDialog.show();
            }
        });
        initProductListData();
    }

    /**
     * 生成购物车列表的使用的数据
     * 如果包含无效商品，先加入一个空商品，用来表示
     */
    protected void initProductListData(){
        mmProductList = new ArrayList<>();
        //mmProductList.addAll(mMapCount.keySet());//TODO:很重要，必须与mMapCount保持一直
        for(ProductData.ListBean bean:mMapCount.keySet()){//先加入合法商品
            if(!bean.isInvalid())mmProductList.add(bean);
        }
        if(mSetInvalid.size()>0){
            mmProductList.add(new ProductData.ListBean());//加入头部
            for(ProductData.ListBean bean:mSetInvalid){//加入失效商品
                if(bean.isInvalid())mmProductList.add(bean);
            }
        }
    }

    /**
     * 更新全选多选框
     */
    protected void notifySelectAll(){
        if(mmSelected.size()==mMapCount.size()){
            //全选
            mmCbSelectAll.setChecked(true);
        }else{
            //非全选
            mmCbSelectAll.setChecked(false);
        }
    }

    /**
     * 购物车的adapter
     */
    protected class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ProductActivityV2.this);
            if(viewType==0){
                return new ViewHolder(inflater.inflate(R.layout.item_cart,parent,false));
            }else{
                return new HeaderViewHolder(inflater.inflate(R.layout.item_cart_invalid_header,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
            if(getItemViewType(position)==1){
                return;
            }

            ViewHolder holder = (ViewHolder)viewholder;
            holder.listBean = mmProductList.get(position);
            holder.mmTvName.setText(holder.listBean.getName());
            int count = mMapCount.containsKey(holder.listBean)?mMapCount.get(holder.listBean):0;
            holder.mmTvCount.setText(count+holder.listBean.getProductUom());
            StringBuilder sb = new StringBuilder();
            if(GlobalApplication.getInstance().getCanSeePrice()){
                sb.append("￥"+df.format(holder.listBean.getPrice())).append("/").append(holder.listBean.getProductUom()).append(" ");
            }
            sb.append(holder.listBean.getUnit());
            holder.mmTvContent.setText(sb.toString());

            if(holder.listBean.isInvalid()){
                holder.mmCbCheck.setEnabled(false);
                holder.mmTvInvalide.setVisibility(View.VISIBLE);
            }else{
                holder.mmCbCheck.setEnabled(true);
                holder.mmTvInvalide.setVisibility(View.GONE);
            }

            if(TextUtils.isEmpty(holder.listBean.getProductTag())){
                holder.mmTvTag.setVisibility(View.GONE);
            }else{
                holder.mmTvTag.setVisibility(View.VISIBLE);
            }

            holder.mmCbCheck.setChecked(mmSelected.contains(holder.listBean.getProductID()));
        }

        @Override
        public int getItemCount() {
            return mmProductList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(mmProductList.get(position).getProductID()==0)return 1;
            return 0;
        }

        /**
         * 更新购物车
         * 如果全部删除了，则关闭cart
         */
        public void notifyChanged(){
            if(mmProductList.size()==0){
                showCart(false);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 商品
     */
    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ProductData.ListBean listBean;
        TextView mmTvName;
        CheckBox mmCbCheck;
        TextView mmTvCount;
        TextView mmTvContent;
        TextView mmTvInvalide;
        TextView mmTvTag;
        public ViewHolder(View itemView) {
            super(itemView);
            mmTvName = (TextView) itemView.findViewById(R.id.tv_item_cart_name);
            mmCbCheck = (CheckBox) itemView.findViewById(R.id.cb_item_cart);
            mmCbCheck.setOnClickListener(this);
            mmTvCount = (TextView)itemView.findViewById(R.id.tv_item_cart_count);
            mmTvContent = (TextView)itemView.findViewById(R.id.tv_item_cart_content);
            mmTvInvalide = (TextView)itemView.findViewById(R.id.tv_invalid);
            mmTvTag = (TextView)itemView.findViewById(R.id.tv_item_cart_sale);
            itemView.findViewById(R.id.iv_item_cart_add).setOnClickListener(this);
            itemView.findViewById(R.id.iv_item_cart_minus).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.cb_item_cart://点击选择框
                    if(mmCbCheck.isChecked()){
                        mmSelected.add(listBean.getProductID());
                    }else{
                        mmSelected.remove(listBean.getProductID());
                    }
                    updateBottomBar();
                    notifySelectAll();
                    break;
                case R.id.iv_item_cart_add://增加
                    int count = mMapCount.containsKey(listBean)?mMapCount.get(listBean):0;
                    mMapCount.put(listBean,++count);
                    EventBus.getDefault().post(new ProductCountUpdateEvent(listBean,count));
                    mmTvCount.setText(count+listBean.getProductUom());
                    mmCbCheck.setChecked(true);
                    break;
                case R.id.iv_item_cart_minus://减少
                    count = mMapCount.containsKey(listBean)?mMapCount.get(listBean):0;
                    mMapCount.put(listBean,--count);
                    if(count==0){
                        //从购物车中删除
                        mmProductList.remove(listBean);
                        mMapCount.remove(listBean);
                        mSetInvalid.remove(listBean);
                        if(mSetInvalid.size()==0){
                            initProductListData();
                        }
                        mmCartAdapter.notifyChanged();
                    }else {
                        mmCbCheck.setChecked(true);
                    }
                    EventBus.getDefault().post(new ProductCountUpdateEvent(listBean,count));
                    mmTvCount.setText(count+listBean.getProductUom());
                    break;
            }
        }
    }

    /**
     * 无效商品的头部
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        HeaderViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.tv_item_cart_clear_invalid).setOnClickListener(this);
        }

        /**
         * 点击清空失效按钮
         * @param view
         */
        @Override
        public void onClick(View view) {
            CustomDialog customDialog = new CustomDialog(ProductActivityV2.this);
            customDialog.setMessage("清空购物车中所有失效商品");
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
        private void clearInvalid(){
            Iterator<Map.Entry<ProductData.ListBean, Integer>> it = mMapCount.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<ProductData.ListBean, Integer> entry = it.next();
                if(entry.getKey().isInvalid()){
                    it.remove();
                }
            }
            mSetInvalid.clear();
            initProductListData();
            mmCartAdapter.notifyChanged();
            updateBottomBar();
        }

    }

    /**
     * 初始化全选
     */
    protected void initSelectAll(){
        for(ProductData.ListBean listBean:mMapCount.keySet()){
            mmSelected.add(listBean.getProductID());
        }
    }

}
