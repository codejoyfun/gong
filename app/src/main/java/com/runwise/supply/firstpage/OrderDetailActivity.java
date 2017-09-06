package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.OrderDetailResponse;
import com.runwise.supply.firstpage.entity.CancleRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderState;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.YourScrollableViewPager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private YourScrollableViewPager viewpager;
    @ViewInject(R.id.drag_layout)
    private DragTopLayout dragLayout;
    @ViewInject(R.id.tv_open)
    private ImageView ivOpen;
    private boolean isModifyOrder;          //可修改订单
    private int orderId;                    //如果有orderId, 需要重新刷新

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
                    startActivityForResult(intent3, UPLOAD);
                }
                break;
            case R.id.tv_open:
                if (dragLayout.getState() == DragTopLayout.PanelState.EXPANDED) {
                    dragLayout.toggleTopView();
                    canShow = true;
                }else{
                    if (mProductTypeWindow.isShowing()){
                        mProductTypeWindow.dismiss();
                    }else{
                        showPopWindow();
                    }
                }
                dragLayout.listener(new DragTopLayout.PanelListener() {
                    @Override
                    public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
                        if (panelState == DragTopLayout.PanelState.COLLAPSED) {
                            if (canShow){
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
    boolean canShow = false;
    private void showPopWindow(){
        int y = findViewById(R.id.title_bar).getHeight() + tablayout.getHeight();
        mProductTypeWindow.showAtLocation(findViewById(R.id.rl_content), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewpager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case DETAIL:
                BaseEntity.ResultBean resultBean = result.getResult();
                OrderDetailResponse response = (OrderDetailResponse) resultBean.getData();
                bean = response.getOrder();
                adapter.setStatus(bean.getName(), bean.getState());
                updateUI();
                loadingLayout.onSuccess(1, "暂时没有数据哦");
                break;
            case CANCEL:
                finish();
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

    private void updateUI() {
        if (bean != null) {
            if (bean.getHasReturn() != 0) {
                adapter.setHasReturn(true);
                returnLL.setVisibility(View.VISIBLE);
                //可能有多个退货单。
                for (final String returnId : bean.getReturnOrders()) {
                    TextView tv = new TextView(mContext);
                    tv.setTextSize(14);
                    tv.setTextColor(Color.parseColor("#999999"));
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setTag(returnId);
                    String returnName = ProductBasicUtils.getReturnMap().get(returnId);
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
                bottom_bar.setVisibility(View.GONE);
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
                    bottom_bar.setVisibility(View.GONE);
                }
                //预计价钱改为，商品金额
                ygMoney.setText("商品金额");
            } else if (bean.getState().equals("rated")) {
                state = "订单已评价";
                bottom_bar.setVisibility(View.GONE);
            }
            orderStateTv.setText(state);
            tipTv.setText(tip);
            rightBtn.setText(OrderActionUtils.getDoBtnTextByState(bean));
            dateTv.setText(TimeUtils.getMMdd(bean.getCreateDate()));
            //支付凭证在收货流程后，才显示
            if ((bean.getState().equals("rated") || bean.getState().equals("done"))
                    && bean.getOrderSettleName().contains("单次结算")) {
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
                //同时，显示右上角，申请售后
                setTitleRightText(true, "申请售后");
                isModifyOrder = false;
            } else if (bean.getState().equals(OrderState.DRAFT.getName())) {
                setTitleRightText(true, "修改");
                isModifyOrder = true;
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
            if ("done".equals(bean.getState()) && bean.getDeliveredQty() != bean.getAmount()) {
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

    private void setUpDataForViewPage() {
        List<Fragment> orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("全部");

        HashMap<String, ArrayList<ListBean.LinesBean>> map = new HashMap<>();
        for (ListBean.LinesBean linesBean : listDatas) {
            ArrayList<ListBean.LinesBean> linesBeen = map.get(linesBean.getStockType());
            if (linesBeen == null) {
                linesBeen = new ArrayList<>();
                map.put(linesBean.getStockType(), linesBeen);
            }
            linesBeen.add(linesBean);
        }
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            ArrayList<ListBean.LinesBean> value = (ArrayList<ListBean.LinesBean>) entry.getValue();
            titles.add(key);
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(key));
        }
        orderProductFragmentList.add(0, newProductFragment((ArrayList<ListBean.LinesBean>) listDatas));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
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
        initPopWindow((ArrayList<String>) titles);
    }

    public OrderProductFragment newProductFragment(ArrayList<ListBean.LinesBean> value) {
        OrderProductFragment orderProductFragment = new OrderProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(OrderProductFragment.BUNDLE_KEY_LIST, value);
        bundle.putString(OrderProductFragment.BUNDLE_KEY_NAME, bean.getName());
        bundle.putString(OrderProductFragment.BUNDLE_KEY_STATE, bean.getState());
        bundle.putBoolean(OrderProductFragment.BUNDLE_KEY_RETURN, bean.getHasReturn() != 0);
        bundle.putBoolean(OrderProductFragment.BUNDLE_KEY_TWO_UNIT, bean.isIsTwoUnit());
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
        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewpager.setCurrentItem(position);
                tablayout.getTabAt(position).select();
                for (int i = 0;i < mProductTypeAdapter.selectList.size();i++){
                    mProductTypeAdapter.selectList.set(i,new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position,new Boolean(true));
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

}
