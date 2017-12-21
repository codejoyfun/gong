package com.runwise.supply;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.OrderDetailResponse;
import com.runwise.supply.event.IntEvent;
import com.runwise.supply.firstpage.OrderDtailAdapter;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.fragment.ReceiveProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;
import io.vov.vitamio.utils.NumberUtil;

/**
 * 收货异步的收货详情页
 *
 * Created by Dong on 2017/12/21.
 */

public class ReceiveDetailActivity extends NetWorkActivity {

    public static final String INTENT_KEY_ORDER_ID = "order_id";
    public static final int REQUEST_DETAIL = 0x34;
    public static final int REQUEST_CATEGORY = 0x344;
    private int orderId;
    private OrderResponse.ListBean bean;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.viewpager)
    private ViewPager viewpager;
    @ViewInject(R.id.tv_receive_sum)
    private TextView mTvSummary;
    private CategoryRespone categoryRespone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_async_receive);
        setTitleText(true, "收货清单");
        showBackBtn();
        Bundle bundle = getIntent().getExtras();
        orderId = bundle.getInt(INTENT_KEY_ORDER_ID, 0);

        //需要自己刷新
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/order/");
        sb.append(orderId).append("/");
        sendConnection(sb.toString(), request, REQUEST_DETAIL, false, OrderDetailResponse.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_DETAIL:
                BaseEntity.ResultBean resultBean = result.getResult();
                OrderDetailResponse response = (OrderDetailResponse) resultBean.getData();
                bean = response.getOrder();
                GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
                sendConnection("/api/product/category", getCategoryRequest, REQUEST_CATEGORY, false, CategoryRespone.class);
                loadingLayout.onSuccess(1, "暂时没有数据哦");
                break;
            case REQUEST_CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                updateUI();
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    public void updateUI(){
        //计算总数
        double total = 0;
        double totalActual = 0;
        for(OrderResponse.ListBean.LinesBean linesBean:bean.getLines()){
            total = total + linesBean.getProductUomQty();
            totalActual = totalActual + linesBean.getDeliveredQty();
        }
        mTvSummary.setText(Html.fromHtml("<font color=\"#666666\">已收 </font>" +
                "<font color=\"#6BB400\">"+ NumberUtil.getIOrD(totalActual) +"</font>" +
                "<font color=\"#666666\">/"+ NumberUtil.getIOrD(total) + "件</font>"));
        setUpDataForViewPage();
    }

    private void setUpDataForViewPage() {
        List<Fragment> orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<OrderResponse.ListBean.LinesBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<OrderResponse.ListBean.LinesBean>());
        }
        for (OrderResponse.ListBean.LinesBean linesBean : bean.getLines()) {
            ArrayList<OrderResponse.ListBean.LinesBean> linesBeanList = map.get(linesBean.getCategory());
            if (linesBeanList == null) {
                linesBeanList = new ArrayList<>();
                map.put(linesBean.getCategory(), linesBeanList);
            }
            linesBeanList.add(linesBean);
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<OrderResponse.ListBean.LinesBean> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        orderProductFragmentList.add(0, newProductFragment((ArrayList<OrderResponse.ListBean.LinesBean>) bean.getLines()));

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
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public ReceiveProductFragment newProductFragment(ArrayList<OrderResponse.ListBean.LinesBean> value) {
        ReceiveProductFragment orderProductFragment = new ReceiveProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ReceiveProductFragment.BUNDLE_KEY_LIST, value);
        bundle.putBoolean(ReceiveProductFragment.BUNDLE_KEY_TWO_UNIT, bean.isIsTwoUnit());
        orderProductFragment.setArguments(bundle);
        return orderProductFragment;
    }
}
