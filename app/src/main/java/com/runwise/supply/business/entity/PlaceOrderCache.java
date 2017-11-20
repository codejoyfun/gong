package com.runwise.supply.business.entity;

import com.runwise.supply.orderpage.entity.DefaultPBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2017/11/20.
 */

public class PlaceOrderCache implements Serializable{
    public List<DefaultPBean> getDefaultPBeans() {
        return defaultPBeans;
    }

    public void setDefaultPBeans(List<DefaultPBean> defaultPBeans) {
        this.defaultPBeans = defaultPBeans;
    }

    List<DefaultPBean> defaultPBeans;
}
