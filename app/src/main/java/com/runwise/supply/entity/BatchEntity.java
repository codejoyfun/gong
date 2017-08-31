package com.runwise.supply.entity;

import java.io.Serializable;

/**
 * Created by mike on 2017/8/31.
 */

public class BatchEntity implements Serializable{

    String batchNum;
    String productDate;
    String productCount;
    boolean isProductDate;

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getProductCount() {
        return productCount;
    }

    public void setProductCount(String productCount) {
        this.productCount = productCount;
    }
    public boolean isProductDate() {
        return isProductDate;
    }

    public void setProductDate(boolean productDate) {
        isProductDate = productDate;
    }

}
