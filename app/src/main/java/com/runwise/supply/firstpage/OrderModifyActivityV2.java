package com.runwise.supply.firstpage;

import android.os.Bundle;

import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.DefaultPBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的修改订单流程
 * 
 * Created by Dong on 2017/11/23.
 */

public class OrderModifyActivityV2 extends ProductActivityV2 {
    public static final String INTENT_KEY_ORDER = "order";
    OrderResponse.ListBean bean;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bean = getIntent().getExtras().getParcelable("order");
        //初始化商品数据
        List<OrderResponse.ListBean.LinesBean> list = bean.getLines();
        for (OrderResponse.ListBean.LinesBean lb : list) {
            ProductData.ListBean listBean = new ProductData.ListBean();
            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(this).get(lb.getProductID());
            listBean.setProductID(lb.getProductID());
            listBean.setTracking(lb.getTracking());
            listBean.setProductUom(lb.getProductUom());
            listBean.setUnit(lb.getUnit());
            if(bean.isNewType()){
                listBean.setPrice(lb.getProductPrice());
                listBean.setSettlePrice(lb.getProductSettlePrice()+"");
            }else if(basicBean!=null){
                listBean.setPrice(basicBean.getPrice());
                listBean.setSettlePrice(basicBean.getSettlePrice()+"");
                listBean.setImage(basicBean.getImage());
            }
            listBean.setName(lb.getName());
            listBean.setDefaultCode(lb.getDefaultCode());
            listBean.setCategory(lb.getCategory());
            listBean.setIsTwoUnit(lb.isTwoUnit());
            listBean.setStockType(lb.getStockType());
            mMapCount.put(listBean,(int) lb.getProductUomQty());
        }
        super.onCreate(savedInstanceState);
        setTitleText(true, bean.getName());
        updateBottomBar();
    }

    @Override
    protected void getCache() {
        //不需要获取缓存
    }

    @Override
    protected void saveCache() {
        //不需要保存
    }
}
