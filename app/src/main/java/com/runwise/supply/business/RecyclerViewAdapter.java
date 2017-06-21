package com.runwise.supply.business;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.util.CommonUtils;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.DealerEnity;
import com.runwise.supply.tools.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/1/2.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List datas = null;
    Activity context = null;
    private OnRecyclerViewItemClickListener mItemClickListener = null;
    private DealerEnity enity;
    public void setOnItemClickerListener(OnRecyclerViewItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public RecyclerViewAdapter(List datas, Activity context){
        this.datas = datas;
        this.context = context;
    }
    //创建新View,被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dealeritem,parent,false);
        LinearLayout dealerLL = (LinearLayout)view.findViewById(R.id.dealerid);
        dealerLL.setLayoutParams(new LinearLayout.LayoutParams(CommonUtils.getScreenWidth(context)-110, 400));
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(this);
        Button btn = (Button) view.findViewById(R.id.button);
        btn.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        enity = (DealerEnity)datas.get(position);
        ((ViewHolder)holder).name.setText(enity.getDealer_name());
        ((ViewHolder)holder).address.setText(enity.getAddress());
        ((ViewHolder)holder).ratingBar.setRating(enity.getScore());
        ((ViewHolder)holder).score.setText(enity.getScore()+"分");
        ((ViewHolder)holder).km.setText(enity.getDistance());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class   ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView address;
        public Button   button;
        public RatingBar ratingBar;
        public TextView score;
        public TextView km;
        public ViewHolder(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.dealername);
            address = (TextView) itemView.findViewById(R.id.address);
            button = (Button)itemView.findViewById(R.id.button);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingbar);
            score = (TextView)itemView.findViewById(R.id.ratingscore);
            km = (TextView)itemView.findViewById(R.id.km);
        }

    }
    public static interface OnRecyclerViewItemClickListener{
        void onItemClick(View view , DealerEnity dataModel);
        void onCallClick(DealerEnity dataModel);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button){
            mItemClickListener.onCallClick(enity);
        }else {
            mItemClickListener.onItemClick(view,enity);
        }
    }
}
