package com.runwise.supply.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2017/11/23.
 */

public class OrderSubmitProductAdapter extends RecyclerView.Adapter<OrderSubmitProductAdapter.ViewHolder>{

    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    List<ProductBasicList.ListBean> mListBeanList;
    private boolean canSeePrice = false;

    public OrderSubmitProductAdapter(List<ProductBasicList.ListBean> listBeanList){
        mListBeanList = listBeanList;
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_order_submit,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductBasicList.ListBean listBean = mListBeanList.get(position);
            FrecoFactory.getInstance(holder.itemView.getContext()).displayWithoutHost(holder.mSdvProduct, listBean.getImageLocal());
        holder.mTvProductCount.setText(NumberUtil.getIOrD(listBean.getActualQty()));
        holder.mTvProductName.setText(listBean.getName());
        holder.mTvProductUnit.setText(listBean.getSaleUom());
        holder.mTvRemark.setText(listBean.getRemark()==null?"":"备注："+listBean.getRemark());
        StringBuilder sb = new StringBuilder();
        if(canSeePrice)sb.append("¥").append(NumberUtil.getIOrD(listBean.getPrice())).append("/").append(listBean.getSaleUom()).append(" | ");
        sb.append(listBean.getUnit());
        holder.mTvProductPrice.setText(sb.toString());
        if(TextUtils.isEmpty(listBean.getProductTag())){
            holder.mTvSalesPromotion.setVisibility(View.GONE);
        }else{
            holder.mTvSalesPromotion.setVisibility(View.VISIBLE);
        }


        if (position == 0) {
            holder.mVStickHeader.setVisibility(View.VISIBLE);
            holder.mVProductMain.setTag(FIRST_STICKY_VIEW);
            holder.mTvHeader.setText(getCategoryCountInfo(listBean.getCategoryParent(), listBean.getCategoryChild()));
        } else {
            if (!TextUtils.equals(listBean.getCategoryParent(), mListBeanList.get(position - 1).getCategoryParent()) ||
                    !TextUtils.equals(listBean.getCategoryChild(), mListBeanList.get(position - 1).getCategoryChild())) {
                holder.mVStickHeader.setVisibility(View.VISIBLE);
                holder.mVProductMain.setTag(HAS_STICKY_VIEW);
                holder.mTvHeader.setText(getCategoryCountInfo(listBean.getCategoryParent(), listBean.getCategoryChild()));
            } else {
                holder.mVProductMain.setTag(NONE_STICKY_VIEW);
                holder.mVStickHeader.setVisibility(View.GONE);
            }
        }
        holder.mVProductMain.setContentDescription(getCategoryCountInfo(listBean.getCategoryParent(), listBean.getCategoryChild()));
    }

    private String getCategoryCountInfo(String categoryParent, String categoryChild) {
        String desc = "";
        int count = 0;
        int productCount = 0;
        for (ProductBasicList.ListBean listBean : mListBeanList) {
            if (listBean.getCategoryParent().equals(categoryParent)
                    && listBean.getCategoryChild().equals(categoryChild)) {
                count++;
                productCount += listBean.getActualQty();
            }
        }
        if (TextUtils.isEmpty(categoryChild)) {
            desc = categoryParent + "(" + count + "种,共" + productCount+"件)";
        } else {
            desc = categoryParent + "/" + categoryChild + "(" + count + "种,共" + productCount+"件)";
        }
        return desc;
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
