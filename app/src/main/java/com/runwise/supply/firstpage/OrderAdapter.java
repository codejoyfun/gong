package com.runwise.supply.firstpage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.IBaseAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderState;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.tools.TimeUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderAdapter extends IBaseAdapter {
    protected static final int TYPE_ORDER = 0;        //正常订单
    protected static final int TYPE_RETURN = 1;       //退货单
    public interface DoActionInterface{
        void doAction(OrderDoAction action,int postion);
        void call(String phone);
    }
    private DoActionInterface callback;
    private Context context;
    private int returnCount;            //退货单数量
    private int orderCount;             //正常订单数量

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

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
        if (!expandMap.containsKey(Integer.valueOf(position))){
            expandMap.put(Integer.valueOf(position),Boolean.valueOf(false));
        }
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.firstpage_order_item, null);
            ViewUtils.inject(viewHolder,convertView);
            convertView.setTag(viewHolder);
            if (!GlobalApplication.getInstance().getCanSeePrice()){
                viewHolder.priceLL.setVisibility(View.GONE);
            }
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final LinearLayout tlLL = viewHolder.timelineLL;
        final ImageButton downArrow = viewHolder.arrowBtn;
        final RecyclerView recyclerView =  viewHolder.recyclerView;
        if (getItemViewType(position) == TYPE_ORDER){
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
                        setTimeLineContent(bean.getStateTracker(),recyclerView);
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
                    String doAction = ((TextView)v).getText().toString();
                    OrderDoAction action = OrderActionUtils.getDoActionByText(doAction,bean);
                    if (callback != null){
                        callback.doAction(action,position);
                    }

                }
            });
            if (expandMap.get(Integer.valueOf(position)) != null && expandMap.get(Integer.valueOf(position)).booleanValue()){
                viewHolder.timelineLL.setVisibility(View.VISIBLE);
                //重刷一次，免得重复
                downArrow.setImageResource(R.drawable.login_btn_dropup);
                setTimeLineContent(bean.getStateTracker(),recyclerView);
            }else{
                viewHolder.timelineLL.setVisibility(View.GONE);
                downArrow.setImageResource(R.drawable.login_btn_dropdown);
            }
            viewHolder.titleTv.setText(bean.getName());
            //派单前，派单后，用户收货后
            StringBuffer etSb = new StringBuffer();
            if (bean.getState().equals(OrderState.DRAFT.getName()) || bean.getState().equals(OrderState.SALE.getName())){
                etSb.append("预计").append(bean.getEstimatedDate()).append("送达");
            }else if(bean.getState().equals(OrderState.PEISONG.getName())){
                etSb.append("预计").append(bean.getEstimatedTime()).append("送达");
            }else{
                etSb.append(bean.getDoneDatetime()).append("已送达");
            }
            viewHolder.timeTv.setText(etSb.toString());
            viewHolder.stateTv.setText(OrderState.getValueByName(bean.getState()));
            viewHolder.stateTv.setTextColor(Color.parseColor("#333333"));
            if (bean.getWaybill() != null && bean.getWaybill() != null && bean.getWaybill().getDeliverVehicle() != null){
                viewHolder.carNumTv.setText(bean.getWaybill().getDeliverVehicle().getLicensePlate());
                viewHolder.driverLL.setVisibility(View.VISIBLE);
            }else{
                viewHolder.carNumTv.setText("未指派");
                viewHolder.driverLL.setVisibility(View.GONE);
            }
            if (bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null){
                viewHolder.senderTv.setText(bean.getWaybill().getDeliverUser().getName());
                viewHolder.senderTv.setVisibility(View.VISIBLE);
            }else{
                viewHolder.senderTv.setText("未指派");
                viewHolder.senderTv.setVisibility(View.GONE);
            }
            viewHolder.callIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean != null && bean.getWaybill() !=  null && bean.getWaybill().getDeliverUser()!= null
                            && bean.getWaybill().getDeliverUser().getMobile() != null){
                        callback.call(bean.getWaybill().getDeliverUser().getMobile());
                    }else{
                        callback.call(null);
                    }

                }
            });
            StringBuffer sb = new StringBuffer("共");
            sb.append((int)bean.getAmount()).append("件商品");
            viewHolder.countTv.setText(sb.toString());
            viewHolder.moneyTv.setText(bean.getAmountTotal()+"");
            StringBuffer drawableSb = new StringBuffer("state_restaurant_");
            drawableSb.append(bean.getState());
            if (getResIdByDrawableName(drawableSb.toString()) == 0){
                viewHolder.imgIv.setImageResource(R.drawable.state_restaurant_draft);
            }else{
                viewHolder.imgIv.setImageResource(getResIdByDrawableName(drawableSb.toString()));
            }
            String doString = OrderActionUtils.getDoBtnTextByState(bean);
            if (!TextUtils.isEmpty(doString)){
                if (doString.equals("已评价")){
                    viewHolder.doBtn.setVisibility(View.INVISIBLE);
                }else{
                    viewHolder.doBtn.setVisibility(View.VISIBLE);
                    viewHolder.doBtn.setText(doString);
                }
            }else{
                viewHolder.doBtn.setVisibility(View.INVISIBLE);
            }
        }else{
            final ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean) mList.get(position);
            //发货单
            viewHolder.imgIv.setImageResource(R.drawable.more_restaurant_returnrecord);
            viewHolder.titleTv.setText(bean.getName());
            viewHolder.stateTv.setText("退货中");
            viewHolder.stateTv.setTextColor(Color.parseColor("#FA694D"));
            viewHolder.timeTv.setText(TimeUtils.getMMddHHmm(bean.getCreateDate()));
            StringBuffer sb = new StringBuffer("共");
            sb.append((int)bean.getAmount()).append("件商品");
            viewHolder.countTv.setText(sb.toString());
            viewHolder.moneyTv.setText(bean.getAmountTotal()+"");
            viewHolder.doBtn.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(bean.getDriveMobile())){
                viewHolder.carNumTv.setText(bean.getVehicle());
                viewHolder.carNumTv.setVisibility(View.VISIBLE);
                viewHolder.callIb.setVisibility(View.VISIBLE);
                viewHolder.driverLL.setVisibility(View.VISIBLE);
            }else{
                viewHolder.carNumTv.setText("未指派");
                viewHolder.carNumTv.setVisibility(View.GONE);
                viewHolder.callIb.setVisibility(View.GONE);
                viewHolder.driverLL.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(bean.getDriver())){
                viewHolder.senderTv.setText(bean.getDriver());
            }else{
                viewHolder.senderTv.setText("未指派");
            }
            viewHolder.callIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(bean.getDriveMobile())){
                        callback.call(bean.getDriveMobile());
                    }else{
                        callback.call(null);
                    }
                }
            });
            viewHolder.arrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //更改boolean状态
                    Boolean isExpand = expandMap.get(Integer.valueOf(position)).booleanValue();
                    isExpand = !isExpand;
                    expandMap.put(Integer.valueOf(position),isExpand);
                    if (isExpand){
                        //只有点击时，才去放timeline的内容
                        setTimeLineContent(bean.getStateTracker(),recyclerView);
                        tlLL.setVisibility(View.VISIBLE);
                        downArrow.setImageResource(R.drawable.login_btn_dropup);
                    }else{
                        tlLL.setVisibility(View.GONE);
                        downArrow.setImageResource(R.drawable.login_btn_dropdown);
                    }

                }
            });
            if (expandMap.get(Integer.valueOf(position)) != null && expandMap.get(Integer.valueOf(position)).booleanValue()){
                viewHolder.timelineLL.setVisibility(View.VISIBLE);
                //重刷一次，免得重复
                downArrow.setImageResource(R.drawable.login_btn_dropup);
                setTimeLineContent(bean.getStateTracker(),recyclerView);
            }else{
                viewHolder.timelineLL.setVisibility(View.GONE);
                downArrow.setImageResource(R.drawable.login_btn_dropdown);
            }

        }


        return convertView;
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
        TextView doBtn;
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
        @ViewInject(R.id.priceLL)
        LinearLayout priceLL;
        @ViewInject(R.id.callIb)
        ImageButton callIb;
        @ViewInject(R.id.driverLL)
        LinearLayout driverLL;

    }

    private void setTimeLineContent(List<String> stList, RecyclerView recyclerView){
        TimeLineAdapter adapter = new TimeLineAdapter(context,stList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }
    private int getResIdByDrawableName(String name){
        ApplicationInfo appInfo = context.getApplicationInfo();
        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < returnCount){
            return TYPE_RETURN;
        }else{
            return TYPE_ORDER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
