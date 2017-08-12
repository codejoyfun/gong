package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.mine.CheckActivity;
import com.runwise.supply.tools.UserUtils;


public class LoginTipActivity extends BaseActivity {
    private String mobel;
    @ViewInject(R.id.finishBtn)
    private Button finishBtn;
    @ViewInject(R.id.tipText)
    private TextView tipText;

    private String username;
    private String pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tip);
        setTitleText(false,"登陆验证");
        setTitleLeftIcon(true,R.drawable.nav_closed);
        mobel = this.getIntent().getStringExtra("mobel");
        username = this.getIntent().getStringExtra("username");
        pwd = this.getIntent().getStringExtra("pwd");
        if(TextUtils.isEmpty(mobel)) {
            finishBtn.setVisibility(View.GONE);
            tipText.setText("该账号已在其他设备登陆\n由于没有绑定手机，请联系客服");
        }
        else {
            finishBtn.setVisibility(View.VISIBLE);
            tipText.setText("该账号已在其他设备登陆\n需要验证您的手机号码\n"+ CommonUtils.heandlerMobel(mobel));
        }
    }

    @OnClick({R.id.finishBtn,R.id.left_layout})
    public void doFinish(View view) {
        switch (view.getId()) {
            case R.id.left_layout:
                finish();
                break;
            case  R.id.finishBtn:
                dialog.setTitle("确认手机号码");
                dialog.setMessage("我们将发送短信到这个号码：\n"+CommonUtils.heandlerMobel(mobel));
                dialog.setMessageGravity();
                dialog.setModel(CustomDialog.BOTH);
                dialog.setLeftBtnListener("取消",null);
                dialog.setRightBtnListener("确定", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        Intent intent = new Intent(mContext, LoginRelogActivity.class);
                        intent.putExtra("mobel",mobel);
                        intent.putExtra("username",username);
                        intent.putExtra("pwd",pwd);
                        if (UserUtils.checkLogin(intent,LoginTipActivity.this)) {
                            startActivity(intent);
                        }
                        finish();
                    }
                });
                dialog.show();
                break;

        }
    }

}
