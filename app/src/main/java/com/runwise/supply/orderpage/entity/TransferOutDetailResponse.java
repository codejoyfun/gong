package com.runwise.supply.orderpage.entity;

import android.os.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2017/10/14.
 */

public class TransferOutDetailResponse implements Serializable {

    private Info info;

    private List<TransferBatchLine> lines;

    public List<TransferBatchLine> getLines() {
        return lines;
    }

    public void setLines(List<TransferBatchLine> lines) {
        this.lines = lines;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public static class Info implements Serializable {
        private String locationDestName;
        private String locationName;
        private String pickingName;
        private int pickingID;

        public String getLocationDestName() {
            return locationDestName;
        }

        public void setLocationDestName(String locationDestName) {
            this.locationDestName = locationDestName;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public String getPickingName() {
            return pickingName;
        }

        public void setPickingName(String pickingName) {
            this.pickingName = pickingName;
        }

        public int getPickingID() {
            return pickingID;
        }

        public void setPickingID(int pickingID) {
            this.pickingID = pickingID;
        }
    }


    public static class TransferBatchLine implements Serializable {
        private String productID;
        private String productUom;
        private String productImage;
        private String productUnit;
        private int priceUnit;
        private int productUomQty;

        public int getActualQty() {
            return actualQty;
        }

        public void setActualQty(int actualQty) {
            this.actualQty = actualQty;
        }

        private int actualQty;
        private List<TransferBatchLot> productInfo;

        public String getProductUom() {
            return productUom;
        }

        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }

        public String getProductImage() {
            return productImage;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        public String getProductUnit() {
            return productUnit;
        }

        public void setProductUnit(String productUnit) {
            this.productUnit = productUnit;
        }

        public int getProductUomQty() {
            return productUomQty;
        }

        public void setProductUomQty(int productUomQty) {
            this.productUomQty = productUomQty;
        }

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
    }

    public static class TransferBatchLot implements Serializable {
        private String lotID;
        private int quantQty;

        //本地数据
        private int usedQty;

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
            return usedQty;
        }

        public void setActualQty(int actualQty) {
            this.usedQty = actualQty;
        }

        public TransferBatchLot() {
        }


        protected TransferBatchLot(Parcel in) {
            this.lotID = in.readString();
            this.quantQty = in.readInt();
            this.usedQty = in.readInt();
        }

    }
}
