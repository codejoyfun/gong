package com.runwise.supply.orderpage;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kids.commonframe.base.BaseEntity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.ProductData;
import com.umeng.analytics.MobclickAgent;

/**
 * 常购订单，类似智能下单的逻辑，继承自手动下单
 *
 * Created by Dong on 2017/11/23.
 */
public class AlwaysOrderActivity extends ProductActivityV2 {

    public static final int REQUEST_ALWAYS = 20000;
    private Handler mHandler = new Handler();
    @ViewInject(R.id.ttt)
    protected View mViewAnim;
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
        super.onCreate(savedInstanceState);
        setTitleText(true,"常购清单");
        ViewUtils.inject(this);
        initLoadingImgs();
    }

    /**
     * 在父类onCreate最后被调用
     */
    @Override
    protected void startRequest() {
        requestPresent();
    }

    /**
     * 请求预设数据
     */
    private void requestPresent(){
        //开始动画
        mViewAnim.setVisibility(View.VISIBLE);
        mRlBottomBar.setVisibility(View.GONE);
        mHandler.post(runnable);
        Object request = null;
        sendConnection("/api/shop/always_buy/product/list",request,REQUEST_ALWAYS,false,PresetProductData.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        super.onSuccess(result,where);
        switch (where){
            case REQUEST_ALWAYS:
                //停止动画
                mHandler.removeCallbacks(runnable);
                mViewAnim.setVisibility(View.GONE);
                mRlBottomBar.setVisibility(View.VISIBLE);
                BaseEntity.ResultBean resultBean = result.getResult();
                PresetProductData data = (PresetProductData) resultBean.getData();
                //init mCountMap;
                if(data.getList()==null || data.getList().size()==0){
                    //Toast.makeText(this,"小主，暂时不用采购哦~",Toast.LENGTH_LONG).show();
                }else{
                    for(ProductData.ListBean pBean: data.getList()){
                        mMapCount.put(pBean,pBean.getPresetQty());
                    }
                }
                requestCategory();
                initSelectAll();
                updateBottomBar();
                showCart(true);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        super.onFailure(errMsg,result,where);
        switch (where){
            case REQUEST_ALWAYS:
                //停止动画
                mHandler.removeCallbacks(runnable);
                mViewAnim.setVisibility(View.GONE);
                mRlBottomBar.setVisibility(View.VISIBLE);
                requestCategory();
                updateBottomBar();
                Toast.makeText(this,"小主，暂时不用采购哦~",Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * 智能下单不需要缓存
     */
    @Override
    protected void saveCache() {}

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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("常购清单页面");
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("常购清单页面");
    }
}
