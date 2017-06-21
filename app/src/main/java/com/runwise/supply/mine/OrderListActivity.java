package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
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
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.IWebViewActivity;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.message.RequestDetlActivity;
import com.runwise.supply.mine.entity.OrderEntity;
import com.runwise.supply.mine.entity.OrderList;
import com.runwise.supply.mine.entity.OrderResult;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 我的分期
 */
public class OrderListActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private CarInfoListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_collection_list);
        this.setTitleText(true,"我的分期");
        this.setTitleLeftIcon(true,R.drawable.back_btn);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setOnItemClickListener(this);

        adapter = new CarInfoListAdapter();

        if(mOnRefreshListener2 == null){
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    page = 1;
                    requestData(false, REQUEST_START, page+ "", "10");
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, REQUEST_DEN, (++page) +"", "10");
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        loadingLayout.setStatusLoading();
        requestData(false, REQUEST_MAIN, page+ "", "10");
        loadingLayout.setOnRetryClickListener(this);
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    public void requestData (boolean showDialog,int where, String page,String limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPage(page);
        sendConnection("orders/periods.json",request,where,showDialog,OrderResult.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                OrderResult mainResult = (OrderResult)result;
                OrderList mainListResult = mainResult.getData();
                adapter.setData(mainListResult.getEntities());
//                mainListResult.getEntities().get(0).setOrder_status(11);
                loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                OrderResult startResult = (OrderResult) result;
                OrderList startListResult = startResult.getData();
                adapter.setData(startListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                OrderResult endResult = (OrderResult) result;
                OrderList sndListResult = endResult.getData();
                if (sndListResult.getEntities() != null && !sndListResult.getEntities().isEmpty()) {
                    adapter.appendData(sndListResult.getEntities());
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
        loadingLayout.onFailure("",R.drawable.no_network);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderEntity bean = (OrderEntity)parent.getAdapter().getItem(position);
        Intent intent = new Intent(mContext, IWebViewActivity.class);
        intent.putExtra(WebViewActivity.WEB_TITLE,bean.getTitle());
        if (bean.getOrder_status() == 1 ) {
            intent.putExtra(WebViewActivity.WEB_URL, bean.getApply_info_url());
            startActivity(intent);
        }
        else if(bean.getOrder_status() == 11) {
            Intent dealIntent = new Intent(this,RequestDetlActivity.class);
            startActivity(dealIntent);
        }
        else{
            intent.putExtra(WebViewActivity.WEB_URL, bean.getPeriod_url());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page+ "", "10");
    }


    public class CarInfoListAdapter extends IBaseAdapter<OrderEntity> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_step_pay, null);
                holder = new ViewHolder();
                holder.payPic = (SimpleDraweeView) convertView.findViewById(R.id.payPic);
                holder.payTitle = (TextView) convertView.findViewById(R.id.payTitle);
                holder.payStatus = (TextView) convertView.findViewById(R.id.payStatus);
                holder.payName = (TextView) convertView.findViewById(R.id.payName);
                holder.patTime = (TextView) convertView.findViewById(R.id.patTime);
                holder.payMoney = (TextView) convertView.findViewById(R.id.payMoney);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderEntity carInfo = mList.get(position);
            OrderEntity.ImageBean carImage = carInfo.getImage();
            if( carImage != null ) {
                FrecoFactory.getInstance(mContext).disPlay(holder.payPic, carImage.getImg_path());
            }
            switch (carInfo.getOrder_status()) {
                //审核中
                case 1:
                    holder.payStatus.setText("审核中");
                    holder.patTime.setVisibility(View.INVISIBLE);
                    holder.payMoney.setText("分期金额:￥"+carInfo.getPeriod_price());
                    break;
                //已通过
                case 10:
                    holder.payStatus.setText("已通过");
                    holder.patTime.setVisibility(View.INVISIBLE);
                    holder.payMoney.setText("待支付金额:￥"+carInfo.getWanting_price());
                    break;
                //申请被拒
                case 11:
                    holder.payStatus.setText("申请被拒");
                    holder.patTime.setVisibility(View.INVISIBLE);
                    holder.payMoney.setText("分期金额:￥"+carInfo.getPeriod_price());
                    break;
                // 还款中
                case 20:
                    holder.payStatus.setText("还款中");
                    holder.patTime.setVisibility(View.VISIBLE);
                    holder.patTime.setText(carInfo.getNext_at());
                    holder.payMoney.setText("待支付金额:￥"+carInfo.getWanting_price());
                    break;
                //逾期中
                case 21:
                    holder.payStatus.setText("逾期中");
                    holder.patTime.setVisibility(View.INVISIBLE);
                    holder.payMoney.setText("分期金额:￥"+carInfo.getPeriod_price());
                    break;
                //已还完
                case 30:
                    holder.payStatus.setText("已还完");
                    holder.patTime.setVisibility(View.INVISIBLE);
                    holder.payMoney.setText("已还款金额:￥"+carInfo.getComplete_price());
                    break;
            }
            if (carInfo.getDealer() != null) {
                holder.payTitle.setText(carInfo.getDealer().getDealer_name());
            }
            else {
                holder.payTitle.setText(carInfo.getSub_title());
            }
            holder.payName.setText(carInfo.getTitle());
            return convertView;
        }
        class ViewHolder {
             SimpleDraweeView payPic;
             TextView payTitle;
             TextView payStatus;
             TextView payName;
             TextView patTime;
             TextView payMoney;
        }
    }
}
