package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.kids.commonframe.base.util.ToastUtil;
import com.runwise.supply.IWebViewActivity;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.HelperResult;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.CheckVersionManager;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.tools.StatusBarUtil;


/**
 * 关于我们
 */
public class AboutActivity extends NetWorkActivity {
    private final int REQUEST_HELP = 1;
//    @ViewInject(R.id.appName)
//    private TextView appName;
    @ViewInject(R.id.appCode)
    private TextView appCode;
    private long[] mHints = new long[7];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_about);
        this.setTitleText(true,"关于");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        appCode.setText(CommonUtils.getVersionName(this));
//        appName.setText(CommonUtils.getAppName(this));
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
         switch (where) {
             case REQUEST_HELP:
                 HelperResult helperResult = (HelperResult)result;
                 Intent intent = new Intent(mContext, IWebViewActivity.class);
                 intent.putExtra(WebViewActivity.WEB_TITLE,"详情");
                 intent.putExtra(WebViewActivity.WEB_URL,helperResult.getData().getHelp_url());
                 startActivity(intent);
                 break;
         }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    @OnClick({R.id.left_layout,R.id.activity_about})
    public void doClickHandler(View view) {
        switch (view.getId()) {
//            case R.id.versionLayout:
//                CheckVersionManager checkVersionManager = new CheckVersionManager(this);
//                checkVersionManager.checkVersion(true);
//                break;
//            case R.id.helpLayout:
//                sendConnection("/Appapi/article/get_help_url",REQUEST_HELP,true, HelperResult.class);
//                break;
            case R.id.left_layout:
                this.finish();
                break;
            case R.id.activity_about:
            case R.id.scrollview:
                //将mHints数组内的所有元素左移一个位置
                System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
                //获得当前系统已经启动的时间
                mHints[mHints.length - 1] = SystemClock.uptimeMillis();
                if(SystemClock.uptimeMillis()-mHints[0]<=1500){
                    startActivity(new Intent(this,ChangeHostActivity.class));
                }
                break;
        }
    }
}
