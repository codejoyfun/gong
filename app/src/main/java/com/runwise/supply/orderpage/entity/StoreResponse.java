package com.runwise.supply.orderpage.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2017/10/13.
 */

public class StoreResponse implements Serializable {
    private List<Store> shopList;

    public List<Store> getList() {
        return shopList;
    }

    public void setList(List<Store> shopList) {
        this.shopList = shopList;
    }

    public static class Store implements Serializable{
        private int shopID;
        private String shopName;

        public int getShopID() {
            return shopID;
        }

        public void setShopID(int shopID) {
            this.shopID = shopID;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
    }


}
