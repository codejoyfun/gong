package com.runwise.supply.firstpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runwise.supply.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/7/14.
 */

public class TimeLineAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private List<String> traceList = new ArrayList();
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL= 0x0001;

    public TimeLineAdapter(Context context, List list) {
        inflater = LayoutInflater.from(context);
        this.traceList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.firstpage_timeline_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        if (getItemViewType(position) == TYPE_TOP) {
            // 第一行头的竖线不显示
            itemHolder.tvTopLine.setVisibility(View.INVISIBLE);
            // 字体颜色加深
            itemHolder.tvAcceptTime.setTextColor(0xff666666);
            itemHolder.tvAcceptStation.setTextColor(0xff666666);
            itemHolder.tvDot.setBackgroundResource(R.drawable.timeline_dot_first);
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            itemHolder.tvTopLine.setVisibility(View.VISIBLE);
            itemHolder.tvAcceptTime.setTextColor(0xff999999);
            itemHolder.tvAcceptStation.setTextColor(0xff999999);
            itemHolder.tvDot.setBackgroundResource(R.drawable.timeline_dot_normal);
        }
        if (position == traceList.size() - 1){
            itemHolder.tvDownLine.setVisibility(View.INVISIBLE);
        }
        String timeLine = traceList.get(position);
        String[] sections = timeLine.split(" ");
        if (sections.length >= 3){
            String time = sections[0]+" "+sections[1];
            String state = sections[2];
            itemHolder.tvAcceptTime.setText(time);
            itemHolder.tvAcceptStation.setText(state);
        }

    }

    @Override
    public int getItemCount() {
        return traceList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        }
        return TYPE_NORMAL;
    }
    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAcceptTime, tvAcceptStation;
        public TextView tvTopLine, tvDot;
        public TextView tvDownLine;
        public ViewHolder(View itemView) {
            super(itemView);
            tvAcceptTime = (TextView) itemView.findViewById(R.id.tvAcceptTime);
            tvAcceptStation = (TextView) itemView.findViewById(R.id.tvAcceptStation);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);
            tvDownLine = (TextView)itemView.findViewById(R.id.tvDownLine);
        }
    }
}
