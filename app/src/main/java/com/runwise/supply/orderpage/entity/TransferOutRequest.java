package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by mike on 2017/10/16.
 * "products": [{
 * "productID": 651
 * "lotsInfo": [{"lotIDID":Z170929000001,
 * "qty": 1},
 * {"lotIDID":Z170929000002,
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

    public List<IProduct> getProducts() {
        return products;
    }

    public void setProducts(List<IProduct> products) {
        this.products = products;
    }

    private String pickingID;
    private List<IProduct> products;

    public interface IProduct{};

    public static class Product implements IProduct{
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

    public static class ProductNoLot implements IProduct{
        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public Lot getLotsInfo() {
            return lotsInfo;
        }

        public void setLotsInfo(Lot lotsInfo) {
            this.lotsInfo = lotsInfo;
        }

        private int productID;
        private Lot lotsInfo;
    }

    public static class Lot {
        public String getLotIDID() {
            return lotIDID;
        }

        public void setLotIDID(String lotIDID) {
            this.lotIDID = lotIDID;
        }

        private String lotIDID;

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        private String qty;

    }

}
