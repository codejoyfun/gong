package com.runwise.supply.orderpage;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 异步下单，在本地保存提交中的订单信息
 *
 * Created by Dong on 2017/12/1.
 */

public class TempOrderManager {

    /**
     * 异步保存订单信息
     */
    public void saveTempOrderAsync(OrderResponse.ListBean order){
        Observable.fromCallable(()->saveTempOrder(order))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void saveTempOrder(OrderResponse.ListBean order){

    }

    /**
     * 获取提交中的订单列表
     *
     * @return
     */
    public List<OrderResponse.ListBean> getTempOrders(){
        return null;
    }

    /**
     * 从本地记录中删除对应的订单
     * @param key
     */
    public void removeTempOrder(String key){

    }
}
