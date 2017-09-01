package com.runwise.supply.mine;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.mine.entity.RefreshPepertoy;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
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
    private final int PRODUCT_DETAIL = 2;


    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;

    private List<RepertoryEntity.ListBean> productList;
    private RepertoryEntity repertoryEntity;
    boolean isLogin;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TabPageIndicatorAdapter(this.getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            requestData();
        }
        else{
            buildData();
        }
    }

    private void getProductDetail(int productId) {
        sendConnection("/gongfu/v2/product/"+productId,null,PRODUCT_DETAIL,false, ProductOne.class);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateRepertory event) {
        requestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPullEvent(RefreshPepertoy event) {
        if(isLogin) {
            requestData();
        }
        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    buildData();
                }
            }, 1000);
        }
    }
    private void requestData() {
        sendConnection("/api/stock/list",null,PRODUCT_GET,true, RepertoryEntity.class);
    }
    private  void buildData() {
        String xmlStr = "{\n" +
                "    \"list\": [\n" +
                "        {\n" +
                "            \"code\": \"11012215\",\n" +
                "            \"inventoryValue\": 108,\n" +
                "            \"lifeEndDate\": \"2017-07-27 00:00:00\",\n" +
                "            \"lotID\": 287,\n" +
                "            \"lotNum\": \"Z170827000010\",\n" +
                "            \"qty\": 18,\n" +
                "            \"uom\": \"包\",\n" +
                "            \"productID\": 20,\n" +
                "            \"product\": {\n" +
                "                \"isTwoUnit\": false,\n" +
                "                \"image\": {\n" +
                "                    \"imageMedium\": \"/gongfu/image/product/20/image_medium/\",\n" +
                "                    \"image\": \"/gongfu/image/product/20/image/\",\n" +
                "                    \"imageSmall\": \"/gongfu/image/product/20/image_small/\",\n" +
                "                    \"imageID\": 20\n" +
                "                },\n" +
                "                \"barcode\": \"\",\n" +
                "                \"settlePrice\": 0,\n" +
                "                \"unit\": \"300g/袋\",\n" +
                "                \"productID\": 20,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"一号灌汤包馅料\",\n" +
                "                \"defaultCode\": \"11012213\",\n" +
                "                \"stockType\": \"donghuo\",\n" +
                "                \"settleUomId\": \"\",\n" +
                "                \"price\": 15.200000000000001,\n" +
                "                \"uom\": \"包\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": \"11012215\",\n" +
                "            \"inventoryValue\": 180,\n" +
                "            \"lifeEndDate\": \"2017-07-27 00:00:00\",\n" +
                "            \"lotID\": 286,\n" +
                "            \"lotNum\": \"rose\",\n" +
                "            \"qty\": 30,\n" +
                "            \"uom\": \"包\",\n" +
                "            \"productID\": 20,\n" +
                "            \"product\": {\n" +
                "                \"isTwoUnit\": false,\n" +
                "                \"image\": {\n" +
                "                    \"imageMedium\": \"/gongfu/image/product/21/image_medium/\",\n" +
                "                    \"image\": \"/gongfu/image/product/21/image/\",\n" +
                "                    \"imageSmall\": \"/gongfu/image/product/21/image_small/\",\n" +
                "                    \"imageID\": 21\n" +
                "                },\n" +
                "                \"barcode\": \"\",\n" +
                "                \"settlePrice\": 0,\n" +
                "                \"unit\": \"200g/袋\",\n" +
                "                \"productID\": 21,\n" +
                "                \"tracking\": \"none\",\n" +
                "                \"name\": \"一次性碗 - 直无批\",\n" +
                "                \"defaultCode\": \"11012214\",\n" +
                "                \"stockType\": \"other\",\n" +
                "                \"settleUomId\": \"\",\n" +
                "                \"price\": 1.11,\n" +
                "                \"uom\": \"件\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": \"11012215\",\n" +
                "            \"inventoryValue\": 10,\n" +
                "            \"lifeEndDate\": \"2017-08-26 00:00:00\",\n" +
                "            \"lotID\": 275,\n" +
                "            \"lotNum\": \"ghhui\",\n" +
                "            \"qty\": 5,\n" +
                "            \"uom\": \"包\",\n" +
                "            \"productID\": 22,\n" +
                "            \"product\": {\n" +
                "                \"isTwoUnit\": false,\n" +
                "                \"image\": {\n" +
                "                    \"imageMedium\": \"/gongfu/image/product/22/image_medium/\",\n" +
                "                    \"image\": \"/gongfu/image/product/22/image/\",\n" +
                "                    \"imageSmall\": \"/gongfu/image/product/22/image_small/\",\n" +
                "                    \"imageID\": 22\n" +
                "                },\n" +
                "                \"barcode\": \"\",\n" +
                "                \"settlePrice\": 0,\n" +
                "                \"unit\": \"200g/袋\",\n" +
                "                \"productID\": 22,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"中盐牌加碘精制盐\",\n" +
                "                \"defaultCode\": \"11012215\",\n" +
                "                \"stockType\": \"ganhuo\",\n" +
                "                \"settleUomId\": \"\",\n" +
                "                \"price\": 58.910000000000004,\n" +
                "                \"uom\": \"包\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": \"11012215\",\n" +
                "            \"inventoryValue\": 75,\n" +
                "            \"lifeEndDate\": \"2017-09-27 00:00:00\",\n" +
                "            \"lotID\": 282,\n" +
                "            \"lotNum\": \"Z170827000004\",\n" +
                "            \"qty\": 15,\n" +
                "            \"uom\": \"包\",\n" +
                "            \"productID\": 27,\n" +
                "            \"product\": {\n" +
                "                \"isTwoUnit\": false,\n" +
                "                \"image\": {\n" +
                "                    \"imageMedium\": \"/gongfu/image/product/23/image_medium/\",\n" +
                "                    \"image\": \"/gongfu/image/product/23/image/\",\n" +
                "                    \"imageSmall\": \"/gongfu/image/product/23/image_small/\",\n" +
                "                    \"imageID\": 23\n" +
                "                },\n" +
                "                \"barcode\": \"\",\n" +
                "                \"settlePrice\": 0,\n" +
                "                \"unit\": \"200g/袋\",\n" +
                "                \"productID\": 23,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"【五得利】高筋小麦粉\",\n" +
                "                \"defaultCode\": \"11012216\",\n" +
                "                \"stockType\": \"ganhuo\",\n" +
                "                \"settleUomId\": \"\",\n" +
                "                \"price\": 104.48,\n" +
                "                \"uom\": \"包\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": \"11012215\",\n" +
                "            \"inventoryValue\": 50,\n" +
                "            \"lifeEndDate\": \"2017-11-22 17:46:42\",\n" +
                "            \"lotID\": 1,\n" +
                "            \"lotNum\": \"Z201708140001\",\n" +
                "            \"qty\": 1,\n" +
                "            \"uom\": \"袋\",\n" +
                "            \"productID\": 29,\n" +
                "            \"product\": {\n" +
                "                \"isTwoUnit\": false,\n" +
                "                \"image\": {\n" +
                "                    \"imageMedium\": \"/gongfu/image/product/24/image_medium/\",\n" +
                "                    \"image\": \"/gongfu/image/product/24/image/\",\n" +
                "                    \"imageSmall\": \"/gongfu/image/product/24/image_small/\",\n" +
                "                    \"imageID\": 24\n" +
                "                },\n" +
                "                \"barcode\": \"\",\n" +
                "                \"settlePrice\": 0,\n" +
                "                \"unit\": \"500g/桶\",\n" +
                "                \"productID\": 24,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"元宝调和油\",\n" +
                "                \"defaultCode\": \"11012217\",\n" +
                "                \"stockType\": \"ganhuo\",\n" +
                "                \"settleUomId\": \"\",\n" +
                "                \"price\": 100.04,\n" +
                "                \"uom\": \"桶\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
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
                repertoryEntity = (RepertoryEntity)result.getResult().getData();
                productList = repertoryEntity.getList();
                boolean nullProductExit = false;
                for(RepertoryEntity.ListBean bean : productList) {
                    ProductBasicList.ListBean baseProduct = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
                    if( baseProduct == null ) {
                        getProductDetail(bean.getProductID());
                        nullProductExit = true;
                    }
                    else{
                        bean.setProduct(baseProduct);
                    }
                }
                if(!nullProductExit) {
                    EventBus.getDefault().post(repertoryEntity);
                }
                break;
            case PRODUCT_DETAIL:
                ProductOne productOne = (ProductOne) result.getResult().getData();
                for(RepertoryEntity.ListBean bean : productList) {
                    if(productOne.getProduct().getProductID() == bean.getProductID()) {
                        bean.setProduct(productOne.getProduct());
                        ProductBasicUtils.getBasicMap(mContext).put(productOne.getProduct().getProductID()+"",productOne.getProduct());
                        break;
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

    @Override
    public void onEventUserlogin(UserLoginEvent userLoginEvent) {
        isLogin = true;
    }

    @Override
    public void onEventUserlogout(UserLogoutEvent userLogoutEvent) {
        isLogin = false;
    }
}















