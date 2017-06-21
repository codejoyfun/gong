package com.kids.commonframe.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.kids.commonframe.R;

public class WebViewActivity extends BaseActivity {
    public static String WEB_TITLE = "web_title";
    public static String WEB_URL = "web_url";

    private ProgressBar progressBar;
    private WebView x5WebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        x5WebView = (WebView) findViewById(R.id.x5WebView);
        registerInterface(x5WebView);
        initWebViewSetting();
        this.setTitleLeftIcon(true,R.drawable.back_btn);

        Intent intent = this.getIntent();
        String title = intent.getStringExtra(WEB_TITLE);
        String url = intent.getStringExtra(WEB_URL);
        this.setTitleText(true,title);
        x5WebView.loadUrl(url);
        findViewById(R.id.left_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x5WebView.canGoBack()) {
                    x5WebView.goBack();
                }
                else {
                    finish();
                }
            }
        });
        x5WebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (progressBar != null && newProgress != 100) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                else if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    private void initWebViewSetting() {
        WebSettings webSetting = x5WebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setDisplayZoomControls(false);
        //webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        //webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    public void registerInterface(WebView x5WebView) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( x5WebView != null ) {
            x5WebView.destroyDrawingCache();
            x5WebView.destroy();
            x5WebView = null;
        }
    }

    @Override
    public void onBackPressed() {
        if(x5WebView.canGoBack()) {
            x5WebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}
