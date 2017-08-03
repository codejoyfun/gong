package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.List;

/**
 * Created by libin on 2017/8/1.
 */

public class ReturnDetailActivity extends NetWorkActivity {
    @ViewInject(R.id.orderStateTv)
    private TextView orderStateTv;
    @ViewInject(R.id.tipTv)
    private TextView tipTv;
    @ViewInject(R.id.dateTv)
    private TextView dateTv;
    @ViewInject(R.id.orderNumTv)
    private TextView orderNumTv;
    @ViewInject(R.id.buyerValue)
    private TextView buyerValue;
    @ViewInject(R.id.orderTimeValue)
    private TextView orderTimeValue;
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    private ReturnDetailAdapter adapter;
    @ViewInject(R.id.countTv)
    private TextView countTv;
    @ViewInject(R.id.ygMoneyTv)
    private TextView ygMoneyTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.return_detain_layout);
        setTitleText(true,"退货单详情");
        setTitleLeftIcon(true,R.drawable.nav_back);
        initViews();
    }

    private void initViews() {
        dateTv.setVisibility(View.INVISIBLE);
        adapter = new ReturnDetailAdapter(mContext);
        LinearLayoutManager mgr = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mgr);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        Bundle bundle = getIntent().getExtras();
        ReturnOrderBean.ListBean bean = bundle.getParcelable("return");
        if (bean.getState().equals("process")){
            orderStateTv.setText("退货进行中");
            tipTv.setText("原销售订单：SO"+bean.getOrderID());
        }else{
            orderStateTv.setText("退货成功");
            tipTv.setText("退货成功，退货商品：");
        }
        orderNumTv.setText(bean.getName());
        buyerValue.setText(bean.getCreateUser());
        orderTimeValue.setText(bean.getCreateDate());
        List<ReturnOrderBean.ListBean.LinesBean> list = bean.getLines();
        adapter.setReturnList(list);
        recyclerView.getLayoutParams().height = list.size()* CommonUtils.dip2px(mContext,86);
        countTv.setText(bean.getAmount()+"件");
        ygMoneyTv.setText(bean.getAmountTotal()+"元");

    }

    @OnClick({R.id.title_iv_left,R.id.gotoStateBtn})
    public void btnClick(View view){
        switch(view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.gotoStateBtn:
                Intent intent = new Intent(mContext,OrderStateActivity.class);
                intent.putExtra("mode",true);
                Bundle bundle = getIntent().getExtras();
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}