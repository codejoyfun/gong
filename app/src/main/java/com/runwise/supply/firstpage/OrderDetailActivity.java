package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.tools.AndroidWorkaround;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderDetailActivity extends NetWorkActivity{
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
    private TextView uploadBtn;
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
    @ViewInject(R.id.gotoStateBtn)
    private Button gotoStateBtn;    //查看更多状态
    @ViewInject(R.id.rightBtn2)
    private Button rightBtn2;
    @ViewInject(R.id.rightBtn)
    private Button rightBtn;
    @ViewInject(R.id.bottom_bar)
    private RelativeLayout bottom_bar;
    private boolean isHasAttachment;        //默认无凭证
    private BottomDialog bDialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener(){
                @Override
                public void bindView(View v) {
                    initViews(v);
                }
            }).setLayoutRes(R.layout.return_replace_layout)
            .setCancelOutside(true)
            .setDimAmount(0.5f);

    private void initViews(View v) {
        TextView returnTv = (TextView) v.findViewById(R.id.returnTv);
        TextView replaceTv = (TextView)v.findViewById(R.id.replaceTv);
        TextView cancleTv = (TextView)v.findViewById(R.id.cancleTv);
        returnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到退换流程：如果超过7天,不支持退货
                if (isMoreThanReturnData()){
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
                }else{
                    Intent intent = new Intent(OrderDetailActivity.this,ReturnActivity.class);
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
                ToastUtil.show(mContext,"暂不支持");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.order_detail_layout);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)){
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }
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
                //底部只有"取消订单"
                rightBtn.setText("取消订单");
                rightBtn2.setVisibility(View.INVISIBLE);
            }else if(bean.getState().equals("sale")){
                state = "订单已确认";
                tip = "正在为您挑拣商品";
                bottom_bar.setVisibility(View.GONE);
            }else if(bean.getState().equals("peisong")){
                state = "订单已发货";
                tip = "预计发达时间："+bean.getEstimatedTime();
                rightBtn.setText("收货");
            }else if (bean.getState().equals("done")){
                state = "订单已收货";
                tip = "收货人："+bean.getReceiveUserName();
                if (TextUtils.isEmpty(bean.getAppraisalUserName())){
                    rightBtn.setText("评价");
                    rightBtn2.setText("售后订单");
                }else{
                    //已评价，只显示一个按钮
                    rightBtn.setText("售后订单");
                    rightBtn2.setVisibility(View.INVISIBLE);
                }


            }else if(bean.getState().equals("rated")){
                state = "订单已评价";
                rightBtn.setText("售后订单");
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
                    uploadBtn.setText("上传凭证");
                    isHasAttachment = false;
                }else{
                    payStateValue.setText("已上传支付凭证");
                    uploadBtn.setText("查看凭证");
                    isHasAttachment = true;
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
            indexLine.setTranslationX(padding);

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

    @OnClick({R.id.title_iv_left,R.id.allBtn,R.id.coldBtn,R.id.title_tv_rigth,R.id.uploadBtn,
            R.id.freezeBtn,R.id.dryBtn,R.id.gotoStateBtn,R.id.rightBtn,R.id.rightBtn2})
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
            case R.id.gotoStateBtn:
                Intent intent = new Intent(this,OrderStateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", bean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.title_tv_rigth:
                if (!bDialog.isVisible()){
                    bDialog.show();
                }else{
                    bDialog.dismiss();
                }
                break;
            case R.id.rightBtn:
                Intent intent2;
                if (rightBtn.getText().toString().equals("收货")){
                    intent2 = new Intent(mContext,ReceiveActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putParcelable("order",bean);
                    intent2.putExtras(bundle2);
                    startActivity(intent2);
                    finish();
                }else if(rightBtn.getText().toString().equals("评价")){
                    intent2 = new Intent(mContext,EvaluateActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putParcelable("order",bean);
                    intent2.putExtras(bundle2);
                    startActivity(intent2);
                    finish();
                }else if(rightBtn.getText().toString().equals("售后订单")){

                }
                break;
            case R.id.uploadBtn:
                //凭证
                Intent intent3 = new Intent(mContext,UploadPayedPicActivity.class);
                intent3.putExtra("orderid",bean.getOrderID());
                intent3.putExtra("ordername",bean.getName());
                intent3.putExtra("hasattachment",isHasAttachment);
                startActivity(intent3);
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
