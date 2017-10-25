package com.runwise.supply;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.entity.TransferStateEntity;
import com.runwise.supply.entity.TransferStateResponse;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 调拨单状态
 *
 * Created by Dong on 2017/10/14.
 */

public class TransferStateActivity extends NetWorkActivity {

    public static final String INTENT_KEY_TRANSFER = "transfer_entity";
    private static final int REQUEST_STATE = 0;

    private TransferEntity mTransferEntity;
    private List<TransferStateEntity> mStateList;
    @ViewInject(R.id.rv_transfer_state)
    private RecyclerView mRvStates;
    private StateAdapter mStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_state);
        showBackBtn();
        setTitleText(true,"调拨单状态");
        setTitleLeftIcon(true,R.drawable.nav_back);
        mStateAdapter = new StateAdapter(this);
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER);
        mRvStates.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRvStates.setAdapter(mStateAdapter);
        //requestStates();
        initStates();
    }

    private void initStates(){
        if(mTransferEntity.getStateTracker()!=null){
            mStateList = new ArrayList<>();
            for(String strState:mTransferEntity.getStateTracker()){
                TransferStateEntity stateEntity = new TransferStateEntity();
                String[] pieces = strState.split(" ");
                String state = pieces[2];
                String stateTime = pieces[0]+" "+pieces[1];
                StringBuilder sbContent = new StringBuilder();
                stateEntity.setOperateTime(stateTime);
                stateEntity.setState(state);
                stateEntity.setOperator(pieces[pieces.length-1]);
                if(state.contains(TransferEntity.STATE_SUBMITTED)){//已提交
                    sbContent.append("操作人：").append(stateEntity.getOperator()).append("\n")
                            .append("调拨单号：").append(mTransferEntity.getPickingName()).append("\n")
                            .append("调拨商品：").append(pieces[3]);
                }
                else if(state.contains(TransferEntity.STATE_MODIFIED)){//已修改
                    sbContent.append("操作人：").append(stateEntity.getOperator()).append("\n")
                            .append("调拨商品：").append(pieces[3]);
                }
                else if(state.contains(TransferEntity.STATE_DELIVER)){//已发出
                    sbContent.append("操作人：").append(stateEntity.getOperator()).append("\n")
                            .append("调拨路径：").append(mTransferEntity.getLocationName()).append("\u2192")
                            .append(mTransferEntity.getLocationDestName());
                }else if(state.contains(TransferEntity.STATE_COMPLETE)){//已完成
                    sbContent.append("入库人：").append(stateEntity.getOperator()).append("\n")
                            .append("收货商品：").append(pieces[3]);
                }else{
                    sbContent.append("操作人：").append(stateEntity.getOperator()).append("\n")
                            .append("调拨单号：").append(mTransferEntity.getPickingName()).append("\n")
                            .append("调拨商品：").append(pieces[3]);
                }
                stateEntity.setContent(sbContent.toString());
                mStateList.add(stateEntity);
            }
            mStateAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_STATE:
                TransferStateResponse response = (TransferStateResponse) result.getResult().getData();
                mStateList = response.getStateList();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private class StateAdapter extends RecyclerView.Adapter {
        private LayoutInflater inflater;
        private static final int TYPE_TOP = 0x0000;
        private static final int TYPE_NORMAL= 0x0001;

        public StateAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.order_state_line_item,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder itemHolder = (ViewHolder) holder;
            if (getItemViewType(position) == TYPE_TOP) {
                // 第一行头的竖线不显示
                itemHolder.tvTopLine.setVisibility(View.INVISIBLE);
                // 字体颜色加深
                itemHolder.orderStateTv.setTextColor(0xff6BB400);
                itemHolder.tvDot.setBackgroundResource(R.drawable.restaurant_orderstatus_point_highlight);
            } else if (getItemViewType(position) == TYPE_NORMAL) {
                itemHolder.tvTopLine.setVisibility(View.VISIBLE);
                itemHolder.orderStateTv.setTextColor(0xff2E2E2E);
                itemHolder.tvDot.setBackgroundResource(R.drawable.timeline_dot_normal);
            }
            if (position == mStateList.size() - 1){
                itemHolder.tvDownLine.setVisibility(View.INVISIBLE);
            }
            TransferStateEntity osl = mStateList.get(position);
            itemHolder.orderStateTv.setText(osl.getState());
            itemHolder.orderContentTv.setText(osl.getContent());
            itemHolder.stateTimeTv.setText(osl.getOperateTime());
        }
        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_TOP;
            }
            return TYPE_NORMAL;
        }

        @Override
        public int getItemCount() {
            return mStateList==null?0:mStateList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView orderStateTv, orderContentTv;
            public TextView stateTimeTv;
            public TextView tvTopLine, tvDot;
            public TextView tvDownLine;
            public ViewHolder(View itemView) {
                super(itemView);
                orderStateTv = (TextView) itemView.findViewById(R.id.orderStateTv);
                orderContentTv = (TextView) itemView.findViewById(R.id.orderContentTv);
                stateTimeTv = (TextView)itemView.findViewById(R.id.stateTimeTv);
                tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
                tvDot = (TextView) itemView.findViewById(R.id.tvDot);
                tvDownLine = (TextView)itemView.findViewById(R.id.tvDownLine);
            }
        }
    }
}
