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
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderStateLine;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;

/**
 * Created by libin on 2017/7/16.
 */

public class OrderStateActivity extends NetWorkActivity {
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    private StateAdatper adatper;
    private List<OrderStateLine> datas = new ArrayList<>();
    private String deliverPhone;  //默认没有
    private BottomDialog dialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener(){
                @Override
                public void bindView(View v) {
                    initViews(v);
                }
            }).setLayoutRes(R.layout.state_line_bottom_dialog)
            .setCancelOutside(false)
            .setDimAmount(0.5f);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.order_state_line_layout);
        setTitleText(true,"订单状态");
        setTitleLeftIcon(true,R.drawable.nav_back);
        setTitleRigthIcon(true,R.drawable.nav_contract);
        //从上个页面获取数据
        setOrderTracker();
    }
    @OnClick({R.id.title_iv_rigth,R.id.title_iv_left})
    private void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_rigth:
                if (!dialog.isVisible()){
                    dialog.show();
                }
                break;
            case R.id.title_iv_left:
                finish();
                break;
        }
    }
    private void initViews(View view){
        Button serviceBtn = (Button) view.findViewById(R.id.serviceBtn);
        Button deliverBtn = (Button) view.findViewById(R.id.deliverBtn);
        Button onlineServiceBtn = (Button) view.findViewById(R.id.onlineServiceBtn);
        Button cancleBtn = (Button) view.findViewById(R.id.cancleBtn);
        if (TextUtils.isEmpty(deliverPhone)){
            deliverBtn.setVisibility(View.GONE);
        }
        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + "02037574563");
                intent.setData(data);
                startActivity(intent);
            }
        });
        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + deliverPhone);
                intent.setData(data);
                startActivity(intent);
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void setOrderTracker() {
        Bundle bundle = getIntent().getExtras();
        OrderResponse.ListBean bean = bundle.getParcelable("order");
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
                timeSb.append(pieces[0]).append(pieces[1]);
            }
            osl.setState(state);
            osl.setTime(timeSb.toString());
            if (str.contains("已评价")){
                content.append("评价员：").append(bean.getAppraisalUserName())
                        .append(",如有问题请联系客服");
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
                            .append("\n").append("配送员：").append(bean.getWaybill().getDeliverUser().getName())
                            .append(bean.getWaybill().getDeliverUser().getMobile()).append("\n")
                            .append("预计到达时间：").append(bean.getEstimatedTime());
                }else{
                    content.append("暂未分配");
                }
            }else if(str.contains("已确认")){
                content.append("正在为您挑拣商品");
            }else if(str.contains("已修改")){
                content.append("共").append(bean.getDeliveredQty()).append("件商品,")
                        .append("¥").append(bean.getAmountTotal());
            }else if(str.contains("已提交")){
                content.append("订单号：").append(bean.getName());
            }
            osl.setContent(content.toString());
            datas.add(osl);
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
}
