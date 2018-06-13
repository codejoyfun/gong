package com.runwise.supply.event;


import com.runwise.supply.entity.StockProductListResponse;

import java.util.List;

/**
 * Created by mike on 2018/6/13.
 */

public class TransferoutProductCountUpdateEvent {
    public Object getException() {
        return exception;
    }

    public void setException(Object exception) {
        this.exception = exception;
    }

    Object exception;

    public TransferoutProductCountUpdateEvent(){}
    public TransferoutProductCountUpdateEvent(StockProductListResponse.ListBean listBean, double count){
        this.bean = listBean;
        this.count = count;
    }

    public TransferoutProductCountUpdateEvent(List<StockProductListResponse.ListBean> beanList){
        this.beanList = beanList;
    }


    public StockProductListResponse.ListBean bean;
    public List<StockProductListResponse.ListBean> beanList;
    public double count;
}
