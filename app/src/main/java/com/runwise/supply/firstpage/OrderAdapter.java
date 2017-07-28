package com.runwise.supply.firstpage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.IBaseAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderState;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderAdapter extends IBaseAdapter {
    public interface DoActionInterface{
        void doAction(OrderDoAction action,int postion);
    }
    private DoActionInterface callback;
    private Context context;

    public void setDoActionCallBack(DoActionInterface doActionCallBack) {
        this.callback = doActionCallBack;
    }

    //记录当前扩展打开的状态
    private HashMap<Integer,Boolean> expandMap = new HashMap<>();
    public OrderAdapter(Context context,DoActionInterface callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected View getExView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.firstpage_order_item, null);
            ViewUtils.inject(viewHolder,convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final LinearLayout tlLL = viewHolder.timelineLL;
        final ImageButton downArrow = viewHolder.arrowBtn;
        final RecyclerView recyclerView =  viewHolder.recyclerView;
        final OrderResponse.ListBean bean = (OrderResponse.ListBean) mList.get(position);
        viewHolder.arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更改boolean状态
                Boolean isExpand = expandMap.get(Integer.valueOf(position)).booleanValue();
                isExpand = !isExpand;
                expandMap.put(Integer.valueOf(position),isExpand);
                if (isExpand){
                    //只有点击时，才去放timeline的内容
                    setTimeLineContent(bean,recyclerView);
                    tlLL.setVisibility(View.VISIBLE);
                    downArrow.setImageResource(R.drawable.login_btn_dropup);
                }else{
                    tlLL.setVisibility(View.GONE);
                    downArrow.setImageResource(R.drawable.login_btn_dropdown);
                }

            }
        });
        viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据状态进行不同的逻辑处理
                OrderDoAction action = null;
                String doAction = ((Button)v).getText().toString();
                if ("取消订单".equals(doAction)){
                    action = OrderDoAction.CANCLE;
                }else if("点货".equals(doAction)){
                    action = OrderDoAction.TALLY;
                }else if("收货".equals(doAction)){
                    action = OrderDoAction.RECEIVE;
                }else if("上传支付凭证".equals(doAction)){
                    action = OrderDoAction.UPLOAD;
                }else if("评价".equals(doAction)){
                    action = OrderDoAction.RATE;
                }
                if (callback != null){
                    callback.doAction(action,position);
                }

            }
        });
        if (expandMap.get(Integer.valueOf(position)) != null && expandMap.get(Integer.valueOf(position)).booleanValue()){
            viewHolder.timelineLL.setVisibility(View.VISIBLE);
            //重刷一次，免得重复
            downArrow.setImageResource(R.drawable.login_btn_dropup);
            setTimeLineContent(bean,recyclerView);
        }else{
            viewHolder.timelineLL.setVisibility(View.GONE);
            downArrow.setImageResource(R.drawable.login_btn_dropdown);
        }
        if (!expandMap.containsKey(Integer.valueOf(position))){
            expandMap.put(Integer.valueOf(position),Boolean.valueOf(false));
        }
        viewHolder.titleTv.setText(bean.getName());
        viewHolder.timeTv.setText(bean.getEstimatedTime());
        viewHolder.stateTv.setText(OrderState.getValueByName(bean.getState()));
        if (bean.getWaybill() != null && bean.getWaybill() != null){
            viewHolder.carNumTv.setText(bean.getWaybill().getDeliverVehicle().getLicensePlate());
        }else{
            viewHolder.carNumTv.setText("未指派");
        }
        if (bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null){
            viewHolder.senderTv.setText(bean.getWaybill().getDeliverUser().getName());
        }else{
            viewHolder.senderTv.setText("未指派");
        }
        StringBuffer sb = new StringBuffer("共");
        sb.append(bean.getAmount()).append("件商品");
        viewHolder.countTv.setText(sb.toString());
        viewHolder.moneyTv.setText(bean.getAmountTotal()+"");
        StringBuffer drawableSb = new StringBuffer("state_restaurant_");
        drawableSb.append(bean.getState());
        if (getResIdByDrawableName(drawableSb.toString()) == 0){
            viewHolder.imgIv.setImageResource(R.drawable.state_restaurant_draft);
        }else{
            viewHolder.imgIv.setImageResource(getResIdByDrawableName(drawableSb.toString()));
        }
        String doString = getDoBtnTextByState(bean);
        if (!TextUtils.isEmpty(doString)){
            viewHolder.doBtn.setVisibility(View.VISIBLE);
            viewHolder.doBtn.setText(doString);
        }else{
            viewHolder.doBtn.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private String getDoBtnTextByState(OrderResponse.ListBean bean) {
        String btnText = null;
        if (bean.getState().equals(OrderState.DRAFT.getName())){
            btnText = "取消订单";
        }else if(bean.getState().equals(OrderState.SALE.getName())){
            //不做任务事情，返回null,隐藏此按钮
        }else if(bean.getState().equals(OrderState.PEISONG.getName())){
            if (bean.isIsDoubleReceive()){
                if (bean.isIsFinishTallying()){
                    btnText = "收货";
                }else{
                    btnText = "点货";
                }
            }else{
                btnText = "收货";
            }

        }else if(bean.getState().equals(OrderState.DONE.getName())){
            btnText = "上传支付凭证";
        }else if(bean.getState().equals(OrderState.RATED.getName())){
            btnText = "评价";
        }
        return btnText;
    }

    public class ViewHolder{
        @ViewInject(R.id.img)
        ImageView imgIv;
        @ViewInject(R.id.orderNumTv)
        TextView titleTv;
        @ViewInject(R.id.orderTimeTv)
        TextView timeTv;
        @ViewInject(R.id.countTv)
        TextView countTv;
        @ViewInject(R.id.moneyTv)
        TextView moneyTv;
        @ViewInject(R.id.stateTv)
        TextView stateTv;
        @ViewInject(R.id.doBtn)
        Button doBtn;
        @ViewInject(R.id.arrowBtn)
        ImageButton arrowBtn;
        @ViewInject(R.id.carNumTv)
        TextView carNumTv;
        @ViewInject(R.id.senderTv)
        TextView senderTv;
        @ViewInject(R.id.timelineLL)
        LinearLayout timelineLL;
        @ViewInject(R.id.recyclerView)
        RecyclerView recyclerView;
    }

    private void setTimeLineContent(OrderResponse.ListBean bean,RecyclerView recyclerView){
        ArrayList<String> list = (ArrayList<String>) bean.getStateTracker();
        TimeLineAdapter adapter = new TimeLineAdapter(context,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }
    private int getResIdByDrawableName(String name){
        ApplicationInfo appInfo = context.getApplicationInfo();
        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }
}
