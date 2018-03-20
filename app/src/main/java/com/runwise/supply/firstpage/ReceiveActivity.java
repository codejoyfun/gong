package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.bean.ReceiveProEvent;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.entity.BatchEntity;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.OrderDetailResponse;
import com.runwise.supply.entity.ReceiveBeanList;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReceiveBean;
import com.runwise.supply.firstpage.entity.ReceiveRequest;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.OrderUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ReceiveInfo;
import com.runwise.supply.repertory.entity.UpdateRepertory;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.MyDbUtil;
import com.runwise.supply.tools.ProductBasicHelper;
import com.runwise.supply.tools.RunwiseKeyBoard;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.NoScrollViewPager;
import com.runwise.supply.view.NoWatchEditText;
import com.runwise.supply.view.ProductTypePopup;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_COMPANY_NAME;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_RECEIVE_FINISH;
import static com.runwise.supply.firstpage.EditBatchActivity.INTENT_KEY_BATCH_ENTITIES;
import static com.runwise.supply.firstpage.EditBatchActivity.INTENT_KEY_PRODUCT;
import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean.TYPE_VENDOR_DELIVERY;
import static com.runwise.supply.orderpage.entity.ReceiveInfo.SEPARATOR;

//import com.socketmobile.capture.client.CaptureClient;
//import com.socketmobile.capture.client.CaptureDeviceClient;
//import com.socketmobile.capture.events.DataDecodedEvent;
//import com.socketmobile.capture.events.DeviceAvailabilityEvent;
//import com.socketmobile.capture.types.DecodedData;

/**
 * Created by libin on 2017/7/16.
 */

public class ReceiveActivity extends NetWorkActivity implements DoActionCallback, LoadingLayoutInterface/*, CaptureClient.Listener*/ {
    private static final int RECEIVE = 100;           //收货
    private static final int TALLYING = 200;          //点货
    private static final int DONE_TALLY = 300;          //第二个人完成点货
    private static final int BEGIN_TALLY = 400;           //开始点货
    private static final int END_TALLY = 500;           //退出点货
    private static final int PRODUCT_DETAIL = 600;
    private static final int ORDER_DETAIL = 700;
    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private NoScrollViewPager viewPager;
    @ViewInject(R.id.tv_receive_count)
    private TextView mTvReceiveCount;
    @ViewInject(R.id.tv_receive_count_tag)
    private TextView mTvReceiveCountTag;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.ll_search_bar)
    private LinearLayout mLlSearchBar;
    @ViewInject(R.id.et_sou_suo)
    private EditText mEtSouSuo;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
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
    private double totalQty;           //预计总的商品总数
    private OrderResponse.ListBean lbean;
    //    private CaptureClient mClient;
    TimePickerView pvCustomTime;
    WheelView wheelView;

    private TextView titleTv;
    private TextView mTvContent;
    private NoWatchEditText edEt;
    private TextView mTvUnit;
    private TextView mTvStockCount;

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

    private ProductBasicHelper productBasicHelper;

    CategoryRespone categoryRespone;

    String companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.receive_layout);
        setTitleRigthIcon(true, R.drawable.search);

        companyName = (String) SPUtils.get(getActivityContext(), FILE_KEY_COMPANY_NAME, "");

        Bundle bundle = getIntent().getExtras();
        lbean = bundle.getParcelable("order");
        isSettle = lbean.isIsTwoUnit();
        setTitleLeftIcon(true, R.drawable.nav_back);
        mode = bundle.getInt("mode");
        if (mode == 1) {
            setTitleText(true, "点货");
        } else {
            setTitleText(true, "收货");
        }
        initPopWindow();
        initPopWindow2();       //单独初始化一下双单位的popview
