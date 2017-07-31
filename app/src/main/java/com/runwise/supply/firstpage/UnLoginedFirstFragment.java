package com.runwise.supply.firstpage;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.business.BannerHolderView;
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.firstpage.entity.LunboRequest;
import com.runwise.supply.firstpage.entity.LunboResponse;
import com.runwise.supply.firstpage.entity.NewsRequest;
import com.runwise.supply.firstpage.entity.NewsResponse;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by libin on 2017/6/26.
 */

public class UnLoginedFirstFragment extends NetWorkFragment implements StatisticsAdapter.StatisticsItemClickListener{
    private static final int FROMSTART = 0;
    private static final int FROMEND = 1;
    private static final int FROMLB = 2;
    private static final String LOGIN_FRAGMENT = "loginFragment";

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.header)
    private View headView;

    private LayoutInflater layoutInflater;
    private ConvenientBanner banner;
    private RecyclerView recyclerView;
    private StatisticsAdapter sAdapter;
    private NewsAdapter nAdapter;
    private int lastNewsId;                     //缓存最旧的一条新闻id
    private boolean isLoadFirst = true;         //标记只加载一次
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
//        View headView = layoutInflater.inflate(R.layout.unlogin_head_layout,null);
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

//        pullListView.getRefreshableView().addHeaderView(headView);
        nAdapter = new NewsAdapter(mContext);
        pullListView.setAdapter(nAdapter);
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsResponse.ListBean bean = (NewsResponse.ListBean)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(mContext,PageDeatailActivity.class);
                intent.putExtra("url",bean.getUrl());
                startActivity(intent);
            }
        });
        requestLB();
    }

    @Override
    public void onResume() {
        super.onResume();
        //判断登录状态,进行fragment的切换
        if (SPUtils.isLogin(mContext)){
            MainActivity ma = (MainActivity) getActivity();
            if (ma.getCurrentTabIndex() == 0){
                switchContent(this,new LoginedFirstFragment());
            }
        }else{
            //如果之前有登录fragment，这里就得隐藏它
            LoginedFirstFragment fragment = (LoginedFirstFragment) getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);
            if (fragment != null && fragment.isVisible()){
                getFragmentManager().beginTransaction().hide(fragment).show(this).commit();
            }
           pullListView.setRefreshing(true);
        }
    }
    private void requestLB(){
        //只在首次加载轮播图+统计表
        LunboRequest lbRequest = new LunboRequest("餐户端");
        sendConnection("/gongfu/blog/post/list/",lbRequest,FROMLB,true,LunboResponse.class);

    }
    private void requestData(boolean isStart) {
        int loadType;
        String loadStr;
        NewsRequest newsRequest = null;
        if (isStart){
            loadType = FROMSTART;
            loadStr = "/gongfu/v2/wechat/news";
        }else{
            loadType = FROMEND;
            loadStr = "/gongfu/v2/wechat/more";
            newsRequest = new NewsRequest(lastNewsId);
        }
        sendConnection(loadStr,newsRequest,loadType,false,NewsResponse.class);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_unlogin_first;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case FROMSTART:
                BaseEntity.ResultBean resultBean= result.getResult();
                NewsResponse sRes = (NewsResponse) resultBean.getData();
                updateUI(sRes.getList(),true);
                break;
            case FROMEND:
                BaseEntity.ResultBean resultBean2= result.getResult();
                NewsResponse eRes = (NewsResponse) resultBean2.getData();
                updateUI(eRes.getList(),false);
                break;
            case FROMLB:
                LunboResponse lRes = (LunboResponse) result.getResult();
                updateLb(lRes.getPost_list());
                break;
            default:
                break;
        }
    }

    private void updateLb(final List<ImagesBean> post_list) {
        banner.setPages(new CBViewHolderCreator<BannerHolderView>() {
            @Override
            public BannerHolderView createHolder() {
                return new BannerHolderView();
            }
        }, post_list)
                .setPointViewVisible(true)
//                .setPageIndicator(new Int[]{R.id.})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(5000)
                .setPageIndicator(new int[]{R.drawable.guidepage_circle_normal,R.drawable.guidepage_circle_highlight})
                .setPointViewVisible(true)
                .setManualPageable(true);  //设置手动影响;
        banner.setCanLoop(true);
        banner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ImagesBean bean = post_list.get(position);
                if (bean != null && bean.getCover_url() != null){
                    Intent intent = new Intent(mContext,PageDeatailActivity.class);
                    intent.putExtra("url", Constant.BASE_URL + bean.getPost_url());
                    startActivity(intent);
                }
            }
        });
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

    public void switchContent(Fragment from, Fragment to) {
        FragmentManager mManager = getFragmentManager();
        if (from != to) {
            FragmentTransaction mTransaction = mManager.beginTransaction();
            if (!to.isAdded()) {
                mTransaction.hide(from).add(R.id.realtabcontent, to,LOGIN_FRAGMENT);

            } else
                mTransaction.hide(from).show(to);
            mTransaction.commit();
        }

    }
}
