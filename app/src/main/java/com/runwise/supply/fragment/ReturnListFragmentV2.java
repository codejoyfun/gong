package com.runwise.supply.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderStateAdapter;
import com.runwise.supply.adapter.OrderTimeAdapter;
import com.runwise.supply.entity.ReturnActivityRefreshEvent;
import com.runwise.supply.firstpage.ReturnDetailActivity;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.mine.OrderDataType;
import com.runwise.supply.mine.entity.MsgList;
import com.runwise.supply.mine.entity.MsgResult;
import com.runwise.supply.mine.entity.ReturnData;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.CustomDatePickerDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Unbinder;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/3/11.
 */

public class ReturnListFragmentV2 extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int PRODUCT_GET = 4;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRlOrderState.setVisibility(View.GONE);
        mRlOrderTime.setVisibility(View.GONE);

        OrderStateAdapter orderStateAdapter = new OrderStateAdapter();

        String[] mTitles = new String[]{"全部状态", "待确认","退货中", "已完成"};
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
                mTvOrderTime.setText(orderTimeAdapter.getItem(position).state);
                mIvOrderTime.setRotation(0);
                setTime(orderTimeAdapter.getItem(position).state);
            }
        });

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        //pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ReturnActivityRefreshEvent returnActivityRefreshEvent) {
        Object param = null;
        sendConnection("/API/v2/return_order/list", param, PRODUCT_GET, true, ReturnData.class);
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
            case "退货中":
                return "process";
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
                break;
            case "本周":
                mStartTime = TimeUtils.getThisWeekStart();
                mEndTime = TimeUtils.getThisWeekEnd();
                break;
            case "上周":
                mStartTime = TimeUtils.getPerWeekStart();
                mEndTime = TimeUtils.getPerWeekEnd();
                break;
            case "自定义区间":
                CustomDatePickerDialog customDatePickerDialog = new CustomDatePickerDialog(getActivity());
                customDatePickerDialog.show();
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
                break;
            case R.id.rl_order_time:
                mRlOrderTime.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top_200));
                mRlOrderTime.setVisibility(View.GONE);
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


    public void requestData (boolean showDialog,int where, int page,int limit) {
//        PageRequest request = new PageRequest();
//        request.setLimit(limit);
//        request.setPz(page);
//        sendConnection("message/list.json",request,where,showDialog,MsgResult.class);
        Object param = null;
        sendConnection("/API/v2/return_order/list",param,PRODUCT_GET,false, ReturnData.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                MsgResult mainResult = (MsgResult)result;
                MsgList mainListResult = mainResult.getData();
//                adapter.setData(mainListResult.getEntities());
                loadingLayout.onSuccess(adapter.getCount(),"",R.drawable.news);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                MsgResult startResult = (MsgResult) result;
                MsgList startListResult = startResult.getData();
//                adapter.setData(startListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                MsgResult endResult = (MsgResult) result;
                MsgList sndListResult = endResult.getData();
                if (sndListResult.getEntities() != null && !sndListResult.getEntities().isEmpty()) {
//                    adapter.appendData(sndListResult.getEntities());
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case PRODUCT_GET:
                ReturnData repertoryEntity = (ReturnData)result.getResult().getData();
                allList = repertoryEntity.getAllList();
                adapter.setData(allList);
                loadingLayout.onSuccess(allList.size(), "哎呀！这里是空哒~~", R.drawable.default_icon_ordernone);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
//        loadingLayout.onFailure("",R.drawable.no_network);
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean)parent.getAdapter().getItem(position);
        Intent intent = new Intent(mContext,ReturnDetailActivity.class);
        intent.putExtra("rid",bean.getReturnOrderID()+"");
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
        requestData(false, REQUEST_MAIN, page, 10);
    }


    public class CarInfoListAdapter extends IBaseAdapter<ReturnOrderBean.ListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            CarInfoListAdapter.ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_return_list, null);
                holder = new CarInfoListAdapter.ViewHolder();
                ViewUtils.inject(holder,convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (CarInfoListAdapter.ViewHolder) convertView.getTag();
            }
            ReturnOrderBean.ListBean bean = mList.get(position);

            if("process".equals(bean.getState())) {
                holder.payStatus.setText("退货中");
                holder.orderStatus.setImageResource(R.drawable.state_delivery_7_return);
            }
            //已发货
            else if("done".equals(bean.getState())) {
                holder.payStatus.setText("已退货");
                holder.orderStatus.setImageResource(R.drawable.state_delivery_7_return);
            }
            else  {
                holder.payStatus.setText("已退货");
                holder.orderStatus.setImageResource(R.drawable.state_delivery_7_return);
            }

            holder.payTitle.setText(bean.getName());
            holder.payDate.setText(TimeUtils.getTimeStamps3(bean.getCreateDate()));
            holder.patSum.setText("共"+ NumberUtil.getIOrD(bean.getAmount())+"件商品");
            if(GlobalApplication.getInstance().getCanSeePrice()) {
                holder.payMoney.setVisibility(View.VISIBLE);
                holder.payMoney.setText("共"+bean.getAmountTotal());
            }
            else {
                holder.payMoney.setVisibility(View.GONE);
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
            @ViewInject(R.id.orderStatus)
            ImageView orderStatus;
        }

    }
}
