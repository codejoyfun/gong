package com.runwise.supply.repertory;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.entity.StockListRequest;
import com.runwise.supply.mine.entity.RefreshPepertoy;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.mine.entity.SearchKeyAct;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.fragment.OrderProductFragment.BUNDLE_KEY_LIST;

/**
 * 单个类别的库存列表
 * 分页加载
 *
 */
@Deprecated
public class RepoListFragment extends NetWorkFragment {
    public static final String ARG_CATEGORY = "arg_category";
    private static final int PAGE_LIMIT = 10;
    private static final int REQ_STOCK = 0;
    private static final int REQ_STOCK_MORE = 1;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;

    protected String mKeyword = "";//搜索关键字
    private String mCategory;//类别
    private int mPz;//第几页
    private int limit = PAGE_LIMIT;//一页多少条
    private boolean isFirstLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getString(ARG_CATEGORY);
        adapter = new ProductAdapter();
        pullListView = (PullToRefreshListView)findViewById(R.id.pullListView);
        loadingLayout = (LoadingLayout)findViewById(R.id.loadingLayout);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setAdapter(adapter);
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

        if(getArguments().getBoolean("arg_current",false))getFirstData();
    }

    protected void getFirstData(){
        refresh();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    //返回当前标签下名称包含的
//    private List<RepertoryEntity.ListBean> findArrayByWord(String word) {
//        mKeyword = word;
//        List<RepertoryEntity.ListBean> findList = new ArrayList<>();
//        if (TextUtils.isEmpty(word)) {
//            return dataList;
//        }
//        for (RepertoryEntity.ListBean bean : dataList) {
//            if (bean.getProduct().getName().contains(word)) {
//                findList.add(bean);
//            }
//        }
//        return findList;
//    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    /**
     * 设置为当前tab的时候被调用
     * 防止一次过全部tab调接口
     */
    public void refresh(String keyword){
        if(!isFirstLoaded){
            isFirstLoaded = true;
            refresh();
            return;
        }
    }

    protected void refresh(){
        mPz = 1;
        requestData(REQ_STOCK);
    }

    private void loadMore(){
        mPz++;
        requestData(REQ_STOCK_MORE);
    }

    private void requestData(int where){
        sendConnection("/api/v2/stock/list",new StockListRequest(limit,mPz,mKeyword,mCategory),where,false,RepertoryEntity.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        RepertoryEntity repertoryEntity;
        switch (where){
            case REQ_STOCK:
                repertoryEntity = (RepertoryEntity)result.getResult().getData();
                adapter.clear();
                adapter.appendData(repertoryEntity.getList());
                adapter.notifyDataSetChanged();
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                loadingLayout.onSuccess(adapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_goodsnone);
                break;
            case REQ_STOCK_MORE:
                repertoryEntity = (RepertoryEntity)result.getResult().getData();
                adapter.appendData(repertoryEntity.getList());
                if(repertoryEntity.getList().size()==0){
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }else{
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    public class ProductAdapter extends IBaseAdapter<RepertoryEntity.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.repertory_layout_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final RepertoryEntity.ListBean bean = mList.get(position);
            ProductBasicList.ListBean productBean = ProductBasicUtils.getBasicMap(getActivity()).get(String.valueOf(bean.getProductID()));
            if (productBean != null) {
                if (!TextUtils.isEmpty(mKeyword)) {
                    int index = productBean.getName().indexOf(mKeyword);
                    if (index != -1) {
                        SpannableString spannStr = new SpannableString(productBean.getName());
                        spannStr.setSpan(new ForegroundColorSpan(Color.parseColor("#6bb400")), index, index + mKeyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder.name.setText(spannStr);
                    }
                } else {
                    viewHolder.name.setText(productBean.getName());
                }
                viewHolder.number.setText(productBean.getDefaultCode() + " | ");
                viewHolder.content.setText(productBean.getUnit());
                if (productBean.getImage() !=null){
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImageSmall());
                }
            }
            viewHolder.value.setText(NumberUtil.getIOrD(String.valueOf(bean.getQty())));
            viewHolder.uom.setText(bean.getUom());
            if (TextUtils.isEmpty(bean.getUom())){
                ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getContext()).get(String.valueOf(bean.getProductID()));
                if (listBean!=null){
                    viewHolder.uom.setText(listBean.getProductUom());
                }
            }
            if (TextUtils.isEmpty(bean.getLotNum())){
                viewHolder.dateNumber.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.dateNumber.setText(bean.getLotNum());
                viewHolder.dateNumber.setVisibility(View.VISIBLE);
            }
            viewHolder.dateLate.setText(DateFormateUtil.getLaterFormat(bean.getLifeEndDate()));
            if (bean.getImageId() != 0){
                viewHolder.sDv.setImageResource(bean.getImageId());
            }
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView name;
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;
            @ViewInject(R.id.number)
            TextView number;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.value)
            TextView value;
            @ViewInject(R.id.uom)
            TextView uom;
            @ViewInject(R.id.dateNumber)
            TextView dateNumber;
            @ViewInject(R.id.dateLate)
            TextView dateLate;
        }
    }
}