//        Capture.init(getApplicationContext());
//        mClient = new CaptureClient();
//        mClient.setListener(this);
//        mClient.connect();
        if (lbean.getLines() == null || lbean.getLines().isEmpty()) {
            requestOrderDetail();//没有传商品列表，需要查询订单详情
        } else {
            initUI();
        }
    }

    /**
     * 获取到商品列表后更新界面
     */
    private void initUI() {
        if (lbean != null && lbean.getLines() != null) {
            datas.addAll(lbean.getLines());
        }
        if (mode == 1) {
            //开始点货
            String userName = SampleApplicationLike.getInstance().getUserName();
            if (TextUtils.isEmpty(userName) || !userName.equals(lbean.getTallyingUserName())) {
                startOrEndTally(true);
            }
        }
        getCategory();
        updateUI();
        setUpSearch();
    }

    /**
     * 需要查详情拿商品列表
     */
    private void requestOrderDetail() {
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/order/");
        sb.append(lbean.getOrderID()).append("/");
        sendConnection(sb.toString(), request, ORDER_DETAIL, true, OrderDetailResponse.class);
    }

    private void updateUI() {
        if (mode == 1) {
            setTitleText(true, "点货");
            String tallyingUserName = lbean.getTallyingUserName();
            if (!TextUtils.isEmpty(tallyingUserName)) {
                if (!tallyingUserName.equals(SampleApplicationLike.getInstance().getUserName())) {
                    ShuangRensShouHuoQueRen = true;
                    //双人收货模式下的确认人(不能编辑,只能确认)
                    for (OrderResponse.ListBean.LinesBean linesBean : lbean.getLines()) {
                        ReceiveBean receiveBean = new ReceiveBean();
                        receiveBean.setProductId(linesBean.getProductID());
                        receiveBean.setCount((int) linesBean.getTallyingAmount());
                        final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(linesBean.getProductID()));
                        receiveBean.setTracking(basicBean.getTracking());
                        if (basicBean.getTracking().equals("lot")) {
                            List<ReceiveRequest.ProductsBean.LotBean> lot_list = new ArrayList<>();
                            for (OrderResponse.ListBean.LinesBean.LotListBean lotListBean : linesBean.getLotList()) {
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
                        countMap.put(String.valueOf(linesBean.getProductID()), receiveBean);
                    }
                }
            }
        } else {
            setTitleText(true, "收货");
            String source = (String) SPUtils.get(getActivityContext(), companyName + String.valueOf(lbean.getOrderID()), "");
            if (!TextUtils.isEmpty(source)) {
                ReceiveBeanList receiveBeanList = new ReceiveBeanList(source);
                List<ReceiveBean> beanList = receiveBeanList.getList();
                boolean isEmpty = true;
                for (ReceiveBean receiveBean : beanList) {
                    countMap.put(String.valueOf(receiveBean.getProductId()), receiveBean);
                    if (receiveBean.getCount() != 0) {
                        isEmpty = false;
                    }
                }
                //如果从缓存那里获取每个商品的收货数量为0，那重置回初始值,每个商品的数量和对应的库存数量一致
                if (isEmpty) {
                    for (ReceiveBean receiveBean : beanList) {
                        receiveBean.setCount(receiveBean.getProductUomQty());
                    }
                }
                for (OrderResponse.ListBean.LinesBean bean : datas) {
                    totalQty += bean.getProductUomQty();
                }
                updatePbProgress();
            } else {
                boolean isEmpty = true;
                for (OrderResponse.ListBean.LinesBean linesBean : lbean.getLines()) {
                    ReceiveBean receiveBean = new ReceiveBean();
                    receiveBean.setProductId(linesBean.getProductID());
                    if (lbean.isActualSendOrder()) {
                        receiveBean.setCount(linesBean.getActualSendNum());
                    } else {
                        receiveBean.setCount(linesBean.getProductUomQty());
                    }
                    if (receiveBean.getCount() != 0) {
                        isEmpty = false;
                    }

                    receiveBean.setTracking(linesBean.getTracking());
                    if (linesBean.getTracking().equals("lot")) {
                        List<ReceiveRequest.ProductsBean.LotBean> lot_list = new ArrayList<>();
                        ReceiveRequest.ProductsBean.LotBean lotBean = new ReceiveRequest.ProductsBean.LotBean();
                        lotBean.setLot_name("");
                        if (lbean.isActual()) {
                            lotBean.setHeight(linesBean.getActualSendNum());
                            lotBean.setQty((int) linesBean.getActualSendNum());
                        } else {
                            lotBean.setHeight(linesBean.getProductUomQty());
                            lotBean.setQty((int) linesBean.getProductUomQty());
                        }

                        lotBean.setProduce_datetime(TimeUtils.getYMD(new Date()));
                        lot_list.add(lotBean);
                        receiveBean.setLot_list(lot_list);
                    }
                    countMap.put(String.valueOf(linesBean.getProductID()), receiveBean);
                }
                //如果从缓存那里获取每个商品的收货数量为0，那重置回初始值,每个商品的数量和对应的库存数量一致
                if (isEmpty) {
                    for (ReceiveBean receiveBean : countMap.values()) {
                        receiveBean.setCount(receiveBean.getProductUomQty());
                    }
                }
                setDefalutProgressBar();
            }
        }
    }

    private void setUpSearch() {
        mEtSouSuo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //发送搜索事件
                ProductQueryEvent event = new ProductQueryEvent(s.toString());
                EventBus.getDefault().post(event);
            }
        });
    }

    private void getCategory() {
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(SampleApplicationLike.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
    }

    private void setUpDataForViewPage() {
        List<Fragment> receiveFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<OrderResponse.ListBean.LinesBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<OrderResponse.ListBean.LinesBean>());
        }

        for (OrderResponse.ListBean.LinesBean lineBean : lbean.getLines()) {
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(lineBean.getProductID()));
            if (listBean != null && !TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<OrderResponse.ListBean.LinesBean> listBeen = map.get(listBean.getCategory());
                if (listBeen == null) {
                    listBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), listBeen);
                }
                listBeen.add(lineBean);
            }

        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<OrderResponse.ListBean.LinesBean> value = map.get(category);
            receiveFragmentList.add(newReceiveFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }

        receiveFragmentList.add(0, newReceiveFragment((ArrayList<OrderResponse.ListBean.LinesBean>) lbean.getLines()));
        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager(), titles, receiveFragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(receiveFragmentList.size());
        smartTabLayout.setupWithViewPager(viewPager);
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                mTypeWindow.dismiss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        initPopWindow((ArrayList<String>) titles);
    }

    public ReceiveFragment newReceiveFragment(ArrayList<OrderResponse.ListBean.LinesBean> value) {
        ReceiveFragment receiveFragment = new ReceiveFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode); //mode:0正常收货，1点货，2双人收货
        bundle.putParcelable("order", lbean);
        bundle.putParcelableArrayList("datas", value);
        receiveFragment.setArguments(bundle);
        receiveFragment.setCallback(ReceiveActivity.this);
        return receiveFragment;
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
//        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
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
                double count;
                if (TextUtils.isEmpty(productAmountString)) {
                    count = 0;
                } else {
                    count = Double.valueOf(mEtProductAmount.getText().toString());
                }
                bottomData.setCount(count);
