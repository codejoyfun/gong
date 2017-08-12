package com.runwise.supply;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.CheckVersionManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.business.MainRepertoryFragment;
import com.runwise.supply.firstpage.LoginedFirstFragment;
import com.runwise.supply.firstpage.UnLoginedFirstFragment;
import com.runwise.supply.index.IndexFragment;
import com.runwise.supply.message.MessageFragment;
import com.runwise.supply.mine.MineFragment;
import com.runwise.supply.orderpage.OrderFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;
import com.socketmobile.capture.Capture;
//import com.socketmobile.capture.Capture;
//import com.socketmobile.capture.client.CaptureClient;
//import com.socketmobile.capture.client.CaptureDeviceClient;
//import com.socketmobile.capture.events.DataDecodedEvent;
//import com.socketmobile.capture.events.DeviceAvailabilityEvent;
//import com.socketmobile.capture.types.DecodedData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends NetWorkActivity {
    //缓存全部商品列表的标识
    private static final int QUERY_ALL = 1;
    private final int REQUEST_UNREAD = 2;

//    private int devicesConnected = -1;
    private long mExitTime;
    @ViewInject(android.R.id.tabhost)
    private FragmentTabHost mTabHost;
    //未读小红点
    private TextView mMsgHite;
//    private UserInfo userInfo;
    //缓存基本商品信息到内存，便于每次查询对应productid所需基本信息
    private class CachRunnale implements  Runnable{
        private List<ProductBasicList.ListBean> basicList;

        public CachRunnale(List basicList) {
            this.basicList = basicList;
        }

        @Override
        public void run() {
            DbUtils dbUtils = DbUtils.create(MainActivity.this);
            ProductBasicUtils.setBasicArr(basicList);
            HashMap<String,ProductBasicList.ListBean> map = new HashMap<>();
            for (ProductBasicList.ListBean bean : basicList){
                map.put(String.valueOf(bean.getProductID()),bean);
                //同时存到dB里面
                try {
                    dbUtils.saveOrUpdate(bean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            ProductBasicUtils.setBasicMap(map);

        }
    }
//    private CaptureClient.Listener mListener= new CaptureClient.Listener()
//    {
//
//
//        @Override
//        public void onConnectionFailure(int i) {
//
//        }
//
//        @Override
//        public void onDeviceAvailabilityChanged(CaptureDeviceClient captureDeviceClient) {
//
//        }
//
//        @Override
//        public void onDataDecoded(CaptureDeviceClient captureDeviceClient, DecodedData decodedData) {
//            print(decodedData.getString());
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        setContentView(R.layout.activity_main);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        requestPermissions();
        initTabView();
        //检查版本
        CheckVersionManager checkVersionManager = new CheckVersionManager(this);
//        checkVersionManager.checkVersion(false);
//        Capture.init(getApplicationContext());
//        CaptureClient mClient = new CaptureClient();
//        mClient.setListener(mListener);
//        mClient.connect();
        //每次首次进来，先获取基本商品列表,暂时缓存到内存里。
        if (SPUtils.isLogin(mContext)){
            queryProductList();
        }
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
//                ToastUtil.show(mContext,"一些必要的权限被禁用");
//                dialog.setMsg("一些必要的权限被禁用,可能影响您的使用");
//                dialog.setModel(CustomDialog.RIGHT);
//                dialog.setRightBtnListener("知道啦",null);
//                dialog.show();
//                Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initTabView() {
        //TODO:这里根据登录状态，设置不同的页面进去
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
//        if (!isLogined()){
            mTabHost.addTab(createTabSpace(R.drawable.tab_1_selector, R.string.tab_1), UnLoginedFirstFragment.class, null);
//            mTabHost.addTab(createTabSpace(R.drawable.tab_1_selector, R.string.tab_1), LoginedFirstFragment.class, null);
            mTabHost.addTab(createTabSpace(R.drawable.tab_2_selector, R.string.tab_2), OrderFragment.class, null);
            mTabHost.addTab(createTabSpace(R.drawable.tab_3_selector, R.string.tab_3), MainRepertoryFragment.class, null);
            mTabHost.addTab(createTabSpace(R.drawable.tab_4_selector, R.string.tab_4), MessageFragment.class, null);
            mTabHost.addTab(createTabSpace(R.drawable.tab_5_selector, R.string.tab_5), MineFragment.class, null);
//        }
//        else{
//            mTabHost.addTab(createTabSpace(R.drawable.tab_1_selector, R.string.tab_1), IndexFragment.class, null);
//            mTabHost.addTab(createTabSpace(R.drawable.tab_2_selector, R.string.tab_2), OrderFragment.class, null);
//            mTabHost.addTab(createTabSpace(R.drawable.tab_4_selector, R.string.tab_3), MineFragment.class, null);
//        }
        mTabHost.getTabWidget().setDividerDrawable(null);

    }
    private boolean isLogined() {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    private TabHost.TabSpec createTabSpace(int bgRes, int tabNameRes) {
        if (mTabHost != null) {
            TabHost.TabSpec subTab = mTabHost.newTabSpec(getString(tabNameRes));
            View subTabView = LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
            ImageView tabIv = (ImageView) subTabView.findViewById(R.id.tab_iv_icon);
            TextView tabTv = (TextView) subTabView.findViewById(R.id.tab_tv_name);
            if( R.string.tab_4 == tabNameRes) {
                mMsgHite = (TextView) subTabView.findViewById(R.id.tv_hint);
            }
            tabIv.setImageResource(bgRes);
            tabTv.setText(getString(tabNameRes));
            subTab.setIndicator(subTabView);
            return subTab;
        }
        return null;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch(where){
            case QUERY_ALL:
                BaseEntity.ResultBean resultBean= result.getResult();
                ProductBasicList basicList = (ProductBasicList) resultBean.getData();
                new Thread(new CachRunnale(basicList.getList())).start();
                break;
            case REQUEST_UNREAD:
                mMsgHite.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtil.show(this, "再按一次退出程序");
            mExitTime = System.currentTimeMillis();
        }
        else {
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //每次首次进来，先获取基本商品列表,暂时缓存到内存里。
//        if (SPUtils.isLogin(mContext)){
//            queryProductList();
//        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
//        userInfo = GlobalApplication.getInstance().loadUserInfo();
    }

    @Override
    public void onUserLoginout() {
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onScan(DataDecodedEvent event) {
//        print(event.data.getData().toString());
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDataDecoded(DataDecodedEvent event) {
//        // Do something
//        print(event.data.getData().toString());
//    }
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onCaptureDeviceAvailabilityChanged(DeviceAvailabilityEvent event) {
//        Collection<CaptureDeviceClient> devices;
//        updateDeviceState(event);
//
//        // The first time we receive this event - via sticky event - we
//        // want to configure all available devices
//        if (devicesConnected < 0) {
//            devices = event.getAllDevices();
//        } else {
//            devices = event.getChangedDevices();
//        }
//
//        devicesConnected = event.getDeviceCount();
//
//        for (CaptureDeviceClient device : devices) {
////            if (device.isMine()) {
////                // Configuration
////            }
//        }
//    }
//
//    private void updateDeviceState(DeviceAvailabilityEvent event) {
////        Button btn = (Button) findViewById(R.id.btn_toggle);
//        if (event.isAnyDeviceAvailable()) {
//            print("Device available");
//
//        } else if (event.isAnyDeviceConnected()) {
//            print("Device connected");
//
//        } else {
//            print("No device");
//
//        }
//    }

    private void print(String message) {
        ToastUtil.show(mContext,message);
//        ((TextView) findViewById(R.id.hello_scan)).append("\n" + message);
    }
    private void queryProductList() {
        Object request = null;
        sendConnection("/gongfu/v2/product/list/",request, QUERY_ALL,false, ProductBasicList.class);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestBasicProduct(UserLoginEvent event){
        if (ProductBasicUtils.getBasicArr().size() == 0){
            queryProductList();
        }
    }
    public int getCurrentTabIndex(){
        return mTabHost.getCurrentTab();
    }
}
