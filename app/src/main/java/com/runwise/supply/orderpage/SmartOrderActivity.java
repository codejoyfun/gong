package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.OrderSuccessEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.LastBuyResponse;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.runwise.supply.view.SystemUpgradeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.runwise.supply.orderpage.OrderSubmitActivity.INTENT_KEY_SELF_HELP;

/**
 * 智能下单
 *
 * Created by Dong on 2017/12/27.
 */

public class SmartOrderActivity extends NetWorkActivity {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_order);
        setTitleText(true,"智能下单");
        showBackBtn();
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

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_NEXT){
                    InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm!=null)imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    showOpv();
                    return true;
                }
                return false;
            }
        });

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

    @OnClick({R.id.dayWeekTv,R.id.sureBtn,R.id.safeValueTv})
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
                if(!SystemUpgradeHelper.getInstance(this).check(this))return;
                if(!SPUtils.isLogin(this)){
                    startActivity(new Intent(this, LoginActivity.class));
                    return;
                }
                //跳转到智能下单页面
//                Intent intent = new Intent(mContext,OneKeyOrderActivity.class);
                Intent intent = new Intent(mContext,OneKeyActivityV2.class);
                intent.putExtra("yongliang_factor",Double.valueOf(safeArr.get(selectedIndex)));
                intent.putExtra("predict_sale_amount",Double.valueOf(editText.getText().toString()));
                startActivity(intent);
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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null)imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                opv.show(true);
                break;
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

    private void showOpv(){
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
    }
}
