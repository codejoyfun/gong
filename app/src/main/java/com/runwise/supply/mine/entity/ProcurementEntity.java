package com.runwise.supply.mine.entity;

import java.util.List;

/**
 * Created by mike on 2017/8/22.
 */

public class ProcurementEntity {
    private List<ListBean> list;

    public void setList(List<ListBean> list){
        this.list = list;
    }
    public List<ListBean> getList(){
        return this.list;
    }

    public class ListBean{
        private String date;

        private List<Products> products;

        private String user;

        public void setDate(String date){
            this.date = date;
        }
        public String getDate(){
            return this.date;
        }
        public void setProducts(List<Products> products){
            this.products = products;
        }
        public List<Products> getProducts(){
            return this.products;
        }
        public void setUser(String user){
            this.user = user;
        }
        public String getUser(){
            return this.user;
        }
    }

    public class Products
    {
        private int qty;

        private int productID;

        public void setQty(int qty){
            this.qty = qty;
        }
        public int getQty(){
            return this.qty;
        }
        public void setProductID(int productID){
            this.productID = productID;
        }
        public int getProductID(){
            return this.productID;
        }
    }




}