//                bottomData.setLot_name(mEtBatchNumber.getText().toString());
                if (wheelView == null || wheelView.getCurrentItem() == 0) {
//                    bottomData.setProduce_datetime(mTvProductDateValue.getText().toString());
//                    bottomData.setLife_datetime("");
                } else {
//                    bottomData.setProduce_datetime("");
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

    private void initPopWindow2() {
        dialogView2 = LayoutInflater.from(this).inflate(R.layout.dialog_edit_layout2, null);
        RelativeLayout rl = (RelativeLayout) dialogView2.findViewById(R.id.dialog_main);
        mPopWindow2 = new PopupWindow(dialogView2, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopWindow2.setContentView(dialogView2);
//        mPopWindow2.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow2.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow2.setFocusable(true);
        mPopWindow2.setOutsideTouchable(true);
//        mPopWindow2.setAnimationStyle(R.style.BottomDialog_AnimationStyle);
        Button input_minus = (Button) dialogView2.findViewById(R.id.input_minus);
        Button input_add = (Button) dialogView2.findViewById(R.id.input_add);
        titleTv = (TextView) dialogView2.findViewById(R.id.tv_name);
        mTvContent = (TextView) dialogView2.findViewById(R.id.tv_content);
        mTvUnit = (TextView) dialogView2.findViewById(R.id.tv_unit);
        mTvStockCount = (TextView) dialogView2.findViewById(R.id.tv_stock_count);
        edEt = (NoWatchEditText) dialogView2.findViewById(R.id.et_product_count);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof RelativeLayout) {
                    mPopWindow2.dismiss();
                }
            }
        });
        dialogView2.findViewById(R.id.iv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow2.dismiss();
            }
        });
        input_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double count = Double.valueOf(edEt.getText().toString());
                if (count > 0) {
                    count = BigDecimal.valueOf(count).subtract(BigDecimal.ONE).doubleValue();
                    if (count < 0) count = 0;
                    edEt.setText(NumberUtil.getIOrD(count));
                }
            }
        });
        input_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double count = Double.valueOf(edEt.getText().toString());
                count = BigDecimal.valueOf(count).add(BigDecimal.ONE).doubleValue();
                if (count < 0) count = 0;
                edEt.setText(NumberUtil.getIOrD(count));
