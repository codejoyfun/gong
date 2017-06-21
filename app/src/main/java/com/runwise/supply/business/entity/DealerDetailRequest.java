package com.runwise.supply.business.entity;

/**
 * Created by libin on 2017/1/21.
 */

public class DealerDetailRequest {
    public DealerDetailRequest(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    private String dealer_id;

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }
}
