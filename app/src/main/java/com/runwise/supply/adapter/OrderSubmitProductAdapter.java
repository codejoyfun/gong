package com.runwise.supply.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.UserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2017/11/23.
 */

public class OrderSubmitProductAdapter extends RecyclerView.Adapter<OrderSubmitProductAdapter.ViewHolder>{

    List<ProductData.ListBean> mListBeanList;

    public OrderSubmitProductAdapter(List<ProductData.ListBean> listBeanList){
        mListBeanList = listBeanList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_order_submit,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductData.ListBean listBean = mListBeanList.get(position);
        if (listBean.getImage() != null){
            FrecoFactory.getInstance(holder.itemView.getContext()).disPlay(holder.mSdvProduct, Constant.BASE_URL+listBean.getImage().getImageSmall());
        }
        holder.mTvProductCount.setText(String.valueOf(listBean.getActualQty()));
        holder.mTvProductName.setText(listBean.getName());
        holder.mTvProductUnit.setText(listBean.getProductUom());
        holder.mTvProductPrice.setText("¥"+ NumberUtil.getIOrD(listBean.getPrice())+"/"+listBean.getProductUom()+" | "+listBean.getUnit());
    }

    @Override
    public int getItemCount() {
        return mListBeanList.size();
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
