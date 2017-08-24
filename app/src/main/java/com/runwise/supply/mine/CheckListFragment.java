package com.runwise.supply.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.IWebViewActivity;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.mine.entity.ChannelPandian;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.mine.entity.OrderEntity;
import com.runwise.supply.tools.TimeUtils;

/**
 * 盘点记录
 */
public class CheckListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int REQUEST_CHANNEL = 4;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private CarInfoListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    public OrderDataType orderDataType;
    private String mName;
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
                    requestData(false, REQUEST_DEN, ++page, 10);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        loadingLayout.setStatusLoading();
        requestData(false, REQUEST_MAIN, page, 10);
        loadingLayout.setOnRetryClickListener(this);
        mName = GlobalApplication.getInstance().loadUserInfo().getUsername();
    }


    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        switch (orderDataType) {
            case BENZHOU:
                request.setDate_type(1);
                break;
            case SHANGZHOU:
                request.setDate_type(2);
                break;
            case GENGZAO:
                request.setDate_type(3);
                break;
            default:
                request.setDate_type(0);
        }
        sendConnection("/api/inventory/list",request,where,showDialog,CheckResult.class);
    }
    private void channelPandian(int id) {
        ChannelPandian request = new ChannelPandian();
        request.setId(id);
        request.setState("draft");
        sendConnection("/api/inventory/state",request,REQUEST_CHANNEL,true,null);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                CheckResult mainListResult = (CheckResult)result.getResult().getData();
                adapter.setData(mainListResult.getList());
                loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                CheckResult startResult = (CheckResult)result.getResult().getData();
                adapter.setData(startResult.getList());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                CheckResult endResult = (CheckResult)result.getResult().getData();
                if (endResult.getList() != null && !endResult.getList().isEmpty()) {
                    adapter.appendData(endResult.getList());
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case REQUEST_CHANNEL:
                ToastUtil.show(mContext,"盘点已取消");
                requestData(false, REQUEST_MAIN, page, 10);
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
        CheckResult.ListBean bean = (CheckResult.ListBean)parent.getAdapter().getItem(position);
        Intent intent = new Intent(mContext, CheckDetailActivity.class);
        intent.putExtra("id",bean.getInventoryID()+"");
        intent.putExtra("bean",bean);
        startActivity(intent);
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.activity_jilu_list;
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }


    public class CarInfoListAdapter extends IBaseAdapter<CheckResult.ListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_check_list, null);
                holder = new ViewHolder();
                ViewUtils.inject(holder,convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            final CheckResult.ListBean bean = mList.get(position);
            holder.payDate.setText(TimeUtils.getTimeStamps3(bean.getCreateDate()));
            holder.name.setText(bean.getCreateUser());
            if ("confirm".equals(bean.getState()) && bean.getCreateUser().equals(mName)) {
                holder.money.setVisibility(View.GONE);
                holder.handlerBtn.setVisibility(View.VISIBLE);
                holder.handlerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        channelPandian(bean.getInventoryID());
                    }
                });
            }
            if ("confirm".equals(bean.getState()) && !bean.getCreateUser().equals(mName)) {
                holder.money.setVisibility(View.VISIBLE);
                holder.handlerBtn.setVisibility(View.GONE);
                holder.money.setTextColor(Color.parseColor("#3d3d3d"));
                holder.money.setText("盘点中");
            }
            else {
                if(GlobalApplication.getInstance().getCanSeePrice()) {
                    holder.money.setText("¥"+bean.getValue()+"");
                    if(bean.getValue() >= 0) {
                        holder.money.setTextColor(Color.parseColor("#9cb62e"));
                    }
                    else{
                        holder.money.setTextColor(Color.parseColor("#e75967"));
                    }
                }
                else{
                    holder.money.setText(bean.getNum()+"");
                    if(bean.getNum() >= 0) {
                        holder.money.setTextColor(Color.parseColor("#9cb62e"));
                    }
                    else{
                        holder.money.setTextColor(Color.parseColor("#e75967"));
                    }
                }

                holder.money.setVisibility(View.VISIBLE);
                holder.handlerBtn.setVisibility(View.GONE);
            }
            return convertView;
        }
        class ViewHolder {
            @ViewInject(R.id.payDate)
            TextView payDate;
            @ViewInject(R.id.name)
            TextView name;
            @ViewInject(R.id.money)
            TextView money;
            @ViewInject(R.id.handlerBtn)
            TextView handlerBtn;
        }
    }
}
