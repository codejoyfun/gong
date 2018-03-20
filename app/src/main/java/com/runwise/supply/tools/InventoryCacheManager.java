package com.runwise.supply.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.kids.commonframe.base.util.SPUtils;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.entity.ShowInventoryNoticeEvent;

import org.greenrobot.eventbus.EventBus;

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
 * <p>
 * Created by Dong on 2017/12/9.
 */

public class InventoryCacheManager {

    private static final String PREF_NAME = "inventory2_";
    private static final String PREF_BRIEF = "inventory_list_items2";
    private static final String PREF_KEY_IS_INVENTORY = "is_inventory";
    private static final String PREF_KEY_CURRENT_INVENTORY_ID = "pref_key_current_inventory_id";
    private static final int INVAILD_INVENTORY_ID = -1;
    private SharedPreferences mInventoryPrefs;
    private SharedPreferences mListPrefs;
    private SharedPreferences mPrefs;

    private InventoryCacheManager(Context context) {
        mInventoryPrefs = context.getSharedPreferences(PREF_NAME, 0);
        mListPrefs = context.getSharedPreferences(PREF_BRIEF, 0);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static InventoryCacheManager sInstance;

    public static InventoryCacheManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new InventoryCacheManager(context);
        }
        return sInstance;
    }

    /**
     * 缓存盘点信息
     *
     * @param inventoryBean
     */
    public void saveInventory(InventoryResponse.InventoryBean inventoryBean) {
        SPUtils.saveObject(mInventoryPrefs, String.valueOf(inventoryBean.getInventoryID()), inventoryBean);
        //保存一份简要的信息用于首页显示
        InventoryBrief inventoryBrief = new InventoryBrief();
        inventoryBrief.setInventoryID(inventoryBean.getInventoryID());
        inventoryBrief.setCreateUser(SampleApplicationLike.getInstance().getUserName());
        inventoryBrief.setCreateTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        inventoryBrief.setName(inventoryBean.getName());
        SPUtils.saveObject(mListPrefs, String.valueOf(inventoryBrief.getInventoryID()), inventoryBrief);
    }

    /**
     * 加载缓存的盘点信息
     *
     * @param inventoryID
     * @return
     */
    public InventoryResponse.InventoryBean loadInventory(int inventoryID) {
        return (InventoryResponse.InventoryBean) SPUtils.readObject(mInventoryPrefs, String.valueOf(inventoryID));
    }

    /**
     * 删除缓存的盘点信息
     *
     * @param inventoryID
     */
    public void removeInventory(int inventoryID) {
        mInventoryPrefs.edit().remove(String.valueOf(inventoryID)).apply();//删掉详情
        mListPrefs.edit().remove(String.valueOf(inventoryID)).apply();//删掉列表项
    }

    /**
     * 返回所有的缓存的盘点单的列表
     *
     * @return
     */
    public List<InventoryBrief> loadInventoryBrief() {
        List<InventoryBrief> dataList = new ArrayList<>();
        try {
            Map<String, ?> mapAll = mListPrefs.getAll();
            for (Map.Entry entry : mapAll.entrySet()) {
                String string = (String) entry.getValue();
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

    public void setCurrentInventoryId(int inventoryId) {
        mPrefs.edit().putInt(PREF_KEY_CURRENT_INVENTORY_ID, inventoryId).apply();
    }

    public int getCurrentInventoryId() {
        return mPrefs.getInt(PREF_KEY_CURRENT_INVENTORY_ID, INVAILD_INVENTORY_ID);
    }

    /**
     * 设置是否盘点中
     *
     * @param isInventory
     */
    public void setIsInventory(boolean isInventory) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_INVENTORY, isInventory).apply();
    }

    /**
     * 获取是否盘点中
     *
     * @return
     */
    public boolean isInventoryInProgress() {
        return mPrefs.getBoolean(PREF_KEY_IS_INVENTORY, false);
    }

    /**
     * 获取是否盘点中,并弹提示
     *
     * @return
     */
    public boolean checkIsInventory(Context context) {
        if (isInventoryInProgress()) {
            int inventoryId = getCurrentInventoryId();
            if (inventoryId != INVAILD_INVENTORY_ID) {
                InventoryResponse.InventoryBean inventoryBean = getInstance(context).loadInventory(inventoryId);
                Toast.makeText(context, inventoryBean.getCreateUser() + "正在盘点中，请先完成或取消！", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return false;
    }

    boolean shouldShow = false;

    public void shouldShowInventoryInProgress(boolean shouldShow) {
        EventBus.getDefault().post(new ShowInventoryNoticeEvent(shouldShow));
        this.shouldShow = shouldShow;
    }

    public boolean shouldShowInventoryInProgress() {
        return shouldShow;
    }

    /**
     * 首页的盘点信息
     */
    public static class InventoryBrief implements Serializable {
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
