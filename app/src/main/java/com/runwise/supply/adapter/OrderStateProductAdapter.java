package com.runwise.supply.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/1/16.
 */

public class OrderStateProductAdapter extends RecyclerView.Adapter {
    List<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> mAlterProductList;


    public OrderStateProductAdapter(List<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> alterProducts) {
        mAlterProductList = alterProducts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status_product, null);
        OrderStateProductViewHolder orderStateProductViewHolder = new OrderStateProductViewHolder(itemView);
        return orderStateProductViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderStateProductViewHolder orderStateProductViewHolder = (OrderStateProductViewHolder) holder;
        OrderResponse.ListBean.ProductAlteredBean.AlterProductBean alterProductBean = mAlterProductList.get(position);
        orderStateProductViewHolder.mTvName.setText(alterProductBean.getName());
        orderStateProductViewHolder.mTvContent.setText(alterProductBean.getDefaultCode() + " " + alterProductBean.getUnit());
        orderStateProductViewHolder.mTvOldProductCount.setText("x" + alterProductBean.getOriginNum());
        orderStateProductViewHolder.mTvOldProductCount.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        orderStateProductViewHolder.mTvNewProductCount.setText(NumberUtil.getIOrD(alterProductBean.getAlterNum())+alterProductBean.getUom());
    }

    @Override
    public int getItemCount() {
        return mAlterProductList.size();
    }

    static class OrderStateProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_content)
        TextView mTvContent;
        @BindView(R.id.tv_old_product_count)
        TextView mTvOldProductCount;
        @BindView(R.id.tv_new_product_count)
        TextView mTvNewProductCount;
        @BindView(R.id.countLL)
        LinearLayout mCountLL;

        OrderStateProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
