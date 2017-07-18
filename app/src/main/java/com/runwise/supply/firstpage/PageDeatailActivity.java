package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.view.webview.X5WebView;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.HashMap;
import java.util.Map;

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
        String url = getIntent().getStringExtra("url");
        Map<String, String > map = new HashMap<String, String>() ;
        map.put("X-Odoo-Db", "DemoforHD20170516");
        webview.loadUrl(url,map);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
