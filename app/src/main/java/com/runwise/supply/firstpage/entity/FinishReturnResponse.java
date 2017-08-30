package com.runwise.supply.firstpage.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2017/8/29.
 */

public class FinishReturnResponse implements Serializable{
    private ReturnOrder returnOrder;

    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }

    public ReturnOrder getReturnOrder() {
        return returnOrder;
    }

    public class ReturnOrder implements Serializable{

        private double amountTotal;
        private List<String> stateTracker;
        private String doneDate;
        private String createDate;
        private String state;
        private String vehicle;
        private boolean isTwoUnit;
        private String deliveryType;
        private int hasAttachment;
        private String driver;
        private boolean isThirdPartLog;
        private String doneDtate;
        private String loadingDate;
        private String createUser;
        private boolean isDispatch;
        private int returnOrderID;
        private String driveMobile;
        private int orderID;
        private boolean isPaid;
        private String name;
        private boolean returnThirdPartLog;
        private List<Lines> lines;
        private int amount;

        public void setAmountTotal(double amountTotal) {
            this.amountTotal = amountTotal;
        }

        public double getAmountTotal() {
            return amountTotal;
        }

        public void setStateTracker(List<String> stateTracker) {
            this.stateTracker = stateTracker;
        }

        public List<String> getStateTracker() {
            return stateTracker;
        }

        public void setDoneDate(String doneDate) {
            this.doneDate = doneDate;
        }

        public String getDoneDate() {
            return doneDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public String getVehicle() {
            return vehicle;
        }

        public void setIsTwoUnit(boolean isTwoUnit) {
            this.isTwoUnit = isTwoUnit;
        }

        public boolean getIsTwoUnit() {
            return isTwoUnit;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }

        public String getDeliveryType() {
            return deliveryType;
        }

        public void setHasAttachment(int hasAttachment) {
            this.hasAttachment = hasAttachment;
        }

        public int getHasAttachment() {
            return hasAttachment;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getDriver() {
            return driver;
        }

        public void setIsThirdPartLog(boolean isThirdPartLog) {
            this.isThirdPartLog = isThirdPartLog;
        }

        public boolean getIsThirdPartLog() {
            return isThirdPartLog;
        }

        public void setDoneDtate(String doneDtate) {
            this.doneDtate = doneDtate;
        }

        public String getDoneDtate() {
            return doneDtate;
        }

        public void setLoadingDate(String loadingDate) {
            this.loadingDate = loadingDate;
        }

        public String getLoadingDate() {
            return loadingDate;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setIsDispatch(boolean isDispatch) {
            this.isDispatch = isDispatch;
        }

        public boolean getIsDispatch() {
            return isDispatch;
        }

        public void setReturnOrderID(int returnOrderID) {
            this.returnOrderID = returnOrderID;
        }

        public int getReturnOrderID() {
            return returnOrderID;
        }

        public void setDriveMobile(String driveMobile) {
            this.driveMobile = driveMobile;
        }

        public String getDriveMobile() {
            return driveMobile;
        }

        public void setOrderID(int orderID) {
            this.orderID = orderID;
        }

        public int getOrderID() {
            return orderID;
        }

        public void setIsPaid(boolean isPaid) {
            this.isPaid = isPaid;
        }

        public boolean getIsPaid() {
            return isPaid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setReturnThirdPartLog(boolean returnThirdPartLog) {
            this.returnThirdPartLog = returnThirdPartLog;
        }

        public boolean getReturnThirdPartLog() {
            return returnThirdPartLog;
        }

        public void setLines(List<Lines> lines) {
            this.lines = lines;
        }

        public List<Lines> getLines() {
            return lines;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }

        public class Lines implements Serializable{

            private String productUom;
            private int priceUnit;
            private int tax;
            private int discount;
            private int deliveredQty;
            private int priceSubtotal;
            private int productID;
            private int saleOrderProductID;
            private List<String> lotIDs;
            private int pickupWeight;
            private int pickupNum;
            private String stockType;
            private List<String> lotList;
            private int productUomQty;

            public void setProductUom(String productUom) {
                this.productUom = productUom;
            }

            public String getProductUom() {
                return productUom;
            }

            public void setPriceUnit(int priceUnit) {
                this.priceUnit = priceUnit;
            }

            public int getPriceUnit() {
                return priceUnit;
            }

            public void setTax(int tax) {
                this.tax = tax;
            }

            public int getTax() {
                return tax;
            }

            public void setDiscount(int discount) {
                this.discount = discount;
            }

            public int getDiscount() {
                return discount;
            }

            public void setDeliveredQty(int deliveredQty) {
                this.deliveredQty = deliveredQty;
            }

            public int getDeliveredQty() {
                return deliveredQty;
            }

            public void setPriceSubtotal(int priceSubtotal) {
                this.priceSubtotal = priceSubtotal;
            }

            public int getPriceSubtotal() {
                return priceSubtotal;
            }

            public void setProductID(int productID) {
                this.productID = productID;
            }

            public int getProductID() {
                return productID;
            }

            public void setSaleOrderProductID(int saleOrderProductID) {
                this.saleOrderProductID = saleOrderProductID;
            }

            public int getSaleOrderProductID() {
                return saleOrderProductID;
            }

            public void setLotIDs(List<String> lotIDs) {
                this.lotIDs = lotIDs;
            }

            public List<String> getLotIDs() {
                return lotIDs;
            }

            public void setPickupWeight(int pickupWeight) {
                this.pickupWeight = pickupWeight;
            }

            public int getPickupWeight() {
                return pickupWeight;
            }

            public void setPickupNum(int pickupNum) {
                this.pickupNum = pickupNum;
            }

            public int getPickupNum() {
                return pickupNum;
            }

            public void setStockType(String stockType) {
                this.stockType = stockType;
            }

            public String getStockType() {
                return stockType;
            }

            public void setLotList(List<String> lotList) {
                this.lotList = lotList;
            }

            public List<String> getLotList() {
                return lotList;
            }

            public void setProductUomQty(int productUomQty) {
                this.productUomQty = productUomQty;
            }

            public int getProductUomQty() {
                return productUomQty;
            }

        }

    }


}
