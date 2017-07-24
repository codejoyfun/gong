package com.runwise.supply.mine.entity;

import android.content.Intent;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mychao on 2017/7/18.
 */

public class CheckOrderData {

    private List<BankStatementBean> bankStatement;

    public List<BankStatementBean> getBankStatement() {
        return bankStatement;
    }

    public void setBankStatement(List<BankStatementBean> bankStatement) {
        this.bankStatement = bankStatement;
    }

    public static class BankStatementBean implements Serializable{
        /**
         * totalPrice : 251.55
         * endDate : 2017-07-21
         * name : INV/2017/0002
         * beginDate : 2017-06-21
         * bankStatementID : 12
         * orders : [{"orderID":221,"amount":11,"createDate":"2017-06-12 09:16:49","totalPrice":242.19,"name":"SO221"}]
         */

        private String totalPrice;
        private String endDate;
        private String name;
        private String beginDate;
        private String bankStatementID;
        private List<OrdersBean> orders;
        private boolean timeLater;

        public boolean isTimeLater() {
            return timeLater;
        }

        public void setTimeLater(boolean timeLater) {
            this.timeLater = timeLater;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getBankStatementID() {
            return bankStatementID;
        }

        public void setBankStatementID(String bankStatementID) {
            this.bankStatementID = bankStatementID;
        }

        public List<OrdersBean> getOrders() {
            return orders;
        }

        public void setOrders(List<OrdersBean> orders) {
            this.orders = orders;
        }

        public static class OrdersBean implements Serializable{
            /**
             * orderID : 221
             * amount : 11
             * createDate : 2017-06-12 09:16:49
             * totalPrice : 242.19
             * name : SO221
             */

            private String orderID;
            private String amount;
            private String createDate;
            private String totalPrice;
            private String name;

            public String getOrderID() {
                return orderID;
            }

            public void setOrderID(String orderID) {
                this.orderID = orderID;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public String getTotalPrice() {
                return totalPrice;
            }

            public void setTotalPrice(String totalPrice) {
                this.totalPrice = totalPrice;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

}
