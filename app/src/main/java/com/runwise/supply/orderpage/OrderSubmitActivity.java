package com.runwise.supply.orderpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.OrderSuccessEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.PlaceOrderTimeStatisticsUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
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
import com.runwise.supply.view.DateServiceDialog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_DATE_OF_SERVICE;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ORDER_AGAIN;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ORDER_MODIFY;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ORDER_SUBMIT_ALWAY;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ORDER_SUBMIT_SELF;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ORDER_SUBMIT_SMART;
import static com.runwise.supply.orderpage.ProductActivityV2.PLACE_ORDER_TYPE_AGAIN;
import static com.runwise.supply.orderpage.ProductActivityV2.PLACE_ORDER_TYPE_ALWAYS;
import static com.runwise.supply.orderpage.ProductActivityV2.PLACE_ORDER_TYPE_MODIFY;
import static com.runwise.supply.orderpage.ProductActivityV2.PLACE_ORDER_TYPE_SELF;
import static com.runwise.supply.orderpage.ProductActivityV2.PLACE_ORDER_TYPE_SMART;

/**
 * 下单页 以及 修改订单页，取决于有没有传订单对象
 */
public class OrderSubmitActivity extends NetWorkActivity {
    public static final String INTENT_KEY_SELF_HELP = "is_normal_order";//是不是一般下单流程
    public static final String INTENT_KEY_PRODUCTS = "intent_products";
    public static final int REQUEST_USER_INFO = 1 << 0;
    public static final int REQUEST_SUBMIT = 2000;
    public static final int REQUEST_DUPLICATE = 2500;
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
    //记录当前是选中的哪个送货时期，默认明天, 0今天，1明天，2后天
    private int selectedDate;
    //缓存外部显示用的日期周几
    private String cachedDWStr;
    int mReserveGoodsAdvanceDate;
    public static final String INTENT_KEY_ORDER = "intent_key_order";
    OrderResponse.ListBean mOrder;

    private ArrayList<ProductData.ListBean> mProductList;//选择的商品
    private TempOrderManager.TempOrder mTempOrder;//本地记录提交中的订单
    private boolean isSelfHelpOrder;//是否一般下单流程，是的话需要清空购物车缓存；再来一单、智能下单不需要清空购物车
    protected int mPlaceOrderType;
    public static final String  INTENT_KEY_PLACE_ORDER_TYPE = "intent_key_place_order_type";

