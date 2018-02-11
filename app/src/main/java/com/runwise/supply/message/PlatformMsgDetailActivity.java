package com.runwise.supply.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.message.entity.DetailResult;
import com.runwise.supply.tools.PlatformNotificationManager;
import com.umeng.analytics.MobclickAgent;

public class PlatformMsgDetailActivity extends NetWorkActivity implements LoadingLayoutInterface {

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private MsgDetailAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_msg);
        Intent intent = this.getIntent();
        setTitleText(true,"平台通知");
        setTitleLeftIcon(true,R.drawable.back_btn);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
//        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
//        pullListView.setOnItemClickListener(this);

        adapter = new MsgDetailAdapter();
        adapter.setData(PlatformNotificationManager.getInstance(this).getMsgList());
//        if(mOnRefreshListener2 == null){
//            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
//                @Override
//                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
//                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                    page = 1;
//                    requestData(false, REQUEST_START, page, 10);
//                }
//
//                @Override
//                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                    requestData(false, REQUEST_DEN, (++page), 10);
//                }
//            };
//        }
//        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
//        loadingLayout.setStatusLoading();
//        loadingLayout.setOnRetryClickListener(this);
        PlatformNotificationManager manager;

    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        finish();
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @OnClick(R.id.stepPayFinish)
    public void doFinish(View view) {
        this.finish();
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
    }

    public class MsgDetailAdapter extends IBaseAdapter<DetailResult.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            int viewType = getItemViewType(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_msg_detail, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final DetailResult.ListBean bean =  mList.get(position);
            viewHolder.msgContext.setText(bean.getBody());
            viewHolder.msgTime.setText(bean.getDate());
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.msgTime)
            TextView msgTime;
            @ViewInject(R.id.msgContext)
            TextView msgContext;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("平台通知页");
        MobclickAgent.onResume(this);          //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("平台通知页");
        MobclickAgent.onPause(this);          //统计时长
    }
}
