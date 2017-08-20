package com.runwise.supply.message;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.firstpage.OrderDetailActivity;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.message.entity.MessageListEntity;
import com.runwise.supply.message.entity.MessageResult;
import com.runwise.supply.message.entity.MsgListResult;
import com.runwise.supply.message.entity.MsgSendRequest;
import com.runwise.supply.tools.UserUtils;

/**
 * 聊天信息
 */
public class MessageListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int REQUEST_SEND = 4;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private MsgListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener mOnRefreshListener2;

    private int page = 1;

    @ViewInject(R.id.editText)
    private EditText editText;

    @ViewInject(R.id.chatIcon)
    private ImageView         chatIcon;
    @ViewInject(R.id.chatName)
    private  TextView         chatName;
    @ViewInject(R.id.chatTime)
    private  TextView         chatTime;
    @ViewInject(R.id.chatStatus)
    private TextView         chatStatus;
    @ViewInject(R.id.chatContext)
    private  TextView            chatContext;
    @ViewInject(R.id.sendMsgBtn)
    private TextView sendMsgBtn;
    private MessageResult.OrderBean orderBean;

    public int type;
    private UserInfo userInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        orderBean = (MessageResult.OrderBean) this.getActivity().getIntent().getSerializableExtra("orderBean");
        UserUtils.setOrderStatus(orderBean.getState(),chatStatus,chatIcon);

        chatName.setText(orderBean.getName());
        chatTime.setText(orderBean.getEstimated_time());
        chatContext.setText("共"+orderBean.getAmount()+"件商品,¥"+ orderBean.getAmount_total());

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullListView.setOnItemClickListener(this);

        adapter = new MsgListAdapter();

        if(mOnRefreshListener2 == null){
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, REQUEST_DEN, (++page), 10);

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
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
//        loadingLayout.setStatusLoading();
        requestData(false, REQUEST_MAIN, page, 10);
        loadingLayout.setOnRetryClickListener(this);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = editText.getText().toString();
                if(orderBean != null) {
                    sendMessage(orderBean.getId(),msgText);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(editable.toString())) {
                    sendMsgBtn.setEnabled(false);
                }
                else{
                    sendMsgBtn.setEnabled(true);
                }
            }
        });
    }
    @OnClick(R.id.titleLayout)
    public void doOrderClick(View view) {
        OrderResponse.ListBean bean = new OrderResponse.ListBean();
        bean.setOrderID(orderBean.getId());
        bean.setState(orderBean.getState());
        bean.setCreateDate(orderBean.getCreate_date());
        bean.setName(orderBean.getName());
        if (orderBean.getWaybill() != null) {
            OrderResponse.ListBean.WaybillBean waybillBean = new OrderResponse.ListBean.WaybillBean();
            waybillBean.setWaybillID(orderBean.getWaybill().getId());
            bean.setWaybill(waybillBean);
        }
        bean.setAmount(orderBean.getAmount());
        bean.setAmountTotal(orderBean.getAmount_total());
//        bean.setIsToday(orderBean.getTa);
        bean.setDoneDatetime(orderBean.getDone_datetime());
        bean.setConfirmationDate(orderBean.getConfirmation_date());
        bean.setEstimatedTime(orderBean.getEstimated_time());
        bean.setEndUnloadDatetime(orderBean.getEnd_unload_datetime());
        bean.setStartUnloadDatetime(orderBean.getStart_unload_datetime());
        bean.setCreateUserName(orderBean.getCreate_user_name());
        bean.setLoadingTime(orderBean.getLoading_time());

        Intent intent = new Intent(mContext,OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("order",bean);
        bundle.putInt("orderid",orderBean.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);

//        http://develop.runwise.cn/gongfu/message/order/293/list 在线客服

        //  http://develop.runwise.cn/gongfu/message/waybill/132/293/list 配送员
        if(orderBean != null) {
            switch (type) {
                case 0:
                    int orderId = orderBean.getId();
                    request.setOrder_id(orderId);
                    sendConnection("/gongfu/message/order/" + orderId + "/list", request, where, showDialog, MsgListResult.class);
                    break;
                case 1:
                    MessageResult.OrderBean.WaybillBean wayBill = orderBean.getWaybill();
                    if(wayBill != null && wayBill.getId() != 0) {
                        int oId = orderBean.getId();
                        int bId = wayBill.getId();
                        request.setOrder_id(oId);
                        request.setWaybill_id(bId);
                        sendConnection("/gongfu/message/waybill/"+bId+"/" + oId + "/list", request, where, showDialog, MsgListResult.class);
                    }
                    else{
                        ToastUtil.show(mContext,"该订单没有配送员，暂时不能聊天");
                    }
                    break;
            }

        }
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                MsgListResult mainListResult = (MsgListResult)result.getResult();
                adapter.setData(mainListResult.getList());
//                loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
//                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                pullListView.onRefreshComplete();
                if(adapter.getCount() >0) {
                    pullListView.getRefreshableView().setSelection(adapter.getCount() - 1);
                }
                break;
//            case REQUEST_START:
//                MsgListResult startResult = (MsgListResult)result.getResult();
////                adapter.setData(startResult.getList());
//                pullListView.onRefreshComplete(Integer.MAX_VALUE);
//                break;
            case REQUEST_DEN:
                MsgListResult endResult = (MsgListResult)result.getResult();
                if (endResult.getList() != null && !endResult.getList().isEmpty()) {
                    adapter.appendData(endResult.getList());
//                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
//                else {
//                    pullListView.onRefreshComplete(adapter.getCount());
//                }
                pullListView. onRefreshComplete();
                break;
            case REQUEST_SEND:
                requestData(false, REQUEST_MAIN, 1, 10);
                editText.setText("");
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
        loadingLayout.onFailure(errMsg);
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
    protected int createViewByLayoutId() {
        return R.layout.activity_chatmsg_list;
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }

    public void sendMessage(int orderId,String common) {
        MsgSendRequest msgSendRequest = new MsgSendRequest();
        msgSendRequest.setOrder_id(orderId);
        msgSendRequest.setComment(common);
        switch (type) {
            case 0:
                sendConnection("/gongfu/message/order/" + orderId + "/write", msgSendRequest, REQUEST_SEND, true, null);
                break;
            case 1:
                MessageResult.OrderBean.WaybillBean wayBill = orderBean.getWaybill();
                if(wayBill != null && wayBill.getId() != 0) {
                    int bId = wayBill.getId();
                    msgSendRequest.setWaybill_id(bId);
//                    http://develop.runwise.cn/gongfu/message/waybill/132/293/write
                    sendConnection("/gongfu/message/waybill/" + bId + "/" + orderId + "/write", msgSendRequest, REQUEST_SEND, true, null);
                }
                else {
                    ToastUtil.show(mContext,"该订单没有配送员，暂时不能聊天");
                }
                break;
        }
    }
    public class MsgListAdapter extends IBaseAdapter<MsgListResult.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            int viewType = getItemViewType(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                switch (viewType) {
                    case 0:
                        convertView = View.inflate(mContext, R.layout.item_chat_type1, null);
                        break;
                    case 1:
                        convertView = View.inflate(mContext, R.layout.item_chat_type2, null);
                        break;
                }
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final MsgListResult.ListBean bean =  mList.get(position);
            switch (viewType) {
                case 0:
                    if(bean.getFaq() != null && !bean.getFaq().isEmpty()) {
                        viewHolder.chatContextLeft.setText(bean.getBody()+"\n");
                        for(int i = 0; i < bean.getFaq().size(); i++ ){
                            final MsgListResult.ListBean.FaqBean faq = bean.getFaq().get(i);
                            SpannableString spannableString = null;
                            if( i == bean.getFaq().size() -1) {
                                spannableString = new SpannableString(faq.getQuestion());
                            }
                            else{
                                spannableString = new SpannableString(faq.getQuestion() + "\n");
                            }
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    ds.setColor(getResources().getColor(R.color.base_color));       //设置文件颜色
                                }
                                @Override
                                public void onClick(View widget) {
                                    int orderId = orderBean.getId();
                                    sendMessage(orderId,faq.getQuestion());
                                }
                            }, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            viewHolder.chatContextLeft.append(spannableString);
                        }
                        viewHolder.chatContextLeft.setHighlightColor(Color.TRANSPARENT);
                        viewHolder.chatContextLeft.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    else{
                        viewHolder.chatContextLeft.setText(bean.getBody());
                    }
                    viewHolder.chatTimeLeft.setText(DateFormateUtil.InfoClassShowdateFormat(bean.getDate()));
                    Uri uri = null;
                    if(bean.getAuthor_id() != null && !TextUtils.isEmpty(bean.getAuthor_id().getAvatar_url())) {
                        uri = Uri.parse(Constant.BASE_URL + bean.getAuthor_id().getAvatar_url());
                    }
                    else {
                        switch (type) {
                            case 0:
                                uri = Uri.parse("res:///" + R.drawable.cvrs_customerservice);
                                break;
                            case 1:
                                uri = Uri.parse("res:///" + R.drawable.comment_profilephoto);
                                break;
                        }
                    }
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.chatHeadLeft,uri);
                    break;

                case 1:
                    if( bean.getAuthor_id() != null) {
                        FrecoFactory.getInstance(mContext).disPlay(viewHolder.chatHeadRight, Constant.BASE_URL + bean.getAuthor_id().getAvatar_url());
                    }
                    viewHolder.chatContextRight.setText(bean.getBody());
                    viewHolder.chatTimeRight.setText(DateFormateUtil.InfoClassShowdateFormat(bean.getDate()));
                    break;
            }
            return convertView;
        }
        @Override
        public int getItemViewType(int position) {
            MsgListResult.ListBean bean =  mList.get(position);
            if( userInfo.getUsername() == null || userInfo.getUsername().equals(String.valueOf(bean.getAuthor_id().getName()))) {
                return 1;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        class ViewHolder {
            @ViewInject(R.id.chatHeadLeft)
            SimpleDraweeView chatHeadLeft;
            @ViewInject(R.id.chatContextLeft)
            TextView chatContextLeft;
            @ViewInject(R.id.chatTimeLeft)
            TextView chatTimeLeft;

            @ViewInject(R.id.chatHeadRight)
            SimpleDraweeView        chatHeadRight;
            @ViewInject(R.id.chatContextRight)
            TextView         chatContextRight;
            @ViewInject(R.id.chatTimeRight)
            TextView chatTimeRight;
        }
    }
}
