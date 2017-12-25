package com.runwise.supply.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.kids.commonframe.base.util.SPUtils;
import com.lidroid.xutils.DbUtils;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.message.entity.DetailResult;
import com.runwise.supply.message.entity.MessageResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 保存推送平台通知的类
 *
 * Created by Dong on 2017/12/22.
 */

public class PlatformNotificationManager {
    private static final String PREF_NAME = "platform_notification3_";
    private static final String KEY_LATEST = "latest";
    private static PlatformNotificationManager sInstance;
    private SharedPreferences mPrefs;
    private SharedPreferences mDefaultPrefs;
//    private DbUtils mDbUtils;
    private DetailResult.ListBean mCacheLatest;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

    public static synchronized PlatformNotificationManager getInstance(Context context){
        if(sInstance==null){
            sInstance = new PlatformNotificationManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public PlatformNotificationManager(Context context){
        String uid = GlobalApplication.getInstance().getUid();
        mPrefs = context.getSharedPreferences(PREF_NAME+uid,0);//针对不同用户保存
        mDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 获取最新的一条message
     * @return
     */
    public DetailResult.ListBean getLastMessage(){
        String latestId = mDefaultPrefs.getString(KEY_LATEST,"");
        if(TextUtils.isEmpty(latestId))return null;
        mCacheLatest = (DetailResult.ListBean)SPUtils.readObject(mPrefs,latestId);
        return mCacheLatest;
    }

    public MessageResult.ChannelBean.LastMessageBeanX getLastMessageX(){
        DetailResult.ListBean lastListBean = getLastMessage();
        if(lastListBean==null)return null;
        MessageResult.ChannelBean.LastMessageBeanX lastMessageBeanX = new MessageResult.ChannelBean.LastMessageBeanX();
        lastMessageBeanX.setBody(lastListBean.getBody());
        lastMessageBeanX.setDate(lastListBean.getDate());
        lastMessageBeanX.setSeen(lastListBean.isSeen());

        return lastMessageBeanX;
    }

    /**
     * 获取历史消息列表
     * @return
     */
    public List<DetailResult.ListBean> getMsgList(){
        List<DetailResult.ListBean> dataList = new ArrayList<>();
        try {
            Map<String,?> mapAll = mPrefs.getAll();
            for(Map.Entry entry:mapAll.entrySet()){
                String string = (String)entry.getValue();
                if (TextUtils.isEmpty(string) || KEY_LATEST.equals(entry.getKey())) {//跳过用来保存最新的那条
                    continue;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = SPUtils.StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    dataList.add((DetailResult.ListBean) readObject);
                }
            }
            Collections.sort(dataList,(r1,r2)->r1.getDate().compareTo(r2.getDate()));//按时间排序

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 保存
     * @param id
     * @param message
     * @param date
     */
    public void addMsg(long id,String message,String date){
        DetailResult.ListBean bean = new DetailResult.ListBean();
        bean.setId(id);
        bean.setBody(message);
        bean.setDate(sdf.format(new Date()));
        SPUtils.saveObject(mPrefs,String.valueOf(id),bean);
        mDefaultPrefs.edit().putString(KEY_LATEST,String.valueOf(id)).apply();
    }

    public void setMessage(DetailResult.ListBean bean){
        SPUtils.saveObject(mPrefs,String.valueOf(bean.getId()),bean);
    }

    public void setLatestRead(){
        if(mCacheLatest!=null)mCacheLatest.setSeen(true);
        setMessage(mCacheLatest);
    }
}
