package com.runwise.supply.mine.entity;

/**
 * Created by myChaoFile on 17/2/7.
 */

public class CollectCarInfo {
    private String member_id;//": 1,    //用户id
    private String car_id;//": 1,    //车id
    private CollectCar car;

    public CollectCar getCar() {
        return car;
    }

    public void setCar(CollectCar car) {
        this.car = car;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }
}
