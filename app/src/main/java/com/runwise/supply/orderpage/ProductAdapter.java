package com.runwise.supply.orderpage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.LongPressUtil;
import com.runwise.supply.view.ProductImageDialog;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by Dong on 2017/11/28.
 */

public class ProductAdapter extends IBaseAdapter<ProductBasicList.ListBean> {

    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;
    DecimalFormat df = new DecimalFormat("#.##");
    Context mContext;
    boolean canSeePrice = false;
//    Map<ProductData.ListBean,Integer> mCountMap;
    ProductActivityV2.ProductCountSetter productCountSetter;
    boolean hasOtherSub = false;

    public ProductAdapter(@NonNull Context context, boolean hasOtherSub){
        mContext = context;
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        this.hasOtherSub = hasOtherSub;
    }

//    public void setCountMap(Map<ProductData.ListBean,Integer> countMap){
//        mCountMap = countMap;
//    }

    public void setProductCountSetter(ProductActivityV2.ProductCountSetter productCountSetter){
        this.productCountSetter = productCountSetter;
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final ProductBasicList.ListBean bean = (ProductBasicList.ListBean) mList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //有其它子分类和没有其它子分类layout有不同，但是id必须是一样的
            convertView = View.inflate(mContext, R.layout.item_product_with_subcategory, null);
            ViewUtils.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            String headText = bean.getCategoryChild();
            viewHolder.mStickHeader.setVisibility(!TextUtils.isEmpty(headText)?View.VISIBLE:View.GONE);
            viewHolder.mTvHeader.setText(headText);
            viewHolder.mStickHeader.setTag(FIRST_STICKY_VIEW);
        } else {
            if (!TextUtils.equals(bean.getCategoryChild(), mList.get(position - 1).getCategoryChild())) {
                viewHolder.mStickHeader.setVisibility(View.VISIBLE);
                viewHolder.mTvHeader.setText(bean.getCategoryChild());
                viewHolder.mStickHeader.setTag(HAS_STICKY_VIEW);
            } else {
                viewHolder.mStickHeader.setVisibility(View.GONE);
                viewHolder.mStickHeader.setTag(NONE_STICKY_VIEW);
            }
        }
        convertView.setContentDescription(bean.getCategoryChild());

        //标签
        if(TextUtils.isEmpty(bean.getProductTag())){
            viewHolder.tvProductTag.setVisibility(View.GONE);
        }else{
            viewHolder.tvProductTag.setVisibility(View.VISIBLE);
        }

//        final int count = mCountMap.get(bean)==null?0:mCountMap.get(bean);
        double count = productCountSetter.getCount(bean);
        viewHolder.tvCount.setText(NumberUtil.getIOrD(count)+bean.getSaleUom());
        //先根据集合里面对应个数初始化一次
        if (count > 0) {
            viewHolder.tvCount.setVisibility(View.VISIBLE);
            viewHolder.inputMBtn.setVisibility(View.VISIBLE);
            viewHolder.inputPBtn.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
        } else {
            viewHolder.tvCount.setVisibility(View.INVISIBLE);
            viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
            viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
        }

        /**
         * 减
         */
        viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                int currentNum = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                double currentNum = productCountSetter.getCount(bean);
                if (currentNum > 0) {
                    //https://stackoverflow.com/questions/179427/how-to-resolve-a-java-rounding-double-issue
                    //防止double的问题
                    currentNum = BigDecimal.valueOf(currentNum).subtract(BigDecimal.ONE).doubleValue();
                    if(currentNum<0)currentNum = 0;
                    viewHolder.tvCount.setText(NumberUtil.getIOrD(currentNum) + bean.getSaleUom());
//                    mCountMap.put(bean, currentNum);
                    productCountSetter.setCount(bean,currentNum);
                    if (currentNum == 0) {
                        v.setVisibility(View.INVISIBLE);
                        viewHolder.tvCount.setVisibility(View.INVISIBLE);
                        viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
//                        mCountMap.remove(bean);
                    }
                    ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(bean,currentNum);
                    productCountUpdateEvent.setException(ProductAdapter.this);
                    EventBus.getDefault().post(productCountUpdateEvent);
                }

            }
        });
        LongPressUtil longPressUtil = new LongPressUtil();
        longPressUtil.setUpEvent(viewHolder.inputMBtn, new LongPressUtil.CallBack() {
            @Override
            public void call() {
                viewHolder.tvCount.setText(0 + bean.getSaleUom());
                productCountSetter.setCount(bean, 0);

                viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
                viewHolder.tvCount.setVisibility(View.INVISIBLE);
                viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);

                ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(bean, 0);
                productCountUpdateEvent.setException(ProductAdapter.this);
                EventBus.getDefault().post(productCountUpdateEvent);
            }
        });

        /**
         * 加
         */
        viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                int currentNum = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                double currentNum = productCountSetter.getCount(bean);
                currentNum = BigDecimal.valueOf(currentNum).add(BigDecimal.ONE).doubleValue();
                viewHolder.tvCount.setText(NumberUtil.getIOrD(currentNum) + bean.getSaleUom());
