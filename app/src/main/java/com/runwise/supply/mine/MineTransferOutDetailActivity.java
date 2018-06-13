package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderSubmitProductAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MineTransferOutDetailActivity extends NetWorkActivity {

    @BindView(R.id.tv_num)
    TextView mTvNum;
    @BindView(R.id.tv_num_value)
    TextView mTvNumValue;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_time_value)
    TextView mTvTimeValue;
    @BindView(R.id.tv_operator)
    TextView mTvOperator;
    @BindView(R.id.tv_operator_value)
    TextView mTvOperatorValue;
    @BindView(R.id.tv_product)
    TextView mTvProduct;
    @BindView(R.id.tv_product_value)
    TextView mTvProductValue;
    @BindView(R.id.rl_info)
    RelativeLayout mRlInfo;
    @BindView(R.id.rv_product_list)
    RecyclerView  mRvProductList;
    @BindView(R.id.rl_content)
    RelativeLayout mRlContent;
    MineTransferOutDetailAdapter mMineTransferOutDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_transfer_out_detail);
        ButterKnife.bind(this);

        setTitleText(true,"出库单详情");
        showBackBtn();

        mRvProductList.setLayoutManager(new LinearLayoutManager(mContext));
        mMineTransferOutDetailAdapter = new MineTransferOutDetailAdapter();
        mRvProductList.setAdapter(mMineTransferOutDetailAdapter);

    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }


    public class MineTransferOutDetailAdapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_mine_transfer_detail,null);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdv_product)
        SimpleDraweeView mSdvProduct;
        @BindView(R.id.tv_product_name)
        TextView mTvProductName;
        @BindView(R.id.tv_sales_promotion)
        TextView mTvSalesPromotion;
        @BindView(R.id.tv_product_price)
        TextView mTvProductPrice;
        @BindView(R.id.tv_product_count)
        TextView mTvProductCount;
        @BindView(R.id.tv_product_unit)
        TextView mTvProductUnit;
        @BindView(R.id.tv_item_order_submit_remark)
        TextView mTvRemark;

        @BindView(R.id.stick_header)
        View mVStickHeader;
        @BindView(R.id.tv_header)
        TextView mTvHeader;
        @BindView(R.id.product_main)
        View mVProductMain;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
