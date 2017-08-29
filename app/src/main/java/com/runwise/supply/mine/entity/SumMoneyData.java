package com.runwise.supply.mine.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by myChaoFile on 2017/8/24.
 */

public class SumMoneyData implements Serializable{

    /**
     * purchase_number_list : [22,28,31,31,54]
     * product_list : ["【五得利】高筋小麦粉","一号灌汤包馅料","一次性碗 - 直无批","泰金香玉兰香米","海天 味极鲜 - 直批次"]
     * total_amount : 12045.42
     * total_number : 216.0
     * purchase_volume_list : [2298.58,497.95000000000005,34.47,3982.4500000000003,1877.8500000000001]
     */

    private double total_amount;
    private int total_number;
    private List<Double> purchase_number_list;
    private List<String> product_list;
    private List<Double> purchase_volume_list;

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public List<Double> getPurchase_number_list() {
        return purchase_number_list;
    }

    public void setPurchase_number_list(List<Double> purchase_number_list) {
        this.purchase_number_list = purchase_number_list;
    }

    public List<String> getProduct_list() {
        return product_list;
    }

    public void setProduct_list(List<String> product_list) {
        this.product_list = product_list;
    }

    public List<Double> getPurchase_volume_list() {
        return purchase_volume_list;
    }

    public void setPurchase_volume_list(List<Double> purchase_volume_list) {
        this.purchase_volume_list = purchase_volume_list;
    }
}
