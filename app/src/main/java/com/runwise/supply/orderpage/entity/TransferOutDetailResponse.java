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

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductTracking() {
            return productTracking;
        }

        public void setProductTracking(String productTracking) {
            this.productTracking = productTracking;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        private String productCode;
        private String productTracking;
        private String productName;
        private int priceUnit;
        private double productUomQty;

        public double getActualQty() {
            return actualQty;
        }

        public void setActualQty(double actualQty) {
            this.actualQty = actualQty;
        }

        private double actualQty;
        private List<TransferBatchLot> productLotInfo;

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

        public double getProductUomQty() {
            return productUomQty;
        }

        public void setProductUomQty(double productUomQty) {
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

        public List<TransferBatchLot> getProductLotInfo() {
            return productLotInfo;
        }

        public void setPriceUnit(int priceUnit) {
            this.priceUnit = priceUnit;
        }

        public void setProductLotInfo(List<TransferBatchLot> productLotInfo) {
            this.productLotInfo = productLotInfo;
        }
    }

    public static class TransferBatchLot implements Serializable {
        private String lotID;

        public String getLotIDID() {
            return lotIDID;
        }

        public void setLotIDID(String lotIDID) {
            this.lotIDID = lotIDID;
        }

        private String lotIDID;
        private double quantQty;

        //本地数据
        private double usedQty;

        public String getLotID() {
            return lotID;
        }

        public double getQuantQty() {
            return quantQty;
        }

        public void setLotID(String lotID) {
            this.lotID = lotID;
        }

        public void setQuantQty(double quantQty) {
            this.quantQty = quantQty;
        }

        public double getUsedQty() {
            return usedQty;
        }

        public void setUsedQty(double usedQty) {
            this.usedQty = usedQty;
        }

        public TransferBatchLot() {
        }


        protected TransferBatchLot(Parcel in) {
            this.lotID = in.readString();
            this.quantQty = in.readDouble();
            this.usedQty = in.readDouble();
        }

    }
}
