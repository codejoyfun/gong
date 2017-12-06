package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.view.webview.X5WebView;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.HashMap;
import java.util.Map;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_DB_NAME;


/**
 * Created by libin on 2017/6/29.
 */

public class PageDeatailActivity extends NetWorkActivity {
    @ViewInject(R.id.webview)
    private X5WebView webview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.page_detail_webview);
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        setTitleText(true,"详情");
        String url = getIntent().getStringExtra("url");
        Map<String, String > map = new HashMap<String, String>() ;
        String db = "";
        if(SPUtils.isLogin(this))db = (String)SPUtils.get(mContext,FILE_KEY_DB_NAME,"");
        else db = Constant.UNLOGIN_DB;
        map.put("X-Odoo-Db", db);
        webview.loadUrl(url,map);
    }
    @OnClick(R.id.title_iv_left)
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
        }
    }
    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
