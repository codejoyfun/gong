package com.runwise.supply.repertory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
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
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.EditRepertoryResult;
import com.runwise.supply.repertory.entity.EditRequest;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.tools.InventoryCacheManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;
import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.repertory.EditRepertoryAddActivity.INTENT_FILTER;
import static com.runwise.supply.repertory.InventoryFragment.INTENT_CATEGORY;

/**
 * 新界面的盘点
 *
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
    InventoryResponse.InventoryBean mInventoryBean;
    CategoryRespone categoryRespone;
    List<Fragment> orderProductFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);
        setTitleText(true,"盘点单");
        showBackBtn();
        setTitleRightIcon2(true,R.drawable.nav_add);
        dragLayout.setOverDrag(false);
        mInventoryBean = (InventoryResponse.InventoryBean) getIntent().getSerializableExtra(INTENT_KEY_INVENTORY_BEAN);
        mTvInventoryId.setText(String.valueOf(mInventoryBean.getInventoryID()));
        mTvInventoryPerson.setText(mInventoryBean.getCreateUser());
        mTvInventoryDate.setText(mInventoryBean.getCreateDate());
        initDialog();
        getCategory();
    }

    /**
     * 查询类别
     */
    private void getCategory(){
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage();
                break;
            case INVENTORY_COMMIT:
                InventoryCacheManager inventoryCacheManager = new InventoryCacheManager(this);
                inventoryCacheManager.removeInventory(mInventoryBean.getInventoryID());
                ToastUtil.show(mContext,"盘点成功");
                Intent intent = new Intent(mContext,EditRepertoryFinishActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private void setUpDataForViewPage() {
        orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<InventoryResponse.InventoryProduct>> map = new HashMap<>();
        titles.add("全部");
        for(String category:categoryRespone.getCategoryList()){
            titles.add(category);
            map.put(category,new ArrayList<>());
        }

        for (InventoryResponse.InventoryProduct inventoryProduct : mInventoryBean.getLines()) {
            ProductBasicList.ListBean listBean = inventoryProduct.getProduct();
            if (!TextUtils.isEmpty(listBean.getCategory())){
                ArrayList<InventoryResponse.InventoryProduct> productByCategory = map.get(listBean.getCategory());
                if (productByCategory == null) {
                    productByCategory = new ArrayList<>();
                    map.put(listBean.getCategory(), productByCategory);
                }
                productByCategory.add(inventoryProduct);
            }
        }

        for(String category:categoryRespone.getCategoryList()){
            ArrayList<InventoryResponse.InventoryProduct> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(category,value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        //加入全部
        orderProductFragmentList.add(0, newProductFragment("",mInventoryBean.getLines()));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        viewpager.setOffscreenPageLimit(orderProductFragmentList.size());
        tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewpager.setCurrentItem(position);
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
        bundle.putString(INTENT_CATEGORY,category);
        editRepertoryListFragment.setArguments(bundle);
        editRepertoryListFragment.setData(value);
        return editRepertoryListFragment;
    }

    @OnClick({R.id.tv_inventory_commit,R.id.tv_inventory_cache,R.id.title_iv_rigth2})
    public void onBtnClicked(View v){
        switch (v.getId()){
            case R.id.tv_inventory_commit:
                commit();
                //TODO:提交请求
                break;
            case R.id.tv_inventory_cache:
                new InventoryCacheManager(this).saveInventory(mInventoryBean);
                finish();
                break;
            case R.id.title_iv_rigth2:
                //增加新商品，传入已有商品进行过滤
                Intent intent = new Intent(this,EditRepertoryAddActivity.class);
                ArrayList<Integer> filters = new ArrayList<>();
                for(InventoryResponse.InventoryProduct product:mInventoryBean.getLines()){
                    filters.add(product.getProductID());
                }
                intent.putExtra(INTENT_FILTER,filters);
                startActivity(intent);
                break;
        }
    }

    /**
     * 设置初始盘点值
     * @param inventoryResponse
     */
    private void initData(InventoryResponse inventoryResponse){
        List<InventoryResponse.InventoryProduct> productList = inventoryResponse.getInventory().getLines();
        for(InventoryResponse.InventoryProduct product:productList){
            if(product.getLotList()!=null){
                for(InventoryResponse.InventoryLot batch:product.getLotList()){
                    batch.setEditNum(batch.getTheoreticalQty());
                }
            }
        }
    }

    /**
     * 添加新商品
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewBean(NewAdd newBean) {
        dragLayout.toggleTopView();
        if (newBean.getType() == 1) {
            boolean isFind = false;
            for (InventoryResponse.InventoryProduct bean : mInventoryBean.getLines()) {
                if (bean.getProductID()==newBean.getInventoryProduct().getProductID()) {
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
    @ViewInject(R.id.name1)
    private TextView mTvTitle;
    @ViewInject(R.id.colseIcon1)
    private ImageView mIvClose;
    @ViewInject(R.id.et_product_amount1)
    private EditText mEtCount;
    @ViewInject(R.id.rl_dialog_add_sum)
    private View mIncludeSumDialog;
    @ViewInject(R.id.finalButton1)
    private TextView mTvButton;
    private void initDialog(){
        mIvClose.setOnClickListener(v->{
            finishDialog();
        });
        mIncludeSumDialog.setOnClickListener(v->{
            finishDialog();
        });
    }

    /**
     * 显示修改没有批次的商品数量
     * @param inventoryProduct
     */
    public void showAddSumDialog(InventoryResponse.InventoryProduct inventoryProduct){
        mIncludeSumDialog.setVisibility(View.VISIBLE);
        mTvTitle.setText(inventoryProduct.getProduct().getName());
        mEtCount.setText(NumberUtil.getIOrD(inventoryProduct.getEditNum()));
        mEtCount.selectAll();
        mTvButton.setOnClickListener(v->{
            String etValue = mEtCount.getText().toString();
            if(TextUtils.isDigitsOnly(etValue)){
                inventoryProduct.setEditNum(Double.valueOf(etValue));
                EventBus.getDefault().post(new InventoryEditEvent());
            }
            finishDialog();
        });
    }

    private void finishDialog(){
        hideKeyboard();
        mIncludeSumDialog.setVisibility(View.GONE);
    }


    InputMethodManager imm;
    private void hideKeyboard(){
        if(imm==null)imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if(imm!=null && v!=null)imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void commit(){
        EditRequest editRequest = new EditRequest();
        editRequest.setId(mInventoryBean.getInventoryID());
        editRequest.setState("done");
        List<EditRequest.ProductBean> editListBean = new ArrayList<>();
        for(InventoryResponse.InventoryProduct bean : mInventoryBean.getLines()) {
            if(bean.getLotList()==null){//无批次
                EditRequest.ProductBean productBean = new EditRequest.ProductBean();
                productBean.setProduct_id(bean.getProductID());
                productBean.setId((int)bean.getInventoryLineID());
                productBean.setActual_qty(bean.getEditNum());
                productBean.setLot_id((int)bean.getLotID());
                productBean.setLot_num(bean.getLotNum());
                editListBean.add(productBean);
                continue;
            }
            for(InventoryResponse.InventoryLot lot:bean.getLotList()){
                EditRequest.ProductBean productBean = new EditRequest.ProductBean();
                productBean.setProduct_id(bean.getProductID());
                productBean.setId(lot.getInventoryLineID());
                productBean.setActual_qty(lot.getEditNum());
                productBean.setLot_id(lot.getLotID());
                productBean.setLot_num(lot.getLotNum());
                if(lot.isProductDate() && lot.isNewAdded())productBean.setProduce_datetime(lot.getProductDate());
                else if(!lot.isProductDate() && lot.isNewAdded())productBean.setLife_datetime(lot.getProductDate());
                editListBean.add(productBean);
            }

        }
        editRequest.setInventory_lines(editListBean);

//							sendConnection("/gongfu/shop/inventory/state",editRequest,PRODUCT_COMMIT,true, EditRepertoryResult.class);
        sendConnection("/api/inventory/state",editRequest,INVENTORY_COMMIT,true, EditRepertoryResult.class);
    }
}
