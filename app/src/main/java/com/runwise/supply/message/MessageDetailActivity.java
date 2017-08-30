package com.runwise.supply.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.message.entity.MessageResult;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 聊天详情
 */

public class MessageDetailActivity extends NetWorkActivity implements Button.OnClickListener{
    @ViewInject(R.id.mid_layout)
    private LinearLayout mid_layout;
    private Button carBtn;                  //标题上面
    private Button settingBtn;              //标题上面
    private MessageListFragment chatFragment1;    //车的页面
    private MessageListFragment chatFragment2;//设置页面
    private FragmentManager fragmentManager;
    private Fragment currentFragment;      //当前加载的
    public static final String INTENT_KEY_ORDER = "orderBean";

    private String mobel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.car_setting_layout);
        setTitleLeftIcon(true,R.drawable.returned);
//        setTitleRightIcon2(true,R.drawable.not_collected);
        setTitleRigthIcon(true,R.drawable.nav_contract);
//        isWebFrom = getIntent().getBooleanExtra("isWebFrom",false);
        MessageResult.OrderBean orderBean = (MessageResult.OrderBean)(getIntent().getSerializableExtra(INTENT_KEY_ORDER));

        String deliveryType =  orderBean.getDeliveryType();
        if (deliveryType != null && (deliveryType.equals(OrderResponse.ListBean.TYPE_STANDARD)
                ||deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH))){
            //中间添加两个切换按钮
            addTitleBarBtn();

        }else{
            setTitleText(true,"在线客服");
        }
        if (savedInstanceState == null){
            initFragments();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //维护两个fragment
    private void initFragments(){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        chatFragment1 = new MessageListFragment();
        chatFragment1.type = 0;
        fragmentTransaction.add(R.id.fragmentContainer, chatFragment1);
        currentFragment = chatFragment1;
        fragmentTransaction.commit();
        chatFragment2 = new MessageListFragment();
        chatFragment2.type = 1;
    }
    private void addTitleBarBtn(){
        mid_layout.removeAllViews();
        carBtn = new Button(mContext);
        carBtn.setText("在线客服");
        carBtn.setTag("在线客服");
        carBtn.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        carBtn.setBackgroundResource(R.drawable.car_setting_circle_select);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200,80);
        params.alignWithParent = true;
        carBtn.setLayoutParams(params);
        mid_layout.addView(carBtn);
        settingBtn = new Button(mContext);
        settingBtn.setText("配送员");
        settingBtn.setTag("配送员");
        settingBtn.setBackgroundResource(R.drawable.setting_car_circle);
        settingBtn.setTextColor(ContextCompat.getColor(mContext, R.color.base_color));
        settingBtn.setLayoutParams(params);
        mid_layout.addView(settingBtn);
        carBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

    }
    @OnClick({R.id.title_iv_left,R.id.right_layout})
    public void btnClick(View view){
        switch(view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
//            case R.id.title_iv_rigth:
////                ShareUtil.showShare(mContext,"车-详情",chatFragment1.getCarSeriesTile(),LogoUrl,share_url);
//                break;
            case R.id.right_layout:
                MessageResult.OrderBean orderBean = (MessageResult.OrderBean) this.getIntent().getSerializableExtra("orderBean");
                final CustomBottomDialog customBottomDialog = new CustomBottomDialog(mContext);
                ArrayMap<Integer, String> menus = new ArrayMap<>();
                menus.put(0, "联系客服");
                if(orderBean.getWaybill() != null && orderBean.getWaybill().getUser() != null && !TextUtils.isEmpty(orderBean.getWaybill().getUser().getMobile())) {
                    mobel = orderBean.getWaybill().getUser().getMobile();
                    menus.put(1, "联系配送员");
                }
                customBottomDialog.addItemViews(menus);
                customBottomDialog.setOnBottomDialogClick(new CustomBottomDialog.OnBottomDialogClick() {
                    @Override
                    public void onItemClick(View view) {
                        switch (view.getId()) {
                            case 0:
                                final String number = GlobalApplication.getInstance().loadUserInfo().getCompanyHotLine();
                                dialog.setModel(CustomDialog.BOTH);
                                dialog.setTitle("联系客服");
                                dialog.setMessageGravity();
                                dialog.setMessage(number);
                                dialog.setLeftBtnListener("取消",null);
                                dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
                                    @Override
                                    public void doClickButton(Button btn, CustomDialog dialog) {
                                        CommonUtils.callNumber(mContext,number);
                                    }
                                });
                                dialog.show();
                                break;
                            case 1:
                                dialog.setModel(CustomDialog.BOTH);
                                dialog.setTitle("联系配送员");
                                dialog.setMessageGravity();
                                dialog.setMessage(mobel);
                                dialog.setLeftBtnListener("取消",null);
                                dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
                                    @Override
                                    public void doClickButton(Button btn, CustomDialog dialog) {
                                        CommonUtils.callNumber(mContext,mobel);
                                    }
                                });
                                dialog.show();
                                break;

                        }
                        customBottomDialog.dismiss();
                    }
                });
                customBottomDialog.show();
                break;
        }
    }
    @Override
    public void onClick(View view) {
        settingBtn.setBackgroundResource(R.drawable.setting_car_circle);
        carBtn.setBackgroundResource(R.drawable.car_setting_circle);
        carBtn.setTextColor(ContextCompat.getColor(mContext, R.color.base_color));
        settingBtn.setTextColor(ContextCompat.getColor(mContext, R.color.base_color));
        ((Button)view).setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        if ("在线客服".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.car_setting_circle_select);
            //切换fragment
            switchContent(currentFragment, chatFragment1);
        }else if("配送员".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.setting_car_circle_select);
            //切换fragment
            switchContent(currentFragment, chatFragment2);
        }
    }
    public void switchContent(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragmentContainer, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
