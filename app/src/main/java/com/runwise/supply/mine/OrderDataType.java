package com.runwise.supply.mine;

/**
 * Created by libin on 2017/7/6.
 */

public enum OrderDataType {
    ALL("all"),
    BENZHOU("benzhou"),
    SHANGZHOU("shangzhou"),
    SHANGYUE("shangyue"),
    GENGZAO("gengzao");

    private final String type;

    OrderDataType(String type) {
        this.type =type;
    }

    public String getType() {
        return type;
    }
}