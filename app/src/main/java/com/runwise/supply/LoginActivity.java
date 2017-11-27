package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kids.commonframe.base.ActivityManager;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.CheckVersionManager;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.LoginData;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.GetHostRequest;
import com.runwise.supply.entity.HostResponse;
import com.runwise.supply.entity.LoginRequest;
import com.runwise.supply.entity.RemUser;
import com.runwise.supply.tools.MyDbUtil;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_DB_NAME;
import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_HOST;
import static com.runwise.supply.FindPasswordActivity.INTENT_KEY_COMPANY_NAME;


public class LoginActivity extends NetWorkActivity {
    private static final int LOGIN = 1;
    private static final int USER_INFO = 2;
    private static final int GET_HOST = 3;
    public static Intent targerIntent;
    /**
     * 登录按钮
     */
    @ViewInject(R.id.login_btn)
    private Button login_btn;

    private boolean holdCode;
    @ViewInject(R.id.teacher_reg_phone)
    private EditText mPhone;
    @ViewInject(R.id.cet_company)
    private EditText mCetCompany;
    @ViewInject(R.id.teacher_reg_password)
    private EditText mPassword;
    @ViewInject(R.id.teacher_reg_password_see)
    private CheckBox mPasswordSee;

    private LoginPopUtil loginPopUtil;
    @ViewInject(R.id.topLayout)
    private View topLayout;
    @ViewInject(R.id.login_pop_btn)
    private ImageView loginPopIcon;

    @ViewInject(R.id.remPassword)
    private CheckBox remPassword;
    @ViewInject(R.id.login_pop_btn)
    private View loginPopBtn;

    private RemListAdapter remListAdapter;
    private LoginRequest loginRequest;

    private DbUtils mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		overridePendingTransition(R.anim.slide_in_bottom,R.anim.activity_open_exit);
        Intent intent = this.getIntent();
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        this.setContentView(R.layout.activity_login);
//		String phoneNumber = SharePrenceUtil.getTempPhoneNumber(this);d
//		if ( !TextUtils.isEmpty(phoneNumber) ) {
//			ed_userName.setText(phoneNumber);
//			ed_pass.requestFocus();
//		}
        mPasswordSee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        login_btn.setEnabled(false);
        mPhone.addTextChangedListener(new TextWatchListener());
        mPassword.addTextChangedListener(new TextWatchListener());

