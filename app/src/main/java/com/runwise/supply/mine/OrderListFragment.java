package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
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
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.firstpage.EvaluateActivity;
import com.runwise.supply.firstpage.OrderDetailActivity;
import com.runwise.supply.firstpage.OrderDoAction;
import com.runwise.supply.firstpage.ReceiveActivity;
import com.runwise.supply.firstpage.entity.CancleRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.repertory.entity.UpdateRepertory;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 我的订单
 */
public class OrderListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int CANCEL = 4;
    private static final int DELETE_ORDER = 5;
    private static final int REQUEST_MAIN_PAGE = 6;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private CarInfoListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    public OrderDataType orderDataType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setOnItemClickListener(this);

        adapter = new CarInfoListAdapter();

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

        requestData(false, REQUEST_MAIN_PAGE, page, 1000);
    }


    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        switch (orderDataType) {
            case BENZHOU:
                request.setStart(TimeUtils.getThisWeekStart());
                request.setEnd(TimeUtils.getCurrentDate());
                break;
            case SHANGZHOU:
                request.setStart(TimeUtils.getPerWeekStart());
                request.setEnd(TimeUtils.getPerWeekEnd());
                break;
            case GENGZAO:
                request.setEnd(TimeUtils.getPerWeekStart());
                break;
        }
        sendConnection("/gongfu/order/list",request,where,showDialog,OrderResponse.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                OrderResponse mainListResult = (OrderResponse)result.getResult().getData();
                adapter.setData(mainListResult.getList());
//                mainListResult.getEntities().get(0).setOrder_status(11);
                loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                OrderResponse startResult = (OrderResponse)result.getResult().getData();
                adapter.setData(startResult.getList());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                OrderResponse endResult = (OrderResponse)result.getResult().getData();
                if (endResult.getList() != null && !endResult.getList().isEmpty()) {
                    adapter.appendData(endResult.getList());
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case CANCEL:
                requestData(true, REQUEST_MAIN, page, 10);
                ToastUtil.show(mContext,"订单已取消");
                break;
            case DELETE_ORDER:
                requestData(true, REQUEST_MAIN, page, 10);
                ToastUtil.show(mContext,"订单已删除");
                break;
            case REQUEST_MAIN_PAGE:
                OrderResponse listSizeBean = (OrderResponse)result.getResult().getData();
                OrderActivity orderActivity = (OrderActivity) mContext;
                switch (orderDataType) {
                    case BENZHOU:
                        orderActivity.setTabText(1,"本周("+listSizeBean.getList().size()+")");
                        break;
                    case SHANGZHOU:
                        orderActivity.setTabText(2,"上周("+listSizeBean.getList().size()+")");
                        break;
                    case GENGZAO:
                        orderActivity.setTabText(3,"更早("+listSizeBean.getList().size()+")");
                        break;
                    default:
                        orderActivity.setTabText(0,"全部("+listSizeBean.getList().size()+")");

                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
        loadingLayout.onFailure("",R.drawable.no_network);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderResponse.ListBean bean = (OrderResponse.ListBean)parent.getAdapter().getItem(position);
        //待确认
        if("draft".equals(bean.getState())) {
//            Intent mIntent = new Intent(mContext,OrderModifyActivity.class);
//            Bundle mBundle = new Bundle();
//            mBundle.putParcelable("order", bean);
//            mIntent.putExtras(mBundle);
//            startActivity(mIntent);

            Intent intent = new Intent(mContext,OrderDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("order",bean);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(mContext,OrderDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("order",bean);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.activity_collection_list;
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }

    /**
     * 取消订单
     */
    private void cancleOrderRequest(OrderResponse.ListBean bean) {
        StringBuffer urlSb = new StringBuffer("/gongfu/order/");
        urlSb.append(bean.getOrderID()).append("/state");
        CancleRequest request = new CancleRequest();
        request.setState("cancel");
        sendConnection(urlSb.toString(),request,CANCEL,true,BaseEntity.ResultBean.class);
    }
    /**
     * 删除订单
     */
    private void deleteOrderRequest(OrderResponse.ListBean bean) {
        StringBuffer urlSb = new StringBuffer("/gongfu/order/");
        urlSb.append(bean.getOrderID()).append("/state");
        CancleRequest request = new CancleRequest();
        request.setState("deleted");
        sendConnection(urlSb.toString(),request,DELETE_ORDER,true,BaseEntity.ResultBean.class);
    }

    public class CarInfoListAdapter extends IBaseAdapter<OrderResponse.ListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_step_pay, null);
                holder = new ViewHolder();
                ViewUtils.inject(holder,convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
           final OrderResponse.ListBean bean = mList.get(position);
            //待确认
            if("draft".equals(bean.getState())) {
                holder.payStatus.setText("待确认");
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.payBtn.setText("取消订单");
                holder.orderStatus.setImageResource(R.drawable.state_restaurant_1_tocertain);
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.setMessage("您确定要取消订单吗?");
                        dialog.setModel(CustomDialog.BOTH);
                        dialog.setLeftBtnListener("不取消了",null);
                        dialog.setRightBtnListener("取消订单", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                cancleOrderRequest(bean);
                            }
                        });
                        dialog.show();
                    }
                });
            }
            //已确认
            else if("sale".equals(bean.getState())) {
                holder.payBtn.setVisibility(View.GONE);
                holder.payStatus.setText("已确认");
                holder.orderStatus.setImageResource(R.drawable.state_restaurant_2_certain);
            }
            //已发货
            else if("peisong".equals(bean.getState())) {
                holder.payStatus.setText("已发货");
                holder.payBtn.setVisibility(View.VISIBLE);
                String btnText;
                if (bean.isIsDoubleReceive()){
                    if (bean.isIsFinishTallying()){
                        //双人收货
                        btnText = "收货";
                    }else{
                        //双人点货
                        btnText = "点货";
                    }
                }else{
                    //正常收货
                    btnText = "收货";
                }
                holder.payBtn.setText(btnText);
                holder.orderStatus.setImageResource(R.drawable.state_restaurant_3_delivering);
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //在这里做判断，是正常收货，还是双人收货,同时判断点货人是谁，如果是自己，则不能再收货
                        OrderDoAction action;
                        if (bean.isIsDoubleReceive()){
                            String userName = GlobalApplication.getInstance().getUserName();
                            if (bean.getTallyingUserName().equals(userName)){
                                action = OrderDoAction.SELFTALLY;
                            }else{
                                action = OrderDoAction.SETTLERECEIVE;
                            }
                        }
                        else {
                            action = OrderDoAction.RECEIVE;
                        }
                        switch (action) {
                            case RECEIVE://正常收货
                                Intent intent = new Intent(mContext,ReceiveActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("order",bean);
                                bundle.putInt("mode",0);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            case SETTLERECEIVE:
                                //点货，计入结算单位
                                Intent sIntent = new Intent(mContext,ReceiveActivity.class);
                                Bundle sBundle = new Bundle();
                                sBundle.putParcelable("order",bean);
                                sBundle.putInt("mode",2);
                                sIntent.putExtras(sBundle);
                                startActivity(sIntent);
                                break;
                            case SELFTALLY:
                                dialog.setMessageGravity();
                                dialog.setMessage("您已经点过货了，应由其他人完成收货");
                                dialog.setRightBtnListener("确认", null);
                                dialog.show();
                                break;
                        }
                    }
                });
            }
            //待评价
            else if("done".equals(bean.getState())) {
                holder.payStatus.setText("待评价");
                holder.payBtn.setVisibility(View.GONE);
                holder.orderStatus.setImageResource(R.drawable.state_restaurant_2_certain);
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.payBtn.setText("评价");
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent2 = new Intent(mContext,EvaluateActivity.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putParcelable("order",bean);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                    }
                });
            }
            //已评价
            else if("rated".equals(bean.getState())) {
                holder.payStatus.setText("已评价");
                holder.payBtn.setVisibility(View.GONE);
                holder.orderStatus.setImageResource(R.drawable.state_restaurant_5_rated);
            }
            //已取消cancel
            else{
                holder.payStatus.setText("订单关闭");
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.payBtn.setText("删除订单");
                holder.orderStatus.setImageResource(R.drawable.state_restaurant_6_closed);
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.setMessage("您确定要取消订单吗?");
                        dialog.setModel(CustomDialog.BOTH);
                        dialog.setLeftBtnListener("不删除了",null);
                        dialog.setRightBtnListener("删除订单", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                deleteOrderRequest(bean);
                            }
                        });
                        dialog.show();
                    }
                });
            }
            holder.payTitle.setText(bean.getName());
            holder.payDate.setText(TimeUtils.getTimeStamps3(bean.getCreateDate()));
            holder.patSum.setText("共"+bean.getAmount()+"件商品");
            if(GlobalApplication.getInstance().getCanSeePrice()) {
                holder.payMoney.setVisibility(View.VISIBLE);
                holder.payMoney.setText("共"+bean.getAmountTotal());
            }
            else {
                holder.payMoney.setVisibility(View.GONE);
            }
            if(bean.getHasReturn() > 0) {
                holder.returnTv.setVisibility(View.VISIBLE);
            }
            else {
                holder.returnTv.setVisibility(View.GONE);
            }
            if("done".equals(bean.getState()) && bean.getDeliveredQty() != bean.getAmount()) {
               holder.realTv.setVisibility(View.VISIBLE);
            }
            else{
                holder.realTv.setVisibility(View.GONE);
            }
//            holder.payMoney.setText(bean.getAmountTotal()+"");
            return convertView;
        }
        class ViewHolder {
            @ViewInject(R.id.payTitle)
            TextView payTitle;
            @ViewInject(R.id.payStatus)
            TextView payStatus;
            @ViewInject(R.id.payDate)
            TextView payDate;
            @ViewInject(R.id.patSum)
            TextView patSum;
            @ViewInject(R.id.payMoney)
            TextView payMoney;
            @ViewInject(R.id.payBtn)
            TextView payBtn;
            @ViewInject(R.id.orderStatus)
            ImageView orderStatus;
            @ViewInject(R.id.returnTv)
            TextView returnTv;
            @ViewInject(R.id.realTv)
            TextView realTv;
        }
    }
}
