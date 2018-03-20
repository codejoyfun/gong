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
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductValueDialog;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.ProductImageDialog;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/3/1.
 */

public class ProductAdapterV2 extends RecyclerView.Adapter<ProductAdapterV2.ViewHolder> {
    //    ProductBasicList.ListBean
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    ProductActivityV2.ProductCountSetter productCountSetter;
    boolean canSeePrice = false;
    DecimalFormat df = new DecimalFormat("#.##");
    private List<ProductBasicList.ListBean> mData;

    public ProductAdapterV2(@Nullable List<ProductBasicList.ListBean> data) {
//        super(R.layout.item_product_with_subcategory, data);
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        mData = data;
    }

    public void setProductCountSetter(ProductActivityV2.ProductCountSetter productCountSetter) {
        this.productCountSetter = productCountSetter;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_with_subcategory, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position,List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            ProductBasicList.ListBean listBean = mData.get(position);
            double count = productCountSetter.getCount(listBean);
            holder.mTvProductCount.setText(NumberUtil.getIOrD(count) + listBean.getSaleUom());
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductBasicList.ListBean listBean = mData.get(position);
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
        if (TextUtils.isEmpty(listBean.getProductTag())) {
            holder.mIvProductSale.setVisibility(View.GONE);
        } else {
            holder.mIvProductSale.setVisibility(View.VISIBLE);
        }
        double count = productCountSetter.getCount(listBean);
        holder.mTvProductCount.setText(NumberUtil.getIOrD(count) + listBean.getSaleUom());
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
                    holder.mTvProductCount.setText(NumberUtil.getIOrD(currentNum) + listBean.getSaleUom());
                    productCountSetter.setCount(listBean, currentNum);
                    if (currentNum == 0) {
                        v.setVisibility(View.INVISIBLE);
                        holder.mTvProductCount.setVisibility(View.INVISIBLE);
                        holder.mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
                    }
                    ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(listBean, currentNum);
                    productCountUpdateEvent.setException(ProductAdapterV2.this);
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
                holder.mTvProductCount.setText(NumberUtil.getIOrD(currentNum) + listBean.getSaleUom());
                productCountSetter.setCount(listBean, currentNum);
                if (currentNum == 1) {//0变到1
                    holder.mIvProductReduce.setVisibility(View.VISIBLE);
                    holder.mTvProductCount.setVisibility(View.VISIBLE);
                    holder.mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
                }
                ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(listBean, currentNum);
                productCountUpdateEvent.setException(ProductAdapterV2.this);
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
                new ProductValueDialog(holder.itemView.getContext(), listBean.getName(), currentCount, productCountSetter.getRemark(listBean), new ProductValueDialog.IProductDialogCallback() {
                    @Override
                    public void onInputValue(double value, String remark) {

                        productCountSetter.setCount(listBean, value);
                        listBean.setRemark(remark);
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
                        holder.mTvProductCount.setText(NumberUtil.getIOrD(value) + listBean.getSaleUom());
                        ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(listBean, (int) value);
                        productCountUpdateEvent.setException(ProductAdapterV2.this);
                        EventBus.getDefault().post(productCountUpdateEvent);
                    }
                }).show();
            }
        });

        holder.mTvProductName.setText(listBean.getName());
        holder.mTvProductCode.setText(listBean.getDefaultCode());
        holder.mTvProductContent.setText(listBean.getUnit());

        if (canSeePrice) {
            StringBuffer sb1 = new StringBuffer();
            sb1.append("¥").append(df.format(Double.valueOf(listBean.getPrice())));

            holder.mTvProductPrice.setText(sb1.toString());
            holder.mTvProductPriceUnit.setText("/" + listBean.getSaleUom());
        } else {
            holder.mTvProductPrice.setVisibility(View.GONE);
            holder.mTvProductPriceUnit.setVisibility(View.GONE);
        }
        if (listBean.getImage() != null) {
            FrecoFactory.getInstance(holder.itemView.getContext()).displayWithoutHost(holder.mSdvProductImage, listBean.getImage().getImageSmall());

        }

        holder.mSdvProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductImageDialog productImageDialog = new ProductImageDialog(holder.itemView.getContext());
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

    public List<ProductBasicList.ListBean> getData() {
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
