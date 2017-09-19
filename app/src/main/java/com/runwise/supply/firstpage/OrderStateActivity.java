package com.runwise.supply.firstpage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderStateLine;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.message.MessageDetailActivity;
import com.runwise.supply.message.entity.MessageResult;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;
import me.shaohui.bottomdialog.BottomDialog;

/**
 * Created by libin on 2017/7/16.
 */

public class OrderStateActivity extends NetWorkActivity implements View.OnClickListener{
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    private StateAdatper adatper;
    private List<OrderStateLine> datas = new ArrayList<>();
    private String deliverPhone;        //默认没有
    private boolean isReturnMode;       //退货单模式,默认是处于订单模式下
    UserInfo userInfo;
    private BottomDialog bDialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener(){
                @Override
                public void bindView(View v) {
                    initDialogViews(v);
                }
            }).setLayoutRes(R.layout.state_line_bottom_dialog)
            .setCancelOutside(false)
            .setDimAmount(0.5f);
    //不管是订单或是退货单，给这个bean赋值，便于调到,在线客服页面。
    private MessageResult.OrderBean orderBean = new MessageResult.OrderBean();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.order_state_line_layout);
        isReturnMode = getIntent().getBooleanExtra("mode",false);
        if (isReturnMode){
            setTitleText(true,"退货单状态");
        }else{
            setTitleText(true,"订单状态");
        }
        setTitleLeftIcon(true,R.drawable.nav_back);
        setTitleRigthIcon(true,R.drawable.nav_service_message);
        //从上个页面获取数据
        setOrderTracker();
        userInfo = GlobalApplication.getInstance().loadUserInfo();
    }
    @OnClick({R.id.title_iv_rigth,R.id.title_iv_left})
    private void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_rigth:
                if (!bDialog.isVisible()){
                    bDialog.show();
                }
                break;
            case R.id.title_iv_left:
                finish();
                break;
        }
    }
    private void initDialogViews(View view){
        Button serviceBtn = (Button) view.findViewById(R.id.serviceBtn);
        Button deliverBtn = (Button) view.findViewById(R.id.deliverBtn);
        Button onlineServiceBtn = (Button) view.findViewById(R.id.onlineServiceBtn);
        Button cancleBtn = (Button) view.findViewById(R.id.cancleBtn);
        if (TextUtils.isEmpty(deliverPhone)){
            deliverBtn.setVisibility(View.GONE);
        }
        serviceBtn.setOnClickListener(this);
        deliverBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        onlineServiceBtn.setOnClickListener(this);
    }
    private void setOrderTracker() {
        Bundle bundle = getIntent().getExtras();
        if (isReturnMode){
            ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean) bundle.getSerializable("order");
            orderBean.setId(bean.getReturnOrderID());
            orderBean.setAmount((int)bean.getAmount());
            orderBean.setState(bean.getState());
            orderBean.setName(bean.getName());
            orderBean.setDeliveryType(bean.getDeliveryType());
            orderBean.setCreate_date(bean.getCreateDate());
            orderBean.setAmount_total(bean.getAmountTotal());
//            List<String> trackers = bean.getStateTracker();
            List<String> trackers = this.getIntent().getStringArrayListExtra("tracker");
            for (String str : trackers) {
                OrderStateLine osl = new OrderStateLine();
                String[] pieces = str.split(" ");
                StringBuffer timeSb = new StringBuffer();
                String state = "";
                StringBuffer content = new StringBuffer();
                if (pieces.length >= 3) {
                    state = pieces[2];
                    if (pieces[0].length() == 10){
                        timeSb.append(pieces[0].substring(5)).append(" ").append(pieces[1]);
                    }else{
                        timeSb.append(pieces[0]).append(" ").append(pieces[1]);
                    }
                }
                osl.setState(state);
                osl.setTime(timeSb.toString());
                if (bean.getState().equals("process")) {
                    //退货中...
                    if ((bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_STANDARD) || bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_FRESH)) && !TextUtils.isEmpty(bean.getDriver()) && !TextUtils.isEmpty(bean.getDriveMobile())) {
                        //订单审核通过
                        content.append("请等待取货员上门取货").append("\n")
                                .append("车牌号：").append(bean.getVehicle()).append("\n")
                                .append("取货员：").append(bean.getDriver()).append("\n");
                    } else {
                        //订单已提交
                        content.append("退货单号：").append(bean.getName()).append("\n")
                                .append("退货商品：").append(NumberUtil.getIOrD(bean.getAmount())).append("件");
                        if (GlobalApplication.getInstance().getCanSeePrice()){
                            content.append("，共").append(bean.getAmountTotal()).append("元").append("\n");
                        }
                    }
                } else {
                    //退货成功
                    content.append("退货商品：").append(NumberUtil.getIOrD(bean.getAmount())).append("件");
                    if (GlobalApplication.getInstance().getCanSeePrice()){
                        content.append("，").append(bean.getAmountTotal()).append("元");
                    }

                }
                osl.setContent(content.toString());
                datas.add(osl);
            }

        }else{
            OrderResponse.ListBean bean = bundle.getParcelable("order");
            orderBean.setId(bean.getOrderID());
            orderBean.setAmount((int) bean.getAmount());
            orderBean.setAmount_total(bean.getAmountTotal());
            orderBean.setState(bean.getState());
            orderBean.setName(bean.getName());
            orderBean.setEstimated_time(bean.getEstimatedTime());
            orderBean.setDeliveryType(bean.getDeliveryType());
            if (bean.getWaybill() != null){
                MessageResult.OrderBean.WaybillBean wb = new MessageResult.OrderBean.WaybillBean();
                wb.setId(bean.getWaybill().getWaybillID());
                orderBean.setWaybill(wb);
            }
            //从bean里面拼各种内容
            if (bean == null){
                ToastUtil.show(mContext,"网络异常，请退出重新加载");
                return;
            }
            ArrayList<String> trackers = (ArrayList<String>) bean.getStateTracker();
            for (String str : trackers){
                OrderStateLine osl = new OrderStateLine();
                String[] pieces = str.split(" ");
                StringBuffer timeSb = new StringBuffer();
                String state = "";
                StringBuffer content = new StringBuffer();
                if (pieces.length >= 3){
                    state = pieces[2];
                    if (pieces[0].length() == 10){
                        timeSb.append(pieces[0].substring(5)).append(" ").append(pieces[1]);
                    }else{
                        timeSb.append(pieces[0]).append(" ").append(pieces[1]);
                    }

                }
                osl.setState(state);
                osl.setTime(timeSb.toString());
                if (str.contains("已评价")){
                    content.append("评价员：").append(bean.getAppraisalUserName());
                }else if(str.contains("已收货")){
                    content.append("收货员：").append(bean.getReceiveUserName());
                }else if(str.contains("已点货")){
                    content.append("点货员：").append(bean.getTallyingUserName());
                }else if(str.contains("已发货")){
                    content.append("车牌号：");
                    if (bean.getWaybill() != null && bean.getWaybill().getDeliverVehicle() != null ){
                        if (bean.getWaybill().getDeliverUser().getMobile() != null){
                            deliverPhone = bean.getWaybill().getDeliverUser().getMobile();
                        }
                        content.append(bean.getWaybill().getDeliverVehicle().getLicensePlate())
                                .append("\n").append("配送员：").append(bean.getWaybill().getDeliverUser().getName()).append(" ")
                                .append(bean.getWaybill().getDeliverUser().getMobile()).append("\n")
                                .append("预计到达时间：").append(bean.getEstimatedTime());
                    }else{
                        content.append("暂未分配");
                    }
                }else if(str.contains("已确认")){
                    content.append("正在为您挑拣商品");
                }else if(str.contains("已修改")){
                    content.append("共").append(NumberUtil.getIOrD(bean.getAmount())).append("件商品");
                    if (GlobalApplication.getInstance().getCanSeePrice()){
                        content.append("，¥").append(bean.getAmountTotal());
                    }
                }else if(str.contains("已提交")){
                    content.append("订单号：").append(bean.getName());
                }else if(str.contains("已通知供应商")){
                    content.append("已通知供应商发货，请耐心等待");
                }
                osl.setContent(content.toString());
                datas.add(osl);
            }
        }

        adatper = new StateAdatper(mContext,datas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adatper);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    CustomDialog dialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.serviceBtn:
                if (dialog == null){
                    dialog = new CustomDialog(getActivityContext());
                }
                String number = "";
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getCompanyHotLine())) {
                    number = userInfo.getCompanyHotLine();
                    dialog.setTitle("致电 " + userInfo.getCompany() + " 客服");
                } else {
                    dialog.setTitle("致电 供鲜生 客服");
                }
                dialog.setModel(CustomDialog.BOTH);
                dialog.setMessageGravity();
                dialog.setMessage(number);
                dialog.setLeftBtnListener("取消", null);
                final String finalNumber = number;
                dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        CommonUtils.callNumber(mContext, finalNumber);
                    }
                });
                dialog.show();
                bDialog.dismiss();
                break;
            case R.id.deliverBtn:
                Intent intent2 = new Intent(Intent.ACTION_DIAL);
                Uri data2 = Uri.parse("tel:" + deliverPhone);
                intent2.setData(data2);
                startActivity(intent2);
                bDialog.dismiss();
                break;
            case R.id.cancleBtn:
                bDialog.dismiss();
                break;
            case R.id.onlineServiceBtn:
                //跳转到在线客服
                Intent dealIntent = new Intent(mContext,MessageDetailActivity.class);
                dealIntent.putExtra("orderBean",orderBean);
                startActivity(dealIntent);
                break;
        }
    }
}
