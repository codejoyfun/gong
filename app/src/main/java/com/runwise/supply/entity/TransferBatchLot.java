package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 调拨单详情，商品信息下的批次信息
 *
 * Created by Dong on 2017/10/16.
 */

public class TransferBatchLot implements Parcelable{
    private String lotID;
    private int quantQty;
    private String lotDate;
    private int lotIDID;
    private float usedQty;

    //本地数据
    private int actualQty;

    public String getLotID() {
        return lotID;
    }

    public int getQuantQty() {
        return quantQty;
    }

    public void setLotID(String lotID) {
        this.lotID = lotID;
    }

    public void setQuantQty(int quantQty) {
        this.quantQty = quantQty;
    }

    public int getActualQty() {
        return actualQty;
    }

    public void setActualQty(int actualQty) {
        this.actualQty = actualQty;
    }

    public TransferBatchLot() {
    }

    public String getLotDate() {
        return lotDate;
    }

    public int getLotIDID() {
        return lotIDID;
    }

    public float getUsedQty() {
        return usedQty;
    }

    public void setLotDate(String lotDate) {
        this.lotDate = lotDate;
    }

    public void setLotIDID(int lotIDID) {
        this.lotIDID = lotIDID;
    }

    public void setUsedQty(float usedQty) {
        this.usedQty = usedQty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lotID);
        dest.writeInt(this.quantQty);
        dest.writeInt(this.actualQty);
        dest.writeFloat(this.usedQty);
        dest.writeString(this.lotDate);
        dest.writeInt(this.lotIDID);
    }

    protected TransferBatchLot(Parcel in) {
        this.lotID = in.readString();
        this.quantQty = in.readInt();
        this.actualQty = in.readInt();
        this.usedQty = in.readFloat();
        this.lotDate = in.readString();
        this.lotIDID = in.readInt();
    }

    public static final Creator<TransferBatchLot> CREATOR = new Parcelable.Creator<TransferBatchLot>() {
        @Override
        public TransferBatchLot createFromParcel(Parcel source) {
            return new TransferBatchLot(source);
        }

        @Override
        public TransferBatchLot[] newArray(int size) {
            return new TransferBatchLot[size];
        }
    };
}
