package com.runwise.supply.orderpage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.ProductListRequest;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
    private Map<ProductData.ListBean,Integer> mCountMap;//记录数量，从父activity获取
    private List<ProductData.ListBean> mProductList = new ArrayList<>();
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductAdapter = new ProductAdapter();
        mProductAdapter.setData(mProductList);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setAdapter(mProductAdapter);
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
            mCountMap = ((ProductActivityV2) parentActivity).getCountMap();
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
                if(TextUtils.isEmpty(mKeyword))return;
                ProductData productData = (ProductData)result.getResult().getData();
                mProductAdapter.clear();
                mProductAdapter.appendData(productData.getList());
                mProductAdapter.notifyDataSetChanged();
                pullListView.onFooterRefreshComplete(productData.getList().size(),mLimit,Integer.MAX_VALUE);
                mLoadingLayout.onSuccess(mProductAdapter.getCount(), "搜索不到相关商品，换个关键词试试~", R.drawable.default_icon_goodsnone);
                break;
            case REQUEST_PRODUCT_MORE:
                if(TextUtils.isEmpty(mKeyword))return;
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
        //TODO
    }

    /**
     *
     * TODO:重构为公用
     * 商品列表adapter
     */
    public class ProductAdapter extends IBaseAdapter<ProductData.ListBean> {

        DecimalFormat df = new DecimalFormat("#.##");
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final ProductData.ListBean bean = (ProductData.ListBean) mList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_product_without_subcategory, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final int count = mCountMap.get(bean)==null?0:mCountMap.get(bean);
            viewHolder.tvCount.setText(count+bean.getProductUom());
            //先根据集合里面对应个数初始化一次
            if (count > 0) {
                viewHolder.tvCount.setVisibility(View.VISIBLE);
                viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_green);
            } else {
                viewHolder.tvCount.setVisibility(View.INVISIBLE);
                viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
                viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
            }

            //标签
            if(TextUtils.isEmpty(bean.getProductTag())){
                viewHolder.tvProductTag.setVisibility(View.GONE);
            }else{
                viewHolder.tvProductTag.setText(bean.getProductTag());
            }

            /**
             * 减
             */
            viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int currentNum = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                    if (currentNum > 0) {
                        viewHolder.tvCount.setText(--currentNum + bean.getProductUom());
                        mCountMap.put(bean, currentNum);
                        if (currentNum == 0) {
                            v.setVisibility(View.INVISIBLE);
                            viewHolder.tvCount.setVisibility(View.INVISIBLE);
                            viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
                            mCountMap.remove(bean);
                        }
                        EventBus.getDefault().post(new ProductCountUpdateEvent(bean,currentNum));
                    }

                }
            });

            /**
             * 加
             */
            viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int currentNum = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                    viewHolder.tvCount.setText(++currentNum + bean.getProductUom());
                    mCountMap.put(bean, currentNum);
                    if (currentNum == 1) {//0变到1
                        viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                        viewHolder.tvCount.setVisibility(View.VISIBLE);
                        viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_green);
                    }
                    EventBus.getDefault().post(new ProductCountUpdateEvent(bean,currentNum));
                }
            });

            /**
             * 点击数量展示输入对话框
             */
            viewHolder.tvCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentCount = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                    new ProductValueDialog(getActivity(), bean.getName(), currentCount, new ProductValueDialog.IProductDialogCallback() {
                        @Override
                        public void onInputValue(int value) {

                            if (value == 0) {
                                viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
                                viewHolder.tvCount.setVisibility(View.INVISIBLE);
                                viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
                                mCountMap.remove(bean);
                            }else{
                                viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                                viewHolder.tvCount.setVisibility(View.VISIBLE);
                                viewHolder.tvCount.setText(value+bean.getProductUom());
                                viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_green);
                                mCountMap.put(bean,value);
                            }
                            viewHolder.tvCount.setText(value + bean.getProductUom());
                            EventBus.getDefault().post(new ProductCountUpdateEvent(bean,value));
                        }
                    }).show();
                }
            });

            viewHolder.name.setText(bean.getName());
            viewHolder.tvCode.setText(bean.getDefaultCode());
            viewHolder.tvContent.setText(bean.getUnit());

            if (canSeePrice) {
                StringBuffer sb1 = new StringBuffer();
                if (bean.isIsTwoUnit()) {
                    sb1.append("¥").append(df.format(Double.valueOf(bean.getSettlePrice())));
                    viewHolder.tvPrice.setText(sb1.toString());
                    viewHolder.tvPriceUnit.setText("/"+bean.getSettleUomId());
                } else {
                    sb1.append("¥").append(df.format(Double.valueOf(bean.getPrice())));
                    viewHolder.tvPrice.setText(sb1.toString());
                    viewHolder.tvPriceUnit.setText("/"+bean.getUom());
                }
            } else {
                viewHolder.tvPrice.setVisibility(View.GONE);
                viewHolder.tvPriceUnit.setVisibility(View.GONE);
            }

            if(bean.getImage()!=null){
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + bean.getImage().getImageSmall());
            }

            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.tv_product_name)
            TextView name;   //名称
            @ViewInject(R.id.sdv_product_image)
            SimpleDraweeView sDv;    //头像
            @ViewInject(R.id.iv_product_reduce)
            ImageButton inputMBtn;//减
            @ViewInject(R.id.iv_product_add)
            ImageButton inputPBtn;//加
            @ViewInject(R.id.tv_product_count)
            TextView tvCount;//数量
            @ViewInject(R.id.tv_product_code)//代码
            TextView tvCode;
            @ViewInject(R.id.tv_product_price_unit)//价格后的单位
            TextView tvPriceUnit;
            @ViewInject(R.id.tv_product_price)//价格
            TextView tvPrice;
            @ViewInject(R.id.tv_product_content)
            TextView tvContent;
            @ViewInject(R.id.iv_product_sale)
            TextView tvProductTag;
        }
    }


    @OnClick({R.id.title_iv_left,R.id.btn_cancel})
    public void btnClick(View v){
        switch (v.getId()){
            case R.id.title_iv_left:
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null)imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                getFragmentManager().beginTransaction().remove(this).commit();
                break;
            case R.id.btn_cancel:
                mEtSearch.setText("");
                break;
        }
    }
}
