package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.mine.entity.CheckOrderData;
import com.runwise.supply.mine.entity.MsgEntity;
import com.runwise.supply.mine.entity.MsgList;
import com.runwise.supply.mine.entity.MsgResult;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;

/**
 * 对账单详情
 */
public class AccountsDetailActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;

//    @ViewInject(R.id.loadingLayout)
//    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private CheckOrderAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    @ViewInject(R.id.timeLaterLayout)
    private View timeLaterLayout;
    @ViewInject(R.id.totleMoney)
    private TextView totleMoney;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_account_detail);
        this.setTitleText(true,"对账单");
        this.setTitleLeftIcon(true,R.drawable.back_btn);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setOnItemClickListener(this);

        adapter = new CheckOrderAdapter();

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

        CheckOrderData.BankStatementBean bean = (CheckOrderData.BankStatementBean)this.getIntent().getSerializableExtra("bean");
        adapter.setData(bean.getOrders());
        if(bean.isTimeLater()) {
            timeLaterLayout.setVisibility(View.VISIBLE);
        }
        else {
            timeLaterLayout.setVisibility(View.GONE);
        }
        totleMoney.setText("￥"+bean.getTotalPrice());
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = null;
//        request.setLimit(limit);
//        request.setPz(page);
        sendConnection("/gongfu/v2/account_invoice/list",request,where,showDialog,CheckOrderData.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                CheckOrderData mainListResult = (CheckOrderData)result.getResult().getData();
//                adapter.setData(mainListResult.getBankStatement());
//                loadingLayout.onSuccess(adapter.getCount(),"暂无对账单",R.drawable.default_ico_nonepayorder);
//                pullListView.onRefreshComplete(Integer.MAX_VALUE);
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
//        MsgEntity bean = (MsgEntity)parent.getAdapter().getItem(position);
//        Intent intent = new Intent(mContext,MsgDetailActivity.class);
//        intent.putExtra("msgId",bean.getMessage_id());
//        bean.setIs_read("1");
//        adapter.notifyDataSetChanged();
//        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void retryOnClick(View view) {
//        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }


    public class CheckOrderAdapter extends IBaseAdapter<CheckOrderData.BankStatementBean.OrdersBean>{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_accountsdetail_list, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final CheckOrderData.BankStatementBean.OrdersBean bean =  mList.get(position);
            viewHolder.date.setText(bean.getCreateDate());
            viewHolder.name.setText(bean.getName());
            viewHolder.sum.setText("共" + bean.getAmount()+"件商品");
            viewHolder.money.setText("￥"+bean.getTotalPrice());
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;
            @ViewInject(R.id.money)
            TextView money;
            @ViewInject(R.id.date)
            TextView            date;
            @ViewInject(R.id.sum)
            TextView sum;
        }
    }
}
