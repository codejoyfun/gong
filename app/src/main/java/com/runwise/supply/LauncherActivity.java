package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.SPUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LauncherActivity extends BaseActivity {
    @ViewInject(R.id.launcher_bg)
    private ImageView launcherBg;
    private Handler handler = new Handler();
    private Runnable mainRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        setContentView(R.layout.activity_launcher);
        mainRunnable = new Runnable() {
            @Override
            public void run() {
                doFowardHandler();
            }
        };
        handler.postDelayed(mainRunnable,2000);
    }

    private void doFowardHandler() {
        //-----------------------登录相关
        if(SPUtils.firstLaunch(this)) {
            startActivity(new Intent(this,NavigationActivity.class));
        }
        else {
//            if (SPUtils.isLogin(mContext)) {
//                startActivity(new Intent(mContext, MainActivity.class));
//            } else {
                startActivity(new Intent(mContext, MainActivity.class));
//            }
        }
        finish();
    }
}
