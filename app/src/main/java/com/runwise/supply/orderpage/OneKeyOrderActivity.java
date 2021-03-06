package com.runwise.supply.orderpage;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.OrderSuccessEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.entity.OneKeyRequest;
import com.runwise.supply.entity.OrderCommitResponse;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.CommitOrderRequest;
import com.runwise.supply.orderpage.entity.DefaultPBean;
import com.runwise.supply.orderpage.entity.DefaultProductData;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;

import static com.runwise.supply.R.id.loadingLayout;


/**
 * Created by libin on 2017/6/30.
 */

public class OneKeyOrderActivity extends NetWorkActivity implements OneKeyAdapter.OneKeyInterface {
    private static final int DEFAULT_TYPE = 1 << 0;
    private static final int COMMIT_TYPE = 1 << 1;
    private static final int ADD_PRODUCT = 1 << 2;
    public static final int REQUEST_USER_INFO = 1 << 3;
    int[] loadingImgs = new int[31];
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.rl_bottom)
    private LinearLayout bottom_bar;
    @ViewInject(R.id.countTv)
    private TextView totalNumTv;
    @ViewInject(R.id.moenyTv)
    private TextView totalMoneyTv;
    @ViewInject(R.id.dateTv)
    private TextView dateTv;
    @ViewInject(R.id.loadingImg)
    private ImageView loadingImg;
    private int currentIndex;
    @ViewInject(R.id.loadingTv)
    private TextView loadingTv;
    @ViewInject(R.id.select_bar)
    private RelativeLayout select_bar;
    @ViewInject(R.id.deleteBtn)
    private Button deleteBtn;
    @ViewInject(R.id.allCb)
    private CheckBox allCb;
    @ViewInject(R.id.allLL)
    private LinearLayout allLL;
    @ViewInject(R.id.orderSuccessIv)
    private ImageView orderSuccessIv;
    @ViewInject(R.id.bgView)
    private View bgView;
    @ViewInject(R.id.nopurchaseRL)
    private RelativeLayout nopurchaseRL;
    @ViewInject(loadingLayout)
    private LoadingLayout mLoadingLayout;
    @ViewInject(R.id.onekeyBtn)
    private Button onekeyBtn;

    //标记是否主动点击全部,默认是主动true
    private boolean isInitiative = true;
    //弹窗星期的View集合
    private TextView[] wArr = new TextView[3];
    private TextView[] dArr = new TextView[3];
    //记录当前是选中的哪个送货时期，默认明天, 0今天，1明天，2后天
    private int selectedDate;
    private int selectedDateIndex;
    //缓存外部显示用的日期周几
    private String cachedDWStr;
    //标记当前是否在编辑模式
    private boolean editMode;
    private BottomDialog bDialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener() {
                @Override
                public void bindView(View v) {
                    initDefaultDate(v);
                }
            }).setLayoutRes(R.layout.date_layout)
            .setCancelOutside(true)
            .setDimAmount(0.5f);
    //    private BottomSheetDialog showDialog = new BottomSheetDialog(mContext);
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loadingImg.setImageResource(loadingImgs[currentIndex++]);
            if (currentIndex >= 31) {
                currentIndex = 0;
            }
            handler.postDelayed(runnable, 30);
        }
    };
    private OneKeyAdapter adapter;
    private double predict_sale_amount;
    private double yongliang_factor;
    int mReserveGoodsAdvanceDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.onekey_order_layout);
        setTitleText(true, "智能下单");
        setTitleLeftIcon(true, R.drawable.nav_back);
        setTitleRightText(true, "编辑");
        pullListView.setVisibility(View.INVISIBLE);
        adapter = new OneKeyAdapter(mContext);
        adapter.setCallback(this);
        pullListView.setAdapter(adapter);
        initLoadingImgs();
        handler.postDelayed(runnable, 0);
        dateTv.setText(cachedDWStr);
        predict_sale_amount = getIntent().getDoubleExtra("predict_sale_amount", 0);
        yongliang_factor = getIntent().getDoubleExtra("yongliang_factor", 0) / 100;
