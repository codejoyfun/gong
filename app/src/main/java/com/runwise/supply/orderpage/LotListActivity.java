package com.runwise.supply.orderpage;

import android.content.Intent;
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
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.index.entity.CarImage;
import com.runwise.supply.mine.NotiySettingDateActivity;
import com.runwise.supply.mine.entity.CollectCar;
import com.runwise.supply.mine.entity.CollectCarInfo;
import com.runwise.supply.mine.entity.CollectData;
import com.runwise.supply.mine.entity.CollectResult;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 批次列表
 */
public class LotListActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private NotifyListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    private OrderResponse.ListBean.LinesBean detailBean;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_notiy_list);
        this.setTitleText(true,this.getIntent().getStringExtra("title"));
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        this.setTitleRigthIcon(false,R.drawable.nav_add);

        detailBean = this.getIntent().getParcelableExtra("bean");

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setOnItemClickListener(this);

        adapter = new NotifyListAdapter();

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
                    requestData(false, REQUEST_DEN, (++page) , 10);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
//        loadingLayout.setStatusLoading();
//        requestData(false, REQUEST_MAIN, page, 10);
//        loadingLayout.setOnRetryClickListener(this);
        adapter.setData(detailBean.getLotList());
        loadingLayout.onSuccess(adapter.getCount(),"该商品无批次信息",R.drawable.default_ico_none);

    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    @OnClick(R.id.right_layout)
    public void doRightClick(View view) {
        Intent intent = new Intent(this ,NotiySettingDateActivity.class);
        startActivity(intent);
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        sendConnection("collect/list.json",request,where,showDialog,CollectResult.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                CollectResult mainResult = (CollectResult)result;
                CollectData mainListResult = mainResult.getData();
//                adapter.setData(mainListResult.getEntities());
                loadingLayout.onSuccess(adapter.getCount(),"暂无推送",R.drawable.nonocitify_icon);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                CollectResult startResult = (CollectResult) result;
                CollectData startListResult = startResult.getData();
//                adapter.setData(startListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                CollectResult endResult = (CollectResult) result;
                CollectData sndListResult = endResult.getData();
                if (sndListResult.getEntities() != null && !sndListResult.getEntities().isEmpty()) {
//                    adapter.appendData(sndListResult.getEntities());
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
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
        loadingLayout.onFailure("",R.drawable.nonocitify_icon);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderResponse.ListBean.LinesBean.LotListBean bean = (OrderResponse.ListBean.LinesBean.LotListBean)parent.getAdapter().getItem(position);
        Intent intent = new Intent(mContext,LotListDetailActivity.class);
        intent.putExtra("lotId",bean.getLotID()+"");
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
        requestData(false, REQUEST_MAIN, page, 10);
    }

    public class NotifyListAdapter extends IBaseAdapter<OrderResponse.ListBean.LinesBean.LotListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lot, null);
                holder = new ViewHolder();
                holder.what = (TextView) convertView.findViewById(R.id.what);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderResponse.ListBean.LinesBean.LotListBean bean = mList.get(position);
            holder.time.setText(bean.getName());
            return convertView;
        }

        class ViewHolder {
            TextView what;
            TextView time;
        }

    }
}
