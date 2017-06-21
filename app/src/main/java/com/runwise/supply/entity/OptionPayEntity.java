package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class OptionPayEntity {
    private String period_id;//": 1,
    private String name;//": "12期",
    private String first_period;//": "3.60",
    private String month_period;//": "1400.00"

    public String getPeriod_id() {
        return period_id;
    }

    public void setPeriod_id(String period_id) {
        this.period_id = period_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
