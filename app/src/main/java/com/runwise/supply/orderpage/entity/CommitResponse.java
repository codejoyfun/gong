package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/9.
 */

public class CommitResponse {

    /**
     * order : {"orderID":230,"settle_amount_total":0,"confirmationDate":"","loadingTime":"","name":"SO230","doneDatetime":"","estimatedTime":"2017-06-27 00:00:00","waybill":null,"createDate":"2017-06-26 10:11:57","lines":[{"productUom":"条","priceUnit":8,"tax":17,"discount":0,"deliveredQty":0,"priceSubtotal":40,"productID":9,"saleOrderProductID":559,"lotIDs":[],"stockType":"lengcanghuo","settleAmount":0,"lotList":[],"productUomQty":5}],"createUserName":"胡勇","startUnloadDatetime":"","endUnloadDatetime":"","amountTotal":298.35,"state":"draft","isToday":false,"orderTypeID":null,"amount":15,"estimatedDate":"2017-06-27","store":{"mobile":"13598723645","partner":"胡勇","partnerID":36,"name":"【我家酸菜鱼】淮海中路店","address":"上海市徐汇区淮海中路1068号首层"},"stateTracker":["2017-06-26 10:12 订单已提交"]}
     */

    private OrderBean order;

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public static class OrderBean {
        /**
         * orderID : 230
         * settle_amount_total : 0
         * confirmationDate :
         * loadingTime :
         * name : SO230
         * doneDatetime :
         * estimatedTime : 2017-06-27 00:00:00
         * waybill : null
         * createDate : 2017-06-26 10:11:57
         * lines : [{"productUom":"条","priceUnit":8,"tax":17,"discount":0,"deliveredQty":0,"priceSubtotal":40,"productID":9,"saleOrderProductID":559,"lotIDs":[],"stockType":"lengcanghuo","settleAmount":0,"lotList":[],"productUomQty":5}]
         * createUserName : 胡勇
         * startUnloadDatetime :
         * endUnloadDatetime :
         * amountTotal : 298.35
         * state : draft
         * isToday : false
         * orderTypeID : null
         * amount : 15
         * estimatedDate : 2017-06-27
         * store : {"mobile":"13598723645","partner":"胡勇","partnerID":36,"name":"【我家酸菜鱼】淮海中路店","address":"上海市徐汇区淮海中路1068号首层"}
         * stateTracker : ["2017-06-26 10:12 订单已提交"]
         */

        private int orderID;
        private int settle_amount_total;
        private String confirmationDate;
        private String loadingTime;
        private String name;
        private String doneDatetime;
        private String estimatedTime;
        private Object waybill;
        private String createDate;
        private String createUserName;
        private String startUnloadDatetime;
        private String endUnloadDatetime;
        private double amountTotal;
        private String state;
        private boolean isToday;
        private Object orderTypeID;
        private int amount;
        private String estimatedDate;
        private StoreBean store;
        private List<LinesBean> lines;
        private List<String> stateTracker;

        public int getOrderID() {
            return orderID;
        }

        public void setOrderID(int orderID) {
            this.orderID = orderID;
        }

        public int getSettle_amount_total() {
            return settle_amount_total;
        }

        public void setSettle_amount_total(int settle_amount_total) {
            this.settle_amount_total = settle_amount_total;
        }

        public String getConfirmationDate() {
            return confirmationDate;
        }

        public void setConfirmationDate(String confirmationDate) {
            this.confirmationDate = confirmationDate;
        }

        public String getLoadingTime() {
            return loadingTime;
        }

        public void setLoadingTime(String loadingTime) {
            this.loadingTime = loadingTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDoneDatetime() {
            return doneDatetime;
        }

        public void setDoneDatetime(String doneDatetime) {
            this.doneDatetime = doneDatetime;
        }

        public String getEstimatedTime() {
            return estimatedTime;
        }

        public void setEstimatedTime(String estimatedTime) {
            this.estimatedTime = estimatedTime;
        }

        public Object getWaybill() {
            return waybill;
        }

        public void setWaybill(Object waybill) {
            this.waybill = waybill;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateUserName() {
            return createUserName;
        }

        public void setCreateUserName(String createUserName) {
            this.createUserName = createUserName;
        }

        public String getStartUnloadDatetime() {
            return startUnloadDatetime;
        }

        public void setStartUnloadDatetime(String startUnloadDatetime) {
            this.startUnloadDatetime = startUnloadDatetime;
        }

        public String getEndUnloadDatetime() {
            return endUnloadDatetime;
        }

        public void setEndUnloadDatetime(String endUnloadDatetime) {
            this.endUnloadDatetime = endUnloadDatetime;
        }

        public double getAmountTotal() {
            return amountTotal;
        }

        public void setAmountTotal(double amountTotal) {
            this.amountTotal = amountTotal;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public boolean isIsToday() {
            return isToday;
        }

        public void setIsToday(boolean isToday) {
            this.isToday = isToday;
        }

        public Object getOrderTypeID() {
            return orderTypeID;
        }

        public void setOrderTypeID(Object orderTypeID) {
            this.orderTypeID = orderTypeID;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getEstimatedDate() {
            return estimatedDate;
        }

        public void setEstimatedDate(String estimatedDate) {
            this.estimatedDate = estimatedDate;
        }

        public StoreBean getStore() {
            return store;
        }

        public void setStore(StoreBean store) {
            this.store = store;
        }

        public List<LinesBean> getLines() {
            return lines;
        }

        public void setLines(List<LinesBean> lines) {
            this.lines = lines;
        }

        public List<String> getStateTracker() {
            return stateTracker;
        }

        public void setStateTracker(List<String> stateTracker) {
            this.stateTracker = stateTracker;
        }

        public static class StoreBean {
            /**
             * mobile : 13598723645
             * partner : 胡勇
             * partnerID : 36
             * name : 【我家酸菜鱼】淮海中路店
             * address : 上海市徐汇区淮海中路1068号首层
             */

            private String mobile;
            private String partner;
            private int partnerID;
            private String name;
            private String address;

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getPartner() {
                return partner;
            }

            public void setPartner(String partner) {
                this.partner = partner;
            }

            public int getPartnerID() {
                return partnerID;
            }

            public void setPartnerID(int partnerID) {
                this.partnerID = partnerID;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }

        public static class LinesBean {
            /**
             * productUom : 条
             * priceUnit : 8
             * tax : 17
             * discount : 0
             * deliveredQty : 0
             * priceSubtotal : 40
             * productID : 9
             * saleOrderProductID : 559
             * lotIDs : []
             * stockType : lengcanghuo
             * settleAmount : 0
             * lotList : []
             * productUomQty : 5
             */

            private String productUom;
            private int priceUnit;
            private int tax;
            private int discount;
            private int deliveredQty;
            private int priceSubtotal;
            private int productID;
            private int saleOrderProductID;
            private String stockType;
            private int settleAmount;
            private int productUomQty;
            private List<?> lotIDs;
            private List<?> lotList;

            public String getProductUom() {
                return productUom;
            }

            public void setProductUom(String productUom) {
                this.productUom = productUom;
            }

            public int getPriceUnit() {
                return priceUnit;
            }

            public void setPriceUnit(int priceUnit) {
                this.priceUnit = priceUnit;
            }

            public int getTax() {
                return tax;
            }

            public void setTax(int tax) {
                this.tax = tax;
            }

            public int getDiscount() {
                return discount;
            }

            public void setDiscount(int discount) {
                this.discount = discount;
            }

            public int getDeliveredQty() {
                return deliveredQty;
            }

            public void setDeliveredQty(int deliveredQty) {
                this.deliveredQty = deliveredQty;
            }

            public int getPriceSubtotal() {
                return priceSubtotal;
            }

            public void setPriceSubtotal(int priceSubtotal) {
                this.priceSubtotal = priceSubtotal;
            }

            public int getProductID() {
                return productID;
            }

            public void setProductID(int productID) {
                this.productID = productID;
            }

            public int getSaleOrderProductID() {
                return saleOrderProductID;
            }

            public void setSaleOrderProductID(int saleOrderProductID) {
                this.saleOrderProductID = saleOrderProductID;
            }

            public String getStockType() {
                return stockType;
            }

            public void setStockType(String stockType) {
                this.stockType = stockType;
            }

            public int getSettleAmount() {
                return settleAmount;
            }

            public void setSettleAmount(int settleAmount) {
                this.settleAmount = settleAmount;
            }

            public int getProductUomQty() {
                return productUomQty;
            }

            public void setProductUomQty(int productUomQty) {
                this.productUomQty = productUomQty;
            }

            public List<?> getLotIDs() {
                return lotIDs;
            }

            public void setLotIDs(List<?> lotIDs) {
                this.lotIDs = lotIDs;
            }

            public List<?> getLotList() {
                return lotList;
            }

            public void setLotList(List<?> lotList) {
                this.lotList = lotList;
            }
        }
    }
}
