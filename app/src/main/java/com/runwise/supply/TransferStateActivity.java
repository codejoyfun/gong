package com.runwise.supply;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.entity.TransferStateEntitiy;
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
    private List<TransferStateEntitiy> mStateList;
    @ViewInject(R.id.rv_transfer_state)
    private RecyclerView mRvStates;
    private StateAdapter mStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_state);
        setTitleLeftIcon(true,R.drawable.nav_back);
        mStateAdapter = new StateAdapter(this);
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER);
        mRvStates.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRvStates.setAdapter(mStateAdapter);
        requestStates();
    }

    @OnClick({R.id.title_iv_left})
    private void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
        }
    }

    private void requestStates(){
        sendConnection("/gongfu/shop/transfer/state_list/"+mTransferEntity.getPickingID(),REQUEST_STATE,true,TransferStateResponse.class);
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

    class StateAdapter extends RecyclerView.Adapter {
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
            TransferStateEntitiy osl = mStateList.get(position);
            //TODO:
//            itemHolder.orderStateTv.setText(osl.getState());
//            itemHolder.orderContentTv.setText(osl.getContent());
//            itemHolder.stateTimeTv.setText(osl.getTime());
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
