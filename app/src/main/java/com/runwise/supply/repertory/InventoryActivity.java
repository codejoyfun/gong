package com.runwise.supply.repertory;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.event.InventoryEditEvent;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.EditRepertoryResult;
import com.runwise.supply.repertory.entity.EditRequest;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.InventoryCacheManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import github.chenupt.dragtoplayout.DragTopLayout;
import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ADD_INVENTORY_PRODUCT;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_SUBMIT_THE_INVENTORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.repertory.EditRepertoryAddActivity.INTENT_FILTER;
import static com.runwise.supply.repertory.InventoryFragment.INTENT_CATEGORY;

/**
 * 新界面的盘点
 * <p>
 * Created by Dong on 2017/12/8.
 */

public class InventoryActivity extends NetWorkActivity {
    private static final int INVENTORY_COMMIT = 0x34;
    public static final String INTENT_KEY_INVENTORY_BEAN = "inventory_bean";//传入盘点对象
    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.drag_layout)
    public DragTopLayout dragLayout;
    @ViewInject(R.id.viewpager)
    private ViewPager viewpager;
    @ViewInject(R.id.tv_inventory_id)
    private TextView mTvInventoryId;
    @ViewInject(R.id.tv_inventory_person)
    private TextView mTvInventoryPerson;
    @ViewInject(R.id.tv_inventory_date)
    private TextView mTvInventoryDate;
    @ViewInject(R.id.tv_inventory_cache)
    private TextView mTvInventoryCache;
    InventoryResponse.InventoryBean mInventoryBean;
    CategoryRespone categoryRespone;
    List<Fragment> orderProductFragmentList;
    private double mInventoryTotal = 0;//盘点后总数
    private boolean isSubmitted = false;//是否提交，提交了则onStop不保存
    boolean mAddProduct = false;
    public static final int REQUEST_CHECK_DETAIL = 1 << 0;
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);
        setTitleText(true, "盘点单");
        showBackBtn();
        setTitleRightText(true, "添加");
        dragLayout.setOverDrag(false);
        mInventoryBean = (InventoryResponse.InventoryBean) getIntent().getSerializableExtra(INTENT_KEY_INVENTORY_BEAN);

        mTvInventoryId.setText(mInventoryBean.getName());
        mTvInventoryPerson.setText(mInventoryBean.getCreateUser());
        mTvInventoryDate.setText(mInventoryBean.getCreateDate());
