package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.ProductListResponse;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.ListContainer;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_PRODUCT_CATEGORY_LIST;


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

    private boolean isLive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLive = true;
        mCategory = getArguments().getString(INTENT_KEY_CATEGORY);
        requestChildCategory();
        HashMap<String, Long> childBadges = ((ProductActivityV2) getActivity()).getChildBadges();
        if (mListContainer.getTypeAdapter() != null) {
            mListContainer.getTypeAdapter().updateBadge(childBadges);
        }
        //按比例设置二级分类列表宽度
//        mRvSubCategory.getLayoutParams().width = (int) (GlobalConstant.screenW * 0.2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLive = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&isLive){

        }
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

    public HashMap<String, List<ProductBasicList.ListBean>> getChildProductMap() {
        return mChildProductMap;
    }

    List<ProductBasicList.ListBean> mProductList = new ArrayList<>();

    public void requestChildCategory() {
        long mStartTime;
        mStartTime = System.currentTimeMillis();
        //查询二级分类
        List<ProductBasicList.ListBean> listBeans = ((ProductActivityV2) getActivity()).getProductMap().get(mCategory);
        //        促销商品
        ArrayList<ProductBasicList.ListBean> salesPromotionList = new ArrayList<>();
        if (listBeans == null) {
            switch (mCategory) {
                case "全部":
                    mLoadingLayout.setVisibility(View.GONE);
//                    ((ProductActivityV2) getActivity()).getListBeans()
                    mProductList = ((ProductActivityV2) getActivity()).getListBeans();
                    mListContainer.init(mCategory, ((ProductActivityV2) getActivity()).getListBeans(), null, ((ProductActivityV2) getActivity()).getProductCountSetter());
                    android.util.Log.i("onGlobalLayout 全部", String.valueOf(mStartTime - System.currentTimeMillis()));
                    return;
//                case "促销商品":
//                    //            一级分类是促销商品
//                    listBeans = ((ProductActivityV2) getActivity()).getListBeans();
//                    for (int i = 0; i < listBeans.size(); i++) {
//                        ProductBasicList.ListBean bean = listBeans.get(i);
//                        if (mCategory.equals(getString(R.string.sales_promotion)) && bean.getProductTag().equals(getString(R.string.sales_promotion))) {
//                            salesPromotionList.add(bean);
//                        }
//                    }
//                    mLoadingLayout.setVisibility(View.GONE);
//                    mProductList = salesPromotionList;
//                    mListContainer.init(mCategory, salesPromotionList, null, ((ProductActivityV2) getActivity()).getProductCountSetter());
//                    android.util.Log.i("onGlobalLayout 促销商品", String.valueOf(mStartTime - System.currentTimeMillis()));
//                    return;
                default:
                    mListContainer.setVisibility(View.GONE);
                    return;
            }

        }
        mChildProductMap = new HashMap<>();

        for (int i = 0; i < listBeans.size(); i++) {
            ProductBasicList.ListBean bean = listBeans.get(i);
//            二级分类是促销商品
            if (!TextUtils.isEmpty(bean.getCategoryChild()) && bean.getProductTag()!=null && bean.getProductTag().equals(getString(R.string.sales_promotion))) {
                ProductBasicList.ListBean cloneListBean = (ProductBasicList.ListBean) bean.clone();
                cloneListBean.setCategoryChild(getString(R.string.sales_promotion));
                salesPromotionList.add(cloneListBean);
            }
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

        mChildProductMap.put(getString(R.string.sales_promotion), salesPromotionList);
        List<String> categoryList = new ArrayList<>();
        mProductList.clear();
        List<ProductListResponse.CategoryBean> categoryBeans = (List<ProductListResponse.CategoryBean>) SPUtils.readObject(getActivity(), FILE_KEY_PRODUCT_CATEGORY_LIST);
        for (ProductListResponse.CategoryBean categoryBean : categoryBeans) {
            if (categoryBean.getCategoryParent().equals(mCategory)) {
//                没有二级分类
                if (categoryBean.getCategoryChild() == null || categoryBean.getCategoryChild().size() == 0) {
                    mProductList.addAll(listBeans);
                } else {
                    for (String categoryChild : categoryBean.getCategoryChild()) {
                        List<ProductBasicList.ListBean> beans = mChildProductMap.get(categoryChild);
                        if (beans != null) {
                            mProductList.addAll(beans);
                        }
                        if (!TextUtils.isEmpty(categoryChild)) {
                            categoryList.add(categoryChild);
                        }
                    }
                }
                break;
            }
        }
        mLoadingLayout.setVisibility(View.GONE);
        mListContainer.init(mCategory, mProductList, categoryList, ((ProductActivityV2) getActivity()).getProductCountSetter());
        android.util.Log.i("onGlobalLayout "+mCategory, String.valueOf(mStartTime - System.currentTimeMillis()));
    }

    /**
     * 其它地方修改
     *
     * @param event
     */
    @Subscribe
    public void updateProductCount(ProductCountUpdateEvent event) {
        List<ProductBasicList.ListBean> listBeans = null;
        if (event.bean != null) {
//            一级分类是全部或促销
            if (mChildProductMap == null){
                if (mListContainer.getProductAdapterV2() == null){
                    return;
                }
                List<ProductBasicList.ListBean> sourceList = mListContainer.getProductAdapterV2().getList();
                for (int i = 0;i< sourceList.size();i++) {
                    ProductBasicList.ListBean listBean = sourceList.get(i);
                    if (listBean.getProductID() == event.bean.getProductID()) {
                        refreshItemView(i,listBean);
                        break;
                    }
                }
                return;
            }else{
                listBeans = mChildProductMap.get(event.bean.getCategoryChild());
            }
        } else {
            //购物车商品删除调用的逻辑
            ((ProductActivityV2) getActivity()).initChildBadges();
            mListContainer.getTypeAdapter().updateBadge(((ProductActivityV2) getActivity()).getChildBadges());
            for (ProductBasicList.ListBean listBean : event.beanList) {
                if (listBean.getCategoryParent().equals(mCategory)||mCategory.equals("全部")||mCategory.equals("促销商品")) {
                    for (int i = 0; i < mProductList.size(); i++) {
                        ProductBasicList.ListBean tempListBean = mProductList.get(i);
                        if (tempListBean.getProductID() == listBean.getProductID()) {
                            refreshItemView(i,listBean);
                        }
                    }
                }
            }
        }

        if (event.getException() != null && event.getException().getClass() == ProductAdapter.class) {
            if (listBeans != null) {
                ((ProductActivityV2) getActivity()).initChildBadges();
                mListContainer.getTypeAdapter().updateBadge(((ProductActivityV2) getActivity()).getChildBadges());
                for (int i = 0;i< listBeans.size();i++) {
                    ProductBasicList.ListBean listBean = listBeans.get(i);
                    if (listBean.getProductID() == event.bean.getProductID()) {
                        if (mListContainer.getProductAdapterV2() != null){
                            mListContainer.getProductAdapterV2().notifyDataSetChanged();
                        }
//                        refreshItemView(i,listBean);
                        break;
                    }
                }
            }
            return;
        }
        if (listBeans != null) {
            ((ProductActivityV2) getActivity()).initChildBadges();
            mListContainer.getTypeAdapter().updateBadge(((ProductActivityV2) getActivity()).getChildBadges());
            for (int i = 0;i< listBeans.size();i++) {
                ProductBasicList.ListBean listBean = listBeans.get(i);
                if (listBean.getProductID() == event.bean.getProductID()) {
                    for(int j = 0;j<mProductList.size();j++){
                        if (mProductList.get(j).getProductID() == listBean.getProductID()){
                            refreshItemView(j,listBean);
                        }
                    }

                    break;
                }
            }
        }
    }

    private void refreshItemView(int i,ProductBasicList.ListBean listBean){
        int firstItem = mListContainer.getRecyclerView2().getFirstVisiblePosition();
        int lastItem = mListContainer.getRecyclerView2().getLastVisiblePosition();
//                        可见刷新
        if (i>=firstItem&&i<=lastItem){
            int realPosition = i-firstItem;
            View childView = mListContainer.getRecyclerView2().getChildAt(realPosition);
            TextView tvProductCount = (TextView) childView.findViewById(R.id.tv_product_count);
            ImageView mIvProductReduce = (ImageView) childView.findViewById(R.id.iv_product_reduce);
            ImageView mIvProductAdd = (ImageView) childView.findViewById(R.id.iv_product_add);
            double count = ((ProductActivityV2) getActivity()).getProductCountSetter().getCount(listBean);
            tvProductCount.setText(NumberUtil.getIOrD(count) + listBean.getSaleUom());
            //先根据集合里面对应个数初始化一次
            if (count > 0) {
                tvProductCount.setVisibility(View.VISIBLE);
                mIvProductReduce.setVisibility(View.VISIBLE);
                mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
            } else {
                tvProductCount.setVisibility(View.INVISIBLE);
                mIvProductReduce.setVisibility(View.INVISIBLE);
                mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
            }
        }
    }

}
