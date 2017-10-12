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

    private String transferId;
    private String createTime;
    private String state;
    private String from;
    private String to;
    private List<OrderResponse.ListBean.LinesBean> lines;

    public String getTransferId() {
        return transferId;
    }

    public String getState() {
        return state;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<OrderResponse.ListBean.LinesBean> getLines() {
        return lines;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setLines(List<OrderResponse.ListBean.LinesBean> lines) {
        this.lines = lines;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
