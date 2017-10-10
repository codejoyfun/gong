package com.runwise.supply.fragment;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.TransferEntity;

/**
 * 调入调出fragment
 *
 * Created by Dong on 2017/10/10.
 */

public class TransferListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener {

    private static final int REQUEST_REFRESH = 0;
    private static final int REQUEST_MORE = 1;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private TransferListAdapter adapter;
    private int page = 1;

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_transfer_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setOnItemClickListener(this);
        adapter = new TransferListAdapter();
        pullListView.setAdapter(adapter);
        pullListView.setOnRefreshListener(new PullToRefreshListener());
        requestData(true,REQUEST_REFRESH,page,10);
    }

    protected void requestData(boolean showDialog, int where, int page, int limit){
        //TODO
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_REFRESH:
                break;
            case REQUEST_MORE:
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    /**
     * 调度单列表项点击
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    /**
     * 列表adapter
     */
    private class TransferListAdapter extends IBaseAdapter<TransferEntity>{

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        class ViewHolder{

        }
    }

    /**
     * 下拉监听
     */
    private class PullToRefreshListener implements PullToRefreshBase.OnRefreshListener2<ListView>{
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page = 1;
            requestData(false, REQUEST_REFRESH, page, 10);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            requestData(false, REQUEST_MORE, (++page), 10);
        }
    }
}
