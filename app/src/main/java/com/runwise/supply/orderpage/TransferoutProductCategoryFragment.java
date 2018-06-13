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
import com.runwise.supply.entity.StockProductListResponse;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.event.TransferoutProductCountUpdateEvent;
import com.runwise.supply.mine.TransferoutProductListActivity;
import com.runwise.supply.view.ListContainer;
import com.runwise.supply.view.TransferoutListContainer;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_PRODUCT_CATEGORY_LIST;

/**
 * Created by mike on 2018/6/13.
 */

public class TransferoutProductCategoryFragment extends NetWorkFragment {

    public static final String INTENT_KEY_CATEGORY = "ap_category";
    public static final String INTENT_KEY_FIRST = "first";

    private StockProductListResponse.CategoryBean mCategory;
    @ViewInject(R.id.listcontainer)
    private TransferoutListContainer mListContainer;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    private String mCurrentSubCategory = null;

    private boolean isLive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLive = true;
        mCategory = (StockProductListResponse.CategoryBean) getArguments().getSerializable(INTENT_KEY_CATEGORY);
        requestChildCategory();
        HashMap<String, Long> childBadges = ((TransferoutProductListActivity) getActivity()).getChildBadges();
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
        return R.layout.fragment_transferout_product_category;
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


    HashMap<String, List<StockProductListResponse.ListBean>> mChildProductMap;

    public HashMap<String, List<StockProductListResponse.ListBean>> getChildProductMap() {
        return mChildProductMap;
    }

    List<StockProductListResponse.ListBean> mProductList = new ArrayList<>();