//        showDialog.setTitle("选择送达日期");
//        showDialog.setCancelable(true)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestDefalutProduct();
            }
        }, 2000);
        allLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInitiative = false;
                if (allCb.isChecked()) {
                    allCb.setChecked(false);
                    setDeleteBtnOk(false);
                    adapter.setAllSelect(false);
                } else {
                    allCb.setChecked(true);
                    setDeleteBtnOk(true);
                    adapter.setAllSelect(true);
                }
                isInitiative = true;
            }
        });
        allCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isInitiative) {
                    if (isChecked) {
                        //adapter里面所有的选中
                        setDeleteBtnOk(true);
                        adapter.setAllSelect(true);
                    } else {
                        //清掉adapter里面所有选中的状态
                        setDeleteBtnOk(false);
                        adapter.setAllSelect(false);
                    }
                }
            }
        });
        boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        if (!canSeePrice) {
            totalMoneyTv.setVisibility(View.GONE);
        }
        Object paramBean = null;
        sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USER_INFO, true, UserInfo.class);
    }

    @OnClick({R.id.dateTv, R.id.title_iv_left, R.id.title_tv_rigth, R.id.onekeyBtn, R.id.deleteBtn})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.dateTv:
                //弹出日期选择控件
                if (bDialog.isVisible()) {
                    bDialog.dismiss();
                } else {
                    bDialog.show();
                }
                break;
            case R.id.title_iv_left:
                if (editMode) {
                    //到添加页面
                    Intent intent = new Intent(mContext, ProductActivity.class);
                    Bundle bundle = new Bundle();
                    int size = adapter.getList().size();
                    ArrayList<AddedProduct> addedList = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        DefaultPBean bean = (DefaultPBean) adapter.getList().get(i);
                        Parcel parcel = Parcel.obtain();
                        AddedProduct ap = AddedProduct.CREATOR.createFromParcel(parcel);
                        ap.setProductId(String.valueOf(bean.getProductID()));
                        int count = adapter.getCountMap().get(bean.getProductID());
                        ap.setCount(count);
                        parcel.recycle();
                        addedList.add(ap);
                    }
                    bundle.putParcelableArrayList("ap", addedList);
                    intent.putExtra("apbundle", bundle);
                    startActivityForResult(intent, ADD_PRODUCT);
                } else {
                    dialog.setTitle("提示");
                    dialog.setMessageGravity();
                    dialog.setMessage("确认取消下单？");
                    dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {
                            finish();
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id.title_tv_rigth:
                switchEditMode();
                break;
            case R.id.onekeyBtn:
                //下单按钮
                CommitOrderRequest request = new CommitOrderRequest();
                request.setEstimated_time(TimeUtils.getAB2FormatData(selectedDate));
                request.setOrder_type_id("121");
                List<DefaultPBean> list = adapter.getList();
                List<CommitOrderRequest.ProductsBean> cList = new ArrayList<>();
                for (DefaultPBean bean : list) {
                    CommitOrderRequest.ProductsBean pBean = new CommitOrderRequest.ProductsBean();
                    pBean.setProduct_id(bean.getProductID());
                    int qty = adapter.getCountMap().get(Integer.valueOf(bean.getProductID()));
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
                sendConnection("/gongfu/v2/order/create/", request, COMMIT_TYPE, true, OrderCommitResponse.class);
//                progressDialog.setCancelable(false);
//                backgroundAlpha(0.4f);
//                progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        backgroundAlpha(1f);
//                    }
//                });
                onekeyBtn.setBackgroundColor(Color.parseColor("#7F9ACC35"));
                onekeyBtn.setEnabled(false);
                dateTv.setEnabled(false);
                break;
            case R.id.deleteBtn:
                dialog.setTitle("提示");
                dialog.setMessageGravity();
                dialog.setMessage("确认删除选中商品?");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        adapter.deleteSelectItems();
                        //更新个数
                        countChanged();
                        if (adapter.getCount() == 0) {
                            switchEditMode();
                            bottom_bar.setVisibility(View.INVISIBLE);
                            setTitleRightText(false, "");
                            setTitleLeftIcon(true, R.drawable.back_btn);

                            setSelectedColor(1);
                            selectedDate = mReserveGoodsAdvanceDate;
                            selectedDateIndex = 1;
                            if (bDialog != null&& bDialog.isVisible()){
                                bDialog.dismiss();
                            }
                            dateTv.setText(TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate));
                        }
                    }
                });
                dialog.show();
                break;
            default:
                break;
        }
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private void switchEditMode() {
        //强制隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(allLL.getWindowToken(), 0); //强制隐藏键盘
        if (!editMode) {
            this.setTitleRightText(true, "完成");
            this.setTitleLeftIcon(true, R.drawable.nav_add);
            select_bar.setVisibility(View.VISIBLE);
            ViewPropertyAnimator.animate(bottom_bar).setDuration(500).translationY(CommonUtils.dip2px(mContext, 55));
            ViewPropertyAnimator.animate(select_bar).setDuration(500).translationY(-CommonUtils.dip2px(mContext, 55));
            editMode = true;
        } else {
            adapter.clearSelect();
            this.setTitleRightText(true, "编辑");
            ViewPropertyAnimator.animate(bottom_bar).setDuration(500).translationY(-CommonUtils.dip2px(mContext, 55));
            ViewPropertyAnimator.animate(select_bar).setDuration(500).translationY(CommonUtils.dip2px(mContext, 55));
            this.setTitleLeftIcon(true, R.drawable.nav_back);
            editMode = false;
        }
        adapter.setEditMode(editMode);
        adapter.notifyDataSetChanged();
        if (adapter != null && adapter.getCount() == 0) {
            bottom_bar.setVisibility(View.INVISIBLE);
            setTitleRightText(false, "");
        }
    }

    private void setDeleteBtnOk(boolean isOk) {
        if (isOk) {
            deleteBtn.setEnabled(true);
            deleteBtn.setBackgroundResource(R.drawable.product_delete_ok);
            deleteBtn.setTextColor(Color.parseColor("#FF3B30"));
        } else {
            deleteBtn.setEnabled(false);
            deleteBtn.setBackgroundResource(R.drawable.product_delete_circle);
            deleteBtn.setTextColor(Color.parseColor("#E3E3E3"));
        }

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
                        dateTv.setText(TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate - 1).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate - 1));
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
                        dateTv.setText(TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate));
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
                        dateTv.setText(TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate + 1).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate + 1));
                    }
                }, 500);
            }
        });
    }

    //参数从0开始
    private void setSelectedColor(int i) {
        for (TextView tv : wArr) {
            if (tv != null){
                tv.setTextColor(Color.parseColor("#2E2E2E"));
            }
        }
        for (TextView tv : dArr) {
            if (tv != null){
                tv.setTextColor(Color.parseColor("#2E2E2E"));
            }
        }
        if (wArr[i] != null){
            wArr[i].setTextColor(Color.parseColor("#6BB400"));
        }
        if (dArr[i] != null){
            dArr[i].setTextColor(Color.parseColor("#6BB400"));
        }
    }

    private void requestDefalutProduct() {
        ///gongfu/v2/shop/preset/product/list
        OneKeyRequest request = new OneKeyRequest();
        request.setPredict_sale_amount(predict_sale_amount);
        request.setYongliang_factor(yongliang_factor);
        sendConnection("/gongfu/v2/shop/preset/product/list", request, DEFAULT_TYPE, false, DefaultProductData.class);
    }

    private void initLoadingImgs() {
        StringBuffer sb;
        for (int i = 0; i < 31; i++) {
            sb = new StringBuffer("order_loading_");
            sb.append(i);
            loadingImgs[i] = getResIdByDrawableName(sb.toString());
        }
    }

    private void onSuccessCallBack() {
        //停止动画
        handler.removeCallbacks(runnable);
        loadingImg.setVisibility(View.INVISIBLE);
        loadingTv.setVisibility(View.INVISIBLE);
        bottom_bar.setVisibility(View.VISIBLE);
        ViewPropertyAnimator.animate(bottom_bar).translationY(-CommonUtils.dip2px(mContext, 55));
        pullListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        BaseEntity.ResultBean resultBean = result.getResult();
        switch (where) {
            case DEFAULT_TYPE:
                onSuccessCallBack();
                DefaultProductData data = (DefaultProductData) resultBean.getData();
                adapter.setData(data.getList());
                if (data != null && data.getList() != null && data.getList().size() == 0) {
                    //显示UI
                    nopurchaseRL.setVisibility(View.VISIBLE);
                    setTitleRightText(false,"");
                }
                break;
            case COMMIT_TYPE:
                onSuccessCallBack();
                finish();
                onekeyBtn.setBackgroundColor(Color.parseColor("#9ACC35"));
//                onekeyBtn.setEnabled(true);
                dateTv.setEnabled(true);
                EventBus.getDefault().post(new OrderSuccessEvent());

                final OrderCommitResponse orderCommitResponse = (OrderCommitResponse) result.getResult().getData();
                Intent intent = new Intent(OneKeyOrderActivity.this,OrderCommitSuccessActivity.class);
                intent.putParcelableArrayListExtra(OrderCommitSuccessActivity.INTENT_KEY_ORDERS,orderCommitResponse.getOrders());
                startActivity(intent);
//                bgView.setVisibility(View.VISIBLE);
//                orderSuccessIv.setVisibility(View.VISIBLE);
//                AnimatorSet set = new AnimatorSet();
//                set.playTogether(
//                        ObjectAnimator.ofFloat(orderSuccessIv, "scaleX", 1, 0.7f),
//                        ObjectAnimator.ofFloat(orderSuccessIv, "scaleY", 1, 0.7f)
//                );
//                set.setInterpolator(new AccelerateDecelerateInterpolator());
//                set.setDuration(1000).start();
//                orderSuccessIv.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                        onekeyBtn.setBackgroundColor(Color.parseColor("#9ACC35"));
//                        onekeyBtn.setEnabled(true);
//                        dateTv.setEnabled(true);
//                        EventBus.getDefault().post(new OrderSuccessEvent());
//                    }
//                }, 1500);

                break;
            case REQUEST_USER_INFO:
                UserInfo userInfo = (UserInfo) result.getResult().getData();
                SampleApplicationLike.getInstance().saveUserInfo(userInfo);
                mReserveGoodsAdvanceDate = SampleApplicationLike.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
                cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
                if(TextUtils.isEmpty(dateTv.getText().toString()))dateTv.setText(cachedDWStr);
                selectedDate = mReserveGoodsAdvanceDate;
                selectedDateIndex = 1;
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where) {
            case DEFAULT_TYPE:
                //ToastUtil.show(mContext, errMsg);
                //停止动画
                handler.removeCallbacks(runnable);
                loadingImg.setVisibility(View.INVISIBLE);
                loadingTv.setVisibility(View.INVISIBLE);
                //bottom_bar.setVisibility(View.VISIBLE);
                //ViewPropertyAnimator.animate(bottom_bar).translationY(-CommonUtils.dip2px(mContext, 55));
                pullListView.setVisibility(View.VISIBLE);
                nopurchaseRL.setVisibility(View.VISIBLE);
                setTitleRightText(true, "编辑");
                break;
            case COMMIT_TYPE:
                if(result.getResult()!=null && "A1001".equals(result.getResult().getState())){
                    ToastUtil.show(this,"订单操作频率过高，请稍后再试！");
                    onekeyBtn.setBackgroundColor(Color.parseColor("#9ACC35"));
                    onekeyBtn.setEnabled(true);
                    dateTv.setEnabled(true);
                    return;
                }
                progressDialog.dismiss();

                dialog.setTitle("提示");
                dialog.setMessage("网络连接失败，请查看首页订单列表，检查下单是否成功");
                dialog.setMessageGravity();
                dialog.setCancelable(false);
                dialog.setModel(CustomDialog.RIGHT);
                dialog.setRightBtnListener("我知道啦", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送取消订单请求
                        ActivityManager.getInstance().finishAll();
                        startActivity(new Intent(getActivityContext(), MainActivity.class));
                    }
                });
                dialog.setLeftBtnListener("取消", new CustomDialog.DialogListener(){
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

    private int getResIdByDrawableName(String name) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }

    @Override
    public void countChanged() {
        int totalNum = 0;
        double totalMoney = 0;
        DecimalFormat df = new DecimalFormat("##.#");
        List<DefaultPBean> list = adapter.getList();
        for (DefaultPBean bean : list) {
            int count = adapter.getCountMap().get(Integer.valueOf(bean.getProductID()));
            totalNum += count;
            double price = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID())).getPrice();
            totalMoney += count * price;
        }
        mLoadingLayout.onSuccess(list.size(), "哎呀！这里是空哒~~", R.drawable.default_icon_ordernone);
        totalMoneyTv.setText(df.format(totalMoney) + "元");
        totalNumTv.setText(totalNum + "件");
    }

    @Override
    public void selectClicked(OneKeyAdapter.SELECTTYPE type) {
        isInitiative = false;
        switch (type) {
            case ALL_SELECT:
                allCb.setChecked(true);
                setDeleteBtnOk(true);
                break;
            case PART_SELECT:
                isInitiative = false;
                allCb.setChecked(false);
                setDeleteBtnOk(true);
                break;
            case NO_SELECT:
                setDeleteBtnOk(false);
                allCb.setChecked(false);
                break;
        }
        isInitiative = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            ArrayList<AddedProduct> backList = bundle.getParcelableArrayList("backap");
            List<DefaultPBean> newList = new ArrayList<>();
            if (backList != null) {
                bottom_bar.setVisibility(View.VISIBLE);
                for (AddedProduct pro : backList) {
                    Integer proId = Integer.valueOf(pro.getProductId());
                    Integer count = (int)pro.getCount();
                    DefaultPBean bean = new DefaultPBean();
                    bean.setProductID(proId);
                    newList.add(bean);
                    adapter.getCountMap().put(proId, count);
                }
                adapter.setData(newList);
                if (backList.size() > 0) {
                    ToastUtil.show(mContext, "添加成功");
                    nopurchaseRL.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (editMode) {
            switchEditMode();
        } else {
            finish();
        }
    }
}
