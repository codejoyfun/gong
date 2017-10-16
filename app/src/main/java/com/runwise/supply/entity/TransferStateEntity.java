package com.runwise.supply.entity;

/**
 * 调度单状态
 *
 * Created by Dong on 2017/10/14.
 */

public class TransferStateEntity {
    private String state;
    private String operator;
    private String operateTime;
    private String locations;
    private String pickingName;
    private String receiveInfo;//实际收货
    private String transferInfo;//调拨商品
    private String content;

    public String getState() {
        return state;
    }

    public String getOperator() {
        return operator;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public String getLocations() {
        return locations;
    }

    public String getPickingName() {
        return pickingName;
    }

    public String getReceiveInfo() {
        return receiveInfo;
    }

    public String getTransferInfo() {
        return transferInfo;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public void setPickingName(String pickingName) {
        this.pickingName = pickingName;
    }

    public void setReceiveInfo(String receiveInfo) {
        this.receiveInfo = receiveInfo;
    }

    public void setTransferInfo(String transferInfo) {
        this.transferInfo = transferInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
