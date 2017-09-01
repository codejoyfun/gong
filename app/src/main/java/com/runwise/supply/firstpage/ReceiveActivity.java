package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.ReceiveProEvent;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.BatchEntity;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReceiveBean;
import com.runwise.supply.firstpage.entity.ReceiveRequest;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.OrderUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.UpdateRepertory;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.NoScrollViewPager;
import com.socketmobile.capture.Capture;
import com.socketmobile.capture.client.CaptureClient;
import com.socketmobile.capture.client.CaptureDeviceClient;
import com.socketmobile.capture.events.DataDecodedEvent;
import com.socketmobile.capture.events.DeviceAvailabilityEvent;
import com.socketmobile.capture.types.DecodedData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.Log;

import static com.runwise.supply.firstpage.EditBatchActivity.INTENT_KEY_BATCH_ENTITIES;
import static com.runwise.supply.firstpage.EditBatchActivity.INTENT_KEY_PRODUCT;

/**
 * Created by libin on 2017/7/16.
 */

public class ReceiveActivity extends NetWorkActivity implements DoActionCallback, CaptureClient.Listener {
    private static final int RECEIVE = 100;           //收货
    private static final int TALLYING = 200;          //点货
    private static final int DONE_TALLY = 300;          //第二个人完成点货
    private static final int BEGIN_TALLY = 400;           //开始点货
    private static final int END_TALLY = 500;           //退出点货
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private NoScrollViewPager viewPager;
    @ViewInject(R.id.pbBar)
    private ProgressBar pbBar;
    @ViewInject(R.id.pbValue)
    private TextView pbValue;
    private TabPageIndicatorAdapter adapter;
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    private PopupWindow mPopWindow;     //底部弹出
    private PopupWindow mPopWindow2;    //双单位的弹框
    private View dialogView;            //弹窗的view
    private View dialogView2;           //双单位的弹框view
    //做为底部弹窗数据传值
    private ReceiveBean bottomData;
    //存放收货对应数据记录productId -----> ReceiveBean
    private Map<String, ReceiveBean> countMap = new HashMap<>();
    private TextView mTvProductName;
    private TextView mTvSerialNumber;
    private TextView mTvSpecifications;
    private EditText mEtBatchNumber;
    private TextView mTvProductDateValue;
    private TextView mTvProductDate;
    private EditText mEtProductAmount;
    private SimpleDraweeView productImage;
    private int totalQty;           //预计总的商品总数
    private OrderResponse.ListBean lbean;
    private CaptureClient mClient;
    TimePickerView pvCustomTime;
    WheelView wheelView;

    private TextView titleTv;
    private AppCompatEditText edEt;
    private TextView unitTv;
    private EditText unitValueTv;
    private RelativeLayout twoUnitRL;



    public Map<String, ReceiveBean> getCountMap() {
        return countMap;
    }

    private int devicesConnected = -1;
    private int mode;               //当前页面的形态，总共三种：1点货，2双人收货，0正常收货
    private boolean isSettle;       //是不是双单位订单

    public boolean isSettle() {
        return isSettle;
    }

    static final int REQUEST_CODE_ADD_BATCH = 1 << 0;

