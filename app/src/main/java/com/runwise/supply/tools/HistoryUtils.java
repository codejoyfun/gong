package com.runwise.supply.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.kids.commonframe.base.util.CommonUtils;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by libin on 2017/2/9.
 */

public class HistoryUtils {
    private static final String SAVE_FILE_NAME = "history_spref";
    /**SharedPreferences*/
    private static SharedPreferences mSharedPreferences;
    /**Editor*/
    private static SharedPreferences.Editor mEditor;
    public static void updateDealer(Context context, String name, String did){
        mSharedPreferences = context.getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        /**如果要取得对应的值*/
        String nameIdStr = mSharedPreferences.getString("history", "");
        //需要写入的串
        StringBuffer oldSB = new StringBuffer();
        oldSB.append(name).append("=%=").append(did);
        if (!TextUtils.isEmpty(nameIdStr) && nameIdStr.contains(name+"=%="+did)){
            //存在，更新
            //存在该记录，删除掉，放入串头部
            String newReplaceStr = nameIdStr.replaceAll(oldSB.append("#@#").toString(),"");
            StringBuffer newContentStr = new StringBuffer(oldSB.append("#@#"));
            newContentStr.append(newReplaceStr);
            mEditor.putString("history",newContentStr.toString());
        }else{
            //首次插入
            StringBuffer newContentStr = new StringBuffer(oldSB);
            newContentStr.append("#@#");
            newContentStr.append(nameIdStr);
            mEditor.putString("history",newContentStr.toString());
        }
        mEditor.commit();

    }
    public static LinkedHashMap<String,String> getHistory(Context context){
        mSharedPreferences = context.getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        /**如果要取得对应的值*/
        String nameIdStr = mSharedPreferences.getString("history", "");
        LinkedHashMap<String,String> hashMap = new LinkedHashMap<String,String>();
        if(!TextUtils.isEmpty(nameIdStr) && nameIdStr.contains("#@#")){
            //逐项解析开，放入map中
            String[] nameIds = nameIdStr.split("#@#");
            for (int i = 0; i < nameIds.length; i++){
                //name和id通过=%=号连接
                if (nameIds[i].contains("=%=")){
                    String[] items = nameIds[i].split("=%=");
                    if (items.length > 0)
                        hashMap.put(items[0],items[1]);
                }
            }
        }
        return hashMap;
    }
    public static void clear(Context context){
        mSharedPreferences = context.getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }
}
