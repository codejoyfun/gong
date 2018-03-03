package com.runwise.supply.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.runwise.supply.GlobalApplication;
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

import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/3/1.
 */

public class ProductAdapterV2 extends BaseQuickAdapter<ProductBasicList.ListBean, BaseViewHolder> {

    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    ProductActivityV2.ProductCountSetter productCountSetter;
    boolean canSeePrice = false;
    DecimalFormat df = new DecimalFormat("#.##");
    private List<ProductBasicList.ListBean> mData;

    public ProductAdapterV2(@Nullable List<ProductBasicList.ListBean> data) {
        super(R.layout.item_product_with_subcategory, data);
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        mData = data;
    }

    public void setProductCountSetter(ProductActivityV2.ProductCountSetter productCountSetter){
        this.productCountSetter = productCountSetter;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ProductBasicList.ListBean listBean) {

        if (baseViewHolder.getAdapterPosition() == 0) {
            baseViewHolder.setVisible(R.id.stick_header, !TextUtils.isEmpty(listBean.getCategoryChild()))
                    .setText(R.id.tv_header, listBean.getCategoryChild())
                    .setTag(R.id.food_main, FIRST_STICKY_VIEW);
        } else {
            if (!TextUtils.equals(listBean.getCategoryChild(), mData.get(baseViewHolder.getAdapterPosition() - 1).getCategoryChild())) {
                baseViewHolder.setVisible(R.id.stick_header, true)
                        .setText(R.id.tv_header, listBean.getCategoryChild())
                        .setTag(R.id.food_main, HAS_STICKY_VIEW);
            } else {
                baseViewHolder.setVisible(R.id.stick_header, false)
                        .setTag(R.id.food_main, NONE_STICKY_VIEW);
            }
        }
        baseViewHolder.getConvertView().setContentDescription(listBean.getCategoryChild());

        //标签
        if (TextUtils.isEmpty(listBean.getProductTag())) {
            baseViewHolder.getView(R.id.iv_product_sale).setVisibility(View.GONE);
        } else {
            baseViewHolder.getView(R.id.iv_product_sale).setVisibility(View.VISIBLE);
        }
        double count = productCountSetter.getCount(listBean);
        baseViewHolder.setText(R.id.tv_product_count, NumberUtil.getIOrD(count) + listBean.getSaleUom());
        //先根据集合里面对应个数初始化一次
        if (count > 0) {
            baseViewHolder.getView(R.id.tv_product_count).setVisibility(View.VISIBLE);
            baseViewHolder.getView(R.id.iv_product_reduce).setVisibility(View.VISIBLE);
            baseViewHolder.setBackgroundRes(R.id.iv_product_add, R.drawable.ic_order_btn_add_green_part);
        } else {
            baseViewHolder.getView(R.id.tv_product_count).setVisibility(View.INVISIBLE);
            baseViewHolder.getView(R.id.iv_product_reduce).setVisibility(View.INVISIBLE);
            baseViewHolder.setBackgroundRes(R.id.iv_product_add, R.drawable.order_btn_add_gray);
        }
        /**
         * 减
         */
        baseViewHolder.setOnClickListener(R.id.iv_product_reduce, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                int currentNum = mCountMap.get(listBean)==null?0:mCountMap.get(listBean);
                double currentNum = productCountSetter.getCount(listBean);
                if (currentNum > 0) {
                    //https://stackoverflow.com/questions/179427/how-to-resolve-a-java-rounding-double-issue
                    //防止double的问题
                    currentNum = BigDecimal.valueOf(currentNum).subtract(BigDecimal.ONE).doubleValue();
                    if (currentNum < 0) currentNum = 0;
                    baseViewHolder.setText(R.id.tv_product_count, NumberUtil.getIOrD(currentNum) + listBean.getSaleUom());
                    productCountSetter.setCount(listBean, currentNum);
                    if (currentNum == 0) {
                        v.setVisibility(View.INVISIBLE);
                        baseViewHolder.getView(R.id.tv_product_count).setVisibility(View.INVISIBLE);
                        baseViewHolder.setBackgroundRes(R.id.iv_product_add, R.drawable.order_btn_add_gray);
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
        baseViewHolder.setOnClickListener(R.id.iv_product_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentNum = productCountSetter.getCount(listBean);
                currentNum = BigDecimal.valueOf(currentNum).add(BigDecimal.ONE).doubleValue();
                baseViewHolder.setText(R.id.tv_product_count, NumberUtil.getIOrD(currentNum) + listBean.getSaleUom());
                productCountSetter.setCount(listBean, currentNum);
                if (currentNum == 1) {//0变到1
                    baseViewHolder.getView(R.id.iv_product_reduce).setVisibility(View.VISIBLE);
                    baseViewHolder.getView(R.id.tv_product_count).setVisibility(View.VISIBLE);
                    baseViewHolder.setBackgroundRes(R.id.iv_product_add, R.drawable.ic_order_btn_add_green_part);
                }
                ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(listBean, currentNum);
                productCountUpdateEvent.setException(ProductAdapterV2.this);
                EventBus.getDefault().post(productCountUpdateEvent);
            }
        });
        /**
         * 点击数量展示输入对话框
         */
        baseViewHolder.setOnClickListener(R.id.tv_product_count, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentCount = productCountSetter.getCount(listBean);
                new ProductValueDialog(mContext, listBean.getName(), currentCount, productCountSetter.getRemark(listBean), new ProductValueDialog.IProductDialogCallback() {
                    @Override
                    public void onInputValue(double value, String remark) {

                        productCountSetter.setCount(listBean, value);
                        listBean.setRemark(remark);
                        productCountSetter.setRemark(listBean);
                        if (value == 0) {
                            baseViewHolder.getView(R.id.iv_product_reduce).setVisibility(View.INVISIBLE);
                            baseViewHolder.getView(R.id.tv_product_count).setVisibility(View.INVISIBLE);
                            baseViewHolder.setBackgroundRes(R.id.iv_product_add, R.drawable.order_btn_add_gray);
                        } else {
                            baseViewHolder.getView(R.id.iv_product_reduce).setVisibility(View.VISIBLE);
                            baseViewHolder.getView(R.id.tv_product_count).setVisibility(View.VISIBLE);
                            baseViewHolder.setBackgroundRes(R.id.iv_product_add, R.drawable.ic_order_btn_add_green_part);
                        }
                        baseViewHolder.setText(R.id.tv_product_count, NumberUtil.getIOrD(value) + listBean.getSaleUom());
                        ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(listBean, (int) value);
                        productCountUpdateEvent.setException(ProductAdapterV2.this);
                        EventBus.getDefault().post(productCountUpdateEvent);
                    }
                }).show();
            }
        });

