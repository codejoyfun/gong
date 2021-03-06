package com.runwise.supply.mine.entity;

import java.util.List;

/**
 * Created by mike on 2017/8/22.
 */

public class ProcurementEntity {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * date : 2017-08-21
         * products : [{"qty":5,"productID":26}]
         * user : 张少军
         */

        private String date;
        private String user;
        private List<ProductsBean> products;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public List<ProductsBean> getProducts() {
            return products;
        }

        public void setProducts(List<ProductsBean> products) {
            this.products = products;
        }

        public static class ProductsBean {
            /**
             * qty : 5.0
             * productID : 26
             */

            private String qty;
            private int productID;

            public String getImageMedium() {
                return imageMedium;
            }

            public void setImageMedium(String imageMedium) {
                this.imageMedium = imageMedium;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDefaultCode() {
                return defaultCode;
            }

            public void setDefaultCode(String defaultCode) {
                this.defaultCode = defaultCode;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public String getStockUom() {
                return stockUom;
            }

            public void setStockUom(String stockUom) {
                this.stockUom = stockUom;
            }

            private String imageMedium;
            private String name;
            private String defaultCode;
            private String unit;
            private String stockUom;

            public String getQty() {
                return qty;
            }

            public void setQty(String qty) {
                this.qty = qty;
            }

            public int getProductID() {
                return productID;
            }

            public void setProductID(int productID) {
                this.productID = productID;
            }
        }
    }

}
