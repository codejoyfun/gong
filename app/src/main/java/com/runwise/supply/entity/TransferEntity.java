package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 * 调度单
 *
 * Created by Dong on 2017/10/10.
 */

public class TransferEntity implements Parcelable{

    //调拨单状态
    public static final String STATE_SUBMITTED = "已提交";//已提交
    public static final String STATE_PENDING_DELIVER = "待出库";//待出库
    public static final String STATE_DELIVER = "已发出";//已发出
    public static final String STATE_COMPLETE = "已完成";//完成
    public static final String STATE_CANCEL = "已取消";

    private String pickingID;
    private String pickingName;
    private String date;
    private String pickingState;
    private String locationName;
    private String locationDestName;
    private List<OrderResponse.ListBean.LinesBean> lines;
    private float totalPrice;
    private int totalNum;

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

    public int getTotalNum() {
        return totalNum;
    }

    public List<OrderResponse.ListBean.LinesBean> getLines() {
        return lines;
    }

    public void setPickingID(String pickingID) {
        this.pickingID = pickingID;
    }

    public void setPickingState(String pickingState) {
        this.pickingState = pickingState;
    }

    public void setLines(List<OrderResponse.ListBean.LinesBean> lines) {
        this.lines = lines;
    }

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

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
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
        dest.writeTypedList(this.lines);
        dest.writeFloat(this.totalPrice);
        dest.writeInt(this.totalNum);
    }

    public TransferEntity() {
    }

    protected TransferEntity(Parcel in) {
        this.pickingID = in.readString();
        this.pickingName = in.readString();
        this.date = in.readString();
        this.pickingState = in.readString();
        this.locationName = in.readString();
        this.locationDestName = in.readString();
        this.lines = in.createTypedArrayList(OrderResponse.ListBean.LinesBean.CREATOR);
        this.totalPrice = in.readFloat();
        this.totalNum = in.readInt();
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
