package com.runwise.supply;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.kids.commonframe.base.BaseActivity;

import java.util.List;

/**
 * Created by libin on 2017/3/26.
 */

public class SchemeActivity extends BaseActivity {
    private static  final String  CARSERIES = "series"; //车系详情页
    private static  final String  CARDETAIL = "detail"; //车详情页
    private static  final String  DEALER = "dealer"; //经销商详情页
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            List<String> pathSegments = uri.getPathSegments();
            String uriQuery = uri.getQuery();
            Intent intent;
            if (pathSegments != null && pathSegments.size() == 1) {
            } else if (pathSegments.get(0).contains(DEALER)) {
            } else if (pathSegments.get(0).contains(CARDETAIL)) {
            } else {
                finish();
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
