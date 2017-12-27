package com.runwise.supply.entity;

/**
 * Created by Dong on 2017/12/25.
 */

public class UpdateTimeResponse {
    String startDate;//yyyy-MM-dd hh:mm:ss
    String endDate;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
