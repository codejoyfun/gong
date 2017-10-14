package com.runwise.supply.entity;

/**
 * 调度单状态
 *
 * Created by Dong on 2017/10/14.
 */

public class TransferStateEntitiy {
    private String confirmTime;
    private String changeTime;
    private String sentTime;
    private String receiveTime;

    public String getConfirmTime() {
        return confirmTime;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public String getSentTime() {
        return sentTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }
}
