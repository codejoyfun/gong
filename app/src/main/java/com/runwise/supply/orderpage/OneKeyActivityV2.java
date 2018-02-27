package com.runwise.supply.orderpage;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.util.SmartOrderTimestatisticsUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.OneKeyRequest;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;

/**
 * 智能下单，继承自手动下单
 *
 * Created by Dong on 2017/11/23.
 */
public class OneKeyActivityV2 extends ProductActivityV2 {

    public static final int REQUEST_PRESET = 20000;
    private double predict_sale_amount;
    private double yongliang_factor;
    private Handler mHandler = new Handler();
    @ViewInject(R.id.ttt)
    protected View ttt;
    @ViewInject(R.id.loadingImg)
    protected ImageView loadingImg;
    @ViewInject(R.id.rl_bottom_bar)
    protected RelativeLayout mRlBottomBar;

    //----------用于显示动画--------------
    private int currentIndex;
    int[] loadingImgs = new int[31];
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loadingImg.setImageResource(loadingImgs[currentIndex++]);
            if (currentIndex >= 31) {
                currentIndex = 0;
            }
            mHandler.postDelayed(runnable, 30);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        predict_sale_amount = getIntent().getDoubleExtra("predict_sale_amount", 0);
        yongliang_factor = getIntent().getDoubleExtra("yongliang_factor", 0) / 100;
        super.onCreate(savedInstanceState);
        setTitleText(true,"智能下单");
        ViewUtils.inject(this);
        initLoadingImgs();
        mPlaceOrderType = PLACE_ORDER_TYPE_SMART;
    }

    /**
     * 在父类onCreate最后被调用
     */
    @Override
    protected void startRequest() {
        requestPresent();
    }

    private void requestPresent(){
        //开始动画
        ttt.setVisibility(View.VISIBLE);
        mRlBottomBar.setVisibility(View.GONE);
        mHandler.post(runnable);
        OneKeyRequest request = new OneKeyRequest();
        request.setPredict_sale_amount(predict_sale_amount);
        request.setYongliang_factor(yongliang_factor);
        sendConnection("/gongfu/v2/shop/preset/product/list", request, REQUEST_PRESET, false, PresetProductData.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        super.onSuccess(result,where);
        switch (where){
            case REQUEST_PRESET:
                //停止动画
                mHandler.removeCallbacks(runnable);
                ttt.setVisibility(View.GONE);
                mRlBottomBar.setVisibility(View.VISIBLE);
                BaseEntity.ResultBean resultBean = result.getResult();
                PresetProductData data = (PresetProductData) resultBean.getData();
                //init mCountMap;
                if(data.getList()==null || data.getList().size()==0){
                    Toast.makeText(this,"小主，暂时不用采购哦~",Toast.LENGTH_LONG).show();
                }else{
                    for(ProductBasicList.ListBean pBean: data.getList()){
                        mMapCount.put(pBean,pBean.getPresetQty());
                    }
                }
                requestCategory();
                initSelectAll();
                updateBottomBar();
                showCart(true);
                mFirstGetShopCartCache = false;
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        super.onFailure(errMsg,result,where);
        switch (where){
            case REQUEST_PRESET:
                //停止动画
                mHandler.removeCallbacks(runnable);
                ttt.setVisibility(View.GONE);
                mRlBottomBar.setVisibility(View.VISIBLE);
                requestCategory();
                updateBottomBar();
//                Toast.makeText(this,"小主，暂时不用采购哦~",Toast.LENGTH_LONG).show();
                mFirstGetShopCartCache = false;
                break;
        }
    }

    /**
     * 智能下单不需要缓存
     */
    @Override
    protected void saveCache() {
        checkValid(getSelectProductList());
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
    /**
     * 智能下单不需要缓存
     */
    @Override
    protected void getCache() {}

    private void initLoadingImgs() {
        StringBuffer sb;
        for (int i = 0; i < 31; i++) {
            sb = new StringBuffer("order_loading_");
            sb.append(i);
            loadingImgs[i] = getResIdByDrawableName(sb.toString());
        }
    }

    private int getResIdByDrawableName(String name) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }
    @Override
    protected String getPageName() {
        return "智能下单页面";
    }

    @Override
    protected void statisticsOrderTimeOnResume() {
        SmartOrderTimestatisticsUtil.onResume();
    }

    @Override
    protected void statisticsOrderTimeOnPause() {
        SmartOrderTimestatisticsUtil.onPause(getActivityContext());
    }
}
