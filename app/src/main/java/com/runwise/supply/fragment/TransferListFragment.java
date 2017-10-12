package com.runwise.supply.fragment;

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
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.TransferEntity;

import static com.runwise.supply.entity.TransferEntity.STATE_DELIVER;
import static com.runwise.supply.entity.TransferEntity.STATE_SUBMITTED;

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
    private PullToRefreshListView mPullListView;
    private TransferListAdapter mTransferListAdapter;
    private int page = 1;

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_transfer_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPullListView.setPullToRefreshOverScrollEnabled(false);
        mPullListView.setScrollingWhileRefreshingEnabled(true);
        mPullListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullListView.setOnItemClickListener(this);
        mTransferListAdapter = new TransferListAdapter();
        mPullListView.setAdapter(mTransferListAdapter);
        mPullListView.setOnRefreshListener(new PullToRefreshListener());
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
        //去详情页

    }

    /**
     * 列表adapter
     */
    private class TransferListAdapter extends IBaseAdapter<TransferEntity>{

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView==null){
                convertView = View.inflate(getActivity(),R.layout.item_transfer_list,null);
                viewHolder = new ViewHolder();
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final TransferEntity transferEntity = mList.get(position);
            viewHolder.mmTvTitle.setText(transferEntity.getTransferId());
            viewHolder.mmTvCreateTime.setText(transferEntity.getTransferId());

            if(STATE_SUBMITTED.equals(transferEntity.getState())){//已提交
                viewHolder.mmTvStatus.setText("已提交");
                viewHolder.mmTvAction.setText("取消");
            }else if(STATE_DELIVER.equals(transferEntity.getState())){//已发出
                viewHolder.mmTvStatus.setText("已发出");
                viewHolder.mmTvAction.setText("入库");
                viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                    }
                });
            }

            return convertView;
        }

        class ViewHolder{
            @ViewInject(R.id.item_transfer_title_tv)
            TextView mmTvTitle;
            @ViewInject(R.id.tv_item_transfer_status)
            TextView mmTvStatus;
            @ViewInject(R.id.tv_item_transfer_action)
            TextView mmTvAction;
            @ViewInject(R.id.tv_item_transfer_locations)
            TextView mmTvLocations;
            @ViewInject(R.id.tv_item_transfer_price)
            TextView mmTvPrice;
            @ViewInject(R.id.tv_item_transfer_date)
            TextView mmTvCreateTime;
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
