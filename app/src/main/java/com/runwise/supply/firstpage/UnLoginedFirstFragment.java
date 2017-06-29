package com.runwise.supply.firstpage;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.LunboRequest;
import com.runwise.supply.firstpage.entity.NewsRequest;
import com.runwise.supply.firstpage.entity.NewsResponse;

import java.util.List;

/**
 * Created by libin on 2017/6/26.
 */

public class UnLoginedFirstFragment extends NetWorkFragment implements StatisticsAdapter.StatisticsItemClickListener{
    private static final int FROMSTART = 0;
    private static final int FROMEND = 1;

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;

    private LayoutInflater layoutInflater;
    private ConvenientBanner banner;
    private RecyclerView recyclerView;
    private StatisticsAdapter sAdapter;
    private NewsAdapter nAdapter;
    int lastNewsId;                     //缓存最旧的一条新闻id
    private LinearLayoutManager layoutManager;

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
                requestData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多:刷新列表内容
                requestData(false);
            }
        });
        //表头：放轮播+统计表
        View headView = layoutInflater.inflate(R.layout.unlogin_head_layout,null);
        banner = (ConvenientBanner) headView.findViewById(R.id.ConvenientBanner);
        //通过图片比例，计算banner大小 375:175 = w:x
        int height = 175 * CommonUtils.getScreenWidth(mContext) / 375;
        banner.getLayoutParams().height = height;
        recyclerView = (RecyclerView)headView.findViewById(R.id.recyclerView);
        //初始化滑动列表
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        sAdapter = new StatisticsAdapter();
        sAdapter.setItemListener(this);
        recyclerView.setAdapter(sAdapter);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        pullListView.getRefreshableView().addHeaderView(headView);
        nAdapter = new NewsAdapter(mContext);
        pullListView.setAdapter(nAdapter);
        requestData(true);
    }

    private void requestData(boolean isStart) {
        int loadType;
        String loadStr;
        NewsRequest newsRequest = null;
        if (isStart){
            loadType = FROMSTART;
            loadStr = "/gongfu/v2/wechat/news";
            //只在首次加载轮播图
            LunboRequest lbRequest = new LunboRequest("访客端");
            sendConnection("/gongfu/blog/post/list/",lbRequest,0,false,);
        }else{
            loadType = FROMEND;
            loadStr = "/gongfu/v2/wechat/more";
            newsRequest = new NewsRequest(lastNewsId);
        }
        sendConnection(loadStr,newsRequest,loadType,true,NewsResponse.class);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_unlogin_first;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        BaseEntity.ResultBean resultBean= result.getResult();
        NewsResponse response = (NewsResponse) resultBean.getData();
        switch (where){
            case FROMSTART:
                updateUI(response.getList(),true);
                break;
            case FROMEND:
                updateUI(response.getList(),false);
                break;
            default:
                break;
        }
    }

    private void updateUI(List<NewsResponse.ListBean> list,boolean isStart) {
        if (list != null && list.size() > 0){
            int size = list.size();
            lastNewsId = list.get(size - 1).getWechatID();
        }
        if (isStart){
            nAdapter.setData(list);
            pullListView.onRefreshComplete(Integer.MAX_VALUE);
        }else{
            if (list != null && list.size() > 0){
                nAdapter.appendData(list);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
            }else{
                pullListView.onRefreshComplete(nAdapter.getCount());
            }

        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    //TODO:点击更多示例，跳转到登录页面。
    @Override
    public void onItemClick() {
        ToastUtil.show(mContext,"点击了更多");
    }
}
