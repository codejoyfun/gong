package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.CategoryResponseV2;
import com.runwise.supply.orderpage.entity.ProductData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.runwise.supply.orderpage.ProductListFragmentV2.INTENT_KEY_FIRST_LOAD;
import static com.runwise.supply.orderpage.ProductListFragmentV2.INTENT_KEY_INIT_DATA;


/**
 * 单个一级分类的商品列表
 * 包含多个一个/多个二级分类fragment
 */
public class ProductCategoryFragment extends NetWorkFragment {

    public static final String INTENT_KEY_CATEGORY = "ap_category";
    private CategoryResponseV2.Category mCategory;
    @ViewInject(R.id.rv_sub_category)
    private RecyclerView mRvSubCategory;//子类别的列表view
    @ViewInject(R.id.fl_product_list_container)
    private View vContainer;
    private SubCategoryAdapter mSubCategoryAdapter;
    private String mCurrentSubCategory = "";
    //private boolean hasSubCategory = false;//是否含有二级分类

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getParcelable(INTENT_KEY_CATEGORY);

        //适配没有二级分类,加一个空的tag
        if(mCategory.getCategoryChild()==null)mCategory.setCategoryChild(new ArrayList<String>());
        if(mCategory.getCategoryChild().isEmpty())mCategory.getCategoryChild().add("");

        mSubCategoryAdapter = new SubCategoryAdapter();
        mRvSubCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRvSubCategory.setAdapter(mSubCategoryAdapter);
        mRvSubCategory.setVisibility(View.GONE);

        //按比例设置二级分类列表宽度
        mRvSubCategory.getLayoutParams().width = (int)(GlobalConstant.screenW * 0.2);
        mRvSubCategory.requestLayout();

