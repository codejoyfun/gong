package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.firstpage.ReturnDetailActivity;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.mine.entity.MsgList;
import com.runwise.supply.mine.entity.MsgResult;
import com.runwise.supply.mine.entity.ReturnData;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

/**
 * 退货记录
 */
public class ReturnListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int PRODUCT_GET = 4;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private CarInfoListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    public OrderDataType orderDataType;
    private List<ReturnOrderBean.ListBean> allList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        //pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
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
//        requestData(false, REQUEST_MAIN, page, 10);
//        loadingLayout.setStatusLoading();
//        loadingLayout.setOnRetryClickListener(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ReturnData event) {
        switch (orderDataType) {
            case BENZHOU:
                allList = event.getThisWeekList();
                adapter.setData(allList);
                break;
            case SHANGZHOU:
                allList = event.getLastWeekList();
                adapter.setData(allList);
                break;
            case GENGZAO:
                allList = event.getEarlierList();
                adapter.setData(allList);
                break;
            case ALL:
                allList = event.getAllList();
                adapter.setData(allList);
                break;
        }
        pullListView.onRefreshComplete(adapter.getCount());
        loadingLayout.onSuccess(adapter.getCount(),"哎呀！这里是空哒~~",R.drawable.default_ico_none);
    }


    public void requestData (boolean showDialog,int where, int page,int limit) {
//        PageRequest request = new PageRequest();
//        request.setLimit(limit);
//        request.setPz(page);
//        sendConnection("message/list.json",request,where,showDialog,MsgResult.class);
        Object param = null;
        sendConnection("/API/v2/return_order/list",param,PRODUCT_GET,false, ReturnData.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                MsgResult mainResult = (MsgResult)result;
                MsgList mainListResult = mainResult.getData();
//                adapter.setData(mainListResult.getEntities());
                loadingLayout.onSuccess(adapter.getCount(),"",R.drawable.news);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                MsgResult startResult = (MsgResult) result;
                MsgList startListResult = startResult.getData();
//                adapter.setData(startListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                MsgResult endResult = (MsgResult) result;
                MsgList sndListResult = endResult.getData();
                if (sndListResult.getEntities() != null && !sndListResult.getEntities().isEmpty()) {
//                    adapter.appendData(sndListResult.getEntities());
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case PRODUCT_GET:
                ReturnData repertoryEntity = (ReturnData)result.getResult().getData();
                EventBus.getDefault().post(repertoryEntity);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
//        loadingLayout.onFailure("",R.drawable.no_network);
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean)parent.getAdapter().getItem(position);
        Intent intent = new Intent(mContext,ReturnDetailActivity.class);
        intent.putExtra("rid",bean.getReturnOrderID()+"");
        startActivity(intent);
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.activity_collection_list;
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }


    public class CarInfoListAdapter extends IBaseAdapter<ReturnOrderBean.ListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_return_list, null);
                holder = new ViewHolder();
                ViewUtils.inject(holder,convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            ReturnOrderBean.ListBean bean = mList.get(position);

          if("process".equals(bean.getState())) {
                holder.payStatus.setText("退货中");
                holder.orderStatus.setImageResource(R.drawable.state_delivery_7_return);
            }
            //已发货
            else if("done".equals(bean.getState())) {
                holder.payStatus.setText("已退货");
                holder.orderStatus.setImageResource(R.drawable.state_delivery_7_return);
            }
           else  {
              holder.payStatus.setText("已退货");
              holder.orderStatus.setImageResource(R.drawable.state_delivery_7_return);
          }

            holder.payTitle.setText(bean.getName());
            holder.payDate.setText(TimeUtils.getTimeStamps3(bean.getCreateDate()));
            holder.patSum.setText("共"+ NumberUtil.getIOrD(bean.getAmount())+"件商品");
            if(SampleApplicationLike.getInstance().getCanSeePrice()) {
                holder.payMoney.setVisibility(View.VISIBLE);
                holder.payMoney.setText("共"+bean.getAmountTotal());
            }
            else {
                holder.payMoney.setVisibility(View.GONE);
            }
//            holder.payMoney.setText(bean.getAmountTotal()+"");
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.payTitle)
            TextView payTitle;
            @ViewInject(R.id.payStatus)
            TextView payStatus;
            @ViewInject(R.id.payDate)
            TextView payDate;
            @ViewInject(R.id.patSum)
            TextView patSum;
            @ViewInject(R.id.payMoney)
            TextView payMoney;
            @ViewInject(R.id.orderStatus)
            ImageView orderStatus;
        }

    }
}
