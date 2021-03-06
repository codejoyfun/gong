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
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.entity.OrderDetailResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderStateLine;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.message.MessageDetailActivity;
import com.runwise.supply.message.entity.MessageResult;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;
import me.shaohui.bottomdialog.BottomDialog;

import static com.runwise.supply.firstpage.StateAdatper.DIFF_TYPE_DELIVERY;
import static com.runwise.supply.firstpage.StateAdatper.DIFF_TYPE_RECEIVE;

/**
 * Created by libin on 2017/7/16.
 */

public class OrderStateActivity extends NetWorkActivity implements View.OnClickListener {
    private static final int REQUEST_ORDER_DETAIL = 1;
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    private StateAdatper adatper;
    private List<OrderStateLine> datas = new ArrayList<>();
    private String deliverPhone;        //默认没有
    private boolean isReturnMode;       //退货单模式,默认是处于订单模式下
    UserInfo userInfo;
    private BottomDialog bDialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener() {
                @Override
                public void bindView(View v) {
                    initDialogViews(v);
                }
            }).setLayoutRes(R.layout.state_line_bottom_dialog)
            .setCancelOutside(false)
            .setDimAmount(0.5f);
    //不管是订单或是退货单，给这个bean赋值，便于调到,在线客服页面。
    private MessageResult.OrderBean orderBean = new MessageResult.OrderBean();
    public static String INTENT_KEY_ORDER_ID = "intent_key_order_id";

    private int mOrderId;
    public static final int DEFAULT_INVALID_ORDER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.order_state_line_layout);
        mOrderId = getIntent().getIntExtra(INTENT_KEY_ORDER_ID, DEFAULT_INVALID_ORDER_ID);
        isReturnMode = getIntent().getBooleanExtra("mode", false);
        if (isReturnMode) {
            setTitleText(true, "退货单状态");
        } else {
            setTitleText(true, "订单状态");
        }
        setTitleLeftIcon(true, R.drawable.nav_back);
        setTitleRigthIcon(true, R.drawable.nav_service_message);
        //从上个页面获取数据
        if (mOrderId == DEFAULT_INVALID_ORDER_ID) {
            Bundle bundle = getIntent().getExtras();
            Object data = bundle.getParcelable("order");
            setOrderTracker(data);
        } else {
            getOrderDetail(mOrderId);
        }
        userInfo = SampleApplicationLike.getInstance().loadUserInfo();
    }

    private void getOrderDetail(int orderId) {
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/order/");
        sb.append(orderId).append("/");
        sendConnection(sb.toString(), request, REQUEST_ORDER_DETAIL, false, OrderDetailResponse.class);
    }


    @OnClick({R.id.title_iv_rigth, R.id.title_iv_left})
    private void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_rigth:
                if (!bDialog.isVisible()) {
                    bDialog.show();
                }
                break;
            case R.id.title_iv_left:
                finish();
                break;
        }
    }

    private void initDialogViews(View view) {
        Button serviceBtn = (Button) view.findViewById(R.id.serviceBtn);
        Button deliverBtn = (Button) view.findViewById(R.id.deliverBtn);
        Button onlineServiceBtn = (Button) view.findViewById(R.id.onlineServiceBtn);
        Button cancleBtn = (Button) view.findViewById(R.id.cancleBtn);
        if (TextUtils.isEmpty(deliverPhone)) {
            deliverBtn.setVisibility(View.GONE);
        }
        serviceBtn.setOnClickListener(this);
        deliverBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        onlineServiceBtn.setOnClickListener(this);
    }

    private void setOrderTracker(Object data) {
        if (isReturnMode) {
            ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean) data;
            orderBean.setId(bean.getReturnOrderID());
            orderBean.setAmount((int) bean.getAmount());
            orderBean.setState(bean.getState());
            orderBean.setName(bean.getName());
            orderBean.setDeliveryType(bean.getDeliveryType());
            orderBean.setCreate_date(bean.getCreateDate());
            orderBean.setAmount_total(bean.getAmountTotal());
            List<String> trackers = bean.getStateTracker();
//            List<String> trackers = this.getIntent().getStringArrayListExtra("tracker");
            for (String str : trackers) {
                OrderStateLine osl = new OrderStateLine();
                String[] pieces = str.split(" ");
                StringBuffer timeSb = new StringBuffer();
                String state = "";
                StringBuffer content = new StringBuffer();
                if (pieces.length >= 3) {
                    state = pieces[2];
                    if (pieces[0].length() == 10) {
                        timeSb.append(pieces[0].substring(5)).append(" ").append(pieces[1]);
                    } else {
                        timeSb.append(pieces[0]).append(" ").append(pieces[1]);
                    }
                }
                osl.setState(state);
                osl.setTime(timeSb.toString());
                switch (bean.getState()){
                    case "process":
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
                            if (SampleApplicationLike.getInstance().getCanSeePrice()) {
                                content.append("，共").append(bean.getAmountTotal()).append("元").append("\n");
                            }
                        }
                        break;
                    case "done":
                        //退货成功
                        content.append("退货商品：").append(NumberUtil.getIOrD(bean.getAmount())).append("件");
                        if (SampleApplicationLike.getInstance().getCanSeePrice()) {
                            content.append("，").append(bean.getAmountTotal()).append("元");
                        }
                        break;
                    case "draft":
                        content.append("退货单号：").append(bean.getName()).append("\n")
                                .append("退货商品：").append(NumberUtil.getIOrD(bean.getAmount())).append("件");
                        if (SampleApplicationLike.getInstance().getCanSeePrice()) {
                            content.append("，共").append(bean.getAmountTotal()).append("元").append("\n");
                        }
                        break;
                }
                osl.setContent(content.toString());
                datas.add(osl);
            }

        } else {
            OrderResponse.ListBean bean = (OrderResponse.ListBean) data;
            orderBean.setId(bean.getOrderID());
            orderBean.setAmount((int) bean.getAmount());
            orderBean.setAmount_total(bean.getAmountTotal());
            orderBean.setState(bean.getState());
            orderBean.setName(bean.getName());
            orderBean.setEstimated_time(bean.getEstimatedTime());
            orderBean.setDeliveryType(bean.getDeliveryType());
            if (bean.getWaybill() != null) {
                MessageResult.OrderBean.WaybillBean wb = new MessageResult.OrderBean.WaybillBean();
                wb.setId(bean.getWaybill().getWaybillID());
                orderBean.setWaybill(wb);
            }
            //从bean里面拼各种内容
            if (bean == null) {
                ToastUtil.show(mContext, "网络异常，请退出重新加载");
                return;
            }
            ArrayList<String> trackers = (ArrayList<String>) bean.getStateTracker();
            for (int i = 0; i < trackers.size(); i++) {
                String str = trackers.get(i);
                OrderStateLine osl = new OrderStateLine();
                String[] pieces = str.split(" ");
                StringBuffer timeSb = new StringBuffer();
                String state = "";
                StringBuffer content = new StringBuffer();
                if (pieces.length >= 3) {
                    state = pieces[2];
                    if (pieces[0].length() == 10) {
                        timeSb.append(pieces[0].substring(5)).append(" ").append(pieces[1]);
                    } else {
                        timeSb.append(pieces[0]).append(" ").append(pieces[1]);
                    }
                }
                osl.setState(state);
                osl.setTime(timeSb.toString());
                osl.setTimeStamp(TimeUtils.getTimeStampByYMDHM(pieces[0] + " " + pieces[1]));
                if (str.contains("已评价")) {
                    content.append("评价员：").append(bean.getAppraisalUserName());
                } else if (str.contains("已收货")) {
                    content.append("收货员：").append(bean.getReceiveUserName());
                } else if (str.contains("已点货")) {
                    content.append("点货员：").append(bean.getTallyingUserName());
                } else if (str.contains("已发货")) {

                    if (bean.getWaybill() != null && bean.getWaybill().getDeliverVehicle() != null) {
                        if (bean.getWaybill().getDeliverUser().getMobile() != null) {
                            deliverPhone = bean.getWaybill().getDeliverUser().getMobile();
                        }
                        content.append("车牌号：");
                        content.append(bean.getWaybill().getDeliverVehicle().getLicensePlate())
                                .append("\n").append("配送员：").append(bean.getWaybill().getDeliverUser().getName()).append(" ")
                                .append(bean.getWaybill().getDeliverUser().getMobile()).append("\n")
                                .append("预计到达时间：").append(bean.getEstimatedTime());
                    }
                    if (bean.isActualSendOrder()) {
                        content.append("\n").append("查看差异");
                    }
                } else if (str.contains("已确认")) {
                    content.append("正在为您挑拣商品");
                } else if (str.contains("已修改")) {
                    content.append("共").append(NumberUtil.getIOrD(bean.getAmount())).append("件商品");
                    if (SampleApplicationLike.getInstance().getCanSeePrice()) {
                        content.append("，¥").append(bean.getAmountTotal());
                    }
                } else if (str.contains("已提交")) {
                    content.append("订单号：").append(bean.getName());
                } else if (str.contains("已通知供应商")) {
                    content.append("已通知供应商发货，请耐心等待");
                }
                osl.setContent(content.toString());
                datas.add(osl);
            }
            insertProductAlteredStatus(datas, (OrderResponse.ListBean) data);
        }

        adatper = new StateAdatper(mContext, sortStatusByTime(datas));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adatper);
        adatper.setCallBack(new StateAdatper.CallBack() {
            @Override
            public void onAction(int type,int position) {
                switch (type){
                    case DIFF_TYPE_RECEIVE:
                        startActivity(OrderProductDiffActivity.getStartIntent(getActivityContext(), new ArrayList<>(datas.get(position).getAlterProducts())));
                        break;
                    case DIFF_TYPE_DELIVERY:
                        startActivity(OrderProductDiffActivity.getStartIntent(getActivityContext(), (OrderResponse.ListBean) data));
                        break;
                }
            }
        });
    }

    private void insertProductAlteredStatus(List<OrderStateLine> datas, OrderResponse.ListBean orderBean) {
        List<OrderResponse.ListBean.ProductAlteredBean> productAlteredBeanList = orderBean.getProductAltered();
        if (productAlteredBeanList == null || productAlteredBeanList.size() == 0) {
            return;
        }
        for (int i = productAlteredBeanList.size() - 1; i >= 0; i--) {
            OrderResponse.ListBean.ProductAlteredBean productAlteredBean = productAlteredBeanList.get(i);
            OrderStateLine osl = new OrderStateLine();
            String state = "订单收货数量已被修改";
            String alterDate = productAlteredBean.getAlterDate();
            String timeSb = alterDate.substring(5);
            String content = "修改人:" + productAlteredBean.getAlterUserName();
            content = content + "\n" + "查看差异";

            osl.setState(state);
            osl.setTime(timeSb);
            osl.setContent(content);
            osl.setTimeStamp(TimeUtils.getTimeStampByYMDHM(alterDate));
            osl.setAlterProducts(productAlteredBean.getAlterProducts());
            datas.add(osl);
        }
    }

    private List<OrderStateLine> sortStatusByTime(List<OrderStateLine> datas) {
        OrderStateLine[] orderStateLineList = new OrderStateLine[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            OrderStateLine orderStateLine = datas.get(i);
            orderStateLineList[i] = orderStateLine;
        }
        Arrays.sort(orderStateLineList, new Comparator<OrderStateLine>() {
            @Override
            public int compare(OrderStateLine o1, OrderStateLine o2) {
                if (o1.getTimeStamp() == o2.getTimeStamp()) {
                    return 1;
                }
                return o1.getTimeStamp() < o2.getTimeStamp() ? 1 : -1;
            }
        });
        datas.clear();
        for (int j = 0; j < orderStateLineList.length; j++) {
            datas.add(orderStateLineList[j]);
        }
        return datas;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_ORDER_DETAIL:
                BaseEntity.ResultBean resultBean = result.getResult();
                OrderDetailResponse response = (OrderDetailResponse) resultBean.getData();
                OrderResponse.ListBean orderBean = response.getOrder();
                setOrderTracker(orderBean);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    CustomDialog dialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.serviceBtn:
                if (dialog == null) {
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
                Intent dealIntent = new Intent(mContext, MessageDetailActivity.class);
                dealIntent.putExtra("orderBean", orderBean);
                startActivity(dealIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("订单状态页");
        MobclickAgent.onResume(this);          //统计时长
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("订单状态页");
        MobclickAgent.onPause(this);          //统计时长
    }
}
