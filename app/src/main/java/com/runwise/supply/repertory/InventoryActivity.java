package com.runwise.supply.repertory;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.view.residemenu.DragLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.InventoryCacheManager;
import com.runwise.supply.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

/**
 * Created by Dong on 2017/12/8.
 */

public class InventoryActivity extends NetWorkActivity {

    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.drag_layout)
    private DragTopLayout dragLayout;
    @ViewInject(R.id.viewpager)
    private ViewPager viewpager;
    InventoryResponse mInventoryResponse;
    CategoryRespone categoryRespone;
    List<Fragment> orderProductFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);
        setTitleText(true,"盘点单");
        showBackBtn();

        dragLayout.setOverDrag(false);
        TestData();
        getCategory();
    }

    private void TestData(){
        InventoryResponse inventoryResponse = new InventoryResponse();
        InventoryResponse.InventoryBean inventoryBean = new InventoryResponse.InventoryBean();
        inventoryResponse.setInventory(inventoryBean);
        inventoryBean.setProductList(new ArrayList<>());
        for(int i=0;i<10;i++){
            InventoryResponse.InventoryProduct inventoryProduct = new InventoryResponse.InventoryProduct();
            inventoryProduct.setProductID(651);

            inventoryBean.getProductList().add(inventoryProduct);
            inventoryProduct.setLotList(new ArrayList<>());
            for(int j=0;j<10;j++){
                InventoryResponse.InventoryLot lot = new InventoryResponse.InventoryLot();
                lot.setLifeEndDate("10-01-23");
                lot.setTheoreticalQty(10);
                lot.setLotNum("313251235"+i+j);
                inventoryProduct.getLotList().add(lot);
            }
        }
        mInventoryResponse = inventoryResponse;
    }

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

        for (InventoryResponse.InventoryProduct inventoryProduct : mInventoryResponse.getInventory().getProductList()) {
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(this).get(String.valueOf(inventoryProduct.getProductID()));
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
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        //加入全部
        orderProductFragmentList.add(0, newProductFragment(mInventoryResponse.getInventory().getProductList()));

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

    public Fragment newProductFragment(List<InventoryResponse.InventoryProduct> value) {
        InventoryFragment editRepertoryListFragment = new InventoryFragment();
        editRepertoryListFragment.setData(value);
        return editRepertoryListFragment;
    }

    @OnClick({R.id.tv_inventory_commit,R.id.tv_inventory_cache})
    public void onBtnClicked(View v){
        switch (v.getId()){
            case R.id.tv_inventory_commit:
                //TODO:提交请求
                break;
            case R.id.tv_inventory_cache:
                new InventoryCacheManager(this).saveInventory(mInventoryResponse.getInventory());
                break;
        }
    }

    /**
     * 设置初始盘点值
     * @param inventoryResponse
     */
    private void initData(InventoryResponse inventoryResponse){
        List<InventoryResponse.InventoryProduct> productList = inventoryResponse.getInventory().getProductList();
        for(InventoryResponse.InventoryProduct product:productList){
            if(product.getLotList()!=null){
                for(InventoryResponse.InventoryLot batch:product.getLotList()){
                    batch.setEditNum(batch.getTheoreticalQty());
                }
            }
        }
    }
}
