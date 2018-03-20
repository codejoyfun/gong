package com.runwise.supply;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.LogoutFromJpushEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.SelfOrderTimeStatisticsUtil;
import com.kids.commonframe.base.util.img.ImagePipelineConfigFactory;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.liulishuo.filedownloader.FileDownloader;
import com.runwise.supply.message.MessageFragment;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.MyDbUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.umeng.commonsdk.UMConfigure;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jpush.android.api.JPushInterface;

import static com.runwise.supply.MainActivity.INTENT_KEY_SKIP_TO_LOGIN;

/**
 * Created by mike on 2018/3/19.
 */

public class SampleApplicationLike extends DefaultApplicationLike {
    private static SampleApplicationLike instance;
    /**
     * 当前用户信息
     */
    private UserInfo mUserInfo;
    private String uid;
    private String userName;
    private boolean canSeePrice;



    public SampleApplicationLike(Application application, int tinkerFlags,
                                 boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                                 long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(getApplication(), "c61512815b", false);
        instance = this;
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //初始化facebook
        Fresco.initialize(getApplication() , ImagePipelineConfigFactory.getImagePipelineConfig(getApplication()));
        //下载器初始化
        FileDownloader.init(getApplication());
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplication());
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        UMConfigure.init(getApplication(), UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    public static SampleApplicationLike getInstance() {
        return instance;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutFromJpushEvent logoutFromJpushEvent){
        SPUtils.loginOut(getApplication());
        DbUtils dbUtils = MyDbUtil.create(getApplication());
        try {
            dbUtils.deleteAll(ProductBasicList.ListBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        SelfOrderTimeStatisticsUtil.clear();
        MessageFragment.isLogin = false;
        cleanUesrInfo();
        //退出登录
        EventBus.getDefault().post(new UserLogoutEvent());
        Intent intent = new Intent(getApplication(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_KEY_SKIP_TO_LOGIN,true);
        getApplication().startActivity(intent);
    }
    /**
     * 获取用户信息
     */
    public UserInfo loadUserInfo() {
        mUserInfo = (UserInfo) SPUtils.readObject(getApplication(),"userInfo");
        if (mUserInfo != null) {
            uid = mUserInfo.getUid();
            userName  = mUserInfo.getUsername();
            canSeePrice = mUserInfo.isCanSeePrice();
        }
//         else {
//           SPUtils.loginOut (instance);
//         }
        return  mUserInfo;
    }

    /**
     * 获取用户id
     */
    public String getUid() {
        if (TextUtils.isEmpty(uid)) {
            loadUserInfo();
        }
        return uid;
    }
    public String getUserName(){
        if (TextUtils.isEmpty(userName)){
            loadUserInfo();
        }
        return userName;
    }
    /**
     * 获取用户能否看价格
     *
     */
    public boolean getCanSeePrice(){
        if(mUserInfo == null) {
            loadUserInfo();
        }
        if(mUserInfo == null) {
            return true;
        }
        return mUserInfo.isCanSeePrice();
    }

    public void cleanUesrInfo() {
        mUserInfo = null;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    /**
     * 保存用户信息
     * @param userInfo
     */
    public void saveUserInfo(UserInfo userInfo) {
        if(userInfo != null) {
            this.mUserInfo = userInfo;
        }
        SPUtils.saveObject(getApplication(), "userInfo", userInfo);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

}

