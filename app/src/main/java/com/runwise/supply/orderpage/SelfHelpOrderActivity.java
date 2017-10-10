package com.runwise.supply.orderpage;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.OrderSuccessEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.CustomProgressDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.CommitOrderRequest;
import com.runwise.supply.orderpage.entity.CommitResponse;
import com.runwise.supply.orderpage.entity.DefaultPBean;
import com.runwise.supply.orderpage.entity.DefaultProductData;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;

/**
 * Created by libin on 2017/7/12.
 * 跟智能下单页面大体一致，可继承，暂复制。
 */

public class SelfHelpOrderActivity extends NetWorkActivity implements OneKeyAdapter.OneKeyInterface {
    private static final int DEFAULT_TYPE = 1 << 0;
    private static final int COMMIT_TYPE = 1 << 1;
    private static final int ADD_PRODUCT = 1 << 2;
    public static final int REQUEST_USER_INFO = 1 << 3;
    int[] loadingImgs = new int[31];
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.bottom_bar)
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
    @ViewInject(R.id.self_help_rl)
    private RelativeLayout self_help_rl;
    @ViewInject(R.id.bgView)
    private View bgView;
    @ViewInject(R.id.orderSuccessIv)
    private ImageView orderSuccessIv;
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
    CustomProgressDialog mCustomProgressDialog;

    @OnClick({R.id.dateTv, R.id.title_iv_left, R.id.title_tv_rigth, R.id.onekeyBtn, R.id.deleteBtn, R.id.self_add_btn})
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
//                    dialog.setTitle("提示");
//                    dialog.setMessageGravity();
//                    dialog.setMessage("确认取消下单？");
//                    dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
//                        @Override
//                        public void doClickButton(Button btn, CustomDialog dialog) {
                    finish();
//                        }
//                    });
//                    dialog.show();
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
                    pBean.setQty(qty);
                    cList.add(pBean);
                }
                request.setProducts(cList);
                sendConnection("/gongfu/v2/order/create/", request, COMMIT_TYPE, false, CommitResponse.class);
                mCustomProgressDialog = new CustomProgressDialog(this);
                mCustomProgressDialog.setMsg("下单中...");
                mCustomProgressDialog.show();
                findViewById(R.id.onekeyBtn).setEnabled(false);
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
                    }
                });
                dialog.show();
                break;
            case R.id.self_add_btn:
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
                break;
            default:
                break;
        }
    }

    //切换编辑模式
    private void switchEditMode() {
        if (!editMode) {
            this.setTitleRightText(true, "完成");
            this.setTitleLeftIcon(true, R.drawable.nav_add);
            select_bar.setVisibility(View.VISIBLE);
            ViewPropertyAnimator.animate(bottom_bar).setDuration(500).translationY(CommonUtils.dip2px(mContext, 55));
            ViewPropertyAnimator.animate(select_bar).setDuration(500).translationY(-CommonUtils.dip2px(mContext, 55));
            editMode = true;
        } else {
            //完成模式，清空上次选择的
            adapter.clearSelect();
            InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
            this.setTitleRightText(true, "编辑");
            ViewPropertyAnimator.animate(bottom_bar).setDuration(500).translationY(-CommonUtils.dip2px(mContext, 55));
            ViewPropertyAnimator.animate(select_bar).setDuration(500).translationY(CommonUtils.dip2px(mContext, 55));
            this.setTitleLeftIcon(true, R.drawable.nav_back);
            editMode = false;
        }
        adapter.setEditMode(editMode);
        adapter.notifyDataSetChanged();
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
            tv.setTextColor(Color.parseColor("#2E2E2E"));
        }
        for (TextView tv : dArr) {
            tv.setTextColor(Color.parseColor("#2E2E2E"));
        }
        wArr[i].setTextColor(Color.parseColor("#6BB400"));
        dArr[i].setTextColor(Color.parseColor("#6BB400"));
    }

    int mReserveGoodsAdvanceDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.onekey_order_layout);
        mReserveGoodsAdvanceDate = GlobalApplication.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
        cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
        selectedDate = mReserveGoodsAdvanceDate;
        selectedDateIndex = 1;
        self_help_rl.setVisibility(View.VISIBLE);
        loadingImg.setVisibility(View.INVISIBLE);
        loadingTv.setVisibility(View.INVISIBLE);
        setTitleText(true, "自助下单");
        setTitleLeftIcon(true, R.drawable.nav_back);
        pullListView.setVisibility(View.INVISIBLE);
        adapter = new OneKeyAdapter(mContext);
        adapter.setCallback(this);
        pullListView.setAdapter(adapter);
