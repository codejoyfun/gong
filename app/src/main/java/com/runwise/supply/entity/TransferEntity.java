package com.runwise.supply.entity;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 * Fake scheme
 *
 * Created by Dong on 2017/10/10.
 */

public class TransferEntity {

    //调拨单状态
    public static final String STATE_SUBMITTED = "";//已提交
    public static final String STATE_PENDING_DELIVER = "";//待出库
    public static final String STATE_DELIVER = "";//已发出
    public static final String STATE_COMPLETE = "";//完成

    private String pickingID;
    private String pickingName;
    private String date;
    private String pickingState;
    private String locationName;
    private String locationDestName;
    private List<OrderResponse.ListBean.LinesBean> lines;
    private float totalPrice;
    private int totalNum;

    public String getPickingID() {
        return pickingID;
    }

    public String getPickingState() {
        return pickingState;
    }

    public String getPickingName() {
        return pickingName;
    }

    public String getDate() {
        return date;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationDestName() {
        return locationDestName;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public List<OrderResponse.ListBean.LinesBean> getLines() {
        return lines;
    }

    public void setPickingID(String pickingID) {
        this.pickingID = pickingID;
    }

    public void setPickingState(String pickingState) {
        this.pickingState = pickingState;
    }

    public void setLines(List<OrderResponse.ListBean.LinesBean> lines) {
        this.lines = lines;
    }

    public void setPickingName(String pickingName) {
        this.pickingName = pickingName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationDestName(String locationDestName) {
        this.locationDestName = locationDestName;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
}
