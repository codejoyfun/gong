package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Dong on 2017/10/13.
 */

public class TransferBatchResponse {

    private List<TransferBatchLine> lines;

    public List<TransferBatchLine> getLines() {
        return lines;
    }

    public void setLines(List<TransferBatchLine> lines) {
        this.lines = lines;
    }

    public static class TransferBatchLine implements Parcelable{
        private String productID;
        private int priceUnit;
        private List<TransferBatchLot> productInfo;

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public int getPriceUnit() {
            return priceUnit;
        }

        public List<TransferBatchLot> getProductInfo() {
            return productInfo;
        }

        public void setPriceUnit(int priceUnit) {
            this.priceUnit = priceUnit;
        }

        public void setProductInfo(List<TransferBatchLot> productInfo) {
            this.productInfo = productInfo;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.productID);
            dest.writeInt(this.priceUnit);
            dest.writeTypedList(this.productInfo);
        }

        public TransferBatchLine() {
        }

        protected TransferBatchLine(Parcel in) {
            this.productID = in.readString();
            this.priceUnit = in.readInt();
            this.productInfo = in.createTypedArrayList(TransferBatchLot.CREATOR);
        }

        public static final Creator<TransferBatchLine> CREATOR = new Creator<TransferBatchLine>() {
            @Override
            public TransferBatchLine createFromParcel(Parcel source) {
                return new TransferBatchLine(source);
            }

            @Override
            public TransferBatchLine[] newArray(int size) {
                return new TransferBatchLine[size];
            }
        };
    }

    public static class TransferBatchLot implements Parcelable{
        private String lotID;
        private int quantQty;

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.lotID);
            dest.writeInt(this.quantQty);
        }

        public TransferBatchLot() {
        }

        protected TransferBatchLot(Parcel in) {
            this.lotID = in.readString();
            this.quantQty = in.readInt();
        }

        public static final Creator<TransferBatchLot> CREATOR = new Creator<TransferBatchLot>() {
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
}
