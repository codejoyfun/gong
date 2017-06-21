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
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.mine.entity.MsgEntity;
import com.runwise.supply.mine.entity.MsgList;
import com.runwise.supply.mine.entity.MsgResult;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 消息列表
 */
public class MsgListActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
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
        this.setTitleText(true,"消息");
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
        requestData(false, REQUEST_MAIN, page+ "", "10");
        loadingLayout.setStatusLoading();
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
        sendConnection("message/list.json",request,where,showDialog,MsgResult.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                MsgResult mainResult = (MsgResult)result;
                MsgList mainListResult = mainResult.getData();
                adapter.setData(mainListResult.getEntities());
                loadingLayout.onSuccess(adapter.getCount(),"",R.drawable.news);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                MsgResult startResult = (MsgResult) result;
                MsgList startListResult = startResult.getData();
                adapter.setData(startListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                MsgResult endResult = (MsgResult) result;
                MsgList sndListResult = endResult.getData();
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
        ToastUtil.show(mContext,errMsg);
        loadingLayout.onFailure("",R.drawable.no_network);
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MsgEntity bean = (MsgEntity)parent.getAdapter().getItem(position);
        Intent intent = new Intent(mContext,MsgDetailActivity.class);
        intent.putExtra("msgId",bean.getMessage_id());
        bean.setIs_read("1");
        adapter.notifyDataSetChanged();
        startActivity(intent);
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


    public class CarInfoListAdapter extends IBaseAdapter<MsgEntity> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_msg_list, null);
                holder = new ViewHolder();
                holder.msgFlag =  convertView.findViewById(R.id.msgFlag);
                holder.msgTitle = (TextView) convertView.findViewById(R.id.msgTitle);
                holder.msgTime = (TextView) convertView.findViewById(R.id.msgTime);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            MsgEntity bean = mList.get(position);
            if("1".equals(bean.getIs_read())) {
                holder.msgFlag.setVisibility(View.GONE);
                holder.msgTime.setTextColor(Color.parseColor("#999999"));
                holder.msgTitle.setTextColor(Color.parseColor("#999999"));
            }
            else {
                holder.msgFlag.setVisibility(View.VISIBLE);
                holder.msgTime.setTextColor(Color.parseColor("#333333"));
                holder.msgTitle.setTextColor(Color.parseColor("#333333"));
            }
            holder.msgTime.setText(bean.getCreated_at());
            holder.msgTitle.setText(bean.getTitle());
            return convertView;
        }

        class ViewHolder {
            View msgFlag;
            TextView msgTitle;
            TextView msgTime;
        }

    }
}
