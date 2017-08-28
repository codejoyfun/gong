package com.runwise.supply.firstpage;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.EvaluateLineRequest;
import com.runwise.supply.firstpage.entity.EvaluateRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateActivity extends NetWorkActivity implements EvaluateAdapter.RatingBarClickCallback, CheckBox.OnCheckedChangeListener {
    @ViewInject(R.id.indexLine)
    private View indexLine;
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    @ViewInject(R.id.cb1)
    private CheckBox cb1;
    @ViewInject(R.id.cb2)
    private CheckBox cb2;
    @ViewInject(R.id.cb3)
    private CheckBox cb3;
    @ViewInject(R.id.onekeyTv)
    private TextView onekeyTv;
    @ViewInject(R.id.serviceEt)
    private EditText serviceEt;
    @ViewInject(R.id.qualityEt)
    private EditText qualityEt;
    @ViewInject(R.id.serviceRb)
    private RatingBar serviceRb;
    @ViewInject(R.id.cbLL)
    private View cbLL;
    private static final int ORDERREQUST = 1;
    private static final int LINEREQUEST = 2;

    private EvaluateAdapter adapter;
    private OrderResponse.ListBean bean;
    //维护星星分数的集合,LineId -----> 星星分数
    private Map<Integer, Integer> rateMap = new HashMap<>();
    private PopupWindow popupWindow;
    private View popView;
    private int orderId;
    private int flag = 0;
    private final static String TIP1 = "迟到且无提前告知";
    private final static String TIP2 = "未确认收货就离开";
    private final static String TIP3 = "态度差";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.evaluate_layout);
        setTitleText(true, "评价");
        setTitleLeftIcon(true, R.drawable.nav_back);
        setTitleRightText(true, "提交");
        setDefaultDatas();
    }

    private void setDefaultDatas() {
        int tabWidth = (CommonUtils.getScreenWidth(this) - CommonUtils.dip2px(this, 30)) / 3;
        int translationX = (tabWidth - CommonUtils.dip2px(this, 51)) / 2 + CommonUtils.dip2px(this, 15);
        indexLine.setTranslationX(translationX);
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("order");
        adapter = new EvaluateAdapter(this, null, rateMap);
        adapter.setCallback(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        selectProductTypeData(DataType.LENGCANGHUO);
        if (bean != null) {
            orderId = bean.getOrderID();
            for (OrderResponse.ListBean.LinesBean lb : bean.getLines()) {
                rateMap.put(Integer.valueOf(lb.getSaleOrderProductID()), Integer.valueOf(0));
            }
            if (bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null) {
                String deliverName = bean.getWaybill().getDeliverUser().getName();
                String imgUrl = bean.getWaybill().getDeliverUser().getAvatarUrl();
            } else {
            }
            String estimatTime = bean.getEstimatedTime();
            String endUploadTime = bean.getStartUnloadDatetime();
            StringBuffer sb = new StringBuffer("预计送达时间 ");
            sb.append(estimatTime)
                    .append("   ")
                    .append("开始卸货时间 ")
                    .append(endUploadTime);
        }

        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        serviceRb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 5) {
                    cbLL.setVisibility(View.VISIBLE);
                } else {
                    cbLL.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.coldBtn, R.id.freezeBtn, R.id.dryBtn,
            R.id.title_iv_left, R.id.onekeyTv, R.id.title_tv_rigth})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.coldBtn:
                //切换指示器
                switchTabIndex(DataType.LENGCANGHUO);
                //筛选列表数据
                selectProductTypeData(DataType.LENGCANGHUO);
                break;
            case R.id.freezeBtn:
                switchTabIndex(DataType.FREEZE);
                selectProductTypeData(DataType.FREEZE);
                break;
            case R.id.dryBtn:
                switchTabIndex(DataType.DRY);
                selectProductTypeData(DataType.DRY);
                break;
            case R.id.title_iv_left:
                dialog.setMessage("评价尚未提交\n您确定要返回吗?");
                dialog.setMessageGravity();
                dialog.setLeftBtnListener("返回首页", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        finish();
                    }
                });
                dialog.setRightBtnListener("继续评价", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {

                    }
                });
                dialog.show();
                break;
            case R.id.onekeyTv:
                if (popupWindow == null) {
                    popView = LayoutInflater.from(this).inflate(R.layout.pop_rate_layout, null);
                    popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setFocusable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setOutsideTouchable(true);
                    RatingBar rb = (RatingBar) popView.findViewById(R.id.ratingbar);
                    rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            if (fromUser) {
                                //更新全部商品的
                                Iterator iterator = rateMap.keySet().iterator();
                                while (iterator.hasNext()) {
                                    Integer key = (Integer) iterator.next();
                                    rateMap.put(key, Integer.valueOf((int) rating));
                                }
                                adapter.notifyDataSetChanged();
                                popupWindow.dismiss();
                            }
                        }
                    });
                }
                if (!popupWindow.isShowing()) {
                    int paddingX = CommonUtils.dip2px(mContext, 100);
                    int paddingY = CommonUtils.dip2px(mContext, 10);
                    popupWindow.showAsDropDown(onekeyTv, -paddingX, -paddingY);
                } else {
                    popupWindow.dismiss();
                }
                break;
            case R.id.title_tv_rigth:
                dialog.setTitle("提示");
                dialog.setMessage("确认提交您的评价吗？");
                dialog.setRightBtnListener("提交", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送提交请求
                        sendEvaluateRequest();
                    }
                });
                dialog.show();
                break;
        }
    }

    private void sendEvaluateRequest() {
        showIProgressDialog();
        EvaluateRequest request = new EvaluateRequest();
        request.setQuality_evaluation(qualityEt.getText().toString());
        request.setService_evaluation(serviceEt.getText().toString());
        request.setService_score((int) serviceRb.getRating());
        StringBuffer sb = new StringBuffer("/gongfu/assess/order/");
        sb.append(orderId).append("/");
        sendConnection(sb.toString(), request, ORDERREQUST, false, BaseEntity.ResultBean.class);
        //对订单商品行的评价:gongfu/assess/order/line/
        EvaluateLineRequest lineRequest = new EvaluateLineRequest();
        List<EvaluateLineRequest.OrderBean> list = new ArrayList<>();
        Iterator iterator = rateMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = (Integer) iterator.next();
            EvaluateLineRequest.OrderBean ob = new EvaluateLineRequest.OrderBean();
            ob.setLine_id(key);
            ob.setQuality_score(rateMap.get(key));
            list.add(ob);
        }
        lineRequest.setOrder(list);
        sendConnection("/gongfu/assess/order/line/", lineRequest, LINEREQUEST, false, BaseEntity.ResultBean.class);
    }

    private void selectProductTypeData(DataType type) {
        List<OrderResponse.ListBean.LinesBean> list = bean.getLines();
        List<OrderResponse.ListBean.LinesBean> typeList = new ArrayList<>();
        for (OrderResponse.ListBean.LinesBean lb : list) {
            if (lb.getStockType().equals(type.getType())) {
                typeList.add(lb);
            }
        }
        adapter.setProductList(typeList);
    }

    private void switchTabIndex(DataType type) {
        int tabWidth = (CommonUtils.getScreenWidth(this) - CommonUtils.dip2px(this, 30)) / 3;
        int padding = (tabWidth - CommonUtils.dip2px(this, 51)) / 2;
        float translationX = 0.0F;
        switch (type) {
            case FREEZE:
                translationX = CommonUtils.dip2px(mContext, 15) + tabWidth + padding;
                break;
            case LENGCANGHUO:
                translationX = CommonUtils.dip2px(mContext, 15) + padding;
                break;
            case DRY:
                translationX = CommonUtils.dip2px(mContext, 15) + 2 * tabWidth + padding;
                break;
        }
        ViewPropertyAnimator.animate(indexLine).translationX(translationX);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case ORDERREQUST:
                flag++;
                break;
            case LINEREQUEST:
                flag++;
                break;

        }
        if (flag == 2) {
            dismissIProgressDialog();
            ToastUtil.show(mContext, "提交成功");
            finish();
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void rateChanged(Integer lineId, Integer rateScore) {
        rateMap.put(lineId, rateScore);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        StringBuffer sb = new StringBuffer();
        sb.append(serviceEt.getText().toString());
        switch (buttonView.getId()) {
            case R.id.cb1:
                if (isChecked) {
                    cb1.setTextColor(Color.parseColor("#9ACC35"));
                    if (sb.toString().length() > 0) {
                        sb.append("，");
                    }
                    sb.append(TIP1);
                    serviceEt.setText(sb.toString());
                    serviceEt.setSelection(sb.toString().length());
                } else {
                    cb1.setTextColor(Color.parseColor("#CCCCCC"));
                    if (sb.toString().contains(TIP1)) {
                        String newStr = sb.toString().replaceAll(TIP1, "");
                        serviceEt.setText(newStr);
                        serviceEt.setSelection(newStr.length());
                    }
                }

                break;
            case R.id.cb2:
                if (isChecked) {
                    cb2.setTextColor(Color.parseColor("#9ACC35"));
                    if (sb.toString().length() > 0) {
                        sb.append("，");
                    }
                    sb.append(TIP2);
                    serviceEt.setText(sb.toString());
                    serviceEt.setSelection(sb.toString().length());
                } else {
                    cb2.setTextColor(Color.parseColor("#CCCCCC"));
                    if (sb.toString().contains(TIP2)) {
                        String newStr = sb.toString().replaceAll(TIP2, "");
                        serviceEt.setText(newStr);
                        serviceEt.setSelection(newStr.length());
                    }
                }
                break;
            case R.id.cb3:
                if (isChecked) {
                    cb3.setTextColor(Color.parseColor("#9ACC35"));
                    if (sb.toString().length() > 0) {
                        sb.append("，");
                    }
                    sb.append(TIP3);
                    serviceEt.setText(sb.toString());
                    serviceEt.setSelection(sb.toString().length());
                } else {
                    cb3.setTextColor(Color.parseColor("#CCCCCC"));
                    if (sb.toString().contains(TIP3)) {
                        String newStr = sb.toString().replaceAll(TIP3, "");
                        serviceEt.setText(newStr);
                        serviceEt.setSelection(newStr.length());
                    }
                }
                break;
        }
    }
}
