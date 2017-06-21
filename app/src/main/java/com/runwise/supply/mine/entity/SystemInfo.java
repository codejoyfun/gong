package com.runwise.supply.mine.entity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class SystemInfo {
    private String company_phone;//": "010-68345678",     //联系电话
    private String sys_time;//": 1482844287    //服务器时间

    public String getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(String company_phone) {
        this.company_phone = company_phone;
    }

    public String getSys_time() {
        return sys_time;
    }

    public void setSys_time(String sys_time) {
        this.sys_time = sys_time;
    }
}
