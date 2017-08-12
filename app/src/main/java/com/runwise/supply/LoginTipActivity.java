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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tip);
        setTitleText(false,"登陆验证");
        setTitleLeftIcon(true,R.drawable.nav_closed);
        mobel = this.getIntent().getStringExtra("mobel");
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
                Intent intent = new Intent(mContext, LoginRelogActivity.class);
                intent.putExtra("mobel",mobel);
                if (UserUtils.checkLogin(intent,this)) {
                    startActivity(intent);
                }
                finish();
                break;

        }
    }

}
