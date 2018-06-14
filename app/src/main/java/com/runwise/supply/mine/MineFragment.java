package com.runwise.supply.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.ProcurementActivity;
import com.runwise.supply.R;
import com.runwise.supply.TransferListActivity;
import com.runwise.supply.mine.entity.UpdateUserInfo;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.tools.RunwiseService;
import com.runwise.supply.tools.UserUtils;
import com.runwise.supply.view.ObservableScrollView;
import com.runwise.supply.view.SystemUpgradeLayout;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_USER_GUIDE;
import static com.runwise.supply.R.id.rl_procurement;

/**
 * Created by myChaoFile on 16/10/13.
 */

public class MineFragment extends NetWorkFragment {
    private final int REQUEST_SYSTEM = 1;
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
    String number = "02037574563";


    @ViewInject(R.id.orderRed)
    private View orderRed;

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
            userInfo = SampleApplicationLike.getInstance().loadUserInfo();
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
        }
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
        minePhone.setText("登录/注册");
        minePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPUtils.isLogin(mContext)) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLogin) {
            requestUserInfo();
        }
        MobclickAgent.onPageStart("更多页");
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
                SampleApplicationLike.getInstance().saveUserInfo(userInfo);//更新全局数据
                if (userInfo.isHasNewInvoice()) {
                    orderRed.setVisibility(View.VISIBLE);
                } else {
                    orderRed.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(userInfo.getIsZicai()) && userInfo.getIsZicai().equals("true")) {
                    findViewById(R.id.rl_procurement).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.rl_procurement).setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(userInfo.getIsShopTransfer()) && userInfo.getIsShopTransfer().equals("true")) {
                    findViewById(R.id.rl_transfer).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.rl_transfer).setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(userInfo.getIsSelfTransfer()) && userInfo.getIsSelfTransfer().equals("true")) {
                    findViewById(R.id.rl_transfer_out).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.rl_transfer_out).setVisibility(View.GONE);
                }

                SampleApplicationLike.getInstance().saveUserInfo(userInfo);
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
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @OnClick({R.id.rl_transfer_out,R.id.rl_user_guide, R.id.settingIcon, R.id.cellIcon, R.id.mineHead, R.id.itemLayout_1, R.id.itemLayout_3, R.id.itemLayout_4, R.id.rl_price_list, R.id.rl_bill, rl_procurement, R.id.rl_transfer})
    public void doClickHandler(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_transfer_out:
                //出库
                intent = new Intent(mContext, MineTransferoutActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
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
                intent = OrderListActivityV2.getStartIntent(0, getActivity());
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            //退货记录
            case R.id.itemLayout_3:
                intent = OrderListActivityV2.getStartIntent(1, getActivity());
                if (UserUtils.checkLogin(intent, mContext)) {
                    startActivity(intent);
                }
                break;
            //盘点记录
            case R.id.itemLayout_4:
                intent = new Intent(mContext, CheckActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    if (!ProductBasicUtils.isInit(getActivity())) {
                        return;
                    }
                    startActivity(intent);
                }
                break;
            //价目表
            case R.id.rl_price_list:
                intent = new Intent(mContext, PriceActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    if (SampleApplicationLike.getInstance().getCanSeePrice()) {
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
                    if (SampleApplicationLike.getInstance().getCanSeePrice()) {
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
//                自采
            case rl_procurement:
                if (SPUtils.isLogin(getActivity())) {
                    if (!ProductBasicUtils.isInit(getActivity())) {
                        return;
                    }
                    refreshProcument();
                }

                break;
            case R.id.rl_transfer://门店调拨
                if (SPUtils.isLogin(getActivity())) {
                    if (!ProductBasicUtils.isInit(getActivity())) {
                        return;
                    }
                    refreshTransfer();
                }
                break;
            case R.id.rl_user_guide:
                intent = new Intent(mContext, UserGuideActivity.class);
                if (UserUtils.checkLogin(intent, mContext)) {
                    MobclickAgent.onEvent(getActivity(), EVENT_ID_USER_GUIDE);
                    startActivity(intent);
                }

                break;
        }

    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        userInfo = SampleApplicationLike.getInstance().loadUserInfo();
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
        userInfo = SampleApplicationLike.getInstance().loadUserInfo();
        setLoginStatus(userInfo);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("更多页");
    }
}
