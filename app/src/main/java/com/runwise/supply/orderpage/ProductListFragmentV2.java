package com.runwise.supply.orderpage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.ProductCountChangeEvent;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.StockListRequest;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.view.NoWatchEditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品按照类别分页显示
 *
 * 所有的已选数据保存在父Activity的map中
 *
 */
public class ProductListFragmentV2 extends NetWorkFragment {
    public static final String INTENT_KEY_SUB_CATEGORY = "subcategory";
    private static final int REQUEST_PRODUCT_REFRESH = 0;
    private static final int REQUEST_PRODUCT_MORE = 1;

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    private ProductAdapter mProductAdapter;
    private String mSubCategory;

    private int mPz;
    private int mLimit;
    private String mKeyword;

    private boolean canSeePrice = true;//默认价格中可见
    private Map<ProductData.ListBean,Integer> mCountMap;//记录数量，从父activity获取
    private List<ProductData.ListBean> mProductList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubCategory = getArguments().getString(INTENT_KEY_SUB_CATEGORY);
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
    }

    /**
     * 从父Activity获取统一的记录
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity parentActivity = getActivity();
        if(parentActivity instanceof ProductActivity2){
            mCountMap = ((ProductActivity2) parentActivity).getCountMap();
        }
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
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
        sendConnection("/api/v2/product/list",new StockListRequest(mLimit,mPz,mKeyword,mSubCategory),where,false,ProductData.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ProductCountChangeEvent event) {
        mProductAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearch(ProductQueryEvent event) {
        //TODO 搜索
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
        //TODO
    }

    /**
     * 商品列表adapter
     */
    public class ProductAdapter extends IBaseAdapter<ProductData.ListBean> {

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final ProductData.ListBean bean = (ProductData.ListBean) mList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.product_layout_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.editText.setTag(position);
            viewHolder.editText.removeTextChangedListener();
            viewHolder.editText.addTextChangedListener(new TextWatcher() {
                String mmStrPrevious;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mmStrPrevious = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //检查特殊字符
                    if (!TextUtils.isDigitsOnly(s)) {
                        viewHolder.editText.setText(mmStrPrevious);
                        return;
                    }

                    int position = (int) viewHolder.editText.getTag();
                    ProductData.ListBean listBean = (ProductData.ListBean) mList.get(position);
                    int changedNum = 0;
                    if (!TextUtils.isEmpty(s)) {
                        changedNum = Integer.valueOf(s.toString());
                    }
                    mCountMap.put(listBean, changedNum);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //先根据集合里面对应个数初始化一次
            if (mCountMap.get(bean) > 0) {
                viewHolder.editLL.setVisibility(View.VISIBLE);
                viewHolder.addBtn.setVisibility(View.INVISIBLE);
                viewHolder.unit1.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.editLL.setVisibility(View.INVISIBLE);
                viewHolder.addBtn.setVisibility(View.VISIBLE);
                viewHolder.unit1.setVisibility(View.VISIBLE);
            }
            final EditText editText = viewHolder.editText;
            editText.setText(mCountMap.get(bean) + "");//TODO:check null
            final LinearLayout ll = viewHolder.editLL;
            final ImageButton addBtn = viewHolder.addBtn;
            final TextView unit1 = viewHolder.unit1;
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearFocus();//点击加减的时候，去掉所有edittext的focus，关闭软键盘
                    view.setVisibility(View.INVISIBLE);
                    unit1.setVisibility(View.INVISIBLE);
                    ll.setVisibility(View.VISIBLE);
                    int currentNum = mCountMap.get(bean);
                    editText.setText(++currentNum + "");//TODO:check null
                    mCountMap.put(bean, currentNum);
                }
            });
            viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    clearFocus();//点击加减的时候，去掉所有edittext的focus，关闭软键盘
                    int currentNum = mCountMap.get(bean);//TODO:check null
                    if (currentNum > 0) {
                        editText.setText(--currentNum + "");
                        mCountMap.put(bean, currentNum);
                        if (currentNum == 0) {
                            addBtn.setVisibility(View.VISIBLE);
                            unit1.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            });
            viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    clearFocus();//点击加减的时候，去掉所有edittext的focus，关闭软键盘
                    int currentNum = mCountMap.get(bean);//TODO:check null
                    editText.setText(++currentNum + "");
                    mCountMap.put(bean, currentNum);
                }
            });

            viewHolder.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String str = viewHolder.editText.getText().toString();
                        if (TextUtils.isEmpty(str) || Integer.valueOf(str) == 0) {//输入0或者空的时候，点完成变回初始样式
                            addBtn.setVisibility(View.VISIBLE);
                            unit1.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.INVISIBLE);
                        }
                        InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });

//            viewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean b) {
//                    if (!b) {
//                        String str = viewHolder.editText.getText().toString();
//                        if (TextUtils.isEmpty(str) || Integer.valueOf(str) == 0) {//输入0或者空的时候，失去焦点变回初始样式
//                            addBtn.setVisibility(View.VISIBLE);
//                            unit1.setVisibility(View.VISIBLE);
//                            ll.setVisibility(View.INVISIBLE);
//                        }
//                    }
//
//                }
//            });

            viewHolder.name.setText(bean.getName());

            StringBuffer sb = new StringBuffer(bean.getDefaultCode());
            sb.append(" | ").append(bean.getUnit());
            viewHolder.content.setText(sb.toString());
            DecimalFormat df = new DecimalFormat("#.##");
            if (canSeePrice) {
                StringBuffer sb1 = new StringBuffer();
                if (bean.isIsTwoUnit()) {
                    sb1.append("¥")
                            .append(df.format(Double.valueOf(bean.getSettlePrice())))
                            .append("元/")
                            .append(bean.getSettleUomId());
                } else {
                    sb1.append("¥")
                            .append(df.format(Double.valueOf(bean.getPrice())))
                            .append("元/")
                            .append(bean.getUom());
                }
                viewHolder.tv_price.setText(sb1.toString());
            } else {
                viewHolder.tv_price.setText("");
            }

            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + bean.getImage().getImageSmall());
            viewHolder.unit1.setText(bean.getUom());
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView name;   //名称
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;    //头像
            @ViewInject(R.id.content)
            TextView content;//内容
            @ViewInject(R.id.editLL)
            LinearLayout editLL;        //整体编辑框
            @ViewInject(R.id.addBtn)
            ImageButton addBtn; //添加按钮
            @ViewInject(R.id.input_minus)
            ImageButton inputMBtn;//减
            @ViewInject(R.id.input_add)
            ImageButton inputPBtn;//加
            @ViewInject(R.id.editText)
            NoWatchEditText editText; //输入框
            @ViewInject(R.id.unit1)
            TextView unit1;  //单位
            @ViewInject(R.id.tv_price)
            TextView tv_price;  //单位

        }
    }

    private void clearFocus() {
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }
}
