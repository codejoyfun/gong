package com.runwise.supply.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderStateAdapter;
import com.runwise.supply.adapter.OrderTimeAdapter;
import com.runwise.supply.entity.OrderDetailResponse;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.firstpage.EvaluateActivity;
import com.runwise.supply.firstpage.OrderDetailActivity;
import com.runwise.supply.firstpage.OrderDoAction;
import com.runwise.supply.firstpage.ReceiveActivity;
import com.runwise.supply.firstpage.entity.CancleRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.OrderAgainActivity;
import com.runwise.supply.tools.InventoryCacheManager;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.OrderDateSelectDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.vov.vitamio.utils.NumberUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderListFragmentV2 extends NetWorkFragment implements AdapterView.OnItemClickListener, LoadingLayoutInterface {
    private static final int REQUEST_ORDER_LIST = 1;
    @BindView(R.id.tv_order_state)
    TextView mTvOrderState;
    @BindView(R.id.tv_order_time)
    TextView mTvOrderTime;
    @BindView(R.id.ll_tab)
    LinearLayout mLlTab;
    @BindView(R.id.pullListView)
    PullToRefreshListView mPullListView;
    @BindView(R.id.lv_order_state)
    ListView mLvOrderState;
    @BindView(R.id.lv_order_time)
    ListView mLvOrderTime;
    @BindView(R.id.loadingLayout)
    LoadingLayout mLoadingLayout;
    Unbinder unbinder;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;
    @BindView(R.id.rl_order_state)
    View mRlOrderState;
    @BindView(R.id.rl_order_time)
    View mRlOrderTime;
    @BindView(R.id.iv_order_time)
    View mIvOrderTime;
    @BindView(R.id.iv_order_state)
    View mIvOrderState;


    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int CANCEL = 4;
    private static final int DELETE_ORDER = 5;
    private static final int REQUEST_MAIN_PAGE = 6;
    private static final int REQUEST_ORDER = 7;
    private static final int REQUEST_DETAIL_FOR_ORDER_AGAIN = 8;

    private int page = 1;
    CarInfoListAdapter adapter;

    OrderDateSelectDialog mOrderDateSelectDialog;

    public OrderListFragmentV2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRlOrderState.setVisibility(View.GONE);
        mRlOrderTime.setVisibility(View.GONE);

        OrderStateAdapter orderStateAdapter = new OrderStateAdapter();
        mLvOrderState.setAdapter(orderStateAdapter);
        mLvOrderState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderStateAdapter.setSelect(position);
                orderStateAdapter.notifyDataSetChanged();
                mRlOrderState.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                mRlOrderState.setVisibility(View.GONE);
                mIvOrderState.setRotation(0);
                mTvOrderState.setText(orderStateAdapter.getItem(position).state);
                mState = getState(orderStateAdapter.getItem(position).state);
                requestOrderList(true, mState, REQUEST_START, page, mStartTime, mEndTime);
            }
        });
        OrderTimeAdapter orderTimeAdapter = new OrderTimeAdapter();
        mLvOrderTime.setAdapter(orderTimeAdapter);
        mLvOrderTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderTimeAdapter.setSelect(position);
                orderTimeAdapter.notifyDataSetChanged();
                mRlOrderTime.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                mRlOrderTime.setVisibility(View.GONE);
                if (position != orderTimeAdapter.getCount() - 1){
                    mTvOrderTime.setText(orderTimeAdapter.getItem(position).state);
                }
                mIvOrderTime.setRotation(0);
                setTime(orderTimeAdapter.getItem(position).state);

            }
        });
//        初始化订单列表
        mPullListView.setPullToRefreshOverScrollEnabled(false);
        mPullListView.setScrollingWhileRefreshingEnabled(true);
        mPullListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullListView.setOnItemClickListener(this);

        adapter = new CarInfoListAdapter();

        if (mOnRefreshListener2 == null) {
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    page = 1;
                    requestOrderList(false, mState, REQUEST_START, page, mStartTime, mEndTime);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestOrderList(false, mState, REQUEST_DEN, (++page), mStartTime, mEndTime);
                }
            };

        }
        mPullListView.setOnRefreshListener(mOnRefreshListener2);
        mPullListView.setAdapter(adapter);
        page = 1;
        mLoadingLayout.setStatusLoading();
        requestOrderList(false, "", REQUEST_MAIN, page, mStartTime, mEndTime);
        mLoadingLayout.setOnRetryClickListener(this);
        mOrderDateSelectDialog = new OrderDateSelectDialog(getActivity());
        mOrderDateSelectDialog.setPickerClickListener(new OrderDateSelectDialog.PickerClickListener() {
            @Override
            public void doPickClick(String startYMD, String endYMD) {
                mTvOrderTime.setText(startYMD+"-"+endYMD);
                String[] startYMDArray = startYMD.split("/");
                String[] endYMDArray = endYMD.split("/");
                mStartTime = startYMDArray[0]+"-"+startYMDArray[1]+"-"+startYMDArray[2];
                mEndTime = endYMDArray[0]+"-"+endYMDArray[1]+"-"+endYMDArray[2];
                requestOrderList(true, "", REQUEST_START, page, mStartTime, mEndTime);
                mOrderDateSelectDialog.dismiss();
            }
        });
    }

    //    退货单状态：
