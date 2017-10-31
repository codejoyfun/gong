package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import static android.os.Build.VERSION_CODES.KITKAT;
import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_HOST;
import static com.kids.commonframe.base.util.net.NetWorkHelper.DEFAULT_DATABASE_NAME;

/**
 * Created by libin on 2017/8/11.
 */

public class ChangeHostActivity extends NetWorkActivity {
    private static final int LOGINOUT_HD = 0;
    private static final int LOGINOUT_LBZ = 1;
    private static final int LOGINOUT_Golden = 2;
    private static final int LOGINOUT_Golden2 = 3;
    private static final int LOGINOUT_Test = 4;
    private static final int LOGINOUT_NONE = 5;
    private static final int LOGINOUT_CUSTOMER = 6;
    @ViewInject(R.id.list)
    private ListView listview;
    @ViewInject(R.id.tipTv)
    private TextView tipTv;
    @ViewInject(R.id.rb_formal)
    private RadioButton mRbFormal;
    @ViewInject(R.id.rb_test)
    private RadioButton mRbTest;
    @ViewInject(R.id.rb_custom)
    private RadioButton mRbCustom;
    @ViewInject(R.id.radioGroupID)
    private RadioGroup mRadioGroupID;
    @ViewInject(R.id.et_host)
    private EditText mEtHost;
    private ArrayAdapter<String> adapter;
    private String[] datas = {"海大数据库", "老班长数据库", "GoldenClient2017Test数据库", "LBZ-Test", "TestFor...Company数据库", "不设置数据库"};
private String[] values = {"DemoforHD20170516", "LBZ20170607", "GoldenClient2017Test", "LBZ-Test", "Testfor...Company", ""};
    private int which;
    @ViewInject(R.id.et_database)
    private EditText mEtDatabase;
    @ViewInject(R.id.btn_confirm)
    private Button mBtnConfirm;
    private boolean isLogin;
    String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT <= KITKAT) {
            setTheme(R.style.hostStyle);
        }
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.host_layout);
        isLogin = SPUtils.isLogin(mContext);
        setTitleText(true, "更改HOST");
        setTitleLeftIcon(true, R.drawable.nav_back);
        mRadioGroupID.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_formal:
                        mEtHost.setVisibility(View.GONE);
                        mEtDatabase.setVisibility(View.GONE);
                        mBtnConfirm.setVisibility(View.GONE);
                        if (!isLogin){
                            Constant.BASE_URL = Constant.RELEASE_URL;
                            SPUtils.put(mContext, FILE_KEY_HOST, Constant.RELEASE_URL);
                        }else{
                            mUrl = Constant.RELEASE_URL;
                        }
                        listview.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_test:
                        mEtHost.setVisibility(View.GONE);
                        mEtDatabase.setVisibility(View.GONE);
                        mBtnConfirm.setVisibility(View.GONE);
                        if (!isLogin){
                            Constant.BASE_URL = Constant.DEBUG_URL;
                            SPUtils.put(mContext, FILE_KEY_HOST, Constant.DEBUG_URL);
                        }else{
                            mUrl = Constant.DEBUG_URL;
                        }
                        listview.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_custom:
                        mEtHost.setVisibility(View.VISIBLE);
                        mEtDatabase.setVisibility(View.VISIBLE);
                        mBtnConfirm.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                        break;
                }
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtHost.getText().toString())) {
                    toast("填写的host不能为空");
                    return;
                }
                //删库，进入重新登录界面
                if (isLogin) {
                    loginOut(LOGINOUT_CUSTOMER);
                } else {
                    ProductBasicUtils.clearCache(mContext);
                    Constant.BASE_URL = mEtHost.getText().toString();
                    SPUtils.put(mContext, FILE_KEY_HOST, Constant.BASE_URL);
                    SPUtils.put(mContext, "X-Odoo-Db", mEtDatabase.getText().toString());
                    gotoLogin();
                }

            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, datas);
        listview.setAdapter(adapter);
        ToastUtil.show(mContext, "请选择你要切换的数据库");
        String dbStr = (String) SPUtils.get(mContext, "X-Odoo-Db", DEFAULT_DATABASE_NAME);
        tipTv.setText("当前所在数据库：" + dbStr + "\n切换数据库将会重新登录");
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPositon = (int) id;
                switch (realPositon) {
                    case 0:
                        dialog.setMessage("即将切换到海大数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                //删库，进入重新登录界面
                                if (isLogin) {
                                    loginOut(LOGINOUT_HD);
                                } else {
                                    switchDBByIndex(0);

                                }

                            }
                        });
                        dialog.show();
                        break;
                    case 1:
                        dialog.setMessage("即将切换到老班长数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                //删库，进入重新登录界面
                                if (isLogin) {
                                    loginOut(LOGINOUT_LBZ);
                                } else {
                                    switchDBByIndex(1);
                                }

                            }
                        });
                        dialog.show();
                        break;
                    case 2:
                        dialog.setMessage("即将切换到GoldenClient2017Test数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                if (isLogin) {
                                    loginOut(LOGINOUT_Golden);
                                } else {
                                    switchDBByIndex(2);
                                }
                            }
                        });
                        dialog.show();

                        break;
                    case 3:
                        dialog.setMessage("即将切换到" + datas[3] + "数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                if (isLogin) {
                                    loginOut(LOGINOUT_Golden2);
                                } else {
                                    switchDBByIndex(3);
                                }

                            }
                        });
                        dialog.show();
                        break;
                    case 4:
                        dialog.setMessage("即将切换到Testfor...Company数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                if (isLogin) {
                                    loginOut(LOGINOUT_Test);
                                } else {
                                    switchDBByIndex(4);
                                }

                            }
                        });
                        dialog.show();

                        break;
                    case 5:
                        dialog.setMessage("不设置数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                if (isLogin) {
                                    loginOut(LOGINOUT_NONE);
                                } else {
                                    switchDBByIndex(5);
                                }

                            }
                        });
                        dialog.show();

                        break;
                }
            }
        });
    }

    private void switchDBByIndex(int i) {
        if (!mRbFormal.isChecked() && !mRbTest.isChecked() && !mRbCustom.isChecked()) {
            toast("你还没选择环境!");
            return;
        }
        ProductBasicUtils.clearCache(mContext);
        SPUtils.put(mContext, "X-Odoo-Db", values[i]);
        gotoLogin();
    }

    private void loginOut(int type) {
        Object param = null;
        sendConnection("/gongfu/logout", param, type, true, null);
    }

    @OnClick(R.id.title_iv_left)
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        SPUtils.loginOut(mContext);
        //退出登录
        EventBus.getDefault().post(new UserLogoutEvent());
        switch (where) {
            case LOGINOUT_HD:
                if (!TextUtils.isEmpty(mUrl)){
                    SPUtils.put(mContext, FILE_KEY_HOST, mUrl);
                    Constant.BASE_URL = mUrl;
                }
                switchDBByIndex(0);
                break;
            case LOGINOUT_LBZ:
                if (!TextUtils.isEmpty(mUrl)){
                    SPUtils.put(mContext, FILE_KEY_HOST, mUrl);
                    Constant.BASE_URL = mUrl;
                }
                switchDBByIndex(1);
                break;
            case LOGINOUT_Golden:
                if (!TextUtils.isEmpty(mUrl)){
                    SPUtils.put(mContext, FILE_KEY_HOST, mUrl);
                    Constant.BASE_URL = mUrl;
                }
                switchDBByIndex(2);
                break;
            case LOGINOUT_Golden2:
                if (!TextUtils.isEmpty(mUrl)){
                    SPUtils.put(mContext, FILE_KEY_HOST, mUrl);
                    Constant.BASE_URL = mUrl;
                }
                switchDBByIndex(3);
                break;
            case LOGINOUT_Test:
                if (!TextUtils.isEmpty(mUrl)){
                    SPUtils.put(mContext, FILE_KEY_HOST, mUrl);
                    Constant.BASE_URL = mUrl;
                }
                switchDBByIndex(4);
                break;
            case LOGINOUT_NONE:
                if (!TextUtils.isEmpty(mUrl)){
                    SPUtils.put(mContext, FILE_KEY_HOST, mUrl);
                    Constant.BASE_URL = mUrl;
                }
                switchDBByIndex(5);
                break;
            case LOGINOUT_CUSTOMER:
                Constant.BASE_URL = mEtHost.getText().toString();
                SPUtils.put(mContext, FILE_KEY_HOST, Constant.BASE_URL);
                SPUtils.put(mContext, "X-Odoo-Db", mEtDatabase.getText().toString());
                ProductBasicUtils.clearCache(mContext);
                gotoLogin();
                break;

        }

    }

    private void gotoLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
