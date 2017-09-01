package com.runwise.supply.firstpage.entity;

import com.runwise.supply.orderpage.entity.ImageBean;

import java.util.List;

/**
 * Created by libin on 2017/7/17.
 */

public class ReceiveBean {
    private String name;        //货物名称
    private int productId;   //商品id
    private int count;          //商品数量
    private boolean isTwoUnit;  //是否为双单位
    private String unit;        //是双单位的话，单位是什么
    private double twoUnitValue;//有双单位情况下，用户输入的值
    private String tracking;
    private ImageBean imageBean;

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    private String stockType;

    private String defaultCode;



    List<ReceiveRequest.ProductsBean.LotBean> lot_list;

    public ReceiveBean() {
    }
    public List<ReceiveRequest.ProductsBean.LotBean> getLot_list() {
        return lot_list;
    }

    public void setLot_list(List<ReceiveRequest.ProductsBean.LotBean> lot_list) {
        this.lot_list = lot_list;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
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
    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiveBean that = (ReceiveBean) o;

        return productId == that.productId;

    }

    @Override
    public int hashCode() {
        return productId;
    }


    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }


    public ImageBean getImageBean() {
        return imageBean;
    }

    public void setImageBean(ImageBean imageBean) {
        this.imageBean = imageBean;
    }

}
