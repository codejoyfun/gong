package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.SPUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.tools.RunwiseService;

import java.net.URL;
import java.net.URLConnection;

import static com.kids.commonframe.base.util.net.NetWorkHelper.setResponseTime;

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
       new Thread(new Runnable() {
           @Override
           public void run() {
               getServerDate();
           }
       }).start();
        // 像启动 Service 那样启动 IntentService
        Intent startIntent = new Intent(getActivityContext(), RunwiseService.class);
        startService(startIntent);
    }

   private void  getServerDate(){
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            uc.getDate(); //取得网站日期时间
            setResponseTime(uc.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