//        initDialog();
        if (mInventoryBean.getLines() == null) {
            Object request = null;
            sendConnection("/api/inventory/line/" + mInventoryBean.getInventoryID(), request, REQUEST_CHECK_DETAIL, true, CheckResult.class);
//            toast("该盘点单没有任何商品");
//            finish();
        } else {
            getCategory();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isSubmitted) {
            InventoryCacheManager.getInstance(this).saveInventory(mInventoryBean);
            InventoryCacheManager.getInstance(this).shouldShowInventoryInProgress(true);
        }
    }

    /**
     * 查询类别
     */
    private void getCategory() {
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage();
                initCartViews();
                break;
            case INVENTORY_COMMIT:
                isSubmitted = true;
                InventoryCacheManager.getInstance(this).removeInventory(mInventoryBean.getInventoryID());
                ToastUtil.show(mContext, "盘点成功");
                //计算原库存数量
                double total = 0;
//                for(InventoryResponse.InventoryProduct product:mInventoryBean.getLines()){
//                    if(product.getLotList()!=null){
//                        for(InventoryResponse.InventoryLot lot:product.getLotList()){
//                            total = total + lot.getTheoreticalQty();
//                        }
//                    }else{
//                        total = total + product.getTheoreticalQty();
//                    }
//                }
                //计算盘点后数量
                EditRepertoryFinishActivity.start(this, total, mInventoryTotal);
                InventoryCacheManager.getInstance(this).shouldShowInventoryInProgress(false);
                finish();
                break;
            case REQUEST_CHECK_DETAIL:
                BaseEntity.ResultBean resultBean3 = result.getResult();
                CheckResult checkResult = (CheckResult) resultBean3.getData();
                if (checkResult.getList() != null && checkResult.getList().size() > 0) {
                    mInventoryBean.setLines(transfrom(checkResult.getList().get(0).getLines()));
                    getCategory();
                }

                break;
        }
    }

    private List<InventoryResponse.InventoryProduct> transfrom(List<PandianResult.InventoryBean.LinesBean> lines) {
        List<InventoryResponse.InventoryProduct> inventoryProducts = new ArrayList<>();
        for (PandianResult.InventoryBean.LinesBean linesBean : lines) {
            InventoryResponse.InventoryProduct inventoryProduct = new InventoryResponse.InventoryProduct();

            inventoryProduct.setUom(linesBean.getUom());
            inventoryProduct.setActualQty(linesBean.getActualQty());
            inventoryProduct.setCode(linesBean.getCode());

            inventoryProduct.setDiff((int) linesBean.getDiff());
//            inventoryProduct.setEditNum(linesBean.getEditNum());
            inventoryProduct.setInventoryLineID(linesBean.getInventoryLineID());

            inventoryProduct.setLifeEndDate(linesBean.getLifeEndDate());
            inventoryProduct.setLotID(linesBean.getLotID());
            inventoryProduct.setLotNum(linesBean.getLotNum());

            inventoryProduct.setProduct(linesBean.getProduct());
            inventoryProduct.setProductID(linesBean.getProductID());
            inventoryProduct.setTheoreticalQty(linesBean.getTheoreticalQty());

            inventoryProduct.setLotList(linesBean.getLotList());
            inventoryProduct.setUnitPrice(linesBean.getUnitPrice());

            inventoryProducts.add(inventoryProduct);
        }
        return inventoryProducts;
    }


    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(this, errMsg);
    }

    private void setUpDataForViewPage() {
        if (categoryRespone == null) {
            return;
        }
        orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<InventoryResponse.InventoryProduct>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<>());
        }

        for (InventoryResponse.InventoryProduct inventoryProduct : mInventoryBean.getLines()) {
            ProductBasicList.ListBean listBean = inventoryProduct.getProduct();
            if (!TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<InventoryResponse.InventoryProduct> productByCategory = map.get(listBean.getCategory());
                if (productByCategory == null) {
                    productByCategory = new ArrayList<>();
                    map.put(listBean.getCategory(), productByCategory);
                }
                productByCategory.add(inventoryProduct);
            }
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<InventoryResponse.InventoryProduct> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(category, value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        //加入全部
        orderProductFragmentList.add(0, newProductFragment("", mInventoryBean.getLines()));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        viewpager.setOffscreenPageLimit(orderProductFragmentList.size());
        tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewpager.setCurrentItem(position, false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public Fragment newProductFragment(String category, List<InventoryResponse.InventoryProduct> value) {
        InventoryFragment editRepertoryListFragment = new InventoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_CATEGORY, category);
        editRepertoryListFragment.setArguments(bundle);
        editRepertoryListFragment.setData(value);
        return editRepertoryListFragment;
    }

    @OnClick({R.id.tv_inventory_commit, R.id.tv_inventory_cache, R.id.title_tv_rigth,R.id.rl_cart_container,R.id.rl_bottom_bar})
    public void onBtnClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_inventory_commit:
                MobclickAgent.onEvent(getActivityContext(), EVENT_ID_SUBMIT_THE_INVENTORY);
                CustomDialog dialog = new CustomDialog(this);
                dialog.setMessage("盘点成功，确认更新库存？");
                dialog.setTitleGone();
                dialog.setRightBtnListener("确认", (btn, dialg) -> commit());
                dialog.show();
                break;
            case R.id.tv_inventory_cache:
                showCart(false);
                break;
            case R.id.title_tv_rigth:
                //增加新商品，传入已有商品进行过滤
                Intent intent = new Intent(this, EditRepertoryAddActivity.class);
                ArrayList<Integer> filters = new ArrayList<>();
                for (InventoryResponse.InventoryProduct product : mInventoryBean.getLines()) {
                    filters.add(product.getProductID());
                }
                intent.putExtra(INTENT_FILTER, filters);
                startActivity(intent);
                break;
            case R.id.rl_cart_container:
                showCart(false);
                break;
            case R.id.rl_bottom_bar:
                showCart(true);
                break;
        }
    }

    /**
     * 添加新商品
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewBean(NewAdd newBean) {
        if (!mAddProduct) {
            mAddProduct = true;
            MobclickAgent.onEvent(getActivityContext(), EVENT_ID_ADD_INVENTORY_PRODUCT);
        }

        if (newBean.getType() == 1) {
            boolean isFind = false;
            for (InventoryResponse.InventoryProduct bean : mInventoryBean.getLines()) {
                if (bean.getProductID() == newBean.getInventoryProduct().getProductID()) {
                    bean.setEditNum(bean.getEditNum() + newBean.getBean().getEditNum());
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                mInventoryBean.getLines().add(0, newBean.getInventoryProduct());
            }
        } else {
            mInventoryBean.getLines().add(0, newBean.getInventoryProduct());
        }
        //adapter.notifyDataSetChanged();
        EventBus.getDefault().post(new InventoryEditEvent());
    }

    /**
     * 初始化修改商品数量dialog
     */
