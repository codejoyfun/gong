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
     */

    private int maturityNum;
    private double purchaseAmount;
    private int adventNum;
    private int maturityValue;
    private double adventValue;

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

    public int getMaturityValue() {
        return maturityValue;
    }

    public void setMaturityValue(int maturityValue) {
        this.maturityValue = maturityValue;
    }

    public double getAdventValue() {
        return adventValue;
    }

    public void setAdventValue(double adventValue) {
        this.adventValue = adventValue;
    }
}
