package com.runwise.supply.entity;

import java.util.List;

/**
 * 确认调拨收货
 *
 * Created by Dong on 2017/10/14.
 */

public class TransferInRequest {
    private String pickingID;
    private List<ProductData> products;

    public String getPickingID() {
        return pickingID;
    }

    public List<ProductData> getProducts() {
        return products;
    }

    public void setPickingID(String pickingID) {
        this.pickingID = pickingID;
    }

    public void setProducts(List<ProductData> products) {
        this.products = products;
    }

    public static class ProductData {
        private int productID;
        private List<ProductLotData> lotsInfo;

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public List<ProductLotData> getLotsInfo() {
            return lotsInfo;
        }

        public void setLotsInfo(List<ProductLotData> lotsInfo) {
            this.lotsInfo = lotsInfo;
        }
    }

    public static class ProductDataNoLot extends ProductData{
        private ProductLotData 
    }

    public static class ProductLotData{
        private int lotID;
        private int qtyDone;

        public int getLotID() {
            return lotID;
        }

        public int getQtyDone() {
            return qtyDone;
        }

        public void setLotID(int lotID) {
            this.lotID = lotID;
        }

        public void setQtyDone(int qtyDone) {
            this.qtyDone = qtyDone;
        }
    }
}
