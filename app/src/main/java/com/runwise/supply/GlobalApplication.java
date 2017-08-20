package com.runwise.supply;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.img.ImagePipelineConfigFactory;
import com.liulishuo.filedownloader.FileDownloader;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by myChaoFile on 16/10/13.
 */

public class GlobalApplication extends MultiDexApplication {
    private static GlobalApplication instance;
    /**
     * 当前用户信息
     */
    private UserInfo mUserInfo;
    private String uid;
    private String userName;
    private boolean canSeePrice;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //初始化facebook
        Fresco.initialize(this , ImagePipelineConfigFactory.getImagePipelineConfig(this));
        //下载器初始化
        FileDownloader.init(this);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
    public static GlobalApplication getInstance() {
        return instance;
    }
    /**
     * 获取用户信息
     */
    public UserInfo loadUserInfo() {
         mUserInfo = (UserInfo) SPUtils.readObject(this,"userInfo");
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
        SPUtils.saveObject( this , "userInfo", userInfo);
    }

}
