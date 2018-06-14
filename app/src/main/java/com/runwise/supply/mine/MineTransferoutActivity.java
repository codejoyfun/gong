package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.view.LoadingLayout;
import com.runwise.supply.R;
import com.runwise.supply.entity.TransferOutResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.runwise.supply.mine.MineTransferOutDetailActivity.INTENT_KEY_PICKING_ID;

public class MineTransferoutActivity extends NetWorkActivity {

    private static final int REQUEST_CODE_LIST = 1 << 0;
    @BindView(R.id.pullListView)
    PullToRefreshListView mPullListView;
    @BindView(R.id.loadingLayout)
    LoadingLayout mLoadingLayout;
    MineTransferoutAdapter mMineTransferoutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_transferout);
        ButterKnife.bind(this);

        setTitleText(true, "出库单");
        showBackBtn();
        setTitleRigthIcon(true, R.drawable.ic_nav_add);

        findViewById(R.id.title_iv_rigth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                跳去出库商品列表
                Intent intent = new Intent(getActivityContext(), TransferoutProductListActivity.class);
                startActivity(intent);
            }
        });

        mMineTransferoutAdapter = new MineTransferoutAdapter();
        mPullListView.setAdapter(mMineTransferoutAdapter);

        View headerView = LayoutInflater.from(getActivityContext()).inflate(R.layout.list_header_mine_transfer_out, null);
        mPullListView.getRefreshableView().addHeaderView(headerView);

        mPullListView.setPullToRefreshOverScrollEnabled(false);
        mPullListView.setScrollingWhileRefreshingEnabled(true);
        mPullListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);

        PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                    刷新列表
                requestTransferoutList(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                    加载更多
            }
        };
        mPullListView.setOnRefreshListener(onRefreshListener);
        mPullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivityContext(), MineTransferOutDetailActivity.class);
                intent.putExtra(INTENT_KEY_PICKING_ID,mTransferOutResponse.getList().get(position-2).getPickingID());
                startActivity(intent);
            }
        });
        requestTransferoutList(true);
    }

    private void requestTransferoutList(boolean showDialog) {
        Object o = null;
        sendConnection("/api/self/transfer/list", o, REQUEST_CODE_LIST, showDialog, TransferOutResponse.class);

    }
    TransferOutResponse mTransferOutResponse;
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CODE_LIST:
                BaseEntity.ResultBean resultBean = result.getResult();
                mTransferOutResponse = (TransferOutResponse) resultBean.getData();
                mMineTransferoutAdapter.setList(mTransferOutResponse.getList());
                if (mMineTransferoutAdapter.getCount() == 0 && mPullListView.getRefreshableView().getHeaderViewsCount() == 1) {
                    mLoadingLayout.onSuccess(0, "哎呀！这里是空哒~~",R.drawable.default_ico_none);
                } else {
                    mLoadingLayout.onSuccess(mMineTransferoutAdapter.getCount(), "哎呀！这里是空哒~~",R.drawable.default_ico_none);
                }
                mPullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        if (where == REQUEST_CODE_LIST && errMsg.equals(getResources().getString(R.string.network_error))){
            mLoadingLayout.onFailure(errMsg, R.drawable.default_icon_checkconnection);
        }else{
            mLoadingLayout.onFailure(errMsg,R.drawable.nonocitify_icon);
        }
        mLoadingLayout.setOnRetryClickListener(new LoadingLayoutInterface() {
            @Override
            public void retryOnClick(View view) {
                requestTransferoutList(true);
            }
        });
    }

    public class MineTransferoutAdapter extends BaseAdapter {
        List<TransferOutResponse.TransferOut> transferOutList = new ArrayList<>();

        public void setList(List<TransferOutResponse.TransferOut> transferOutList) {
            this.transferOutList.clear();
            this.transferOutList.addAll(transferOutList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return transferOutList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivityContext()).inflate(R.layout.list_item_mine_transferout, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mTvTranferoutName.setText(transferOutList.get(position).getCreatUID());
            viewHolder.mTvTranferoutNum.setText(transferOutList.get(position).getPickingName());
            viewHolder.mTvTranferoutTime.setText(transferOutList.get(position).getDateExpected().split(" ")[0]);

            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tv_tranferout_num)
            TextView mTvTranferoutNum;
            @BindView(R.id.tv_tranferout_time)
            TextView mTvTranferoutTime;
            @BindView(R.id.tv_tranferout_name)
            TextView mTvTranferoutName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
