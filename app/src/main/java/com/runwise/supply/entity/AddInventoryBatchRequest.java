package com.runwise.supply.entity;

import java.util.List;

/**
 * 盘点增加批次
 *
 * Created by Dong on 2017/12/9.
 */

public class AddInventoryBatchRequest {
    private int productID;
    private List<BatchData> batchDataList;

    public int getProductID() {
        return productID;
    }

    public List<BatchData> getBatchDataList() {
        return batchDataList;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setBatchDataList(List<BatchData> batchDataList) {
        this.batchDataList = batchDataList;
    }

    public static class BatchData{
        private String batchName;
        private String batchTime;
        private int count;

        public String getBatchName() {
            return batchName;
        }

        public String getBatchTime() {
            return batchTime;
        }

        public int getCount() {
            return count;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public void setBatchTime(String batchTime) {
            this.batchTime = batchTime;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
