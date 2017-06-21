package com.runwise.supply.index.entity;

/**
 * Created by myChaoFile on 17/1/6.
 */

public class CarPeriod {
    private String period_id;//": 1,
    private String first_period;//": "3.60",
    private String month_period;//": "2800.00

    public String getPeriod_id() {
        return period_id;
    }

    public void setPeriod_id(String period_id) {
        this.period_id = period_id;
    }

    public String getFirst_period() {
        return first_period;
    }

    public void setFirst_period(String first_period) {
        this.first_period = first_period;
    }

    public String getMonth_period() {
        return month_period;
    }

    public void setMonth_period(String month_period) {
        this.month_period = month_period;
    }
}
