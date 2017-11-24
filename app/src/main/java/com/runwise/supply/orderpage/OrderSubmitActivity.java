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

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderSubmitProductAdapter;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.bottomdialog.BottomDialog;

import static java.lang.System.currentTimeMillis;

public class OrderSubmitActivity extends NetWorkActivity {

    public static final int REQUEST_USER_INFO = 1 << 0;

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
        mRvProductList.setLayoutManager(new LinearLayoutManager(mContext));

        mReserveGoodsAdvanceDate = GlobalApplication.getInstance().loadUserInfo().getReserveGoodsAdvanceDate();
        cachedDWStr = TimeUtils.getABFormatDate(mReserveGoodsAdvanceDate).substring(5) + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
        selectedDate = mReserveGoodsAdvanceDate;
        selectedDateIndex = 1;
        mTvDate.setText(cachedDWStr);
        OrderSubmitProductAdapter orderSubmitProductAdapter;
        if (getIntent().hasExtra(INTENT_KEY_ORDER)) {
            mOrder = (OrderResponse.ListBean) getIntent().getSerializableExtra(INTENT_KEY_ORDER);
            orderSubmitProductAdapter = new OrderSubmitProductAdapter(getTestData());
        }else{
            orderSubmitProductAdapter = new OrderSubmitProductAdapter(getTestData());
        }
        mRvProductList.setAdapter(orderSubmitProductAdapter);

        Object paramBean = null;
        sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USER_INFO, true, UserInfo.class);
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
            selectedDate = 0;
        } else {
            mReserveGoodsAdvanceDate = TimeUtils.differentDaysByMillisecond(createTime + dayDiff * 1000 * 3600 * 24, currentTimeMillis());
            if (estimatedStamp == createTime + dayDiff * 1000 * 3600 * 24) {
                estimatedTimeStr = TimeUtils.getMMdd(createTime + dayDiff * 1000 * 3600 * 24);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate);
            } else if (estimatedStamp > createTime + dayDiff * 1000 * 3600 * 24) {
                estimatedTimeStr = TimeUtils.getMMdd(estimatedStamp);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate + 1);
                selectedDate = 2;
            } else {
                estimatedTimeStr = TimeUtils.getMMdd(estimatedStamp);
                cachedDWStr = estimatedTimeStr + " " + TimeUtils.getWeekStr(mReserveGoodsAdvanceDate - 1);
                selectedDate = 0;
            }
        }
        mTvDate.setText(cachedDWStr);
    }


    List<ProductData.ListBean> getTestData() {
        List<ProductData.ListBean> listBeans = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ProductData.ListBean listBean = new ProductData.ListBean();
            listBean.setActualQty(10);
            listBean.setName("黑芝麻");
            listBean.setPrice(10.0f);
            listBean.setProductUom("袋");
            listBean.setUnit("[满芬牌]100克/袋,10袋/盒");
            listBeans.add(listBean);
        }
        return listBeans;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
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
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @OnClick({R.id.title_iv_left, R.id.rl_date_of_service, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
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

}
