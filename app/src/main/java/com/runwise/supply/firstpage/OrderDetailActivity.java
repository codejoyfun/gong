package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.OrderDetailResponse;
import com.runwise.supply.event.IntEvent;
import com.runwise.supply.firstpage.entity.CancleRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderState;
import com.runwise.supply.firstpage.entity.ReturnDetailResponse;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;
import me.shaohui.bottomdialog.BottomDialog;

import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderDetailActivity extends NetWorkActivity {
    private static final int UPLOAD = 100;
    private static final int DETAIL = 1;          //网络请求
    private static final int CANCEL = 2;
    public static final int CATEGORY = 3333;
    public static final int RETURN_DETAIL = 4;
    public static final int PRODUCT_DETAIL = 5;
    public static final int REQUEST_USERINFO_TRANSFER = 6;
    private ListBean bean;
    private List<OrderResponse.ListBean.LinesBean> listDatas = new ArrayList<>();
    private List<OrderResponse.ListBean.LinesBean> typeDatas = new ArrayList<>();
    private OrderDtailAdapter adapter;
    @ViewInject(R.id.dateTv)
    private TextView dateTv;
    @ViewInject(R.id.orderStateTv)
    private TextView orderStateTv;
    @ViewInject(R.id.tipTv)
    private TextView tipTv;
    @ViewInject(R.id.orderNumTv)
    private TextView orderNumTv;
    @ViewInject(R.id.buyerValue)
    private TextView buyerValue;
    @ViewInject(R.id.orderTimeValue)
    private TextView orderTimeValue;
    @ViewInject(R.id.payStateTv)
    private TextView payStateTv;
    @ViewInject(R.id.payStateValue)
    private TextView payStateValue;
    @ViewInject(R.id.uploadBtn)
    private TextView uploadBtn;
    @ViewInject(R.id.receivtTv)
    private TextView receivtTv;
    @ViewInject(R.id.returnTv)
    private TextView returnTv;
    @ViewInject(R.id.ygMoneyTv)
    private TextView ygMoneyTv;
    @ViewInject(R.id.ygMoney)
    private TextView ygMoney;
    @ViewInject(R.id.countTv)
    private TextView countTv;
    @ViewInject(R.id.gotoStateBtn)
    private Button gotoStateBtn;    //查看更多状态
    @ViewInject(R.id.rightBtn2)
    private Button rightBtn2;
    @ViewInject(R.id.rightBtn)
    private Button rightBtn;
    @ViewInject(R.id.returnLL)
    private View returnLL;

    @ViewInject(R.id.returnContainer)
    private LinearLayout returnContainer;
    @ViewInject(R.id.bottom_bar)
    private RelativeLayout bottom_bar;
    private boolean isHasAttachment;        //默认无凭证
    @ViewInject(R.id.priceLL)
    private View priceLL;
    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.viewpager)
    private ViewPager viewpager;
    @ViewInject(R.id.drag_layout)
    private DragTopLayout dragLayout;
    @ViewInject(R.id.tv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.v_space)
    private View v_space;
    private boolean isModifyOrder;          //可修改订单
    private int orderId;                    //如果有orderId, 需要重新刷新

    public static final int TAB_EXPAND_COUNT = 4;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private BottomDialog bDialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener() {
                @Override
                public void bindView(View v) {
                    initDialogViews(v);
                }
            }).setLayoutRes(R.layout.return_replace_layout)
            .setCancelOutside(true)
            .setDimAmount(0.5f);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.order_detail_layout);
