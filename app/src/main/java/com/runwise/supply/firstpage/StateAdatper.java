package com.runwise.supply.firstpage;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderStateProductAdapter;
import com.runwise.supply.firstpage.entity.OrderResponse;
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

    public interface CallBack{
        void onAction();
    }

    public CallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    CallBack mCallBack;

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

        if (osl.getContent().contains("查看差异")){
            itemHolder.orderContentTv.setHighlightColor(context.getResources().getColor(android.R.color.transparent));
            SpannableString spanableInfo = new SpannableString(osl.getContent());



            spanableInfo.setSpan(new Clickable(clickListener),osl.getContent().length()-4,osl.getContent().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            itemHolder.orderContentTv.setText(spanableInfo);
            itemHolder.orderContentTv.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            itemHolder.orderContentTv.setText(osl.getContent());
        }
        itemHolder.stateTimeTv.setText(osl.getTime());
        List<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> alterProducts  = osl.getAlterProducts();
        if (alterProducts != null && alterProducts.size() > 0){
            itemHolder.rvProductList.setVisibility(View.VISIBLE);
            itemHolder.rvProductList.setLayoutManager(new LinearLayoutManager(context));
            OrderStateProductAdapter orderStateProductAdapter = new OrderStateProductAdapter(alterProducts);
            itemHolder.rvProductList.setAdapter(orderStateProductAdapter);
        }else{
            itemHolder.rvProductList.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemViewType(int position) {
        OrderStateLine osl = traceList.get(position);
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
        public RecyclerView rvProductList;
        public ViewHolder(View itemView) {
            super(itemView);
            orderStateTv = (TextView) itemView.findViewById(R.id.orderStateTv);
            orderContentTv = (TextView) itemView.findViewById(R.id.orderContentTv);
            stateTimeTv = (TextView)itemView.findViewById(R.id.stateTimeTv);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);
            tvDownLine = (TextView)itemView.findViewById(R.id.tvDownLine);
            rvProductList = (RecyclerView)itemView.findViewById(R.id.rv_product_list);
        }
    }
    class Clickable extends ClickableSpan {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        /**
         * 重写父类点击事件
         */
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }

        /**
         * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
         */
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.order_state_clickable));
        }
    }
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCallBack != null){
                mCallBack.onAction();
            }
        }
    };
}
