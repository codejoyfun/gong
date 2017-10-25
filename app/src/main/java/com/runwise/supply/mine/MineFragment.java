package com.runwise.supply.mine;

import android.content.Intent;
import android.graphics.Color;
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
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.ProcurementActivity;
import com.runwise.supply.R;
import com.runwise.supply.TransferListActivity;
import com.runwise.supply.mine.entity.SumMoneyData;
import com.runwise.supply.mine.entity.UpdateUserInfo;
import com.runwise.supply.tools.UserUtils;
import com.runwise.supply.view.ObservableScrollView;
import com.runwise.supply.view.SystemUpgradeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.mine.ProcurementLimitActivity.KEY_SUM_MONEY_DATA;

/**
 * Created by myChaoFile on 16/10/13.
 */

public class MineFragment extends NetWorkFragment {
    private final int REQUEST_SYSTEM = 1;
    private final int REQUEST_SUM = 2;
    private static final int REQUEST_USERINFO = 3;
    private static final int REQUEST_USERINFO_PROCUMENT = 4;
    private static final int REQUEST_USERINFO_TRANSFER = 5;

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

    @ViewInject(R.id.orderRed)
    private View orderRed;
    private SumMoneyData sumMoneyData;

    @ViewInject(R.id.observableScrollView)
    private ObservableScrollView observableScrollView;
    @ViewInject(R.id.mTitleLayout)
    private View mTitleLayout;
    @ViewInject(R.id.headView)
    private View headView;
    @ViewInject(R.id.leftImageView)
    private ImageView leftImageView;
    @ViewInject(R.id.rightImageView)
    private ImageView rightImageView;
    @ViewInject(R.id.titleTextView)
    private TextView titleTextView;
    @ViewInject(R.id.layout_upgrade_notice)
    private SystemUpgradeLayout mLayoutUpgradeNotice;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin = SPUtils.isLogin(mContext);
        if (isLogin) {
            userInfo = GlobalApplication.getInstance().loadUserInfo();
            setLoginStatus(userInfo);
        } else {
            setLogoutStatus();
        }
        mTitleLayout.setBackgroundColor(Color.TRANSPARENT);
        observableScrollView.setImageViews(leftImageView, rightImageView, titleTextView);
        observableScrollView.initAlphaTitle(mTitleLayout, headView, getResources().getColor(R.color.white), new int[]{226, 229, 232});
        observableScrollView.setSlowlyChange(true);

