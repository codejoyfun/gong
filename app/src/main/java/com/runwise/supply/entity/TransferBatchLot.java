package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Denzel on 2017/10/16.
 */

public class TransferBatchLot implements Parcelable{
    private String lotID;
    private int quantQty;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lotID);
        dest.writeInt(this.quantQty);
        dest.writeInt(this.actualQty);
    }

    protected TransferBatchLot(Parcel in) {
        this.lotID = in.readString();
        this.quantQty = in.readInt();
        this.actualQty = in.readInt();
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