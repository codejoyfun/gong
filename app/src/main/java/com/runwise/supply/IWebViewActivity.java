package com.runwise.supply;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.kids.commonframe.base.WebViewActivity;

/**
 * Created by myChaoFile on 16/12/5.
 */

public class IWebViewActivity extends WebViewActivity {
    @Override
    public void registerInterface(WebView x5WebView) {
        super.registerInterface(x5WebView);
        x5WebView.addJavascriptInterface(new JSInterface(this), "JSInterface");
    }

    public class JSInterface {
        Context context;
        private Handler mHandler;

        public JSInterface(Context context) {
            this.context = context;
            mHandler = new Handler();
        }
        @JavascriptInterface
        public void gotoEmChat(final String emId) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

    }

}
