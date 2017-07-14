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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderAdapter extends IBaseAdapter {
    private Context context;
    //记录当前扩展打开的状态
    private HashMap<Integer,Boolean> expandMap = new HashMap<>();
    public OrderAdapter(Context context) {
        this.context = context;
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
                }else{
                    tlLL.setVisibility(View.GONE);
                }
            }
        });
        //TODO:
        viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据状态进行不同的逻辑处理
            }
        });
        if (expandMap.get(Integer.valueOf(position)) != null && expandMap.get(Integer.valueOf(position)).booleanValue()){
            viewHolder.timelineLL.setVisibility(View.VISIBLE);
            //重刷一次，免得重复
            setTimeLineContent(bean,recyclerView);
        }else{
            viewHolder.timelineLL.setVisibility(View.GONE);
        }
        if (!expandMap.containsKey(Integer.valueOf(position))){
            expandMap.put(Integer.valueOf(position),Boolean.valueOf(false));
        }
        viewHolder.titleTv.setText(bean.getName());
        viewHolder.timeTv.setText(bean.getEstimatedTime());
        viewHolder.stateTv.setText(State.getValueByName(bean.getState()));
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
        if (bean.getState().equals(State.DRAFT.getName())){
            btnText = "取消订单";
        }else if(bean.getState().equals(State.SALE.getName())){
            //不做任务事情，返回null,隐藏此按钮
        }else if(bean.getState().equals(State.PEISONG.getName())){
            if (bean.isIsDoubleReceive()){
                if (bean.isIsFinishTallying()){
                    btnText = "收货";
                }else{
                    btnText = "点货";
                }
            }else{
                btnText = "收货";
            }

        }else if(bean.getState().equals(State.DONE.getName())){
            btnText = "上传支付凭证";
        }else if(bean.getState().equals(State.RATED.getName())){
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
    public enum State{
        DRAFT("draft","待确认"),SALE("sale","已确认"),PEISONG("peisong","已发货"),
        DONE("done","已收货"),RATED("rated","已评价"),CANCEL("cancel","已取消");
        private String name;
        private String value;
        private State(String name,String value){
            this.name = name;
            this.value = value;
       }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        public static String getValueByName(String name){
            for(State s : State.values()){
                if (s.getName().equals(name)){
                    return s.getValue();
                }
            }
            return "未知状态";
        }
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
