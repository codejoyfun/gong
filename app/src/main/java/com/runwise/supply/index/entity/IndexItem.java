package com.runwise.supply.index.entity;

/**
 * Created by myChaoFile on 16/11/1.
 */

public class IndexItem {
    private String id;
    private String paixu;//": "D01",
    private String px_name; //": "标准"
    private String params; //参数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaixu() {
        return paixu;
    }

    public void setPaixu(String paixu) {
        this.paixu = paixu;
    }

    public String getPx_name() {
        return px_name;
    }

    public void setPx_name(String px_name) {
        this.px_name = px_name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
