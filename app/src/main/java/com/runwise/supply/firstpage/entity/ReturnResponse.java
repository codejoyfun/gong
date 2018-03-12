package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by mike on 2018/3/12.
 */

public class ReturnResponse {
    private List<ReturnOrderBean.ListBean> list;

    public List<ReturnOrderBean.ListBean> getList() {
        return list;
    }

    public void setList(List<ReturnOrderBean.ListBean> list) {
        this.list = list;
    }
}
