package com.runwise.supply.orderpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.OrderSuccessEvent;
import com.kids.commonframe.base.bean.SystemUpgradeNoticeEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.RegisterActivity;
import com.runwise.supply.orderpage.entity.LastBuyResponse;
import com.runwise.supply.view.SystemUpgradeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/6/29.
 */

public class OrderFragment extends NetWorkFragment {
    private static final int LASTBUY = 1;
    @ViewInject(R.id.lastBuyTv)
    private TextView lastBuyTv;
    @ViewInject(R.id.mainll)
    private View mainll;
    @ViewInject(R.id.dayWeekTv)
    private TextView dwTv;
    @ViewInject(R.id.sureBtn)
    private Button sureBtn;
    @ViewInject(R.id.safeValueTv)
    private TextView safeValueTv;
    @ViewInject(R.id.editText)
    private EditText editText;
    @ViewInject(R.id.layout_system_upgrade_notice)
    private SystemUpgradeLayout mLayoutUpgradeNotice;
    private ListPopupWindow popupWindow;
    String[] times = {"天","周"};
    private OptionsPickerView opv;
    private List<String> safeArr = new ArrayList<>();
    private int selectedIndex = 109;                              //最近一次所选,默认在+10上
    private boolean isBackFirstPage = false;
    @Override
    protected int createViewByLayoutId() {
        return R.layout.order_fragment_layout;
    }
    @OnClick({R.id.dayWeekTv,R.id.sureBtn,R.id.selfHelpBtn,R.id.safeValueTv})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.dayWeekTv:
                if (popupWindow  == null){
                    popupWindow = new ListPopupWindow(mContext);
                    popupWindow.setAdapter(new ArrayAdapter<String>(mContext,R.layout.pop_item,times));
                    popupWindow.setAnchorView(view);
                    popupWindow.setDropDownGravity(Gravity.CENTER);
                    popupWindow.setWidth(CommonUtils.dip2px(mContext,81));
                    popupWindow.setHeight(CommonUtils.dip2px(mContext,93));
                    popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            dwTv.setText(adapterView.getAdapter().getItem(i).toString());
                            popupWindow.dismiss();

                        }
                    });
                    popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.order_whitebg));
                }
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else{
                    popupWindow.show();
                }
                break;
            case R.id.sureBtn:
                if(!SPUtils.isLogin(getActivity())){
                    startActivity(new Intent(getActivity(), RegisterActivity.class));
                    return;
                }
                //跳转到智能下单页面
                Intent intent = new Intent(mContext,OneKeyOrderActivity.class);
                intent.putExtra("yongliang_factor",Double.valueOf(safeArr.get(selectedIndex)));
                intent.putExtra("predict_sale_amount",Double.valueOf(editText.getText().toString()));
                startActivity(intent);
                break;
            case R.id.selfHelpBtn:
                if (SPUtils.isLogin(mContext)){
                    Intent intent2 = new Intent(mContext,SelfHelpOrderActivity.class);
                    startActivity(intent2);
                }else{
                    startActivity(new Intent(getActivity(), RegisterActivity.class));
                }
                break;
            case R.id.safeValueTv:
                if (opv == null){
                    opv = new OptionsPickerView.Builder(mContext,new OptionsPickerView.OnOptionsSelectListener(){

                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            if (safeArr.size() > options1){
                                selectedIndex = options1;
                               String selectStr = safeArr.get(options1);
                                safeValueTv.setText(selectStr +" %");
                            }
                        }
                    }).setTitleText("选择安全系数")
                            .setTitleBgColor(Color.parseColor("#F3F9EF"))
                            .setTitleSize(16)
                            .setSubCalSize(14)
                            .setContentTextSize(23)
                            .setCancelColor(Color.parseColor("#2E2E2E"))
                            .setSubmitColor(Color.parseColor("#2E2E2E"))
                            .build();
                    opv.setPicker(safeArr);
                }
                opv.setSelectOptions(selectedIndex);
                opv.show(true);
                break;
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = -99; i<=99; i++){
            String str = i < 0 ? String.valueOf(i) : ("+"+i);
            safeArr.add(str);
        }
        //设置canSee,登录且能看显示，否则不可见
        if (SPUtils.isLogin(mContext) && GlobalApplication.getInstance().getCanSeePrice()){
            lastBuyTv.setVisibility(View.VISIBLE);
            Object request = null;
            sendConnection("/gongfu/v2/order/last_order_amout/",request,LASTBUY,false, LastBuyResponse.class);
        }else{
            lastBuyTv.setVisibility(View.INVISIBLE);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sureBtn.setEnabled(s.length() > 0);
                float alpha = s.length() > 0 ? 1.0F : 0.4F;
                sureBtn.setAlpha(alpha);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mainll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mLayoutUpgradeNotice.setPageName("下单功能");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBackFirstPage){
            isBackFirstPage = false;
            MainActivity ma = (MainActivity) getActivity();
            ma.gotoTabByIndex(0);
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case LASTBUY:
                BaseEntity.ResultBean response = result.getResult();
                LastBuyResponse lbr = (LastBuyResponse) response.getData();
                double amount = lbr.getAmout();
                DecimalFormat df = new DecimalFormat("#.#");
                lastBuyTv.setText("上次采购额 ¥"+df.format(amount));
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getOrderSuccessEvent(OrderSuccessEvent event){
        isBackFirstPage = true;

    }
}