//        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)){
//            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
//        }
        setTitleText(true, "订单详情");
        setTitleLeftIcon(true, R.drawable.nav_back);
        Bundle bundle = getIntent().getExtras();
        adapter = new OrderDtailAdapter(mContext);
        orderId = bundle.getInt("orderid", 0);
        boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        if (!canSeePrice) {
            priceLL.setVisibility(View.GONE);
        }
        bean = bundle.getParcelable("order");
        if (bean != null) {
            orderId = bean.getOrderID();
        }
        //需要自己刷新
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/order/");
        sb.append(orderId).append("/");
        sendConnection(sb.toString(), request, DETAIL, false, OrderDetailResponse.class);
        loadingLayout.setStatusLoading();
        dragLayout.setOverDrag(false);
    }

    private void getReturnOrder(String rid) {
        //发网络请求获取
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/return_order/");
        sb.append(rid).append("/");
        sendConnection(sb.toString(), request, RETURN_DETAIL, false, ReturnDetailResponse.class);
    }

    @OnClick({R.id.title_iv_left, R.id.title_tv_rigth, R.id.uploadBtn, R.id.gotoStateBtn, R.id.rightBtn, R.id.rightBtn2, R.id.tv_open})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.gotoStateBtn:
                if (bean != null) {
                    Intent intent = new Intent(this, OrderStateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("order", bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.title_tv_rigth:
                if (isModifyOrder) {
                    Intent mIntent = new Intent(this, OrderModifyActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable("order", bean);
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                    finish();
                } else {
                    if (!bDialog.isVisible()) {
                        bDialog.show();
                    } else {
                        bDialog.dismiss();
                    }
                }
                break;
            case R.id.rightBtn:
                Intent intent2;
                OrderDoAction action = OrderActionUtils.getDoActionByText(rightBtn.getText().toString(), bean);
                switch (action) {
                    case CANCLE:
                        dialog.setTitle("提示");
                        dialog.setMessage("确认取消订单?");
                        dialog.setMessageGravity();
                        dialog.setModel(CustomDialog.BOTH);
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                //发送取消订单请求
                                cancleOrderRequest();
                            }
                        });
                        dialog.show();
                        break;
                    case TALLY:
                        if (bean != null) {
                            Intent tIntent = new Intent(mContext, ReceiveActivity.class);
                            Bundle tBundle = new Bundle();
                            tBundle.putParcelable("order", bean);
                            tBundle.putInt("mode", 1);
                            tIntent.putExtras(tBundle);
                            startActivity(tIntent);
                        }
                        break;
                    case TALLYING:
                        if (bean != null) {
                            String name = bean.getTallyingUserName();
                            dialog.setMessageGravity();
                            dialog.setMessage(name + "正在点货");
                            dialog.setModel(CustomDialog.RIGHT);
                            dialog.setRightBtnListener("我知道了", new CustomDialog.DialogListener() {
                                @Override
                                public void doClickButton(Button btn, CustomDialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                        break;
                    case RATE:
                        if (bean != null) {
                            Intent rIntent = new Intent(mContext, EvaluateActivity.class);
                            Bundle rBundle = new Bundle();
                            rBundle.putParcelable("order", bean);
                            rIntent.putExtras(rBundle);
                            startActivity(rIntent);
                        }
                        break;
                    case RECEIVE://正常收货
                        if (bean != null) {
                            Intent reIntent = new Intent(mContext, ReceiveActivity.class);
                            Bundle reBundle = new Bundle();
                            reBundle.putParcelable("order", bean);
                            reBundle.putInt("mode", 0);
                            reIntent.putExtras(reBundle);
                            startActivity(reIntent);
                        }
                        break;
                    case SETTLERECEIVE:
                        //点货，计入结算单位
                        Intent sIntent = new Intent(mContext, ReceiveActivity.class);
                        Bundle sBundle = new Bundle();
                        sBundle.putParcelable("order", bean);
                        sBundle.putInt("mode", 2);
                        sIntent.putExtras(sBundle);
                        startActivity(sIntent);
                        break;
                    case SELFTALLY:
                        dialog.setMessageGravity();
                        dialog.setMessage("您已经点过货了，应由其他人完成收货");
                        dialog.setModel(CustomDialog.RIGHT);
                        dialog.setRightBtnListener("我知道了", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case DELETE:
                        //TODO
                        break;
                }
//               if (rightBtn.getText().toString().equals("收货")){
//                    intent2 = new Intent(mContext,ReceiveActivity.class);
//                    Bundle bundle2 = new Bundle();
//                    bundle2.putParcelable("order",bean);
//                    intent2.putExtras(bundle2);
//                    startActivity(intent2);
//                    finish();
//                }else if(rightBtn.getText().toString().equals("评价")){
//                    intent2 = new Intent(mContext,EvaluateActivity.class);
//                    Bundle bundle2 = new Bundle();
//                    bundle2.putParcelable("order",bean);
//                    intent2.putExtras(bundle2);
//                    startActivity(intent2);
//                    finish();
//                }
                break;
            case R.id.uploadBtn:
                //凭证
                if (bean != null) {
                    Intent intent3 = new Intent(mContext, UploadPayedPicActivity.class);
                    intent3.putExtra("orderid", bean.getOrderID());
                    intent3.putExtra("ordername", bean.getName());
                    intent3.putExtra("hasattachment", isHasAttachment);
                    if (!bean.getState().equals(OrderState.DRAFT.getName()) && bean.getOrderSettleName().contains("单次结算")
                            && bean.getOrderSettleName().contains("先付款后收货")) {
                        intent3.putExtra(UploadPayedPicActivity.INTENT_KEY_CANN_NO_EDIT, true);
                    }
                    startActivityForResult(intent3, UPLOAD);
                }
                break;
            case R.id.tv_open:
                if (dragLayout.getState() == DragTopLayout.PanelState.EXPANDED) {
                    dragLayout.toggleTopView();
                    canShow = true;
                } else {
                    if (mProductTypeWindow.isShowing()) {
                        mProductTypeWindow.dismiss();
                    } else {
                        showPopWindow();
                    }
                }
                dragLayout.listener(new DragTopLayout.PanelListener() {
                    @Override
                    public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
                        if (panelState == DragTopLayout.PanelState.COLLAPSED) {
                            if (canShow) {
                                showPopWindow();
                                canShow = false;
                            }
                        } else {
                            mProductTypeWindow.dismiss();
                        }
                    }

                    @Override
                    public void onSliding(float ratio) {

                    }

                    @Override
                    public void onRefresh() {

                    }
                });

                break;
        }
    }

    private void requestCustomerService() {
        Object paramBean = null;
        this.sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USERINFO_TRANSFER, false, UserInfo.class);
    }

    boolean canShow = false;

    private void showPopWindow() {
        int y = findViewById(R.id.title_bar).getHeight() + tablayout.getHeight();
        mProductTypeWindow.showAtLocation(getRootView(OrderDetailActivity.this), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewpager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    CategoryRespone categoryRespone;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case DETAIL:
                BaseEntity.ResultBean resultBean = result.getResult();
                OrderDetailResponse response = (OrderDetailResponse) resultBean.getData();
                bean = response.getOrder();
                adapter.setStatus(bean.getName(), bean.getState(), bean);
                GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
                sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
                loadingLayout.onSuccess(1, "暂时没有数据哦");
                break;
            case CANCEL:
                finish();
                break;
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                updateUI();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dragLayout.openTopView(false);
                    }
                }, 200);
                break;
            case RETURN_DETAIL:
                BaseEntity.ResultBean rb = result.getResult();
                ReturnDetailResponse rdr = (ReturnDetailResponse) rb.getData();
                if (rdr.getReturnOrder() != null) {
                    setUpReturnOrderView(bean.getReturnOrders().get(0), rdr.getReturnOrder().getName());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dragLayout.openTopView(false);
                        }
                    }, 500);
                    bean.getReturnOrders().remove(0);
                    //可能有多个退货单。
                    if (bean.getReturnOrders().size() > 0) {
                        String returnId = bean.getReturnOrders().get(0);
                        getReturnOrder(returnId);
                    }

                }
                break;
            case PRODUCT_DETAIL:
                ProductOne productOne = (ProductOne) result.getResult().getData();
                ProductBasicList.ListBean listBean = productOne.getProduct();
                //保存进缓存
                missingLinesBean.remove(listBean.getProductID());
                if (cacheProductInfo == null) cacheProductInfo = new ArrayList<>();
                ProductBasicUtils.getBasicMap(this).put(listBean.getProductID() + "", listBean);//更新内存缓存
                cacheProductInfo.add(listBean);
                if (missingLinesBean.size() == 0) {//所有都返回了
                    ProductBasicUtils.saveProductInfoAsync(this, cacheProductInfo);
                    //刷新页面
                    setUpDataForViewPage();
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case UPLOAD:
                //来判断上传页有没有图片
                if (resultCode == 200) {
                    isHasAttachment = data.getBooleanExtra("has", false);
                    if (isHasAttachment) {
                        payStateValue.setText("已上传支付凭证");
                        uploadBtn.setText("查看凭证");
                    } else {
                        payStateValue.setText("未有支付凭证");
                        uploadBtn.setText("上传凭证");
                    }
                }
                break;
        }
    }

    private void initDialogViews(View v) {
        TextView returnTv = (TextView) v.findViewById(R.id.returnTv);
        TextView replaceTv = (TextView) v.findViewById(R.id.replaceTv);
        TextView cancleTv = (TextView) v.findViewById(R.id.cancleTv);
        returnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到退换流程：如果超过7天,不支持退货
                if (isMoreThanReturnData()) {
                    dialog.setMessage("已超过7天无理由退货时间\n如有其他问题请联系客服");
                    dialog.setMessageGravity();
                    dialog.setModel(CustomDialog.RIGHT);
                    dialog.setRightBtnListener("我知道了", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {

                        }
                    });
                    dialog.show();
                    bDialog.dismiss();
                } else {
                    Intent intent = new Intent(OrderDetailActivity.this, ReturnActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("order", bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    bDialog.dismiss();
                    finish();
                }
            }
        });
        replaceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(mContext, "暂不支持");
            }
        });
        cancleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bDialog.dismiss();
            }
        });
    }

    private boolean isMoreThanReturnData() {
        return TimeUtils.isMoreThan7Days(bean.getDoneDatetime());
    }

    private void setUpReturnOrderView(final String returnId, String returnName) {
        TextView tv = new TextView(mContext);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor("#999999"));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTag(returnId);
        if (!TextUtils.isEmpty(returnName)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    CommonUtils.dip2px(mContext, 40));
            SpannableString ss = new SpannableString("退货单号：" + returnName);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#2F96D8")), 5, 5 + returnName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tv.setText(ss);
            returnContainer.addView(tv, params);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到退货单详情
                    Intent intent = new Intent(mContext, ReturnDetailActivity.class);
                    intent.putExtra("rid", returnId);
                    startActivity(intent);
                }
            });
        }
    }

    private void updateUI() {
        if (bean != null) {
            if (bean.getHasReturn() != 0) {
                adapter.setHasReturn(true);
                returnLL.setVisibility(View.VISIBLE);
                //可能有多个退货单。
                if (bean.getReturnOrders().size() > 0) {
                    String returnId = bean.getReturnOrders().get(0);
                    getReturnOrder(returnId);
                }
            }
            if (bean.isIsTwoUnit()) {
                adapter.setTwoUnit(true);
            }
            String state = "";
            String tip = "";
            if (bean.getState().equals("draft")) {
                state = "订单已提交";
                tip = "订单号：" + bean.getName();
            } else if (bean.getState().equals("sale")) {
                state = "订单已确认";
                tip = "正在为您挑拣商品";
                ViewGroup.LayoutParams lp = bottom_bar.getLayoutParams();
                lp.height = 0;
                bottom_bar.setLayoutParams(lp);
//                bottom_bar.setVisibility(View.GONE);
//                setBottom(v_space);
            } else if (bean.getState().equals("peisong")) {
                state = "订单已发货";
                tip = "预计发达时间：" + bean.getEstimatedTime();
            } else if (bean.getState().equals("done")) {
                state = "订单已收货";
                String recdiveName = bean.getReceiveUserName();
//                tip = "收货人："+ recdiveName;
                tip = "配送已完成，如有问题请联系客服";
                //TODO:退货单没有收货人姓名，暂时处理
                if (TextUtils.isEmpty(recdiveName)) {
                    tip = "已退货";
                    state = "订单已退货";
                }
                if (!TextUtils.isEmpty(bean.getAppraisalUserName())) {
                    //已评价
//                    bottom_bar.setVisibility(View.GONE);
                    ViewGroup.LayoutParams lp = bottom_bar.getLayoutParams();
                    lp.height = 0;
                    bottom_bar.setLayoutParams(lp);
                }
                //预计价钱改为，商品金额
                ygMoney.setText("商品金额");
            } else if (bean.getState().equals("rated")) {
                state = "订单已评价";
//                bottom_bar.setVisibility(View.GONE);
                ViewGroup.LayoutParams lp = bottom_bar.getLayoutParams();
                lp.height = 0;
                bottom_bar.setLayoutParams(lp);
                tip = "感谢您的评价，供鲜生祝您生活愉快！";
            } else if ("cancel".equals(bean.getState())) {
                state = "订单已取消";
                tip = "您的订单已取消成功";
            }
            orderStateTv.setText(state);
            tipTv.setText(tip);
            rightBtn.setText(OrderActionUtils.getDoBtnTextByState(bean));
            dateTv.setText(TimeUtils.getMMdd(bean.getCreateDate()));

            if (bean.getOrderSettleName().contains("先付款后收货") && bean.getOrderSettleName().contains("单次结算")) {
                setUpPaymenInstrument();
            }

            //支付凭证在收货流程后，才显示
            if ((bean.getState().equals("rated") || bean.getState().equals("done"))
                    && bean.getOrderSettleName().contains("单次结算")) {
                setUpPaymenInstrument();
            }
            if (bean.getState().equals(OrderState.DRAFT.getName())) {
                setTitleRightText(true, "修改");
                isModifyOrder = true;
            }
            if (!bean.isUnApplyService()&&(bean.getState().equals("rated") || bean.getState().equals("done"))) {
                //同时，显示右上角，申请售后
                setTitleRightText(true, "申请售后");
            }
            //订单信息
            orderNumTv.setText(bean.getName());
            buyerValue.setText(bean.getCreateUserName());
            orderTimeValue.setText(bean.getCreateDate());
            if (bean.getHasReturn() == 0) {
                returnTv.setVisibility(View.INVISIBLE);
            } else {
                returnTv.setVisibility(View.VISIBLE);
            }
            //实收判断
            if ((Constant.ORDER_STATE_DONE.equals(bean.getState()) || Constant.ORDER_STATE_RATED.equals(bean.getState())) && isShiShou()) {
                receivtTv.setVisibility(View.VISIBLE);
                countTv.setText((int) bean.getDeliveredQty() + "件");
            } else {
                receivtTv.setVisibility(View.GONE);
                countTv.setText((int) bean.getAmount() + "件");
            }
            //商品数量/预估金额
            DecimalFormat df = new DecimalFormat("#.##");
            ygMoneyTv.setText("¥" + df.format(bean.getAmountTotal()));
//            countTv.setText((int)bean.getAmount()+"件");
            //设置list
            listDatas = bean.getLines();
            setUpDataForViewPage();

        }
    }

    private void setUpPaymenInstrument() {
        payStateTv.setVisibility(View.VISIBLE);
        payStateValue.setVisibility(View.VISIBLE);
        uploadBtn.setVisibility(View.VISIBLE);
        if (bean.getHasAttachment() == 0) {
            payStateValue.setText("未有支付凭证");
            uploadBtn.setText("上传凭证");
            isHasAttachment = false;
        } else {
            payStateValue.setText("已上传支付凭证");
            uploadBtn.setText("查看凭证");
            isHasAttachment = true;
        }
        isModifyOrder = false;
    }

    /**
     * 是否实收
     *
     * @return
     */
    private boolean isShiShou() {
        for (OrderResponse.ListBean.LinesBean linesBean : bean.getLines()) {
            if (linesBean.getDeliveredQty() != linesBean.getProductUomQty()) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setBottom(View v) {
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) v.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        v.setLayoutParams(params);
    }

    SparseArray<ListBean.LinesBean> missingLinesBean = new SparseArray<>();//本地数据库没有这个商品的数据，需要查询接口
    List<ProductBasicList.ListBean> cacheProductInfo;

    private void setUpDataForViewPage() {
        List<Fragment> orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<ListBean.LinesBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<ListBean.LinesBean>());
        }
        for (ListBean.LinesBean linesBean : listDatas) {
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(linesBean.getProductID()));
            if (listBean != null && !TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<ListBean.LinesBean> linesBeen = map.get(listBean.getCategory());
                if (linesBeen == null) {
                    linesBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), linesBeen);
                }
                linesBeen.add(linesBean);
            } else if (listBean == null) {//本地没有该商品数据
                missingLinesBean.put(linesBean.getProductID(), linesBean);
            }
        }
        if (missingLinesBean != null && missingLinesBean.size() > 0) {
            //request server
            requestMissingInfo();
            return;
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<ListBean.LinesBean> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        orderProductFragmentList.add(0, newProductFragment((ArrayList<ListBean.LinesBean>) listDatas));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
        viewpager.setOffscreenPageLimit(titles.size());
        viewpager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                IntEvent intEvent = new IntEvent();
                intEvent.setHeight(viewpager.getHeight());
                EventBus.getDefault().post(intEvent);
                viewpager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewpager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
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
            tablayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        initPopWindow((ArrayList<String>) titles);
//        int viewPageHeight = listDatas.size() * DensityUtil.dip2px(getActivityContext(), 84);
//
//        ViewGroup.LayoutParams layoutParams = mDragContentView.getLayoutParams();
//        layoutParams.height =  viewPageHeight + DensityUtil.dip2px(getActivityContext(), 130);
//        mDragContentView.setLayoutParams(layoutParams);
//        Log.i("mDragContentView","before " + mDragContentView.getHeight());
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("mDragContentView","after' " + mDragContentView.getHeight());
//            }
//        },5000);
    }

    public OrderProductFragment newProductFragment(ArrayList<ListBean.LinesBean> value) {
        OrderProductFragment orderProductFragment = new OrderProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(OrderProductFragment.BUNDLE_KEY_LIST, value);
        bundle.putString(OrderProductFragment.BUNDLE_KEY_NAME, bean.getName());
        bundle.putString(OrderProductFragment.BUNDLE_KEY_STATE, bean.getState());
        bundle.putBoolean(OrderProductFragment.BUNDLE_KEY_RETURN, bean.getHasReturn() != 0);
        bundle.putBoolean(OrderProductFragment.BUNDLE_KEY_TWO_UNIT, bean.isIsTwoUnit());
        bundle.putParcelable(OrderProductFragment.BUNDLE_KEY_ORDER_DATA, bean);
        orderProductFragment.setArguments(bundle);
        return orderProductFragment;
    }


    //实收代表收货数量和原本下单数量不相等
    private boolean isRealReceive() {
        List<OrderResponse.ListBean.LinesBean> listBeanArr = bean.getLines();
        if (listBeanArr != null) {
            for (ListBean.LinesBean lBean : listBeanArr) {
                if (lBean.getDeliveredQty() != lBean.getProductUomQty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void cancleOrderRequest() {
        StringBuffer urlSb = new StringBuffer("/gongfu/order/");
        urlSb.append(bean.getOrderID()).append("/state");
        CancleRequest request = new CancleRequest();
        request.setState("cancel");
        sendConnection(urlSb.toString(), request, CANCEL, true, BaseEntity.ResultBean.class);

    }

    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        mProductTypeWindow = new PopupWindow(gridView, DensityUtil.getScreenW(getActivityContext()), DensityUtil.getScreenH(getActivityContext()) - (findViewById(R.id.title_bar).getHeight() + tablayout.getHeight()), true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewpager.setCurrentItem(position);
                tablayout.getTabAt(position).select();
                for (int i = 0; i < mProductTypeAdapter.selectList.size(); i++) {
                    mProductTypeAdapter.selectList.set(i, new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position, new Boolean(true));
                mProductTypeAdapter.notifyDataSetChanged();
            }
        });
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductTypeWindow.dismiss();
            }
        });
        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    private void requestMissingInfo() {
        cacheProductInfo = new ArrayList<>();
        for (int i = 0; i < missingLinesBean.size(); i++) {
            Object request = null;
            StringBuffer sb = new StringBuffer("/gongfu/v2/product/");
            int key = missingLinesBean.keyAt(i);
            sb.append(missingLinesBean.get(key).getProductID()).append("/");
            sendConnection(sb.toString(), request, PRODUCT_DETAIL, false, ProductOne.class);
        }
    }
}
