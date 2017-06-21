package com.runwise.supply;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.CheckVersionManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.business.NearFragment;
import com.runwise.supply.entity.UserInfo;
import com.runwise.supply.index.IndexFragment;
import com.runwise.supply.mine.MineFragment;

public class MainActivity extends NetWorkActivity {

    private long mExitTime;
    @ViewInject(android.R.id.tabhost)
    private FragmentTabHost mTabHost;
    //未读小红点
    private TextView mMsgHite;
    private UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        setContentView(R.layout.activity_main);
        requestPermissions();
        initTabView();
        //检查版本
        CheckVersionManager checkVersionManager = new CheckVersionManager(this);
//        checkVersionManager.checkVersion(false);

    }


    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
//                ToastUtil.show(mContext,"一些必要的权限被禁用");
//                dialog.setMsg("一些必要的权限被禁用,可能影响您的使用");
//                dialog.setModel(CustomDialog.RIGHT);
//                dialog.setRightBtnListener("知道啦",null);
//                dialog.show();
//                Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTabView() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(createTabSpace(R.drawable.tab_1_selector, R.string.tab_1), IndexFragment.class, null);
        mTabHost.addTab(createTabSpace(R.drawable.tab_2_selector, R.string.tab_2), NearFragment.class, null);
        mTabHost.addTab(createTabSpace(R.drawable.tab_4_selector, R.string.tab_3), MineFragment.class, null);
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    private TabHost.TabSpec createTabSpace(int bgRes, int tabNameRes) {
        if (mTabHost != null) {
            TabHost.TabSpec subTab = mTabHost.newTabSpec(getString(tabNameRes));
            View subTabView = LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
            ImageView tabIv = (ImageView) subTabView.findViewById(R.id.tab_iv_icon);
            TextView tabTv = (TextView) subTabView.findViewById(R.id.tab_tv_name);
            mMsgHite = (TextView) subTabView.findViewById(R.id.tv_hint);
            tabIv.setImageResource(bgRes);
            tabTv.setText(getString(tabNameRes));
            subTab.setIndicator(subTabView);
            return subTab;
        }
        return null;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtil.show(this, "再按一次退出程序");
            mExitTime = System.currentTimeMillis();
        }
        else {
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        userInfo = GlobalApplication.getInstance().loadUserInfo();
    }

    @Override
    public void onUserLoginout() {
    }
}
