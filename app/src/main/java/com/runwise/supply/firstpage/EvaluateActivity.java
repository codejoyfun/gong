package com.runwise.supply.firstpage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.CheckedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateActivity extends NetWorkActivity implements EvaluateAdapter.RatingBarClickCallback,CheckBox.OnCheckedChangeListener {
    @ViewInject(R.id.indexLine)
    private View indexLine;
    @ViewInject(R.id.headSdv)
    private SimpleDraweeView headSdv;
    @ViewInject(R.id.recyclerView)
    private RecyclerView    recyclerView;
    @ViewInject(R.id.nameTv)
    private TextView        nameTv;
    @ViewInject(R.id.timeTv)
    private TextView        timeTv;
    @ViewInject(R.id.cb1)
    private CheckBox        cb1;
    @ViewInject(R.id.cb2)
    private CheckBox        cb2;
    @ViewInject(R.id.cb3)
    private CheckBox        cb3;


    private EvaluateAdapter adapter;
    private OrderResponse.ListBean bean;
    //维护星星分数的集合
    private Map<String,Integer> rateMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.evaluate_layout);
        setTitleText(true,"评价");
        setTitleLeftIcon(true,R.drawable.nav_back);
        setTitleRightText(true,"提交");
        setDefaultDatas();
    }

    private void setDefaultDatas() {
        int tabWidth = (CommonUtils.getScreenWidth(this) - CommonUtils.dip2px(this,30))/3;
        int translationX = (tabWidth - CommonUtils.dip2px(this,51))/2 + CommonUtils.dip2px(this,15);
        indexLine.setTranslationX(translationX);
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("order");
        adapter = new EvaluateAdapter(this,null,rateMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        selectProductTypeData(DataType.LENGCANGHUO);

        if(bean != null && bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null){
            String deliverName = bean.getWaybill().getDeliverUser().getName();
            nameTv.setText(deliverName);
            String imgUrl = bean.getWaybill().getDeliverUser().getAvatarUrl();
            FrecoFactory.getInstance(mContext).disPlay(headSdv, Constant.BASE_URL+imgUrl);
        }else{
            nameTv.setText("未知");
        }
        String estimatTime = bean.getEstimatedTime();
        String endUploadTime = bean.getEndUnloadDatetime();
        StringBuffer sb = new StringBuffer("预计送达时间 ");
        sb.append(estimatTime)
                .append("    ")
                .append("开始卸货时间 ")
                .append(endUploadTime);
        timeTv.setText(sb.toString());
        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
    }
    @OnClick({R.id.coldBtn,R.id.freezeBtn,R.id.dryBtn,R.id.title_iv_left})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.coldBtn:
                //切换指示器
                switchTabIndex(DataType.LENGCANGHUO);
                //筛选列表数据
                selectProductTypeData(DataType.LENGCANGHUO);
                break;
            case R.id.freezeBtn:
                switchTabIndex(DataType.FREEZE);
                selectProductTypeData(DataType.FREEZE);
                break;
            case R.id.dryBtn:
                switchTabIndex(DataType.DRY);
                selectProductTypeData(DataType.DRY);
                break;
            case R.id.title_iv_left:
                finish();
                break;
        }
    }

    private void selectProductTypeData(DataType type) {
        List<OrderResponse.ListBean.LinesBean> list = bean.getLines();
        List<OrderResponse.ListBean.LinesBean> typeList = new ArrayList<>();
        for (OrderResponse.ListBean.LinesBean lb : list){
            if (lb.getStockType().equals(type.getType())){
                typeList.add(lb);
            }
        }
        adapter.setProductList(typeList);
    }

    private void switchTabIndex(DataType type) {
        int tabWidth = (CommonUtils.getScreenWidth(this) - CommonUtils.dip2px(this,30))/3;
        int padding = (tabWidth - CommonUtils.dip2px(this,51))/2;
        float translationX = 0.0F;
        switch (type){
            case FREEZE:
                translationX = CommonUtils.dip2px(mContext,15) + tabWidth + padding;
                break;
            case LENGCANGHUO:
                translationX = CommonUtils.dip2px(mContext,15) + padding;
                break;
            case DRY:
                translationX = CommonUtils.dip2px(mContext,15) + 2*tabWidth + padding;
                break;
        }
        ViewPropertyAnimator.animate(indexLine).translationX(translationX);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void rateChanged(String productId, Integer rateScore) {
        rateMap.put(productId,rateScore);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb1:
                if(isChecked){
                    cb1.setTextColor(Color.parseColor("#9ACC35"));
                }else{
                    cb1.setTextColor(Color.parseColor("#CCCCCC"));
                }

                break;
            case R.id.cb2:
                if(isChecked){
                    cb2.setTextColor(Color.parseColor("#9ACC35"));
                }else{
                    cb2.setTextColor(Color.parseColor("#CCCCCC"));
                }
                break;
            case R.id.cb3:
                if(isChecked){
                    cb2.setTextColor(Color.parseColor("#9ACC35"));
                }else{
                    cb2.setTextColor(Color.parseColor("#CCCCCC"));
                }
                break;
        }
    }
}
