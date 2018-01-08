package com.runwise.supply.firstpage.entity;

/**
 * Created by libin on 2017/7/22.
 */

public class ReturnBean {
    private int pId;        //商品id
    private double returnCount;//退货数量
    private double maxReturnCount;//最大可退货数量
    private String note;    //描述
    private String name;    //商品名称

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public double getReturnCount() {
        return returnCount;
    }

    public void setReturnCount(double returnCount) {
        this.returnCount = returnCount;
    }

    public double getMaxReturnCount() {
        return maxReturnCount;
    }

    public void setMaxReturnCount(double maxReturnCount) {
        this.maxReturnCount = maxReturnCount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnBean that = (ReturnBean) o;

        return pId == that.pId;

    }

    @Override
    public int hashCode() {
        return pId;
    }
}