    public void requestChildCategory() {
        long mStartTime;
        mStartTime = System.currentTimeMillis();
        //查询二级分类
        List<StockProductListResponse.ListBean> listBeans = ((TransferoutProductListActivity) getActivity()).getProductMap().get(mCategory);
        //        促销商品
        ArrayList<StockProductListResponse.ListBean> salesPromotionList = new ArrayList<>();
        if (listBeans == null) {
            switch (mCategory.getCategoryParent().getName()) {
                case "全部":
                    mLoadingLayout.setVisibility(View.GONE);
//                    ((TransferoutProductListActivity) getActivity()).getListBeans()
                    mProductList = ((TransferoutProductListActivity) getActivity()).getListBeans();
                    mListContainer.init(mCategory.getCategoryParent().getName(), ((TransferoutProductListActivity) getActivity()).getListBeans(), null, ((TransferoutProductListActivity) getActivity()).getProductCountSetter());
                    android.util.Log.i("onGlobalLayout 全部", String.valueOf(mStartTime - System.currentTimeMillis()));
                    return;
//                case "促销商品":
//                    //            一级分类是促销商品
//                    listBeans = ((TransferoutProductListActivity) getActivity()).getListBeans();
//                    for (int i = 0; i < listBeans.size(); i++) {
//                        StockProductListResponse.ListBean bean = listBeans.get(i);
//                        if (mCategory.equals(getString(R.string.sales_promotion)) && bean.getProductTag().equals(getString(R.string.sales_promotion))) {
//                            salesPromotionList.add(bean);
//                        }
//                    }
//                    mLoadingLayout.setVisibility(View.GONE);
//                    mProductList = salesPromotionList;
//                    mListContainer.init(mCategory, salesPromotionList, null, ((TransferoutProductListActivity) getActivity()).getProductCountSetter());
//                    android.util.Log.i("onGlobalLayout 促销商品", String.valueOf(mStartTime - System.currentTimeMillis()));
//                    return;
                default:
                    mListContainer.setVisibility(View.GONE);
                    return;
            }

        }
        mChildProductMap = new HashMap<>();

        for (int i = 0; i < listBeans.size(); i++) {
            StockProductListResponse.ListBean bean = listBeans.get(i);
//            二级分类是促销商品
            String categoryChild = bean.getCategoryChild();
            List<StockProductListResponse.ListBean> beanList;
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
        if (mCategory.getCategoryChild()== null || mCategory.getCategoryChild().size() == 0){
            mProductList.addAll(listBeans);
        }else {
            for (StockProductListResponse.CategoryChild categoryChild : mCategory.getCategoryChild()) {
//                没有二级分类
                    List<StockProductListResponse.ListBean> beans = mChildProductMap.get(categoryChild.getName());
                    if (beans != null) {
                        mProductList.addAll(beans);
                    }
                    if (!TextUtils.isEmpty(categoryChild.getName())) {
                        categoryList.add(categoryChild.getName());
                    }
            }
        }


        mLoadingLayout.setVisibility(View.GONE);
        mListContainer.init(mCategory.getCategoryParent().getName(), mProductList, categoryList, ((TransferoutProductListActivity) getActivity()).getProductCountSetter());
        android.util.Log.i("onGlobalLayout "+mCategory, String.valueOf(mStartTime - System.currentTimeMillis()));
    }

    /**
     * 其它地方修改
     *
     * @param event
     */
    @Subscribe
    public void updateProductCount(TransferoutProductCountUpdateEvent event) {
        List<StockProductListResponse.ListBean> listBeans = null;
        if (event.bean != null) {
//            一级分类是全部或促销
            if (mChildProductMap == null){
                if (mListContainer.getProductAdapterV2() == null){
                    return;
                }
                List<StockProductListResponse.ListBean> sourceList = mListContainer.getProductAdapterV2().getList();
                for (int i = 0;i< sourceList.size();i++) {
                    StockProductListResponse.ListBean listBean = sourceList.get(i);
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
            ((TransferoutProductListActivity) getActivity()).initChildBadges();
            mListContainer.getTypeAdapter().updateBadge(((TransferoutProductListActivity) getActivity()).getChildBadges());
            for (StockProductListResponse.ListBean listBean : event.beanList) {
                if (listBean.getCategoryParent().equals(mCategory)||mCategory.equals("全部")||mCategory.equals("促销商品")) {
                    for (int i = 0; i < mProductList.size(); i++) {
                        StockProductListResponse.ListBean tempListBean = mProductList.get(i);
                        if (tempListBean.getProductID() == listBean.getProductID()) {
                            refreshItemView(i,listBean);
                        }
                    }
                }
            }
        }

        if (event.getException() != null && event.getException().getClass() == ProductAdapter.class) {
            if (listBeans != null) {
                ((TransferoutProductListActivity) getActivity()).initChildBadges();
                mListContainer.getTypeAdapter().updateBadge(((TransferoutProductListActivity) getActivity()).getChildBadges());
                for (int i = 0;i< listBeans.size();i++) {
                    StockProductListResponse.ListBean listBean = listBeans.get(i);
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
            ((TransferoutProductListActivity) getActivity()).initChildBadges();
            mListContainer.getTypeAdapter().updateBadge(((TransferoutProductListActivity) getActivity()).getChildBadges());
            for (int i = 0;i< listBeans.size();i++) {
                StockProductListResponse.ListBean listBean = listBeans.get(i);
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

    private void refreshItemView(int i,StockProductListResponse.ListBean listBean){
        int firstItem = mListContainer.getRecyclerView2().getFirstVisiblePosition();
        int lastItem = mListContainer.getRecyclerView2().getLastVisiblePosition();
//                        可见刷新
        if (i>=firstItem&&i<=lastItem){
            int realPosition = i-firstItem;
            View childView = mListContainer.getRecyclerView2().getChildAt(realPosition);
            TextView tvProductCount = (TextView) childView.findViewById(R.id.tv_product_count);
            ImageView mIvProductReduce = (ImageView) childView.findViewById(R.id.iv_product_reduce);
            ImageView mIvProductAdd = (ImageView) childView.findViewById(R.id.iv_product_add);
            double count = ((TransferoutProductListActivity) getActivity()).getProductCountSetter().getCount(listBean);
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