    private DateServiceDialog mCustomDatePickerDialog;
    DateServiceDialog.DateServiceListener mPickerClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sumbit);
        ButterKnife.bind(this);

        mOrder = getIntent().getParcelableExtra(INTENT_KEY_ORDER);
        mPlaceOrderType = getIntent().getIntExtra(INTENT_KEY_PLACE_ORDER_TYPE,-1);
        isSelfHelpOrder = getIntent().getBooleanExtra(INTENT_KEY_SELF_HELP,false);
        if (mOrder != null) {
            mTvTitle.setText("修改订单");
            mBtnSubmit.setText("修改订单");
        }
        mRvProductList.setLayoutManager(new LinearLayoutManager(mContext));

        mReserveGoodsAdvanceDate = GlobalApplication.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
        cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
        selectedDate = mReserveGoodsAdvanceDate;
        mTvDate.setText(cachedDWStr);

        mCustomDatePickerDialog = new DateServiceDialog(this,mReserveGoodsAdvanceDate);
        mPickerClickListener = new DateServiceDialog.DateServiceListener() {
            @Override
            public void onSelect(String ymd) {
                selectedDate = (int) TimeUtils.dateDiff(TimeUtils.getCurrentDate(),ymd,"yyyy-MM-dd");
                mTvDate.setText(ymd.substring(5) + " " + TimeUtils.getWeekStr(selectedDate));
                mCustomDatePickerDialog.dismiss();
            }
        };
        mCustomDatePickerDialog.setDateServiceListener(mPickerClickListener);
        mCustomDatePickerDialog.setCurrentItem(selectedDate-1);

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

    private void setUpDate() {
        //送达日期
        String estimatedStampStr = mOrder.getEstimatedTime().split(" ")[0];
        int diff = (int) TimeUtils.dateDiff(TimeUtils.getCurrentDate(),estimatedStampStr,"yyyy-MM-dd");
        if (diff < mReserveGoodsAdvanceDate-1){
        //订单原有的送达日期已过期
            cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
            selectedDate = mReserveGoodsAdvanceDate;
        }else{
            cachedDWStr = TimeUtils.getABFormatDate(diff).substring(5) + " " + TimeUtils.getWeekStr(diff);
            selectedDate = diff;
        }
        mCustomDatePickerDialog.setTime(cachedDWStr.substring(0,5));
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
                if (mOrder != null) {
                    setUpDate();
                }else{
                    mCustomDatePickerDialog.setCurrentItem(1);
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
                PlaceOrderTimeStatisticsUtil.upload(getActivityContext());
                break;
            case REQUEST_SUBMIT://下单
            case REQUEST_DUPLICATE://确认的重复下单
                if (mCustomProgressDialog != null) {
                    mCustomProgressDialog.dismiss();
                }
                ActivityManager.getInstance().finishAll();//关闭所有的activity
                intent = new Intent(this, MainActivity.class);//重新打开首页
                startActivity(intent);
                PlaceOrderTimeStatisticsUtil.upload(getActivityContext());

                EventBus.getDefault().post(new OrderSuccessEvent());
                mBtnSubmit.setBackgroundColor(Color.parseColor("#9ACC35"));
                mRlDateOfService.setEnabled(true);

                //跳去中间页
//                final OrderCommitResponse orderCommitResponse = (OrderCommitResponse) result.getResult().getData();
//                Intent intent2 = new Intent(this, OrderCommitSuccessActivity.class);
//                intent2.putParcelableArrayListExtra(OrderCommitSuccessActivity.INTENT_KEY_ORDERS, orderCommitResponse.getOrders());
//                startActivity(intent2);

                //异步过程，本地记录记录下单信息
                TempOrderManager.getInstance(this).saveTempOrderAsync(mTempOrder);
                //新的中间页
                Intent intent2 = new Intent(this, OrderSubmitSuccessActivity.class);
                startActivity(intent2);
                //更新购物车缓存
                if(isSelfHelpOrder)CartManager.getInstance(this).clearCart(mProductList);
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
            case REQUEST_DUPLICATE:
                //接口返回重复下单
                if (result.getResult() != null && "A1001".equals(result.getResult().getState())) {

                    CustomDialog dialog = new CustomDialog(this);
                    dialog.setCancelable(false);
                    dialog.setTitleGone();
                    dialog.setMessageGravity();
                    dialog.setMessage("订单内容重复\n请确认是否再次提交");
                    dialog.setRightBtnListener("我再看看", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {
                            //重新开放提交按钮
                            mBtnSubmit.setBackgroundColor(Color.parseColor("#9ACC35"));
                            mBtnSubmit.setEnabled(true);
                            mRlDateOfService.setEnabled(true);
                            dialog.dismiss();
                        }
                    });
                    dialog.setLeftBtnListener("确认提交", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {
                            submitDuplicateOrder();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return;

                }
                //一般失败
                mCustomProgressDialog.dismiss();
                dialog.setTitle("提示");
                dialog.setMessage("网络连接失败，请查看首页订单列表，检查下单是否成功");
                if(isSelfHelpOrder)CartManager.getInstance(this).clearCart(mProductList);//更新购物车缓存
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
                default:
                    if(!TextUtils.isEmpty(errMsg))ToastUtil.show(this,errMsg);
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
                MobclickAgent.onEvent(getActivityContext(), EVENT_ID_DATE_OF_SERVICE);
                //弹出日期选择控件
                mCustomDatePickerDialog.show();
                break;
            case R.id.btn_submit:
                switch (mPlaceOrderType){
                    case PLACE_ORDER_TYPE_ALWAYS:
                        MobclickAgent.onEvent(getActivityContext(), EVENT_ID_ORDER_SUBMIT_ALWAY);
                        break;
                    case PLACE_ORDER_TYPE_SELF:
                        MobclickAgent.onEvent(getActivityContext(), EVENT_ID_ORDER_SUBMIT_SELF);
                        break;
                    case PLACE_ORDER_TYPE_SMART:
                        MobclickAgent.onEvent(getActivityContext(), EVENT_ID_ORDER_SUBMIT_SMART);
                        break;
                    case PLACE_ORDER_TYPE_AGAIN:
                        MobclickAgent.onEvent(getActivityContext(), EVENT_ID_ORDER_AGAIN);
                        break;
                    case PLACE_ORDER_TYPE_MODIFY:
                        MobclickAgent.onEvent(getActivityContext(), EVENT_ID_ORDER_MODIFY);
                        break;
                }

                if (mOrder == null) {
                    submitOrder();//创建订单
                } else {
                    modifyOrder();//修改订单
                }
                break;
        }
    }

    CommitOrderRequest request;
    /**
     * 下单
     */
    private void submitOrder() {
        //检查网络连接状态
        if(!CommonUtils.isNetworkConnected(this)){
            Toast.makeText(this,R.string.network_error,Toast.LENGTH_LONG).show();
            return;
        }
        //禁用下单按钮
        mBtnSubmit.setBackgroundColor(Color.parseColor("#7F9ACC35"));
        mBtnSubmit.setEnabled(false);
        //保留request，用于确认重复下单时重发
        request = new CommitOrderRequest();
        request.setEstimated_time(TimeUtils.getAB2FormatData(selectedDate));
        request.setOrder_type_id("121");
        List<CommitOrderRequest.ProductsBean> cList = new ArrayList<>();
        for (ProductData.ListBean bean : mProductList) {
            CommitOrderRequest.ProductsBean pBean = new CommitOrderRequest.ProductsBean();
            pBean.setProduct_id(bean.getProductID());
            double qty = bean.getActualQty();
            if (qty == 0) {
                continue;
            }
            pBean.setQty(qty);
            pBean.setRemark(bean.getRemark()==null?"":bean.getRemark());//注意必须传remark
            cList.add(pBean);
        }
        if (cList.size() == 0) {
            toast("你还没选择任何商品!");
            return;
        }
        request.setProducts(cList);
        request.setHash(String.valueOf(System.currentTimeMillis()));
//        sendConnection("/gongfu/v2/order/create/", request, REQUEST_SUBMIT, false, OrderCommitResponse.class);
        sendConnection("/api/order/async/create", request, REQUEST_SUBMIT, false, OrderCommitResponse.class);
        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setMsg("下单中...");
        mCustomProgressDialog.show();
        mRlDateOfService.setEnabled(false);
        //记录订单信息,接口返回后才写入存储
        mTempOrder = new TempOrderManager.TempOrder();
        //只取日期
        mTempOrder.setEstimateDate(request.getEstimated_time().split(" ")[0]);
        mTempOrder.setHashKey(request.getHash());
        mTempOrder.setProductList(mProductList);
    }

    /**
     * 接口返回重复下单提醒后，重复提交订单
     */
    private void submitDuplicateOrder(){
        mBtnSubmit.setBackgroundColor(Color.parseColor("#7F9ACC35"));
        mBtnSubmit.setEnabled(false);
        mRlDateOfService.setEnabled(false);
        //复用request，新的下单请求用新的hash
        request.setHash(String.valueOf(System.currentTimeMillis()));
        sendConnection("/api/duplicate/order/async/create", request, REQUEST_DUPLICATE, false, OrderCommitResponse.class);
        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.setMsg("下单中...");
        mCustomProgressDialog.show();
        //记录订单信息,接口返回后才写入存储
        mTempOrder = new TempOrderManager.TempOrder();
        //只取日期
        mTempOrder.setEstimateDate(request.getEstimated_time().split(" ")[0]);
        mTempOrder.setHashKey(request.getHash());
        mTempOrder.setProductList(mProductList);
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
            double qty = bean.getActualQty();
            if (qty == 0) {
                continue;
            }
            pBean.setQty(qty);
            pBean.setRemark(bean.getRemark()==null?"":bean.getRemark());//注意必须传remark
            cList.add(pBean);
        }
        request.setProducts(cList);
        StringBuffer sb = new StringBuffer("/gongfu/order/");
        sb.append(mOrder.getOrderID()).append("/modify/");
        sendConnection(sb.toString(), request, REQUEST_MODIFY, true, BaseEntity.ResultBean.class);
    }



    /**
     * 更新底部bar
     */
    protected void updateBottomBar() {
        double totalMoney = 0;
        double totalNum = 0;
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
        mTvProductNum.setText("共" + NumberUtil.getIOrD(totalNum) + "件");
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("提交订单页");
        MobclickAgent.onResume(this);          //统计时长
        PlaceOrderTimeStatisticsUtil.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("提交订单页");
        MobclickAgent.onPause(this);          //统计时长
        PlaceOrderTimeStatisticsUtil.onPause(getActivityContext());
    }
}
