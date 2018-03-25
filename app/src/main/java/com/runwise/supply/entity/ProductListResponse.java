package com.runwise.supply.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2018/2/27.
 */

public class ProductListResponse implements Serializable{
//    {"jsonrpc": "2.0", "id": null, "result": {"state": "A0006", "data": []}}
    int version;
    List<ProductBasicList.ListBean> products;
    List<CategoryBean> category;

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
    public List<CategoryBean> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryBean> category) {
        this.category = category;
    }

    public class CategoryBean implements Serializable{
        private String categoryParent;
        private List<String> categoryChild;

        public String getCategoryParent() {
            return categoryParent;
        }

        public void setCategoryParent(String categoryParent) {
            this.categoryParent = categoryParent;
        }
        public List<String> getCategoryChild() {
            return categoryChild;
        }

        public void setCategoryChild(List<String> categoryChild) {
            this.categoryChild = categoryChild;
        }
    }
}
