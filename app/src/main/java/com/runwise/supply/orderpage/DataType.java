package com.runwise.supply.orderpage;

/**
 * Created by libin on 2017/7/6.
 */

public enum DataType{
        ALL("all"),
    LENGCANGHUO("lengcanghuo"),
    FREEZE("donghuo"),
    DRY("ganhuo");

    private final String type;

    DataType(String type) {
        this.type =type;
    }

    public String getType() {
        return type;
    }
}