package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 *  调拨单详情
 *
 * Created by Dong on 2017/10/12.
 */

public class TransferDetailResponse {
    private List<LinesBean> lines;
    private TransferEntity info;

    public void setLines(List<LinesBean> lines) {
        this.lines = lines;
    }

    public List<LinesBean> getLines() {
        return lines;
    }

    public TransferEntity getInfo() {
        return info;
    }

    public void setInfo(TransferEntity info) {
        this.info = info;
    }

    public static class LinesBean implements Parcelable{
        public static final String TRACKING_LOT = "lot";
        private int productID;
        private int priceUnit;
        private double actualOutputNum;
        private String productName;
        private String productCode;
        private String productImage;
        private double productQtyDone;
        private String productTracking;
        private String productUnit;
        private String productUom;
        private double productUomQty;
        private List<TransferBatchLot> productLotInfo;

        public List<TransferBatchLot> getProductLotInfo() {
            return productLotInfo;
        }

        public void setProductLotInfo(List<TransferBatchLot> productLotInfo) {
            this.productLotInfo = productLotInfo;
        }

        public String getProductTracking() {
            return productTracking;
        }

        public void setProductTracking(String productTracking) {
            this.productTracking = productTracking;
        }

        public int getProductID() {
            return productID;
        }

        public int getPriceUnit() {
            return priceUnit;
        }

        public double getActualOutputNum() {
            return actualOutputNum;
        }

        public String getProductName() {
            return productName;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getProductImage() {
            return productImage;
        }

        public double getProductQtyDone() {
            return productQtyDone;
        }

        public String getProductUnit() {
            return productUnit;
        }

        public String getProductUom() {
            return productUom;
        }

        public double getProductUomQty() {
            return productUomQty;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public void setPriceUnit(int priceUnit) {
            this.priceUnit = priceUnit;
        }

        public void setActualOutputNum(double actualOutputNum) {
            this.actualOutputNum = actualOutputNum;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        public void setProductQtyDone(double productQtyDone) {
            this.productQtyDone = productQtyDone;
        }

        public void setProductUnit(String productUnit) {
            this.productUnit = productUnit;
        }

        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }

        public void setProductUomQty(double productUomQty) {
            this.productUomQty = productUomQty;
        }

        public boolean isLotTracking(){
            return TRACKING_LOT.equals(productTracking);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.productID);
            dest.writeInt(this.priceUnit);
            dest.writeDouble(this.actualOutputNum);
            dest.writeString(this.productName);
            dest.writeString(this.productCode);
            dest.writeString(this.productImage);
            dest.writeDouble(this.productQtyDone);
            dest.writeString(this.productTracking);
            dest.writeString(this.productUnit);
            dest.writeString(this.productUom);
            dest.writeDouble(this.productUomQty);
            dest.writeTypedList(this.productLotInfo);
        }

        public LinesBean() {
        }

        protected LinesBean(Parcel in) {
            this.productID = in.readInt();
            this.priceUnit = in.readInt();
            this.actualOutputNum = in.readDouble();
            this.productName = in.readString();
            this.productCode = in.readString();
            this.productImage = in.readString();
            this.productQtyDone = in.readDouble();
            this.productTracking = in.readString();
            this.productUnit = in.readString();
            this.productUom = in.readString();
            this.productUomQty = in.readDouble();
            this.productLotInfo = in.createTypedArrayList(TransferBatchLot.CREATOR);
        }

        public static final Creator<LinesBean> CREATOR = new Creator<LinesBean>() {
            @Override
            public LinesBean createFromParcel(Parcel source) {
                return new LinesBean(source);
            }

            @Override
            public LinesBean[] newArray(int size) {
                return new LinesBean[size];
            }
        };
    }
}
