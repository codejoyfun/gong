package com.runwise.supply.orderpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * 再来一单
 *
 * Created by Dong on 2017/11/24.
 */

public class OrderAgainActivity extends ProductActivityV2 {
    public static final String INTENT_KEY_ORDER_AGAIN = "order_again";
    OrderResponse.ListBean mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mOrder = getIntent().getParcelableExtra(INTENT_KEY_ORDER_AGAIN);
        initData();
        initSelectAll();
        super.onCreate(savedInstanceState);
        showCart(true);
        mPlaceOrderType = PLACE_ORDER_TYPE_AGAIN;
    }

    /**
     * 初始化订单数据
     */
    protected void initData(){
        List<OrderResponse.ListBean.LinesBean> list = mOrder.getLines();
        for (OrderResponse.ListBean.LinesBean lb : list) {
            ProductData.ListBean listBean = new ProductData.ListBean();
            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(this).get(lb.getProductID());
            listBean.setProductID(lb.getProductID());
            listBean.setTracking(lb.getTracking());
            listBean.setProductUom(lb.getProductUom());
            listBean.setUnit(lb.getUnit());
            listBean.setRemark(lb.getRemark());
            if(mOrder.isNewType()){
                listBean.setPrice(lb.getProductPrice());
                listBean.setSettlePrice(lb.getProductSettlePrice()+"");
                listBean.setImage(new ImageBean(lb.getImageMedium()));
                listBean.setUom(lb.getProductUom());
            }else if(basicBean!=null){
                listBean.setPrice(basicBean.getPrice());
                listBean.setSettlePrice(basicBean.getSettlePrice()+"");
                listBean.setImage(basicBean.getImage());
                listBean.setUom(basicBean.getUom());
            }
            listBean.setName(lb.getName());
            listBean.setDefaultCode(lb.getDefaultCode());
            listBean.setCategory(lb.getCategory());
            listBean.setIsTwoUnit(lb.isTwoUnit());
            listBean.setStockType(lb.getStockType());
            mMapCount.put(listBean,lb.getProductUomQty());
            mMapRemarks.put(listBean,lb.getRemark());
        }
    }

    @Override
    protected void getCache() {
        //empty,不需要缓存
    }

    @Override
    protected void saveCache() {
        //empty，不需要缓存
    }

    public static void start(Activity activity, OrderResponse.ListBean order){
        Intent intent = new Intent(activity,OrderAgainActivity.class);
        intent.putExtra(INTENT_KEY_ORDER_AGAIN, (Parcelable) order);
        activity.startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("再来一单页面");
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("再来一单页面");
        MobclickAgent.onPause(this);          //统计时长
    }
}