//    @ViewInject(R.id.name1)
//    private TextView mTvTitle;
//    @ViewInject(R.id.colseIcon1)
//    private ImageView mIvClose;
//    @ViewInject(R.id.et_product_amount1)
//    private EditText mEtCount;
//    @ViewInject(R.id.rl_dialog_add_sum)
//    private View mIncludeSumDialog;
//    @ViewInject(R.id.finalButton1)
//    private TextView mTvButton;
//    private void initDialog(){
//        mIvClose.setOnClickListener(v->{
//            finishDialog();
//        });
//        mIncludeSumDialog.setOnClickListener(v->{
//            finishDialog();
//        });
//    }
//
//    /**
//     * 显示修改没有批次的商品数量
//     * @param inventoryProduct
//     */
//    public void showAddSumDialog(InventoryResponse.InventoryProduct inventoryProduct){
//        mIncludeSumDialog.setVisibility(View.VISIBLE);
//        mTvTitle.setText(inventoryProduct.getProduct().getName());
//        mEtCount.setText(NumberUtil.getIOrD(inventoryProduct.getEditNum()));
//        mEtCount.selectAll();
//        mTvButton.setOnClickListener(v->{
//            String etValue = mEtCount.getText().toString();
//            if(TextUtils.isDigitsOnly(etValue)){
//                inventoryProduct.setEditNum(Double.valueOf(etValue));
//                EventBus.getDefault().post(new InventoryEditEvent());
//            }
//            finishDialog();
//        });
//    }
//
//    private void finishDialog(){
//        hideKeyboard();
//        mIncludeSumDialog.setVisibility(View.GONE);
//    }
//
//
//    InputMethodManager imm;
//    private void hideKeyboard(){
//        if(imm==null)imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        View v = getCurrentFocus();
//        if(imm!=null && v!=null)imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//    }

    /**
     * 提交盘点
     */
    private void commit() {
        EditRequest editRequest = new EditRequest();
        editRequest.setId(mInventoryBean.getInventoryID());
        editRequest.setState("done");
        List<EditRequest.ProductBean> editListBean = new ArrayList<>();
        mInventoryTotal = 0;
        for (InventoryResponse.InventoryProduct bean : mInventoryBean.getLines()) {
            if (bean.getLotList() == null) {//无批次
                EditRequest.ProductBean productBean = new EditRequest.ProductBean();
                productBean.setProduct_id(bean.getProductID());
                productBean.setId((int) bean.getInventoryLineID());
                productBean.setActual_qty(bean.getEditNum());
                productBean.setLot_id((int) bean.getLotID());
                productBean.setLot_num(bean.getLotNum());
                editListBean.add(productBean);
                mInventoryTotal = mInventoryTotal + bean.getEditNum();//计算盘点后总数
                continue;
            }
            for (InventoryResponse.InventoryLot lot : bean.getLotList()) {
                EditRequest.ProductBean productBean = new EditRequest.ProductBean();
                productBean.setProduct_id(bean.getProductID());
                productBean.setId(lot.getInventoryLineID());
                productBean.setActual_qty(lot.getEditNum());
                productBean.setLot_id(lot.getLotID());
                productBean.setLot_num(lot.getLotNum());
                mInventoryTotal = mInventoryTotal + lot.getEditNum();//计算盘点后总数
                if (lot.isProductDate() && lot.isNewAdded())
                    productBean.setProduce_datetime(lot.getProductDate());
                else if (!lot.isProductDate() && lot.isNewAdded())
                    productBean.setLife_datetime(lot.getProductDate());
                editListBean.add(productBean);
            }

        }
        editRequest.setInventory_lines(editListBean);

//							sendConnection("/gongfu/shop/inventory/state",editRequest,PRODUCT_COMMIT,true, EditRepertoryResult.class);
        sendConnection("/api/v2/inventory/state", editRequest, INVENTORY_COMMIT, true, EditRepertoryResult.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("盘点单详情页");
        MobclickAgent.onResume(this);          //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("盘点单详情页");
        MobclickAgent.onPause(this);          //统计时长
    }

    /**
     * 收到库存修改的广播，要更新界面
     */
    @Subscribe
    public void onInventoryEdit(InventoryEditEvent event) {
        if (mInventoryAdapter != null) {
            mInventoryAdapter = new InventoryAdapter(getDiffInventoryProductList());
            mmRvCart.setAdapter(mInventoryAdapter);
        }
        updateBottomBar();//更新底部bar
    }

    /*
    * 更新底部汇总
    * 只计算勾选的
    * 不计算下架的
    */
    protected void updateBottomBar() {
        if (getDiffInventoryProductList().size() == 0) {
            mTvProductTotalPrice.setVisibility(View.INVISIBLE);
            mTvProductTotalCount.setVisibility(View.INVISIBLE);
        } else {
            //计算总价,总量
            double totalMoney = 0;
            double totalPieces = getDiffInventoryProductList().size();
            for (InventoryResponse.InventoryProduct bean : getDiffInventoryProductList()) {
                totalMoney = totalMoney + (bean.getEditNum() - bean.getTheoreticalQty())*bean.getUnitPrice();
            }

            if (totalPieces != 0) {
                mTvProductTotalCount.setVisibility(View.VISIBLE);
                mTvProductTotalCount.setText("差异商品("+NumberUtil.getIOrD(totalPieces)+"种)");
            } else {
                mTvProductTotalCount.setVisibility(View.GONE);
            }

            if (GlobalApplication.getInstance().getCanSeePrice()) {
                mTvProductTotalPrice.setVisibility(View.VISIBLE);
                if (totalMoney>=0){
                    mTvProductTotalPrice.setTextColor(Color.parseColor("#FF3B30"));
                }else{
                    mTvProductTotalPrice.setTextColor(Color.parseColor("#7bbd4f"));
                }
                mTvProductTotalPrice.setText("¥" + df.format(totalMoney));//TODO:format
            } else {
                mTvProductTotalPrice.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 展示购物车dialog
     */
    protected void showCart(boolean isShow) {
        if (isShow) {
            if (mTvInventoryCache.getVisibility() == View.VISIBLE) {
                //已经在显示，收起
                showCart(false);
                return;
            }
            if (getDiffInventoryProductList().size() == 0) return;
            mTvInventoryCache.setVisibility(View.VISIBLE);
            final View view = findViewById(R.id.include_cart);
            view.setVisibility(View.VISIBLE);
            view.setAnimation(AnimationUtils.loadAnimation(getActivityContext(), R.anim.slide_in_from_bottom));
            mRlCartContainer.setVisibility(View.VISIBLE);
            initCartViews();
        } else {
            mTvInventoryCache.setVisibility(View.GONE);
            findViewById(R.id.include_cart).setVisibility(View.INVISIBLE);
            mRlCartContainer.setVisibility(View.GONE);
        }
    }

    @ViewInject(R.id.rv_cart)
    ListView mmRvCart;
    InventoryAdapter mInventoryAdapter;
    @ViewInject(R.id.stick_header)
    View mVStickHeader;
    @ViewInject(R.id.tv_header)
    TextView mTvHeader;
    @ViewInject(R.id.rl_cart_container)
    View mRlCartContainer;
    @ViewInject(R.id.tv_product_total_count)
    TextView mTvProductTotalCount;
    @ViewInject(R.id.tv_product_total_price)
    TextView mTvProductTotalPrice;

    /**
     * 初始化购物车弹框
     */
    protected void initCartViews() {
        mmRvCart = (ListView) findViewById(R.id.rv_cart);
        mTvHeader = (TextView) findViewById(R.id.tv_header);
        mVStickHeader = findViewById(R.id.stick_header);
        mInventoryAdapter = new InventoryAdapter(getDiffInventoryProductList());
        mmRvCart.setAdapter(mInventoryAdapter);
//        initProductListData();
//        setUpRvCart();
    }

    private List<InventoryResponse.InventoryProduct> getDiffInventoryProductList() {
        List<InventoryResponse.InventoryProduct> inventoryProducts = new ArrayList<>();

        if (mInventoryBean.getLines() != null){
            for (InventoryResponse.InventoryProduct inventoryProduct : mInventoryBean.getLines()) {
                if (inventoryProduct.getEditNum() != inventoryProduct.getTheoreticalQty()) {
                    inventoryProducts.add(inventoryProduct);
                }
            }
        }
        return inventoryProducts;
    }

    /**
     * 盘点商品列表的adapter
     */
    private class InventoryAdapter extends IBaseAdapter<InventoryResponse.InventoryProduct> {

        Queue<View> cacheLotView = new ArrayDeque<>();
        int colorBgChanged;
        int colorBgUnChanged;
        int colorMore;
        LayoutInflater mInflater;
        List<InventoryResponse.InventoryProduct> mInventoryProductList = new ArrayList<>();

        InventoryAdapter(List<InventoryResponse.InventoryProduct> inventoryProducts) {
            colorBgUnChanged = Color.parseColor("#ffffff");
            colorBgChanged = Color.parseColor("#FFF3FBED");
            colorMore = Color.parseColor("#fe451d");
            mInflater = LayoutInflater.from(getActivityContext());
            mInventoryProductList.addAll(inventoryProducts);
            setData(inventoryProducts);
        }

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            InventoryResponse.InventoryProduct inventoryProduct = mInventoryProductList.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.dialog_inventory_item, parent, false);
                viewHolder = new ViewHolder();
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            boolean isLot = !"none".equals(inventoryProduct.getProduct().getTracking());
            //无批次
            if (!isLot) {
                viewHolder.mmEtCount.setVisibility(View.VISIBLE);
                viewHolder.mmTvUom.setVisibility(View.VISIBLE);
                viewHolder.mmTvTheoretical.setVisibility(View.VISIBLE);
                viewHolder.mmEtCount.setText(NumberUtil.getIOrD(inventoryProduct.getEditNum()));
//                盘盈的数量标红
                if (inventoryProduct.getEditNum() > inventoryProduct.getTheoreticalQty()) {
                    viewHolder.mmEtCount.setTextColor(colorMore);
                } else {
                    viewHolder.mmEtCount.setTextColor(getResources().getColor(R.color.color2e2e2e));
                }

                viewHolder.mmTvTheoretical.setText("/" + NumberUtil.getIOrD(inventoryProduct.getTheoreticalQty()));
                viewHolder.mmTvUom.setText(inventoryProduct.getProduct().getProductUom());
            } else {//有批次
                viewHolder.mmEtCount.setVisibility(View.GONE);
                viewHolder.mmTvUom.setVisibility(View.GONE);
                viewHolder.mmTvTheoretical.setVisibility(View.GONE);
            }

            //添加批次信息，注意有可能无批次商品也有批次信息，也显示出来
            //***********************有批次信息***************************
            if (inventoryProduct.getLotList() != null && inventoryProduct.getLotList().size() > 0) {

                viewHolder.mmLayoutContainer.setVisibility(View.VISIBLE);
                boolean isChanged = false;
                for (int i = 0; i < inventoryProduct.getLotList().size(); i++) {
                    View view = viewHolder.mmLayoutContainer.getChildAt(i);
                    LotViewHolder lotViewHolder;
                    if (view != null) {
                        lotViewHolder = (LotViewHolder) view.getTag();
                    } else {
                        //need to add view
                        //get from cache first
                        view = cacheLotView.poll();
                        if (view == null) {//cache is not available, need inflate
                            view = mInflater.inflate(R.layout.item_inventory_lot, viewHolder.mmLayoutContainer, false);
                            lotViewHolder = new LotViewHolder();
                            ViewUtils.inject(lotViewHolder, view);
                            view.setTag(lotViewHolder);
                        }
                        lotViewHolder = (LotViewHolder) view.getTag();
                        viewHolder.mmLayoutContainer.addView(view);
                    }
                    InventoryResponse.InventoryLot inventoryLot = inventoryProduct.getLotList().get(i);

                    //判断有否改变
                    if (inventoryLot.getEditNum() != inventoryLot.getTheoreticalQty())
                        isChanged = true;

                    //判断过期
                    if (!TextUtils.isEmpty(inventoryLot.getLifeEndDate())) {
                        lotViewHolder.mmTvExpire.setVisibility(View.VISIBLE);
                        long dayDiff = DateFormateUtil.getDaysToExpire(inventoryLot.getLifeEndDate());
                        Log.d("haha", inventoryLot.getLifeEndDate() + " " + dayDiff);
                        if (dayDiff == 0) {
                            //今天到期
                            lotViewHolder.mmTvExpire.setText("今天到期");
                            lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.inventory_expire));
                            lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.inventory_expire_bg));
                        } else if (dayDiff < 0) {
                            //过期
                            lotViewHolder.mmTvExpire.setText("已过期");
                            lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.inventory_expire));
                            lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.inventory_expire_bg));
                        } else if (dayDiff <= 3) {
                            //小于3天
                            lotViewHolder.mmTvExpire.setText(dayDiff + "天到期");
                            lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.stock_3_days));
                            lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.stock_3_days_bg));
                        } else {
                            //大于4天
                            lotViewHolder.mmTvExpire.setText(dayDiff + "天到期");
                            lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.stock_4_days));
                            lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.stock_4_days_bg));
                        }
                    } else {
                        lotViewHolder.mmTvExpire.setVisibility(View.GONE);
                    }

                    lotViewHolder.mmTvLotName.setText(inventoryLot.getLotNum() != null ? "批次：" + inventoryLot.getLotNum() : "");
                    lotViewHolder.mmTvTheoretical.setText("/" + NumberUtil.getIOrD(inventoryLot.getTheoreticalQty()));
                    lotViewHolder.mmTvLotCount.setText(NumberUtil.getIOrD(inventoryLot.getEditNum()));
                    lotViewHolder.mmTvLotUom.setText(inventoryLot.getUom());
                    lotViewHolder.inventoryLot = inventoryLot;
                }
                viewHolder.mmRlRoot.setBackgroundColor(isLot && isChanged ? colorBgChanged : colorBgUnChanged);
                //remove and cache unused child view
                int currentSize = inventoryProduct.getLotList().size();
                int totalChild = viewHolder.mmLayoutContainer.getChildCount();
                for (int i = totalChild - 1; i >= currentSize; i--) {//由后往前删，防止index乱
                    View view = viewHolder.mmLayoutContainer.getChildAt(i);
                    if (view == null) break;
                    viewHolder.mmLayoutContainer.removeViewAt(i);
                    cacheLotView.offer(view);
                }

            } else {//**********************无批次信息******************************
                viewHolder.mmLayoutContainer.setVisibility(View.GONE);
            }

            viewHolder.mmTvTitle.setText(inventoryProduct.getProduct().getName());
            viewHolder.mmTvCode.setText(inventoryProduct.getCode());
            viewHolder.mmTvUnit.setText(inventoryProduct.getProduct().getUnit());
            if (inventoryProduct.getProduct().getImage() != null) {
                FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(viewHolder.mmSdvImage, inventoryProduct.getProduct().getImage().getImage());
            }
            return convertView;
        }
    }

    /**
     * 盘点商品列表viewholder
     */
    private class ViewHolder {
        @ViewInject(R.id.rl_item_inventory)
        View mmRlRoot;
        @ViewInject(R.id.sdv_stock_image)
        SimpleDraweeView mmSdvImage;
        @ViewInject(R.id.tv_stock_title)
        TextView mmTvTitle;
        @ViewInject(R.id.tv_stock_code)
        TextView mmTvCode;
        @ViewInject(R.id.tv_stock_unit)
        TextView mmTvUnit;
        @ViewInject(R.id.ll_stock_container)
        LinearLayout mmLayoutContainer;
        @ViewInject(R.id.tv_stock_count)
        TextView mmEtCount;
        @ViewInject(R.id.tv_stock_uom)
        TextView mmTvUom;
        @ViewInject(R.id.tv_stock_theoretical)
        TextView mmTvTheoretical;
        @ViewInject(R.id.iv_callin_icon)
        ImageView mmIvArrow;
    }

    /**
     * 批次行的viewholder
     */
    private class LotViewHolder {
        InventoryResponse.InventoryLot inventoryLot;
        @ViewInject(R.id.tv_stock_lot_expire)
        TextView mmTvExpire;
        @ViewInject(R.id.tv_stock_lot_name)
        TextView mmTvLotName;
        @ViewInject(R.id.tv_stock_lot_count)
        TextView mmTvLotCount;
        @ViewInject(R.id.tv_stock_lot_uom)
        TextView mmTvLotUom;
        @ViewInject(R.id.tv_stock_theoretical)
        TextView mmTvTheoretical;
    }
}
