package com.runwise.supply.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dong on 2017/11/22.
 */

public class CartCache implements Serializable{
    ArrayList<ProductBasicList.ListBean> listBeans;//记录购物车中商品

    public ArrayList<ProductBasicList.ListBean> getListBeans() {
        return listBeans;
    }

    public void setListBeans(ArrayList<ProductBasicList.ListBean> listBeans) {
        this.listBeans = listBeans;
    }
}
