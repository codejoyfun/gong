package com.runwise.supply.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.UserInfo;
import com.runwise.supply.mine.entity.EditUserInfoRequest;
import com.runwise.supply.mine.entity.UpdateUserInfoRep;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ql on 2016/7/4.
 */
public class UpdateUserInfoActivity extends NetWorkActivity {

    public static final String TAG = UpdateUserInfoActivity.class.getSimpleName();
    public static final int UPDATE_USERINFO_NICKNAME = 1; //修改家长昵称
    public static final int UPDATE_USERINFO_AGE = 2; //修改年龄
    public static final int UPDATE_PHONE_NUMBER = 3; //修改手机号


    @ViewInject(R.id.update_userinfo_hint)
    private TextView mUserinfoHint;
    @ViewInject(R.id.update_userinfo_value)
    private EditText mUserinfoValueEdt;
    @ViewInject(R.id.update_userinfo_clear)
    private ImageView mUserinfoClear;
    @ViewInject(R.id.title_tv_rigth)
    private TextView tvRigth;

    private String title = "";
    private int type = -1;
    private String value = "";
    private String updateValue = "";
    private String hint = "";
    private EditUserInfoRequest request ;

    private int tMinLen = 0;
    private int tMaxLen = 0;

    private UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_userinfo);
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        initData();
        initView();
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }


    private void initData() {
        Intent intent =getIntent();
        if(intent != null){
            type = intent.getIntExtra("type", -1);
        }
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        final int updateType = type;
        switch (updateType){
            case UPDATE_USERINFO_NICKNAME:
                tMinLen = 1;
                tMaxLen = 10;
                value = userInfo.getNickname();
                title = "修改姓名";
                hint = "请输入姓名";
                mUserinfoValueEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tMaxLen)});
                mUserinfoValueEdt.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case UPDATE_USERINFO_AGE:
                tMinLen = 1;
                tMaxLen = 3;
//                value = userInfo.getAge();
                title = "修改年龄";
                hint = "请输入年龄，数字类型";
                mUserinfoHint.setGravity(Gravity.CENTER);
                mUserinfoValueEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tMaxLen)});
                mUserinfoValueEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case UPDATE_PHONE_NUMBER:
                tMinLen = 1;
                tMaxLen = 11;
                value = userInfo.getPhone();
                title = "修改手机号";
                hint = "请输入手机号，数字类型";
                mUserinfoHint.setGravity(Gravity.CENTER);
                mUserinfoValueEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tMaxLen)});
                mUserinfoValueEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            default:
                showMesDialog("更新类型错误", "确定", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        UpdateUserInfoActivity.this.finish();
                    }
                },null);
                break;
        }
    }

    private void initView() {
        this.setTitleText(true, title);
        this.setTitleRightText(true, "保存");
        this.mUserinfoHint.setText(hint);

        mUserinfoValueEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String pValue = mUserinfoValueEdt.getText().toString();
                if (TextUtils.isEmpty(pValue)) {
                    mUserinfoClear.setVisibility(View.GONE);
                    tvRigth.setEnabled(false);
                    tvRigth.setTextColor(Color.parseColor("#8a8a8a"));
                } else {
                    mUserinfoClear.setVisibility(View.VISIBLE);
                    if (!pValue.equals(value)) {
                        tvRigth.setEnabled(true);
                        tvRigth.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        tvRigth.setEnabled(false);
                        tvRigth.setTextColor(Color.parseColor("#8a8a8a"));
                    }
                }
            }
        });
        this.mUserinfoValueEdt.setText(value);
        mUserinfoClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserinfoValueEdt.setText("");
            }
        });
    }

    private void showMesDialog(String mess, String btnText, CustomDialog.DialogListener listener, DialogInterface.OnDismissListener mDismissListener) {
        if(dialog == null)
        {
            dialog = new CustomDialog(this);
        }
        dialog.setModel(CustomDialog.LEFT);
        dialog.setLeftBtnListener(btnText,listener);
        dialog.setOnDismissListener(mDismissListener);
        dialog.setMessage(mess);
        if(!isFinishing()){
            dialog.show();
        }
    }

    @OnClick(R.id.right_layout)
    public void onClick(View v){
        if(!tvRigth.isEnabled())return;
        updateValue = mUserinfoValueEdt.getText().toString().trim();
        if(request == null){
            request = new EditUserInfoRequest();
        }
        boolean check = true;
        switch (type){
            case UPDATE_USERINFO_NICKNAME:
                request.setUid(userInfo.getMember_id());
                request.setNickname(updateValue);
//                check = checkValue(updateValue,tMinLen,tMaxLen);
                break;
            case UPDATE_USERINFO_AGE:
                request.setUid(userInfo.getMember_id());
                request.setAge(updateValue);
//                check = checkValue(updateValue,tMinLen,tMaxLen);
                break;
            case UPDATE_PHONE_NUMBER:
                check = checkPhoneNumber(updateValue);
                request.setUid(userInfo.getMember_id());
                request.setPhone(updateValue);
                break;
        }
        if(check){
            LogUtil.e(TAG, JSON.toJSONString(request));
            this.sendConnection("/Appapi/user/edit_info", request, type, true, UpdateUserInfoRep.class);
        }
    }

    private boolean checkValue(String value,int minLength, int maxLength){
        if(TextUtils.isEmpty(value)){
            showMesDialog("请输入内容","确定",null,null);
            return false;
        }else if(value.length() < minLength || value.length() > maxLength || !value.matches("^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$")){
            showMesDialog("您输入的格式不正确","确定",null,null);
            return false;
        }
        return true;
    }

    /**
     * 验证手机格式
     */
    private boolean checkPhoneNumber(String mobiles){
        if(!isMobileNO(mobiles)) {
            showMesDialog("手机号不正确，请重新输入！","确定",null,null);
            return  false;
        }
        return true;
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("[1]\\d{10}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        Intent intent = new Intent();
        UserInfo userInfo = GlobalApplication.getInstance().loadUserInfo();
        switch (where){
            case UPDATE_USERINFO_NICKNAME:
                userInfo.setNickname(updateValue);
                intent.putExtra("nickname", updateValue);
                break;

            case UPDATE_USERINFO_AGE:
//                userInfo.setAge(updateValue);
                intent.putExtra("age", updateValue);
                break;
            case UPDATE_PHONE_NUMBER:
                userInfo.setPhone(updateValue);
                intent.putExtra("phone", updateValue);
                break;
        }
        GlobalApplication.getInstance().saveUserInfo(userInfo);
        ToastUtil.show(this, "修改成功");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(this,errMsg);
    }
}
