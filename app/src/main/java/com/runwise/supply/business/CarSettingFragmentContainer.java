package com.runwise.supply.business;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.LauncherActivity;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.CollectResponse;
import com.runwise.supply.business.entity.Item;
import com.runwise.supply.tools.ShareUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2017/1/20.
 */

public class CarSettingFragmentContainer extends NetWorkActivity implements Button.OnClickListener,CarTypeFragment.CarTypeInterface{
    private static final String LogoUrl = "http://114.215.40.244:8085/images/icon-logo.png";
    @ViewInject(R.id.mid_layout)
    private LinearLayout mid_layout;
    private Button carBtn;                  //标题上面
    private Button settingBtn;              //标题上面
    private CarTypeFragment carFragment;    //车的页面
    private WebViewFragment settingFragment;//设置页面
    private FragmentManager fragmentManager;
    private Fragment  currentFragment;      //当前加载的
    private List<Item> itemList;            //配置的文本信息
    private boolean isCarCollected;         //当前车系是否被收藏
    private String collectCarId;            //当前车系的id
    private String share_url;               //分享的url
    private String applyInfoUrl;          //分享的应用图标
    private String shareTitle;              //分享的标题
    private Boolean isWebFrom;          //true的话，定制回退键\

    public String getCollectCarId() {
        return collectCarId;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.car_setting_layout);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)){
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }
        setTitleLeftIcon(true,R.drawable.returned);
        setTitleRightIcon2(true,R.drawable.not_collected);
        setTitleRigthIcon(true,R.drawable.partook);
        isWebFrom = getIntent().getBooleanExtra("isWebFrom",false);
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
        carFragment = new CarTypeFragment();
        String carId = getIntent().getStringExtra("carid");
        Bundle bundle = new Bundle();
        bundle.putString("carid",carId);
        carFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragmentContainer,carFragment);
        currentFragment = carFragment;
        fragmentTransaction.commit();
        settingFragment = new WebViewFragment();
    }
    private void addTitleBarBtn(){
        mid_layout.removeAllViews();
        carBtn = new Button(mContext);
        carBtn.setText("车款");
        carBtn.setTag("车款");
        carBtn.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        carBtn.setBackgroundResource(R.drawable.car_setting_circle_select);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200,80);
        params.alignWithParent = true;
        carBtn.setLayoutParams(params);
        mid_layout.addView(carBtn);
        settingBtn = new Button(mContext);
        settingBtn.setText("配置");
        settingBtn.setTag("配置");
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
                if (isWebFrom){
                    finish();
                    startActivity(new Intent(this, LauncherActivity.class));
                    return;
                }
                finish();
                break;
            case R.id.title_iv_rigth:
                ShareUtil.showShare(mContext,"车-详情",carFragment.getCarSeriesTile(),LogoUrl,share_url);
                break;
            case R.id.title_iv_rigth2:
                //收藏操作：如果是已收藏，就是取消收藏
                //判断登录状态
                Intent intent = new Intent(mContext, CarSettingFragmentContainer.class);
                if (UserUtils.checkLogin(intent,this)) {
                    //收藏,如果是收藏，不发请求
                    if (collectCarId == null){
                        ToastUtil.show(mContext,"数据异常，请稍侯重试");
                        break;
                    }
                   requestCollectCarId(!isCarCollected);
                }
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
        if ("车款".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.car_setting_circle_select);
            //切换fragment
            switchContent(currentFragment,carFragment);
        }else if("配置".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.setting_car_circle_select);
            //切换fragment
            switchContent(currentFragment,settingFragment);
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
    public void changeCollectImg(boolean isCollected,String carId,String shareUrl) {
        this.isCarCollected = isCollected;
        collectCarId = carId;
        share_url = shareUrl;
       if (isCollected){
           setTitleRightIcon2(true,R.drawable.already_collect);
       }else{
           setTitleRightIcon2(true,R.drawable.not_collected);
       }
    }
    private void requestCollectCarId(Boolean willCollect){
        CarTypeFragment.CarRequest request = new CarTypeFragment.CarRequest(collectCarId);
        if (willCollect){
            request.setType("1");
        }else{
            request.setType("0");
        }
        sendConnection("/collect/store.json",request,1000,true, CollectResponse.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        CollectResponse response = (CollectResponse) result;
        if (response != null && response.getData() != null){
            //改变UI
            isCarCollected = !isCarCollected;
            if (isCarCollected){
                ToastUtil.show(mContext,"收藏成功");
            }else {
                ToastUtil.show(mContext,"取消收藏");
            }
            changeCollectImg(isCarCollected,collectCarId,share_url);
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
