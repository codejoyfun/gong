package com.runwise.supply.firstpage.entity;

/**
 * Created by libin on 2017/7/17.
 */

public class ReceiveBean {
    private String name;        //货物名称
    private String productId;   //商品id
    private int count;          //商品数量
    private boolean isTwoUnit;  //是否为双单位
    private String unit;        //是双单位的话，单位是什么
    private double twoUnitValue;//有双单位情况下，用户输入的值

    public ReceiveBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isTwoUnit() {
        return isTwoUnit;
    }

    public void setTwoUnit(boolean twoUnit) {
        isTwoUnit = twoUnit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getTwoUnitValue() {
        return twoUnitValue;
    }

    public void setTwoUnitValue(double twoUnitValue) {
        this.twoUnitValue = twoUnitValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiveBean that = (ReceiveBean) o;

        return productId != null ? productId.equals(that.productId) : that.productId == null;

    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }
}
