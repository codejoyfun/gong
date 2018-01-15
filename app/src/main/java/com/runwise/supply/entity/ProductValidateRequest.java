package com.runwise.supply.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 检查商品有效性
 * TODO:用于自测用途，具体协议未定
 *
 * Created by Dong on 2018/1/5.
 */

public class ProductValidateRequest implements Serializable{
    private List<Integer> products;

    public void setProducts(List<Integer> products) {
        this.products = products;
    }

    public List<Integer> getProducts() {
        return products;
    }
}
