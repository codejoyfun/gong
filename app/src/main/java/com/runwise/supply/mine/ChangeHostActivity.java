package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
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
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by libin on 2017/8/11.
 */

public class ChangeHostActivity extends NetWorkActivity {
    private static final int REQUEST_LOGINOUT = 0;
    @ViewInject(R.id.list)
    private ListView listview;
    @ViewInject(R.id.tipTv)
    private TextView tipTv;
    private ArrayAdapter<String> adapter;
    private String[]  datas = {"海大数据库","老班长数据库","GoldenClient2017Test数据库","TestFor...Companyo数据库"};
    private int which;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.host_layout);
        setTitleText(true,"更改HOST");
        setTitleLeftIcon(true,R.drawable.nav_back);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,datas);
        listview.setAdapter(adapter);
        ToastUtil.show(mContext,"请选择你要切换的数据库");
        String dbStr = (String)SPUtils.get(mContext,"X-Odoo-Db","LBZ20170607");
        if (dbStr.equals("LBZ20170607")){
            tipTv.setText("当前所在数据库：老班长数据库\n切换数据库将会重新登录");
        }else if(dbStr.equals("DemoforHD20170516")){
            tipTv.setText("当前所在数据库：海大数据库\n切换数据库将会重新登录");
        }else if(dbStr.equals("GoldenClient2017Test")){
            tipTv.setText("当前所在数据库：GoldenClient2017Test数据库\n切换数据库将会重新登录");
        }else if(dbStr.equals("Testfor...Company")){
            tipTv.setText("当前所在数据库：TestFor...Company数据库\n切换数据库将会重新登录");
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
                                which = 0;
                                Object param = null;
                                sendConnection("/gongfu/logout",param,REQUEST_LOGINOUT,true,null);
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
                                which = 1;
                                Object param = null;
                                sendConnection("/gongfu/logout",param,REQUEST_LOGINOUT,true,null);
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
                                which = 2;
                                Object param = null;
                                sendConnection("/gongfu/logout",param,REQUEST_LOGINOUT,true,null);
                            }
                        });
                        dialog.show();

                        break;
                    case 3:
                        dialog.setMessage("即将切换到Testfor...Company数据库");
                        dialog.setMessageGravity();
                        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                which = 3;
                                Object param = null;
                                sendConnection("/gongfu/logout",param,REQUEST_LOGINOUT,true,null);
                            }
                        });
                        dialog.show();

                        break;
                }
            }
        });
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
            case REQUEST_LOGINOUT:
                if (which == 0){
                    SPUtils.put(mContext,"X-Odoo-Db","DemoforHD20170516");
                }else if(which == 1){
                    SPUtils.put(mContext,"X-Odoo-Db","LBZ20170607");
                }else if(which == 2){
                    SPUtils.put(mContext,"X-Odoo-Db","GoldenClient2017Test");
                }else if (which == 3){
                    SPUtils.put(mContext,"X-Odoo-Db","Testfor...Company");
                }
                Intent intent  = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
