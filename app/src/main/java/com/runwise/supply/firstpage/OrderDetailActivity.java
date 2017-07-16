package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderDetailActivity extends NetWorkActivity {
    private OrderResponse.ListBean bean;
    private List<OrderResponse.ListBean.LinesBean> listDatas = new ArrayList<>();
    private List<OrderResponse.ListBean.LinesBean> typeDatas = new ArrayList<>();
    private OrderDtailAdapter adapter;
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
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
    private Button uploadBtn;
    @ViewInject(R.id.receivtTv)
    private TextView receivtTv;
    @ViewInject(R.id.returnTv)
    private TextView returnTv;
    @ViewInject(R.id.ygMoneyTv)
    private TextView ygMoneyTv;
    @ViewInject(R.id.countTv)
    private TextView countTv;
    @ViewInject(R.id.indexLine)
    private View indexLine;         //指示线

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_layout);
        setTitleText(true,"订单详情");
        setTitleLeftIcon(true,R.drawable.nav_back);
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("order");
        adapter = new OrderDtailAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        updateUI();
    }
    private void updateUI() {
        if (bean != null){
            String state = "";
            String tip = "";
            if (bean.getState().equals("draft")){
                state = "订单已提交";
                tip = "订单号："+bean.getName();
            }else if(bean.getState().equals("sale")){
                state = "订单已确认";
                tip = "正在为您挑拣商品";
            }else if(bean.getState().equals("peisong")){
                state = "订单已发货";
                tip = "预计发达时间："+bean.getEstimatedTime();
            }else if (bean.getState().equals("done")){
                state = "订单已收货";
                tip = "收货人："+bean.getReceiveUserName();
            }
            orderStateTv.setText(state);
            tipTv.setText(tip);
            //支付凭证在收货流程后，才显示
            if (bean.getState().equals("rated") || bean.getState().equals("done")){
                payStateTv.setVisibility(View.VISIBLE);
                payStateValue.setVisibility(View.VISIBLE);
                uploadBtn.setVisibility(View.VISIBLE);
                if (bean.getHasAttachment() == 0){
                    payStateValue.setText("未有支付凭证");
                }else{
                    payStateValue.setText("已上传支付凭证");
                }
                //同时，显示右上角，申请售后
                setTitleRightText(true,"申请售后");
            }
            //订单信息
            orderNumTv.setText(bean.getName());
            buyerValue.setText(bean.getCreateUserName());
            orderTimeValue.setText(bean.getCreateDate());
            if (bean.getHasReturn() == 0){
                returnTv.setVisibility(View.INVISIBLE);
            }else{
                returnTv.setVisibility(View.VISIBLE);
            }
            //实收判断
            if (isRealReceive()){
                receivtTv.setVisibility(View.VISIBLE);
            }else{
                receivtTv.setVisibility(View.INVISIBLE);
            }
            //商品数量/预估金额
            ygMoneyTv.setText(bean.getAmountTotal()+"元");
            countTv.setText(bean.getAmount()+"件");
            //设置list
            listDatas = bean.getLines();
            adapter.setProductList(listDatas);
            //默认线在全部上
            int padding = (CommonUtils.getScreenWidth(this)/4 - CommonUtils.dip2px(mContext,36))/2;
            ViewPropertyAnimator.animate(indexLine).translationX(padding);

        }
    }
    //实收代表收货数量和原本下单数量不相等
    private boolean isRealReceive() {
        List<OrderResponse.ListBean.LinesBean> listBeanArr =  bean.getLines();
        if (listBeanArr != null){
            for (OrderResponse.ListBean.LinesBean lBean : listBeanArr){
                if (lBean.getDeliveredQty() !=lBean.getProductUomQty()){
                    return true;
                }
            }
        }
        return false;
    }

    @OnClick({R.id.title_iv_left,R.id.allBtn,R.id.coldBtn,R.id.freezeBtn,R.id.dryBtn})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.allBtn:
                //切换页签到全部上面
                switchTabBy(DataType.ALL);
                break;
            case R.id.coldBtn:
                switchTabBy(DataType.LENGCANGHUO);
                //
                break;
            case R.id.freezeBtn:
                switchTabBy(DataType.FREEZE);
                break;
            case R.id.dryBtn:
                switchTabBy(DataType.DRY);
                break;
        }
    }

    private void switchTabBy(DataType type) {
        int padding = (CommonUtils.getScreenWidth(this)/4 - CommonUtils.dip2px(mContext,36))/2;
        int tabWidth = CommonUtils.getScreenWidth(this)/4;
        float translationX = 0.0F;
        switch (type){
            case ALL:
                adapter.setProductList(listDatas);
                translationX = padding;
                break;
            case LENGCANGHUO:
                translationX = tabWidth + padding;
                break;
            case FREEZE:
                translationX = 2*tabWidth + padding;
                break;
            case DRY:
                translationX = 3 * tabWidth + padding;
                break;
        }
        ViewPropertyAnimator.animate(indexLine).translationX(translationX);
        typeDatas.clear();
        if (type == DataType.ALL){
            adapter.setProductList(listDatas);
        }else{
            for (OrderResponse.ListBean.LinesBean bean : listDatas){
                if (bean.getStockType().equals(DataType.LENGCANGHUO.getType()) && type == DataType.LENGCANGHUO){
                    typeDatas.add(bean);
                }else if (bean.getStockType().equals(DataType.FREEZE.getType())  && type == DataType.FREEZE){
                    typeDatas.add(bean);
                }else if (bean.getStockType().equals(DataType.DRY.getType())  && type == DataType.DRY){
                    typeDatas.add(bean);
                }

            }
            adapter.setProductList(typeDatas);
        }

    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

}
