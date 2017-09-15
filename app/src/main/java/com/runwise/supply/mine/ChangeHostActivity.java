package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

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
    @ViewInject(R.id.list)
    private ListView listview;
    @ViewInject(R.id.tipTv)
    private TextView tipTv;
    private ArrayAdapter<String> adapter;
    private String[]  datas = {"海大数据库","老班长数据库","GoldenClient2017Test数据库",DEFAULT_DATABASE_NAME,"TestFor...Company数据库"};
    private String[] values = {"DemoforHD20170516","LBZ20170607","GoldenClient2017Test",DEFAULT_DATABASE_NAME,"Testfor...Company"};
    private int which;
    private boolean isLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.host_layout);
        isLogin = SPUtils.isLogin(mContext);
        setTitleText(true,"更改HOST");
        setTitleLeftIcon(true,R.drawable.nav_back);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,datas);
        listview.setAdapter(adapter);
        ToastUtil.show(mContext,"请选择你要切换的数据库");
        String dbStr = (String)SPUtils.get(mContext,"X-Odoo-Db",DEFAULT_DATABASE_NAME);
        for (int i = 0; i < datas.length; i++){
            String value = values[i];
            if (value.equals(dbStr)){
                tipTv.setText("当前所在数据库："+datas[i]+"\n切换数据库将会重新登录");
                break;
            }
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPositon = (int) id;
                switch (realPositon){
                    case 0:
                        dialog.setMessage("即将切换到海大数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                //删库，进入重新登录界面
                                if (isLogin){
                                    loginOut(LOGINOUT_HD);
                                }else{
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
                                if (isLogin){
                                    loginOut(LOGINOUT_LBZ);
                                }else{
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
                                if (isLogin){
                                    loginOut(LOGINOUT_Golden);
                                }else{
                                    switchDBByIndex(2);
                                }
                            }
                        });
                        dialog.show();

                        break;
                    case 3:
                        dialog.setMessage("即将切换到GoldenClient2017Test10数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                if (isLogin){
                                    loginOut(LOGINOUT_Golden2);
                                }else{
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
                                if (isLogin){
                                    loginOut(LOGINOUT_Test);
                                }else{
                                    switchDBByIndex(4);
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
        ProductBasicUtils.clearCache(mContext);
        SPUtils.put(mContext,"X-Odoo-Db",values[i]);
        gotoLogin();
    }

    private void loginOut(int type) {
        Object param = null;
        sendConnection("/gongfu/logout",param,type,true,null);
    }

    @OnClick(R.id.title_iv_left)
    public void btnClick(View view){
        switch(view.getId()){
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
        switch (where){
            case LOGINOUT_HD:
                switchDBByIndex(0);
                break;
            case LOGINOUT_LBZ:
                switchDBByIndex(1);
                break;
            case LOGINOUT_Golden:
                switchDBByIndex(2);
                break;
            case LOGINOUT_Golden2:
                switchDBByIndex(3);
                break;
            case LOGINOUT_Test:
                switchDBByIndex(4);
                break;

        }
        
    }

    private void gotoLogin() {
        Intent intent  = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
