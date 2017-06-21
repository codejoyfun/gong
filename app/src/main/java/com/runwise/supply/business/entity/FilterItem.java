package com.runwise.supply.business.entity;

/**
 * Created by myChaoFile on 16/10/19.
 */

public class FilterItem {
    private boolean isSelect;
    private String id;
    private String name;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
