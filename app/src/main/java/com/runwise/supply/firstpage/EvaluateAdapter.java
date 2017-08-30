package com.runwise.supply.firstpage;

import android.content.Context;
import android.graphics.Color;
import android.media.Rating;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
    //评价map,LineId ----> 星级
    private Map<Integer,Integer> map;
    private String filterTxt;          //需要着重显示的文本
    public EvaluateAdapter(Context context,List list,Map<Integer,Integer> map) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (list != null)
            productList.addAll(list);
        this.map = map;
    }
    public interface RatingBarClickCallback{
        //商品行id ---->对分数
       void rateChanged(Integer lineId,Integer rateScore);
    }
    private RatingBarClickCallback callback;

    public void setCallback(RatingBarClickCallback callback) {
        this.callback = callback;
    }

    public void setProductList(List<OrderResponse.ListBean.LinesBean> productList,String filterTxt) {
        this.productList.clear();
        this.filterTxt = filterTxt;
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
        final Integer lineId = bean.getSaleOrderProductID();
        ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(pId);
        if (basicBean != null){
            String name = basicBean.getName();
            if (!TextUtils.isEmpty(filterTxt)){
                //需要着重显示的
                int start = name.indexOf(filterTxt);
                SpannableStringBuilder builder = new SpannableStringBuilder(basicBean.getName());
                ForegroundColorSpan cs = new ForegroundColorSpan(Color.parseColor("#9ACC35"));
                builder.setSpan(cs,start,start+filterTxt.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                itemHolder.nameTv.setText(builder);
            }else{
                itemHolder.nameTv.setText(name);
            }
            itemHolder.numTv.setText(basicBean.getDefaultCode());

        }
        if (map != null && map.containsKey(lineId)){
            Integer rate = map.get(lineId);
            itemHolder.ratingBar.setRating(rate);
        }
        itemHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser){
                    //将当前星级信息给父activity
                    if (callback != null){
                        callback.rateChanged(lineId,Integer.valueOf((int)rating));
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
