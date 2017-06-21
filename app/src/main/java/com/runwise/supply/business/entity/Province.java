package com.runwise.supply.business.entity;

/**
 * Created by myChaoFile on 16/10/31.
 */

public class Province {
    private String province_id;//": "11",
    private String name;//": "北京"
    private boolean isSelect;

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
