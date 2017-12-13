package com.runwise.supply.repertory;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FictitiousStock;
import com.runwise.supply.entity.StockListRequest;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.UpdateRepertory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayDeque;
import java.util.Queue;

import io.vov.vitamio.utils.NumberUtil;

/**
 * 单个类别的库存列表
 * 分页加载
 *
 */

public abstract class AbstractStockListFragment extends NetWorkFragment {
    public static final String ARG_CATEGORY = "arg_category";
    public static final String ARG_CURRENT = "arg_current";
    private static final int PAGE_LIMIT = 50;
    private static final int REQ_STOCK = 0;
    private static final int REQ_STOCK_MORE = 1;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
//    protected ProductAdapter adapter;
    protected StockAdapterV2 adapter;
    @ViewInject(R.id.loadingLayout)
    protected LoadingLayout loadingLayout;

    protected String mKeyword = "";//搜索关键字
    private String mCategory;//类别
    private int mPz;//第几页
    private int limit = PAGE_LIMIT;//一页多少条

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getString(ARG_CATEGORY);
//        adapter = new ProductAdapter();
        adapter = new StockAdapterV2();
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
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }


    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        loadingLayout.onFailure(errMsg,R.drawable.nonocitify_icon);
        loadingLayout.setOnRetryClickListener(new LoadingLayoutInterface() {
            @Override
            public void retryOnClick(View view) {
                refresh(true);
            }
        });
    }

    /**
     * 刷新fragment数据
     *
     * @param keyword 搜索词
     */
    public abstract void refresh(String keyword);

    /**
     * 刷新
     * @param showLoadingLayout 是否显示loading layout
     */
    protected void refresh(boolean showLoadingLayout){
        if(showLoadingLayout)loadingLayout.setStatusLoading();
        mPz = 1;
        requestData(REQ_STOCK);
    }

    protected void refresh(){
        refresh(false);
    }

    protected void loadMore(){
        mPz++;
        requestData(REQ_STOCK_MORE);
    }

    private void requestData(final int where){
        boolean isLogin = SPUtils.isLogin(mContext);
        if(!isLogin) {
            //示例数据少，不会有加载更多出现
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSuccess(FictitiousStock.mockStockData(mCategory,mKeyword),where);
                }
            },500);
            return;
        }
        sendConnection("/api/v3/stock/list",new StockListRequest(limit,mPz,mKeyword,mCategory),where,false,RepertoryEntity.class);
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
                pullListView.onFooterRefreshComplete(repertoryEntity.getList().size(),limit,Integer.MAX_VALUE);
                loadingLayout.onSuccess(adapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_goodsnone);
                break;
            case REQ_STOCK_MORE:
                repertoryEntity = (RepertoryEntity)result.getResult().getData();
                adapter.appendData(repertoryEntity.getList());
                if(repertoryEntity.getList()!=null && repertoryEntity.getList().size()!=0){
                    pullListView.onFooterRefreshComplete(repertoryEntity.getList().size(),limit,Integer.MAX_VALUE);
                }else{
                    pullListView.onFooterRefreshComplete(repertoryEntity.getList().size(),limit,adapter.getCount());
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Deprecated
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
            ProductBasicList.ListBean productBean = bean.getProduct();
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


    /**
     * 盘点商品列表的adapter
     */
    public class StockAdapterV2 extends IBaseAdapter<RepertoryEntity.ListBean>{

        Queue<View> cacheLotView = new ArrayDeque<>();
        LayoutInflater mInflater;
        int colorBgChanged;
        int colorBgUnChanged;

        StockAdapterV2(){
            colorBgUnChanged = Color.parseColor("#ffffff");
            colorBgChanged = Color.parseColor("#FFF3FBED");
        }

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            if(mInflater==null)mInflater = LayoutInflater.from(getActivity());
            RepertoryEntity.ListBean inventoryProduct = mList.get(position);
            ViewHolder viewHolder;
            if(convertView==null){
                convertView = mInflater.inflate(R.layout.item_stock,parent,false);
                viewHolder = new ViewHolder();
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder)convertView.getTag();

            //添加批次信息
            //***********************有批次***************************
            if(inventoryProduct.getLotList()!=null && inventoryProduct.getLotList().size()>0){
                viewHolder.mmEtCount.setVisibility(View.GONE);
                viewHolder.mmTvUom.setVisibility(View.GONE);
                viewHolder.mmLayoutContainer.setVisibility(View.VISIBLE);
                for(int i=0;i<inventoryProduct.getLotList().size();i++){
                    View view = viewHolder.mmLayoutContainer.getChildAt(i);
                    LotViewHolder lotViewHolder;
                    if(view!=null){
                        lotViewHolder = (LotViewHolder) view.getTag();
                    }else{
                        //need to add view
                        //get from cache first
                        view = cacheLotView.poll();
                        if(view == null){//cache is not available, need inflate
                            view = mInflater.inflate(R.layout.item_stock_lot,viewHolder.mmLayoutContainer,false);
                            lotViewHolder = new LotViewHolder();
                            ViewUtils.inject(lotViewHolder,view);
                            view.setTag(lotViewHolder);
                        }
                        lotViewHolder = (LotViewHolder)view.getTag();
                        viewHolder.mmLayoutContainer.addView(view);
                    }
                    RepertoryEntity.RepertoryLot inventoryLot = inventoryProduct.getLotList().get(i);
                    //判断过期
                    long dayDiff = DateFormateUtil.getDaysToExpire(inventoryLot.getLifeEndDate());
                    if(dayDiff == 0){
                        //今天到期
                        lotViewHolder.mmTvExpire.setText("今天到期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.inventory_expire));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.inventory_expire_bg));
                    }else if(dayDiff<0){
                        //过期
                        lotViewHolder.mmTvExpire.setText("已过期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.inventory_expire));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.inventory_expire_bg));
                    }else if(dayDiff<=3){
                        //小于3天
                        lotViewHolder.mmTvExpire.setText(dayDiff+"天到期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.stock_3_days));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.stock_3_days_bg));
                    }else{
                        //大于4天
                        lotViewHolder.mmTvExpire.setText(dayDiff+"天到期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.stock_4_days));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.stock_4_days_bg));
                    }
                    lotViewHolder.mmTvLotName.setText(inventoryLot.getLotNum()!=null?"批次："+ inventoryLot.getLotNum():"");
                    lotViewHolder.mmTvLotCount.setText(NumberUtil.getIOrD(inventoryLot.getQty()));
                    lotViewHolder.mmTvLotUom.setText(inventoryLot.getUom());
                }
                //remove and cache unused child view
                int currentSize = inventoryProduct.getLotList().size();
                int totalChild = viewHolder.mmLayoutContainer.getChildCount();
                for(int i = totalChild - 1;i>=currentSize;i--){//由后往前删，防止index乱
                    View view = viewHolder.mmLayoutContainer.getChildAt(i);
                    if(view==null)break;
                    viewHolder.mmLayoutContainer.removeViewAt(i);
                    cacheLotView.offer(view);
                }

            }else{//**********************无批次******************************
                viewHolder.mmEtCount.setVisibility(View.VISIBLE);
                viewHolder.mmTvUom.setVisibility(View.VISIBLE);
                viewHolder.mmEtCount.setText(NumberUtil.getIOrD(inventoryProduct.getQty()));
                viewHolder.mmTvUom.setText(inventoryProduct.getUom());
                viewHolder.mmLayoutContainer.setVisibility(View.GONE);

            }

            viewHolder.mmTvTitle.setText(inventoryProduct.getProduct().getName());
            viewHolder.mmTvCode.setText(inventoryProduct.getCode());
            viewHolder.mmTvUnit.setText(inventoryProduct.getProduct().getUnit());
            //TODO:图片
            if(inventoryProduct.getProduct().getImage()!=null){
                FrecoFactory.getInstance(getActivity()).disPlay(viewHolder.mmSdvImage,inventoryProduct.getProduct().getImage().getImage());
            }
            return convertView;
        }
    }

    /**
     * 盘点商品列表viewholder
     */
    private class ViewHolder{
        @ViewInject(R.id.rl_item_inventory)
        View mmRlRoot;
        @ViewInject(R.id.sdv_stock_image)
        SimpleDraweeView mmSdvImage;
        @ViewInject(R.id.tv_stock_title)
        TextView mmTvTitle;
        @ViewInject(R.id.tv_stock_code)
        TextView mmTvCode;
        @ViewInject(R.id.tv_stock_unit)
        TextView mmTvUnit;
        @ViewInject(R.id.ll_stock_container)
        LinearLayout mmLayoutContainer;
        @ViewInject(R.id.tv_stock_count)
        TextView mmEtCount;
        @ViewInject(R.id.tv_stock_uom)
        TextView mmTvUom;
    }

    private class LotViewHolder{
        @ViewInject(R.id.tv_stock_lot_expire)
        TextView mmTvExpire;
        @ViewInject(R.id.tv_stock_lot_name)
        TextView mmTvLotName;
        @ViewInject(R.id.tv_stock_lot_count)
        TextView mmTvLotCount;
        @ViewInject(R.id.tv_stock_lot_uom)
        TextView mmTvLotUom;
    }
}
