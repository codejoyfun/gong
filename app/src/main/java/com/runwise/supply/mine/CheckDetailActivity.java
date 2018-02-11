package com.runwise.supply.mine;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.YourScrollableViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

/**
 * 盘点记录详情
 */
public class CheckDetailActivity extends NetWorkActivity {
    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.viewpager)
    private YourScrollableViewPager viewPager;
    @ViewInject(R.id.tv_open)
    private ImageView ivOpen;
    private TabPageIndicatorAdapter adapter;
    @ViewInject(R.id.drag_layout)
    private DragTopLayout dragLayout;

    @ViewInject(R.id.text1)
    private TextView text1;
    @ViewInject(R.id.text2)
    private TextView text2;
    @ViewInject(R.id.text3)
    private TextView text3;
    @ViewInject(R.id.text4)
    private TextView text4;
    @ViewInject(R.id.text5)
    private TextView text5;
    private CheckResult.ListBean bean;

    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    CategoryRespone categoryRespone;
    CheckResult mCheckResult;

    public static final String INTENT_KEY_ID = "intent_key_id";
    String mId;
    public static final int REQUEST_CHECK_DETAIL = 1 << 0;

    public static final int MAX_SYNC_COUNT = 2;
    int mSyncCount = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.acticity_checkdetail_layout);
        if (GlobalApplication.getInstance().getCanSeePrice()) {
            text4.setText("盘点结果(元)");
        } else {
            text4.setText("盘点结果(件)");
        }

        this.setTitleText(true, "盘点记录详情");
        this.setTitleLeftIcon(true, R.drawable.back_btn);

        mId = getIntent().getStringExtra(INTENT_KEY_ID);
        Object request = null;
        sendConnection("/api/inventory/line/"+mId,request,REQUEST_CHECK_DETAIL,true,CheckResult.class);
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
        dragLayout.setOverDrag(false);
    }

    @OnClick({R.id.tv_open})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_open:
                if (dragLayout.getState() == DragTopLayout.PanelState.EXPANDED) {
                    dragLayout.toggleTopView();
                    canShow = true;
                } else {
                    if (mProductTypeWindow.isShowing()) {
                        mProductTypeWindow.dismiss();
                    } else {
                        showPopWindow();
                    }
                }
                dragLayout.listener(new DragTopLayout.PanelListener() {
                    @Override
                    public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
                        if (panelState == DragTopLayout.PanelState.COLLAPSED) {
                            if (canShow) {
                                showPopWindow();
                                canShow = false;
                            }
                        } else {
                            mProductTypeWindow.dismiss();
                        }
                    }

                    @Override
                    public void onSliding(float ratio) {

                    }

                    @Override
                    public void onRefresh() {

                    }
                });

                break;
        }
    }
    boolean canShow = false;
    private void showPopWindow(){
        int y = findViewById(R.id.titleLayout).getHeight() + tablayout.getHeight();
        mProductTypeWindow.showAtLocation(getRootView(getActivityContext()), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }
    private void setUpDataForViewPage() {
        List<Fragment> orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<PandianResult.InventoryBean.LinesBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<PandianResult.InventoryBean.LinesBean>());
        }
        for (PandianResult.InventoryBean.LinesBean linesBean : bean.getLines()) {
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(linesBean.getProductID()));
            if (listBean != null && !TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<PandianResult.InventoryBean.LinesBean> linesBeen = map.get(listBean.getCategory());
                if (linesBeen == null) {
                    linesBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), linesBeen);
                }
                linesBeen.add(linesBean);
            }
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<PandianResult.InventoryBean.LinesBean> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        orderProductFragmentList.add(0, newProductFragment((ArrayList<PandianResult.InventoryBean.LinesBean>) bean.getLines()));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tablayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
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
            tablayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        int position = this.getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position, false);
        viewPager.setOffscreenPageLimit(titles.size());
        initPopWindow((ArrayList<String>) titles);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               int height = viewPager.getHeight();
                Log.i("postDelayed",""+height);
            }
        }, 2000);
    }

    public CheckDetailListFragment newProductFragment(ArrayList<PandianResult.InventoryBean.LinesBean> value) {
        CheckDetailListFragment orderProductFragment = new CheckDetailListFragment();
        orderProductFragment.setData(value);
        return orderProductFragment;
    }

    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        mProductTypeWindow = new PopupWindow(gridView, DensityUtil.getScreenW(getActivityContext()), DensityUtil.getScreenH(getActivityContext()) - (findViewById(R.id.titleLayout).getHeight() + tablayout.getHeight()), true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewPager.setCurrentItem(position);
                tablayout.getTabAt(position).select();
                for (int i = 0; i < mProductTypeAdapter.selectList.size(); i++) {
                    mProductTypeAdapter.selectList.set(i, new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position, new Boolean(true));
                mProductTypeAdapter.notifyDataSetChanged();
            }
        });
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductTypeWindow.dismiss();
            }
        });
        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    public CheckResult.ListBean getDataBean() {
        return bean;
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        finish();
    }

    public void setUpData(){
        mSyncCount++;
        if (mSyncCount == MAX_SYNC_COUNT){
            setUpDetail();
            setUpDataForViewPage();
        }
    }

    public void setUpDetail(){
        bean = mCheckResult.getList().get(0);
        for (PandianResult.InventoryBean.LinesBean lines : bean.getLines()) {
            ProductBasicList.ListBean product = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(lines.getProductID()));
            if (product == null) {
                product = new ProductBasicList.ListBean();
                product.setStockType("gege");
            }
            lines.setProduct(product);
        }
        text1.setText("盘点人员：" + bean.getCreateUser());
        text2.setText("盘点单号：" + bean.getName());
        text3.setText("盘点日期：" + TimeUtils.getTimeStamps3(bean.getCreateDate()));
        if (GlobalApplication.getInstance().getCanSeePrice()) {
            text5.setText("¥" + NumberUtil.getIOrD(bean.getValue()) + "");
            if (bean.getValue() >= 0) {
                text5.setTextColor(Color.parseColor("#9cb62e"));
            } else {
                text5.setTextColor(Color.parseColor("#e75967"));
            }
        } else {
            text5.setText(NumberUtil.getIOrD(bean.getNum()));
            if (bean.getNum() >= 0) {
                text5.setTextColor(Color.parseColor("#9cb62e"));
            } else {
                text5.setTextColor(Color.parseColor("#e75967"));
            }
        }
        if ("confirm".equals(bean.getState())) {
            text5.setTextColor(Color.parseColor("#999999"));
            text5.setText("--");
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case CATEGORY:
                BaseEntity.ResultBean resultBean = result.getResult();
                categoryRespone = (CategoryRespone) resultBean.getData();
                setUpData();
                break;
            case REQUEST_CHECK_DETAIL:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                mCheckResult = (CheckResult) resultBean1.getData();
                setUpData();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);


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
