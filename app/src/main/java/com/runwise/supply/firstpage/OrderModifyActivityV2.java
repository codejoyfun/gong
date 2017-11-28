package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;

import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.OrderSubmitActivity;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.DefaultPBean;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;

import java.util.ArrayList;
import java.util.List;

import static com.runwise.supply.orderpage.OrderSubmitActivity.INTENT_KEY_PRODUCTS;

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
//            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(this).get(lb.getProductID());
            listBean.setProductID(lb.getProductID());
            listBean.setTracking(lb.getTracking());
            listBean.setProductUom(lb.getProductUom());
            listBean.setUnit(lb.getUnit());
            //新建的单都是newtype了
            listBean.setPrice(lb.getProductPrice());
            listBean.setSettlePrice(lb.getProductSettlePrice()+"");
            listBean.setImage(new ImageBean(lb.getImageMedium()));
//            if(bean.isNewType()){
//                listBean.setPrice(lb.getProductPrice());
//                listBean.setSettlePrice(lb.getProductSettlePrice()+"");
//                listBean.setImage(new ImageBean(lb.getImageMedium()));
//            }else if(basicBean!=null){
//                listBean.setPrice(basicBean.getPrice());
//                listBean.setSettlePrice(basicBean.getSettlePrice()+"");
//                listBean.setImage(basicBean.getImage());
//            }
            listBean.setName(lb.getName());
            listBean.setDefaultCode(lb.getDefaultCode());
            listBean.setCategory(lb.getCategory());
            listBean.setIsTwoUnit(lb.isTwoUnit());
            listBean.setStockType(lb.getStockType());
            mMapCount.put(listBean,(int) lb.getProductUomQty());
        }
        initSelectAll();
        super.onCreate(savedInstanceState);
        showCart(true);
        mTvOrderCommit.setText("确认修改");
    }

    @Override
    protected void getCache() {
        //不需要获取缓存
    }

    @Override
    protected void saveCache() {
        //不需要保存
    }

    @Override
    protected void onOkClicked() {

        Intent intent = new Intent(this,OrderSubmitActivity.class);
        ArrayList<ProductData.ListBean> list = new ArrayList<>();
        for(ProductData.ListBean bean:mMapCount.keySet()){
            if(bean.isInvalid() || mMapCount.get(bean)==0 || !mmSelected.contains(bean.getProductID()))continue;
            bean.setActualQty(mMapCount.get(bean));
            list.add(bean);
        }
        intent.putParcelableArrayListExtra(INTENT_KEY_PRODUCTS,list);
        intent.putExtra(OrderSubmitActivity.INTENT_KEY_ORDER,bean);
        startActivity(intent);
    }
}
