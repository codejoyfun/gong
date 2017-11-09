package com.runwise.supply.entity;

import java.util.List;

/**
 * 确认调拨收货
 *
 * Created by Dong on 2017/10/14.
 */

public class TransferInRequest {
    private String pickingID;
    private List<IProductData> products;

    public String getPickingID() {
        return pickingID;
    }

    public List<IProductData> getProducts() {
        return products;
    }

    public void setPickingID(String pickingID) {
        this.pickingID = pickingID;
    }

    public void setProducts(List<IProductData> products) {
        this.products = products;
    }

    public interface IProductData{};

    public static class ProductData implements IProductData{
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

    public static class ProductDataNoLot implements IProductData{
        private int productID;
        private ProductLotData lotsInfo;

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public void setLotsInfo(ProductLotData lotsInfo) {
            this.lotsInfo = lotsInfo;
        }

        public int getProductID() {
            return productID;
        }

        public ProductLotData getLotsInfo() {
            return lotsInfo;
        }
    }

    public static class ProductLotData{
        private int lotIDID;
        private int qtyDone;

        public int getLotIDID() {
            return lotIDID;
        }

        public int getQtyDone() {
            return qtyDone;
        }

        public void setLotIDID(int lotIDID) {
            this.lotIDID = lotIDID;
        }

        public void setQtyDone(int qtyDone) {
            this.qtyDone = qtyDone;
        }
    }
}
