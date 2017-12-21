package com.runwise.supply.business.entity;

import java.util.List;

/**
 * 检查提交中的订单是否已经提交完成
 *
 * Created by Dong on 2017/12/7.
 */

public class CheckOrderResponse {
    List<OrderingBean> orderingList;

    public List<OrderingBean> getOrderingList() {
        return orderingList;
    }

    public void setOrderingList(List<OrderingBean> orderingList) {
        this.orderingList = orderingList;
    }

    public static class OrderingBean{
        private String error;
        private String hash;
        private String state;

        public String getError() {
            return error;
        }

        public String getHash() {
            return hash;
        }

        public String getState() {
            return state;
        }

        public void setError(String error) {
            this.error = error;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
