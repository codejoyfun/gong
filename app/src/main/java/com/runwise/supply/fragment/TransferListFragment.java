package com.runwise.supply.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.TransferDetailActivity;
import com.runwise.supply.TransferInActivity;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.entity.TransferListResponse;
import com.runwise.supply.orderpage.TransferOutActivity;
import com.runwise.supply.tools.SystemUpgradeHelper;

import static com.runwise.supply.TransferDetailActivity.EXTRA_TRANSFER_ID;

/**
 * 调入调出fragment
 * <p>
 * Created by Dong on 2017/10/10.
 */

public class TransferListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener {

    public static final String ARG_KEY_TYPE = "type";
    public static final int TYPE_IN = 0;
    public static final int TYPE_OUT = 1;
    private static final int REQUEST_REFRESH = 0;
    private static final int REQUEST_MORE = 1;
    private static final int REQUEST_CANCEL_TRANSFER = 2;
    private static final int REQUEST_OUTPUT_CONFIRM = 3;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView mPullListView;
    private TransferListAdapter mTransferListAdapter;
    private int page = 1;
    int mType;

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_transfer_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPullListView.setPullToRefreshOverScrollEnabled(false);
        mPullListView.setScrollingWhileRefreshingEnabled(true);
        mPullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullListView.setOnItemClickListener(this);
        mTransferListAdapter = new TransferListAdapter();
        mPullListView.setAdapter(mTransferListAdapter);
        mPullListView.setOnRefreshListener(new PullToRefreshListener());
        //requestData(true, REQUEST_REFRESH, page, 10);
    }

    public void refresh() {
        requestData(true, REQUEST_REFRESH, 1, 10);
    }

    protected void requestData(boolean showDialog, int where, int page, int limit){
        mType = getArguments().getInt(ARG_KEY_TYPE);
        Object request = null;
        switch(mType){
            case TYPE_IN:
                sendConnection("/gongfu/shop/transfer/input_list", request, where, showDialog, TransferListResponse.class);
                break;
            case TYPE_OUT:
                sendConnection("/gongfu/shop/transfer/output_list", request, where, showDialog, TransferListResponse.class);
                break;
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_REFRESH:
            case REQUEST_MORE://接口无分页
                TransferListResponse response = (TransferListResponse) result.getResult().getData();
                mTransferListAdapter.setData(response.getList());
                if (mTransferListAdapter.getCount() == 0 && mPullListView.getRefreshableView().getHeaderViewsCount() == 1) {
                    mLoadingLayout.onSuccess(0, "哎呀！这里是空哒~~",R.drawable.default_ico_none);
                } else {
                    mLoadingLayout.onSuccess(mTransferListAdapter.getCount(), "哎呀！这里是空哒~~",R.drawable.default_ico_none);
                }
                mPullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_CANCEL_TRANSFER:
                //refresh
                page = 1;
                requestData(false, REQUEST_REFRESH, page, 10);
                break;
            case REQUEST_OUTPUT_CONFIRM:
                startActivity(TransferOutActivity.getStartIntent(getActivity(),mSelectTransferEntity));
                mInTheRequest = false;
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch(where){
            case REQUEST_OUTPUT_CONFIRM:
                mInTheRequest = false;
                ToastUtil.show(getActivity(),errMsg);
                return;
        }
        mLoadingLayout.onFailure(errMsg,R.drawable.nonocitify_icon);
        mLoadingLayout.setOnRetryClickListener(new LoadingLayoutInterface() {
            @Override
            public void retryOnClick(View view) {
                requestData(false, REQUEST_REFRESH, page, 10);
            }
        });
    }

    /**
     * 调度单列表项点击
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        //去详情页
        if (id == -1) {
            // 点击的是headerView或者footerView
            return;
        }
        int realPosition = (int) id;
        TransferEntity transferEntity = (TransferEntity) mTransferListAdapter.getItem(realPosition);
        Intent intent = new Intent(getActivity(), TransferDetailActivity.class);
        intent.putExtra(EXTRA_TRANSFER_ID, transferEntity.getPickingID());
        startActivity(intent);
    }
    TransferEntity mSelectTransferEntity;
    boolean mInTheRequest  = false;
    private void requestOutputConfirm(TransferEntity transferEntity) {
        if(mInTheRequest){
            return;
        }
        mInTheRequest = true;
        mSelectTransferEntity = transferEntity;
        Object request = null;
        sendConnection("/gongfu/shop/transfer/output_confirm/" + transferEntity.getPickingID(), request, REQUEST_OUTPUT_CONFIRM, true, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 列表adapter
     */
    private class TransferListAdapter extends IBaseAdapter<TransferEntity> {

        @Override
        protected View getExView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_transfer_list, null);
                viewHolder = new ViewHolder();
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
                viewHolder.mmIvIcon.setImageResource(mType==TYPE_IN?R.drawable.state_delivery_8_callin:R.drawable.state_delivery_8_callout);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final TransferEntity transferEntity = mList.get(position);
            viewHolder.mmTvTitle.setText(transferEntity.getPickingName());
            viewHolder.mmTvCreateTime.setText(transferEntity.getDate());
            viewHolder.mmTvLocations.setText(transferEntity.getLocationName() + "\u2192" + transferEntity.getLocationDestName());
            viewHolder.mmTvPrice.setText(transferEntity.getTotalPrice() + "元，" + transferEntity.getTotalNum() + "件商品");
            viewHolder.mmTvAction.setVisibility(View.VISIBLE);

            viewHolder.mmTvStatus.setText(transferEntity.getPickingState());

            if(mType==TYPE_IN){//入库方
                setTransferInViewHolder(viewHolder,transferEntity);
            }
            else if(mType==TYPE_OUT){//出库方
                setTransferOutViewHolder(viewHolder,transferEntity,position);
            }

//            if (STATE_SUBMITTED.equals(transferEntity.getPickingState()) ||
//                    STATE_INSUFFICIENT.equals(transferEntity.getPickingState())) {//已提交
//                viewHolder.mmTvAction.setText("取消");
//                viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //取消
//                        if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
//                        dialog.setTitle("提示");
//                        dialog.setMessage("确认取消订单?");
//                        dialog.setMessageGravity();
//                        dialog.setModel(CustomDialog.BOTH);
//                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
//                            @Override
//                            public void doClickButton(Button btn, CustomDialog dialog) {
//                                //发送取消订单请求
//                                requestCancel(transferEntity);
//                            }
//                        });
//                        dialog.show();
//                    }
//                });
//            }else if(STATE_DELIVER.equals(transferEntity.getPickingState()) ||
//                    STATE_DELIVER2.equals(transferEntity.getPickingState())){//已发出
//                if(mType==TYPE_IN)viewHolder.mmTvAction.setText("入库");
//                else viewHolder.mmTvAction.setVisibility(View.GONE);
//                viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
//                        Intent intent = new Intent(getActivity(), TransferInActivity.class);
//                        intent.putExtra(TransferInActivity.INTENT_KEY_TRANSFER_ENTITY, transferEntity);
//                        startActivity(intent);
//                    }
//                });
//            }else if(STATE_PENDING_DELIVER.equals(transferEntity.getPickingState())){//待出库
//                if(mType==TYPE_OUT)viewHolder.mmTvAction.setText("出库");
//                else viewHolder.mmTvAction.setVisibility(View.GONE);
//                //防止错位
//                viewHolder.mmTvAction.setTag(position);
//                viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
//                        int realPosition = (int) view.getTag();
//                        if (realPosition == position) {
//                            //变成可用状态
//                            requestOutputConfirm(transferEntity);
//                        }
//                    }
//                });
//            }else if(STATE_CANCEL.equals(transferEntity.getPickingState())){
//                viewHolder.mmTvAction.setVisibility(View.GONE);
//            }
//            else{
//                viewHolder.mmTvAction.setVisibility(View.GONE);
//            }

            return convertView;
        }

        /**
         * 入库方的列表项
         * @param viewHolder
         * @param transferEntity
         */
        void setTransferInViewHolder(ViewHolder viewHolder,final TransferEntity transferEntity){
            switch (transferEntity.getPickingStateNum()){
                case TransferEntity.STATE_SUBMIT://已提交，可取消
                    viewHolder.mmTvAction.setVisibility(View.VISIBLE);
                    viewHolder.mmTvAction.setText("取消");
                    viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //取消
                            if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
                            dialog.setTitle("提示");
                            dialog.setMessage("确认取消订单?");
                            dialog.setMessageGravity();
                            dialog.setModel(CustomDialog.BOTH);
                            dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                                @Override
                                public void doClickButton(Button btn, CustomDialog dialog) {
                                    //发送取消订单请求
                                    requestCancel(transferEntity);
                                }
                            });
                            dialog.show();
                        }
                    });
                    break;
                case TransferEntity.STATE_OUT:
                    viewHolder.mmTvAction.setVisibility(View.VISIBLE);
                    viewHolder.mmTvAction.setText("入库");
                    viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
                            Intent intent = new Intent(getActivity(), TransferInActivity.class);
                            intent.putExtra(TransferInActivity.INTENT_KEY_TRANSFER_ENTITY, transferEntity);
                            startActivity(intent);
                        }
                    });
                    break;
                default:
                    viewHolder.mmTvAction.setVisibility(View.GONE);
            }
        }

        /**
         * 出库方列表项
         * @param viewHolder
         * @param transferEntity
         * @param position
         */
        void setTransferOutViewHolder(ViewHolder viewHolder,final TransferEntity transferEntity,final int position){
            switch (transferEntity.getPickingStateNum()){
                case TransferEntity.STATE_SUBMIT://已提交，可出库
                    viewHolder.mmTvAction.setVisibility(View.VISIBLE);
                    viewHolder.mmTvAction.setText("接单");
                    //防止错位
                    viewHolder.mmTvAction.setTag(position);
                    viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
                            int realPosition = (int) view.getTag();
                            if (realPosition == position) {
                                //变成可用状态
                                requestOutputConfirm(transferEntity);
                            }
                        }
                    });
                    break;
                default:
                    viewHolder.mmTvAction.setVisibility(View.GONE);
            }
        }

        class ViewHolder {
            @ViewInject(R.id.iv_transfer_status)
            ImageView mmIvIcon;
            @ViewInject(R.id.item_transfer_title_tv)
            TextView mmTvTitle;
            @ViewInject(R.id.tv_item_transfer_status)
            TextView mmTvStatus;
            @ViewInject(R.id.tv_item_transfer_action)
            TextView mmTvAction;
            @ViewInject(R.id.tv_item_transfer_locations)
            TextView mmTvLocations;
            @ViewInject(R.id.tv_item_transfer_price)
            TextView mmTvPrice;
            @ViewInject(R.id.tv_item_transfer_date)
            TextView mmTvCreateTime;
        }
    }

    /**
     * 下拉监听
     */
    private class PullToRefreshListener implements PullToRefreshBase.OnRefreshListener2<ListView> {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page = 1;
            requestData(false, REQUEST_REFRESH, page, 10);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            //无分页
            requestData(false, REQUEST_MORE, (++page), 10);
        }
    }

    /**
     * 取消调拨单
     */
    private void requestCancel(TransferEntity transferEntity) {
        Object request = null;
        sendConnection("/gongfu/shop/transfer/cancel/" + transferEntity.getPickingID(), request, REQUEST_CANCEL_TRANSFER, true, null);
    }
}
