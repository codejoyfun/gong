package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by mike on 2017/10/16.
 * "products": [{
 * "productID": 651
 * "lotsInfo": [{"lotID":Z170929000001,
 * "qty": 1},
 * {"lotID":Z170929000002,
 * "qty": 2}]
 * }]
 */

public class TransferOutRequest {
    public String getPickingID() {
        return pickingID;
    }

    public void setPickingID(String pickingID) {
        this.pickingID = pickingID;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    private String pickingID;
    private List<Product> products;


    public static class Product {
        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public List<Lot> getLotsInfo() {
            return lotsInfo;
        }

        public void setLotsInfo(List<Lot> lotsInfo) {
            this.lotsInfo = lotsInfo;
        }

        private int productID;
        private List<Lot> lotsInfo;
    }

    public static class Lot {
        public String getLotID() {
            return lotID;
        }

        public void setLotID(String lotID) {
            this.lotID = lotID;
        }

        private String lotID;

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        private String qty;
    }

}
