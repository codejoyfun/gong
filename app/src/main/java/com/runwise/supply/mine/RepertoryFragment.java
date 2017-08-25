package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.UpdateRepertory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存列表
 */

public class RepertoryFragment extends NetWorkFragment {
    private final int PRODUCT_GET = 1;

    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TabPageIndicatorAdapter(this.getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        boolean isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            requestData();
        }
        else{
            buildData();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateRepertory event) {
        requestData();
    }
    private void requestData() {
        sendConnection("/gongfu/shop/stock/list",null,PRODUCT_GET,true, RepertoryEntity.class);
    }
    private  void buildData() {
        String xmlStr = "{\"state\": \"A0006\", \"list\": [{\"product\": {\"name\": \"\\u624b\\u6495\\u9e21\", \"image\": {\"image\": \"/gongfu/image/product/8/image/\", \"image_medium\": \"/gongfu/image/product/8/image_medium/\", \"image_small\": \"/gongfu/image/product/8/image_small/\"}, \"barcode\": \"\", \"stock_type\": \"lengcanghuo\", \"default_code\": \"11012201\", \"id\": 8, \"unit\": \"1kg/\\u5305\"}, \"qty\": 10.0, \"life_end_date\": \"2017-07-08 16:48:33\", \"inventory_value\": 50.0, \"lot_num\": \"Z201706081018\", \"lot_id\": 38, \"uom\": \"\\u5305\"}, {\"product\": {\"name\": \"\\u9171\\u725b\\u8089\", \"image\": {\"image\": \"/gongfu/image/product/9/image/\", \"image_medium\": \"/gongfu/image/product/9/image_medium/\", \"image_small\": \"/gongfu/image/product/9/image_small/\"}, \"barcode\": \"\", \"stock_type\": \"lengcanghuo\", \"default_code\": \"11012202\", \"id\": 9, \"unit\": \"1kg/\\u5305\"}, \"qty\": 18.0, \"life_end_date\": \"2017-07-08 16:51:26\", \"inventory_value\": 135.0, \"lot_num\": \"Z201706081026\", \"lot_id\": 46, \"uom\": \"\\u5305\"}, {\"product\": {\"name\": \"\\u9999\\u6ea2\\u5bb6\\u67ec\\u91d1\\u9999\\u7c73\", \"image\": {\"image\": \"/gongfu/image/product/11/image/\", \"image_medium\": \"/gongfu/image/product/11/image_medium/\", \"image_small\": \"/gongfu/image/product/11/image_small/\"}, \"barcode\": \"\", \"stock_type\": \"ganhuo\", \"default_code\": \"11012204\", \"id\": 11, \"unit\": \"15kg/\\u888b\"}, \"qty\": 14.0, \"life_end_date\": \"2017-08-07 16:51:41\", \"inventory_value\": 560.0, \"lot_num\": \"Z201706081027\", \"lot_id\": 47, \"uom\": \"\\u888b\"}, {\"product\": {\"name\": \"\\u9171\\u725b\\u8089\", \"image\": {\"image\": \"/gongfu/image/product/9/image/\", \"image_medium\": \"/gongfu/image/product/9/image_medium/\", \"image_small\": \"/gongfu/image/product/9/image_small/\"}, \"barcode\": \"\", \"stock_type\": \"lengcanghuo\", \"default_code\": \"11012202\", \"id\": 9, \"unit\": \"1kg/\\u5305\"}, \"qty\": 4.0, \"life_end_date\": \"2017-08-16 11:39:32\", \"inventory_value\": 30.0, \"lot_num\": \"Z201707171923\", \"lot_id\": 85, \"uom\": \"\\u5305\"}, {\"product\": {\"name\": \"\\u3010\\u4e94\\u5f97\\u5229\\u3011\\u9ad8\\u7b4b\\u5c0f\\u9ea6\\u7c89\", \"image\": {\"image\": \"/gongfu/image/product/15/image/\", \"image_medium\": \"/gongfu/image/product/15/image_medium/\", \"image_small\": \"/gongfu/image/product/15/image_small/\"}, \"barcode\": \"\", \"stock_type\": \"ganhuo\", \"default_code\": \"11012208\", \"id\": 15, \"unit\": \"25kg/\\u888b\"}, \"qty\": 54.0, \"life_end_date\": \"2017-09-16 16:47:27\", \"inventory_value\": 2160.0, \"lot_num\": \"Z201706081014\", \"lot_id\": 34, \"uom\": \"\\u888b\"}, {\"product\": {\"name\": \"\\u5143\\u5b9d\\u8c03\\u548c\\u6cb9\", \"image\": {\"image\": \"/gongfu/image/product/12/image/\", \"image_medium\": \"/gongfu/image/product/12/image_medium/\", \"image_small\": \"/gongfu/image/product/12/image_small/\"}, \"barcode\": \"\", \"stock_type\": \"ganhuo\", \"default_code\": \"11012205\", \"id\": 12, \"unit\": \"37\\u65a4/\\u6876\\uff0c\\u6bcf\\u68762\\u5347\"}, \"qty\": 18.0, \"life_end_date\": \"2017-09-16 16:47:43\", \"inventory_value\": 900.0, \"lot_num\": \"Z201706081015\", \"lot_id\": 35, \"uom\": \"\\u6876\"}, {\"product\": {\"name\": \"\\u6cf0\\u91d1\\u9999\\u7389\\u5170\\u9999\\u7c73\", \"image\": {\"image\": \"/gongfu/image/product/13/image/\", \"image_medium\": \"/gongfu/image/product/13/image_medium/\", \"image_small\": \"/gongfu/image/product/13/image_small/\"}, \"barcode\": \"\", \"stock_type\": \"ganhuo\", \"default_code\": \"11012206\", \"id\": 13, \"unit\": \"25kg/\\u888b\"}, \"qty\": 1.0, \"life_end_date\": \"2017-09-24 12:56:14\", \"inventory_value\": 50.0, \"lot_num\": \"Z201706161561\", \"lot_id\": 61, \"uom\": \"\\u888b\"}]}";
        RepertoryEntity repertoryEntity =  JSON.parseObject(xmlStr,RepertoryEntity.class);
        for(RepertoryListFragment fragment : adapter.getFragmentList()){
            fragment.onDataSynEvent(repertoryEntity);
        }
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        requestData();
    }

