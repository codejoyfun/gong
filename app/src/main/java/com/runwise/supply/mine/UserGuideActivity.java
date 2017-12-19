package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.StorageUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.IWebViewActivity;
import com.runwise.supply.InfoActivity;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.entity.GuideResponse;
import com.runwise.supply.mine.entity.UrlResult;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.runwise.supply.tools.UmengUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;

import static com.kids.commonframe.base.WebViewActivity.WEB_TITLE;
import static com.kids.commonframe.base.WebViewActivity.WEB_URL;

/**
 * 用户指南
 */

public class UserGuideActivity extends NetWorkActivity {

    private static final int REQUEST_USER_GUIDE = 0x001;
    private GuideResponse mGuideResponse;
    @ViewInject(R.id.ll_user_guide_container)
    private LinearLayout mLlContainer;
    @ViewInject(R.id.iv_user_guide_expand)
    private ImageView mIvArrow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_user_guide);
        showBackBtn();
        this.setTitleText(true, "用户指南");
        requestGuide();
    }

    /**
     * 请求用户手册
     */
    private void requestGuide(){
        Object request = null;
        sendConnection("/test",REQUEST_USER_GUIDE,false,GuideResponse.class);
    }

    @OnClick({R.id.rl_guide_pic,R.id.rl_guide_video})
    public void onBtnClicked(View v){
        switch (v.getId()){
            case R.id.rl_guide_pic://图片教程
                Intent intent = new Intent(this,UserGuidActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_guide_video://视频教程,展开
                mLlContainer.setVisibility(mLlContainer.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
                mIvArrow.setImageResource(mLlContainer.getVisibility()==View.VISIBLE?R.drawable.login_btn_dropdown:R.drawable.itemlst_bigger);
                break;
        }
    }

    private void inflateGuideView(){
        LayoutInflater inflater = LayoutInflater.from(this);
        if(mGuideResponse.getList()==null)return;
        for(final GuideResponse.GuideItem guideItem:mGuideResponse.getList()){
            View guideView = inflater.inflate(R.layout.item_video_guide,mLlContainer,false);
            ((TextView)guideView.findViewById(R.id.tv_guide_name)).setText(guideItem.getName());
            guideView.setOnClickListener(v->gotoWebview(guideItem.getName(),guideItem.getUrl()));
            mLlContainer.addView(guideView);
        }
    }

    private void gotoWebview(String name,String url){
        Intent intent = new Intent(this,VideoWebviewActivity.class);
        intent.putExtra(WEB_TITLE,name);
        intent.putExtra(WEB_URL,url);
        startActivity(intent);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_USER_GUIDE:
                mGuideResponse = (GuideResponse) result.getResult().getData();
                inflateGuideView();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        //TODO:test
        switch (where){
            case REQUEST_USER_GUIDE:
                UmengUtil.reportError(errMsg);
                mGuideResponse = new GuideResponse();
                mGuideResponse.setList(new ArrayList<>());
                GuideResponse.GuideItem item = new GuideResponse.GuideItem();
                item.setName("下蛋教程");
                item.setUrl("http://v.youku.com/v_show/id_XMzI0MjY5MjM4OA==.html?spm=a2hww.20027244.m_250036.5~5!2~5~5~5~1!2~3~A&f=51392393d");
                mGuideResponse.getList().add(item);
                inflateGuideView();
                break;
        }
    }
}
