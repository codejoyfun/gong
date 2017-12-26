package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
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
import com.runwise.supply.InfoActivity;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.UrlResult;
import com.runwise.supply.tools.FingerprintHelper;
import com.runwise.supply.tools.ScoreUtils;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.SystemUpgradeHelper;
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
    @ViewInject(R.id.rl_config_fingerprint)
    private RelativeLayout mRlFingerprint;
    @ViewInject(R.id.sc_config_fingerprint)
    private SwitchCompat mScFingerprint;

    private final int REQUEST_HELP = 1;

    private boolean isLogin;
    private FingerprintHelper mFingerprintHelper;

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

        setItemName_5.setText(CommonUtils.getVersionName(this));

        mFingerprintHelper = new FingerprintHelper(this, FingerprintManagerCompat.from(this));
        if(mFingerprintHelper.isSupported()){
            mRlFingerprint.setVisibility(View.GONE);
        }else{
            mScFingerprint.setChecked(FingerprintHelper.isFingerprintEnabled(this));
            mScFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        FingerprintDialog fragment = new FingerprintDialog();
                        fragment.setFingerprintHelper(new FingerprintHelper(SettingActivity.this,
                                FingerprintManagerCompat.from(SettingActivity.this)));
                        fragment.setCallback(new FingerprintManagerCompat.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                                fragment.dismiss();
                                FingerprintHelper.setFingerprintEnabled(getActivityContext(),true);
                            }
                        });
                        fragment.show(getSupportFragmentManager(),"tag");
                    }else{
                        FingerprintHelper.setFingerprintEnabled(getActivityContext(),false);
                    }
                }
            });
        }
    }



    @OnClick({R.id.setItemLayout_1,R.id.setItemLayout_2,R.id.setItemLayout_3,R.id.setItemLayout_4,R.id.setItemLayout_5,R.id.setItemLayout_6,R.id.exit_user,R.id.left_layout})
    public void doClickHandler(View view) {
        Intent intent;
        switch (view.getId()) {
            //给我们的留言
            case R.id.setItemLayout_1:
                intent = new Intent(mContext,UserGuideActivity.class);
                startActivity(intent);
                break;
            case R.id.setItemLayout_2:
                //推送设置
                intent = new Intent(mContext,NotiySettingActivity.class);
                startActivity(intent);
                break;
            //清除缓存
            case R.id.setItemLayout_4:
                showDialogue();
                break;
            //给我评分
            case R.id.setItemLayout_3:
//                List<String> packList = ScoreUtils.InstalledAPPs(this);
//                if(packList != null && !packList.isEmpty()) {
//                    ScoreUtils.launchAppDetail(this,this.getPackageName(),packList.get(0));
//                }
//                else {
//                    ScoreUtils.launchAppDetail(this,this.getPackageName(),"");
//                }
                if(!SystemUpgradeHelper.getInstance(this).check(this))return;
                intent = new Intent(mContext,PReceiveMsgActivity.class);
                startActivity(intent);
                break;
            //关于我们
            case R.id.setItemLayout_5:
                intent = new Intent(mContext,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.setItemLayout_6:
                intent = new Intent(mContext,InfoActivity.class);
                intent.putExtra("hide",true);
                startActivity(intent);
                break;
            //返回
            case R.id.left_layout:
                this.finish();
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
                        Fresco.getImagePipeline().clearCaches();
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
