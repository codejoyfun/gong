package com.runwise.supply.firstpage.entity;

/**
 * Created by libin on 2017/7/14.
 */

public class DashBoardResponse {

    /**
     * maturityNum : 0
     * purchaseAmount : 0.160992
     * adventNum : 0
     * maturityValue : 0
     * adventValue : 125.8
     * adventNum: 0
     * totalNumber:0
     */

    private int maturityNum;
    private double purchaseAmount;
    private int adventNum;
    private double maturityValue;
    private double adventValue;
    private int totalNumber;

    public int getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(int orderSum) {
        this.orderSum = orderSum;
    }

    public int getInventorySum() {
        return inventorySum;
    }

    public void setInventorySum(int inventorySum) {
        this.inventorySum = inventorySum;
    }

    private int orderSum;
    private int inventorySum;

    public int getMaturityNum() {
        return maturityNum;
    }

    public void setMaturityNum(int maturityNum) {
        this.maturityNum = maturityNum;
    }

    public double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public int getAdventNum() {
        return adventNum;
    }

    public void setAdventNum(int adventNum) {
        this.adventNum = adventNum;
    }

    public double getMaturityValue() {
        return maturityValue;
    }

    public void setMaturityValue(double maturityValue) {
        this.maturityValue = maturityValue;
    }

    public double getAdventValue() {
        return adventValue;
    }

    public void setAdventValue(double adventValue) {
        this.adventValue = adventValue;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }
}
