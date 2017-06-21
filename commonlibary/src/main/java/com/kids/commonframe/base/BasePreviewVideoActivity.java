package com.kids.commonframe.base;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kids.commonframe.R;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.PreviewVideoViewLayout;

import io.vov.vitamio.medial.IMediaPlayerActivity;

/**
 * Created by ql on 2016/6/15.
 */
public class BasePreviewVideoActivity extends BaseActivity {

    private String videoPath ;
    private PreviewVideoViewLayout videoLayout;
    private FrameLayout title_bg;
    private ImageView viewLeft;
    private Animation topShowAnim;
    private Animation topHideAnim;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoPath = this.getIntent().getStringExtra(IMediaPlayerActivity.VIDEO_URL_KEY);
        setContentView(R.layout.activity_preview_video);
        title_bg = (FrameLayout)findViewById(R.id.title_bg);
        title_bg.setBackgroundColor(Color.parseColor("#77000000"));
        viewLeft = (ImageView) findViewById(R.id.title_iv_left);
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        videoLayout = (PreviewVideoViewLayout) findViewById(R.id.video_layout);

        viewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        videoLayout.setOnVideoPlayListenter(new PreviewVideoViewLayout.VideoPlayListenter() {
            @Override
            public void onVideoPlay() {
                setTitleHide();
            }
            @Override
            public void onVideoPause() {
               setTitleShow();
            }
            @Override
            public void onVideoPlayComplet() {
                setTitleShow();
            }
            @Override
            public void onVideoPlayFail() {
                if(customDialog == null){
                    customDialog = new CustomDialog(BasePreviewVideoActivity.this);
                }
                customDialog.setModel(CustomDialog.LEFT);
                customDialog.setMessage("无法播放此视频");
                customDialog.setLeftBtnListener("确定", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        BasePreviewVideoActivity.this.finish();
                    }
                });
                customDialog.show();
            }
        });
        videoLayout.onVideoViewInit(videoPath);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(videoLayout != null){
            videoLayout.fixLayoutInternal(getResources().getConfiguration().orientation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoLayout != null){
            videoLayout.onVideoViewDestroy();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            windowSet(true);
        }else {
            windowSet(false);
        }
        if(videoLayout != null){
            videoLayout.fixLayout(getResources().getConfiguration().orientation);
        }
    }

    /**
     * 窗体设置
     *
     * @param isFull
     */
    private void windowSet(boolean isFull) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DITHER);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕唤醒
        if (isFull) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    //显示
    public void setTitleShow() {
        if ( topShowAnim == null ) {
            topShowAnim = AnimationUtils.loadAnimation(mContext, com.kids.commonframe.R.anim.medial_top_show);
        }
        if (title_bg.getVisibility() == View.GONE) {
            title_bg.startAnimation(topShowAnim);
            topShowAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    title_bg.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    title_bg.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    //影藏
    public void setTitleHide() {
        if (topHideAnim == null ) {
            topHideAnim = AnimationUtils.loadAnimation(mContext, com.kids.commonframe.R.anim.medial_top_hide);
        }
        if (title_bg.getVisibility() == View.VISIBLE) {
            topHideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    title_bg.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    title_bg.setVisibility(View.GONE);
                }
            });
            title_bg.startAnimation(topHideAnim);
        }
    }
}
