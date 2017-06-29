package com.runwise.supply.firstpage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runwise.supply.R;

/**
 * Created by libin on 2017/6/27.
 */

public class StatisticsAdapter extends RecyclerView.Adapter {
    private static final double[] numValue = {52.8,5,2.7,2400,56};
    private static final String[] contontValue = {"销量增长量(万元)","门店增长量(个)","库存周转率(天)","临期实材额(元)","临期食材量(件)"};

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_item,parent,false);
        RelativeLayout rl1 = (RelativeLayout) view.findViewById(R.id.rl1);
        RelativeLayout rl2 = (RelativeLayout) view.findViewById(R.id.rl1);
        RelativeLayout rl3 = (RelativeLayout) view.findViewById(R.id.rl1);
        RelativeLayout[] rls = {rl1,rl2,rl3};
        for (View rl:rls) {
            rl.setVisibility(View.INVISIBLE);
        }
        rls[viewType].setVisibility(View.VISIBLE);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView titleTv;
        TextView contentTv;
        switch (position){
            case 0:
            case 1:
            case 2:
                titleTv = (TextView)(((ViewHolder)holder).rl1.findViewById(R.id.titleTv));
                contentTv = (TextView)(((ViewHolder)holder).rl1.findViewById(R.id.contentTv));
                titleTv.setText(String.valueOf(numValue[position]));
                contentTv.setText(contontValue[position]);
                break;
            case 3:
            case 4:
                titleTv = (TextView)(((ViewHolder)holder).rl2.findViewById(R.id.upTv));
                contentTv = (TextView)(((ViewHolder)holder).rl2.findViewById(R.id.downTv));
                titleTv.setText(String.valueOf(numValue[position]));
                contentTv.setText(contontValue[position]);
                break;
            case 5:
                //更多示例
                break;
        }

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        //四种样子,且最后一个据有点击事件
        if (position == 0 || position == 1){
            return 0;
        }else if(position ==2){
            return 1;
        }else if(position == 3 || position == 4){
            return 2;
        }else{
            return 3;
        }
    }
    public static class   ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout rl1;
        public RelativeLayout rl2;
        public RelativeLayout rl3;
        public ViewHolder(View itemView) {
            super(itemView);
            rl1 = (RelativeLayout)itemView.findViewById(R.id.rl1);
            rl2 = (RelativeLayout)itemView.findViewById(R.id.rl2);
            rl3 = (RelativeLayout)itemView.findViewById(R.id.rl3);
        }
    }
}