        mLayoutUpgradeNotice.setPageName("自采商品/门店调拨/修改个人信息功能");
    }

    private void setLoginStatus(UserInfo userInfo) {
        isLogin = true;
        if (userInfo != null) {
            LogUtils.e(Constant.BASE_URL + userInfo.getAvatarUrl());

            FrecoFactory.getInstance(mContext).disPlay(mineHead, Constant.BASE_URL + userInfo.getAvatarUrl());
            minePhone.setText(userInfo.getUsername());

            ratingbarPeisong.setRating((float) userInfo.getCateringServiceScore());
            peisongStr.setText(NumberUtil.formatOneBit(String.valueOf(userInfo.getCateringServiceScore())));
            if ("-1".equals(userInfo.getCateringServiceTrend())) {
                peisongImg.setImageResource(R.drawable.tag_down);
            } else {
                peisongImg.setImageResource(R.drawable.tag_up);
            }

            ratingbarZhiliang.setRating((float) userInfo.getCateringQualityScore());
            zhiliangStr.setText(NumberUtil.formatOneBit(String.valueOf(userInfo.getCateringQualityScore())));
            if ("-1".equals(userInfo.getCateringQualityTrend())) {
                zhiliangImg.setImageResource(R.drawable.tag_down);
            } else {
                zhiliangImg.setImageResource(R.drawable.tag_up);
            }
        }
        Object request = null;
        sendConnection("/api/sale/shop/info", request, REQUEST_SUM, false, SumMoneyData.class);
    }

    private void requestUserInfo() {
        Object paramBean = null;
        this.sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USERINFO, false, UserInfo.class);
    }

    private void refreshProcument() {
        Object paramBean = null;
        this.sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USERINFO_PROCUMENT, false, UserInfo.class);
    }

    private void refreshTransfer() {
        Object paramBean = null;
        this.sendConnection("/gongfu/v2/user/information", paramBean, REQUEST_USERINFO_TRANSFER, false, UserInfo.class);
    }

    private void setLogoutStatus() {
        isLogin = false;
        FrecoFactory.getInstance(mContext).disPlay(mineHead, "");
        minePhone.setText("登录/注册>");
        minePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPUtils.isLogin(mContext)) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        ratingbarPeisong.setRating(0);
        peisongStr.setText(0.0 + "");
        peisongImg.setImageResource(R.drawable.tag_up);

        ratingbarZhiliang.setRating(0);
        zhiliangStr.setText(0.0 + "");
        zhiliangImg.setImageResource(R.drawable.tag_down);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLogin) {
            requestUserInfo();
        }
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_USERINFO:
                userInfo = (UserInfo) result.getResult().getData();
                if (userInfo.isHasNewInvoice()) {
                    orderRed.setVisibility(View.VISIBLE);
                } else {
                    orderRed.setVisibility(View.GONE);
                }
                GlobalApplication.getInstance().saveUserInfo(userInfo);
                break;
            case REQUEST_USERINFO_PROCUMENT:
                userInfo = (UserInfo) result.getResult().getData();
                if (Boolean.parseBoolean(userInfo.getIsZicai())) {
                    startActivity(new Intent(mContext, ProcurementActivity.class));
                } else {
                    ToastUtil.show(mContext, "没有自采权限");
                }
                break;
            case REQUEST_USERINFO_TRANSFER:
                userInfo = (UserInfo) result.getResult().getData();
                if (userInfo.getIsShopTransfer().equals("true")) {
                    startActivity(new Intent(mContext, TransferListActivity.class));
                } else {
                    ToastUtil.show(mContext, "没有门店调拨权限");
                }
                break;
            case REQUEST_SUM:
                sumMoneyData = (SumMoneyData) result.getResult().getData();
                if (GlobalApplication.getInstance().getCanSeePrice()) {
                    if (sumMoneyData.getTotal_amount() > 10000) {
                        double price = sumMoneyData.getTotal_amount() / 10000;
                        moneySum.setText(UserUtils.formatPrice(price + "") + "");
                        moneyNuit.setText("万元");
                        moneyNuit.setVisibility(View.VISIBLE);
                    } else {
                        moneySum.setText(UserUtils.formatPrice(sumMoneyData.getTotal_amount() + "") + "");
                        moneyNuit.setVisibility(View.VISIBLE);
                        moneyNuit.setText("元");
                    }
                    showText.setText("上周采购额");
                } else {
                    moneySum.setText(sumMoneyData.getTotal_number() + "");
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

    @OnClick({R.id.settingIcon, R.id.cellIcon, R.id.mineHead, R.id.itemLayout_1, R.id.itemLayout_2, R.id.itemLayout_3, R.id.itemLayout_4,
            R.id.rl_stocktaking_record, R.id.rl_price_list, R.id.rl_bill, R.id.rl_procurement, R.id.ll_cai_gou_e, R.id.rl_transfer})
    public void doClickHandler(View view) {
        Intent intent;
        switch (view.getId()) {
            //头像
            case R.id.mineHead:
                intent = new Intent(mContext, EditUserinfoActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.cellIcon:
                String name = "供鲜生";
                if (isLogin) {
                    if (userInfo != null) {
                        number = userInfo.getCompanyHotLine();
                        name = userInfo.getCompany();
                    }
                }
                dialog.setModel(CustomDialog.BOTH);
                dialog.setTitle("致电" + " " + name + " 客服热线");
                dialog.setMessageGravity();
                dialog.setMessage(number);
                dialog.setLeftBtnListener("取消", null);
                dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        CommonUtils.callNumber(mContext, number);
                    }
                });
                dialog.show();
                break;
            case R.id.settingIcon:
                intent = new Intent(mContext, SettingActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_1:
                intent = new Intent(mContext, OrderActivity.class);
                intent.putExtra("position", 1);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_2:
                intent = new Intent(mContext, OrderActivity.class);
                intent.putExtra("position", 2);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            //退货记录
            case R.id.itemLayout_3:
                intent = new Intent(mContext, ReturnActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            case R.id.itemLayout_4:
                intent = new Intent(mContext, OrderActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            //盘点记录
            case R.id.rl_stocktaking_record:
                intent = new Intent(mContext, CheckActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            //价目表
            case R.id.rl_price_list:
                intent = new Intent(mContext, PriceActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    if (GlobalApplication.getInstance().getCanSeePrice()) {
                        startActivity(intent);
                    } else {
                        dialog.setTitle("提示");
                        dialog.setModel(CustomDialog.RIGHT);
                        dialog.setMessageGravity();
                        dialog.setMessage("您没有查看的权限");
                        dialog.setRightBtnListener("知道啦", null);
                        dialog.show();
                    }
                }
                break;
            //对账单
            case R.id.rl_bill:
                intent = new Intent(mContext, AccountsListActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    if (GlobalApplication.getInstance().getCanSeePrice()) {
                        startActivity(intent);
                    } else {
                        dialog.setTitle("提示");
                        dialog.setModel(CustomDialog.RIGHT);
                        dialog.setMessageGravity();
                        dialog.setMessage("您没有查看的权限");
                        dialog.setRightBtnListener("知道啦", null);
                        dialog.show();
                    }
                }
                break;
            case R.id.rl_procurement:
                refreshProcument();
                break;
            case R.id.ll_cai_gou_e:
                if (SPUtils.isLogin(getActivity())) {
                    intent = new Intent(mContext, ProcurementLimitActivity.class);
                    intent.putExtra(KEY_SUM_MONEY_DATA, sumMoneyData);
                    startActivity(intent);
                }
                break;
            case R.id.rl_transfer://门店调拨
                if (SPUtils.isLogin(getActivity())) {
                  refreshTransfer();
                }
        }

    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        setLoginStatus(userInfo);
        isLogin = true;
    }

    @Override
    public void onUserLoginout() {
        setLogoutStatus();
        isLogin = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUserInfo(UpdateUserInfo userLoginEvent) {
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        setLoginStatus(userInfo);
    }
}