//    待确认 draft
//    退货中 process
//    已完成 done
//    订单状态：
//    待确认 draft
//    待发货 sale
//    待收货 peisong
//    待评价 done
//    已完成 rated
    private String getState(String stateDesc) {
        switch (stateDesc) {
            case "全部状态":
                return "";
            case "待确认":
                return "draft";
            case "待发货":
                return "sale";
            case "待收货":
                return "peisong";
            case "待评价":
                return "done";
            case "已完成":
                return "rated";
        }
        return "";
    }

    private void setTime(String timeDesc) {
        switch (timeDesc) {
            case "全部时间":
                mStartTime = "";
                mEndTime = "";
                requestOrderList(true, mState, REQUEST_START, page, mStartTime, mEndTime);
                break;
            case "本周":
                mStartTime = TimeUtils.getThisWeekStart();
                mEndTime = TimeUtils.getThisWeekEnd();
                requestOrderList(true, mState, REQUEST_START, page, mStartTime, mEndTime);
                break;
            case "上周":
                mStartTime = TimeUtils.getPerWeekStart();
                mEndTime = TimeUtils.getPerWeekEnd();
                requestOrderList(true, mState, REQUEST_START, page, mStartTime, mEndTime);
                break;
            case "自定义区间":
                mOrderDateSelectDialog.show();
//                new CustomDatePickerDialog(getActivity()).show();
                break;
        }

    }


    String mStartTime = "";
    String mEndTime = "";
    String mState = "";

    private void requestOrderList(boolean showDialog, String state, int where, int page, String startTime, String endTime) {
        PageRequest request = new PageRequest();
        request.setLimit(10);
        request.setPz(page);
        request.setStart(startTime);
        request.setEnd(endTime);
        request.setState(state);
        sendConnection("/api/order/list", request, where, showDialog, OrderResponse.class);
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_order_fragment_v2;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                OrderResponse mainListResult = (OrderResponse) result.getResult().getData();
                adapter.setData(mainListResult.getList());
//                mainListResult.getEntities().get(0).setOrder_status(11);
                mLoadingLayout.onSuccess(adapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_ordernone);
                mPullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                OrderResponse startResult = (OrderResponse) result.getResult().getData();
                adapter.setData(startResult.getList());
                mPullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                OrderResponse endResult = (OrderResponse) result.getResult().getData();
                if (endResult.getList() != null && !endResult.getList().isEmpty()) {
                    adapter.appendData(endResult.getList());
                    mPullListView.onRefreshComplete(Integer.MAX_VALUE);
                } else {
                    mPullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case CANCEL:
                requestOrderList(true, mState, REQUEST_MAIN, page, mStartTime, mEndTime);
                ToastUtil.show(mContext, "订单已取消");
                break;
            case DELETE_ORDER:
                requestOrderList(true, mState, REQUEST_MAIN, page, mStartTime, mEndTime);
                ToastUtil.show(mContext, "订单已删除");
                break;
            case REQUEST_ORDER:
                OrderDetailResponse orderDetailResponse = (OrderDetailResponse) result.getResult().getData();
                int orderId = orderDetailResponse.getOrder().getOrderID();
                int index = 0;
                for (int i = 0; i < adapter.getList().size(); i++) {
                    OrderResponse.ListBean listBean = adapter.getList().get(i);
                    if (listBean.getOrderID() == orderId) {
                        index = i;
                        break;
                    }
                }
                if (index <= adapter.getList().size() - 1) {
                    adapter.getList().set(index, orderDetailResponse.getOrder());
                    adapter.notifyDataSetChanged();
                }
                break;
            case REQUEST_DETAIL_FOR_ORDER_AGAIN:
                OrderDetailResponse res = (OrderDetailResponse) result.getResult().getData();
                OrderAgainActivity.start(getActivity(), res.getOrder());
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.rl_order_state_text, R.id.rl_order_time_text, R.id.rl_order_state, R.id.rl_order_time})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_order_state_text:
                    if (mRlOrderState.getVisibility() == View.GONE) {
                        mRlOrderState.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_top_200));
                        mRlOrderState.setVisibility(View.VISIBLE);
                        mRlOrderTime.setVisibility(View.GONE);
                        mIvOrderState.setRotation(180);
                    } else {
                        mRlOrderState.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                        mRlOrderState.setVisibility(View.GONE);
                        mIvOrderState.setRotation(0);
                    }
                    break;
                case R.id.rl_order_time_text:
                    if (mRlOrderTime.getVisibility() == View.GONE) {
                        mRlOrderTime.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_top_200));
                        mRlOrderTime.setVisibility(View.VISIBLE);
                        mRlOrderState.setVisibility(View.GONE);
                        mIvOrderTime.setRotation(180);
                    } else {
                        mRlOrderTime.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                        mRlOrderTime.setVisibility(View.GONE);
                        mIvOrderTime.setRotation(0);
                    }
                    break;
                case R.id.rl_order_state:
                    mRlOrderState.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                    mRlOrderState.setVisibility(View.GONE);
                    break;
                case R.id.rl_order_time:
                    mRlOrderTime.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                    mRlOrderTime.setVisibility(View.GONE);
                    break;
            }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderResponse.ListBean bean = (OrderResponse.ListBean) parent.getAdapter().getItem(position);
        //待确认
        if ("draft".equals(bean.getState())) {
//            Intent mIntent = new Intent(mContext,OrderModifyActivity.class);
//            Bundle mBundle = new Bundle();
//            mBundle.putParcelable("order", bean);
//            mIntent.putExtras(mBundle);
//            startActivity(mIntent);

            Intent intent = new Intent(mContext, OrderDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("order", bean);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, OrderDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("order", bean);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void retryOnClick(View view) {
        mLoadingLayout.setStatusLoading();
        page = 1;
        requestOrderList(false, mState, REQUEST_MAIN, page, mStartTime, mEndTime);
    }

    /**
     * 取消订单
     */
    private void cancleOrderRequest(OrderResponse.ListBean bean) {
        StringBuffer urlSb = new StringBuffer("/gongfu/order/");
        urlSb.append(bean.getOrderID()).append("/state");
        CancleRequest request = new CancleRequest();
        request.setState("cancel");
        sendConnection(urlSb.toString(), request, CANCEL, true, BaseEntity.ResultBean.class);
    }

    /**
     * 删除订单
     */
    private void deleteOrderRequest(OrderResponse.ListBean bean) {
        StringBuffer urlSb = new StringBuffer("/gongfu/order/");
        urlSb.append(bean.getOrderID()).append("/state");
        CancleRequest request = new CancleRequest();
        request.setState("deleted");
        sendConnection(urlSb.toString(), request, DELETE_ORDER, true, BaseEntity.ResultBean.class);
    }

    /**
     * 请求订单详情，拿商品信息然后再来一单
     */
    private void requestOrderDetailForOrderAgain(int orderId) {
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/order/");
        sb.append(orderId).append("/");
        sendConnection(sb.toString(), request, REQUEST_DETAIL_FOR_ORDER_AGAIN, true, OrderDetailResponse.class);
    }


    public class CarInfoListAdapter extends IBaseAdapter<OrderResponse.ListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            CarInfoListAdapter.ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_step_pay_new, null);
                holder = new CarInfoListAdapter.ViewHolder();
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (CarInfoListAdapter.ViewHolder) convertView.getTag();
            }
            final OrderResponse.ListBean bean = mList.get(position);
            holder.tvOneMore.setVisibility(View.GONE);
            //待确认
            if ("draft".equals(bean.getState())) {
                holder.payStatus.setText("待确认");
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.payBtn.setText("取消订单");
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                            return;
                        dialog.setMessage("您确定要取消订单吗?");
                        dialog.setModel(CustomDialog.BOTH);
                        dialog.setLeftBtnListener("不取消了", null);
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
            else if ("sale".equals(bean.getState())) {
                holder.payBtn.setVisibility(View.GONE);
                holder.payStatus.setText("已确认");
            }
            //已发货
            else if ("peisong".equals(bean.getState())) {
                holder.payStatus.setText("已发货");
                holder.payBtn.setVisibility(View.VISIBLE);
                String btnText;
                if (bean.isIsDoubleReceive()) {
                    if (bean.isIsFinishTallying()) {
                        //双人收货
                        btnText = "收货";
                    } else {
                        //双人点货
                        btnText = "点货";
                    }
                } else {
                    //正常收货
                    btnText = "收货";
                }
                holder.payBtn.setText(btnText);
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                            return;
                        //在这里做判断，是正常收货，还是双人收货,同时判断点货人是谁，如果是自己，则不能再收货
                        OrderDoAction action;
                        if (bean.isIsDoubleReceive()) {
                            String userName = GlobalApplication.getInstance().getUserName();
                            if (bean.getTallyingUserName().equals(userName)) {
                                action = OrderDoAction.SELFTALLY;
                            } else {
                                action = OrderDoAction.SETTLERECEIVE;
                            }
                        } else {
                            action = OrderDoAction.RECEIVE;
                        }
                        switch (action) {
                            case RECEIVE://正常收货
                                if (InventoryCacheManager.getInstance(getActivity()).checkIsInventory(getActivity()))
                                    return;
                                Intent intent = new Intent(mContext, ReceiveActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("order", bean);
                                bundle.putInt("mode", 0);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            case SETTLERECEIVE:
                                if (InventoryCacheManager.getInstance(getActivity()).checkIsInventory(getActivity()))
                                    return;
                                //点货，计入结算单位
                                Intent sIntent = new Intent(mContext, ReceiveActivity.class);
                                Bundle sBundle = new Bundle();
                                sBundle.putParcelable("order", bean);
                                sBundle.putInt("mode", 2);
                                sIntent.putExtras(sBundle);
                                startActivity(sIntent);
                                break;
                            case SELFTALLY:
                                if (InventoryCacheManager.getInstance(getActivity()).checkIsInventory(getActivity()))
                                    return;
                                dialog.setModel(CustomDialog.RIGHT);
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
            else if ("done".equals(bean.getState())) {
                //再来一单
                holder.tvOneMore.setVisibility(View.VISIBLE);
                holder.tvOneMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                            return;
                        requestOrderDetailForOrderAgain(bean.getOrderID());
                    }
                });
                holder.payStatus.setText("待评价");
                holder.payBtn.setVisibility(View.GONE);
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.payBtn.setText("评价");
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                            return;
                        Intent intent2 = new Intent(mContext, EvaluateActivity.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putParcelable("order", bean);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                    }
                });
            }
            //已评价
            else if ("rated".equals(bean.getState())) {
                //再来一单
                holder.tvOneMore.setVisibility(View.VISIBLE);
                holder.tvOneMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                            return;
//                        OrderAgainActivity.start(getActivity(),bean);
                        requestOrderDetailForOrderAgain(bean.getOrderID());
                    }
                });
                holder.payStatus.setText("已评价");
                holder.payBtn.setVisibility(View.GONE);
            }
            //已取消cancel
            else {
                holder.payStatus.setText("订单关闭");
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.payBtn.setText("删除订单");
                holder.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                            return;
                        dialog.setMessage("您确定要取消订单吗?");
                        dialog.setModel(CustomDialog.BOTH);
                        dialog.setLeftBtnListener("不删除了", null);
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
            holder.payTitle.setText(bean.getTime());
            holder.payDate.setText(bean.getName());

            StringBuffer descStringBuffer = new StringBuffer();

            if (bean.getState().equals(Constant.ORDER_STATE_DONE) || bean.getState().equals(Constant.ORDER_STATE_RATED)) {
                descStringBuffer.append(bean.getTypeQty()+"种 "+NumberUtil.getIOrD(bean.getDeliveredQty()) + "件商品");
            } else {
                descStringBuffer.append(bean.getTypeQty()+"种 "+NumberUtil.getIOrD(bean.getAmount()) + "件商品");
            }
            if (GlobalApplication.getInstance().getCanSeePrice()) {
                descStringBuffer.append(",¥" + NumberUtil.getIOrD(bean.getAmountTotal()));
            }
            holder.patSum.setText(descStringBuffer.toString());
            if (bean.getHasReturn() > 0) {
                holder.returnTv.setVisibility(View.VISIBLE);
            } else {
                holder.returnTv.setVisibility(View.GONE);
            }
            if ((Constant.ORDER_STATE_DONE.equals(bean.getState()) || Constant.ORDER_STATE_RATED.equals(bean.getState())) && bean.isActual()) {
                holder.realTv.setVisibility(View.VISIBLE);
            } else {
                holder.realTv.setVisibility(View.GONE);
            }
//            holder.payMoney.setText(bean.getAmountTotal()+"");
            return convertView;
        }

        SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTarget = new SimpleDateFormat("MM月dd日", Locale.getDefault());

        private String formatTimeStr(String str) {
            try {
                return sdfTarget.format(sdfSource.parse(str));
            } catch (ParseException e) {
                e.printStackTrace();
                return str;
            }
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
            @ViewInject(R.id.tv_one_more)
            TextView tvOneMore;
        }
    }
}
