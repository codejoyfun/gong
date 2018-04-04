package com.runwise.supply.mine;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FictitiousStock;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.firstpage.OrderDetailActivity;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.mine.entity.RefreshPepertoy;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.UpdateRepertory;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.ProductBasicHelper;
import com.runwise.supply.view.ProductTypePopup;
import com.runwise.supply.view.SystemUpgradeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;


/**
 * 库存列表
 */

public class RepertoryFragment extends NetWorkFragment {
    private final int PRODUCT_GET = 1;
    private final int PRODUCT_DETAIL = 2;


    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.layout_system_upgrade_notice)
    private SystemUpgradeLayout mLayoutUpgradeNotice;
    private TabPageIndicatorAdapter adapter;

    private List<RepertoryEntity.ListBean> productList;
    private RepertoryEntity repertoryEntity;
    boolean isLogin;
    private Handler handler = new Handler();
    private ProductBasicHelper mProductHelper;
    private ProductTypePopup mTypeWindow;//商品类型弹出框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            requestCategory();
        }
        else{
            buildData();
        }
        mLayoutUpgradeNotice.setPageName("盘点功能");
        mProductHelper = new ProductBasicHelper(getActivity(),netWorkHelper);
    }

    public void onLogout(UserLogoutEvent userLogoutEvent){

    }

    @OnClick({R.id.iv_open})
    public void btnClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.iv_open:
//                if (mProductTypeWindow == null){
//                    return;
//                }
//                if (!mProductTypeWindow.isShowing()){
//                    showPopWindow();
//                }else{
//                    mProductTypeWindow.dismiss();
//                }
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

//    private PopupWindow mProductTypeWindow;
//    ProductTypeAdapter mProductTypeAdapter;
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

//        View dialog = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_tab_type, null);
//        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
//        mProductTypeAdapter = new ProductTypeAdapter(typeList);
//        gridView.setAdapter(mProductTypeAdapter);
//        final int[] location = new int[2];
//        smartTabLayout.getLocationOnScreen(location);
//        int y = (int) (location[1] + smartTabLayout.getHeight());
//        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getScreenH(getActivity()) - y, true);
//        mProductTypeWindow.setContentView(dialog);
//        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
//        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        mProductTypeWindow.setFocusable(false);
//        mProductTypeWindow.setOutsideTouchable(false);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mProductTypeWindow.dismiss();
//                viewPager.setCurrentItem(position);
//                smartTabLayout.getTabAt(position).select();
//                for (int i = 0;i < mProductTypeAdapter.selectList.size();i++){
//                    mProductTypeAdapter.selectList.set(i,new Boolean(false));
//                }
//                mProductTypeAdapter.selectList.set(position,new Boolean(true));
//                mProductTypeAdapter.notifyDataSetChanged();
//            }
//        });
//        dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mProductTypeWindow.dismiss();
//            }
//        });
//        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                ivOpen.setImageResource(R.drawable.arrow);
//            }
//        });
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

    private void initUI(List<String> titles,List<RepertoryListFragment> repertoryEntityFragmentList){
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
//                mProductTypeWindow.dismiss();
                mTypeWindow.dismiss();
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

    private void getProductDetail(int productId) {
        sendConnection("/gongfu/v2/product/"+productId,null,PRODUCT_DETAIL,false, ProductOne.class);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateRepertory event) {
        requestCategory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPullEvent(RefreshPepertoy event) {
        if(isLogin) {
            requestCategory();
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
    private void requestCategory(){
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(SampleApplicationLike.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, OrderDetailActivity.CATEGORY, false, CategoryRespone.class);
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
        setUpDataForViewPage(mUnLoginCategoryRespone,repertoryEntity);
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
            case PRODUCT_GET:
                repertoryEntity = (RepertoryEntity)result.getResult().getData();
                productList = repertoryEntity.getList();

                if(!mProductHelper.checkRepertoryProducts(productList)){
                    mProductHelper.requestDetail(PRODUCT_DETAIL);
                    //先显示
//                    return;
                }
                setUpDataForViewPage(categoryRespone,repertoryEntity);

//                boolean nullProductExit = false;
//                for(RepertoryEntity.ListBean bean : productList) {
//                    ProductBasicList.ListBean baseProduct = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
//                    if( baseProduct == null ) {
//                        getProductDetail(bean.getProductID());
//                        nullProductExit = true;
//                    }
//                    else{
//                        bean.setProduct(baseProduct);
//                    }
//                }
//                if(!nullProductExit) {
//                    setUpDataForViewPage(categoryRespone,repertoryEntity);
//                }
                break;
            case PRODUCT_DETAIL:
                if(mProductHelper.onSuccess(result)){
                    setUpDataForViewPage(categoryRespone,repertoryEntity);
                    return;
                }

//                ProductOne productOne = (ProductOne) result.getResult().getData();
//                for(RepertoryEntity.ListBean bean : productList) {
//                    if(productOne.getProduct().getProductID() == bean.getProductID()) {
//                        bean.setProduct(productOne.getProduct());
//                        ProductBasicUtils.getBasicMap(mContext).put(productOne.getProduct().getProductID()+"",productOne.getProduct());
//                        break;
//                    }
//                }
//                setUpDataForViewPage(categoryRespone,repertoryEntity);
                break;
            case OrderDetailActivity.CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                requestData();
            break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }

    private void setUpDataForViewPage(CategoryRespone categoryRespone,RepertoryEntity repertoryEntity) {
        List<RepertoryListFragment> repertoryEntityFragmentList = new ArrayList<>();
        HashMap<String, ArrayList<RepertoryEntity.ListBean>> map = new HashMap<>();
        List<String> titles = new ArrayList<>();
        titles.add("全部");
        for(String category:categoryRespone.getCategoryList()){
            titles.add(category);
            map.put(category,new ArrayList<RepertoryEntity.ListBean>());
        }
        for (RepertoryEntity.ListBean listBean : repertoryEntity.getList()) {
            ProductBasicList.ListBean product = ProductBasicUtils.getBasicMap(getActivity()).get(String.valueOf(listBean.getProductID()));
            if (product == null){
                listBean.setProduct(ProductBasicUtils.getBasicMap(getActivity()).get(listBean.getProductID()+""));
            }
            if(product != null && !TextUtils.isEmpty(product.getCategory())){
                ArrayList<RepertoryEntity.ListBean> listBeen = map.get(product.getCategory());
                if (listBeen == null) {
                    listBeen = new ArrayList<>();
                    map.put(product.getCategory(), listBeen);
                }
                listBeen.add(listBean);
            }
        }
        for(String category:categoryRespone.getCategoryList()){
            ArrayList<RepertoryEntity.ListBean> value = map.get(category);
            repertoryEntityFragmentList.add(newRepertoryListFragment(value));
        }
        repertoryEntityFragmentList.add(0, newRepertoryListFragment((ArrayList<RepertoryEntity.ListBean>) repertoryEntity.getList()));
        initUI(titles,repertoryEntityFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    public RepertoryListFragment newRepertoryListFragment(ArrayList<RepertoryEntity.ListBean> value) {
        RepertoryListFragment repertoryListFragment = new RepertoryListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(OrderProductFragment.BUNDLE_KEY_LIST, value);
        repertoryListFragment.setArguments(bundle);
        return repertoryListFragment;
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<RepertoryListFragment> fragmentList = new ArrayList<>();
        public TabPageIndicatorAdapter(FragmentManager fm,List<String> titles,List<RepertoryListFragment> repertoryEntityFragmentList) {
            super(fm);
            titleList = titles;
            fragmentList.addAll(repertoryEntityFragmentList);
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















