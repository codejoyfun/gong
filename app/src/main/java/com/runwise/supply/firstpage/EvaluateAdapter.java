package com.runwise.supply.firstpage;

import android.content.Context;
import android.media.Rating;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private Context context;
    private List<OrderResponse.ListBean.LinesBean> productList = new ArrayList();
    //评价map,存productId ----> 星级
    private Map<String,Integer> map;
    public EvaluateAdapter(Context context,List list,Map<String,Integer> map) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (list != null)
            productList.addAll(list);
        this.map = map;
    }
    public interface RatingBarClickCallback{
       void rateChanged(String productId,Integer rateScore);
    }
    private RatingBarClickCallback callback;

    public void setCallback(RatingBarClickCallback callback) {
        this.callback = callback;
    }

    public void setProductList(List<OrderResponse.ListBean.LinesBean> productList) {
        this.productList.clear();
        if (productList != null){
            this.productList.addAll(productList);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.evaluate_list_item,parent,false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        OrderResponse.ListBean.LinesBean bean = productList.get(position);
        final String pId = String.valueOf(bean.getProductID());
        ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(pId);
        if (basicBean != null){
            itemHolder.numTv.setText(basicBean.getDefaultCode());
            itemHolder.nameTv.setText(basicBean.getName());
        }
        if (map != null && map.containsKey(pId)){
            Integer rate = map.get(pId);
            itemHolder.ratingBar.setRating(rate);
        }
        itemHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser){
                    //将当前星级信息给父activity
                    if (callback != null){
                        callback.rateChanged(pId,Integer.valueOf((int)rating));
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView numTv;
        public TextView nameTv;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            numTv = (TextView) itemView.findViewById(R.id.numTv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rateRb);

        }
    }
}