    public boolean ShuangRensShouHuoQueRen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.receive_layout);
        Bundle bundle = getIntent().getExtras();
        lbean = bundle.getParcelable("order");
        isSettle = lbean.isIsTwoUnit();
        mode = bundle.getInt("mode");
        if (mode == 1) {
            setTitleText(true, "点货");
            String tallyingUserName = lbean.getTallyingUserName();
            if (!TextUtils.isEmpty(tallyingUserName)){
                if (!tallyingUserName.equals(GlobalApplication.getInstance().getUserName())){
                    ShuangRensShouHuoQueRen = true;
                    //双人收货模式下的确认人(不能编辑,只能确认)
                    for (OrderResponse.ListBean.LinesBean linesBean:lbean.getLines()){
                        ReceiveBean receiveBean = new ReceiveBean();
                        receiveBean.setProductId(linesBean.getProductID());
                        receiveBean.setCount((int) linesBean.getTallyingAmount());
                        final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(linesBean.getProductID()));
                        receiveBean.setTracking(basicBean.getTracking());
                        if (basicBean.getTracking().equals("lot")) {
                            List<ReceiveRequest.ProductsBean.LotBean> lot_list =  new ArrayList<>();
                            for (OrderResponse.ListBean.LinesBean.LotListBean lotListBean:linesBean.getLotList()){
                                ReceiveRequest.ProductsBean.LotBean lotBean = new ReceiveRequest.ProductsBean.LotBean();
                                lotBean.setLot_name(lotListBean.getName());
                                lotBean.setHeight((int) lotListBean.getHeight());
                                lotBean.setQty((int) lotListBean.getQty());
                                lotBean.setLife_datetime(lotListBean.getLife_datetime());
                                lotBean.setProduce_datetime(lotListBean.getProduce_datetime());
                                lot_list.add(lotBean);
                            }
                            receiveBean.setLot_list(lot_list);
                        }
                        countMap.put(String.valueOf(linesBean.getProductID()),receiveBean);
                    }
                }
            }
        } else {
            setTitleText(true, "收货");
        }
        setTitleLeftIcon(true, R.drawable.nav_back);
        setTitleRightText(true, "完成");
        if (lbean != null && lbean.getLines() != null) {
            datas.addAll(lbean.getLines());
        }
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        setDefalutProgressBar();
        initPopWindow();
        initPopWindow2();       //单独初始化一下双单位的popview
        Capture.init(getApplicationContext());
//        mClient = new CaptureClient();
//        mClient.setListener(this);
//        mClient.connect();
        if (mode == 1) {
            //开始点货
            String userName = GlobalApplication.getInstance().getUserName();
            if (TextUtils.isEmpty(userName) || !userName.equals(lbean.getTallyingUserName())) {
                startOrEndTally(true);
            }
        }
    }

    public String getDeliveryType() {
        return lbean.getDeliveryType();
    }

    @Override
    protected void onStop() {
        super.onStop();
//         client = Capture.get();
//         client.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * Alternatively, you can register individual activities to be Capture enabled. Just don't
         * forget to unregister your activity in the corresponding onPause/onStop method.
         */
//         client = Capture.get();
//         client.connect();

    }

    private void initPopWindow() {
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_product, null);
        RelativeLayout rl = (RelativeLayout) dialogView.findViewById(R.id.dialog_main);
        mPopWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopWindow.setContentView(dialogView);
        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
//        fitPopupWindowOverStatusBar(true);  //全屏
        mTvProductName = (TextView) dialogView.findViewById(R.id.tv_product_name);
        productImage = (SimpleDraweeView) dialogView.findViewById(R.id.productImage);
        mEtBatchNumber = (EditText) dialogView.findViewById(R.id.et_batch_number);
        mTvProductDateValue = (TextView) dialogView.findViewById(R.id.tv_product_date_value);
        mTvProductDate = (TextView) dialogView.findViewById(R.id.tv_product_date);

        mTvSerialNumber = (TextView) dialogView.findViewById(R.id.tv_serial_number);
        mTvSpecifications = (TextView) dialogView.findViewById(R.id.tv_specifications);
        mEtProductAmount = (EditText) dialogView.findViewById(R.id.et_product_amount);

        mTvProductDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pvCustomTime == null) {
                    pvCustomTime = new TimePickerView.Builder(ReceiveActivity.this, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {//选中事件回调
                            mTvProductDateValue.setText(TimeUtils.getYMD(date));
                            mTvProductDate.setText(wheelView.getAdapter().getItem(wheelView.getCurrentItem()).toString());
                        }
                    }).setLayoutRes(R.layout.custom_time_picker, new CustomListener() {

                        @Override
                        public void customLayout(View v) {
                            final Button btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
                            Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
                            wheelView = (WheelView) v.findViewById(R.id.options);
                            ArrayList<String> stringArrayList = new ArrayList<String>();
                            stringArrayList.add("生产日期");
                            stringArrayList.add("到期日期");
                            wheelView.setAdapter(new ArrayWheelAdapter(stringArrayList));
                            wheelView.setCyclic(false);
                            wheelView.setTextSize(18);
                            wheelView.setLineSpacingMultiplier(1.6F);

                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvCustomTime.returnData();
                                    pvCustomTime.dismiss();
                                }
                            });
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvCustomTime.dismiss();
                                }
                            });
                        }
                    })
                            .setType(new boolean[]{true, true, true, false, false, false})
                            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                            .build();
                    pvCustomTime.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(Object o) {
                            View rootview = LayoutInflater.from(ReceiveActivity.this).inflate(R.layout.receive_layout, null);
                            mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
                        }
                    });
                }
                pvCustomTime.show();
                mPopWindow.dismiss();

            }
        });

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof RelativeLayout) {
                    mPopWindow.dismiss();
                }
            }
        });
        TextView sureBtn = (TextView) dialogView.findViewById(R.id.finalButton);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pId = String.valueOf(bottomData.getProductId());
                String productAmountString = mEtProductAmount.getText().toString();
                int count;
                if (TextUtils.isEmpty(productAmountString)) {
                    count = 0;
                } else {
                    count = Integer.valueOf(mEtProductAmount.getText().toString());
                }
                bottomData.setCount(count);
