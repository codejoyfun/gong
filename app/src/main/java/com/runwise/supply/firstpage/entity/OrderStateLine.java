package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/16.
 * 订单状态时间条里面，用来传值
 */

public class OrderStateLine {
    private String state;
    private String content;
    private String time;
    List<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> alterProducts;


    public OrderStateLine() {
    }
    public List<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> getAlterProducts() {
        return alterProducts;
    }

    public void setAlterProducts(List<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> alterProducts) {
        this.alterProducts = alterProducts;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
