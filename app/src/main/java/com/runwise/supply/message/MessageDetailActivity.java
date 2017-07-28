package com.runwise.supply.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 聊天详情
 */

public class MessageDetailActivity extends NetWorkActivity implements Button.OnClickListener{
    private static final String LogoUrl = "http://114.215.40.244:8085/images/icon-logo.png";
    @ViewInject(R.id.mid_layout)
    private LinearLayout mid_layout;
    private Button carBtn;                  //标题上面
    private Button settingBtn;              //标题上面
    private MessageListFragment chatFragment1;    //车的页面
    private MessageListFragment chatFragment2;//设置页面
    private FragmentManager fragmentManager;
    private Fragment currentFragment;      //当前加载的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.car_setting_layout);
        setTitleLeftIcon(true,R.drawable.returned);
        setTitleRightIcon2(true,R.drawable.not_collected);
        setTitleRigthIcon(true,R.drawable.partook);
//        isWebFrom = getIntent().getBooleanExtra("isWebFrom",false);
        //中间添加两个切换按钮
        addTitleBarBtn();
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
        String carId = getIntent().getStringExtra("carid");
        Bundle bundle = new Bundle();
        bundle.putString("carid",carId);
        chatFragment1.setArguments(bundle);
        fragmentTransaction.add(R.id.fragmentContainer, chatFragment1);
        currentFragment = chatFragment1;
        fragmentTransaction.commit();
        chatFragment2 = new MessageListFragment();
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
        settingBtn.setTextColor(ContextCompat.getColor(mContext, R.color.btn_green));
        settingBtn.setLayoutParams(params);
        mid_layout.addView(settingBtn);
        carBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

    }
    @OnClick({R.id.title_iv_left,R.id.title_iv_rigth,R.id.title_iv_rigth2})
    public void btnClick(View view){
        switch(view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.title_iv_rigth:
//                ShareUtil.showShare(mContext,"车-详情",chatFragment1.getCarSeriesTile(),LogoUrl,share_url);
                break;
            case R.id.title_iv_rigth2:
                break;
        }
    }
    @Override
    public void onClick(View view) {
        settingBtn.setBackgroundResource(R.drawable.setting_car_circle);
        carBtn.setBackgroundResource(R.drawable.car_setting_circle);
        carBtn.setTextColor(ContextCompat.getColor(mContext, R.color.btn_green));
        settingBtn.setTextColor(ContextCompat.getColor(mContext, R.color.btn_green));
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
