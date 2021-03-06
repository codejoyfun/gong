package com.runwise.supply.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.runwise.supply.R;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.entity.StockProductListResponse;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.event.TransferoutProductCountUpdateEvent;
import com.runwise.supply.mine.TransferoutProductListActivity;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductValueDialog;
import com.runwise.supply.orderpage.TransferProductValueDialog;
import com.runwise.supply.view.ProductImageDialog;
import com.runwise.supply.view.TransferProductImageDialog;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/6/13.
 */

public class TransferoutProductAdapterV2 extends RecyclerView.Adapter<ProductAdapterV2.ViewHolder> {
    //    StockProductListResponse.ListBean
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    TransferoutProductListActivity.ProductCountSetter productCountSetter;
    boolean canSeePrice = false;
    DecimalFormat df = new DecimalFormat("#.##");
    private List<StockProductListResponse.ListBean> mData;

    public TransferoutProductAdapterV2(@Nullable List<StockProductListResponse.ListBean> data) {
//        super(R.layout.item_product_with_subcategory, data);
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        mData = data;
    }

    public void setProductCountSetter(TransferoutProductListActivity.ProductCountSetter productCountSetter) {
        this.productCountSetter = productCountSetter;
    }


    @Override


    public ProductAdapterV2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transferout_product_with_subcategory, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ProductAdapterV2.ViewHolder viewHolder = new ProductAdapterV2.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductAdapterV2.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            StockProductListResponse.ListBean listBean = mData.get(position);
            double count = productCountSetter.getCount(listBean);
            holder.mTvProductCount.setText(NumberUtil.getIOrD(count) + listBean.getStockUom());
            //先根据集合里面对应个数初始化一次
            if (count > 0) {
                holder.mTvProductCount.setVisibility(View.VISIBLE);
                holder.mIvProductReduce.setVisibility(View.VISIBLE);
                holder.mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
            } else {
                holder.mTvProductCount.setVisibility(View.INVISIBLE);
                holder.mIvProductReduce.setVisibility(View.INVISIBLE);
                holder.mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
            }
        }
    }

    @Override
    public void onBindViewHolder(ProductAdapterV2.ViewHolder holder, int position) {
        StockProductListResponse.ListBean listBean = mData.get(position);
        if (holder.getAdapterPosition() == 0) {
            String headText = listBean.getCategoryChild();
            holder.mStickHeader.setVisibility(!TextUtils.isEmpty(headText)?View.VISIBLE:View.GONE);
            holder.mTvHeader.setText(headText);
            holder.mFoodMain.setTag(FIRST_STICKY_VIEW);
        } else {
            if (!TextUtils.equals(listBean.getCategoryChild(), mData.get(holder.getAdapterPosition() - 1).getCategoryChild())) {
                holder.mStickHeader.setVisibility(View.VISIBLE);
                holder.mTvHeader.setText(listBean.getCategoryChild());
                holder.mFoodMain.setTag(HAS_STICKY_VIEW);
            } else {
                holder.mStickHeader.setVisibility(View.GONE);
                holder.mFoodMain.setTag(NONE_STICKY_VIEW);
            }
        }
        holder.itemView.setContentDescription(listBean.getCategoryChild());

        //标签
            holder.mIvProductSale.setVisibility(View.GONE);
        double count = productCountSetter.getCount(listBean);
        holder.mTvProductCount.setText(NumberUtil.getIOrD(count) + listBean.getStockUom());
        //先根据集合里面对应个数初始化一次
        if (count > 0) {
            holder.mTvProductCount.setVisibility(View.VISIBLE);
            holder.mIvProductReduce.setVisibility(View.VISIBLE);
            holder.mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
        } else {
            holder.mTvProductCount.setVisibility(View.INVISIBLE);
            holder.mIvProductReduce.setVisibility(View.INVISIBLE);
            holder.mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
        }
        /**
         * 减
         */
        holder.mIvProductReduce.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                int currentNum = mCountMap.get(listBean)==null?0:mCountMap.get(listBean);
                double currentNum = productCountSetter.getCount(listBean);
                if (currentNum > 0) {
                    //https://stackoverflow.com/questions/179427/how-to-resolve-a-java-rounding-double-issue
                    //防止double的问题
                    currentNum = BigDecimal.valueOf(currentNum).subtract(BigDecimal.ONE).doubleValue();
                    if (currentNum < 0) currentNum = 0;
                    holder.mTvProductCount.setText(NumberUtil.getIOrD(currentNum) + listBean.getStockUom());
                    productCountSetter.setCount(listBean, currentNum);
                    if (currentNum == 0) {
                        v.setVisibility(View.INVISIBLE);
                        holder.mTvProductCount.setVisibility(View.INVISIBLE);
                        holder.mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
                    }
                    TransferoutProductCountUpdateEvent productCountUpdateEvent = new TransferoutProductCountUpdateEvent(listBean, currentNum);
                    productCountUpdateEvent.setException(TransferoutProductAdapterV2.this);
                    EventBus.getDefault().post(productCountUpdateEvent);
                }

            }
        });

        /**
         * 加
         */
        holder.mIvProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentNum = productCountSetter.getCount(listBean);
                currentNum = BigDecimal.valueOf(currentNum).add(BigDecimal.ONE).doubleValue();
                holder.mTvProductCount.setText(NumberUtil.getIOrD(currentNum) + listBean.getStockUom());
                productCountSetter.setCount(listBean, currentNum);
                if (currentNum == 1) {//0变到1
                    holder.mIvProductReduce.setVisibility(View.VISIBLE);
                    holder.mTvProductCount.setVisibility(View.VISIBLE);
                    holder.mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
                }
                TransferoutProductCountUpdateEvent productCountUpdateEvent = new TransferoutProductCountUpdateEvent(listBean, currentNum);
                productCountUpdateEvent.setException(TransferoutProductAdapterV2.this);
                EventBus.getDefault().post(productCountUpdateEvent);
            }
        });
        /**
         * 点击数量展示输入对话框
         */
        holder.mTvProductCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentCount = productCountSetter.getCount(listBean);
                new TransferProductValueDialog(holder.itemView.getContext(), listBean.getName(), currentCount, productCountSetter.getRemark(listBean), new TransferProductValueDialog.IProductDialogCallback() {
                    @Override
                    public void onInputValue(double value) {

                        productCountSetter.setCount(listBean, value);
                        productCountSetter.setRemark(listBean);
                        if (value == 0) {
                            holder.mIvProductReduce.setVisibility(View.INVISIBLE);
                            holder.mTvProductCount.setVisibility(View.INVISIBLE);
                            holder.mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
                        } else {
                            holder.mIvProductReduce.setVisibility(View.VISIBLE);
                            holder.mTvProductCount.setVisibility(View.VISIBLE);
                            holder.mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
                        }
                        holder.mTvProductCount.setText(NumberUtil.getIOrD(value) + listBean.getStockUom());
                        TransferoutProductCountUpdateEvent productCountUpdateEvent = new TransferoutProductCountUpdateEvent(listBean, (int) value);
                        productCountUpdateEvent.setException(TransferoutProductAdapterV2.this);
                        EventBus.getDefault().post(productCountUpdateEvent);
                    }
                }).show();
            }
        });

        holder.mTvProductName.setText(listBean.getName());
        holder.mTvProductCode.setText(listBean.getDefaultCode());
        holder.mTvProductContent.setText(listBean.getUnit());
        holder.mTvProductPriceUnit.setText("");

        holder.mTvProductPrice.setText("库存  " + listBean.getQty() + listBean.getStockUom());

        if (listBean.getImage() != null) {
            FrecoFactory.getInstance(holder.itemView.getContext()).displayWithoutHost(holder.mSdvProductImage, listBean.getImage().getImageSmall());

        }

        holder.mSdvProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransferProductImageDialog productImageDialog = new TransferProductImageDialog(holder.itemView.getContext());
                productImageDialog.setListBean(listBean);
                productImageDialog.setProductCountSetter(productCountSetter);
                productImageDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<StockProductListResponse.ListBean> getData() {
        return mData;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_header)
        TextView mTvHeader;
        @BindView(R.id.stick_header)
        FrameLayout mStickHeader;
        @BindView(R.id.sdv_product_image)
        SimpleDraweeView mSdvProductImage;
        @BindView(R.id.tv_product_name)
        TextView mTvProductName;
        @BindView(R.id.tv_product_code)
        TextView mTvProductCode;
        @BindView(R.id.v_line)
        View mVLine;
        @BindView(R.id.tv_product_content)
        TextView mTvProductContent;
        @BindView(R.id.iv_product_sale)
        TextView mIvProductSale;
        @BindView(R.id.tv_product_price)
        TextView mTvProductPrice;
        @BindView(R.id.tv_product_price_unit)
        TextView mTvProductPriceUnit;
        @BindView(R.id.iv_product_reduce)
        ImageButton mIvProductReduce;
        @BindView(R.id.tv_product_count)
        TextView mTvProductCount;
        @BindView(R.id.iv_product_add)
        ImageButton mIvProductAdd;
        @BindView(R.id.food_main)
        LinearLayout mFoodMain;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}