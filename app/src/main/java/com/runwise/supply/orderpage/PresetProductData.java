package com.runwise.supply.orderpage;

import android.os.Parcelable;

import com.runwise.supply.orderpage.entity.DefaultPBean;
import com.runwise.supply.orderpage.entity.ProductData;

import java.util.List;

/**
 * Created by Dong on 2017/11/24.
 */

public class PresetProductData {
    private List<ProductData.ListBean> list;

    public List<ProductData.ListBean> getList() {
        return list;
    }

    public void setList(List<ProductData.ListBean> list) {
        this.list = list;
    }

}
