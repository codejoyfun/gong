package com.runwise.supply.orderpage;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;


/**
 * Created by libin on 2017/6/30.
 */

public class OneKeyOrderActivity extends NetWorkActivity {
    int[] loadingImgs = new int[31];
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingImg)
    private ImageView loadingImg;
    private int currentIndex;
    @ViewInject(R.id.loadingTv)
    private TextView loadingTv;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loadingImg.setImageResource(loadingImgs[currentIndex++]);
            if (currentIndex >= 31){
                currentIndex = 0;
            }
            handler.postDelayed(runnable,30);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onekey_order_layout);
        setTitleText(true,"智能下单");
        setTitleLeftIcon(true,R.drawable.nav_back);
        setTitleRightText(true,"编辑");
        pullListView.setVisibility(View.INVISIBLE);
        initLoadingImgs();
        handler.postDelayed(runnable,0);

    }

    private void initLoadingImgs() {
        StringBuffer sb;
        for (int i = 0; i < 31;i++){
            sb = new StringBuffer("order_loading_");
            sb.append(i);
            loadingImgs[i] = getResIdByDrawableName(sb.toString());
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    private int getResIdByDrawableName(String name){
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }
}