//                mCountMap.put(bean, currentNum);
                productCountSetter.setCount(bean,currentNum);
                if (currentNum == 1) {//0变到1
                    viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                    viewHolder.tvCount.setVisibility(View.VISIBLE);
                    viewHolder.inputPBtn.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
                }
                ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(bean,currentNum);
                productCountUpdateEvent.setException(ProductAdapter.this);
                EventBus.getDefault().post(productCountUpdateEvent);
            }
        });

        /**
         * 点击数量展示输入对话框
         */
        viewHolder.tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int currentCount = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                double currentCount = productCountSetter.getCount(bean);
                new ProductValueDialog(mContext, bean.getName(), currentCount, productCountSetter.getRemark(bean),new ProductValueDialog.IProductDialogCallback() {
                    @Override
                    public void onInputValue(double value,String remark) {

                        productCountSetter.setCount(bean,value);
                        bean.setRemark(remark);
                        productCountSetter.setRemark(bean);
                        if (value == 0) {
                            viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
                            viewHolder.tvCount.setVisibility(View.INVISIBLE);
                            viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
//                            mCountMap.remove(bean);
                        }else{
                            viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                            viewHolder.tvCount.setVisibility(View.VISIBLE);
                            viewHolder.tvCount.setText(value+bean.getSaleUom());
                            viewHolder.inputPBtn.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
//                            mCountMap.put(bean,value);
                        }
                        viewHolder.tvCount.setText(value + bean.getSaleUom());
                        ProductCountUpdateEvent productCountUpdateEvent = new ProductCountUpdateEvent(bean,value);
                        productCountUpdateEvent.setException(ProductAdapter.this);
                        EventBus.getDefault().post(productCountUpdateEvent);
                    }
                }).show();
            }
        });

        viewHolder.name.setText(bean.getName());
        viewHolder.tvCode.setText(bean.getDefaultCode());
        viewHolder.tvContent.setText(bean.getUnit());

        if (canSeePrice) {
            StringBuffer sb1 = new StringBuffer();
                sb1.append("¥").append(df.format(Double.valueOf(bean.getPrice())));
                viewHolder.tvPrice.setText(sb1.toString());
                viewHolder.tvPriceUnit.setText("/"+bean.getSaleUom());
        } else {
            viewHolder.tvPrice.setVisibility(View.GONE);
            viewHolder.tvPriceUnit.setVisibility(View.GONE);
        }

        if(bean.getImage()!=null){
            FrecoFactory.getInstance(mContext).displayWithoutHost(viewHolder.sDv, bean.getImage().getImageSmall());
        }
        viewHolder.sDv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductImageDialog productImageDialog = new ProductImageDialog(mContext);
                productImageDialog.setListBean(bean);
                productImageDialog.setProductCountSetter(productCountSetter);
                productImageDialog.show();
            }
        });
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.tv_product_name)
        TextView name;   //名称
        @ViewInject(R.id.sdv_product_image)
        SimpleDraweeView sDv;    //头像
        @ViewInject(R.id.iv_product_reduce)
        ImageButton inputMBtn;//减
        @ViewInject(R.id.iv_product_add)
        ImageButton inputPBtn;//加
        @ViewInject(R.id.tv_product_count)
        TextView tvCount;//数量
        @ViewInject(R.id.tv_product_code)//代码
                TextView tvCode;
        @ViewInject(R.id.tv_product_price_unit)//价格后的单位
                TextView tvPriceUnit;
        @ViewInject(R.id.tv_product_price)//价格
                TextView tvPrice;
        @ViewInject(R.id.tv_product_content)
        TextView tvContent;
        @ViewInject(R.id.iv_product_sale)
        TextView tvProductTag;
        @ViewInject(R.id.tv_header)
        TextView mTvHeader;
        @ViewInject(R.id.stick_header)
        FrameLayout mStickHeader;
    }
}
