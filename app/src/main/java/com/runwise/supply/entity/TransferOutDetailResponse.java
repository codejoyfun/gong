package com.runwise.supply.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2018/6/14.
 */

public class TransferOutDetailResponse implements Serializable {
    private Info info;
    private List<Lines> lines;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
    public List<Lines> getLines() {
        return lines;
    }

    public void setLines(List<Lines> lines) {
        this.lines = lines;
    }

    public static class Info implements Serializable {
        private double totalPrice;
        private int pickingID;
        private int productLines;
        private String pickingName;
        private int pickingStateNum;
        private int totalNum;

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setPickingID(int pickingID) {
            this.pickingID = pickingID;
        }

        public int getPickingID() {
            return pickingID;
        }

        public void setProductLines(int productLines) {
            this.productLines = productLines;
        }

        public int getProductLines() {
            return productLines;
        }

        public void setPickingName(String pickingName) {
            this.pickingName = pickingName;
        }

        public String getPickingName() {
            return pickingName;
        }

        public void setPickingStateNum(int pickingStateNum) {
            this.pickingStateNum = pickingStateNum;
        }

        public int getPickingStateNum() {
            return pickingStateNum;
        }

        public void setTotalNum(int totalNum) {
            this.totalNum = totalNum;
        }

        public int getTotalNum() {
            return totalNum;
        }
    }
    public static class Lines {
        private String productUom;
        private double priceUnit;
        private String productName;
        private int productID;
        private String productCode;
        private double productUnit;
        private String productImage;
        private int productQtyDone;
        private int productUomQty;
        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }
        public String getProductUom() {
            return productUom;
        }

        public void setPriceUnit(double priceUnit) {
            this.priceUnit = priceUnit;
        }
        public double getPriceUnit() {
            return priceUnit;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
        public String getProductName() {
            return productName;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }
        public int getProductID() {
            return productID;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }
        public String getProductCode() {
            return productCode;
        }

        public void setProductUnit(double productUnit) {
            this.productUnit = productUnit;
        }
        public double getProductUnit() {
            return productUnit;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }
        public String getProductImage() {
            return productImage;
        }

        public void setProductQtyDone(int productQtyDone) {
            this.productQtyDone = productQtyDone;
        }
        public int getProductQtyDone() {
            return productQtyDone;
        }

        public void setProductUomQty(int productUomQty) {
            this.productUomQty = productUomQty;
        }
        public int getProductUomQty() {
            return productUomQty;
        }

    }
}
