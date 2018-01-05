package com.runwise.supply.entity;

import android.text.TextUtils;

import com.runwise.supply.orderpage.entity.ProductData;

import java.util.List;

/**
 * 检查商品有效性
 * TODO:用于自测用途，具体协议未定
 *
 * Created by Dong on 2018/1/5.
 */

public class ProductValidateRequest {
    private List<Product> list;
    public ProductValidateRequest(){};
    public ProductValidateRequest(List<Product> listToCheck){list = listToCheck;}
    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }

    /**
     * 需要检查的商品信息
     */
    public static final class Product{
        private int productId;
        private double price;
        private boolean isSale;
        public Product(){};
        public Product(ProductData.ListBean bean){
            productId = bean.getProductID();
            price = bean.getPrice();
            isSale = TextUtils.isEmpty(bean.getProductTag());
        }

        public int getProductId() {
            return productId;
        }

        public double getPrice() {
            return price;
        }

        public boolean isSale() {
            return isSale;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setSale(boolean sale) {
            isSale = sale;
        }
    }
}
