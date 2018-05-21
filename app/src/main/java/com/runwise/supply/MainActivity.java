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
import com.kids.commonframe.base.LoginData;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.SelfOrderTimeStatisticsUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.entity.LoginRequest;
import com.runwise.supply.entity.RemUser;
import com.runwise.supply.entity.UnReadData;
import com.runwise.supply.entity.UpdateTimeRequest;
import com.runwise.supply.entity.UpdateTimeResponse;
import com.runwise.supply.event.PlatformNotificationEvent;
import com.runwise.supply.firstpage.UnLoginedFirstFragment;
import com.runwise.supply.firstpage.entity.VersionRequest;
import com.runwise.supply.message.MessageFragment;
import com.runwise.supply.message.entity.DetailResult;
import com.runwise.supply.mine.MineFragment;
import com.runwise.supply.orderpage.OrderFragmentV2;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.MainRepertoryFragment;
import com.runwise.supply.tools.MyDbUtil;
import com.runwise.supply.tools.PlatformNotificationManager;
import com.runwise.supply.tools.RunwiseService;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.vov.vitamio.utils.Log;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_PASSWORD;


public class MainActivity extends NetWorkActivity {
    public static final String INTENT_KEY_TAB = "tab";
    //缓存全部商品列表的标识
    private final int REQUEST_UNREAD = 2;
    private final int REQUEST_UPLOAD_VERSION = 3;
    private final int REQUEST_SYSTEM_UPGRADE_TIME = 4;
    private final int REQUEST_LOGIN = 5;
    private static final int CHECK_UPGRADE_INTERVAL = 1000 * 60 * 10;//10分钟查询间隔

    //    private int devicesConnected = -1;
    private long mExitTime;
    @ViewInject(android.R.id.tabhost)
    private FragmentTabHost mTabHost;
    //未读小红点
//    private TextView mMsgHite;
    //    private UserInfo userInfo;
    private boolean isLogin;
    public static final String INTENT_KEY_SKIP_TO_LOGIN = "intent_key_skip_to_login";

    long mTimeStartQUERY_ALL;

    long mTimeStartREQUEST_UNREAD;

    DbUtils mDbUtils;

    //缓存基本商品信息到内存，便于每次查询对应productid所需基本信息
    private class CachRunnale implements Runnable {
        private List<ProductBasicList.ListBean> basicList;

        public CachRunnale(List basicList) {
            this.basicList = basicList;
        }

        @Override
        public void run() {
            mDbUtils = MyDbUtil.create(MainActivity.this);
            HashMap<String, ProductBasicList.ListBean> map = new HashMap<>();
            mDbUtils.configAllowTransaction(true);
            try {
                mDbUtils.saveOrUpdateAll(basicList);
                for (ProductBasicList.ListBean bean : basicList) {
                    map.put(String.valueOf(bean.getProductID()), bean);
                }
//                dbUtils = MyDbUtil.create(MainActivity.this);
                List<ProductBasicList.ListBean> list = mDbUtils.findAll(ProductBasicList.ListBean.class);
//                Log.d("haha","total in db:"+list.size());
                for (ProductBasicList.ListBean bean : list) {
                    String keyId = bean.getProductID() + "";
                    if (!map.containsKey(keyId)) {
                        if (bean.getImage() == null) {//TODO:xutils的坑，没有load imagebean？
                            bean.setImage(new ImageBean());
                        }
                        map.put(keyId, bean);
                    }
                }
                ProductBasicUtils.setBasicMap(map);
            } catch (DbException e) {
                e.printStackTrace();
            }
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
            Constant.BASE_URL = (String) SPUtils.get(getActivityContext(), SPUtils.FILE_KEY_HOST, "");
            if (TextUtils.isEmpty(Constant.BASE_URL)) {
                logout();
                return;
            }
            //检查版本
            CheckVersionManager checkVersionManager = new CheckVersionManager(this);
            checkVersionManager.checkVersion(false);
        } else {
            Constant.BASE_URL = Constant.UNLOGIN_URL;
        }
        initTabView();
        String registrationID = JPushInterface.getRegistrationID(this);
        Log.i("JPushInterface", "dfd " + registrationID);
//        Capture.init(getApplicationContext());
//        CaptureClient mClient = new CaptureClient();
//        mClient.setListener(mListener);
//        mClient.connect();
        //每次首次进来，先获取基本商品列表,暂时缓存到内存里。

        String tab1 = getString(R.string.tab_1);
        String tab2 = getString(R.string.tab_2);
        String tab3 = getString(R.string.tab_3);
        String tab5 = getString(R.string.tab_5);
        mTags = new String[]{tab1, tab2, tab3, tab5};
        if (getIntent().getBooleanExtra(INTENT_KEY_SKIP_TO_LOGIN, false)) {
            startActivity(new Intent(getActivityContext(), LoginActivity.class));
        } else {

        }
    }

