package com.runwise.supply.orderpage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductData;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by Dong on 2017/11/28.
 */

public class ProductAdapter extends IBaseAdapter<ProductData.ListBean> {
    DecimalFormat df = new DecimalFormat("#.##");
    Context mContext;
    boolean canSeePrice = false;
    Map<ProductData.ListBean,Integer> mCountMap;
    boolean hasOtherSub = false;

    public ProductAdapter(@NonNull Context context, @NonNull Map<ProductData.ListBean,Integer> countMap, boolean hasOtherSub){
        mContext = context;
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        mCountMap = countMap;
        this.hasOtherSub = hasOtherSub;
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final ProductData.ListBean bean = (ProductData.ListBean) mList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //有其它子分类和没有其它子分类layout有不同，但是id必须是一样的
            if(hasOtherSub) convertView = View.inflate(mContext, R.layout.item_product_with_subcategory, null);
            else convertView = View.inflate(mContext,R.layout.item_product_without_subcategory,null);
            ViewUtils.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //标签
        if(TextUtils.isEmpty(bean.getProductTag())){
            viewHolder.tvProductTag.setVisibility(View.GONE);
        }else{
            viewHolder.tvProductTag.setText(bean.getProductTag());
        }

        final int count = mCountMap.get(bean)==null?0:mCountMap.get(bean);
        viewHolder.tvCount.setText(count+bean.getProductUom());
        //先根据集合里面对应个数初始化一次
        if (count > 0) {
            viewHolder.tvCount.setVisibility(View.VISIBLE);
            viewHolder.inputMBtn.setVisibility(View.VISIBLE);
            viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_green);
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
                int currentNum = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                if (currentNum > 0) {
                    viewHolder.tvCount.setText(--currentNum + bean.getProductUom());
                    mCountMap.put(bean, currentNum);
                    if (currentNum == 0) {
                        v.setVisibility(View.INVISIBLE);
                        viewHolder.tvCount.setVisibility(View.INVISIBLE);
                        viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_green);
                        mCountMap.remove(bean);
                    }
                    EventBus.getDefault().post(new ProductCountUpdateEvent(bean,currentNum));
                }

            }
        });

        /**
         * 加
         */
        viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentNum = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                viewHolder.tvCount.setText(++currentNum + bean.getProductUom());
                mCountMap.put(bean, currentNum);
                if (currentNum == 1) {//0变到1
                    viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                    viewHolder.tvCount.setVisibility(View.VISIBLE);
                    viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
                }
                EventBus.getDefault().post(new ProductCountUpdateEvent(bean,currentNum));
            }
        });

        /**
         * 点击数量展示输入对话框
         */
        viewHolder.tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentCount = mCountMap.get(bean)==null?0:mCountMap.get(bean);
                new ProductValueDialog(mContext, bean.getName(), currentCount, new ProductValueDialog.IProductDialogCallback() {
                    @Override
                    public void onInputValue(int value) {

                        if (value == 0) {
                            viewHolder.inputMBtn.setVisibility(View.INVISIBLE);
                            viewHolder.tvCount.setVisibility(View.INVISIBLE);
                            viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_green);
                            mCountMap.remove(bean);
                        }else{
                            viewHolder.inputMBtn.setVisibility(View.VISIBLE);
                            viewHolder.tvCount.setVisibility(View.VISIBLE);
                            viewHolder.tvCount.setText(value+bean.getProductUom());
                            viewHolder.inputPBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
                            mCountMap.put(bean,value);
                        }
                        viewHolder.tvCount.setText(value + bean.getProductUom());
                        EventBus.getDefault().post(new ProductCountUpdateEvent(bean,value));
                    }
                }).show();
            }
        });

        viewHolder.name.setText(bean.getName());
        viewHolder.tvCode.setText(bean.getDefaultCode());
        viewHolder.tvContent.setText(bean.getUnit());

        if (canSeePrice) {
            StringBuffer sb1 = new StringBuffer();
            if (bean.isIsTwoUnit()) {
                sb1.append("¥").append(df.format(Double.valueOf(bean.getSettlePrice())));
                viewHolder.tvPrice.setText(sb1.toString());
                viewHolder.tvPriceUnit.setText("/"+bean.getSettleUomId());
            } else {
                sb1.append("¥").append(df.format(Double.valueOf(bean.getPrice())));
                viewHolder.tvPrice.setText(sb1.toString());
                viewHolder.tvPriceUnit.setText("/"+bean.getUom());
            }
        } else {
            viewHolder.tvPrice.setVisibility(View.GONE);
            viewHolder.tvPriceUnit.setVisibility(View.GONE);
        }

        if(bean.getImage()!=null){
            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + bean.getImage().getImageSmall());
        }

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
    }
}
