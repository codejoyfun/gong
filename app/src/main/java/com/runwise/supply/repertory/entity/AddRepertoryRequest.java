package com.runwise.supply.repertory.entity;

/**
 * Created by myChaoFile on 2017/8/25.
 */

public class AddRepertoryRequest {

    /**
     * lot_name :
     * product_id : 20
     * produce_datetime : 2017-08-25
     * life_datetime :
     */

    private String lot_name;
    private int product_id;
    private String produce_datetime;
    private String life_datetime;

    public String getLot_name() {
        return lot_name;
    }

    public void setLot_name(String lot_name) {
        this.lot_name = lot_name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduce_datetime() {
        return produce_datetime;
    }

    public void setProduce_datetime(String produce_datetime) {
        this.produce_datetime = produce_datetime;
    }

    public String getLife_datetime() {
        return life_datetime;
    }

    public void setLife_datetime(String life_datetime) {
        this.life_datetime = life_datetime;
    }
}
