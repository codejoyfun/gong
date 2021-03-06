package com.runwise.supply.orderpage;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.ProductCountChangeEvent;
import com.kids.commonframe.base.bean.ProductGetEvent;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.MyDbUtil;
import com.runwise.supply.tools.RunwiseService;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.ProductTypePopup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

/**
 * Created by libin on 2017/7/3.
 */

public class ProductActivity extends NetWorkActivity {
    //数据获取
    private static final int PRODUCT_GET = 1;
    @ViewInject(R.id.editText)
    private EditText editText;
    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;
    private ArrayList<AddedProduct> addedPros;       //从前面页面传来的数组。
    private List<ProductBasicList.ListBean> dataList = new ArrayList<>();//全部的商品信息
    private ProductTypePopup mTypeWindow;//商品类型弹出框

    public static final String INTENT_KEY_BACKAP = "backap";

    public List<ProductBasicList.ListBean> getDataList() {
        return dataList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StatusBarUtil.StatusBarLightMode(this);
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.product_layout);
        //获取上一个页面传来的Parcelable
        Intent fromIntent = getIntent();
        Bundle bundle = fromIntent.getBundleExtra("apbundle");
        if (bundle != null){
            addedPros = bundle.getParcelableArrayList("ap");
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //通知子fragment更新list产品数量
                EventBus.getDefault().post(new ProductCountChangeEvent());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (TextUtils.isEmpty(s.toString())){
//                    //清空了，显示之前的全部
//                    EventBus.getDefault().post(new ProductGetEvent());
//                }else{
//                    //搜索基本商品信息过滤
//                    ProductQueryEvent event = new ProductQueryEvent(s.toString());
//                    EventBus.getDefault().post(event);
//                }
                //搜索基本商品信息过滤
                ProductQueryEvent event = new ProductQueryEvent(s.toString());
                EventBus.getDefault().post(event);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendRequest();

    }

    private void setUpDataForViewPage() {
        List<Fragment> repertoryEntityFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        HashMap<String, ArrayList<ProductBasicList.ListBean>> map = new HashMap<>();
        List<String> titles = new ArrayList<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<ProductBasicList.ListBean>());
        }
        for (ProductBasicList.ListBean listBean : dataList) {
            if (!TextUtils.isEmpty(listBean.getCategoryParent())) {
                ArrayList<ProductBasicList.ListBean> listBeen = map.get(listBean.getCategoryParent());
                if (listBeen == null) {
                    listBeen = new ArrayList<>();
                    map.put(listBean.getCategoryParent(), listBeen);
                }
                listBeen.add(listBean);
            }
        }
        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<ProductBasicList.ListBean> value = map.get(category);
            repertoryEntityFragmentList.add(newRepertoryListFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }

        repertoryEntityFragmentList.add(0, newRepertoryListFragment((ArrayList<ProductBasicList.ListBean>) dataList));
        initUI(titles, repertoryEntityFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    public ProductListFragment newRepertoryListFragment(ArrayList<ProductBasicList.ListBean> value) {
        ProductListFragment repertoryListFragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        if (addedPros != null && addedPros.size() > 0) {
            bundle.putParcelableArrayList("ap", addedPros);
        }
        bundle.putSerializable(OrderProductFragment.BUNDLE_KEY_LIST, value);
        repertoryListFragment.setArguments(bundle);
        return repertoryListFragment;
    }


//    private PopupWindow mProductTypeWindow;
//    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow = new ProductTypePopup(this,
                ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.getScreenH(getActivityContext()) - y,
                typeList,0);
        mTypeWindow.setViewPager(viewPager);
        mTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    private void showPopWindow() {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mTypeWindow.showAtLocation(getRootView(ProductActivity.this), Gravity.NO_GRAVITY, 0, y);
        mTypeWindow.setSelect(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    private void initUI(List<String> titles, List<Fragment> repertoryEntityFragmentList) {
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager(), titles, repertoryEntityFragmentList);
        viewPager.setAdapter(adapter);
        smartTabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(titles.size());
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                mTypeWindow.dismiss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    @OnClick({R.id.title_iv_left, R.id.addBtn, R.id.iv_open})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                dialog.setMessageGravity();
                dialog.setMessage("还没保存哦,确认取消?");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        finish();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.addBtn:
                //回值给调用的页面
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //当前选中的商品信息
                ArrayList<AddedProduct> addedList = new ArrayList<>();
                HashMap<String, Double> countMap = ProductListFragment.getCountMap();
                Iterator iter = countMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Double> entry = (Map.Entry) iter.next();
                    String key = entry.getKey();
                    Double count = entry.getValue();
                    Parcel parcel = Parcel.obtain();
                    AddedProduct pro = AddedProduct.CREATOR.createFromParcel(parcel);
                    if (count != 0) {
                        pro.setCount(count);
                        pro.setProductId(key);
                        addedList.add(pro);
                    }
                }
                bundle.putParcelableArrayList(INTENT_KEY_BACKAP, addedList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_open:
//                if (mProductTypeWindow == null) {
//                    return;
//                }
//                if (!mProductTypeWindow.isShowing()) {
//                    showPopWindow();
//                } else {
//                    mProductTypeWindow.dismiss();
//                }
                if (mTypeWindow == null) {
                    return;
                }
                if (!mTypeWindow.isShowing()) {
                    showPopWindow();
                } else {
                    mTypeWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    private void sendRequest() {
        DbUtils dbUtils = MyDbUtil.create(getApplicationContext());
        try {
            dataList = ProductBasicUtils.getBasicArr();
            if (dataList == null || dataList.isEmpty()) {
                Selector selector = Selector.from(ProductBasicList.ListBean.class);
                selector.orderBy("orderBy", false);
                dataList = dbUtils.findAll(selector);
                dataList =  RunwiseService.filterSubValid(dataList);
                ProductBasicUtils.setBasicArr(dataList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(SampleApplicationLike.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);

    }

    CategoryRespone categoryRespone;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                //传给子Fragment
                ProductGetEvent event = new ProductGetEvent();
                EventBus.getDefault().post(event);
                setUpDataForViewPage();
                break;
            default:
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, List<String> titles, List<Fragment> repertoryEntityFragmentList) {
            super(fm);
            fragmentList.addAll(repertoryEntityFragmentList);
            titleList = titles;
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
