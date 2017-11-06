package com.runwise.supply.repertory;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FictitiousStock;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.firstpage.OrderDetailActivity;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.mine.entity.SearchKeyAct;
import com.runwise.supply.repertory.entity.UpdateRepertory;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.view.ProductTypePopup;
import com.runwise.supply.view.SystemUpgradeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.repertory.RepoListFragment.ARG_CATEGORY;


/**
 * 库存列表
 * 不同类别用不同的StockListFragment分别显示
 */

public class StockFragment extends NetWorkFragment {

    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.layout_system_upgrade_notice)
    private SystemUpgradeLayout mLayoutUpgradeNotice;
    private TabPageIndicatorAdapter adapter;

    boolean isLogin;
    private Handler handler = new Handler();
    private ProductTypePopup mTypeWindow;//商品类型弹出框

    private AbstractStockListFragment currentFragment;//记录当前的fragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            requestCategory();
        }
        else{
            buildData();//TODO
        }
        mLayoutUpgradeNotice.setPageName("盘点功能");
    }

    @OnClick({R.id.iv_open})
    public void btnClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.iv_open:
                if (mTypeWindow == null){
                    return;
                }
                if (!mTypeWindow.isShowing()){
                    showPopWindow();
                }else{
                    mTypeWindow.dismiss();
                }
                break;
        }
    }

    private void initPopWindow(ArrayList<String> typeList) {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow = new ProductTypePopup(getActivity(),
                ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.getScreenH(getActivity()) - y,
                typeList,0);
        mTypeWindow.setViewPager(viewPager);
        mTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    private void showPopWindow(){
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
//        mProductTypeWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, y);
//        mProductTypeAdapter.setSelectIndex(viewPager.getCurrentItem());
        mTypeWindow.showAtLocation(mainView,Gravity.NO_GRAVITY,0,y);
        mTypeWindow.setSelect(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    private void initUI(List<String> titles,List<AbstractStockListFragment> repertoryEntityFragmentList){
        adapter = new TabPageIndicatorAdapter(this.getActivity().getSupportFragmentManager(),titles,repertoryEntityFragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(repertoryEntityFragmentList.size());
        smartTabLayout.removeAllTabs();
        try{
            smartTabLayout.setupWithViewPager(viewPager);
        }catch (Exception e){
            e.printStackTrace();
        }
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                mTypeWindow.dismiss();

                //刷新当前fragment
                AbstractStockListFragment fragment = adapter.getFragmentList().get(position);
                fragment.refresh(mKeyword);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(titles.size()<=TAB_EXPAND_COUNT){
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }else{
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    /**
     * TODO: 库存更新，最好要移动到对应类别的fragment
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateRepertory event) {
        requestCategory();
    }

    String mKeyword = "";
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearch(SearchKeyAct event) {
        if (mContext.getClass().getSimpleName().equals(event.getActName())) {
            mKeyword = event.getKeyWork();
            currentFragment.refresh(mKeyword);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onPullEvent(RefreshPepertoy event) {
//        if(isLogin) {
//            requestCategory();
//        }
//        else {
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    buildData();
//                }
//            }, 1000);
//        }
//    }

    private void requestCategory(){
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, OrderDetailActivity.CATEGORY, false, CategoryRespone.class);
    }

    /**
     * TODO
     */
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
                "                \"unit\": \"1000克/袋\",\n" +
                "                \"productID\": 20,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"新鲜柠檬\",\n" +
                "                \"defaultCode\": \"300001\",\n" +
                "                \"category\": \"冷藏货\",\n" +
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
                "                \"unit\": \"1000克/袋\",\n" +
                "                \"productID\": 21,\n" +
                "                \"tracking\": \"none\",\n" +
                "                \"name\": \"莴笋\",\n" +
                "                \"defaultCode\": \"300002\",\n" +
                "                \"category\": \"冷藏货\",\n" +
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
                "                \"unit\": \"1000克/袋\",\n" +
                "                \"productID\": 22,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"新鲜油桃\",\n" +
                "                \"defaultCode\": \"300003\",\n" +
                "                \"category\": \"冷藏货\",\n" +
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
                "                \"unit\": \"1000克/袋\",\n" +
                "                \"productID\": 23,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"新鲜本地番茄\",\n" +
                "                \"defaultCode\": \"300006\",\n" +
                "                \"category\": \"冷藏货\",\n" +
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
                "                \"unit\": \"600克/包\",\n" +
                "                \"productID\": 24,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"虾皇饺\",\n" +
                "                \"defaultCode\": \"300007\",\n" +
                "                \"category\": \"冻货\",\n" +
                "                \"settleUomId\": \"\",\n" +
                "                \"price\": 100.04,\n" +
                "                \"uom\": \"桶\"\n" +
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
                "                \"unit\": \"1000克/袋\",\n" +
                "                \"productID\": 23,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"新鲜本地番茄\",\n" +
                "                \"defaultCode\": \"300006\",\n" +
                "                \"category\": \"冷藏货\",\n" +
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
                "                \"unit\": \"200克/包\",\n" +
                "                \"productID\": 24,\n" +
                "                \"tracking\": \"lot\",\n" +
                "                \"name\": \"香糯汤圆 黑芝麻口味\",\n" +
                "                \"defaultCode\": \"300008\",\n" +
                "                \"category\": \"冻货\",\n" +
                "                \"settleUomId\": \"\",\n" +
                "                \"price\": 100.04,\n" +
                "                \"uom\": \"桶\"\n" +
                "            }\n" +
                "        }\n"+
                "    ]\n" +
                "}";
        mUnLoginCategoryRespone = new CategoryRespone();
        List<String> categoryList = new ArrayList<>();
        categoryList.add("冷藏货");
        categoryList.add("冻货");
        categoryList.add("干货");
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) smartTabLayout.getLayoutParams();
        layoutParams.setMargins(0,0,0,0);
        smartTabLayout.setLayoutParams(layoutParams);
        mUnLoginCategoryRespone.setCategoryList(categoryList);
//        RepertoryEntity repertoryEntity = new RepertoryEntity();
//        List<RepertoryEntity.ListBean> listBeen = new ArrayList<>();
//        RepertoryEntity.ListBean listBean = new RepertoryEntity.ListBean();
//        ProductBasicList.ListBean product = new ProductBasicList.ListBean();
//        product.setProductUom("1000克/袋");
//        product.setName("新鲜柠檬");
//        product.setDefaultCode("300001");
//        listBean.setProduct(product);
//        listBeen.add(listBean);
//        repertoryEntity.setList(listBeen);
        RepertoryEntity repertoryEntity = FictitiousStock.getRepertoryEntity();
//        RepertoryEntity repertoryEntity =  JSON.parseObject(xmlStr,RepertoryEntity.class);
       // setUpDataForViewPage(mUnLoginCategoryRespone,repertoryEntity);//TODO
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        requestCategory();
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_repertory_layout;
    }

    CategoryRespone categoryRespone;
    CategoryRespone mUnLoginCategoryRespone;
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case OrderDetailActivity.CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage(categoryRespone);
            break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }

    private void setUpDataForViewPage(CategoryRespone categoryRespone) {
        List<AbstractStockListFragment> repertoryEntityFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        titles.add("全部");
        repertoryEntityFragmentList.add(0, newRepertoryListFragment("",true));

        for(String category:categoryRespone.getCategoryList()){
            titles.add(category);
            repertoryEntityFragmentList.add(newRepertoryListFragment(category,false));
        }

        initUI(titles,repertoryEntityFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    public AbstractStockListFragment newRepertoryListFragment(String category,boolean isCurrent) {
        AbstractStockListFragment repertoryListFragment;
        if(getActivity() instanceof DealerSearchActivity){
            repertoryListFragment = new SearchStockListFragment();//搜索
        }else{
            repertoryListFragment = new StockListFragment();//正常显示
        }

        Bundle bundle = new Bundle();
        bundle.putString(ARG_CATEGORY,category);
        bundle.putBoolean("arg_current",isCurrent);
        repertoryListFragment.setArguments(bundle);
        return repertoryListFragment;
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<AbstractStockListFragment> fragmentList = new ArrayList<>();
        public TabPageIndicatorAdapter(FragmentManager fm,List<String> titles,List<AbstractStockListFragment> repertoryEntityFragmentList) {
            super(fm);
            titleList = titles;
            fragmentList.addAll(repertoryEntityFragmentList);
        }
        public List<AbstractStockListFragment> getFragmentList() {
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            currentFragment = fragmentList.get(position);
            super.setPrimaryItem(container, position, object);
        }
    }


}















