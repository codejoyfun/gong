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
import com.runwise.supply.entity.ShowInventoryNoticeEvent;
import com.runwise.supply.firstpage.OrderDetailActivity;
import com.runwise.supply.mine.entity.SearchKeyAct;
import com.runwise.supply.repertory.entity.UpdateRepertory;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.InventoryCacheManager;
import com.runwise.supply.view.ProductTypePopup;
import com.runwise.supply.view.SystemUpgradeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.repertory.AbstractStockListFragment.ARG_CURRENT;
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
    @ViewInject(R.id.include_notice)
    private View mViewNotice;
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
            //展示盘点中
            if(InventoryCacheManager.getInstance(getActivity()).shouldShowInventoryInProgress()){
                showNotice(new ShowInventoryNoticeEvent(true));
            }
        }
        else{
            buildData();
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
                viewPager.setCurrentItem(position,false);
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
            ((ViewGroup.MarginLayoutParams)smartTabLayout.getLayoutParams()).setMargins(0,0,0,0);
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
        sendConnection("/api/product/category", getCategoryRequest, OrderDetailActivity.CATEGORY, true, CategoryRespone.class);
    }

    /**
     * 未登录，示例数据
     */
    private  void buildData() {
        onSuccess(FictitiousStock.mockCategory(),CATEGORY);
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
        AbstractStockListFragment allStockListFragment = newRepertoryListFragment("");
        allStockListFragment.getArguments().putBoolean(ARG_CURRENT,true);//全部为打开tab
        repertoryEntityFragmentList.add(0, allStockListFragment);

        for(String category:categoryRespone.getCategoryList()){
            titles.add(category);
            repertoryEntityFragmentList.add(newRepertoryListFragment(category));
        }

        initUI(titles,repertoryEntityFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    public AbstractStockListFragment newRepertoryListFragment(String category) {
        AbstractStockListFragment repertoryListFragment;
        if(getActivity() instanceof DealerSearchActivity){
            repertoryListFragment = new SearchStockListFragment();//搜索
        }else{
            repertoryListFragment = new StockListFragment();//正常显示
        }
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CATEGORY,category);
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

    /**
     * 展示盘点中提示
     */
    boolean isClose = false;
    @Subscribe
    public void showNotice(ShowInventoryNoticeEvent showInventoryNoticeEvent){
        if(showInventoryNoticeEvent.isShow && !isClose){
            mViewNotice.setVisibility(View.VISIBLE);
            mViewNotice.findViewById(R.id.iv_notice_close).setOnClickListener(v->{
                isClose = true;
                mViewNotice.setVisibility(View.GONE);
            });
        }else{
            if(!showInventoryNoticeEvent.isShow)isClose = false;
            mViewNotice.setVisibility(View.GONE);
        }
    }
}