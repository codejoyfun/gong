package com.runwise.supply.adapter;

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
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.entity.StockProductListResponse;
import com.runwise.supply.event.TransferoutProductCountUpdateEvent;
import com.runwise.supply.mine.TransferoutProductListActivity;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.TransferProductValueDialog;
import com.runwise.supply.tools.LongPressUtil;
import com.runwise.supply.view.ProductImageDialog;
import com.runwise.supply.view.TransferProductImageDialog;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/6/13.
 */

public class TransferoutProductAdapter extends IBaseAdapter<StockProductListResponse.ListBean> {

    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;
    DecimalFormat df = new DecimalFormat("#.##");
    Context mContext;
    boolean canSeePrice = false;
    //    Map<ProductData.ListBean,Integer> mCountMap;
    TransferoutProductListActivity.ProductCountSetter productCountSetter;
    boolean hasOtherSub = false;

    public TransferoutProductAdapter(@NonNull Context context, boolean hasOtherSub) {
        mContext = context;
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        this.hasOtherSub = hasOtherSub;
    }

//    public void setCountMap(Map<ProductData.ListBean,Integer> countMap){
//        mCountMap = countMap;
//    }

    public void setProductCountSetter(TransferoutProductListActivity.ProductCountSetter productCountSetter) {
        this.productCountSetter = productCountSetter;
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final StockProductListResponse.ListBean bean = (StockProductListResponse.ListBean) mList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //有其它子分类和没有其它子分类layout有不同，但是id必须是一样的
            convertView = View.inflate(mContext, R.layout.list_item_transferout_product, null);
            ViewUtils.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            String headText = bean.getCategoryChild();
            viewHolder.mStickHeader.setVisibility(!TextUtils.isEmpty(headText) ? View.VISIBLE : View.GONE);
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
        viewHolder.tvProductTag.setVisibility(View.GONE);


//        final int count = mCountMap.get(bean)==null?0:mCountMap.get(bean);
        double count = productCountSetter.getCount(bean);
        viewHolder.tvCount.setText(NumberUtil.getIOrD(count) + bean.getStockUom());
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
                    if (currentNum < 0) currentNum = 0;
                    viewHolder.tvCount.setText(NumberUtil.getIOrD(currentNum) + bean.getStockUom());
//                    mCountMap.put(bean, currentNum);
                    productCountSetter.setCount(bean, currentNum);
                    if (currentNum == 0) {
                        v.setVisibility(View.INVISIBLE);
                        viewHolder.tvCount.setVisibility(View.INVISIBLE);
                        viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
//                        mCountMap.remove(bean);
                    }
                    TransferoutProductCountUpdateEvent TransferoutProductCountUpdateEvent = new TransferoutProductCountUpdateEvent(bean, currentNum);
                    TransferoutProductCountUpdateEvent.setException(this);
                    EventBus.getDefault().post(TransferoutProductCountUpdateEvent);
                }

            }
        });
        LongPressUtil longPressUtil = new LongPressUtil();
        longPressUtil.setUpEvent(viewHolder.inputMBtn, new LongPressUtil.CallBack() {
            @Override
            public void call() {
                viewHolder.tvCount.setText(0 + bean.getStockUom());
                productCountSetter.setCount(bean, 0);

                viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
                viewHolder.tvCount.setVisibility(View.INVISIBLE);
                viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);

                TransferoutProductCountUpdateEvent TransferoutProductCountUpdateEvent = new TransferoutProductCountUpdateEvent(bean, 0);
                TransferoutProductCountUpdateEvent.setException(this);
                EventBus.getDefault().post(TransferoutProductCountUpdateEvent);
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
//                if (currentNum > bean.getQty()) {
//                    ToastUtil.show(mContext, "超过了库存数量!");
//                    return;
//                }
                viewHolder.tvCount.setText(NumberUtil.getIOrD(currentNum) + bean.getStockUom());
//                mCountMap.put(bean, currentNum);
                productCountSetter.setCount(bean, currentNum);
                if (currentNum == 1) {//0变到1
                    viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                    viewHolder.tvCount.setVisibility(View.VISIBLE);
                    viewHolder.inputPBtn.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
                }
                TransferoutProductCountUpdateEvent TransferoutProductCountUpdateEvent = new TransferoutProductCountUpdateEvent(bean, currentNum);
                TransferoutProductCountUpdateEvent.setException(this);
                EventBus.getDefault().post(TransferoutProductCountUpdateEvent);
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
                new TransferProductValueDialog(mContext, bean.getName(), currentCount, productCountSetter.getRemark(bean), new TransferProductValueDialog.IProductDialogCallback() {
                    @Override
                    public void onInputValue(double value) {
//                        if (value > bean.getQty()) {
//                            ToastUtil.show(mContext, "超过了库存数量!");
//                            return;
//                        }
                        productCountSetter.setCount(bean, value);
                        productCountSetter.setRemark(bean);
                        if (value == 0) {
                            viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
                            viewHolder.tvCount.setVisibility(View.INVISIBLE);
                            viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
//                            mCountMap.remove(bean);
                        } else {

                            viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                            viewHolder.tvCount.setVisibility(View.VISIBLE);
                            viewHolder.tvCount.setText(value + bean.getStockUom());
                            viewHolder.inputPBtn.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
//                            mCountMap.put(bean,value);
                        }
                        viewHolder.tvCount.setText(value + bean.getStockUom());
                        TransferoutProductCountUpdateEvent TransferoutProductCountUpdateEvent = new TransferoutProductCountUpdateEvent(bean, (int) value);
                        TransferoutProductCountUpdateEvent.setException(this);
                        EventBus.getDefault().post(TransferoutProductCountUpdateEvent);
                    }
                }).show();
            }
        });

        viewHolder.name.setText(bean.getName());
        viewHolder.tvCode.setText(bean.getDefaultCode());
        viewHolder.tvContent.setText(bean.getUnit());

        viewHolder.tvPrice.setText("库存  " + bean.getQty() + bean.getStockUom());

        if (bean.getImage() != null) {
            FrecoFactory.getInstance(mContext).displayWithoutHost(viewHolder.sDv, bean.getImage().getImageSmall());
        }
        viewHolder.sDv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransferProductImageDialog productImageDialog = new TransferProductImageDialog(mContext);
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
