package com.runwise.supply.firstpage;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/7/15.
 */

public class OrderDtailAdapter extends RecyclerView.Adapter{
    private Context context;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetail_list_item,parent,false);
        ViewHolder vh = new ViewHolder(view);
        vh.oldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderResponse.ListBean.LinesBean bean = productList.get(position);
        int pId = bean.getProductID();
        ViewHolder vh = (ViewHolder)holder;
        ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap().get(String.valueOf(pId));
        if (basicBean != null && basicBean.getImage() != null){
            FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL+basicBean.getImage().getImageSmall());
        }
        int puq = (int)bean.getProductUomQty();
        int dq = (int)bean.getDeliveredQty();
        vh.oldPriceTv.setText("x"+dq);
        vh.nowPriceTv.setText("x"+puq);
        vh.weightTv.setText(bean.getSettleAmount()+"");
        if (basicBean != null){
            vh.name.setText(basicBean.getName());
            StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
            sb.append("  ").append(basicBean.getUnit()).append("\n").append(bean.getPriceUnit()).append("元/").append(bean.getProductUom());
            vh.content.setText(sb.toString());
        }else{
            vh.name.setText("");
        }

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
        public ViewHolder(View itemView) {
            super(itemView);
            productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView)itemView.findViewById(R.id.content);
            oldPriceTv = (TextView)itemView.findViewById(R.id.oldPriceTv);
            nowPriceTv = (TextView)itemView.findViewById(R.id.nowPriceTv);
            weightTv = (TextView)itemView.findViewById(R.id.weightTv);
        }
    }
}
