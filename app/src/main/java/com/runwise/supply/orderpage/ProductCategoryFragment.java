package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单个一级分类的商品列表
 * 包含多个一个/多个二级分类fragment
 */
public class ProductCategoryFragment extends NetWorkFragment {

    public static final String INTENT_KEY_CATEGORY = "ap_category";
    private CategoryResponseV2.Category mCategory;
    @ViewInject(R.id.rv_sub_category)
    private RecyclerView mRvSubCategory;//子类别的列表view
    private SubCategoryAdapter mSubCategoryAdapter;
    private Map<String,ProductListFragmentV2> mMapProductListFragments = new HashMap<>();//子类别对应的商品列表fragment
    private String mCurrentSubCategory = "";
    private boolean hasSubCategory = false;//是否含有二级分类

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getParcelable(INTENT_KEY_CATEGORY);
        if(mCategory.getCategoryChild()==null || mCategory.getCategoryChild().length==0){
            //没有二级分类
            //隐藏二级分类
            mRvSubCategory.setVisibility(View.GONE);
        }else{
            //有二级分类
            hasSubCategory = true;
            mSubCategoryAdapter = new SubCategoryAdapter();
            mRvSubCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            mRvSubCategory.setAdapter(mSubCategoryAdapter);
        }

        //必须在onCreate的时候就加载一个子fragment，否则在滑动时加载的话会有顿挫
        //子Fragment加载后不查接口，只有实际到前台展示时，才通过show()查接口
        //加载第一个子fragment
        if(hasSubCategory){//包含二级分类
            //按比例设置二级分类列表宽度
            mRvSubCategory.getLayoutParams().width = (int)(GlobalConstant.screenW * 0.2);
            mRvSubCategory.requestLayout();

            mCurrentSubCategory = mCategory.getCategoryChild()[0];
            Fragment newFragment = newProductListFragment(mCurrentSubCategory);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fl_product_list_container,newFragment,mCurrentSubCategory)
                    .commit();
        }else {//只有一级分类
            //直接加入空的二级分类fragment
            Fragment fragment = newProductListFragment("");
            fragment.getArguments().putBoolean(ProductListFragmentV2.INTENT_KEY_HAS_OTHER_SUB,false);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fl_product_list_container,fragment)
                    .commitAllowingStateLoss();

        }

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

    /**
     * 每次当前tab被选择的时候被调用，然后再通知当前显示的子fragment，用于懒加载策略
     */
    public void onSelected() {
        Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.fl_product_list_container);
        if(currentFragment!=null){
            ProductListFragmentV2 fragment = (ProductListFragmentV2)currentFragment;
            fragment.show();
        }
        //检查是否已经有子fragment被加载了
//        List<Fragment> fragmentList = getChildFragmentManager().getFragments();
//        if(fragmentList!=null && fragmentList.size()>0)return;
//        //加载第一个子fragment
//        if(hasSubCategory){//包含二级分类
//            switchSubCategory(mCategory.getCategoryChild()[0]);
//        }else {//只有一级分类
//            //直接加入空的二级分类fragment
//            getChildFragmentManager().beginTransaction()
//                    .add(R.id.fl_product_list_container,newProductListFragment("noSub"))
//                    .commitAllowingStateLoss();
//
//        }
    }

    /**
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
     * @param subCategory
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
            newFragment.getArguments().putBoolean("firstLoad",true);
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
            String subCategory = mCategory.getCategoryChild()[position];
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
            return mCategory==null||mCategory.getCategoryChild()==null?0:mCategory.getCategoryChild().length;
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
}