//                edEt.setSelection(String.valueOf(String.valueOf(count)).length());
            }
        });
        Button sureBtn = (Button) dialogView2.findViewById(R.id.btn_confirm);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edEt.getText().toString())) {
                    toast("输入数量不能为空!");
                    return;
                }
                String pId = String.valueOf(bottomData.getProductId());
                double count = Double.valueOf(edEt.getText().toString());
                bottomData.setCount(count);
                countMap.put(pId, bottomData);
                mPopWindow2.dismiss();
                //更新进度条
                updatePbProgress();
                //更新fragment列表内容
                EventBus.getDefault().post(new ReceiveProEvent(true));
            }
        });

    }

    private void updatePbProgress() {
        double pbNum = 0;
        for (ReceiveBean bean : countMap.values()) {
            pbNum += bean.getCount();
        }
        mTvReceiveCount.setText(NumberUtil.getIOrD(pbNum));
        mTvReceiveCountTag.setText("/" + NumberUtil.getIOrD(totalQty) + "件");
    }

    private void setDefalutProgressBar() {
        double totalActualSendQty = 0;
        totalQty = 0;
        if (lbean.isActualSendOrder()){
            for (OrderResponse.ListBean.LinesBean bean : datas) {
                totalQty += bean.getProductUomQty();
                totalActualSendQty+=bean.getActualSendNum();
            }
        }else{
            for (OrderResponse.ListBean.LinesBean bean : datas) {
                totalQty += bean.getProductUomQty();
            }
            totalActualSendQty = totalQty;
        }

        mTvReceiveCount.setText(NumberUtil.getIOrD(totalActualSendQty));
        mTvReceiveCountTag.setText("/" + NumberUtil.getIOrD(totalQty) + "件");
    }

    //    private PopupWindow mProductTypeWindow;