    private void logout() {
        SPUtils.loginOut(mContext);
        ProductBasicUtils.clearBasicMap();
        SelfOrderTimeStatisticsUtil.clear();
        DbUtils dbUtils = MyDbUtil.create(getApplicationContext());
        try {
            dbUtils.deleteAll(ProductBasicList.ListBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        MessageFragment.isLogin = false;
        SampleApplicationLike.getInstance().cleanUesrInfo();
        JPushInterface.setAliasAndTags(getApplicationContext(), "", null, null);
        //退出登录
        ActivityManager.getInstance().finishAll();
        EventBus.getDefault().post(new UserLogoutEvent());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_KEY_SKIP_TO_LOGIN, true);
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
        mTabHost.addTab(createTabSpace(R.drawable.tab_1_selector, R.string.tab_1), UnLoginedFirstFragment.class, null);
        mTabHost.addTab(createTabSpace(R.drawable.tab_2_selector, R.string.tab_2), OrderFragmentV2.class, null);
        mTabHost.addTab(createTabSpace(R.drawable.tab_3_selector, R.string.tab_3), MainRepertoryFragment.class, null);
        mTabHost.addTab(createTabSpace(R.drawable.tab_5_selector, R.string.tab_5), MineFragment.class, null);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < mTags.length; i++) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(mTags[i]);
                    if (fragment != null) {
                        fragment.setUserVisibleHint(false);
                    }
                }
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tabId);
                if (fragment != null) {
                    fragment.setUserVisibleHint(true);
                }
            }
        });
//        库存要判断商品数据是否load在本地
        mTabHost.getTabWidget().getChildTabViewAt(2)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(SPUtils.isLogin(getActivityContext())){
                            if (!ProductBasicUtils.isInit(getActivityContext())) {
                                return;
                            }
                        }
                        mTabHost.setCurrentTab(2);

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
//                mMsgHite = (TextView) subTabView.findViewById(R.id.tv_hint);
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
            case REQUEST_UNREAD:
                UnReadData unReadData = (UnReadData) result.getResult().getData();
                DetailResult.ListBean bean = PlatformNotificationManager.getInstance(this).getLastMessage();
                if (unReadData.getUnread() || (bean != null && !bean.isSeen())) {
//                    mMsgHite.setVisibility(View.VISIBLE);
                } else {
//                    mMsgHite.setVisibility(View.GONE);
                }
//                LogUtils.e("onSuccessTime REQUEST_UNREAD "+String.valueOf(System.currentTimeMillis() - mTimeStartREQUEST_UNREAD));
                break;
            case REQUEST_SYSTEM_UPGRADE_TIME:
                UpdateTimeResponse response = (UpdateTimeResponse) result.getResult().getData();
                SystemUpgradeHelper.getInstance(this).create(response.getStartDate(), response.getEndDate());
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

    private long lastSystemQuery = 0;//上次查询系统更新通知的时间

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //每次首次进来，先获取基本商品列表,暂时缓存到内存里。
        login();
        if (isLogin) {
//            首页再去拿商品列表
            Intent startIntent = new Intent(getActivityContext(), RunwiseService.class);
            startService(startIntent);
            Object request = null;
            sendConnection("/gongfu/message/unread", request, REQUEST_UNREAD, false, UnReadData.class);
            mTimeStartREQUEST_UNREAD = System.currentTimeMillis();

            //查询系统更新
            long currentTime = System.currentTimeMillis();
            UserInfo userInfo = SampleApplicationLike.getInstance().loadUserInfo();
            if (userInfo == null) return;
            DbUtils db = MyDbUtil.create(this);
            try {
                RemUser rem = db.findFirst(Selector.from(RemUser.class).where(WhereBuilder.b("userName", "=", userInfo.getLogin())));
                if (rem != null && currentTime - lastSystemQuery > CHECK_UPGRADE_INTERVAL) {//10 minutes
                    lastSystemQuery = currentTime;
                    //get company name
                    Object systemUpdateTimeRequest = new UpdateTimeRequest(rem.getCompany());
                    sendConnection(Constant.UNLOGIN_URL, "/api/system/update", systemUpdateTimeRequest,
                            REQUEST_SYSTEM_UPGRADE_TIME, false, UpdateTimeResponse.class, true);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
//        if (mDbUtils != null){
//            mDbUtils.close();
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tabIndex = intent.getIntExtra(INTENT_KEY_TAB, -1);
        if (tabIndex != -1) gotoTabByIndex(tabIndex);
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        isLogin = true;
        if (mTabHost != null) {
            mTabHost.setCurrentTab(0);
        }
//        queryProductList();
    }

    @Override
    public void onUserLoginout() {
        isLogin = false;
        if (mTabHost != null) {
            mTabHost.setCurrentTab(0);
        }
    }

    @Subscribe
    public void refresh(PlatformNotificationEvent event) {
//        mMsgHite.setVisibility(View.VISIBLE);
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

    private void upLoadVersion() {
        VersionRequest versionRequest = new VersionRequest();
        versionRequest.setVersion_name("安卓" + CommonUtils.getVersionName(this));
        sendConnection("/gongfu/v2/user/app/version/", versionRequest, REQUEST_UPLOAD_VERSION, false, null);
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

    private void login() {
        Object object = SPUtils.readObject(getActivityContext(), SPUtils.FILE_KEY_USER_INFO);
        if (object != null) {
            UserInfo userInfo = (UserInfo) object;
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setLogin(userInfo.getLogin());
            String password = (String) SPUtils.get(getActivityContext(), FILE_KEY_PASSWORD, "");
            if (TextUtils.isEmpty(password)) {
                return;
            }
            loginRequest.setPassword(password);
            String registrationId = JPushInterface.getRegistrationID(this);
            loginRequest.setRegistrationID(registrationId);
            sendConnection("/gongfu/v2/authenticate", loginRequest, REQUEST_LOGIN, false, LoginData.class);
        }

    }
}
