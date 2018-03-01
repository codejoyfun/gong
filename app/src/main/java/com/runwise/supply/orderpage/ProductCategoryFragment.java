package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.CategoryChildResponse;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.ListContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 单个一级分类的商品列表
 * 包含多个一个/多个二级分类fragment
 */
public class ProductCategoryFragment extends NetWorkFragment {

    public static final String INTENT_KEY_CATEGORY = "ap_category";
    public static final String INTENT_KEY_FIRST = "first";

    private String mCategory;
    @ViewInject(R.id.listcontainer)
    private ListContainer mListContainer;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    private String mCurrentSubCategory = null;
    private List<String> mCategoryChildList;//子类别列表

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getString(INTENT_KEY_CATEGORY);
        mListContainer.init();


        //按比例设置二级分类列表宽度
//        mRvSubCategory.getLayoutParams().width = (int) (GlobalConstant.screenW * 0.2);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_product_category;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
    }

    public void refresh() {
        Fragment newFragment = getChildFragmentManager().findFragmentByTag(mCurrentSubCategory);
        if (newFragment != null) {
            ((ProductListFragmentV2) newFragment).refresh(false);
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        mLoadingLayout.setOnRetryClickListener(v -> {
            requestChildCategory();
        });
        mLoadingLayout.onFailure(errMsg, R.drawable.nonocitify_icon);
    }


    HashMap<String, List<ProductBasicList.ListBean>> mChildProductMap;
    public HashMap<String, List<ProductBasicList.ListBean>> getChildProductMap(){
        return mChildProductMap;
    }

    public void requestChildCategory() {
        //查询二级分类
        HashMap<String, List<ProductBasicList.ListBean>> productMap = ((ProductActivityV2) getActivity()).getProductMap();
        List<ProductBasicList.ListBean> listBeans = productMap.get(mCategory);

        mChildProductMap = new HashMap<>();
        for (int i = 0; i < listBeans.size(); i++) {
            ProductBasicList.ListBean bean = listBeans.get(i);
            String categoryChild = bean.getCategoryChild();
            List<ProductBasicList.ListBean> beanList;
            if (mChildProductMap.containsKey(categoryChild)) {
                beanList = mChildProductMap.get(categoryChild);
                beanList.add(bean);
            } else {
                beanList = new ArrayList<>();
                beanList.add(bean);
                mChildProductMap.put(categoryChild, beanList);
            }
        }
        List<String> categoryList = new ArrayList<>();
        Iterator iter = mChildProductMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String categoryChild = (String) entry.getKey();
            if (!TextUtils.isEmpty(categoryChild)){
                categoryList.add(categoryChild);
            }
        }


        mLoadingLayout.setVisibility(View.GONE);
        //根据子类别加入fragment
        //适配没有二级分类,加一个空的tag
        CategoryChildResponse categoryChildResponse = new CategoryChildResponse();
        categoryChildResponse.setCategoryChild(categoryList);

        //检查接口返回的二级分类和现有的是否一致
        boolean isNeedUpdate = false;//是否需要刷新二级分类
        if (mCategoryChildList == null) {//初次进入，没有子分类
            isNeedUpdate = true;
        } else if (categoryChildResponse.getCategoryChild() != null) {
            if (mCategoryChildList.size() != categoryChildResponse.getCategoryChild().size()) {//原有子分类数量不一样
                isNeedUpdate = true;
            } else {
                for (int i = 0; i < mCategoryChildList.size(); i++) {
                    String childCategory = mCategoryChildList.get(i);
                    if (!childCategory.equals(categoryChildResponse.getCategoryChild().get(i))) {
                        isNeedUpdate = true;
                        break;
                    }
                }
            }
        } else {//mCategoryChildList!=null && categoryChildResponse.getCategoryChild()==null
            isNeedUpdate = true;
        }

//                isNeedUpdate = true;
        //不需要更新二级分类，刷新当前列表
        if (!isNeedUpdate) {
            ProductListFragmentV2 fragment = (ProductListFragmentV2) getChildFragmentManager().findFragmentByTag(mCurrentSubCategory);
            fragment.refresh(true);
            return;
        }

        //*****需要更新二级分类****
        mCurrentSubCategory = null;

        //清空现有fragment
        //NOTE:remove居然没有用，在之后的findTagById还是会找出来，但是最后却还是remove了，可能是因为是队列处理的原因？
        //现在先用detach
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                getChildFragmentManager().beginTransaction().detach(fragment).commitAllowingStateLoss();
            }
        }

