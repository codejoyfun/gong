package com.runwise.supply.event;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.List;

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

    public ProductCountUpdateEvent(List<ProductBasicList.ListBean> beanList){
        this.beanList = beanList;
    }


    public ProductBasicList.ListBean bean;
    public List<ProductBasicList.ListBean> beanList;
    public double count;
}
