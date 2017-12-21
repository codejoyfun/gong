package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.ProductListRequest;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductData;

import org.greenrobot.eventbus.Subscribe;

import java.util.Map;


/**
 * 商品按照类别分页显示
 *
 * 所有的已选数据保存在父Activity的map中
 *
 */
public class ProductListFragmentV2 extends NetWorkFragment {
    public static final String INTENT_KEY_CATEGORY = "category";
    public static final String INTENT_KEY_SUB_CATEGORY = "subcategory";
    public static final String INTENT_KEY_HAS_OTHER_SUB = "has_other_sub";
    private static final int REQUEST_PRODUCT_REFRESH = 0;
    private static final int REQUEST_PRODUCT_MORE = 1;

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    private ProductAdapter mProductAdapter;

    private String mCategory;
    private String mSubCategory;
    private int mPz;
    private int mLimit = 20;
    private String mKeyword;

    private Map<ProductData.ListBean,Integer> mCountMap;//记录数量，从父activity获取

    boolean hasOtherSub;//是否有其它子分类，用于区分子项的layout

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubCategory = getArguments().getString(INTENT_KEY_SUB_CATEGORY);
        mCategory = getArguments().getString(INTENT_KEY_CATEGORY);
        hasOtherSub = getArguments().getBoolean(INTENT_KEY_HAS_OTHER_SUB,false);
        mProductAdapter = new ProductAdapter(getActivity(),hasOtherSub);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setAdapter(mProductAdapter);

        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }
        });

        refresh(true);
    }

    /**
     * 从父Activity获取统一的记录
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity parentActivity = getActivity();
        if(parentActivity instanceof ProductActivityV2){
//            mCountMap = ((ProductActivityV2) parentActivity).getCountMap();
//            mProductAdapter.setCountMap(mCountMap);
            mProductAdapter.setProductCountSetter(((ProductActivityV2)parentActivity).getProductCountSetter());
        }
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_products;
    }

    /**
     * 加载更多
     */
    protected void loadMore(){
        mPz++;
        requestData(REQUEST_PRODUCT_MORE);
    }

    /**
     * 刷新
     * @param showLoadingLayout 是否显示loading layout
     */
    protected void refresh(boolean showLoadingLayout){
        if(showLoadingLayout)mLoadingLayout.setStatusLoading();
        mPz = 1;
        requestData(REQUEST_PRODUCT_REFRESH);
    }

    protected void requestData(int where){
        sendConnection("/gongfu/v3/product/list",new ProductListRequest(mLimit,mPz,mKeyword,mCategory,mSubCategory),where,false,ProductData.class);
    }

    /**
     * 其它地方修改
     * @param event
     */
    @Subscribe
    public void updateProductCount(ProductCountUpdateEvent event){
        mProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_PRODUCT_REFRESH:
                ProductData productData = (ProductData)result.getResult().getData();
                mProductAdapter.clear();
                mProductAdapter.appendData(productData.getList());
                mProductAdapter.notifyDataSetChanged();
                pullListView.onFooterRefreshComplete(productData.getList().size(),mLimit,Integer.MAX_VALUE);
                mLoadingLayout.onSuccess(mProductAdapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_goodsnone);
                break;
            case REQUEST_PRODUCT_MORE:
                productData = (ProductData)result.getResult().getData();
                mProductAdapter.appendData(productData.getList());
                if(productData.getList()!=null && productData.getList().size()!=0){
                    pullListView.onFooterRefreshComplete(productData.getList().size(),mLimit,Integer.MAX_VALUE);
                }else{
                    pullListView.onFooterRefreshComplete(productData.getList().size(),mLimit,mProductAdapter.getCount());
                }
                mProductAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        mLoadingLayout.onFailure(errMsg,R.drawable.nonocitify_icon);
        mLoadingLayout.setOnRetryClickListener(new LoadingLayoutInterface() {
            @Override
            public void retryOnClick(View view) {
                refresh(true);
            }
        });
    }
}