package io.vov.vitamio.medial;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.R;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.GlobalConstant;

import java.io.File;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;


/**
 * 主播放界面
 * @author chao
 */
public class IMediaPlayerActivity extends BaseActivity implements OnPreparedListener,OnCompletionListener,OnInfoListener ,
		OnBufferingUpdateListener,OnErrorListener{

	public static final String VIDEO_NAME_KEY = "medial_name";
	public static final String VIDEO_URL_KEY = "medial_uri";
	public static final String VIDEO_FACE_URL_KEY = "medial_face_uri";
	public static final String VIDEO_FIRST_PIC = "medial_first_pic";

	//    private String videoUrl ="http://video.cztv.com/video/rzx/201208/15/1345010952759.mp4";
	private String videoUrl ="http://61.167.54.40/mpv.videocc.net/4e26c379c8/9/4e26c379c83b9a1553848bb2dca17ea9_1.mp4?wsiphost=local";
//	private String videoUrl ="http://kbtcvideo.3ikids.com/729724/activity/material/20151021185032106.mp4";

	private String videoFirstPic="";
	private String videoName="京城张少的电影";
	//private String videoUrl ="http://www.anon.cn/zpzs/dhzz/3.wmv";
	//private String videoUrl ="http://www.modrails.com/videos/passenger_nginx.mov";
	//	private String videoUrl ="mnt/sdcard/龙拳.mp4";
	private VideoView mVideoView;
	private IMediaController mediaController;
	private LinearLayout noVideoLayout;
	private TextView fileName;
	private TextView loadingP;

	private LinearLayout loadingLayout;
	private TextView loadingText;
	private TextView loadingRate;
	private ScreenLockListener screenLockListener;

	private boolean firstLunch = true;
	private SimpleDraweeView vodioFaceimageView;
	private FrameLayout vodioFaceLayout;
	private ImageView vodioFaceBtn;

	private View leftLayout;
	private ResizeOptions imageSize;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ( !Vitamio.isInitialized(this) ){
			Toast.makeText(this, "播放类库加载失败", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		videoName=this.getIntent().getStringExtra(IMediaPlayerActivity.VIDEO_NAME_KEY);
		videoUrl=this.getIntent().getStringExtra(IMediaPlayerActivity.VIDEO_URL_KEY);
		videoFirstPic=this.getIntent().getStringExtra(IMediaPlayerActivity.VIDEO_FIRST_PIC);
		String videoFaceUrl=this.getIntent().getStringExtra(IMediaPlayerActivity.VIDEO_FACE_URL_KEY);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.medial_play_main);
		this.setTitleText(true, "播放");
		this.setTitleLeftIcon(true, R.drawable.back_btn);

		FrameLayout mainView = (FrameLayout)this.findViewById(R.id.mediaViewLayout);
		loadingLayout = (LinearLayout) this.findViewById(R.id.progress_layout);
		loadingText = (TextView) this.findViewById(R.id.progress_text);
		loadingRate = (TextView) this.findViewById(R.id.progress_rate);
		noVideoLayout=(LinearLayout)this.findViewById(R.id.no_video_layout);
		noVideoLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		fileName=(TextView)noVideoLayout.findViewById(R.id.mediaplayer_no_video);
		loadingP=(TextView)noVideoLayout.findViewById(R.id.no_video_text);
		leftLayout = findViewById(R.id.left_layout);

		fileName.setText(videoName);

		mediaController=new IMediaController(this,videoName);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVideoView.setMediaController(mediaController);
		mediaController.setVideoView(mVideoView);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnErrorListener(this);
		File cacheDir = new File(this.getExternalCacheDir(),"cacheMedialFile");
		if ( !cacheDir.exists() ) {
			cacheDir.mkdirs();
		}
//		mVideoView.setCacheDirectory(cacheDir.getAbsolutePath());
		mediaController.setAnchorView(mainView);
		mVideoView.setOnCompletionListener(this);
		//		mVideoView.setBufferSize(1024*512);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnBufferingUpdateListener(this);

		screenLockListener = new ScreenLockListener(this);
		screenLockListener.begin(new ScreenLockListener.ScreenStateListener() {
			@Override
			public void onUserPresent() {
				mediaController.show();
			}
			@Override
			public void onScreenOn() {
			}
			@Override
			public void onScreenOff() {
				mVideoView.pause();
			}
		});
		vodioFaceimageView = (SimpleDraweeView) this.findViewById(R.id.vodioFacePic);
		vodioFaceLayout = (FrameLayout) this.findViewById(R.id.vodioFaceLayout);
		vodioFaceBtn = (ImageView) this.findViewById(R.id.vodioFaceBtn);
		vodioFaceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println(videoUrl);
				mVideoView.setVideoURI(Uri.parse(videoUrl));
				vodioFaceLayout.setVisibility(View.GONE);
			}
		});

		leftLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mediaController!=null) {
					mediaController.doBackHandler();
				}
			}
		});

		this.imageSize =  new ResizeOptions(GlobalConstant.screenW , GlobalConstant.screenH / 2);
		FrecoFactory.getInstance(mContext).disPlay(vodioFaceimageView, Uri.fromFile(new File(videoUrl)), imageSize);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	public VideoView getVideoView(){
		return mVideoView;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mediaController!=null) {
			mediaController.release();
		}
		if(mVideoView!=null) {
			mVideoView.stopPlayback();
		}
		if(screenLockListener != null) {
			screenLockListener.unregisterListener();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		//		noVideoLayout.setVisibility(View.GONE);
		mp.start();
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		//设置让视频重新播放
		mediaController.setPlayerFinish();
	}

	@Override
	public boolean onInfo(MediaPlayer arg0, int what, int param) {
		switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				if (mVideoView.isPlaying()) {
					mVideoView.pause();
					loadingText.setText("缓存中... ");
					loadingLayout.setVisibility(View.VISIBLE);
				}
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				noVideoLayout.setVisibility(View.GONE);
				mediaController.startMedialPlaying();
//			mVideoView.start();
				//缓存完成，继续播放
				loadingLayout.setVisibility(View.GONE);
				if (firstLunch) {
					mediaController.show();
					firstLunch = false;
				}
				break;
			case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
				//显示 下载速度
				loadingRate.setText("("+param+"KB/S"+")");
				break;
		}
		return true;
	}
	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		loadingText.setText(arg1+"%");
		loadingP.setText("加载中..."+arg1+"%");
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(this, "视频播放失败", Toast.LENGTH_LONG).show();
		return true;
	}
	@Override
	public void onBackPressed() {
		if(mediaController!=null) {
			mediaController.doBackHandler();
		}
	}
//	@OnClick(R.id.left_layout)
	public void goBack (View view) {
		if(mediaController!=null) {
			mediaController.doBackHandler();
		}
	}
}
