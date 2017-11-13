package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.OrderChangedEvent;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.OneKeyAdapter;
import com.runwise.supply.orderpage.OrderCommitSuccessActivity;
import com.runwise.supply.orderpage.ProductActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.CommitOrderRequest;
import com.runwise.supply.orderpage.entity.DefaultPBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;

import static java.lang.System.currentTimeMillis;

/**
 * Created by libin on 2017/8/13.
 */

public class OrderModifyActivity extends NetWorkActivity implements OneKeyAdapter.OneKeyInterface {
    private static final int COMMIT_TYPE = 1 << 0;
    private static final int ADD_PRODUCT = 1 << 1;
    private static final int REQUEST_USER_INFO = 1 << 2;

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
    @ViewInject(R.id.select_bar)
    private RelativeLayout select_bar;
    @ViewInject(R.id.deleteBtn)
    private Button deleteBtn;
    @ViewInject(R.id.onekeyBtn)
    private Button onekeyBtn;
    @ViewInject(R.id.allCb)
    private CheckBox allCb;
    @ViewInject(R.id.allLL)
    private LinearLayout allLL;
    //弹窗星期的View集合
    private TextView[] wArr = new TextView[3];
    private TextView[] dArr = new TextView[3];
    //记录当前是选中的哪个送货时期，默认明天, 0今天，1明天，2后天
    private int selectedDate = 1;
    int mDayDiff = 0;
    //缓存外部显示用的日期周几
    private String cachedDWStr;
    //标记当前是否在编辑模式
    private boolean editMode;
    private int orderId;
    private BottomDialog dialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener() {
                @Override
                public void bindView(View v) {
                    initDefaultDate(v);
                }
            }).setLayoutRes(R.layout.date_layout)
            .setCancelOutside(true)
            .setDimAmount(0.5f);
    private Handler handler = new Handler();
    private OneKeyAdapter adapter;
    private boolean canSeePrice;
    OrderResponse.ListBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.order_modify_layout);
        requestUserInfo();
        //订单得到数据
        bean = getIntent().getExtras().getParcelable("order");
        setUpDate(2);
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        setTitleRightText(true, "编辑");
        setTitleLeftIcon(true, R.drawable.nav_back);
        adapter = new OneKeyAdapter(mContext);
        adapter.setCallback(this);
        pullListView.setAdapter(adapter);
        bottom_bar.setVisibility(View.VISIBLE);
        ViewPropertyAnimator.animate(bottom_bar).translationY(-CommonUtils.dip2px(mContext, 55));

        setTitleText(true, bean.getName());
        orderId = bean.getOrderID();
        List<OrderResponse.ListBean.LinesBean> list = bean.getLines();
        List<DefaultPBean> newList = new ArrayList<>();
        for (OrderResponse.ListBean.LinesBean lb : list) {
            DefaultPBean db = new DefaultPBean();
            db.setProductID(lb.getProductID());
            int count = (int) lb.getProductUomQty();
            adapter.getCountMap().put(lb.getProductID(), count);
            newList.add(db);
        }
        adapter.setData(newList);
        allLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allCb.isChecked()) {
                    allCb.setChecked(false);
                    setDeleteBtnOk(false);
                    adapter.setAllSelect(false);
                } else {
                    allCb.setChecked(true);
                    setDeleteBtnOk(true);
                    adapter.setAllSelect(true);
                }
            }
        });
        setUpEditMode();
        adapter.setEditMode(editMode);
        adapter.notifyDataSetChanged();
    }

    private void setUpDate(int dayDiff) {
        //送达日期
        long estimatedStamp = TimeUtils.getFormatTime(bean.getEstimatedTime());
        //下单日期
        long createTime = TimeUtils.stringToTimeStamp(bean.getCreateDate());
        String estimatedTimeStr;
        //最初下单的送达日期最小值
        long minStamp = createTime + 1000 * 3600 * 24 * (dayDiff - 1);
        if (TimeUtils.differentDaysByMillisecond(currentTimeMillis(), minStamp) > 0) {
            mDayDiff = 1;
            estimatedTimeStr = TimeUtils.getMMdd(currentTimeMillis());
            cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(0);
            selectedDate = 0;
        } else {
            mDayDiff = TimeUtils.differentDaysByMillisecond(createTime + dayDiff * 1000 * 3600 * 24, currentTimeMillis());
            if (estimatedStamp == createTime + dayDiff * 1000 * 3600 * 24){
                estimatedTimeStr = TimeUtils.getMMdd(createTime + dayDiff * 1000 * 3600 * 24);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mDayDiff);
            }else if(estimatedStamp > createTime + dayDiff * 1000 * 3600 * 24){
                estimatedTimeStr = TimeUtils.getMMdd(estimatedStamp);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mDayDiff + 1);
                selectedDate = 2;
            }else{
                estimatedTimeStr = TimeUtils.getMMdd(estimatedStamp);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mDayDiff - 1);
                selectedDate = 0;
            }
        }

        dateTv.setText(cachedDWStr);
    }

    private void requestUserInfo() {
        Object paramBean = null;
        sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USER_INFO, true, UserInfo.class);
    }

    int mReserveGoodsAdvanceDate;

    @Override
    public void onSuccess(BaseEntity result, int where) {

        switch (where) {
            case COMMIT_TYPE:
                ToastUtil.show(mContext, "订单修改成功");
                EventBus.getDefault().post(new OrderChangedEvent());
                finish();

                BaseEntity.ResultBean bean = result.getResult();
                JSONArray jsonArray = (JSONArray) bean.getOrders();
                ArrayList<OrderResponse.ListBean> list = new ArrayList<>();
                list.addAll(JSON.parseArray(jsonArray.toString(), OrderResponse.ListBean.class));

                Intent intent = new Intent(this, OrderCommitSuccessActivity.class);
                intent.putParcelableArrayListExtra(OrderCommitSuccessActivity.INTENT_KEY_ORDERS, list);
                intent.putExtra(OrderCommitSuccessActivity.INTENT_KEY_TYPE, 0);
                startActivity(intent);


                break;
            case REQUEST_USER_INFO:
                UserInfo userInfo = (UserInfo) result.getResult().getData();
                GlobalApplication.getInstance().saveUserInfo(userInfo);
                mReserveGoodsAdvanceDate = GlobalApplication.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
                cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
                selectedDate = 1;
                setUpDate(mReserveGoodsAdvanceDate);
                setSelectedColor(1);
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private void setUpEditMode() {
        this.setTitleRightText(true, "完成");
        this.setTitleLeftIcon(true, R.drawable.nav_add);
        select_bar.setVisibility(View.VISIBLE);
        ViewPropertyAnimator.animate(bottom_bar).setDuration(500).translationY(CommonUtils.dip2px(mContext, 55));
        ViewPropertyAnimator.animate(select_bar).setDuration(500).translationY(-CommonUtils.dip2px(mContext, 55));
        editMode = true;
    }

    @OnClick({R.id.dateTv, R.id.title_iv_left, R.id.title_tv_rigth, R.id.onekeyBtn, R.id.deleteBtn})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.dateTv:
                //弹出日期选择控件
                if (dialog.isVisible()) {
                    dialog.dismiss();
                } else {
                    dialog.show();
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
                } else
                    finish();
                break;
            case R.id.title_tv_rigth:
                if (!editMode) {
                    setUpEditMode();
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
                break;
            case R.id.onekeyBtn:
                //下单按钮
                CommitOrderRequest request = new CommitOrderRequest();
                request.setEstimated_time(TimeUtils.getAB2FormatData(selectedDate - 1 + mDayDiff));
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
                StringBuffer sb = new StringBuffer("/gongfu/order/");
                sb.append(orderId).append("/modify/");
                sendConnection(sb.toString(), request, COMMIT_TYPE, true, BaseEntity.ResultBean.class);
                break;
            case R.id.deleteBtn:
                adapter.deleteSelectItems();
                //更新个数
                countChanged();
                break;
            default:
                break;
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
        wArr[selectedDate].setTextColor(Color.parseColor("#6BB400"));
        dArr[selectedDate].setTextColor(Color.parseColor("#6BB400"));
        //计算当前日期起，明后天的星期几+号数
        wTv1.setText(TimeUtils.getWeekStr(mDayDiff - 1));
        String[] t = TimeUtils.getABFormatDate(mDayDiff - 1).split("-");
        if (t.length > 2) {
            dTv1.setText(t[1] + "-" + t[2]);
        }
        wTv2.setText(TimeUtils.getWeekStr(mDayDiff));
        t = TimeUtils.getABFormatDate(mDayDiff).split("-");
        if (t.length > 2) {
            dTv2.setText(t[1] + "-" + t[2]);
        }
        wTv3.setText(TimeUtils.getWeekStr(mDayDiff + 1));
        t = TimeUtils.getABFormatDate(mDayDiff + 1).split("-");
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
                        selectedDate = 0;
                        dialog.dismiss();
                        dateTv.setText(TimeUtils.getABFormatDate(mDayDiff - 1).substring(5) + " " + TimeUtils.getWeekStr(mDayDiff - 1));
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
                        selectedDate = 1;
                        dialog.dismiss();
                        dateTv.setText(TimeUtils.getABFormatDate(mDayDiff).substring(5) + " " + TimeUtils.getWeekStr(mDayDiff));
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
                        selectedDate = 2;
                        dialog.dismiss();
                        dateTv.setText(TimeUtils.getABFormatDate(mDayDiff + 1).substring(5) + " " + TimeUtils.getWeekStr(mDayDiff + 1));
                    }
                }, 500);
            }
        });
    }

    //参数从0开始
    private void setSelectedColor(int i) {
        if (wArr[0] == null) {
            return;
        }
        for (TextView tv : wArr) {
            tv.setTextColor(Color.parseColor("#2E2E2E"));
        }
        for (TextView tv : dArr) {
            tv.setTextColor(Color.parseColor("#2E2E2E"));
        }
        wArr[i].setTextColor(Color.parseColor("#6BB400"));
        dArr[i].setTextColor(Color.parseColor("#6BB400"));
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
        if (canSeePrice) {
            totalMoneyTv.setVisibility(View.VISIBLE);
        }
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
                allCb.setChecked(false);
                setDeleteBtnOk(true);
                break;
            case NO_SELECT:
                setDeleteBtnOk(false);
                allCb.setChecked(false);
                break;
        }
    }

    private void setTitleEditShow() {
        if (adapter.getCount() == 0) {
            setTitleRightText(false, "编辑");
        } else {
            if (editMode) {
                setTitleRightText(true, "完成");
            } else {
                setTitleRightText(true, "编辑");
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
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
                bottom_bar.setVisibility(View.VISIBLE);
                ViewPropertyAnimator.animate(bottom_bar).translationY(-CommonUtils.dip2px(mContext, 55));
            } else {
            }
            setTitleEditShow();
        }
    }
}
