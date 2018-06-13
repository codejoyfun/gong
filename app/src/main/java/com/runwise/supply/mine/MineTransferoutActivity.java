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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.view.LoadingLayout;
import com.runwise.supply.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MineTransferoutActivity extends NetWorkActivity {

    @BindView(R.id.pullListView)
    PullToRefreshListView mPullListView;
    @BindView(R.id.loadingLayout)
    LoadingLayout mLoadingLayout;
    MineTransferoutAdapter mMineTransferoutAdapter;

    int mPage = 1;

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
                Intent intent = new Intent(getActivityContext(),TransferoutProductListActivity.class);
                startActivity(intent);
            }
        });

        mMineTransferoutAdapter = new MineTransferoutAdapter();

        View headerView = LayoutInflater.from(getActivityContext()).inflate(R.layout.list_header_mine_transfer_out, null);
        mPullListView.getRefreshableView().addHeaderView(headerView);
        mPullListView.setAdapter(mMineTransferoutAdapter);

        mPullListView.setPullToRefreshOverScrollEnabled(false);
        mPullListView.setScrollingWhileRefreshingEnabled(true);
        mPullListView.setMode(PullToRefreshBase.Mode.BOTH);

        PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                mPage = 1;
//                    刷新列表
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
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    public class MineTransferoutAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivityContext()).inflate(R.layout.list_item_mine_transferout, null);
            }
            return convertView;
        }
    }
}
