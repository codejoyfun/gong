package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.kids.commonframe.base.WebViewActivity;
import com.runwise.supply.R;

/**
 * Created by Dong on 2017/12/19.
 */

public class VideoWebviewActivity extends WebViewActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x5WebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //不给intent打开app
                if(!url.startsWith("http")){
                    //Toast.makeText(WebViewActivity.this,url,Toast.LENGTH_LONG).show();
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        });

        ImageView ivClose = (ImageView)findViewById(com.kids.commonframe.R.id.title_iv_left2);
        ivClose.setVisibility(View.VISIBLE);
        ivClose.setImageResource(R.drawable.nav_closed);
        ivClose.setOnClickListener(v->{
            Intent intent = new Intent(this,SettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }
}
