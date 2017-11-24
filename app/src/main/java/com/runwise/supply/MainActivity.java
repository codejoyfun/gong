package com.runwise.supply;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.CheckVersionManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.entity.UnReadData;
import com.runwise.supply.firstpage.UnLoginedFirstFragment;
import com.runwise.supply.firstpage.entity.VersionRequest;
import com.runwise.supply.message.MessageFragment;
import com.runwise.supply.mine.MineFragment;
import com.runwise.supply.orderpage.OrderFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.MainRepertoryFragment;
import com.runwise.supply.tools.MyDbUtil;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.vov.vitamio.utils.Log;

//import com.socketmobile.capture.Capture;
//import com.socketmobile.capture.client.CaptureClient;
//import com.socketmobile.capture.client.CaptureDeviceClient;
//import com.socketmobile.capture.events.DataDecodedEvent;
//import com.socketmobile.capture.events.DeviceAvailabilityEvent;
//import com.socketmobile.capture.types.DecodedData;

public class MainActivity extends NetWorkActivity {
    public static final String INTENT_KEY_TAB = "tab";
    //缓存全部商品列表的标识
    private static final int QUERY_ALL = 1;
    private final int REQUEST_UNREAD = 2;
    private final int REQUEST_UPLOAD_VERSION = 3;

    //    private int devicesConnected = -1;
    private long mExitTime;
    @ViewInject(android.R.id.tabhost)
    private FragmentTabHost mTabHost;
    //未读小红点
    private TextView mMsgHite;
    //    private UserInfo userInfo;
    private boolean isLogin;
    public static final String INTENT_KEY_SKIP_TO_LOGIN = "intent_key_skip_to_login";

    long mTimeStartQUERY_ALL;

    long mTimeStartREQUEST_UNREAD;

    //缓存基本商品信息到内存，便于每次查询对应productid所需基本信息
    private class CachRunnale implements Runnable {
        private List<ProductBasicList.ListBean> basicList;

        public CachRunnale(List basicList) {
            this.basicList = basicList;
        }

