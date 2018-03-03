package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductAdapterV2;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.CategoryChildResponse;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.ListContainer;

import org.greenrobot.eventbus.Subscribe;

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
        requestChildCategory();
        HashMap<String, Long> childBadges = ((ProductActivityV2)getActivity()).getChildBadges();
        mListContainer.getTypeAdapter().updateBadge(childBadges);
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
    List<ProductBasicList.ListBean> mProductList = new ArrayList<>();
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
        mProductList.clear();
        Iterator iter = mChildProductMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String categoryChild = (String) entry.getKey();
            mProductList.addAll((ArrayList<ProductBasicList.ListBean>) entry.getValue());
            if (!TextUtils.isEmpty(categoryChild)){
                categoryList.add(categoryChild);
            }
        }
        mLoadingLayout.setVisibility(View.GONE);

        mListContainer.init(mCategory,mProductList,categoryList,((ProductActivityV2)getActivity()).getProductCountSetter());

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
        } else {
            isNeedUpdate = true;
        }

        if (!isNeedUpdate) {
            ProductListFragmentV2 fragment = (ProductListFragmentV2) getChildFragmentManager().findFragmentByTag(mCurrentSubCategory);
            fragment.refresh(true);
            return;
        }
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
        }
    }

    /**
     * 其它地方修改
     * @param event
     */
    @Subscribe
    public void updateProductCount(ProductCountUpdateEvent event){
        List<ProductBasicList.ListBean> listBeans = null;
        if (event.bean != null){
            listBeans = mChildProductMap.get(event.bean.getCategoryChild());
        }else{
            //购物车商品删除调用的逻辑
            ((ProductActivityV2)getActivity()).initChildBadges();
            mListContainer.getTypeAdapter().updateBadge(((ProductActivityV2)getActivity()).getChildBadges());
            for (ProductBasicList.ListBean listBean:event.beanList){
                if (listBean .getCategoryParent().equals(mCategory)){
                    for (int i = 0;i<mProductList.size();i++){
                        ProductBasicList.ListBean tempListBean = mProductList.get(i);
                        if (tempListBean.getProductID() == listBean.getProductID()){
                            mListContainer.getProductAdapterV2().notifyItemChanged(i);
                        }
                    }
                }
            }
        }

        if (event.getException()!=null&& event.getException().getClass() == ProductAdapterV2.class){
            if (listBeans != null){
                ((ProductActivityV2)getActivity()).initChildBadges();
                mListContainer.getTypeAdapter().updateBadge(((ProductActivityV2)getActivity()).getChildBadges());
            }
            return;
        }
        if(listBeans != null){
            ((ProductActivityV2)getActivity()).initChildBadges();
            mListContainer.getTypeAdapter().updateBadge(((ProductActivityV2)getActivity()).getChildBadges());
            for (ProductBasicList.ListBean listBean:listBeans){
                if (listBean.getProductID() == event.bean.getProductID()){
                    mListContainer.getProductAdapterV2().notifyDataSetChanged();
                    break;
                }
            }
        }


    }

}
