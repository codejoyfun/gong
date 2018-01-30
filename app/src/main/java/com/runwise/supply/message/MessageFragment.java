package com.runwise.supply.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.RegisterActivity;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.event.PlatformNotificationEvent;
import com.runwise.supply.message.entity.MessageListEntity;
import com.runwise.supply.message.entity.MessageResult;
import com.runwise.supply.tools.PlatformNotificationManager;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.tools.UserUtils;
import com.runwise.supply.view.SystemUpgradeLayout;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mychao on 2017/7/14.
 * 消息列表
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

    @ViewInject(R.id.tipLayout)
    private View tipLayout;
    public static boolean isLogin;
    private boolean firstLaunch;
    private Handler handler = new Handler();

    @ViewInject(R.id.titleLayout)
    private View titleLayout;
    @ViewInject(R.id.layout_system_upgrade_notice)
    private SystemUpgradeLayout mLayoutUpgradeNotice;

    private SimpleDateFormat mSdfSysTimeSource = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault());
    private SimpleDateFormat mSdfSysTimeTarget = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true,"消息");
//        this.setTitleRigthIcon(true,R.drawable.nav_service_message);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setOnItemClickListener(this);

        adapter = new MessageAdapter();
        if(mOnRefreshListener2 == null){
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    if(isLogin) {
                        String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                        page = 1;
                        requestData(false, REQUEST_START, page, 10);
                    }
                    else{
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pullListView.onRefreshComplete();
                            }
                        }, 1000);
                    }
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
        loadingLayout.setOnRetryClickListener(this);
        loadingLayout.setStatusLoading();

        isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            tipLayout.setVisibility(View.GONE);
            requestData(false, REQUEST_MAIN, page, 10);
        }
        else{
            tipLayout.setVisibility(View.VISIBLE);
            buildData();
        }
        titleLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mLayoutUpgradeNotice.setPageName("客服功能");

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("cn.jpush.android.intent.NOTIFICATION_RECEIVED");
                intent.addCategory("com.runwise.supply");
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("type","platform");
                    JSONObject aps = new JSONObject();
                    aps.put("alerts","测试消息"+System.currentTimeMillis());
                    jsonObject.put("APS",aps);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                intent.putExtra(JPushInterface.EXTRA_EXTRA,jsonObject.toString());
                getActivity().sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(firstLaunch && isLogin) {
            page = 1;
            pullListView.setRefreshing();
        }
        firstLaunch = true;
        MobclickAgent.onPageStart("消息首页"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void requestData (boolean showDialog, int where, int page, int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        sendConnection("/gongfu/message/list",request,where,showDialog,MessageResult.class);
    }

    public void buildData() {
        String jsonStr = "{\"state\": \"A0006\", \"order\": [{\"is_today\": false, \"done_datetime\": \"\", \"create_date\": \"2017-08-04 18:58:59\", \"name\": \"SO565\", \"confirmation_date\": \"2017-08-04 18:59:31\", \"estimated_time\": \"2017-08-05 09:00:00\", \"waybill\": {\"user\": {\"mobile\": \"13737574564\", \"avatar_url\": \"/gongfu/user/avatar/12/-8754775316850759619.png\", \"name\": \"\\u51af\\u5efa\"}, \"vehicle\": {\"name\": \"Audi/A7/\\u54c8H123456\", \"license_plate\": \"\\u54c8H123456\"}, \"name\": \"PS20170804195\", \"id\": 195}, \"amount\": 4.0, \"end_unload_datetime\": false, \"last_message\": {}, \"state\": \"peisong\", \"order_type_id\": null, \"start_unload_datetime\": false, \"create_user_name\": \"\\u674e\\u6052\\u8fbe\", \"loading_time\": \"2017-08-05 09:30:00\", \"id\": 565, \"amount_total\": 58.5}], \"channel\": [{\"read\": false, \"last_message\": {\"body\": \"\\u5404\\u95e8\\u5e97\\u8bf7\\u6ce8\\u610f\\uff0c\\u201c\\u62cd\\u9ec4\\u74dc\\u201d\\u5c06\\u4f1a\\u4ece6\\u67088\\u53f7\\u52307\\u67081\\u53f7\\u671f\\u95f4\\u6682\\u505c\\u4f9b\\u5e94\\uff0c\\u8bf7\\u7559\\u610f\\uff0c\\u4e0d\\u4fbf\\u4e4b\\u5904\\u656c\\u8bf7\\u539f\\u8c05\\u3002\", \"id\": 14047, \"date\": \"2017-06-08 16:05:55\", \"seen\": true, \"model\": \"mail.channel\", \"author_id\": {\"avatar_url\": \"\", \"id\": 3, \"name\": \"Administrator\"}}, \"id\": 22, \"name\": \"\\u7f3a\\u8d27\\u901a\\u77e5\"}]}";
        MessageResult repertoryEntity =  JSON.parseObject(jsonStr,MessageResult.class);
        adapter.setData(handlerMessageList(repertoryEntity));
        loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        tipLayout.setVisibility(View.GONE);
        isLogin = true;
        requestData(false, REQUEST_MAIN, page, 10);
    }
    @Override
    public void onUserLoginout() {
        tipLayout.setVisibility(View.VISIBLE);
        isLogin = false;
        buildData();
    }
    @OnClick({R.id.tipLayout,R.id.closeTip})
    public void loginTipLayout(View view){
        switch (view.getId()) {
            case R.id.tipLayout:
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.closeTip:
                tipLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_msg_list;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                MessageResult mainListResult = (MessageResult)result.getResult();
                adapter.setData(handlerMessageList(mainListResult));
                loadingLayout.onSuccess(adapter.getCount(),"哎呀！这里是空哒~~",R.drawable.default_icon_newsnone);
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

        //加入平台通知
        MessageResult.ChannelBean.LastMessageBeanX beanX = PlatformNotificationManager.getInstance(getContext()).getLastMessageX();
        if(beanX!=null){
            if(channelList==null)channelList = new ArrayList<>();
            MessageResult.ChannelBean platformChannelBean = new MessageResult.ChannelBean();
            platformChannelBean.setName("平台通知");
            platformChannelBean.setLast_message(beanX);
            platformChannelBean.setRead(beanX.isSeen());
            channelList.add(platformChannelBean);
        }

        if(channelList != null) {
            for( int i = 0; i< channelList.size(); i++) {
                MessageResult.ChannelBean channelBean = channelList.get(i);
                MessageListEntity bean = new MessageListEntity();
                bean.setChannelBean(channelBean);
                bean.setType(1);
                if(i == channelList.size()-1) {
                    bean.setFirstItem(true);
                }
                messageList.add(bean);
            }
        }
        if(orderList != null) {
            for(int i = 0; i<orderList.size(); i++) {
                MessageResult.OrderBean orderBean = orderList.get(i);
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
        loadingLayout.onFailure(errMsg,R.drawable.default_icon_checkconnection);
    }

    /**
     * 收到平台通知推送
     * @param event
     */
    @Subscribe
    public void refresh(PlatformNotificationEvent event){
        if(firstLaunch && isLogin) {
            page = 1;
            pullListView.setRefreshing();
        }
        firstLaunch = true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
        boolean isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            MessageListEntity bean = (MessageListEntity)parent.getAdapter().getItem(position);
            if(bean.getType() == 1) {
                if("平台通知".equals(bean.getChannelBean().getName())){
                    PlatformNotificationManager.getInstance(getContext()).setLatestRead();
                    Intent intent = new Intent(mContext,PlatformMsgDetailActivity.class);
                    startActivity(intent);
                    return;
                }
                Intent dealIntent = new Intent(mContext,SystemMsgDetailActivity.class);
                dealIntent.putExtra("id",bean.getChannelBean().getId()+"");
                dealIntent.putExtra("name",bean.getChannelBean().getName());
                startActivity(dealIntent);
            }
            else{
                Intent dealIntent = new Intent(mContext,MessageDetailActivity.class);
                MessageResult.OrderBean orderBean = bean.getOrderBean();
                dealIntent.putExtra("orderBean",orderBean);
                startActivity(dealIntent);
            }
        }
        else{
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
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
            final MessageListEntity bean =  mList.get(position);
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
            switch (viewType) {
                case 0:
                    MessageResult.OrderBean orderBean = bean.getOrderBean();
                    boolean normalOrder;
                    if(orderBean.getName().startsWith("RO")) {
                        normalOrder = false;
                    }
                    else{
                        normalOrder = true;
                    }
                    UserUtils.setOrderStatus(orderBean.getState(),viewHolder.chatStatus,viewHolder.chatIcon,normalOrder);

                    viewHolder.chatName.setText(orderBean.getName());
                    viewHolder.chatTime.setText(TimeUtils.getTimeStamps3(orderBean.getCreate_date()));
                    if(GlobalApplication.getInstance().getCanSeePrice()) {
                        viewHolder.chatContext.setText("共"+ NumberUtil.getIOrD(orderBean.getAmount())+"件商品,¥"+ NumberUtil.getIOrD(orderBean.getAmount_total()));
                    }
                    else {
                        viewHolder.chatContext.setText("共"+ NumberUtil.getIOrD(orderBean.getAmount())+"件商品");
                    }
                    if(orderBean.getLast_message() != null && !TextUtils.isEmpty(orderBean.getLast_message().getBody())) {
                        viewHolder.chatMsg.setVisibility(View.VISIBLE);
                        viewHolder.chatMsg.setText(orderBean.getLast_message().getBody());
                        if(!orderBean.getLast_message().isSeen()) {
                            viewHolder.chatUnRead.setVisibility(View.VISIBLE);
                        }
                        else {
                            viewHolder.chatUnRead.setVisibility(View.GONE);
                        }
                    }
                    else {
                        viewHolder.chatMsg.setVisibility(View.GONE);
                        viewHolder.chatUnRead.setVisibility(View.GONE);
                    }

                    break;

                case 1:
                    if(bean.isFirstItem()) {
                        ((LinearLayout.LayoutParams) viewHolder.bottomLineView.getLayoutParams()).bottomMargin = CommonUtils.dip2px(mContext, 16);
                        ((LinearLayout.LayoutParams) viewHolder.bottomLineView.getLayoutParams()).leftMargin = 0;
                    }
                    else {
                        ((LinearLayout.LayoutParams) viewHolder.bottomLineView.getLayoutParams()).bottomMargin = 0;
                        ((LinearLayout.LayoutParams) viewHolder.bottomLineView.getLayoutParams()).leftMargin = CommonUtils.dip2px(mContext, 12);
                    }
                    MessageResult.ChannelBean channelBean = bean.getChannelBean();
                    viewHolder.msgName.setText(channelBean.getName());
                    if("平台通知".equals(channelBean.getName())){
                        viewHolder.msgIcon.setImageResource(R.drawable.message_icon_notify);
                    }
                    else if("缺货通知".equals(channelBean.getName())) {
                        viewHolder.msgIcon.setImageResource(R.drawable.notify_stockout);
                    }
                    else if("系统升级".equals(channelBean.getName()) || "系统维护".equals(channelBean.getName())) {
                        viewHolder.msgIcon.setImageResource(R.drawable.notify_system);
                    }
                    else if("缓存上传".equals(channelBean.getName())) {
                        viewHolder.msgIcon.setImageResource(R.drawable.restaurant_message_upload);
                    }
                    else {
                        viewHolder.msgIcon.setImageResource(R.drawable.notify_stockout);
                    }
                    if(!channelBean.isRead()) {
                        viewHolder.msgUnRead.setVisibility(View.VISIBLE);
                    }
                    else {
                        viewHolder.msgUnRead.setVisibility(View.GONE);
                    }
                    MessageResult.ChannelBean.LastMessageBeanX messageBeanX = channelBean.getLast_message();
//                    viewHolder.msgTime.setText(DateFormateUtil.InfoClassShowdateFormat(messageBeanX.getDate()));
                    try{
                        viewHolder.msgTime.setText(mSdfSysTimeTarget.format(mSdfSysTimeSource.parse(messageBeanX.getDate())));
                    }catch (ParseException e){
                        viewHolder.msgTime.setText(messageBeanX.getDate());
                    }
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
            @ViewInject(R.id.msgUnRead)
            View msgUnRead;
            @ViewInject(R.id.bottomLineView)
            View bottomLineView;

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
            @ViewInject(R.id.chatUnRead)
            View            chatUnRead;
            @ViewInject(R.id.chatMsg)
            TextView chatMsg;
        }
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("消息首页");
    }
}