//                if(mCategoryChildList!=null){
//                    for(String category:mCategoryChildList){
//                        transaction.remove(getChildFragmentManager().findFragmentByTag(category));
//                    }
//                }

        mCategoryChildList = categoryChildResponse.getCategoryChild();
        if (categoryChildResponse.getCategoryChild() == null || categoryChildResponse.getCategoryChild().isEmpty()) {
            //没有子类别
            //隐藏子类别选择列表，并加入一个空的子类别
            mRvSubCategory.setVisibility(View.GONE);
            mCategoryChildList = new ArrayList<>();
            mCategoryChildList.add("");
        } else {
            mRvSubCategory.setVisibility(View.VISIBLE);
        }
        //切换到第一个子类别，并刷新
        switchSubCategory(mCategoryChildList.get(0));

    }

    /**
     * 新建子类商品列表fragment
     *
     * @param subCategory 子类名称
     * @return
     */
    public ProductListFragmentV2 newProductListFragment(String subCategory) {
        ProductListFragmentV2 productListFragment = new ProductListFragmentV2();
        Bundle bundle = new Bundle();
        bundle.putString(ProductListFragmentV2.INTENT_KEY_CATEGORY, mCategory);
        bundle.putString(ProductListFragmentV2.INTENT_KEY_SUB_CATEGORY, subCategory);
        if (mCategoryChildList.size() > 1) {//有多个子分类
            bundle.putBoolean(ProductListFragmentV2.INTENT_KEY_HAS_OTHER_SUB, true);
        }
        productListFragment.setArguments(bundle);
        return productListFragment;
    }

    /**
     * 转换子类对应的fragment
     *
     * @param subCategory 子类名称
     */
    private void switchSubCategory(String subCategory) {
        if (subCategory.equals(mCurrentSubCategory)) return;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment currentFragment = getChildFragmentManager().findFragmentByTag(mCurrentSubCategory);
        if (currentFragment != null) {
            ft.detach(currentFragment);//用attach和detach，onCreate只会被调用一次
        }
        mCurrentSubCategory = subCategory;
        Fragment newFragment = getChildFragmentManager().findFragmentByTag(subCategory);
        if (newFragment != null) {
            ft.attach(newFragment);
            ((ProductListFragmentV2) newFragment).refresh(true);
        } else {
            newFragment = newProductListFragment(subCategory);
//            newFragment.getArguments().putBoolean(INTENT_KEY_FIRST_LOAD,true);//表示加载的同时需要查询商品列表接口
            ft.add(R.id.fl_product_list_container, newFragment, subCategory);
        }
        ft.commitAllowingStateLoss();
        mSubCategoryAdapter.notifyDataSetChanged();

        //ft.replace(R.id.fl_product_list_container,mMapProductListFragments.get(subCategory),subCategory);
//        ProductListFragmentV2 fragment = mMapProductListFragments.get(subCategory);
//        if(mCurrentSubCategory!=null)ft.hide(mMapProductListFragments.get(mCurrentSubCategory));
//        ft.show(fragment);
//        ft.commitAllowingStateLoss();
//        fragment.requestChildCategory();
//        mCurrentSubCategory = subCategory;
//        mSubCategoryAdapter.notifyDataSetChanged();
    }

    /**
     * 子分类列表adapter
     */
    private class SubCategoryAdapter extends RecyclerView.Adapter<ViewHolder> {
        int selectColor = getResources().getColor(R.color.color4bb400);
        int unSelectColor = getResources().getColor(R.color.color999999);

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_sub_category, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String subCategory = mCategoryChildList.get(position);
            holder.mmSubCategory = subCategory;
            holder.mmTvSubCategory.setText(subCategory);
            if (mCurrentSubCategory.equals(holder.mmTvSubCategory.getText())) {
                holder.mmTvSubCategory.setTextColor(selectColor);
            } else {
                holder.mmTvSubCategory.setTextColor(unSelectColor);
            }
        }

        @Override
        public int getItemCount() {
            return mCategory == null || mCategoryChildList == null ? 0 : mCategoryChildList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
     * 添加展示特价专区子分类
     * @param productList
     */
//    @Deprecated
//    public void showSpecialSaleFragment(String tagName,List<ProductData.ListBean> productList){
//        if(productList==null || productList.isEmpty()){//没有特价商品
//            shouldShowSubCategory();
//            return;
//        }
//        if(mCategory.getCategoryChild().contains(tagName))return;//已经添加了，跳过
//        mCategory.getCategoryChild().add(0,tagName);//增加特价子分类
//        mRvSubCategory.setVisibility(View.VISIBLE);
//        vContainer.setVisibility(View.INVISIBLE);//先隐藏掉container，防止上一个fragment出现
//        mRvSubCategory.getAdapter().notifyDataSetChanged();
//
//        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//        Fragment currentFragment = getChildFragmentManager().findFragmentByTag(mCurrentSubCategory);
//        if(currentFragment!=null){
//            ft.detach(currentFragment);
//        }
//        mCurrentSubCategory = tagName;
//        Fragment newFragment = newProductListFragment(tagName);
//        ArrayList<ProductData.ListBean> arrayList = new ArrayList<>();
//        arrayList.addAll(productList);
//        newFragment.getArguments().putParcelableArrayList(INTENT_KEY_INIT_DATA,arrayList);
//        ft.add(R.id.fl_product_list_container,newFragment,tagName);
//        ft.commitAllowingStateLoss();
//        mSubCategoryAdapter.notifyDataSetChanged();
//    }
}
