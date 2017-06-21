package com.kids.commonframe.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.lidroid.xutils.util.LogUtils;
import com.tencent.smtt.sdk.QbSdk;

/**
 * 加载x5core的基类
 */
public abstract class BaseWebView extends BaseActivity {
    private Handler handler;
    public static final int MSG_WEBVIEW_CONSTRUCTOR = 1;
    public static final int MSG_WEBVIEW_POLLING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch(msg.what){
                    case MSG_WEBVIEW_CONSTRUCTOR:
                        initView();
                        break;
                    case MSG_WEBVIEW_POLLING:
                        polling();//循环查询X5内核是否加载
                        break;
                }
                super.handleMessage(msg);
            }
        };
//        this.preinitX5WebCore();
        initView();
    }
    /**
     * X5内核在使用preinit接口之后，对于首次安装首次加载没有效果
     * 实际上，X5webview的preinit接口只是降低了webview的冷启动时间；
     * 因此，现阶段要想做到首次安装首次加载X5内核，必须要让X5内核提前获取到内核的加载条件
     */
    private void preinitX5WebCore() {
        if(!QbSdk.isTbsCoreInited()){//preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
            QbSdk.preInit(this, myCallback);//设置X5初始化完成的回调接口  第三个参数为true：如果首次加载失败则继续尝试加载；
        }else{
            handler.sendEmptyMessage(MSG_WEBVIEW_CONSTRUCTOR);
        }
    }

    /**
     * 需要不断检查网络反馈信息是否到达
     */
    private void polling() {
        if (QbSdk.isTbsCoreInited()){
            handler.sendEmptyMessage(MSG_WEBVIEW_CONSTRUCTOR);
        }else{
            handler.sendEmptyMessageDelayed(MSG_WEBVIEW_POLLING, 500);
        }
    }

    private QbSdk.PreInitCallback myCallback = new QbSdk.PreInitCallback() {
        @Override
        public void onCoreInitFinished() {
            LogUtils.e("内核已加载完毕");
        }

        @Override
        public void onViewInitFinished(boolean b) {
            polling();
        }
    };

    /**
     * 用x5内核的webview 用改方法初始化控件，不要写在onCreate中
     */
    public abstract void initView();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        try{
            super.onConfigurationChanged(newConfig);
            if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){

            }else if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