//        initLoadingImgs();
        handler.postDelayed(runnable, 0);
        dateTv.setText(cachedDWStr);
//        showDialog.setTitle("选择送达日期");
//        showDialog.setCancelable(true)
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                requestDefalutProduct();
//            }
//        },2000);
        setTitleEditShow();
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
        boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        if (!canSeePrice) {
            totalMoneyTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Object paramBean = null;
        sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USER_INFO, true, UserInfo.class);
    }

    private void setTitleEditShow() {
        if (adapter.getCount() == 0) {
            setTitleRightText(false, "编辑");
            self_help_rl.setVisibility(View.VISIBLE);
            pullListView.setVisibility(View.INVISIBLE);
        } else {
            if (editMode) {
                setTitleRightText(true, "完成");
            } else {
                setTitleRightText(true, "编辑");
            }
            self_help_rl.setVisibility(View.INVISIBLE);
            pullListView.setVisibility(View.VISIBLE);
        }
    }

    private void requestDefalutProduct() {
        ///gongfu/v2/shop/preset/product/list
        Object request = null;
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

    private void onSuccessCallBack(){
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
                if (adapter.getCount() == 0) {
                    setTitleRightText(false, "编辑");
                } else {
                    setTitleRightText(false, "编辑");
                }
                break;
            case COMMIT_TYPE:
                onSuccessCallBack();
                if (mCustomProgressDialog != null) {
                    mCustomProgressDialog.dismiss();
                }
                bgView.setVisibility(View.VISIBLE);
                orderSuccessIv.setVisibility(View.VISIBLE);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(orderSuccessIv, "scaleX", 1, 0.7f),
                        ObjectAnimator.ofFloat(orderSuccessIv, "scaleY", 1, 0.7f)
                );
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.setDuration(1000).start();
                orderSuccessIv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        EventBus.getDefault().post(new OrderSuccessEvent());
                        findViewById(R.id.onekeyBtn).setEnabled(true);
                    }
                }, 1500);

                break;
            case REQUEST_USER_INFO:
                UserInfo userInfo = (UserInfo) result.getResult().getData();
                GlobalApplication.getInstance().saveUserInfo(userInfo);
                mReserveGoodsAdvanceDate = GlobalApplication.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
                cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
                selectedDate = mReserveGoodsAdvanceDate;
                selectedDateIndex = 1;
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        findViewById(R.id.onekeyBtn).setEnabled(true);

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
        List<DefaultPBean> list = adapter.getList();
        HashMap pbMap = ProductBasicUtils.getBasicMap(mContext);
        if (pbMap != null && pbMap.size() > 0) {
            for (DefaultPBean bean : list) {
                ProductBasicList.ListBean lb = (ProductBasicList.ListBean) pbMap.get(String.valueOf(bean.getProductID()));
                double price = lb.getPrice();
                int count = adapter.getCountMap().get(bean.getProductID());
                totalNum += count;
                totalMoney += count * price;
            }
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String formatMoney = df.format(totalMoney);
        totalMoneyTv.setText(formatMoney + "元");
        totalNumTv.setText(totalNum + "件");
        setTitleEditShow();

    }

    @Override
    public void selectClicked(OneKeyAdapter.SELECTTYPE type) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 2000:
                Bundle bundle = data.getExtras();
                ArrayList<AddedProduct> backList = bundle.getParcelableArrayList("backap");
                List<DefaultPBean> newList = new ArrayList<>();
                if (backList != null) {
                    for (AddedProduct pro : backList) {
                        Integer proId = Integer.valueOf(pro.getProductId());
                        Integer count = pro.getCount();
                        DefaultPBean bean = new DefaultPBean();
                        bean.setProductID(proId);
                        newList.add(bean);
                        adapter.getCountMap().put(proId, count);
                    }
                    adapter.setData(newList);
                }
                if (backList != null && backList.size() > 0) {
                    ToastUtil.show(mContext, "添加成功");
                    self_help_rl.setVisibility(View.GONE);
                    bottom_bar.setVisibility(View.VISIBLE);
                    ViewPropertyAnimator.animate(bottom_bar).translationY(-CommonUtils.dip2px(mContext, 55));
                    pullListView.setVisibility(View.VISIBLE);
                } else {
                    self_help_rl.setVisibility(View.VISIBLE);
                    bottom_bar.setVisibility(View.INVISIBLE);
                }
                setTitleEditShow();
                break;
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
