package com.runwise.supply.firstpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.LotListActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.List;

import static com.kids.commonframe.config.Constant.ORDER_STATE_DRAFT;
import static com.kids.commonframe.config.Constant.ORDER_STATE_PEISONG;
import static com.kids.commonframe.config.Constant.ORDER_STATE_SALE;

/**
 * Created by libin on 2017/7/15.
 */

public class OrderDtailAdapter extends RecyclerView.Adapter{
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

    public OrderDtailAdapter(Context context) {
        this.context = context;
    }

    public void setProductList(List<OrderResponse.ListBean.LinesBean> productList) {
        this.productList.clear();
        if (productList != null && productList.size() > 0){
            this.productList.addAll(productList);
        }
        notifyDataSetChanged();
    }

    public void setStatus(String orderName,String status,OrderResponse.ListBean listBean) {
        this.status = status;
        this.orderName = orderName;
        mListBean = listBean;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetail_list_item,parent,false);
        ViewHolder vh = new ViewHolder(view);
        vh.oldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mListBean.isNewType()){//新版订单信息
            bindNewType(holder,position);
            return;
        }
        //旧版订单信息
        final OrderResponse.ListBean.LinesBean bean = productList.get(position);
        int pId = bean.getProductID();
        ViewHolder vh = (ViewHolder)holder;
        final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pId));
        if (basicBean != null && basicBean.getImage() != null){
            FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL+basicBean.getImage().getImageSmall());
        }
        int puq = (int)bean.getProductUomQty();
        int dq = (int)bean.getDeliveredQty();
        if((Constant.ORDER_STATE_DONE.equals(status)||Constant.ORDER_STATE_RATED.equals(status)) && bean.getDeliveredQty() != bean.getProductUomQty()) {
            vh.oldPriceTv.setText("x"+puq);
            vh.nowPriceTv.setText("x"+dq);
            vh.oldPriceTv.setVisibility(View.VISIBLE);
        }
        else{
            vh.oldPriceTv.setVisibility(View.GONE);
            vh.nowPriceTv.setText("x"+puq);
        }

        if (basicBean != null){
            vh.name.setText(basicBean.getName());
            StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
            sb.append("  ").append(basicBean.getUnit());
            boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
            if (canSeePrice){
                if (isTwoUnit){
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(basicBean.getSettlePrice()))).append("元/").append(basicBean.getSettleUomId());
                }else{
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(basicBean.getPrice()))).append("元/").append(bean.getProductUom());
                }
            }
            vh.unit1.setText(bean.getProductUom());
            vh.content.setText(sb.toString());
            if (isTwoUnit){
                vh.weightTv.setText(bean.getSettleAmount()+basicBean.getSettleUomId());
                vh.weightTv.setVisibility(View.VISIBLE);
            }else{
                vh.weightTv.setVisibility(View.INVISIBLE);
            }
        }else{
            vh.name.setText("");
        }
        //发货状态订单
//        if("peisong".equals(status)) {
        vh.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String deliveryType = mListBean.getDeliveryType();
                if (deliveryType.equals(OrderResponse.ListBean.TYPE_STANDARD)||deliveryType.equals(OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY)
                        ||deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH)||deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_THIRD_PART_DELIVERY)){
                    if(!status.equals("peisong")&&!status.equals("done")&&!status.equals("rated")){
                        return;
                    }
                }
                if (deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_VENDOR_DELIVERY)||deliveryType.equals(OrderResponse.ListBean.TYPE_VENDOR_DELIVERY)){
                    if((status.equals("done")||status.equals("rated"))&&(bean.getLotList()!=null&&bean.getLotList().size() == 0)) {
                        ToastUtil.show(v.getContext(), "该产品无批次追踪");
                        return;
                    }
                    if (status.equals(ORDER_STATE_PEISONG)||status.equals(ORDER_STATE_DRAFT)||status.equals(ORDER_STATE_SALE)){
                        return;
                    }
                }
                Intent intent = new Intent(context, LotListActivity.class);
                intent.putExtra("title",basicBean.getName());
                intent.putExtra("bean", (Parcelable) bean);
                context.startActivity(intent);
            }
        });
