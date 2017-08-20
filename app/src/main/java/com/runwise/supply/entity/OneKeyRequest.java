package com.runwise.supply.entity;

/**
 * Created by libin on 2017/8/20.
 */

public class OneKeyRequest {


    /**
     * predict_sale_amount : 255
     * yongliang_factor : 0.15
     */

    private double predict_sale_amount;
    private double yongliang_factor;

    public double getPredict_sale_amount() {
        return predict_sale_amount;
    }

    public void setPredict_sale_amount(double predict_sale_amount) {
        this.predict_sale_amount = predict_sale_amount;
    }

    public double getYongliang_factor() {
        return yongliang_factor;
    }

    public void setYongliang_factor(double yongliang_factor) {
        this.yongliang_factor = yongliang_factor;
    }
}
