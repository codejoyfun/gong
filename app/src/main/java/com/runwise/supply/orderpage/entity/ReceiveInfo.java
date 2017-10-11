package com.runwise.supply.orderpage.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

import java.io.Serializable;

/**
 * Created by mike on 2017/10/11.
 */

public class ReceiveInfo implements Serializable {
    public static final String SEPARATOR = "*";
    @Id
    @NoAutoIncrement
    String id;
    @Column
    private int orderId;
    @Column
    private int productId;
    @Column
    private String batchNumberList;
    @Column
    private String countList;
    @Column
    private int count;

    public String getBatchNumberList() {
        return batchNumberList;
    }

    public void setBatchNumberList(String batchNumberList) {
        this.batchNumberList = batchNumberList;
    }

    public String getCountList() {
        return countList;
    }

    public void setCountList(String countList) {
        this.countList = countList;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
