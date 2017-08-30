package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnDetailResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.List;

import static com.runwise.supply.firstpage.ReturnSuccessActivity.INTENT_KEY_RESULTBEAN;

/**
 * Created by libin on 2017/8/1.
 */

public class ReturnDetailActivity extends NetWorkActivity {
    private static final int DETAIL = 0;
    private static final int FINISHRETURN = 1;
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
    @ViewInject(R.id.doBtn)
    private TextView doBtn;
    @ViewInject(R.id.priceLL)
    private LinearLayout priceLL;
    @ViewInject(R.id.rl_bottom)
    private RelativeLayout rlBottom;

    @ViewInject(R.id.payStateValue)
    private TextView payStateValue;
    @ViewInject(R.id.payStateTv)
    private TextView payStateTv;
    @ViewInject(R.id.uploadBtn)
    private Button uploadBtn;

    private ReturnOrderBean.ListBean bean;
    public static final int REQUEST_CODE_UPLOAD = 1<<0;

    private boolean hasAttatchment = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.return_detain_layout);
        setTitleText(true, "退货单详情");
        setTitleLeftIcon(true, R.drawable.nav_back);
        String rid = getIntent().getStringExtra("rid");
        initViews();
        if (TextUtils.isEmpty(rid)) {
            updateUI();
        } else {
            //发网络请求获取
            Object request = null;
            StringBuffer sb = new StringBuffer("/gongfu/v2/return_order/");
            sb.append(rid).append("/");
            sendConnection(sb.toString(), request, DETAIL, true, ReturnDetailResponse.class);
        }


    }

    private void initViews() {
        boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        if (!canSeePrice) {
            priceLL.setVisibility(View.GONE);
        }
        dateTv.setVisibility(View.INVISIBLE);
        adapter = new ReturnDetailAdapter(mContext);
        LinearLayoutManager mgr = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mgr);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("return");

        String deliveryType = bean.getDeliveryType();
        //不显示
        if (bean.getState().equals("process")) {
            if (deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH) || deliveryType.equals(OrderResponse.ListBean.TYPE_STANDARD)) {
                rlBottom.setVisibility(View.GONE);
            }
            if (deliveryType.equals(OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY) || deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_THIRD_PART_DELIVERY)) {
                if (bean.getWaybill() == null || bean.getWaybill().getDeliverUser() == null || TextUtils.isEmpty(bean.getWaybill().getDeliverUser().getName())) {
                    rlBottom.setVisibility(View.GONE);
                }
            }
        } else {
            rlBottom.setVisibility(View.GONE);
        }
        payStateTv.setVisibility(View.VISIBLE);
        payStateValue.setVisibility(View.VISIBLE);
        uploadBtn.setVisibility(View.VISIBLE);
        hasAttatchment = bean.getHasAttachment() >0;
        updateReturnView();
    }

    private void updateReturnView(){
        if (!hasAttatchment){
            payStateTv.setText("退货凭证: ");
            payStateValue.setText("未有退货凭证");
            uploadBtn.setText("上传退货凭证");

        }else{
            payStateTv.setText("退货凭证: ");
            payStateValue.setText("已有退货凭证");
            uploadBtn.setText("查看退货凭证");
        }
    }

    private void updateUI() {
        if (bean != null) {
            //获取物流状态
            List<String> trackerList = bean.getStateTracker();
            if (trackerList != null && trackerList.size() > 0) {
                String newestTrack = trackerList.get(0);
                String[] pathStr = newestTrack.split(" ");
                if (pathStr.length == 3) {   //正常状态，取最新的物流状态
                    orderStateTv.setText(pathStr[2]);
                } else {
                    //异常，自己设置
                    if (bean.getState().equals("process")) {
                        orderStateTv.setText("退货进行中");
//                tipTv.setText("原销售订单：SO"+bean.getOrderID());
                    } else {
                        orderStateTv.setText("退货成功");
                    }
                }
            }
            StringBuffer sb = new StringBuffer("退货商品：");
            sb.append((int)bean.getAmount()).append("件");
//                    .append(" 共").append(bean.getAmountTotal()).append("元");
            tipTv.setText(sb.toString());
            orderNumTv.setText(bean.getName());
            buyerValue.setText(bean.getCreateUser());
            orderTimeValue.setText(bean.getCreateDate());
            List<ReturnOrderBean.ListBean.LinesBean> list = bean.getLines();
            adapter.setReturnList(list);
            recyclerView.getLayoutParams().height = list.size() * CommonUtils.dip2px(mContext, 86);
            countTv.setText((int) bean.getAmount() + "件");
            ygMoneyTv.setText(bean.getAmountTotal() + "元");
        }
    }

    @OnClick({R.id.title_iv_left, R.id.gotoStateBtn, R.id.doBtn,R.id.uploadBtn})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.gotoStateBtn:
                Intent intent = new Intent(mContext, OrderStateActivity.class);
                intent.putExtra("mode", true);
                Bundle bundle = getIntent().getExtras();
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.doBtn:
                dialog.setMessageGravity();
                dialog.setMessage("确认数量一致?");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        Object request = null;
                        sendConnection("/gongfu/v2/return_order/" +
                                bean.getReturnOrderID() +
                                "/done", request, FINISHRETURN, false, FinishReturnResponse.class);
                    }
                });
                dialog.show();
                break;
            case R.id.uploadBtn:
                Intent uIntent = new Intent(mContext, UploadReturnPicActivity.class);
                uIntent.putExtra("orderid", bean.getOrderID());
                uIntent.putExtra("ordername", bean.getName());
                uIntent.putExtra("hasattachment", hasAttatchment);
                startActivityForResult(uIntent,REQUEST_CODE_UPLOAD);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_UPLOAD){
                //刷新界面
                hasAttatchment = data.getBooleanExtra("has",false);
                updateReturnView();
            }
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case DETAIL:
                BaseEntity.ResultBean rb = result.getResult();
                ReturnDetailResponse rdr = (ReturnDetailResponse) rb.getData();
                bean = rdr.getReturnOrder();
                updateUI();
                break;
            case FINISHRETURN:
                FinishReturnResponse finishReturnResponse = (FinishReturnResponse) result.getResult().getData();
                ToastUtil.show(mContext, "退货成功");
                Intent intent = new Intent(mContext, ReturnSuccessActivity.class);
                intent.putExtra(INTENT_KEY_RESULTBEAN, finishReturnResponse);
                startActivity(intent);
                rlBottom.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
