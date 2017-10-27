package com.runwise.supply.firstpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderStateLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/7/16.
 */

public class StateAdatper extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private Context context;
    private List<OrderStateLine> traceList = new ArrayList();
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL= 0x0001;

    public StateAdatper(Context context, List list) {
        inflater = LayoutInflater.from(context);
        this.traceList = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.order_state_line_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        if (getItemViewType(position) == TYPE_TOP) {
            // 第一行头的竖线不显示
            itemHolder.tvTopLine.setVisibility(View.INVISIBLE);
            // 字体颜色加深
            itemHolder.orderStateTv.setTextColor(0xff6BB400);
            itemHolder.tvDot.setBackgroundResource(R.drawable.restaurant_orderstatus_point_highlight);
            itemHolder.tvDot.getLayoutParams().width = context.getResources().getDimensionPixelSize(R.dimen.state_green_dot);
            itemHolder.tvDot.getLayoutParams().height = context.getResources().getDimensionPixelSize(R.dimen.state_green_dot);
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            itemHolder.tvTopLine.setVisibility(View.VISIBLE);
            itemHolder.orderStateTv.setTextColor(0xff2E2E2E);
            itemHolder.tvDot.setBackgroundResource(R.drawable.timeline_dot_normal);
        }
        if (position == traceList.size() - 1){
            itemHolder.tvDownLine.setVisibility(View.INVISIBLE);
        }
        OrderStateLine osl = traceList.get(position);
        itemHolder.orderStateTv.setText(osl.getState());
        itemHolder.orderContentTv.setText(osl.getContent());
        itemHolder.stateTimeTv.setText(osl.getTime());
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return traceList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderStateTv, orderContentTv;
        public TextView stateTimeTv;
        public TextView tvTopLine, tvDot;
        public TextView tvDownLine;
        public ViewHolder(View itemView) {
            super(itemView);
            orderStateTv = (TextView) itemView.findViewById(R.id.orderStateTv);
            orderContentTv = (TextView) itemView.findViewById(R.id.orderContentTv);
            stateTimeTv = (TextView)itemView.findViewById(R.id.stateTimeTv);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);
            tvDownLine = (TextView)itemView.findViewById(R.id.tvDownLine);
        }
    }
}