    @Override
    public void onUserLoginout() {
        buildData();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_repertory_layout;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case PRODUCT_GET:
                RepertoryEntity repertoryEntity = (RepertoryEntity)result.getResult();
                List<RepertoryEntity.ListBean> productList = repertoryEntity.getList();
                for(RepertoryEntity.ListBean bean : productList) {
                    ProductBasicList.ListBean baseProduct = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProduct().getId()));
                    if( baseProduct == null ) {
                        RepertoryEntity.ListBean.ProductBean oldProduct = bean.getProduct();
                        ProductBasicList.ListBean product = new ProductBasicList.ListBean();
                        product.setProductID(oldProduct.getId());
                        product.setName(oldProduct.getName());
                        product.setBarcode(oldProduct.getBarcode());
                        product.setStockType(oldProduct.getStock_type());
                        product.setDefaultCode(oldProduct.getDefault_code());
                        product.setUnit(oldProduct.getUnit());
                        ImageBean imageBean = new ImageBean();
                        imageBean.setImage(oldProduct.getImage().getImage());
                        imageBean.setImageSmall(oldProduct.getImage().getImage_small());
                        imageBean.setImageMedium(oldProduct.getImage().getImage_medium());
                        product.setImage(imageBean);
                        ProductBasicUtils.getBasicMap(mContext).put(oldProduct.getId()+"",product);
                    }
                }
                EventBus.getDefault().post(repertoryEntity);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<RepertoryListFragment> fragmentList = new ArrayList<>();
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            titleList.add("全部");
            titleList.add("冷藏货");
            titleList.add("冻货");
            titleList.add("干货");
            Bundle bundle = new Bundle();
            RepertoryListFragment allFragment = new RepertoryListFragment();
            allFragment.type = DataType.ALL;
            allFragment.setArguments(bundle);
            RepertoryListFragment coldFragment = new RepertoryListFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            coldFragment.setArguments(bundle);
            RepertoryListFragment freezeFragment = new RepertoryListFragment();
            freezeFragment.type = DataType.FREEZE;
            freezeFragment.setArguments(bundle);
            RepertoryListFragment dryFragment = new RepertoryListFragment();
            dryFragment.type = DataType.DRY;
            dryFragment.setArguments(bundle);

            fragmentList.add(allFragment);
            fragmentList.add(coldFragment);
            fragmentList.add(freezeFragment);
            fragmentList.add(dryFragment);
        }
        public List<RepertoryListFragment> getFragmentList() {
            return  fragmentList;
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }
    }

}