//    ProductTypeAdapter mProductTypeAdapter;
    private ProductTypePopup mTypeWindow;

    private void initPopWindow(ArrayList<String> typeList) {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow = new ProductTypePopup(this,
                ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.getScreenH(getActivityContext()) - y,
                typeList, 0);
        mTypeWindow.setViewPager(viewPager);
        mTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
//        View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_tab_type, null);
//        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
//        mProductTypeAdapter = new ProductTypeAdapter(typeList);
//        gridView.setAdapter(mProductTypeAdapter);
//        final int[] location = new int[2];
//        smartTabLayout.getLocationOnScreen(location);
//        int y = (int) (location[1] + smartTabLayout.getHeight());
//        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getScreenH(getActivityContext()) - y, true);
//        mProductTypeWindow.setContentView(dialog);
//        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
//        mProductTypeWindow.setFocusable(false);
//        mProductTypeWindow.setOutsideTouchable(false);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mProductTypeWindow.dismiss();
//                viewPager.setCurrentItem(position);
//                smartTabLayout.getTabAt(position).select();
//                for (int i = 0; i < mProductTypeAdapter.selectList.size(); i++) {
//                    mProductTypeAdapter.selectList.set(i, new Boolean(false));
//                }
//                mProductTypeAdapter.selectList.set(position, new Boolean(true));
//                mProductTypeAdapter.notifyDataSetChanged();
//            }
//        });
//        dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mProductTypeWindow.dismiss();
//            }
//        });
//        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                ivOpen.setImageResource(R.drawable.arrow);
//            }
//        });
    }

    private void showPopWindow() {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow.showAtLocation(getRootView(ReceiveActivity.this), Gravity.NO_GRAVITY, 0, y);
        mTypeWindow.setSelect(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    @OnClick({R.id.title_iv_left, R.id.btn_confirm, R.id.iv_open, R.id.btn_cancel, R.id.title_iv_rigth})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_open:
                if (mTypeWindow == null) {
                    return;
                }
                if (!mTypeWindow.isShowing()) {
                    showPopWindow();
                } else {
                    mTypeWindow.dismiss();
                }
                break;
            case R.id.title_iv_left:
                ReceiveBeanList receiveBeanList = new ReceiveBeanList();
                List<ReceiveBean> list = new ArrayList<>();
                for (ReceiveBean bean : countMap.values()) {
                    list.add(bean);
                }
                receiveBeanList.setList(list);
                if(list.size() > 0){
                    SPUtils.put(getActivityContext(), companyName + String.valueOf(lbean.getOrderID()), receiveBeanList.toString());
                }
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
            case R.id.btn_confirm:
                //如果每样商品数量都匹配，则提示:确认收货与订单数量一致?
                //如果每样商品数量不一致，则提示:收货数量与订单不一致，是否确认收货?
                //如果无批次，提示请认真核对商品数量，确认收货无法修改哦！
                //如果有批次，提示
                String tip = TYPE_VENDOR_DELIVERY.equals(lbean.getDeliveryType()) ?
                        "请认真核对商品数量和生产日期,确认收货后无法修改哦！" : "请认真核对商品数量，确认收货无法修改哦！";
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
                            if (isSettle && !lbean.getDeliveryType().equals("fresh_vendor_delivery")) {
                                receiveProductRequest2();
                            } else {
                                receiveProductRequest();
                            }
                        } else if (mode == 1) {
                            tallyRequest();
                        } else if (mode == 2) {
                            tallyDoneRequest();
                        }
                        MobclickAgent.onEvent(getActivityContext(), EVENT_ID_RECEIVE_FINISH);
                    }
                });
                dialog.show();
                break;
            case R.id.btn_cancel:
                if (mLlSearchBar.getVisibility() == View.GONE) {
                    mLlSearchBar.setVisibility(View.VISIBLE);
                } else {
                    mLlSearchBar.setVisibility(View.GONE);
                    mEtSouSuo.setText("");
                }
                break;
            case R.id.title_iv_rigth:
                if (mLlSearchBar.getVisibility() == View.GONE) {
                    mLlSearchBar.setVisibility(View.VISIBLE);
                } else {
                    mLlSearchBar.setVisibility(View.GONE);
                }
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
            if (bean.getTracking().equals("lot")) {
                pb.setLot_list(bean.getLot_list());
                pb.setTracking(bean.getTracking());
            }
            pbList.add(pb);
        }
        rr.setProducts(pbList);
        StringBuffer sb = new StringBuffer("/gongfu/v2/order/");
        sb.append(lbean.getOrderID()).append("/receive/");
        sendConnection(sb.toString(), rr, RECEIVE, true, BaseEntity.ResultBean.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        saveReceiveInfo();
    }

    public void saveReceiveInfo() {
        DbUtils dbUtils = MyDbUtil.create(ReceiveActivity.this);
        dbUtils.configAllowTransaction(true);
        for (ReceiveBean bean : countMap.values()) {
            ReceiveInfo receiveInfo = new ReceiveInfo();
            receiveInfo.setOrderId(lbean.getOrderID());
            receiveInfo.setProductId(bean.getProductId());
            receiveInfo.setId(lbean.getOrderID() + SEPARATOR + bean.getProductId());
            if (bean.getTracking().equals("lot")) {
                StringBuffer batchNumberList = new StringBuffer();
                StringBuffer countList = new StringBuffer();
                for (ReceiveRequest.ProductsBean.LotBean lotBean : bean.getLot_list()) {
                    batchNumberList.append(lotBean.getLot_name()).append(SEPARATOR);
                    countList.append(lotBean.getQty()).append(SEPARATOR);
                }
                if (batchNumberList.toString().length() > 0) {
                    batchNumberList.deleteCharAt(batchNumberList.toString().length() - 1);
                }
                if (countList.toString().length() > 0) {
                    countList.deleteCharAt(countList.toString().length() - 1);
                }
                receiveInfo.setCountList(countList.toString());
                receiveInfo.setBatchNumberList(batchNumberList.toString());
            } else {
                receiveInfo.setCount((int) bean.getCount());
            }
            try {
                dbUtils.saveOrUpdate(receiveInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }


    //双单位收货的请求
    private void receiveProductRequest2() {
        ReceiveRequest rr = new ReceiveRequest();
        List<ReceiveRequest.ProductsBean> pbList = new ArrayList<>();
        for (ReceiveBean bean : countMap.values()) {
            ReceiveRequest.ProductsBean pb = new ReceiveRequest.ProductsBean();
            pb.setProduct_id(bean.getProductId());
            pb.setQty(bean.getCount());
            pb.setHeight(bean.getTwoUnitValue());
            pbList.add(pb);
        }
        rr.setProducts(pbList);
        StringBuffer sb = new StringBuffer("/gongfu/order/");
        sb.append(lbean.getOrderID()).append("/receive/");
        sendConnection(sb.toString(), rr, RECEIVE, true, BaseEntity.ResultBean.class);

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
            if (bean.getTracking().equals("lot")) {
                pb.setLot_list(bean.getLot_list());
                pb.setTracking(bean.getTracking());

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
            int qty = (int) lb.getActualSendNum();
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
            case ORDER_DETAIL:
                OrderDetailResponse orderDetailResponse = (OrderDetailResponse) result.getResult().getData();
                lbean = orderDetailResponse.getOrder();
                initUI();
                break;
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
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage();
                ReceiveProEvent receiveProEvent = new ReceiveProEvent();
                receiveProEvent.setNotifyDataSetChange(true);
                EventBus.getDefault().post(receiveProEvent);
                break;
            case PRODUCT_DETAIL:
                if (productBasicHelper.onSuccess(result)) {
                    updateUI();
                    getCategory();
                }
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
            case ORDER_DETAIL:
                mLoadingLayout.onFailure(errMsg, R.drawable.default_icon_checkconnection);
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
            double count = 0;
            List<ReceiveRequest.ProductsBean.LotBean> lot_list = new ArrayList<>();
            for (BatchEntity batchEntity : batchEntities) {
                count += Double.parseDouble(batchEntity.getProductCount());
                ReceiveRequest.ProductsBean.LotBean lotBean = new ReceiveRequest.ProductsBean.LotBean();
                lotBean.setHeight(Double.parseDouble(batchEntity.getProductCount()));
                lotBean.setQty(Double.parseDouble(batchEntity.getProductCount()));
                if (batchEntity.isProductDate()) {
                    lotBean.setProduce_datetime(batchEntity.getProductDate());
                } else {
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
            countMap.put(String.valueOf(receiveBean.getProductId()), receiveBean);
            updatePbProgress();
            ReceiveProEvent receiveProEvent = new ReceiveProEvent();
            receiveProEvent.setNotifyDataSetChange(true);
            EventBus.getDefault().post(receiveProEvent);
        }
    }

    @Override
    public void doAction(ReceiveBean bean) {
        if (ShuangRensShouHuoQueRen) {
            return;
        }
        bottomData = bean;
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();

            return;
        }
        if (mPopWindow2 != null && mPopWindow2.isShowing()) {
            mPopWindow2.dismiss();
            return;
        }
        if (lbean.getDeliveryType().equals("vendor_delivery") && bean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)) {
            Intent intent = new Intent(mContext, EditBatchActivity.class);
            intent.putExtra(INTENT_KEY_PRODUCT, bottomData.getProductId());
            startActivity(intent);
        } else {
            String pId = String.valueOf(bottomData.getProductId());
            bottomData.setCount(bean.getCount());
            countMap.put(pId, bottomData);
//            View rootview = LayoutInflater.from(this).inflate(R.layout.receive_layout, null);
//            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(bottomData.getProductId()));
//            if (listBean != null) {
//                FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(productImage, listBean.getImage().getImageSmall());
//                StringBuffer sb = new StringBuffer(listBean.getDefaultCode());
//                sb.append("  ").append(listBean.getUnit());
//                boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
//                if (canSeePrice) {
//                    if (listBean.isTwoUnit()) {
//                        sb.append("\n").append(UserUtils.formatPrice(String.valueOf(listBean.getSettlePrice()))).append("元/").append(listBean.getSettleUomId());
//                    } else {
//                        sb.append("\n").append(UserUtils.formatPrice(String.valueOf(listBean.getPrice()))).append("元/").append(listBean.getProductUom());
//                    }
//                }
//                mTvContent.setText(sb.toString());
//                mTvUnit.setText(listBean.getUom());
//            }
            RunwiseKeyBoard runwiseKeyBoard = new RunwiseKeyBoard(getActivityContext());
            runwiseKeyBoard.setUp(bottomData, new RunwiseKeyBoard.SetCountListener() {
                @Override
                public void onSetCount(double count) {
                    String pId = String.valueOf(bottomData.getProductId());
                    bottomData.setIsChange(true);
                    bottomData.setCount(count);
                    countMap.put(pId, bottomData);
                    mPopWindow2.dismiss();
                    //更新进度条
                    updatePbProgress();
                    //更新fragment列表内容
                    EventBus.getDefault().post(new ReceiveProEvent(true));
                }
            });
            runwiseKeyBoard.show();
            titleTv.setText(bottomData.getName());
//            edEt.setText(NumberUtil.getIOrD(bottomData.getCount()));
//            mTvStockCount.setText(NumberUtil.getIOrD(bottomData.getProductUomQty()));
//            edEt.setSelectAllOnFocus(true);
//            mPopWindow2.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
//            edEt.requestFocus();
//            edEt.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//            },200);
//            View dialogRoot = dialogView2.findViewById(R.id.dialog_main);
//            dialogRoot.setVisibility(View.GONE);
//            edEt.postDelayed(new Runnable(){
//                @Override
//                public void run() {
//                    Animation animation = AnimationUtils.loadAnimation(getActivityContext(),R.anim.fade_in);
//                    dialogRoot.setVisibility(View.VISIBLE);
//                    dialogRoot.startAnimation(animation);
//                }
//            },400);


            //更新进度条
            updatePbProgress();
            //更新fragment列表内容

            EventBus.getDefault().post(new ReceiveProEvent(false));
        }
    }

    @Override
    public void retryOnClick(View view) {
        requestOrderDetail();//没有传商品列表，需要查询订单详情
    }

//    @Override
//    public void onConnectionFailure(int i) {
//
//    }
//
//    @Override
//    public void onDeviceAvailabilityChanged(CaptureDeviceClient captureDeviceClient) {
//
//    }
//
//    @Override
//    public void onDataDecoded(CaptureDeviceClient captureDeviceClient, DecodedData decodedData) {
//        Log.i("aaaa", "aaaaaa");
//    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList) {
            super(fm);
            this.titleList = titleList;
            this.fragmentList = fragmentList;
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

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onScan(DataDecodedEvent event) {
//        print(event.data.getData().toString());
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onCaptureDeviceAvailabilityChanged(DeviceAvailabilityEvent event) {
//        Collection<CaptureDeviceClient> devices;
//        updateDeviceState(event);
//
//        // The first time we receive this event - via sticky event - we
//        // want to configure all available devices
//        if (devicesConnected < 0) {
//            devices = event.getAllDevices();
//        } else {
//            devices = event.getChangedDevices();
//        }
//
//        devicesConnected = event.getDeviceCount();
//
//        for (CaptureDeviceClient device : devices) {
////            if (device.isMine()) {
////                // Configuration
////            }
//        }
//    }

//
//    private void updateDeviceState(DeviceAvailabilityEvent event) {
//        String string;
//        if (event.isAnyDeviceAvailable()) {
//            print("Device available");
////            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
//            string = "Device ready";
//        } else if (event.isAnyDeviceConnected()) {
//            print("Device connected");
////            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
//            string = "Device in use";
//        } else {
//            print("No device");
////            btn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
////            btn.setText("No device connected");
//        }
//    }

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
        ReceiveBeanList receiveBeanList = new ReceiveBeanList();
        List<ReceiveBean> list = new ArrayList<>();
        for (ReceiveBean bean : countMap.values()) {
            list.add(bean);
        }
        receiveBeanList.setList(list);
        if (list.size() > 0){
            SPUtils.put(getActivityContext(), companyName + String.valueOf(lbean.getOrderID()), receiveBeanList.toString());
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("收货页面");
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("收货页面");
        MobclickAgent.onPause(this);          //统计时长
    }
}
