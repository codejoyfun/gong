package com.runwise.supply;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.business.CarSeriesActivity;
import com.runwise.supply.business.CarSettingFragmentContainer;
import com.runwise.supply.business.DealerDetainActivity;

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
        if (uri != null){
            List<String> pathSegments = uri.getPathSegments();
            String uriQuery = uri.getQuery();
            Intent intent;
            if (pathSegments != null && pathSegments.size() == 1) {
                if (pathSegments.get(0).contains(CARSERIES)){
                    String series_id = uri.getQueryParameter("id");
                    intent = new Intent(this, CarSeriesActivity.class);
                    intent.putExtra("isWebFrom",true);
                    intent.putExtra("series_id",series_id);
                    startActivity(intent);
                }else if(pathSegments.get(0).contains(DEALER)){
                    String dealer_id = uri.getQueryParameter("id");
                    intent = new Intent(this, DealerDetainActivity.class);
                    intent.putExtra("isWebFrom",true);
                    intent.putExtra("dealer_id",dealer_id);
                    startActivity(intent);
                }else if(pathSegments.get(0).contains(CARDETAIL)){
                    String car_id = uri.getQueryParameter("id");
                    intent = new Intent(this, CarSettingFragmentContainer.class);
                    intent.putExtra("isWebFrom",true);
                    intent.putExtra("carid",car_id);
                    startActivity(intent);
                }else{
                    finish();
                }
            }


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