//                bottomData.setLot_name(mEtBatchNumber.getText().toString());
                if (wheelView == null || wheelView.getCurrentItem() == 0) {
//                    bottomData.setProduct_datetime(mTvProductDateValue.getText().toString());
//                    bottomData.setLife_datetime("");
                } else {
//                    bottomData.setProduct_datetime("");
//                    bottomData.setLife_datetime(mTvProductDateValue.getText().toString());
                }
                countMap.put(pId, bottomData);
                mPopWindow.dismiss();
                //更新进度条
                updatePbProgress();
                //更新fragment列表内容
                ReceiveProEvent receiveProEvent = new ReceiveProEvent();
                receiveProEvent.setNotifyDataSetChange(true);
                EventBus.getDefault().post(receiveProEvent);
            }
        });
    }
    private void initPopWindow2(){
        dialogView2 = LayoutInflater.from(this).inflate(R.layout.dialog_edit_layout2,null);
        RelativeLayout rl = (RelativeLayout) dialogView2.findViewById(R.id.dialog_main);
        mPopWindow2 = new PopupWindow(dialogView2, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
        mPopWindow2.setContentView(dialogView2);
        mPopWindow2.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow2.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow2.setFocusable(true);
        mPopWindow2.setOutsideTouchable(true);
//        fitPopupWindowOverStatusBar(true);  //全屏
        ImageButton input_minus = (ImageButton)dialogView2.findViewById(R.id.input_minus);
        ImageButton input_add = (ImageButton)dialogView2.findViewById(R.id.input_add);
        titleTv = (TextView)dialogView2.findViewById(R.id.title);
        edEt  = (AppCompatEditText)dialogView2.findViewById(R.id.acet);
        unitTv = (TextView)dialogView2.findViewById(R.id.unitTv);
        unitValueTv = (EditText)dialogView2.findViewById(R.id.unitValue);
        twoUnitRL = (RelativeLayout)dialogView2.findViewById(R.id.twoUnitRL);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof RelativeLayout){
                    mPopWindow2.dismiss();
                }
            }
        });
        input_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.valueOf(edEt.getText().toString());
                if (count > 0){
                    count--;
                    edEt.setText(String.valueOf(count));
                    edEt.setSelection(String.valueOf(String.valueOf(count)).length());
                }

            }
        });
        input_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.valueOf(edEt.getText().toString());
                count++;
                edEt.setText(String.valueOf(count));
                edEt.setSelection(String.valueOf(String.valueOf(count)).length());
            }
        });
        Button sureBtn = (Button) dialogView2.findViewById(R.id.sureBtn);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pId = String.valueOf(bottomData.getProductId());
                int count = Integer.valueOf(edEt.getText().toString());
                double settleCount = TextUtils.isEmpty(unitValueTv.getText().toString()) ? 0 : Double.valueOf(unitValueTv.getText().toString());
                bottomData.setCount(count);
                bottomData.setTwoUnitValue(settleCount);    //双单位的值
                countMap.put(pId,bottomData);
                mPopWindow2.dismiss();
                //更新进度条
                updatePbProgress();
                //更新fragment列表内容
                EventBus.getDefault().post(new ReceiveProEvent(true));
            }
        });

    }

    private void updatePbProgress() {
        int pbNum = 0;
        for (ReceiveBean bean : countMap.values()) {
            pbNum += bean.getCount();
        }
        pbBar.setProgress(pbNum);
        pbValue.setText("已收" + pbNum + "/" + totalQty + "件商品");
    }

    private void setDefalutProgressBar() {
        for (OrderResponse.ListBean.LinesBean bean : datas) {
            totalQty += bean.getProductUomQty();
        }
        pbValue.setText("已收0/" + totalQty + "件商品");
        pbBar.setMax(totalQty);
        pbBar.setProgress(0);
    }

    @OnClick({R.id.title_iv_left, R.id.title_tv_rigth})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                if (mode == 1) {
                    dialog.setTitle("提示");
                    dialog.setMessage("确认取消点货?");
                    dialog.setMessageGravity();
                    dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {
                            startOrEndTally(false);

                        }
                    });
                    dialog.show();
                } else {
                    finish();
                }
                break;
            case R.id.title_tv_rigth:
                //如果每样商品数量都匹配，则提示:确认收货与订单数量一致?
                //如果每样商品数量不一致，则提示:收货数量与订单不一致，是否确认收货?
                String tip = "确认收货与订单数量一致?";
                if (!isSameReceiveCount()) {
                    tip = "收货数量与订单不一致，是否确认收货?";
                }
                if (mode == 2) {
                    tip = "确认完成收货?";
                }
                dialog.setTitle("提示");
                dialog.setMessageGravity();
                dialog.setMessage(tip);
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //完成收货/点货／双人收货
                        if (mode == 0) {
                            if (isSettle && !lbean.getDeliveryType().equals("fresh_vendor_delivery")){
                                receiveProductRequest2();
                            }else{
                                receiveProductRequest();
                            }
                        } else if (mode == 1) {
                            tallyRequest();
                        } else if (mode == 2) {
                            tallyDoneRequest();
                        }

                    }
                });
                dialog.show();
                break;
        }
    }

    private void startOrEndTally(boolean isBegin) {
        showIProgressDialog();
        Object request = null;
        StringBuffer sb = new StringBuffer("/api/order/");
        sb.append(lbean.getOrderID());
        if (isBegin) {
            sb.append("/tallying/begin/");
            sendConnection(sb.toString(), request, BEGIN_TALLY, true, BaseEntity.ResultBean.class);
        } else {
            sb.append("/tallying/cancel/");
            sendConnection(sb.toString(), request, END_TALLY, true, BaseEntity.ResultBean.class);
        }
    }

    private void tallyDoneRequest() {
//        URL	http://develop.runwise.cn/gongfu/order/600/tallying/done/
        StringBuffer sb = new StringBuffer("/gongfu/order/");
        sb.append(lbean.getOrderID()).append("/tallying/done/");
        sendConnection(sb.toString(), new Object(), DONE_TALLY, true, BaseEntity.ResultBean.class);
    }

    private void receiveProductRequest() {
        ReceiveRequest rr = new ReceiveRequest();
        List<ReceiveRequest.ProductsBean> pbList = new ArrayList<>();
        for (ReceiveBean bean : countMap.values()) {
            ReceiveRequest.ProductsBean pb = new ReceiveRequest.ProductsBean();
            pb.setProduct_id(bean.getProductId());
            pb.setHeight(bean.getCount());
            pb.setQty(bean.getCount());
            pb.setTracking(bean.getTracking());
            if (bean.getTracking().equals("lot")) {
                pb.setLot_list(bean.getLot_list());
            }
            pbList.add(pb);
        }
        rr.setProducts(pbList);
        StringBuffer sb = new StringBuffer("/gongfu/order/");
        sb.append(lbean.getOrderID()).append("/receive/");
        sendConnection(sb.toString(), rr, RECEIVE, true, BaseEntity.ResultBean.class);

    }
    //双单位收货的请求
    private void receiveProductRequest2() {
        ReceiveRequest rr = new ReceiveRequest();
        List<ReceiveRequest.ProductsBean> pbList = new ArrayList<>();
        for (ReceiveBean bean : countMap.values()){
            ReceiveRequest.ProductsBean pb = new ReceiveRequest.ProductsBean();
            pb.setProduct_id(bean.getProductId());
            pb.setQty(bean.getCount());
            pb.setHeight(bean.getTwoUnitValue());
            pbList.add(pb);
        }
        rr.setProducts(pbList);
        StringBuffer sb = new StringBuffer("/gongfu/order/");
        sb.append(lbean.getOrderID()).append("/receive/");
        sendConnection(sb.toString(),rr,RECEIVE,true, BaseEntity.ResultBean.class);

    }

    //点货接口{"products":[{"qty":5,"product_id":11,"height":2}]},
    // URL	http://develop.runwise.cn/api/order/572/tallying/
    private void tallyRequest() {

        ReceiveRequest rr = new ReceiveRequest();
        List<ReceiveRequest.ProductsBean> pbList = new ArrayList<>();
        for (ReceiveBean bean : countMap.values()) {
            ReceiveRequest.ProductsBean pb = new ReceiveRequest.ProductsBean();
            pb.setProduct_id(bean.getProductId());
            pb.setHeight(bean.getCount());
            pb.setQty(bean.getCount());
            pb.setTracking(bean.getTracking());
            if (bean.getTracking().equals("lot")) {
                pb.setLot_list(bean.getLot_list());
            }
            pbList.add(pb);
        }
        rr.setProducts(pbList);
        StringBuffer sb = new StringBuffer("/api/order/");
        sb.append(lbean.getOrderID()).append("/tallying/");
        sendConnection(sb.toString(), rr, TALLYING, true, BaseEntity.ResultBean.class);

    }

    private boolean isSameReceiveCount() {
        for (OrderResponse.ListBean.LinesBean lb : datas) {
            int qty = (int) lb.getProductUomQty();
            String pId = String.valueOf(lb.getProductID());
            if (countMap.containsKey(pId)) {
                if (qty != countMap.get(pId).getCount()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case RECEIVE:
                Intent intent = new Intent(mContext, ReceiveSuccessActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", lbean);
                intent.putExtras(bundle);
                startActivity(intent);
                EventBus.getDefault().post(new UpdateRepertory());
                EventBus.getDefault().post(new OrderUpdateEvent());
                finish();
                break;
            case TALLYING:
                ToastUtil.show(mContext, "点货成功");
                finish();
                break;
            case DONE_TALLY:
                Intent dIntent = new Intent(mContext, ReceiveSuccessActivity.class);
                Bundle dBundle = new Bundle();
                dBundle.putParcelable("order", lbean);
                dIntent.putExtras(dBundle);
                startActivity(dIntent);
                EventBus.getDefault().post(new UpdateRepertory());
                EventBus.getDefault().post(new OrderUpdateEvent());
                finish();
                break;
            case BEGIN_TALLY:
                break;
            case END_TALLY:
                finish();
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where) {
            case RECEIVE:
                ToastUtil.show(mContext, errMsg);
                break;
            case BEGIN_TALLY:
                ToastUtil.show(mContext, "网络异常，请稍侯再试");
                finish();
                break;
            case END_TALLY:
                ToastUtil.show(mContext, "网络异常，请稍侯再试");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
                OrderResponse.ListBean.LinesBean bean = data.getParcelableExtra(INTENT_KEY_PRODUCT);
                ArrayList<BatchEntity> batchEntities = (ArrayList<BatchEntity>) data.getSerializableExtra(INTENT_KEY_BATCH_ENTITIES);
                final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
                ReceiveBean receiveBean = new ReceiveBean();
                receiveBean.setName(basicBean.getName());
                receiveBean.setTracking(basicBean.getTracking());
//                        receiveBean.setCount((int)bean.getProductUomQty());
                int count =0;
            List<ReceiveRequest.ProductsBean.LotBean> lot_list = new ArrayList<>();
                for (BatchEntity batchEntity :batchEntities){
                    count += Integer.parseInt(batchEntity.getProductCount());
                    ReceiveRequest.ProductsBean.LotBean lotBean = new ReceiveRequest.ProductsBean.LotBean();
                    lotBean.setHeight(Integer.parseInt(batchEntity.getProductCount()));
                    lotBean.setQty(Integer.parseInt(batchEntity.getProductCount()));
                    if (batchEntity.isProductDate()){
                        lotBean.setProduce_datetime(batchEntity.getProductDate());
                    }else{
                        lotBean.setLife_datetime(batchEntity.getProductDate());
                    }
                    lotBean.setLot_name(batchEntity.getBatchNum());
                    lot_list.add(lotBean);
                }
                receiveBean.setCount(count);
                receiveBean.setProductId(bean.getProductID());
                receiveBean.setImageBean(basicBean.getImage());
                receiveBean.setDefaultCode(basicBean.getDefaultCode());
                receiveBean.setUnit(basicBean.getUnit());
                receiveBean.setStockType(bean.getStockType());
                if (isSettle) {
                    receiveBean.setTwoUnit(true);
                    receiveBean.setUnit(basicBean.getSettleUomId());
                } else {
                    receiveBean.setTwoUnit(false);
                }
                receiveBean.setLot_list(lot_list);
                countMap.put(String.valueOf(receiveBean.getProductId()),receiveBean);
            updatePbProgress();
            ReceiveProEvent receiveProEvent = new ReceiveProEvent();
            receiveProEvent.setNotifyDataSetChange(true);
                EventBus.getDefault().post(receiveProEvent);
            }
    }

    @Override
    public void doAction(ReceiveBean bean) {
        if (ShuangRensShouHuoQueRen){
            return;
        }
        bottomData = bean;
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();

            return;
        }
        if (mPopWindow2 != null && mPopWindow2.isShowing()){
            mPopWindow2.dismiss();
            return;
        }
            //单独处理双单位的,不管其它维护的分类
            if(bean.isTwoUnit() && !lbean.getDeliveryType().equals("fresh_vendor_delivery")){
                View rootview = LayoutInflater.from(this).inflate(R.layout.receive_layout, null);
                mPopWindow2.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
                titleTv.setText(bottomData.getName());
                if (countMap.containsKey(String.valueOf(bean.getProductId()))){ //如果countMap里面有，则优先用countMap。
                    ReceiveBean rb = countMap.get(String.valueOf(bean.getProductId()));
                    edEt.setText(String.valueOf(rb.getCount()));
                    edEt.setSelection(String.valueOf(rb.getCount()).length());
                    unitTv.setText(rb.getUnit());
                    if (rb.getTwoUnitValue() == 0){
                        unitValueTv.setText("");
                    }else{
                        unitValueTv.setText(rb.getTwoUnitValue()+"");
                    }

                }else{
                    edEt.setText(bottomData.getCount()+"");
                    edEt.setSelection(String.valueOf(bottomData.getCount()).length());
                    unitTv.setText(bottomData.getUnit());
//                unitValueTv.setText(bottomData.getTwoUnitValue()+"");
                    unitValueTv.setText("");
                }

                //双人点货
                if (isSettle){
                    twoUnitRL.setVisibility(View.VISIBLE);
                }else{
                    twoUnitRL.setVisibility(View.GONE);
                }
            return;
            }
            if (lbean.getDeliveryType().equals("vendor_delivery") && bean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)) {
                Intent intent = new Intent(mContext, EditBatchActivity.class);
                intent.putExtra(INTENT_KEY_PRODUCT, bottomData.getProductId());
                startActivity(intent);
//                View rootview = LayoutInflater.from(this).inflate(R.layout.receive_layout, null);
//                mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
//                mTvProductName.setText(bottomData.getName());
//                FrecoFactory.getInstance(mContext).disPlay(productImage, Constant.BASE_URL + bottomData.getImageBean().getImageSmall());
//                mTvSerialNumber.setText(bottomData.getDefaultCode());
//                mTvSpecifications.setText(bottomData.getUnit());
//                mEtBatchNumber.setText(bottomData.getLot_name());
//
//                if (countMap.containsKey(String.valueOf(bean.getProductId()))) { //如果countMap里面有，则优先用countMap。
//                    ReceiveBean receiveBean = countMap.get(String.valueOf(bean.getProductId()));
//                    mEtProductAmount.setText(String.valueOf(receiveBean.getCount()));
//                    mEtProductAmount.setSelection(String.valueOf(receiveBean.getCount()).length());
//
//                } else {
//                    mEtProductAmount.setText(bottomData.getCount()+"");
//                    mEtProductAmount.setSelection(String.valueOf(bottomData.getCount()).length());
//                }
            } else {
                String pId = String.valueOf(bottomData.getProductId());
//                double settleCount = TextUtils.isEmpty(unitValueTv.getText().toString()) ? 0 : Double.valueOf(unitValueTv.getText().toString());
                bottomData.setCount(bean.getCount());
//                bottomData.setTwoUnitValue(settleCount);    //双单位的值
                countMap.put(pId, bottomData);
                mPopWindow.dismiss();
                //更新进度条
                updatePbProgress();
                //更新fragment列表内容

                EventBus.getDefault().post(new ReceiveProEvent());
            }


//            //双人点货
//            if (isSettle){
//                twoUnitRL.setVisibility(View.VISIBLE);
//            }else{
//                twoUnitRL.setVisibility(View.GONE);
//            }

    }

    @Override
    public void onConnectionFailure(int i) {

    }

    @Override
    public void onDeviceAvailabilityChanged(CaptureDeviceClient captureDeviceClient) {

    }

    @Override
    public void onDataDecoded(CaptureDeviceClient captureDeviceClient, DecodedData decodedData) {
        Log.i("aaaa", "aaaaaa");
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            titleList.add("全部");
            titleList.add("冷藏货");
            titleList.add("冻货");
            titleList.add("干货");
            Bundle bundle = new Bundle();
            bundle.putInt("mode", mode); //mode:0正常收货，1点货，2双人收货
            if (datas != null && datas.size() > 0) {
                bundle.putParcelableArrayList("datas", datas);
            }
            bundle.putParcelable("order", lbean);
            ReceiveFragment allFragment = new ReceiveFragment();
            allFragment.type = DataType.ALL;
            allFragment.setCallback(ReceiveActivity.this);
            allFragment.setArguments(bundle);
            ReceiveFragment coldFragment = new ReceiveFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            coldFragment.setCallback(ReceiveActivity.this);
            coldFragment.setArguments(bundle);
            ReceiveFragment freezeFragment = new ReceiveFragment();
            freezeFragment.setCallback(ReceiveActivity.this);
            freezeFragment.type = DataType.FREEZE;
            freezeFragment.setArguments(bundle);
            ReceiveFragment dryFragment = new ReceiveFragment();
            dryFragment.setCallback(ReceiveActivity.this);
            dryFragment.type = DataType.DRY;
            dryFragment.setArguments(bundle);
            fragmentList.add(allFragment);
            fragmentList.add(coldFragment);
            fragmentList.add(freezeFragment);
            fragmentList.add(dryFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScan(DataDecodedEvent event) {
        print(event.data.getData().toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCaptureDeviceAvailabilityChanged(DeviceAvailabilityEvent event) {
        Collection<CaptureDeviceClient> devices;
        updateDeviceState(event);

        // The first time we receive this event - via sticky event - we
        // want to configure all available devices
        if (devicesConnected < 0) {
            devices = event.getAllDevices();
        } else {
            devices = event.getChangedDevices();
        }

        devicesConnected = event.getDeviceCount();

        for (CaptureDeviceClient device : devices) {
//            if (device.isMine()) {
//                // Configuration
//            }
        }
    }

    private void updateDeviceState(DeviceAvailabilityEvent event) {
        String string;
        if (event.isAnyDeviceAvailable()) {
            print("Device available");
//            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            string = "Device ready";
        } else if (event.isAnyDeviceConnected()) {
            print("Device connected");
//            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            string = "Device in use";
        } else {
            print("No device");
//            btn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
//            btn.setText("No device connected");
        }
    }

    private void print(String message) {
        ToastUtil.show(mContext, message);
    }

    public void fitPopupWindowOverStatusBar(boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(mPopWindow, needFullScreen);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mode == 1) {
            dialog.setTitle("提示");
            dialog.setMessage("确认取消点货?");
            dialog.setMessageGravity();
            dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                @Override
                public void doClickButton(Button btn, CustomDialog dialog) {
                    startOrEndTally(false);
                }
            });
            dialog.setLeftBtnListener("取消", new CustomDialog.DialogListener() {
                @Override
                public void doClickButton(Button btn, CustomDialog dialog) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }
}
