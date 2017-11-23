package com.runwise.supply.entity;

import com.runwise.supply.orderpage.entity.ProductData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dong on 2017/11/22.
 */

public class CartCache implements Serializable{
    ArrayList<ProductData.ListBean> listBeans;

    public ArrayList<ProductData.ListBean> getListBeans() {
        return listBeans;
    }

    public void setListBeans(ArrayList<ProductData.ListBean> listBeans) {
        this.listBeans = listBeans;
    }
}
