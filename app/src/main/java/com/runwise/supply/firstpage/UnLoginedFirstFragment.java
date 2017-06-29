package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;

/**
 * Created by libin on 2017/6/26.
 */

public class UnLoginedFirstFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;

    private LayoutInflater layoutInflater;
    private ConvenientBanner banner;
    private RecyclerView recyclerView;
    private StatisticsAdapter sAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = LayoutInflater.from(mContext);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新:轮播图+统计表+列表内容
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多:刷新列表内容
            }
        });
        //表头：放轮播+统计表
        View headView = layoutInflater.inflate(R.layout.unlogin_head_layout,null);
        banner = (ConvenientBanner) headView.findViewById(R.id.ConvenientBanner);
        recyclerView = (RecyclerView)headView.findViewById(R.id.recyclerView);
        pullListView.getRefreshableView().addHeaderView(headView);
        recyclerView.setAdapter(sAdapter);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_unlogin_first;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
