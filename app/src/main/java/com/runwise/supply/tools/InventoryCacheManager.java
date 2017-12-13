package com.runwise.supply.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.kids.commonframe.base.util.SPUtils;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.orderpage.TempOrderManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 缓存盘点单信息
 *
 * Created by Dong on 2017/12/9.
 */

public class InventoryCacheManager {

    private static final String PREF_NAME = "inventory2_";
    private static final String PREF_BRIEF = "inventory_list_items2";
    private SharedPreferences mInventoryPrefs;
    private SharedPreferences mListPrefs;

    public InventoryCacheManager(Context context){
        mInventoryPrefs = context.getSharedPreferences(PREF_NAME,0);
        mListPrefs = context.getSharedPreferences(PREF_BRIEF,0);
    }

    /**
     * 缓存盘点信息
     * @param inventoryBean
     */
    public void saveInventory(InventoryResponse.InventoryBean inventoryBean){
        SPUtils.saveObject(mInventoryPrefs,String.valueOf(inventoryBean.getInventoryID()),inventoryBean);
        //保存一份简要的信息用于首页显示
        InventoryBrief inventoryBrief = new InventoryBrief();
        inventoryBrief.setInventoryID(inventoryBean.getInventoryID());
        inventoryBrief.setCreateUser(GlobalApplication.getInstance().getUserName());
        inventoryBrief.setCreateTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        inventoryBrief.setName(inventoryBean.getName());
        SPUtils.saveObject(mListPrefs,String.valueOf(inventoryBrief.getInventoryID()),inventoryBrief);
    }

    /**
     * 加载缓存的盘点信息
     * @param inventoryID
     * @return
     */
    public InventoryResponse.InventoryBean loadInventory(int inventoryID){
        return (InventoryResponse.InventoryBean)SPUtils.readObject(mInventoryPrefs,String.valueOf(inventoryID));
    }

    /**
     * 删除缓存的盘点信息
     * @param inventoryID
     */
    public void removeInventory(int inventoryID){
        mInventoryPrefs.edit().remove(String.valueOf(inventoryID)).apply();//删掉详情
        mListPrefs.edit().remove(String.valueOf(inventoryID)).apply();//删掉列表项
    }

    /**
     * 返回所有的缓存的盘点单的列表
     * @return
     */
    public List<InventoryBrief> loadInventoryBrief(){
        List<InventoryBrief> dataList = new ArrayList<>();
        try {
            Map<String,?> mapAll = mListPrefs.getAll();
            for(Map.Entry entry:mapAll.entrySet()){
                String string = (String)entry.getValue();
                if (TextUtils.isEmpty(string)) {
                    continue;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = SPUtils.StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    dataList.add((InventoryBrief) readObject);
                }
            }

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
     * 首页的盘点信息
     */
    public static class InventoryBrief implements Serializable{
        private int inventoryID;
        private String createTime;
        private String createUser;
        private String name;

        public int getInventoryID() {
            return inventoryID;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setInventoryID(int inventoryID) {
            this.inventoryID = inventoryID;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
