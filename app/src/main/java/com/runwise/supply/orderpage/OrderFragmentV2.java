package com.runwise.supply.orderpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.OrderSuccessEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.tools.RunwiseService;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.runwise.supply.orderpage.OrderSubmitActivity.INTENT_KEY_SELF_HELP;

/**
 * 新版订单tab
 * 包含常购订单
 *
 * Created by Dong on 2017/6/29.
 */

public class OrderFragmentV2 extends NetWorkFragment {

    private boolean isBackFirstPage = false;
    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_tab_order;
    }


    @OnClick({R.id.rl_tab_order_always,R.id.rl_tab_order_intelligent,R.id.rl_tab_order_self_help})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.rl_tab_order_always:
                if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
                if (SPUtils.isLogin(mContext)){
                    if (!ProductBasicUtils.isInit(getActivity())){
                        return;
                    }
                    Intent intent2 = new Intent(mContext,AlwaysOrderActivity.class);
                    startActivity(intent2);
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.rl_tab_order_intelligent:
                if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
                if (SPUtils.isLogin(mContext)){
                    if (!ProductBasicUtils.isInit(getActivity())){
                        return;
                    }
                    Intent intent2 = new Intent(mContext,SmartOrderActivity.class);
                    startActivity(intent2);
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.rl_tab_order_self_help:
                if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
                if (SPUtils.isLogin(mContext)){
                    if (!ProductBasicUtils.isInit(getActivity())){
                        return;
                    }
                    Intent intent2 = new Intent(mContext,ProductActivityV2.class);
                    intent2.putExtra(INTENT_KEY_SELF_HELP,true);//自助下单
                    startActivity(intent2);
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBackFirstPage){
            isBackFirstPage = false;
            MainActivity ma = (MainActivity) getActivity();
            ma.gotoTabByIndex(0);
        }
        MobclickAgent.onPageStart("下单首页");
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("下单首页");
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getOrderSuccessEvent(OrderSuccessEvent event){
        isBackFirstPage = true;
    }

}
