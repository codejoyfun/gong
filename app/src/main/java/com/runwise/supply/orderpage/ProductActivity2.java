package com.runwise.supply.orderpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.CategoryResponseV2;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.ProductTypePopup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.orderpage.ProductCategoryFragment.INTENT_KEY_CATEGORY;

/**
 * 分页/二级分类的商品选择页
 * 注意要区分有含有二级分类和完全没有二级分类两种显示
 *
 * Created by Dong on 2017/7/3.
 */

public class ProductActivity2 extends NetWorkActivity {
    //数据获取
    private static final int REQUEST_CATEGORY = 1;
    @ViewInject(R.id.et_search_product)
    private EditText mEtSearch;
    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    @ViewInject(R.id.vp_product_fragments)
    private ViewPager mViewPagerCategoryFrags;
    private TabPageIndicatorAdapter mAdapterVp;

    private ArrayList<AddedProduct> addedPros;       //从前面页面传来的数组。
    private ProductTypePopup mTypeWindow;//商品类型弹出框

    CategoryResponseV2 categoryResponse;
    public static final String INTENT_KEY_BACKAP = "backap";

    private Map<ProductData.ListBean, Integer> mMapCount = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StatusBarUtil.StatusBarLightMode(this);
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_product_selection);
        //获取上一个页面传来的Parcelable
        Intent fromIntent = getIntent();
        Bundle bundle = fromIntent.getBundleExtra("apbundle");
        if (bundle != null){
            addedPros = bundle.getParcelableArrayList("ap");
        }

        mEtSearch.addTextChangedListener(new TextWatcher() {
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

        requestCategory();
    }

    private void setupViewPager() {
        List<Fragment> categoryFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (CategoryResponseV2.Category category : categoryResponse.getCategoryList()) {
            titles.add(category.getName());
            categoryFragmentList.add(newCategoryFragment(category));
            tabFragmentList.add(TabFragment.newInstance(category.getName()));
        }

        initUI(titles, categoryFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    /**
     * 新建一级类别fragment
     * @param category
     * @return
     */
    public ProductCategoryFragment newCategoryFragment(CategoryResponseV2.Category category) {
        ProductCategoryFragment productCategoryFragment = new ProductCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(INTENT_KEY_CATEGORY, category);
        productCategoryFragment.setArguments(bundle);
        return productCategoryFragment;
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
        mTypeWindow.setViewPager(mViewPagerCategoryFrags);
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
        mTypeWindow.showAtLocation(getRootView(ProductActivity2.this), Gravity.NO_GRAVITY, 0, y);
        mTypeWindow.setSelect(mViewPagerCategoryFrags.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    private void initUI(List<String> titles, List<Fragment> repertoryEntityFragmentList) {
        mAdapterVp = new TabPageIndicatorAdapter(getSupportFragmentManager(), titles, repertoryEntityFragmentList);
        mViewPagerCategoryFrags.setAdapter(mAdapterVp);
        smartTabLayout.setupWithViewPager(mViewPagerCategoryFrags);
        mViewPagerCategoryFrags.setOffscreenPageLimit(titles.size());
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPagerCategoryFrags.setCurrentItem(position);
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
                HashMap<String, Integer> countMap = ProductListFragment.getCountMap();
                Iterator iter = countMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry) iter.next();
                    String key = entry.getKey();
                    Integer count = entry.getValue();
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

    private void requestCategory() {
        ///gongfu/v3/shop/product/list
        Object request = null;
        sendConnection("/gongfu/v2/product/category", request, REQUEST_CATEGORY, true, CategoryResponseV2.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryResponse = (CategoryResponseV2) resultBean1.getData();
                setupViewPager();
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

    public Map<ProductData.ListBean,Integer> getCountMap(){
        return mMapCount;
    }
}
