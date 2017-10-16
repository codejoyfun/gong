package com.runwise.supply.entity;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 * Created by Dong on 2017/10/12.
 */

public class TransferDetailResponse {
    private List<OrderResponse.ListBean.LinesBean> lines;
    private TransferEntity info;

    public void setLines(List<OrderResponse.ListBean.LinesBean> lines) {
        this.lines = lines;
    }

    public List<OrderResponse.ListBean.LinesBean> getLines() {
        return lines;
    }
}
