package com.runwise.supply.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.message.entity.OrderMsgDetail;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

/**
 * 消息订单详情列表
 */
public class OrderMsgDetailListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int PRODUCT_DETAIL = 4;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private PriceAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    public String type;
    private boolean normal;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setOnItemClickListener(this);

        adapter = new PriceAdapter();

        if(mOnRefreshListener2 == null){
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    page = 1;
                    requestData(false, REQUEST_START, page, 10);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, REQUEST_DEN, (++page) , 10);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
        loadingLayout.setStatusLoading();
        loadingLayout.setOnRetryClickListener(this);
    }


    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = null;
//        request.setLimit(limit);
//        request.setPz(page);
        Intent intent = mContext.getIntent();
        normal = intent.getBooleanExtra("normalOrder",true);

        if (normal) {
            sendConnection("/gongfu/v2/order/" + intent.getStringExtra("orderId"), request, where, showDialog, OrderMsgDetail.class);
        }
        else {
            sendConnection("/gongfu/v2/return_order/" + intent.getStringExtra("orderId"), request, where, showDialog, OrderMsgDetail.class);
        }
    }

    public List<OrderMsgDetail.OrderBean.LinesBean> handlerDataList(List<OrderMsgDetail.OrderBean.LinesBean> prodectList) {
        if(type.equals("全部")) {
            return prodectList;
        }
        List<OrderMsgDetail.OrderBean.LinesBean> typeList = new ArrayList<>();
        for (OrderMsgDetail.OrderBean.LinesBean bean : prodectList){
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivity()).get(String.valueOf(bean.getProductID()));
            if (listBean != null && listBean.getCategoryParent().equals(type)){
                typeList.add(bean);
            }
        }
        return typeList;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                OrderMsgDetail mainListResult = (OrderMsgDetail)result.getResult().getData();
                if(normal) {
                    adapter.setData(handlerDataList(mainListResult.getOrder().getLines()));
                }
                else{
                    adapter.setData(handlerDataList(mainListResult.getReturnOrder().getLines()));
                }
                loadingLayout.onSuccess(adapter.getCount(),"哎呀！这里是空哒~~",R.drawable.default_icon_goodsnone);
//                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                OrderMsgDetail startListResult = (OrderMsgDetail)result.getResult().getData();
                adapter.setData(startListResult.getOrder().getLines());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                OrderMsgDetail sndListResult = (OrderMsgDetail)result.getResult().getData();
                if (sndListResult.getOrder().getLines() != null && !sndListResult.getOrder().getLines().isEmpty()) {
                    adapter.appendData(sndListResult.getOrder().getLines());
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case PRODUCT_DETAIL:
                ProductOne productOne = (ProductOne) result.getResult().getData();
                ProductBasicList.ListBean listBean = productOne.getProduct();
                //保存进缓存
                ProductBasicUtils.getBasicMap(getActivity()).put(listBean.getProductID()+"",listBean);//更新内存缓存
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
        loadingLayout.onFailure(errMsg,R.drawable.default_icon_checkconnection);
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        OrderMsgDetail.ListBean bean = (OrderMsgDetail.ListBean)parent.getAdapter().getItem(position);
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.activity_notitle_list;
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }


    public class PriceAdapter extends IBaseAdapter<OrderMsgDetail.OrderBean.LinesBean>{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.orderdetail_layout_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final OrderMsgDetail.OrderBean.LinesBean bean =  mList.get(position);
            ProductBasicList.ListBean productBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
            if (productBean != null){
                viewHolder.name.setText(productBean.getName());
                viewHolder.number.setText(productBean.getDefaultCode() + " | ");
                viewHolder.content.setText(productBean.getUnit());
                FrecoFactory.getInstance(mContext).displayWithoutHost(viewHolder.sDv, productBean.getImage().getImageSmall());
                if (SampleApplicationLike.getInstance().getCanSeePrice()) {
                    viewHolder.dateNumber.setVisibility(View.VISIBLE);
                    viewHolder.dateNumber.setText("¥"+ UserUtils.formatPrice(productBean.getPrice()+"")+"/"+bean.getProductUom());
                }
                else{
                    viewHolder.dateNumber.setVisibility(View.GONE);
                }
            } else{//本地数据没有，查接口
                requestMissingInfo(bean.getProductID());
            }
            viewHolder.uom.setText(NumberUtil.getIOrD(bean.getProductUomQty())+"" + bean.getProductUom());
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;
            @ViewInject(R.id.productImage)
            SimpleDraweeView    sDv;
            @ViewInject(R.id.number)
            TextView            number;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.uom)
            TextView         uom;
            @ViewInject(R.id.dateNumber)
            TextView         dateNumber;
            @ViewInject(R.id.dateLate)
            TextView            dateLate;
        }
    }

    private void requestMissingInfo(int productId){
            Object request = null;
            StringBuffer sb = new StringBuffer("/gongfu/v2/product/");
            sb.append(productId).append("/");
            sendConnection(sb.toString(), request, PRODUCT_DETAIL, false, ProductOne.class);
    }
}
