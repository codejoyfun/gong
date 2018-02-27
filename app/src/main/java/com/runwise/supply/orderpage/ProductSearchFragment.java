package com.runwise.supply.orderpage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.ProductListRequest;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

/**
 * Created by Dong on 2017/11/23.
 */

public class ProductSearchFragment extends NetWorkFragment {
    private static final int REQUEST_PRODUCT_REFRESH = 0;
    private static final int REQUEST_PRODUCT_MORE = 1;

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    @ViewInject(R.id.et_search)
    private EditText mEtSearch;//搜索框

    private ProductAdapter mProductAdapter;

    private String mCategory;
    private String mSubCategory;
    private int mPz;
    private int mLimit = 20;
    private String mKeyword;

    private boolean canSeePrice = true;//默认价格中可见
    private Map<ProductBasicList.ListBean,Integer> mCountMap;//记录数量，从父activity获取
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductAdapter = new ProductAdapter(getActivity(),false);
        pullListView.setAdapter(mProductAdapter);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();

        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mKeyword = charSequence.toString();
                if(TextUtils.isEmpty(mKeyword)){
                    mProductAdapter.clear();
                    mProductAdapter.notifyDataSetChanged();
                    mLoadingLayout.onSuccess(mProductAdapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_goodsnone);
                    return;
                }
                refresh(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (mEtSearch.requestFocus()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    boolean isShowing = imm.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }

        //点击任意地方收起键盘
        findViewById(R.id.v_touch_cover).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });
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
    public void onPause() {
        super.onPause();

    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.activity_product_search;
    }

    /**
     * 刷新
     */
    protected void refresh(){
        refresh(false);
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
        sendConnection("/gongfu/v3/product/list",new ProductListRequest(mLimit,mPz,mKeyword,mCategory,mSubCategory),where,false,ProductBasicList.class);
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
                if(TextUtils.isEmpty(mKeyword))return;
                ProductBasicList ProductBasicList = (ProductBasicList)result.getResult().getData();
                mProductAdapter.clear();
                mProductAdapter.appendData(ProductBasicList.getList());
                mProductAdapter.notifyDataSetChanged();
                pullListView.onFooterRefreshComplete(ProductBasicList.getList().size(),mLimit,Integer.MAX_VALUE);
                mLoadingLayout.onSuccess(mProductAdapter.getCount(), "搜索不到相关商品，换个关键词试试~", R.drawable.default_icon_goodsnone);
                break;
            case REQUEST_PRODUCT_MORE:
                if(TextUtils.isEmpty(mKeyword))return;
                ProductBasicList = (ProductBasicList)result.getResult().getData();
                mProductAdapter.appendData(ProductBasicList.getList());
                if(ProductBasicList.getList()!=null && ProductBasicList.getList().size()!=0){
                    pullListView.onFooterRefreshComplete(ProductBasicList.getList().size(),mLimit,Integer.MAX_VALUE);
                }else{
                    pullListView.onFooterRefreshComplete(ProductBasicList.getList().size(),mLimit,mProductAdapter.getCount());
                }
                mProductAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        //TODO
    }

    @OnClick({R.id.title_iv_left,R.id.btn_cancel,R.id.loadingLayout})
    public void btnClick(View v){
        switch (v.getId()){
            case R.id.title_iv_left:
                hideKeyboard();
                getFragmentManager().beginTransaction().remove(this).commit();
                break;
            case R.id.btn_cancel:
                mEtSearch.setText("");
                break;
            case R.id.loadingLayout:
                hideKeyboard();
                break;
        }
    }

    InputMethodManager imm;
    private void hideKeyboard(){
        if(mEtSearch.hasFocus()){
            if(imm==null)imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm!=null)imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            mEtSearch.clearFocus();
        }
    }
}