        loginPopUtil = new LoginPopUtil(this);
        remListAdapter = new RemListAdapter();
        loginPopUtil.setAdapter(remListAdapter);
        loginPopUtil.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RemUser user = (RemUser) parent.getAdapter().getItem(position);
                mPhone.setText(user.getUserName());
                mPassword.setText(user.getPassword());
                mCetCompany.setText(user.getCompany());
                loginPopUtil.hidePop();
            }
        });
        showFirstUserFromDB();
    }

    public void showFirstUserFromDB(){
        mDb = MyDbUtil.create(this);
        try {
            List<RemUser> userList = mDb.findAll(Selector.from(RemUser.class).orderBy("id", true));
            if (userList != null && !userList.isEmpty()) {
                RemUser rem = userList.get(0);
                mPhone.setText(rem.getUserName());
                mPassword.setText(rem.getPassword());
                mCetCompany.setText(rem.getCompany());
            }

            if (userList == null || userList.size() <= 1) {
                loginPopBtn.setVisibility(View.GONE);
            } else {
                loginPopBtn.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDb.close();
    }

    private void getHost(String companyName) {
        GetHostRequest getHostRequest = new GetHostRequest();
        getHostRequest.setCompanyName(companyName);
        sendConnection("http://develop.runwise.cn:9000", "/api/get/host", getHostRequest, GET_HOST, true, HostResponse.class);
    }

    @OnClick(R.id.root_layout)
    public void hideInput(View v) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.login_pop_btn)
    public void doLoginPopBtn(View view) {
        DbUtils mDb = MyDbUtil.create(this);
        try {
            //没多个历史帐号时，不需要有下拉箭头
            List<RemUser> userList = mDb.findAll(Selector.from(RemUser.class).orderBy("id", true));
            if (userList != null && !userList.isEmpty() && userList.size() > 1) {
                remListAdapter.setData(userList);
                loginPopUtil.showPop(topLayout, loginPopIcon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDb.close();
    }

    private boolean isEmpty() {
        boolean empty = false;
        if (CommonUtils.isEmpty(mCetCompany.getText().toString())) {
            Toast.makeText(this, "公司名不能为空", Toast.LENGTH_SHORT).show();
            empty = true;
        } else if (CommonUtils.isEmpty(mPhone.getText().toString())) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            empty = true;
        } else if (CommonUtils.isEmpty(mPassword.getText().toString())) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            empty = true;
        }
        return empty;
    }


    @OnClick(R.id.login_btn)
    public void onLogin(View v) {
        if (isEmpty()) {
            return;
        }
//		if (!ParamsUtil.isPhoneNumberValid(userName)) {
//			ToastUtil.show(mContext, "用户名必须是手机号码或邮箱");
//
        getHost(mCetCompany.getText().toString());
    }

    private void login() {
        String userName = mPhone.getText().toString().trim();
        String passowrd = mPassword.getText().toString();

        loginRequest = new LoginRequest();
        loginRequest.setLogin(userName);
        loginRequest.setPassword(passowrd);
        String registrationId = JPushInterface.getRegistrationID(this);
        loginRequest.setRegistrationID(registrationId);
        sendConnection("/gongfu/v2/authenticate", loginRequest, LOGIN, true, LoginData.class);
    }

    /*
     * 注册
     */
    @OnClick(R.id.login_register)
    public void onReg(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        this.startActivity(intent);
    }

    /*
     * 忘记密码
     */
    @OnClick(R.id.login_find)
    public void onFind(View v) {
        if (TextUtils.isEmpty(mCetCompany.getText().toString())){
            Toast.makeText(this, "公司名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, FindPasswordActivity.class);
        intent.putExtra(INTENT_KEY_COMPANY_NAME,mCetCompany.getText().toString());
        this.startActivity(intent);
    }

    HostResponse mHostResponse;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case LOGIN:
                BaseEntity.ResultBean resultBean = result.getResult();
                LoginData loginData = (LoginData) resultBean.getData();
                UserInfo userInfoData = loginData.getUser();
                if ("false".equals(loginData.getIsSuccess())) {
                    Intent intent = new Intent(mContext, LoginTipActivity.class);
                    intent.putExtra("mobel", loginData.getMobile());
                    intent.putExtra("username", loginRequest.getLogin());
                    intent.putExtra("pwd", loginRequest.getPassword());
                    startActivity(intent);
                    return;
                }
                    mDb = MyDbUtil.create(this);
                    try {
                        RemUser rem = mDb.findFirst(Selector.from(RemUser.class).where(WhereBuilder.b("userName", "=", loginRequest.getLogin())));
                        if (rem != null) {
                            mDb.delete(rem);
                        }
                        RemUser newRem = new RemUser();
                        newRem.setUserName(loginRequest.getLogin());
                        if (remPassword.isChecked()){
                            newRem.setPassword(loginRequest.getPassword());
                        }else{
                            newRem.setPassword("");
                        }
                        newRem.setCompany(mCetCompany.getText().toString());
                        mDb.save(newRem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mDb.close();
                GlobalApplication.getInstance().saveUserInfo(userInfoData);
//				ToastUtil.show(mContext,"登录成功");
                //@libin added
                if (!"true".equals(userInfoData.getIsAgreeItem())) {
                    InfoActivity.targerIntent = targerIntent;
                    Intent intent = new Intent(this, InfoActivity.class);
                    startActivity(intent);
                } else {
                    EventBus.getDefault().post(new UserLoginEvent());
                    SPUtils.setLogin(mContext, true);
                    ActivityManager.getInstance().finishAll();
                    startActivity(new Intent(getActivityContext(), MainActivity.class));
//					JPushInterface.setAliasAndTags(getApplicationContext(),CommonUtils.getDeviceId(this) , null, null);
//					if (targerIntent != null) {
//						startActivity(targerIntent);
//					}
                }
                this.finish();
                overridePendingTransition(R.anim.activity_close_enter, R.anim.slide_out_bottom);
                break;
            case USER_INFO:
//				UserInfoResult userInfoResult = (UserInfoResult) result;
//				UserInfoData userInfoData = userInfoResult.getData();
//
//				UserLoginEvent loginEvent = new UserLoginEvent();
//				loginEvent.setIntent(targerIntent);
//				EventBus.getDefault().post(loginEvent);
//
//				ToastUtil.show(mContext,"登录成功");
//				if (targerIntent != null) {
//					startActivity(targerIntent);
//				}
//				this.finish();
                break;
            case GET_HOST:
                mHostResponse = (HostResponse) result.getResult().getData();
                if (TextUtils.isEmpty(mHostResponse.getPort())) {
                    SPUtils.put(getActivityContext(), FILE_KEY_HOST, mHostResponse.getHost());
                } else {
                    SPUtils.put(getActivityContext(), FILE_KEY_HOST, mHostResponse.getHost() + ":" + mHostResponse.getPort());
                }
                SPUtils.put(getActivityContext(), FILE_KEY_DB_NAME, mHostResponse.getDbName());
                //检查版本
                CheckVersionManager checkVersionManager = new CheckVersionManager(this);
                checkVersionManager.checkVersion(false);
                login();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext, errMsg);
    }

    @OnClick(R.id.closeBtn)
    public void doBack(View v) {
        this.finish();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.slide_out_bottom);
    }

    private class TextWatchListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setFinishStatus();
        }
    }

    private void setFinishStatus() {
        String phoneNumber = mPhone.getText().toString().trim();
        String code = mPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code)) {
            login_btn.setEnabled(true);
        } else {
            login_btn.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        targerIntent = null;
//		if(mDb != null) {
//			mDb.close();
//		}
    }


    public class RemListAdapter extends IBaseAdapter<RemUser> {
        @Override
        protected View getExView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_rem, null);
                //viewHolder.shopType = (ImageView)convertView.findViewById(R.id.shoptype);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final RemUser user = mList.get(position);
            //设置值
            viewHolder.userName.setText(user.getUserName());
            viewHolder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.remove(position);
                    if (mList == null || mList.size() <= 1) {
                        loginPopBtn.setVisibility(View.GONE);
                    } else {
                        loginPopBtn.setVisibility(View.VISIBLE);
                    }
                    DbUtils mDb = MyDbUtil.create(mContext);
                    try {
                        if (user != null) {
                            mDb.delete(user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyDataSetChanged();
                    showFirstUserFromDB();
                }
            });
            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.userName)
            TextView userName;
            @ViewInject(R.id.deleteImg)
            ImageView deleteImg;
        }
    }
}
