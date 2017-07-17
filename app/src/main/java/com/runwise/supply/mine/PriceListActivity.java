package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.mine.entity.MsgEntity;
import com.runwise.supply.mine.entity.MsgList;
import com.runwise.supply.mine.entity.MsgResult;
import com.runwise.supply.mine.entity.ProductData;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 价目表
 */
public class PriceListActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private PriceAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_title_list);
        this.setTitleText(true,"价目表");
        this.setTitleLeftIcon(true,R.drawable.back_btn);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setOnItemClickListener(this);

        adapter = new PriceAdapter();

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
        requestData(false, REQUEST_MAIN, page, 10);
        loadingLayout.setStatusLoading();
        loadingLayout.setOnRetryClickListener(this);
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = null;
//        request.setLimit(limit);
//        request.setPz(page);
        sendConnection("/gongfu/v2/product/list",request,where,showDialog,ProductData.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                ProductData mainListResult = (ProductData)result.getResult().getData();
                adapter.setData(mainListResult.getList());
                loadingLayout.onSuccess(adapter.getCount(),"",R.drawable.news);
//                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                ProductData startListResult = (ProductData)result.getResult().getData();
                adapter.setData(startListResult.getList());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                ProductData sndListResult = (ProductData)result.getResult().getData();
                if (sndListResult.getList() != null && !sndListResult.getList().isEmpty()) {
                    adapter.appendData(sndListResult.getList());
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
        loadingLayout.onFailure(errMsg);
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
        requestData(false, REQUEST_MAIN, page, 10);
    }


    public class PriceAdapter extends IBaseAdapter<ProductData.ListBean>{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.pirce_layout_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ProductData.ListBean bean =  mList.get(position);
            viewHolder.name.setText(bean.getName());
            viewHolder.number.setText(bean.getDefaultCode());
            viewHolder.content.setText(bean.getUnit());
            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + bean.getImage().getImageSmall());
            viewHolder.value.setText("￥"+bean.getPrice());
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;
            @ViewInject(R.id.number)
            TextView            number;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.value)
            TextView         value;
        }
    }
}