//        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public static class   ViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView productImage;
        public TextView name;
        public TextView content;
        public TextView oldPriceTv;
        public TextView nowPriceTv;
        public TextView weightTv;
        public TextView unit1;
        public View rootView;
        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView)itemView.findViewById(R.id.content);
            oldPriceTv = (TextView)itemView.findViewById(R.id.oldPriceTv);
            nowPriceTv = (TextView)itemView.findViewById(R.id.nowPriceTv);
            weightTv = (TextView)itemView.findViewById(R.id.weightTv);
            unit1 = (TextView)itemView.findViewById(R.id.unit1);
        }
    }

    /**
     * 新版的订单不用另外查商品信息，直接在订单接口返回下单时保存的商品信息
     * @param holder
     * @param position
     */
    private void bindNewType(RecyclerView.ViewHolder holder, int position){
        final OrderResponse.ListBean.LinesBean bean = productList.get(position);
        ViewHolder vh = (ViewHolder)holder;
        FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL+bean.getImageMedium());

        int puq = (int)bean.getProductUomQty();
        int dq = (int)bean.getDeliveredQty();
        if((Constant.ORDER_STATE_DONE.equals(status)||Constant.ORDER_STATE_RATED.equals(status)) && bean.getDeliveredQty() != bean.getProductUomQty()) {
            vh.oldPriceTv.setText("x"+puq);
            vh.nowPriceTv.setText("x"+dq);
            vh.oldPriceTv.setVisibility(View.VISIBLE);
        }
        else{
            vh.oldPriceTv.setVisibility(View.GONE);
            vh.nowPriceTv.setText("x"+puq);
        }

        vh.name.setText(bean.getName());
            StringBuffer sb = new StringBuffer(bean.getDefaultCode());
            sb.append("  ").append(bean.getUnit());
            boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
            if (canSeePrice){
                if (isTwoUnit){
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductSettlePrice()))).append("元/").append(bean.getSettleUomId());
                }else{
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductPrice()))).append("元/").append(bean.getProductUom());
                }
            }
            vh.unit1.setText(bean.getProductUom());
            vh.content.setText(sb.toString());
            if (isTwoUnit){
                vh.weightTv.setText(bean.getSettleAmount()+ " " +bean.getSettleUomId());
                vh.weightTv.setVisibility(View.VISIBLE);
            }else{
                vh.weightTv.setVisibility(View.INVISIBLE);
            }

        //发货状态订单
        vh.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deliveryType = mListBean.getDeliveryType();
                if (deliveryType.equals(OrderResponse.ListBean.TYPE_STANDARD)||deliveryType.equals(OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY)
                        ||deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH)||deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_THIRD_PART_DELIVERY)){
                    if(!status.equals("peisong")&&!status.equals("done")&&!status.equals("rated")){
                        return;
                    }
                }
                if (deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_VENDOR_DELIVERY)||deliveryType.equals(OrderResponse.ListBean.TYPE_VENDOR_DELIVERY)){
                    if((status.equals("done")||status.equals("rated"))&&(bean.getLotList()!=null&&bean.getLotList().size() == 0)) {
                        ToastUtil.show(v.getContext(), "该产品无批次追踪");
                        return;
                    }
                    if (status.equals(ORDER_STATE_PEISONG)||status.equals(ORDER_STATE_DRAFT)||status.equals(ORDER_STATE_SALE)){
                        return;
                    }
                }
                Intent intent = new Intent(context, LotListActivity.class);
                intent.putExtra("title",bean.getName());
                intent.putExtra("bean", (Parcelable) bean);
                context.startActivity(intent);
            }
        });
    }
}
