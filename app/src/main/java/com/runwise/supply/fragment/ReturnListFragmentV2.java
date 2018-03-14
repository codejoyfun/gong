package com.runwise.supply.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderStateAdapter;
import com.runwise.supply.adapter.OrderTimeAdapter;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.entity.ReturnActivityRefreshEvent;
import com.runwise.supply.firstpage.ReturnDetailActivity;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.firstpage.entity.ReturnResponse;
import com.runwise.supply.mine.OrderDataType;
import com.runwise.supply.tools.InventoryCacheManager;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.OrderDateSelectDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Unbinder;
import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean.TYPE_VENDOR_DELIVERY;

/**
 * Created by mike on 2018/3/11.
 */

public class ReturnListFragmentV2 extends NetWorkFragment implements AdapterView.OnItemClickListener, LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int PRODUCT_GET = 4;
    private static final int REQUEST_CANCEL_RETURN_ORDER = 5;
    private static final int REQUEST_FINISHRETURN = 6;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private CarInfoListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;


    @ViewInject(R.id.tv_order_state)
    TextView mTvOrderState;
    @ViewInject(R.id.tv_order_time)
    TextView mTvOrderTime;
    @ViewInject(R.id.ll_tab)
    LinearLayout mLlTab;
    @ViewInject(R.id.lv_order_state)
    ListView mLvOrderState;
    @ViewInject(R.id.lv_order_time)
    ListView mLvOrderTime;
    Unbinder unbinder;
    @ViewInject(R.id.rl_order_state)
    View mRlOrderState;
    @ViewInject(R.id.rl_order_time)
    View mRlOrderTime;
    @ViewInject(R.id.iv_order_time)
    View mIvOrderTime;
    @ViewInject(R.id.iv_order_state)
    View mIvOrderState;

    private int page = 1;
    public OrderDataType orderDataType;
    private List<ReturnOrderBean.ListBean> allList;
    OrderDateSelectDialog mOrderDateSelectDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRlOrderState.setVisibility(View.GONE);
        mRlOrderTime.setVisibility(View.GONE);

        OrderStateAdapter orderStateAdapter = new OrderStateAdapter();

        String[] mTitles = new String[]{"全部状态", "待审核", "退货中", "已退货"};
        orderStateAdapter.setTitles(mTitles);

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
                requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
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
                if (position != orderTimeAdapter.getCount() - 1) {
                    mTvOrderTime.setText(orderTimeAdapter.getItem(position).state);
                }
                mIvOrderTime.setRotation(0);
                setTime(orderTimeAdapter.getItem(position).state);

            }
        });

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        //pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setOnItemClickListener(this);

        adapter = new CarInfoListAdapter();

        if (mOnRefreshListener2 == null) {
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    page = 1;
                    requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, mState, REQUEST_DEN, page, mStartTime, mEndTime);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        requestData(false, mState, REQUEST_MAIN, page, mStartTime, mEndTime);
        loadingLayout.setStatusLoading();
        loadingLayout.setOnRetryClickListener(this);
        mOrderDateSelectDialog = new OrderDateSelectDialog(getActivity());
        mOrderDateSelectDialog.setPickerClickListener(new OrderDateSelectDialog.PickerClickListener() {
            @Override
            public void doPickClick(String startYMD, String endYMD) {
                mTvOrderTime.setText(startYMD + "-" + endYMD);
                String[] startYMDArray = startYMD.split("/");
                String[] endYMDArray = endYMD.split("/");
                mStartTime = startYMDArray[0] + "-" + startYMDArray[1] + "-" + startYMDArray[2];
                mEndTime = endYMDArray[0] + "-" + endYMDArray[1] + "-" + endYMDArray[2];
                requestData(true, "", REQUEST_START, page, mStartTime, mEndTime);
                mOrderDateSelectDialog.dismiss();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ReturnActivityRefreshEvent returnActivityRefreshEvent) {
        requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
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
            case "待审核":
                return "draft";
            case "退货中":
                return "process";
            case "已退货":
                return "done";
        }
        return "";
    }

    private void setTime(String timeDesc) {
        switch (timeDesc) {
            case "全部时间":
                mStartTime = "";
                mEndTime = "";
                requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
                break;
            case "本周":
                mStartTime = TimeUtils.getThisWeekStart();
                mEndTime = TimeUtils.getThisWeekEnd();
                requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
                break;
            case "上周":
                mStartTime = TimeUtils.getPerWeekStart();
                mEndTime = TimeUtils.getPerWeekEnd();
                requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
                break;
            case "自定义区间":
                mOrderDateSelectDialog.show();
                break;
        }

    }


    String mStartTime = "";
    String mEndTime = "";
    String mState = "";

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
                mIvOrderState.setRotation(0);
                break;
            case R.id.rl_order_time:
                mRlOrderTime.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                mRlOrderTime.setVisibility(View.GONE);
                mIvOrderTime.setRotation(0);
                break;
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDataSynEvent(ReturnData event) {
//        switch (orderDataType) {
//            case BENZHOU:
//                allList = event.getThisWeekList();
//                adapter.setData(allList);
//                break;
//            case SHANGZHOU:
//                allList = event.getLastWeekList();
//                adapter.setData(allList);
//                break;
//            case GENGZAO:
//                allList = event.getEarlierList();
//                adapter.setData(allList);
//                break;
//            case ALL:
//                allList = event.getAllList();
//                adapter.setData(allList);
//                break;
//        }
//        pullListView.onRefreshComplete(adapter.getCount());
//        loadingLayout.onSuccess(adapter.getCount(),"哎呀！这里是空哒~~",R.drawable.default_ico_none);
//    }


    public void requestData(boolean showDialog, String state, int where, int page, String startTime, String endTime) {
        if (where == REQUEST_MAIN||where == REQUEST_START){
            page = 1;
        }
        PageRequest request = new PageRequest();
        request.setLimit(10);
        request.setPz(page);
        request.setState(state);
        request.setStart(startTime);
        request.setEnd(endTime);
        request.setIsReturn(true);
        sendConnection("/api/order/list", request, where, showDialog, ReturnResponse.class);
    }

    private void cancelReturnOrder(int returnOrderId) {
        String url = "/api/return_order/" + returnOrderId + "/cancel";
        Object obj = null;
        sendConnection(url, obj, REQUEST_CANCEL_RETURN_ORDER, true, BaseEntity.ResultBean.class);
    }

    private void finishReturn(int returnOrderId) {
        Object request = null;
        sendConnection("/gongfu/v2/return_order/" +
                returnOrderId +
                "/done", request, REQUEST_FINISHRETURN, true, FinishReturnResponse.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        ReturnResponse repertoryEntity;
        switch (where) {
            case REQUEST_MAIN:
                repertoryEntity = (ReturnResponse) result.getResult().getData();
                allList = repertoryEntity.getList();
                adapter.setData(allList);
                loadingLayout.onSuccess(allList.size(), "哎呀！这里是空哒~~", R.drawable.default_icon_ordernone);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                repertoryEntity = (ReturnResponse) result.getResult().getData();
                allList = repertoryEntity.getList();
                adapter.setData(allList);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                loadingLayout.onSuccess(allList.size(), "哎呀！这里是空哒~~", R.drawable.default_icon_ordernone);
                break;
            case REQUEST_DEN:
                repertoryEntity = (ReturnResponse) result.getResult().getData();
                allList = repertoryEntity.getList();
                if (allList != null && allList.size() > 0) {
                    adapter.appendData(allList);
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                } else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case REQUEST_CANCEL_RETURN_ORDER:
                requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
                ToastUtil.show(mContext, "取消申请退货成功");
                break;
            case REQUEST_FINISHRETURN:
                requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
                ToastUtil.show(mContext, "退货成功");
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext, errMsg);
//        loadingLayout.onFailure("",R.drawable.no_network);
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(mContext, ReturnDetailActivity.class);
        intent.putExtra("rid", bean.getReturnOrderID() + "");
        startActivity(intent);
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_order_fragment_v2;
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, mState, REQUEST_START, page, mStartTime, mEndTime);
    }


    public class CarInfoListAdapter extends IBaseAdapter<ReturnOrderBean.ListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            CarInfoListAdapter.ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_return_list, null);
                holder = new CarInfoListAdapter.ViewHolder();
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (CarInfoListAdapter.ViewHolder) convertView.getTag();
            }
            ReturnOrderBean.ListBean bean = mList.get(position);
            holder.payBtn.setVisibility(View.GONE);
            switch (bean.getState()){
                case "process":
                    holder.payStatus.setText("退货中");
                    if (bean.getDeliveryType().equals(TYPE_VENDOR_DELIVERY)) {
                        holder.payBtn.setVisibility(View.VISIBLE);
                        holder.payBtn.setText("完成退货");
                        holder.payBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                                    return;
                                if (InventoryCacheManager.getInstance(getActivity()).checkIsInventory(getActivity()))
                                    return;
                                dialog.setMessage("确认数量一致?");
                                dialog.setModel(CustomDialog.BOTH);
                                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                                    @Override
                                    public void doClickButton(Button btn, CustomDialog dialog) {
                                        finishReturn(bean.getReturnOrderID());
                                    }
                                });
                                dialog.show();

                            }
                        });
                    } else {
                        holder.payBtn.setVisibility(View.GONE);
                    }
                    break;
                case "done":
                    holder.payStatus.setText("已退货");
                    break;
                case "draft":
                    holder.payStatus.setText("待审核");
                    holder.payBtn.setVisibility(View.VISIBLE);
                    holder.payBtn.setText("取消申请");
                    holder.payBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))
                                return;
                            dialog.setMessage("您确定要取消申请吗?");
                            dialog.setModel(CustomDialog.BOTH);
                            dialog.setLeftBtnListener("不取消了", null);
                            dialog.setRightBtnListener("取消申请", new CustomDialog.DialogListener() {
                                @Override
                                public void doClickButton(Button btn, CustomDialog dialog) {
                                    cancelReturnOrder(bean.getReturnOrderID());
                                }
                            });
                            dialog.show();

                        }
                    });
                    break;
            }

            holder.payTitle.setText(bean.getTime());
            holder.payDate.setText(bean.getName());
            StringBuffer descStringBuffer = new StringBuffer();
            descStringBuffer.append(bean.getTypeQty() + "种 " + NumberUtil.getIOrD(bean.getAmount()) + "件商品");
            if (GlobalApplication.getInstance().getCanSeePrice()) {
                descStringBuffer.append(",¥" + bean.getAmountTotal());
            }
            holder.patSum.setText(descStringBuffer.toString());
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
            @ViewInject(R.id.payBtn)
            TextView payBtn;
            @ViewInject(R.id.orderStatus)
            ImageView orderStatus;
        }

    }
}
