package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.OrderSubmitActivity;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.umeng.analytics.MobclickAgent;

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
        mPlaceOrderType = PLACE_ORDER_TYPE_MODIFY;
        bean = getIntent().getExtras().getParcelable("order");
        //初始化商品数据
        List<OrderResponse.ListBean.LinesBean> list = bean.getLines();
        List<ProductBasicList.ListBean> listBeans = new ArrayList<>();
        for (OrderResponse.ListBean.LinesBean lb : list) {
            ProductBasicList.ListBean listBean = new ProductBasicList.ListBean();
//            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(this).get(lb.getProductID());
            listBean.setProductID(lb.getProductID());
            listBean.setTracking(lb.getTracking());
            listBean.setProductUom(lb.getProductUom());
            listBean.setUom(lb.getProductUom());
            listBean.setUnit(lb.getUnit());
            //新建的单都是newtype了
            listBean.setPrice(lb.getProductPrice());
            listBean.setSettlePrice((float) lb.getProductSettlePrice());
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
            listBean.setStockType(lb.getStockType());
            listBean.setRemark(lb.getRemark());
            listBean.setCategoryParent(lb.getCategoryParent());
            listBean.setCategoryChild(lb.getCategoryChild());

            listBeans.add(listBean);
            mMapCount.put(listBean,lb.getProductUomQty());
            mMapRemarks.put(listBean,lb.getRemark());
        }
        initSelectAll();
        super.onCreate(savedInstanceState);
        showCart(true);
        mTvOrderCommit.setText("确认修改");
        checkValid(listBeans);
    }

    @Override
    protected void getCache() {
        //不需要获取缓存
        checkValid(getSelectProductList());
    }

    @Override
    protected void saveCache() {
        //不需要保存
    }

    private ArrayList<ProductBasicList.ListBean> getSelectProductList(){
        ArrayList<ProductBasicList.ListBean> list = new ArrayList<>();
        for(ProductBasicList.ListBean bean:mMapCount.keySet()){
            if(bean.isInvalid() || mMapCount.get(bean)==0 || !mmSelected.contains(bean.getProductID()))continue;
            bean.setActualQty(mMapCount.get(bean));
            bean.setRemark(mMapRemarks.get(bean));
            list.add(bean);
        }
        return list;
    }

    @Override
    protected void onOkClicked() {
        Intent intent = new Intent(this,OrderSubmitActivity.class);
        ArrayList<ProductBasicList.ListBean> list = getSelectProductList();
        intent.putParcelableArrayListExtra(INTENT_KEY_PRODUCTS,list);
        intent.putExtra(OrderSubmitActivity.INTENT_KEY_ORDER, (Parcelable) bean);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("订单修改页");
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("订单修改页");
        MobclickAgent.onPause(this);          //统计时长
    }
}
