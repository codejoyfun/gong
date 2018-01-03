package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 调拨单
 *
 * Created by Dong on 2017/10/10.
 */

public class TransferEntity implements Parcelable{
    public static final int STATE_SUBMIT = 0;//提交状态
    public static final int STATE_OUT = 1;//发出状态
    public static final int STATE_FINISH = 2;//完成状态

    private String pickingID;
    private String pickingName;
    private String date;
    private String pickingState;
    private String pickingStateFull;
    private String locationName;
    private String locationDestName;
    private List<String> stateTracker;
    private float totalPrice;//根据当前状态的数量，如提交则是订单数，已出库则是出库数，已入库则是实际完成数
    private double totalNum;//same as totalPrice
    private int pickingStateNum;
    private boolean isConfirmed;//调出方是否已经点过“出库按钮”，即后台confirm接口是否已经被调用

    public int getPickingStateNum() {
        return pickingStateNum;
    }

    public void setPickingStateNum(int pickingStateNum) {
        this.pickingStateNum = pickingStateNum;
    }

    public String getPickingID() {
        return pickingID;
    }

    public String getPickingState() {
        return pickingState;
    }

    public String getPickingName() {
        return pickingName;
    }

    public String getDate() {
        return date;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationDestName() {
        return locationDestName;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public double getTotalNum() {
        return totalNum;
    }

//    public List<TransferDetailResponse.LinesBean> getLines() {
//        return lines;
//    }

    public void setPickingID(String pickingID) {
        this.pickingID = pickingID;
    }

    public void setPickingState(String pickingState) {
        this.pickingState = pickingState;
    }

//    public void setLines(List<TransferDetailResponse.LinesBean> lines) {
//        this.lines = lines;
//    }

    public void setPickingName(String pickingName) {
        this.pickingName = pickingName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationDestName(String locationDestName) {
        this.locationDestName = locationDestName;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalNum(double totalNum) {
        this.totalNum = totalNum;
    }

    public List<String> getStateTracker() {
        return stateTracker;
    }

    public void setStateTracker(List<String> stateTracker) {
        this.stateTracker = stateTracker;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getPickingStateFull() {
        return pickingStateFull;
    }

    public void setPickingStateFull(String pickingStateFull) {
        this.pickingStateFull = pickingStateFull;
    }

    public TransferEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pickingID);
        dest.writeString(this.pickingName);
        dest.writeString(this.date);
        dest.writeString(this.pickingState);
        dest.writeString(this.locationName);
        dest.writeString(this.locationDestName);
//        dest.writeTypedList(this.lines);
        dest.writeStringList(this.stateTracker);
        dest.writeFloat(this.totalPrice);
        dest.writeDouble(this.totalNum);
        dest.writeInt(isConfirmed?1:0);
        dest.writeString(this.pickingStateFull);
    }

    protected TransferEntity(Parcel in) {
        this.pickingID = in.readString();
        this.pickingName = in.readString();
        this.date = in.readString();
        this.pickingState = in.readString();
        this.locationName = in.readString();
        this.locationDestName = in.readString();
//        this.lines = in.createTypedArrayList(TransferDetailResponse.LinesBean.CREATOR);
        this.stateTracker = in.createStringArrayList();
        this.totalPrice = in.readFloat();
        this.totalNum = in.readDouble();
        this.isConfirmed = in.readInt()==1;
        this.pickingStateFull = in.readString();
    }

    public static final Creator<TransferEntity> CREATOR = new Creator<TransferEntity>() {
        @Override
        public TransferEntity createFromParcel(Parcel source) {
            return new TransferEntity(source);
        }

        @Override
        public TransferEntity[] newArray(int size) {
            return new TransferEntity[size];
        }
    };
}
