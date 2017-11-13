package com.runwise.supply.entity;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * 首页订单列表排序
 *
 * Created by Dong on 2017/11/12.
 */

public abstract class FirstPageOrder implements Comparable<FirstPageOrder> {

    /**
     * 实现这个方法用于排序
     * @return
     */
    abstract public String getCreateDate();

    @Override
    public int compareTo(@NonNull FirstPageOrder firstPageOrder) {
        return firstPageOrder.getCreateDate().compareTo(getCreateDate());
    }
}
