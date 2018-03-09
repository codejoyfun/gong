package com.runwise.supply.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.IBaseAdapter;
import com.runwise.supply.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mike on 2018/3/9.
 */

public class OrderStateAdapter extends IBaseAdapter<OrderStateAdapter.OrderStateBean> {

    String[] mTitles = new String[]{"全部状态", "待确认", "待发货", "待收货", "待评价", "已完成"};
    ArrayList<OrderStateBean> mOrderStateBeanList = new ArrayList<>();

    public OrderStateAdapter() {
        for (int i = 0; i < mTitles.length; i++) {
            OrderStateBean orderStateBean = new OrderStateBean();
            orderStateBean.state = mTitles[i];
            if (i == 0) {
                orderStateBean.select = true;
            }
            mOrderStateBeanList.add(orderStateBean);
        }
        setData(mOrderStateBeanList);
    }


    @Override
    public OrderStateBean getItem(int position) {
        return mOrderStateBeanList.get(position);
    }

    public void setSelect(int position){
        for (OrderStateBean orderStateBean:mOrderStateBeanList){
            orderStateBean.select = false;
        }
        mOrderStateBeanList.get(position).select = true;
    }


    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_order_state, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTvOrderState.setText(mOrderStateBeanList.get(position).state);
        viewHolder.mIvCheck.setVisibility(mOrderStateBeanList.get(position).select ? View.VISIBLE : View.GONE);
        viewHolder.mTvOrderState.setTextColor(mOrderStateBeanList.get(position).select ? Color.parseColor("#7bbd4f"):Color.parseColor("#595959"));
        return convertView;
    }


    public class OrderStateBean {
        public String state;
        public boolean select;
    }


    static class ViewHolder {
        @BindView(R.id.tv_order_state)
        TextView mTvOrderState;
        @BindView(R.id.iv_check)
        ImageView mIvCheck;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
