package com.runwise.supply.orderpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.OrderSuccessEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.CustomProgressDialog;
import com.kids.commonframe.config.Constant;
import com.kids.commonframe.base.view.CustomProgressDialog;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderSubmitProductAdapter;
import com.runwise.supply.entity.OrderChangedEvent;
import com.runwise.supply.entity.OrderCommitResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.entity.CommitOrderRequest;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.bottomdialog.BottomDialog;

import static java.lang.System.currentTimeMillis;

public class OrderSubmitActivity extends NetWorkActivity {
    public static final String INTENT_KEY_PRODUCTS = "intent_products";
    public static final int REQUEST_USER_INFO = 1 << 0;
    public static final int REQUEST_SUBMIT = 2000;
    public static final int REQUEST_MODIFY = 3000;

    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @BindView(R.id.iv_date)
    ImageView mIvDate;
    @BindView(R.id.iv_more)
    ImageView mIvMore;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.rl_date_of_service)
    RelativeLayout mRlDateOfService;
    @BindView(R.id.tv_product_num)
    TextView mTvProductNum;
    @BindView(R.id.tv_total_money)
    TextView mTvTotalMoney;
    @BindView(R.id.btn_submit)
    Button mBtnSubmit;
    @BindView(R.id.rl_bottom)
    RelativeLayout mRlBottom;
    @BindView(R.id.tv_product_list)
    TextView mTvProductList;
    @BindView(R.id.rv_product_list)
    RecyclerView mRvProductList;

    CustomProgressDialog mCustomProgressDialog;
    //弹窗星期的View集合
    private TextView[] wArr = new TextView[3];
    private TextView[] dArr = new TextView[3];
    //记录当前是选中的哪个送货时期，默认明天, 0今天，1明天，2后天
    private int selectedDate;
    private int selectedDateIndex;
    //缓存外部显示用的日期周几
    private String cachedDWStr;
    int mReserveGoodsAdvanceDate;
    private Handler handler = new Handler();
    public static final String INTENT_KEY_ORDER = "intent_key_order";
    OrderResponse.ListBean mOrder;

    private ArrayList<ProductData.ListBean> mProductList;//选择的商品

    private BottomDialog bDialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener() {
                @Override
                public void bindView(View v) {
                    initDefaultDate(v);
                }
            }).setLayoutRes(R.layout.date_layout)
            .setCancelOutside(true)
            .setDimAmount(0.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sumbit);
        ButterKnife.bind(this);
        mOrder = getIntent().getParcelableExtra(INTENT_KEY_ORDER);
        if (mOrder != null) {
            mTvTitle.setText("修改订单");
            mBtnSubmit.setText("修改订单");
        }
        mRvProductList.setLayoutManager(new LinearLayoutManager(mContext));

        mReserveGoodsAdvanceDate = GlobalApplication.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
        cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
        selectedDate = mReserveGoodsAdvanceDate;
        selectedDateIndex = 1;
        mTvDate.setText(cachedDWStr);
        OrderSubmitProductAdapter orderSubmitProductAdapter;
        orderSubmitProductAdapter = new OrderSubmitProductAdapter(getProductData());
        mRvProductList.setAdapter(orderSubmitProductAdapter);

        Object paramBean = null;
        sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USER_INFO, true, UserInfo.class);
        updateBottomBar();
    }

    List<ProductData.ListBean> getProductData() {
        mProductList = getIntent().getParcelableArrayListExtra(INTENT_KEY_PRODUCTS);
        return mProductList;
    }

    private void setUpDate(int dayDiff) {
        //送达日期
        long estimatedStamp = TimeUtils.getFormatTime(mOrder.getEstimatedTime());
        //下单日期
        long createTime = TimeUtils.stringToTimeStamp(mOrder.getCreateDate());
        String estimatedTimeStr;
        //最初下单的送达日期最小值
        long minStamp = createTime + 1000 * 3600 * 24 * (dayDiff - 1);
        if (TimeUtils.differentDaysByMillisecond(currentTimeMillis(), minStamp) > 0) {
            mReserveGoodsAdvanceDate = 1;
            estimatedTimeStr = TimeUtils.getMMdd(currentTimeMillis());
            cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(0);
            selectedDateIndex = 0;
            selectedDate = mReserveGoodsAdvanceDate - 1;
        } else {
            mReserveGoodsAdvanceDate = TimeUtils.differentDaysByMillisecond(createTime + dayDiff * 1000 * 3600 * 24, currentTimeMillis());
            if (estimatedStamp == createTime + dayDiff * 1000 * 3600 * 24) {
                estimatedTimeStr = TimeUtils.getMMdd(createTime + dayDiff * 1000 * 3600 * 24);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
                selectedDateIndex = 1;
                selectedDate = mReserveGoodsAdvanceDate;
            } else if (estimatedStamp > createTime + dayDiff * 1000 * 3600 * 24) {
                estimatedTimeStr = TimeUtils.getMMdd(estimatedStamp);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate + 1);
                selectedDateIndex = 2;
                selectedDate = mReserveGoodsAdvanceDate+1;
            } else {
                estimatedTimeStr = TimeUtils.getMMdd(estimatedStamp);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate - 1);
                selectedDateIndex = 0;
                selectedDate = mReserveGoodsAdvanceDate-1;
            }
        }
        mTvDate.setText(cachedDWStr);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        Intent intent;
        switch (where) {
            case REQUEST_USER_INFO:
                UserInfo userInfo = (UserInfo) result.getResult().getData();
                GlobalApplication.getInstance().saveUserInfo(userInfo);
                mReserveGoodsAdvanceDate = GlobalApplication.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
                cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
                selectedDate = mReserveGoodsAdvanceDate;
                selectedDateIndex = 1;
                setSelectedColor(1);
                if (mOrder != null) {
                    setUpDate(mReserveGoodsAdvanceDate);
                }
                break;
            case REQUEST_MODIFY:
                ToastUtil.show(mContext, "订单修改成功");
                EventBus.getDefault().post(new OrderChangedEvent());
                
                ActivityManager.getInstance().finishAll();//关闭所有的activity
                intent = new Intent(this, MainActivity.class);//重新打开首页
                startActivity(intent);

                BaseEntity.ResultBean bean = result.getResult();
                JSONArray jsonArray = (JSONArray) bean.getOrders();
                ArrayList<OrderResponse.ListBean> list = new ArrayList<>();
                list.addAll(JSON.parseArray(jsonArray.toString(), OrderResponse.ListBean.class));

                intent = new Intent(this, OrderCommitSuccessActivity.class);
                intent.putParcelableArrayListExtra(OrderCommitSuccessActivity.INTENT_KEY_ORDERS, list);
                intent.putExtra(OrderCommitSuccessActivity.INTENT_KEY_TYPE, 0);
                startActivity(intent);
                break;
            case REQUEST_SUBMIT:
                final OrderCommitResponse orderCommitResponse = (OrderCommitResponse) result.getResult().getData();
                if (mCustomProgressDialog != null) {
                    mCustomProgressDialog.dismiss();
                }
                ActivityManager.getInstance().finishAll();//关闭所有的activity
                intent = new Intent(this, MainActivity.class);//重新打开首页
                startActivity(intent);

                EventBus.getDefault().post(new OrderSuccessEvent());
                mBtnSubmit.setBackgroundColor(Color.parseColor("#9ACC35"));
                mRlDateOfService.setEnabled(true);

                //跳去中间页
                Intent intent2 = new Intent(this, OrderCommitSuccessActivity.class);
                intent2.putParcelableArrayListExtra(OrderCommitSuccessActivity.INTENT_KEY_ORDERS, orderCommitResponse.getOrders());
                startActivity(intent2);
//                Intent intent2 = new Intent(this, OrderSubmitSuccessActivity.class);
//                startActivity(intent2);
                //清空购物车
                //SPUtils.put(getActivityContext(), Constant.SP_KEY_CART, "");//删购物车
                CartManager.getInstance(this).clearCart(mProductList);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        if (mCustomProgressDialog != null) {
            mCustomProgressDialog.dismiss();
        }
        switch (where) {
            case REQUEST_SUBMIT:
                if (result.getResult() != null && "A1001".equals(result.getResult().getState())) {
                    ToastUtil.show(this, "订单操作频率过高，请稍后再试！");
                    mBtnSubmit.setBackgroundColor(Color.parseColor("#9ACC35"));
                    mBtnSubmit.setEnabled(true);
                    mRlDateOfService.setEnabled(true);
                    return;
                }
                mCustomProgressDialog.dismiss();
                dialog.setTitle("提示");
                dialog.setMessage("网络连接失败，请查看首页订单列表，检查下单是否成功");
                CartManager.getInstance(this).clearCart(mProductList);//更新购物车
                dialog.setMessageGravity();
                dialog.setModel(CustomDialog.RIGHT);
                dialog.setCancelable(false);
                dialog.setRightBtnListener("我知道啦", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送取消订单请求
                        ActivityManager.getInstance().finishAll();
                        startActivity(new Intent(getActivityContext(), MainActivity.class));
                    }
                });
                dialog.setLeftBtnListener("取消", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送取消订单请求
                        ActivityManager.getInstance().finishAll();
                        startActivity(new Intent(getActivityContext(), MainActivity.class));
                    }
                });
                dialog.show();
                break;
        }

    }

    @OnClick({R.id.title_iv_left, R.id.rl_date_of_service, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.rl_date_of_service:
                //弹出日期选择控件
                if (bDialog.isVisible()) {
                    bDialog.dismiss();
                } else {
                    bDialog.show();
                }
                break;
            case R.id.btn_submit:
                if (mOrder == null) {
                    submitOrder();//创建订单
                } else {
                    modifyOrder();//修改订单
                }
                break;
        }
    }

    /**
     * 下单
     */
    private void submitOrder() {
        //下单按钮
        CommitOrderRequest request = new CommitOrderRequest();
        request.setEstimated_time(TimeUtils.getAB2FormatData(selectedDate));
        request.setOrder_type_id("121");
        List<CommitOrderRequest.ProductsBean> cList = new ArrayList<>();
        for (ProductData.ListBean bean : mProductList) {
            CommitOrderRequest.ProductsBean pBean = new CommitOrderRequest.ProductsBean();
            pBean.setProduct_id(bean.getProductID());
            int qty = bean.getActualQty();
            if (qty == 0) {
                continue;
            }
            pBean.setQty(qty);
            cList.add(pBean);
        }
        if (cList.size() == 0) {
            toast("你还没选择任何商品!");
            return;
        }
        request.setProducts(cList);
        sendConnection("/gongfu/v2/order/create/", request, REQUEST_SUBMIT, false, OrderCommitResponse.class);
        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setMsg("下单中...");
        mCustomProgressDialog.show();
        mBtnSubmit.setBackgroundColor(Color.parseColor("#7F9ACC35"));
        mBtnSubmit.setEnabled(false);
        mRlDateOfService.setEnabled(false);
    }

    /**
     * 修改订单
     */
    private void modifyOrder() {
        //下单按钮
        CommitOrderRequest request = new CommitOrderRequest();
        request.setEstimated_time(TimeUtils.getAB2FormatData(selectedDate));
        List<CommitOrderRequest.ProductsBean> cList = new ArrayList<>();
        for (ProductData.ListBean bean : mProductList) {
            CommitOrderRequest.ProductsBean pBean = new CommitOrderRequest.ProductsBean();
            pBean.setProduct_id(bean.getProductID());
            int qty = bean.getActualQty();
            if (qty == 0) {
                continue;
            }
            pBean.setQty(qty);
            cList.add(pBean);
        }
        request.setProducts(cList);
        StringBuffer sb = new StringBuffer("/gongfu/order/");
        sb.append(mOrder.getOrderID()).append("/modify/");
        sendConnection(sb.toString(), request, REQUEST_MODIFY, true, BaseEntity.ResultBean.class);
    }

    private void initDefaultDate(View v) {
        RelativeLayout rll1 = (RelativeLayout) v.findViewById(R.id.rll1);
        RelativeLayout rll2 = (RelativeLayout) v.findViewById(R.id.rll2);
        RelativeLayout rll3 = (RelativeLayout) v.findViewById(R.id.rll3);
        TextView wTv1 = (TextView) v.findViewById(R.id.wTv1);
        TextView dTv1 = (TextView) v.findViewById(R.id.dTv1);
        TextView wTv2 = (TextView) v.findViewById(R.id.wTv2);
        TextView dTv2 = (TextView) v.findViewById(R.id.dTv2);
        TextView wTv3 = (TextView) v.findViewById(R.id.wTv3);
        TextView dTv3 = (TextView) v.findViewById(R.id.dTv3);
        wArr[0] = wTv1;
        wArr[1] = wTv2;
        wArr[2] = wTv3;
        dArr[0] = dTv1;
        dArr[1] = dTv2;
        dArr[2] = dTv3;
        //选中哪个，通过selectedDate来判断
        wArr[selectedDateIndex].setTextColor(Color.parseColor("#6BB400"));
        dArr[selectedDateIndex].setTextColor(Color.parseColor("#6BB400"));
        //计算当前日期起，明后天的星期几+号数
        wTv1.setText(TimeUtils.getWeekStr(mReserveGoodsAdvanceDate - 1));
        String[] t = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate - 1).split("-");
        if (t.length > 2) {
            dTv1.setText(t[1] + "-" + t[2]);
        }
        wTv2.setText(TimeUtils.getWeekStr(mReserveGoodsAdvanceDate));
        t = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).split("-");
        if (t.length > 2) {
            dTv2.setText(t[1] + "-" + t[2]);
        }
        wTv3.setText(TimeUtils.getWeekStr(mReserveGoodsAdvanceDate + 1));
        t = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate + 1).split("-");
        if (t.length > 2) {
            dTv3.setText(t[1] + "-" + t[2]);
        }
        //初始化点击事件
        rll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空颜色
                setSelectedColor(0);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectedDate = mReserveGoodsAdvanceDate - 1;
                        selectedDateIndex = 0;
                        bDialog.dismiss();
                        mTvDate.setText(TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate - 1).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate - 1));
                    }
                }, 500);
            }
        });
        rll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空颜色
                setSelectedColor(1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectedDate = mReserveGoodsAdvanceDate;
                        selectedDateIndex = 1;
                        bDialog.dismiss();
                        mTvDate.setText(TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate));
                    }
                }, 500);
            }
        });
        rll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空颜色
                setSelectedColor(2);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectedDate = mReserveGoodsAdvanceDate + 1;
                        selectedDateIndex = 2;
                        bDialog.dismiss();
                        mTvDate.setText(TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate + 1).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate + 1));
                    }
                }, 500);
            }
        });
    }

    //参数从0开始
    private void setSelectedColor(int i) {
        for (TextView tv : wArr) {
            if (tv != null) {
                tv.setTextColor(Color.parseColor("#2E2E2E"));
            }
        }
        for (TextView tv : dArr) {
            if (tv != null) {
                tv.setTextColor(Color.parseColor("#2E2E2E"));
            }
        }
        if (wArr[i] != null) {
            wArr[i].setTextColor(Color.parseColor("#6BB400"));
        }
        if (dArr[i] != null) {
            dArr[i].setTextColor(Color.parseColor("#6BB400"));
        }
    }

    /**
     * 更新底部bar
     */
    protected void updateBottomBar() {
        double totalMoney = 0;
        int totalNum = 0;
        for (ProductData.ListBean bean : mProductList) {
            totalMoney = totalMoney + bean.getPrice() * bean.getActualQty();
            totalNum = totalNum + bean.getActualQty();
        }
        if (GlobalApplication.getInstance().getCanSeePrice()) {
            DecimalFormat df = new DecimalFormat("#.##");
            mTvTotalMoney.setVisibility(View.VISIBLE);
            mTvTotalMoney.setText("￥" + df.format(totalMoney));//TODO:format
        } else {
            mTvTotalMoney.setVisibility(View.GONE);
        }
        mTvProductNum.setText("共" + totalNum + "件");
    }
}
