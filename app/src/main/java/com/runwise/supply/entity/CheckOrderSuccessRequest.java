package com.runwise.supply.entity;

import com.runwise.supply.orderpage.TempOrderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查提交中的订单是否提交完成
 *
 * Created by Dong on 2017/12/6.
 */
public class CheckOrderSuccessRequest {
    List<String> hash_list;

    public CheckOrderSuccessRequest(){}

    public CheckOrderSuccessRequest(List<TempOrderManager.TempOrder> orderList){
        hash_list = new ArrayList<>();
        for(TempOrderManager.TempOrder tempOrder:orderList){
            hash_list.add(tempOrder.getHashKey());
        }
    }

    public List<String> getHash_list() {
        return hash_list;
    }

    public void setHash_list(List<String> hash_list) {
        this.hash_list = hash_list;
    }
}
