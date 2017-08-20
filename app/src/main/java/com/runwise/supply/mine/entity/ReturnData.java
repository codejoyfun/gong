package com.runwise.supply.mine.entity;

import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;

import java.util.List;

/**
 * Created by mychao on 2017/7/17.
 */

public class ReturnData {
    private List<ReturnOrderBean.ListBean> lastWeekList;
    private List<ReturnOrderBean.ListBean> thisWeekList;
    private List<ReturnOrderBean.ListBean> allList;
    private List<ReturnOrderBean.ListBean> earlierList;

    public List<ReturnOrderBean.ListBean> getLastWeekList() {
        return lastWeekList;
    }

    public void setLastWeekList(List<ReturnOrderBean.ListBean> lastWeekList) {
        this.lastWeekList = lastWeekList;
    }

    public List<ReturnOrderBean.ListBean> getThisWeekList() {
        return thisWeekList;
    }

    public void setThisWeekList(List<ReturnOrderBean.ListBean> thisWeekList) {
        this.thisWeekList = thisWeekList;
    }

    public List<ReturnOrderBean.ListBean> getAllList() {
        return allList;
    }

    public void setAllList(List<ReturnOrderBean.ListBean> allList) {
        this.allList = allList;
    }

    public List<ReturnOrderBean.ListBean> getEarlierList() {
        return earlierList;
    }

    public void setEarlierList(List<ReturnOrderBean.ListBean> earlierList) {
        this.earlierList = earlierList;
    }

    public static class AllListBean {
        /**
         * orderID : 257
         * doneDate :
         * name : RSO024
         * driver : 黄天
         * createDate : 2017-06-29 13:56:26
         * createUser :
         * lines : [{"productUom":"包","priceUnit":9.5,"tax":17,"discount":0,"deliveredQty":1,"priceSubtotal":9.5,"productID":10,"saleOrderProductID":48,"lotIDs":["45"],"stockType":"lengcanghuo","lotList":[{"lotPk":"4845","lotID":45,"qty":2}],"productUomQty":1},{"productUom":"包","priceUnit":14.25,"tax":17,"discount":0,"deliveredQty":1,"priceSubtotal":14.25,"productID":9,"saleOrderProductID":49,"lotIDs":["50"],"stockType":"lengcanghuo","lotList":[{"lotPk":"4950","lotID":50,"qty":2}],"productUomQty":1}]
         * amount : 2.0
         * amountTotal : 27.79
         * state : done
         * loadingDate : 2017-06-29
         * vehicle : 沪A 040100
         * isDispatch : true
         * returnOrderID : 24
         * driveMobile : 13737574566
         * stateTracker : ["2017-06-29 13:58 退货单已完成","2017-06-29 13:56 退货单审核已通过","2017-06-29 13:56 退货单已提交"]
         */

        private int orderID;
        private String doneDate;
        private String name;
        private String driver;
        private String createDate;
        private String createUser;
        private double amount;
        private double amountTotal;
        private String state;
        private String loadingDate;
        private String vehicle;
        private boolean isDispatch;
        private int returnOrderID;
        private String driveMobile;
        private List<LinesBean> lines;
        private List<String> stateTracker;

        public int getOrderID() {
            return orderID;
        }

        public void setOrderID(int orderID) {
            this.orderID = orderID;
        }

        public String getDoneDate() {
            return doneDate;
        }

        public void setDoneDate(String doneDate) {
            this.doneDate = doneDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
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

        public String getLoadingDate() {
            return loadingDate;
        }

        public void setLoadingDate(String loadingDate) {
            this.loadingDate = loadingDate;
        }

        public String getVehicle() {
            return vehicle;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public boolean isIsDispatch() {
            return isDispatch;
        }

        public void setIsDispatch(boolean isDispatch) {
            this.isDispatch = isDispatch;
        }

        public int getReturnOrderID() {
            return returnOrderID;
        }

        public void setReturnOrderID(int returnOrderID) {
            this.returnOrderID = returnOrderID;
        }

        public String getDriveMobile() {
            return driveMobile;
        }

        public void setDriveMobile(String driveMobile) {
            this.driveMobile = driveMobile;
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

        public static class LinesBean {
            /**
             * productUom : 包
             * priceUnit : 9.5
             * tax : 17.0
             * discount : 0.0
             * deliveredQty : 1.0
             * priceSubtotal : 9.5
             * productID : 10
             * saleOrderProductID : 48
             * lotIDs : ["45"]
             * stockType : lengcanghuo
             * lotList : [{"lotPk":"4845","lotID":45,"qty":2}]
             * productUomQty : 1.0
             */

            private String productUom;
            private double priceUnit;
            private double tax;
            private double discount;
            private double deliveredQty;
            private double priceSubtotal;
            private int productID;
            private int saleOrderProductID;
            private String stockType;
            private double productUomQty;
            private List<String> lotIDs;
            private List<LotListBean> lotList;

            public String getProductUom() {
                return productUom;
            }

            public void setProductUom(String productUom) {
                this.productUom = productUom;
            }

            public double getPriceUnit() {
                return priceUnit;
            }

            public void setPriceUnit(double priceUnit) {
                this.priceUnit = priceUnit;
            }

            public double getTax() {
                return tax;
            }

            public void setTax(double tax) {
                this.tax = tax;
            }

            public double getDiscount() {
                return discount;
            }

            public void setDiscount(double discount) {
                this.discount = discount;
            }

            public double getDeliveredQty() {
                return deliveredQty;
            }

            public void setDeliveredQty(double deliveredQty) {
                this.deliveredQty = deliveredQty;
            }

            public double getPriceSubtotal() {
                return priceSubtotal;
            }

            public void setPriceSubtotal(double priceSubtotal) {
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

            public double getProductUomQty() {
                return productUomQty;
            }

            public void setProductUomQty(double productUomQty) {
                this.productUomQty = productUomQty;
            }

            public List<String> getLotIDs() {
                return lotIDs;
            }

            public void setLotIDs(List<String> lotIDs) {
                this.lotIDs = lotIDs;
            }

            public List<LotListBean> getLotList() {
                return lotList;
            }

            public void setLotList(List<LotListBean> lotList) {
                this.lotList = lotList;
            }

            public static class LotListBean {
                /**
                 * lotPk : 4845
                 * lotID : 45
                 * qty : 2.0
                 */

                private String lotPk;
                private int lotID;
                private double qty;

                public String getLotPk() {
                    return lotPk;
                }

                public void setLotPk(String lotPk) {
                    this.lotPk = lotPk;
                }

                public int getLotID() {
                    return lotID;
                }

                public void setLotID(int lotID) {
                    this.lotID = lotID;
                }

                public double getQty() {
                    return qty;
                }

                public void setQty(double qty) {
                    this.qty = qty;
                }
            }
        }
    }

}
