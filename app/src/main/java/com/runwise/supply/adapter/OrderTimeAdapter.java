package com.runwise.supply.adapter;

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

public class OrderTimeAdapter extends IBaseAdapter<OrderTimeAdapter.OrderStateBean> {
    String[] mTitles = new String[]{"全部时间", "本周", "上周", "自定义区间"};
    ArrayList<OrderStateBean> mOrderStateBeanList = new ArrayList<>();

    public OrderTimeAdapter() {
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

    public void setSelect(int position){
        for (OrderStateBean orderStateBean:mOrderStateBeanList){
            orderStateBean.select = false;
        }
        mOrderStateBeanList.get(position).select = true;
    }
    @Override
    public OrderStateBean getItem(int position) {
        return mOrderStateBeanList.get(position);
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
