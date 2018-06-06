package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 异步下单，在本地保存提交中的订单信息
 * 按用户保存
 *
 * Created by Dong on 2017/12/1.
 */

public class TempOrderManager {
    private static TempOrderManager sInstance;
    private static final String PREF_NAME_PREFIX = "temp_orders4_";
    private SharedPreferences mPrefs;

    public static TempOrderManager getInstance(Context context){
        if(sInstance==null){
            sInstance = new TempOrderManager(context);
        }
        return sInstance;
    }

    private TempOrderManager(Context context){
        String prefName = PREF_NAME_PREFIX + SampleApplicationLike.getInstance().getUid();//按用户保存
        mPrefs = context.getSharedPreferences(prefName,0);
    }

    /**
     * 异步保存订单信息
     */
    public void saveTempOrderAsync(TempOrder order){
        Observable.fromCallable(()-> saveTempOrder(order))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 保存订单信息
     * @param order
     * @return
     */
    public Void saveTempOrder(TempOrder order){
        SPUtils.saveObject(mPrefs,order.getHashKey(),order);
        return null;
    }

    /**
     * 获取提交中的订单列表
     *
     * @return
     */
    public List<TempOrder> getTempOrders(){
        List<TempOrder> dataList = new ArrayList<>();
        try {
            Map<String,?> mapAll = mPrefs.getAll();
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
                    dataList.add((TempOrder) readObject);
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
     * 异步获取所有提交中订单
     * @param callback
     */
    public void getTempOrdersAsync(IGetTempOrdersCallback callback){
        Observable.fromCallable(this::getTempOrders)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onGetTempOrders);
    }

    /**
     * 从本地记录中删除对应的订单
     */
    public void removeTempOrder(TempOrder tempOrder){
        mPrefs.edit().remove(tempOrder.getHashKey()).apply();
    }

    public interface IGetTempOrdersCallback{
        void onGetTempOrders(List<TempOrder> order);
    }

    /**
     * 提交中的订单
     */
    public static class TempOrder implements Serializable,Parcelable{
        private String estimateDate;
        private String hashKey;//标记提交中的订单，由本地生成
        private boolean isFailed;//是否提交失败，后台返回
        private double totalMoney;
        private double totalPieces;
        private String firstLineName;

        public String getFirstLineName() {
            return firstLineName;
        }

        public void setFirstLineName(String firstLineName) {
            this.firstLineName = firstLineName;
        }

        public int getLinesAmount() {
            return linesAmount;
        }

        public void setLinesAmount(int linesAmount) {
            this.linesAmount = linesAmount;
        }

        private int linesAmount;
        private ArrayList<ProductBasicList.ListBean> productList;

        public String getEstimateDate() {
            return estimateDate;
        }

        public String getHashKey() {
            return hashKey;
        }

        public ArrayList<ProductBasicList.ListBean> getProductList() {
            return productList;
        }

        public void setEstimateDate(String estimateDate) {
            this.estimateDate = estimateDate;
        }

        public void setHashKey(String hashKey) {
            this.hashKey = hashKey;
        }

        public void setProductList(ArrayList<ProductBasicList.ListBean> productList) {
            this.productList = productList;
            totalMoney = 0;
            totalPieces = 0;
            if(productList!=null){
                for(ProductBasicList.ListBean bean:productList){
                    totalMoney = totalMoney + bean.getActualQty() * bean.getPrice();
                    totalPieces = totalPieces + bean.getActualQty();
                }
            }
        }

        public boolean isFailed() {
            return isFailed;
        }

        public void setFailed(boolean failed) {
            isFailed = failed;
        }

        public double getTotalMoney() {
            return totalMoney;
        }

        public double getTotalPieces() {
            return totalPieces;
        }

        public void setTotalMoney(double totalMoney) {
            this.totalMoney = totalMoney;
        }

        public void setTotalPieces(int totalPieces) {
            this.totalPieces = totalPieces;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.estimateDate);
            dest.writeString(this.hashKey);
            dest.writeByte(this.isFailed ? (byte) 1 : (byte) 0);
            dest.writeDouble(this.totalMoney);
            dest.writeDouble(this.totalPieces);
            dest.writeTypedList(this.productList);
        }

        public TempOrder() {
        }

        protected TempOrder(Parcel in) {
            this.estimateDate = in.readString();
            this.hashKey = in.readString();
            this.isFailed = in.readByte() != 0;
            this.totalMoney = in.readDouble();
            this.totalPieces = in.readDouble();
            this.productList = in.createTypedArrayList(ProductBasicList.ListBean.CREATOR);
        }

        public static final Creator<TempOrder> CREATOR = new Creator<TempOrder>() {
            @Override
            public TempOrder createFromParcel(Parcel source) {
                return new TempOrder(source);
            }

            @Override
            public TempOrder[] newArray(int size) {
                return new TempOrder[size];
            }
        };
    }

    /**
     * 用户登出的时候清除instance
     * @param userLogoutEvent
     */
    @Subscribe
    public void destroy(UserLogoutEvent userLogoutEvent){
        sInstance = null;
    }
}
