package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.CheckVersionManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.StorageUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.ChangePwdActivity;
import com.runwise.supply.IWebViewActivity;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.UrlResult;
import com.runwise.supply.tools.ScoreUtils;
import com.runwise.supply.tools.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * 设置
 */

public class SettingActivity extends NetWorkActivity {
    @ViewInject(R.id.setItemName_4)
    private TextView setItemName_4;
    @ViewInject(R.id.setItemName_5)
    private TextView setItemName_5;

    private final int REQUEST_HELP = 1;

    private boolean isLogin;
    @ViewInject(R.id.exit_user)
    private View exitButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);

        setContentView(R.layout.activity_setting);

        this.setTitleLeftIcon(true, R.drawable.back_btn);
        this.setTitleText(true, "设置");

        File cacheDir = StorageUtils.getCacheDirectory(mContext);
        try {
            setItemName_4.setText(CommonUtils.formetFileSize(CommonUtils.getFileFolderSize(cacheDir)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        isLogin = SPUtils.isLogin(mContext);
        if (isLogin) {
            exitButton.setVisibility(View.VISIBLE);
        }
        else {
            exitButton.setVisibility(View.GONE);
        }
        setItemName_5.setText(CommonUtils.getVersionName(this));
    }



    @OnClick({R.id.setItemLayout_1,R.id.setItemLayout_2,R.id.setItemLayout_3,R.id.setItemLayout_4,R.id.setItemLayout_5,R.id.exit_user,R.id.left_layout})
    public void doClickHandler(View view) {
        Intent intent;
        switch (view.getId()) {
            //给我们的留言
            case R.id.setItemLayout_1:
                intent = new Intent(mContext,UserGuidActivity.class);
                startActivity(intent);
                break;
            case R.id.setItemLayout_2:
//                //检查版本
//                CheckVersionManager checkVersionManager = new CheckVersionManager(this);
//                checkVersionManager.checkVersion(true);
                break;
            //清除缓存
            case R.id.setItemLayout_4:
                showDialogue();
                break;
            //给我评分
            case R.id.setItemLayout_3:
                List<String> packList = ScoreUtils.InstalledAPPs(this);
                if(packList != null && !packList.isEmpty()) {
                    ScoreUtils.launchAppDetail(this,this.getPackageName(),packList.get(0));
                }
                else {
                    ScoreUtils.launchAppDetail(this,this.getPackageName(),"");
                }
                break;
            //关于我们
            case R.id.setItemLayout_5:
                intent = new Intent(mContext,AboutActivity.class);
                startActivity(intent);
                break;
            //返回
            case R.id.left_layout:
                this.finish();
                break;
            //退出登录
            case R.id.exit_user:
                showLogoutDialog();
                break;

        }

    }
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_HELP:
                UrlResult helperResult = (UrlResult)result;
                Intent intent = new Intent(mContext, IWebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_TITLE,"关于我们");
                intent.putExtra(WebViewActivity.WEB_URL,helperResult.getData().getEntity().getUrl_addr());
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private void showLogoutDialog() {
        final CustomBottomDialog mLogoutDialog = new CustomBottomDialog(this);
        ArrayMap<Integer, String> menus = new ArrayMap<Integer, String>();
        menus.put(0, "退出登录");
        mLogoutDialog.addItemViews(menus);
        mLogoutDialog.setOnBottomDialogClick(new CustomBottomDialog.OnBottomDialogClick() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()) {
                    case 0:
                        SPUtils.loginOut(mContext);
                        //退出登录
                        EventBus.getDefault().post(new UserLogoutEvent());
                        finish();
                        break;
                }
                mLogoutDialog.dismiss();
            }
        });
        mLogoutDialog.show();
    }

    private void showDialogue() {
        final CustomBottomDialog  mClearDialog = new CustomBottomDialog(this);
        ArrayMap<Integer, String> menus = new ArrayMap<Integer, String>();
        menus.put(0, "确认清除");
        mClearDialog.addItemViews(menus);
        mClearDialog.setOnBottomDialogClick(new CustomBottomDialog.OnBottomDialogClick() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()) {
                    case 0:
                        File cacheDir = StorageUtils.getCacheDirectory(mContext);
                        CommonUtils.deleteDir(cacheDir);
                        ToastUtil.show(mContext,"清除完成");
                        setItemName_4.setText("0M");
                        break;
                }
                mClearDialog.dismiss();
            }
        });
        mClearDialog.show();
    }

    /**
     * 验证登录
     *
     * @param targerIntent
     * @return
     */
    public boolean checkLogin(Intent targerIntent) {
        MobclickAgent.onEvent(mContext, "class_view_sum");
        if (!isLogin) {
            LoginActivity.targerIntent = targerIntent;
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            startActivity(loginIntent);
            return false;
        }
        return true;
    }
    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        super.onUserLogin(userLoginEvent);
        isLogin = true;
    }

    @Override
    public void onUserLoginout() {
        super.onUserLoginout();
        isLogin = false;
    }

}
