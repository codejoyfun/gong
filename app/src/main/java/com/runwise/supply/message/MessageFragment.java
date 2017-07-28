package com.runwise.supply.message;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.message.entity.MessageListEntity;
import com.runwise.supply.message.entity.MessageResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mychao on 2017/7/14.
 */

public class MessageFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private MessageAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true,"消息");
        this.setTitleRigthIcon(true,R.drawable.nav_service_message);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setOnItemClickListener(this);

        adapter = new MessageAdapter();
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
                    requestData(false, REQUEST_DEN, (++page), 10);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        loadingLayout.setStatusLoading();
        requestData(false, REQUEST_MAIN, page, 10);
        loadingLayout.setOnRetryClickListener(this);
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        sendConnection("/gongfu/message/list",request,where,showDialog,MessageResult.class);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_msg;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                MessageResult mainListResult = (MessageResult)result.getResult();
                adapter.setData(handlerMessageList(mainListResult));
                loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                MessageResult startResult = (MessageResult)result.getResult();
                adapter.setData(handlerMessageList(startResult));
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                MessageResult endResult = (MessageResult)result.getResult();
                endResult.setChannel(null);
                if (endResult.getOrder() != null && !endResult.getOrder().isEmpty()) {
                    adapter.appendData(handlerMessageList(endResult));
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
        }
    }
    private List<MessageListEntity> handlerMessageList(MessageResult endResult) {
        List<MessageListEntity> messageList = new ArrayList<>();
         List<MessageResult.OrderBean> orderList = endResult.getOrder();
         List<MessageResult.ChannelBean> channelList = endResult.getChannel();
        if(channelList != null) {
            for(MessageResult.ChannelBean channelBean:channelList) {
                MessageListEntity bean = new MessageListEntity();
                bean.setChannelBean(channelBean);
                bean.setType(1);
                messageList.add(bean);
            }
        }
        if(orderList != null) {
            for(MessageResult.OrderBean orderBean:orderList) {
                MessageListEntity bean = new MessageListEntity();
                bean.setOrderBean(orderBean);
                bean.setType(0);
                messageList.add(bean);
            }
        }
        return messageList;
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
        loadingLayout.onFailure("",R.drawable.no_network);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        OrderEntity bean = (OrderEntity)parent.getAdapter().getItem(position);
//        Intent intent = new Intent(mContext, IWebViewActivity.class);
//        intent.putExtra(WebViewActivity.WEB_TITLE,bean.getTitle());
//        if (bean.getOrder_status() == 1 ) {
//            intent.putExtra(WebViewActivity.WEB_URL, bean.getApply_info_url());
//            startActivity(intent);
//        }
//        else if(bean.getOrder_status() == 11) {
//            Intent dealIntent = new Intent(this,RequestDetlActivity.class);
//            startActivity(dealIntent);
//        }
//        else{
//            intent.putExtra(WebViewActivity.WEB_URL, bean.getPeriod_url());
//            startActivity(intent);
//        }
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }

    public class MessageAdapter extends IBaseAdapter<MessageListEntity>{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            int viewType = getItemViewType(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                switch (viewType) {
                    case 0:
                        convertView = View.inflate(mContext, R.layout.item_msg_type2, null);
                        break;
                    case 1:
                        convertView = View.inflate(mContext, R.layout.item_msg_type1, null);
                        break;
                }
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final MessageListEntity bean =  mList.get(position);
            switch (viewType) {
                case 0:
                    MessageResult.OrderBean orderBean = bean.getOrderBean();
                    //待确认
                    if("draft".equals(orderBean.getState())) {
                        viewHolder.chatStatus.setText("待确认");
                        viewHolder.chatIcon.setImageResource(R.drawable.state_restaurant_1_tocertain);
                    }
                    //已确认
                    else if("sale".equals(orderBean.getState())) {
                        viewHolder.chatStatus.setText("已确认");
                        viewHolder.chatIcon.setImageResource(R.drawable.state_restaurant_2_certain);
                    }
                    //已发货
                    else if("peisong".equals(orderBean.getState())) {
                        viewHolder.chatStatus.setText("已发货");
                        viewHolder.chatIcon.setImageResource(R.drawable.state_restaurant_3_delivering);
                    }
                    //已收货
                    else if("done".equals(orderBean.getState())) {
                        viewHolder.chatStatus.setText("已收货");
                        viewHolder.chatIcon.setImageResource(R.drawable.state_restaurant_2_certain);
                    }
                    //已评价
                    else if("rated".equals(orderBean.getState())) {
                        viewHolder.chatStatus.setText("已评价");
                        viewHolder.chatIcon.setImageResource(R.drawable.state_restaurant_5_rated);
                    }
                    //已取消cancel
                    else{
                        viewHolder.chatStatus.setText("订单关闭");
                        viewHolder.chatIcon.setImageResource(R.drawable.state_restaurant_6_closed);
                    }
                    viewHolder.chatName.setText(orderBean.getName());
                    viewHolder.chatTime.setText(orderBean.getEstimated_time());
                    viewHolder.chatContext.setText("共"+orderBean.getAmount()+"件商品,¥"+ orderBean.getAmount_total());
                    if(orderBean.getLast_message() != null && !TextUtils.isEmpty(orderBean.getLast_message().getBody())) {
                        viewHolder.chatMsg.setVisibility(View.VISIBLE);
                        viewHolder.chatMsg.setText(orderBean.getLast_message().getBody());
                    }
                    else {
                        viewHolder.chatMsg.setVisibility(View.GONE);
                    }
                    break;

                case 1:
                    MessageResult.ChannelBean channelBean = bean.getChannelBean();
                    viewHolder.msgName.setText(channelBean.getName());
                    if("缺货通知".equals(channelBean.getName())) {
                        viewHolder.msgIcon.setImageResource(R.drawable.notify_stockout);
                    }
                    else if("系统升级".equals(channelBean.getName())) {
                        viewHolder.msgIcon.setImageResource(R.drawable.notify_system);
                    }
                    else if("缓存上传".equals(channelBean.getName())) {
                        viewHolder.msgIcon.setImageResource(R.drawable.restaurant_message_upload);
                    }
                    else {
                        viewHolder.msgIcon.setImageResource(R.drawable.notify_stockout);
                    }
                    MessageResult.ChannelBean.LastMessageBeanX messageBeanX = channelBean.getLast_message();
                    viewHolder.msgTime.setText(DateFormateUtil.InfoClassShowdateFormat(messageBeanX.getDate()));
                    viewHolder.msgCotext.setText(messageBeanX.getBody());
                    break;
            }
            return convertView;
        }
        @Override
        public int getItemViewType(int position) {
            MessageListEntity bean =  mList.get(position);
            return bean.getType();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        class ViewHolder {
            @ViewInject(R.id.msgIcon)
            ImageView msgIcon;
            @ViewInject(R.id.msgName)
            TextView msgName;
            @ViewInject(R.id.msgTime)
            TextView msgTime;
            @ViewInject(R.id.msgCotext)
            TextView msgCotext;

            @ViewInject(R.id.chatIcon)
            ImageView         chatIcon;
            @ViewInject(R.id.chatName)
            TextView         chatName;
            @ViewInject(R.id.chatTime)
            TextView         chatTime;
            @ViewInject(R.id.chatStatus)
            TextView         chatStatus;
            @ViewInject(R.id.chatContext)
            TextView            chatContext;
            @ViewInject(R.id.chatMsg)
            TextView chatMsg;
        }
    }

}
