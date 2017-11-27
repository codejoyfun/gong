package com.runwise.supply.entity;

import com.runwise.supply.orderpage.entity.ProductData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Dong on 2017/11/22.
 */

public class CartCache implements Serializable{
    ArrayList<ProductData.ListBean> listBeans;//记录购物车中商品

    public ArrayList<ProductData.ListBean> getListBeans() {
        return listBeans;
    }

    public void setListBeans(ArrayList<ProductData.ListBean> listBeans) {
        this.listBeans = listBeans;
    }
}