        //必须在onCreate的时候就加载一个子fragment，否则在滑动时加载的话会有顿挫
        //子Fragment加载后不查接口，只有实际到前台展示时，才通过firstLoad()查接口
        mCurrentSubCategory = mCategory.getCategoryChild().get(0);
        Fragment newFragment = newProductListFragment(mCurrentSubCategory);
        getChildFragmentManager().beginTransaction()
                .add(R.id.fl_product_list_container,newFragment,mCurrentSubCategory)
                .commit();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_product_category;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        //empty
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        //empty
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("haha","setUserVisibleHint:"+(mCategory==null?"null":mCategory.getCategoryParent())+" isVisible:"+isVisible());
    }

    /**
     * 每次当前tab被选择的时候调用，然后再通知当前显示的子fragment，用于懒加载策略
     */
    public void onSelected() {;
        if(!isAdded())return;
        Log.d("haha",mCategory.getCategoryParent()+" onSelected");
        Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.fl_product_list_container);
        if(currentFragment!=null){
            ProductListFragmentV2 fragment = (ProductListFragmentV2)currentFragment;
            fragment.firstLoad();
        }
    }

    /**
     * 新建子类商品列表fragment
     * @param subCategory 子类名称
     * @return
     */
    public ProductListFragmentV2 newProductListFragment(String subCategory) {
        ProductListFragmentV2 productListFragment = new ProductListFragmentV2();
        Bundle bundle = new Bundle();
        bundle.putString(ProductListFragmentV2.INTENT_KEY_CATEGORY,mCategory.getCategoryParent());
        bundle.putString(ProductListFragmentV2.INTENT_KEY_SUB_CATEGORY,subCategory);
        productListFragment.setArguments(bundle);
        return productListFragment;
    }

    /**
     * 转换子类对应的fragment
     * @param subCategory 子类名称
     *
     */
    private void switchSubCategory(String subCategory){
        if(subCategory.equals(mCurrentSubCategory))return;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment currentFragment = getChildFragmentManager().findFragmentByTag(mCurrentSubCategory);
        if(currentFragment!=null){
            ft.detach(currentFragment);//用attach和detach，onCreate只会被调用一次
        }
        mCurrentSubCategory = subCategory;
        Fragment newFragment = getChildFragmentManager().findFragmentByTag(subCategory);
        if(newFragment!=null){
            ft.attach(newFragment);
        }
        else{
            newFragment = newProductListFragment(subCategory);
            newFragment.getArguments().putBoolean(INTENT_KEY_FIRST_LOAD,true);//表示加载的同时需要查询商品列表接口
            ft.add(R.id.fl_product_list_container,newFragment,subCategory);
        }
        ft.commitAllowingStateLoss();
        mSubCategoryAdapter.notifyDataSetChanged();

        //ft.replace(R.id.fl_product_list_container,mMapProductListFragments.get(subCategory),subCategory);
//        ProductListFragmentV2 fragment = mMapProductListFragments.get(subCategory);
//        if(mCurrentSubCategory!=null)ft.hide(mMapProductListFragments.get(mCurrentSubCategory));
//        ft.show(fragment);
//        ft.commitAllowingStateLoss();
//        fragment.refresh();
//        mCurrentSubCategory = subCategory;
//        mSubCategoryAdapter.notifyDataSetChanged();
    }

    /**
     * 子分类列表adapter
     */
    private class SubCategoryAdapter extends RecyclerView.Adapter<ViewHolder>{
        int selectColor = getResources().getColor(R.color.color4bb400);
        int unSelectColor = getResources().getColor(R.color.color999999);
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_sub_category,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String subCategory = mCategory.getCategoryChild().get(position);
            holder.mmSubCategory = subCategory;
            holder.mmTvSubCategory.setText(subCategory);
            if(mCurrentSubCategory.equals(holder.mmTvSubCategory.getText())){
                holder.mmTvSubCategory.setTextColor(selectColor);
            }else{
                holder.mmTvSubCategory.setTextColor(unSelectColor);
            }
        }

        @Override
        public int getItemCount() {
            return mCategory==null||mCategory.getCategoryChild()==null?0:mCategory.getCategoryChild().size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        String mmSubCategory;
        TextView mmTvSubCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            mmTvSubCategory = (TextView) itemView.findViewById(R.id.tv_sub_category);
            mmTvSubCategory.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switchSubCategory(mmSubCategory);
        }
    }

    /**
     * 只有一个子分类，不显示子分类
     */
    private void shouldShowSubCategory(){
        if(mCategory.getCategoryChild().size()==1)mRvSubCategory.setVisibility(View.GONE);
        else mRvSubCategory.setVisibility(View.VISIBLE);
    }

    /**
     * 添加展示特价专区子分类
     * @param productList
     */
    public void showSpecialSaleFragment(String tagName,List<ProductData.ListBean> productList){
        if(productList==null || productList.isEmpty()){//没有特价商品
            shouldShowSubCategory();
            return;
        }
        if(mCategory.getCategoryChild().contains(tagName))return;//已经添加了，跳过
        mCategory.getCategoryChild().add(0,tagName);//增加特价子分类
        mRvSubCategory.setVisibility(View.VISIBLE);
        vContainer.setVisibility(View.INVISIBLE);//先隐藏掉container，防止上一个fragment出现
        mRvSubCategory.getAdapter().notifyDataSetChanged();

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment currentFragment = getChildFragmentManager().findFragmentByTag(mCurrentSubCategory);
        if(currentFragment!=null){
            ft.detach(currentFragment);
        }
        mCurrentSubCategory = tagName;
        Fragment newFragment = newProductListFragment(tagName);
        ArrayList<ProductData.ListBean> arrayList = new ArrayList<>();
        arrayList.addAll(productList);
        newFragment.getArguments().putParcelableArrayList(INTENT_KEY_INIT_DATA,arrayList);
        ft.add(R.id.fl_product_list_container,newFragment,tagName);
        ft.commitAllowingStateLoss();
        mSubCategoryAdapter.notifyDataSetChanged();
    }

    /**
     * 把fragment的container显示出来
     */
    public void show(){
        vContainer.setVisibility(View.VISIBLE);
    }
}
