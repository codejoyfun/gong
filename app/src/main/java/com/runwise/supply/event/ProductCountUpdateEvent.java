package com.runwise.supply.event;

import com.runwise.supply.orderpage.entity.ProductBasicList;

/**
 * Created by Dong on 2017/11/22.
 */

public class ProductCountUpdateEvent {

    public Object getException() {
        return exception;
    }

    public void setException(Object exception) {
        this.exception = exception;
    }

    Object exception;

    public ProductCountUpdateEvent(){}
    public ProductCountUpdateEvent(ProductBasicList.ListBean listBean, double count){
        this.bean = listBean;
        this.count = count;
    }


    public ProductBasicList.ListBean bean;
    public double count;
}
