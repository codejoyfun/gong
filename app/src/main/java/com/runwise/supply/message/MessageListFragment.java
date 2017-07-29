package com.runwise.supply.message;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.mine.OrderDataType;
import com.runwise.supply.mine.entity.OrderResult;
import com.runwise.supply.tools.TimeUtils;

/**
 * 聊天信息
 */
public class MessageListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
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
                    requestData(false, REQUEST_START, page, 10);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, REQUEST_DEN, (++page), 10);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        loadingLayout.setStatusLoading();
        requestData(false, REQUEST_MAIN, page, 10);
        loadingLayout.setOnRetryClickListener(this);
    }


    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        sendConnection("/gongfu/order/list",request,where,showDialog,OrderResult.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                OrderResult mainListResult = (OrderResult)result.getResult();
                adapter.setData(mainListResult.getList());
//                mainListResult.getEntities().get(0).setOrder_status(11);
                loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                OrderResult startResult = (OrderResult)result.getResult();
                adapter.setData(startResult.getList());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                OrderResult endResult = (OrderResult)result.getResult();
                if (endResult.getList() != null && !endResult.getList().isEmpty()) {
                    adapter.appendData(endResult.getList());
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
//        OrderEntity bean = (OrderEntity)parent.getAdapter().getItem(position);
//        Intent intent = new Intent(mContext, IWebViewActivity.class);
//        intent.putExtra(WebViewActivity.WEB_TITLE,bean.getTitle());
//        if (bean.getOrder_status() == 1 ) {
//            intent.putExtra(WebViewActivity.WEB_URL, bean.getApply_info_url());
//            startActivity(intent);
//        }
//        else if(bean.getOrder_status() == 11) {
//            Intent dealIntent = new Intent(this,RequestDetlActivity.class);
//            startActivity(dealIntent);
//        }
//        else{
//            intent.putExtra(WebViewActivity.WEB_URL, bean.getPeriod_url());
//            startActivity(intent);
//        }
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


    public class CarInfoListAdapter extends IBaseAdapter<OrderResult.ListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_step_pay, null);
                holder = new ViewHolder();
                ViewUtils.inject(holder,convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderResult.ListBean bean = mList.get(position);

            holder.payTitle.setText(bean.getName());
            holder.payDate.setText(bean.getEstimated_date());
            holder.patSum.setText("共"+bean.getAmount()+"件商品");
            holder.payMoney.setText(bean.getAmountTotal()+"");
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
            @ViewInject(R.id.payBtn)
            TextView payBtn;
            @ViewInject(R.id.orderStatus)
            ImageView orderStatus;
        }
    }
}