        baseViewHolder.setText(R.id.tv_product_name, listBean.getName());
        baseViewHolder.setText(R.id.tv_product_code, listBean.getDefaultCode());
        baseViewHolder.setText(R.id.tv_product_content, listBean.getUnit());

        if (canSeePrice) {
            StringBuffer sb1 = new StringBuffer();
            sb1.append("¥").append(df.format(Double.valueOf(listBean.getPrice())));

            baseViewHolder.setText(R.id.tv_product_price, sb1.toString());
            baseViewHolder.setText(R.id.tv_product_price_unit, "/" + listBean.getSaleUom());
        } else {
            baseViewHolder.getView(R.id.tv_product_price).setVisibility(View.GONE);
            baseViewHolder.getView(R.id.tv_product_price_unit).setVisibility(View.GONE);
        }
        if (listBean.getImage() != null) {
            FrecoFactory.getInstance(mContext).displayWithoutHost(baseViewHolder.getView(R.id.sdv_product_image), listBean.getImage().getImageSmall());
        }

        baseViewHolder.setOnClickListener(R.id.sdv_product_image,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductImageDialog productImageDialog = new ProductImageDialog(mContext);
                productImageDialog.setListBean(listBean);
                productImageDialog.setProductCountSetter(productCountSetter);
                productImageDialog.show();
            }
        });

    }

}
