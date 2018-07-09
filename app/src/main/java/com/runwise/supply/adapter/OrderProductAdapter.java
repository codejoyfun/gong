package com.runwise.supply.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.LotListActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.config.Constant.ORDER_STATE_DRAFT;
import static com.kids.commonframe.config.Constant.ORDER_STATE_PEISONG;
import static com.kids.commonframe.config.Constant.ORDER_STATE_SALE;

/**
 * Created by mike on 2017/9/27.
 */

public class OrderProductAdapter extends BaseAdapter {
    private Context context;
    private boolean hasReturn;          //是否有退货，默认没有
    private boolean isTwoUnit;           //双单位,有值就显示
    //当前状态
    private String status;
    private String orderName;
    private OrderResponse.ListBean mListBean;

    public void setHasReturn(boolean hasReturn) {
        this.hasReturn = hasReturn;
    }

    public void setTwoUnit(boolean twoUnit) {
        isTwoUnit = twoUnit;
    }

    private List<OrderResponse.ListBean.LinesBean> productList = new ArrayList();

    public OrderProductAdapter(Context context) {
        this.context = context;
    }

    public void setProductList(List<OrderResponse.ListBean.LinesBean> productList) {
        this.productList.clear();
        if (productList != null && productList.size() > 0) {
            this.productList.addAll(productList);
        }
        notifyDataSetChanged();
    }

    public void setStatus(String orderName, String status, OrderResponse.ListBean listBean) {
        this.status = status;
        this.orderName = orderName;
        mListBean = listBean;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.orderdetail_list_item, parent, false);
            vh = new ViewHolder(convertView);
            vh.oldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        final OrderResponse.ListBean.LinesBean bean = productList.get(position);
        int pId = bean.getProductID();

        String imageUrl = bean.getImageMedium();
        final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pId));
        if(TextUtils.isEmpty(bean.getImageMedium()) && basicBean!=null && basicBean.getImage()!=null){
            imageUrl = basicBean.getImage().getImageSmall();
        }
        FrecoFactory.getInstance(context).displayWithoutHost(vh.productImage, imageUrl);
        double puq = bean.getProductUomQty();
        double dq = bean.getDeliveredQty();
        if ((Constant.ORDER_STATE_DONE.equals(status) || Constant.ORDER_STATE_RATED.equals(status)) && bean.getDeliveredQty() != bean.getProductUomQty()) {
            vh.oldPriceTv.setText("x" + NumberUtil.getIOrD(puq));
            vh.nowPriceTv.setText("x" + NumberUtil.getIOrD(dq));
            vh.oldPriceTv.setVisibility(View.VISIBLE);
        } else {
//            实发
            if (Constant.ORDER_STATE_PEISONG.equals(status) && bean.getActualSendNum() != bean.getProductUomQty()){
                vh.oldPriceTv.setText("x" + NumberUtil.getIOrD(puq));
                vh.oldPriceTv.setVisibility(View.VISIBLE);
            }else{
                vh.oldPriceTv.setVisibility(View.GONE);
            }
            vh.nowPriceTv.setText("x" + NumberUtil.getIOrD(bean.getActualSendNum()));
        }

        vh.name.setText(bean.getName());
        StringBuffer sb = new StringBuffer(bean.getDefaultCode());
        sb.append(" | ").append(bean.getUnit());
        boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        if (canSeePrice) {
            if (isTwoUnit) {
                sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductSettlePrice()))).append("元/").append(bean.getSettleUomId());
            } else {
                sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductPrice()))).append("元/").append(bean.getProductUom());
            }
        }
        if (!TextUtils.isEmpty(bean.getRemark())) {
            sb.append("\n备注：").append(bean.getRemark());
        }
        vh.unit1.setText(bean.getProductUom());
        vh.content.setText(sb.toString());
        if (isTwoUnit) {
            vh.weightTv.setText(bean.getSettleAmount() + bean.getSettleUomId() +"");
            vh.weightTv.setVisibility(View.VISIBLE);
        } else {
            vh.weightTv.setVisibility(View.INVISIBLE);
        }

        //发货状态订单
//        if("peisong".equals(status)) {
//        }
        return convertView;
    }

    public static class ViewHolder {
        public SimpleDraweeView productImage;
        public TextView name;
        public TextView content;
        public TextView oldPriceTv;
        public TextView nowPriceTv;
        public TextView weightTv;
        public TextView unit1;
        public View rootView;

        public ViewHolder(View itemView) {
            rootView = itemView;
            productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView) itemView.findViewById(R.id.content);
            oldPriceTv = (TextView) itemView.findViewById(R.id.oldPriceTv);
            nowPriceTv = (TextView) itemView.findViewById(R.id.nowPriceTv);
            weightTv = (TextView) itemView.findViewById(R.id.weightTv);
            unit1 = (TextView) itemView.findViewById(R.id.unit1);
        }
    }
}
