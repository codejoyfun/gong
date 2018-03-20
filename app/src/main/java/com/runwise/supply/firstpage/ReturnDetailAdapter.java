package com.runwise.supply.firstpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/8/3.
 */

public class ReturnDetailAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ReturnOrderBean.ListBean.LinesBean> returnList = new ArrayList<>();
    private LayoutInflater inflater;
    private boolean canSeePrice;
    public List<ReturnOrderBean.ListBean.LinesBean> getReturnList() {
        return returnList;
    }

    public ReturnDetailAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
    }

    public void setReturnList(List<ReturnOrderBean.ListBean.LinesBean> list) {
        this.returnList.clear();
        if (list != null){
            this.returnList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.orderdetail_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        ReturnOrderBean.ListBean.LinesBean lb = returnList.get(position);
        int pid = lb.getProductID();
        ProductBasicList.ListBean basiclb = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pid));
        if (basiclb != null){
            String imgUrl = basiclb.getImage().getImageMedium();
            FrecoFactory.getInstance(context).disPlay(viewHolder.sdv, Constant.BASE_URL+imgUrl);
            viewHolder.nameTv.setText(basiclb.getName());
            StringBuffer sb = new StringBuffer(basiclb.getDefaultCode());
            sb.append(" | ").append(basiclb.getUnit());
            if (canSeePrice){
                DecimalFormat df = new DecimalFormat("#.##");
                if (basiclb.isTwoUnit()){
                    sb.append("\n").append("¥").append(df.format(basiclb.getSettlePrice())).append("元/").append(basiclb.getSettleUomId());
                }else{
                    sb.append("\n").append("¥")
                            .append(df.format(basiclb.getPrice()))
                            .append("元/")
                            .append(basiclb.getUom());
                }
            }
            viewHolder.contentTv.setText(sb.toString());
            viewHolder.unit1.setText(basiclb.getUom());
        }
        int rCount = (int)lb.getDeliveredQty();
        viewHolder.countTV.setText("x"+rCount);

    }

    @Override
    public int getItemCount() {
        return returnList.size();
    }
    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTv;
        public SimpleDraweeView sdv;
        public TextView contentTv;
        public TextView countTV;
        public TextView unit1;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.name);
            sdv = (SimpleDraweeView)itemView.findViewById(R.id.productImage);
            contentTv = (TextView)itemView.findViewById(R.id.content);
            countTV = (TextView)itemView.findViewById(R.id.nowPriceTv);
            unit1 = (TextView)itemView.findViewById(R.id.unit1);
        }
    }
}
