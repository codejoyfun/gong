package com.runwise.supply.event;

import com.runwise.supply.orderpage.entity.ProductData;

/**
 * Created by Dong on 2017/11/22.
 */

public class ProductCountUpdateEvent {
    public ProductCountUpdateEvent(){}
    public ProductCountUpdateEvent(ProductData.ListBean listBean,double count){
        this.bean = listBean;
        this.count = count;
    }
    public ProductData.ListBean bean;
    public double count;
}