        @Override
        public void run() {
            DbUtils dbUtils = MyDbUtil.create(MainActivity.this);
            ProductBasicUtils.setBasicArr(basicList);
//            Log.d("haha","total from network:"+basicList.size());
            HashMap<String, ProductBasicList.ListBean> map = new HashMap<>();
            dbUtils.configAllowTransaction(true);
            try{
                dbUtils.saveOrUpdateAll(basicList);
            }catch(DbException e){
                e.printStackTrace();
            }

            for (ProductBasicList.ListBean bean : basicList) {
                map.put(String.valueOf(bean.getProductID()), bean);
            }

            try{
                //把数据库中原有的数据也提取出来，可能包括协议已删除的商品
                List<ProductBasicList.ListBean> list = dbUtils.findAll(ProductBasicList.ListBean.class);
//                Log.d("haha","total in db:"+list.size());
                for(ProductBasicList.ListBean bean:list){
                    String keyId = bean.getProductID()+"";
                    if(!map.containsKey(keyId)){
                        if(bean.getImage()==null){//TODO:xutils的坑，没有load imagebean？
                            bean.setImage(new ImageBean());
                        }
                        map.put(keyId,bean);
                    }
                }
            }catch (DbException e){
                e.printStackTrace();
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
    String[] mTags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        setContentView(R.layout.activity_main);
        StatusBarUtil.StatusBarLightMode(this);
        requestPermissions();
        upLoadVersion();
        isLogin = SPUtils.isLogin(mContext);
        if (isLogin) {
            Constant.BASE_URL = (String) SPUtils.get(getActivityContext(),SPUtils.FILE_KEY_HOST, "");
            if (TextUtils.isEmpty(Constant.BASE_URL)){
                logout();
                return;
            }
            queryProductList();
        }else{
            Constant.BASE_URL = Constant.UNLOGIN_URL;
        }
        initTabView();
        String registrationID = JPushInterface.getRegistrationID(this);
        Log.i("JPushInterface", "dfd " + registrationID);
        //检查版本
        CheckVersionManager checkVersionManager = new CheckVersionManager(this);
        checkVersionManager.checkVersion(false);
//        Capture.init(getApplicationContext());
//        CaptureClient mClient = new CaptureClient();
//        mClient.setListener(mListener);
//        mClient.connect();
        //每次首次进来，先获取基本商品列表,暂时缓存到内存里。

        String tab1 = getString(R.string.tab_1);
        String tab2 = getString(R.string.tab_2);
        String tab3 = getString(R.string.tab_3);
        String tab4 = getString(R.string.tab_4);
        String tab5 = getString(R.string.tab_5);
        mTags = new String[]{tab1,tab2,tab3,tab4,tab5};
        if (getIntent().getBooleanExtra(INTENT_KEY_SKIP_TO_LOGIN, false)) {
            startActivity(new Intent(getActivityContext(), LoginActivity.class));
        } else {

        }
    }

    private void logout(){
        SPUtils.loginOut(mContext);
        MessageFragment.isLogin = false;
        GlobalApplication.getInstance().cleanUesrInfo();
        JPushInterface.setAliasAndTags(getApplicationContext(), "", null, null);
        //退出登录
        ActivityManager.getInstance().finishAll();
        EventBus.getDefault().post(new UserLogoutEvent());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_KEY_SKIP_TO_LOGIN,true);
        startActivity(intent);
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
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0;i<mTags.length;i++){
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(mTags[i]);
                    if (fragment!=null){
                        fragment.setUserVisibleHint(false);
                    }
                }
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tabId);
                if (fragment!=null){
                    fragment.setUserVisibleHint(true);
                }
            }
        });
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
            if (R.string.tab_4 == tabNameRes) {
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
        switch (where) {
            case QUERY_ALL:
                BaseEntity.ResultBean resultBean = result.getResult();
                ProductBasicList basicList = (ProductBasicList) resultBean.getData();
                new Thread(new CachRunnale(basicList.getList())).start();
//                LogUtils.e("onSuccessTime QUERY_ALL "+String.valueOf(System.currentTimeMillis() - mTimeStartQUERY_ALL));
                break;
            case REQUEST_UNREAD:
                UnReadData unReadData = (UnReadData) result.getResult().getData();
                if (unReadData.getUnread()) {
                    mMsgHite.setVisibility(View.VISIBLE);
                } else {
                    mMsgHite.setVisibility(View.GONE);
                }
//                LogUtils.e("onSuccessTime REQUEST_UNREAD "+String.valueOf(System.currentTimeMillis() - mTimeStartREQUEST_UNREAD));
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
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次首次进来，先获取基本商品列表,暂时缓存到内存里。
//        if (SPUtils.isLogin(mContext)){
//            queryProductList();
//        }URL	http://develop.runwise.cn/gongfu/message/unread/
        if (isLogin) {
            Object request = null;
            sendConnection("/gongfu/message/unread", request, REQUEST_UNREAD, false, UnReadData.class);
            mTimeStartREQUEST_UNREAD = System.currentTimeMillis();
        }
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
        int tabIndex = intent.getIntExtra(INTENT_KEY_TAB,-1);
        if(tabIndex!=-1)gotoTabByIndex(tabIndex);
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        isLogin = true;
        if (mTabHost != null) {
            mTabHost.setCurrentTab(0);
        }
        queryProductList();
    }

    @Override
    public void onUserLoginout() {
        isLogin = false;
        if (mTabHost != null) {
            mTabHost.setCurrentTab(0);
        }
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
        ToastUtil.show(mContext, message);
//        ((TextView) findViewById(R.id.hello_scan)).append("\n" + message);
    }

    private void upLoadVersion(){
        VersionRequest versionRequest = new VersionRequest();
        versionRequest.setVersion_name("安卓" + CommonUtils.getVersionName(this));
        sendConnection("/gongfu/v2/user/app/version/", versionRequest, REQUEST_UPLOAD_VERSION, false, null);
    }

    private void queryProductList() {
        Object request = null;
        sendConnection("/gongfu/v2/product/list/", request, QUERY_ALL, false, ProductBasicList.class);
        mTimeStartQUERY_ALL = System.currentTimeMillis();
    }

    public int getCurrentTabIndex() {
        if (mTabHost == null) {
            return 0;
        }
        return mTabHost.getCurrentTab();
    }

    //跳转到哪个tab页下面
    public void gotoTabByIndex(int index) {
        mTabHost.setCurrentTab(index);
    }
}
