package com.runwise.supply.orderpage;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2017/7/6.
 */

public class ProductBasicUtils {
    //productId -> 基本信息对象,便于后续使用查询
    private static HashMap<String,ProductBasicList.ListBean> basicMap = new HashMap<>();
    //便于搜索条件
    private static List<ProductBasicList.ListBean> basicArr = new ArrayList<>();

    public static HashMap<String, ProductBasicList.ListBean> getBasicMap() {
        return basicMap;
    }

    public static void setBasicMap(HashMap<String, ProductBasicList.ListBean> basicMap) {
        ProductBasicUtils.basicMap = basicMap;
    }

    public static List<ProductBasicList.ListBean> getBasicArr() {
        return basicArr;
    }

    public static void setBasicArr(List<ProductBasicList.ListBean> basicArr) {
        ProductBasicUtils.basicArr = basicArr;
    }
}
