package com.runwise.supply.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.List;

/**
 * Created by mike on 2018/2/27.
 */

public class ProductListResponse {
    int version;
    List<ProductBasicList.ListBean> products;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ProductBasicList.ListBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductBasicList.ListBean> products) {
        this.products = products;
    }
}
