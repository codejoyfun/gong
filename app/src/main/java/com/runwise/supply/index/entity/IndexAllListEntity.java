package com.runwise.supply.index.entity;

import java.util.List;

/**
 * Created by myChaoFile on 17/1/9.
 */

public class IndexAllListEntity {
    public final static int CAR_SYS = 0;
    public final static int CAR_INFO = 1;
    public final static int CAR_COM = 2;
    public final static int CAR_FLOOW = 3;

    public final static int GOTO_CAR = 0;
    public final static int GOTO_COM = 1;
    private int type;
    private int what;
    private IndexCarSys carSys1;
    private IndexCarSys carSys2;
    private boolean typeLast;
    private String name;
    private List<IndexCarInfo> carInfoList;
    private IndexDealers carDealer;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public IndexCarSys getCarSys1() {
        return carSys1;
    }

    public void setCarSys1(IndexCarSys carSys1) {
        this.carSys1 = carSys1;
    }

    public IndexCarSys getCarSys2() {
        return carSys2;
    }

    public void setCarSys2(IndexCarSys carSys2) {
        this.carSys2 = carSys2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IndexCarInfo> getCarInfoList() {
        return carInfoList;
    }

    public void setCarInfoList(List<IndexCarInfo> carInfoList) {
        this.carInfoList = carInfoList;
    }

    public IndexDealers getCarDealer() {
        return carDealer;
    }

    public void setCarDealer(IndexDealers carDealer) {
        this.carDealer = carDealer;
    }

    public boolean isTypeLast() {
        return typeLast;
    }

    public void setTypeLast(boolean typeLast) {
        this.typeLast = typeLast;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }
}
