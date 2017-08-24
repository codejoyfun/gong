package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.MainActivity;
import com.runwise.supply.ProcurementActivity;
import com.runwise.supply.R;
import com.runwise.supply.entity.UnReadData;
import com.runwise.supply.mine.entity.SumMoneyData;
import com.runwise.supply.mine.entity.UpdateUserInfo;
import com.runwise.supply.tools.UserUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by myChaoFile on 16/10/13.
 */

public class MineFragment extends NetWorkFragment {
    private final int REQUEST_SYSTEM = 1;
    private final int REQUEST_SUM = 2;
    //电话
    @ViewInject(R.id.minePhone)
    private TextView minePhone;
    //头像
    @ViewInject(R.id.mineHead)
    private SimpleDraweeView mineHead;
    private UserInfo userInfo;
    boolean isLogin;
    @ViewInject(R.id.ratingbarPeisong)
    private RatingBar ratingbarPeisong;
    @ViewInject(R.id.peisongStr)
    private TextView peisongStr;
    @ViewInject(R.id.peisongImg)
    private ImageView peisongImg;

    @ViewInject(R.id.ratingbarZhiliang)
    private RatingBar ratingbarZhiliang;
    @ViewInject(R.id.zhiliangStr)
    private TextView zhiliangStr;
    @ViewInject(R.id.zhiliangImg)
    private ImageView zhiliangImg;
    String number = "02037574563";

    @ViewInject(R.id.moneySum)
    private TextView moneySum;
    @ViewInject(R.id.moneyNuit)
    private TextView moneyNuit;
    @ViewInject(R.id.showText)
    private TextView showText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin = SPUtils.isLogin(mContext);
        if (isLogin) {
            setLoginStatus();
        }
        else {
            setLogoutStatus();
        }
    }

    private void setLoginStatus() {
        isLogin = true;
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        if (userInfo != null) {
            LogUtils.e(Constant.BASE_URL + userInfo.getAvatarUrl());

            FrecoFactory.getInstance(mContext).disPlay(mineHead, Constant.BASE_URL + userInfo.getAvatarUrl());
            minePhone.setText(userInfo.getUsername());

            ratingbarPeisong.setRating(userInfo.getCateringServiceScore());
            peisongStr.setText(userInfo.getCateringServiceScore()+"");
            if ("-1".equals(userInfo.getCateringServiceTrend())) {
                peisongImg.setImageResource(R.drawable.tag_down);
            }
            else {
                peisongImg.setImageResource(R.drawable.tag_up);
            }

            ratingbarZhiliang.setRating(userInfo.getCateringQualityScore());
            zhiliangStr.setText(userInfo.getCateringQualityScore()+"");
            if ("-1".equals(userInfo.getCateringQualityTrend())) {
                zhiliangImg.setImageResource(R.drawable.tag_down);
            }
            else {
                zhiliangImg.setImageResource(R.drawable.tag_up);
            }
        }
        Object request = null;
        sendConnection("/api/sale/shop/info", request, REQUEST_SUM, false, SumMoneyData.class);
    }

    private void setLogoutStatus() {
        isLogin = false;
        FrecoFactory.getInstance(mContext).disPlay(mineHead, "");
        minePhone.setText("登录/注册>");
        minePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SPUtils.isLogin(mContext)) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        ratingbarPeisong.setRating(0);
        peisongStr.setText(0.0+"");
        peisongImg.setImageResource(R.drawable.tag_up);

        ratingbarZhiliang.setRating(0);
        zhiliangStr.setText(0.0+"");
        zhiliangImg.setImageResource(R.drawable.tag_down);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_SYSTEM:
                break;
            case  REQUEST_SUM:
                SumMoneyData sumMoneyData = (SumMoneyData)result.getResult().getData();
                if(GlobalApplication.getInstance().getCanSeePrice()) {
                    moneySum.setText(sumMoneyData.getTotal_amount()+"");
                    moneyNuit.setVisibility(View.GONE);
                    showText.setText("上周采购额");
                }
                else{
                    moneySum.setText(sumMoneyData.getTotal_number()+"");
                    moneyNuit.setText("件");
                    moneyNuit.setVisibility(View.VISIBLE);
                    showText.setText("上周采购量");
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    @OnClick({R.id.settingIcon,R.id.cellIcon,R.id.mineHead,R.id.itemLayout_1,R.id.itemLayout_2, R.id.itemLayout_3,R.id.itemLayout_4,
            R.id.rl_stocktaking_record,R.id.rl_price_list,R.id.rl_bill,R.id.rl_procurement})
    public void doClickHandler(View view) {
        Intent intent;
        switch (view.getId()) {
            //头像
            case R.id.mineHead:
                intent = new Intent(mContext, EditUserinfoActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.cellIcon:
                if(isLogin) {
                    if(userInfo != null) {
                        number = userInfo.getCompanyHotLine();
                    }
                }
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
            case R.id.settingIcon:
                intent = new Intent(mContext, SettingActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_1:
                intent = new Intent(mContext, OrderActivity.class);
                intent.putExtra("position",1);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_2:
                intent = new Intent(mContext, OrderActivity.class);
                intent.putExtra("position",2);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            //退货记录
            case R.id.itemLayout_3:
                intent = new Intent(mContext, ReturnActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_4:
                intent = new Intent(mContext, OrderActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            //盘点记录
            case R.id.rl_stocktaking_record:
                intent = new Intent(mContext, CheckActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            //价目表
            case R.id.rl_price_list:
                intent = new Intent(mContext, PriceActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    if(GlobalApplication.getInstance().getCanSeePrice()) {
                        startActivity(intent);
                    }
                    else{
                        dialog.setModel(CustomDialog.RIGHT);
                        dialog.setMessageGravity();
                        dialog.setMessage("您没有查看的权限");
                        dialog.setRightBtnListener("知道啦",null);
                        dialog.show();
                    }
                }
                break;
            //对账单
            case R.id.rl_bill:
                intent = new Intent(mContext, AccountsListActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    if(GlobalApplication.getInstance().getCanSeePrice()) {
                        startActivity(intent);
                    }
                    else{
                        dialog.setModel(CustomDialog.RIGHT);
                        dialog.setMessageGravity();
                        dialog.setMessage("您没有查看的权限");
                        dialog.setRightBtnListener("知道啦",null);
                        dialog.show();
                    }
                }
                break;
            case R.id.rl_procurement:
                intent = new Intent(mContext, ProcurementActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        setLoginStatus();
    }

    @Override
    public void onUserLoginout() {
        setLogoutStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUserInfo(UpdateUserInfo userLoginEvent) {
        setLoginStatus();
    }
}
