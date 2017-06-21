package io.vov.vitamio.medial;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kids.commonframe.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 视频控制器
 * 
 * @author chao
 */
public class IMediaController extends MediaController {
	private static final int sDefaultTimeout = 5000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private Activity context;
	private View controlRoot;
	private boolean mDragging;
	private TextView mEndTime, mCurrentTime;
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;
	private ImageView mPauseButton;
	private ImageButton mFfwdButton;
	private ImageButton mRewButton;
	private SeekBar mProgress;
	private MediaPlayerControl mPlayer;
	private ImageView showPower;
	private TextView showTime;
	private SimpleDateFormat format;
	private Timer timer;
	private ImageView backButton;
	private String fileName;
	private VideoView videoView;

	private ImageView full_screen;
	public boolean full_screen_flag;
	private long mDuration;
	// private RequestNetListener requestListener;
	private boolean isPlayFinish;

	private View mCommentLayout;

	public void setmCommentLayout(View mCommentLayout) {
		this.mCommentLayout = mCommentLayout;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (showPower != null)
					showTime.setText(getTime());
			}
		}
	};

	/** 视频播放2分钟做后台请求 */
	public interface RequestNetListener {
		void needRequestNet();
	}

	public void setVideoView(VideoView videoView) {
		this.videoView = videoView;
	}

	public IMediaController(Activity context, String fileName) {
		super(context);
		this.context = context;
		this.fileName = fileName;
	}

	// public void setOnRequestNetListener (RequestNetListener
	// requestNetListener) {
	// this.requestListener = requestNetListener;
	// }

	protected View makeControllerView() {
		return getMediaControllView(false);
	}

//	@Override
	protected View makeFullScreenControllerView() {
		return getMediaControllView(true);
	}

	private View getMediaControllView(boolean isFullScreen) {
		LayoutInflater inflate = LayoutInflater.from(context);
		if (isFullScreen) {
			controlRoot = inflate.inflate(
					R.layout.medial_fullscreen_controller_layout, null);
		} else {
			controlRoot = inflate.inflate(
					R.layout.medial_normal_controller_layout, null);
		}
		View mediaCenter = controlRoot.findViewById(R.id.mediaCenter);
		mediaCenter.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (IMediaController.this.isShowing()) {
						IMediaController.this.hide();
					} else {
						IMediaController.this.show();
					}
					break;
				}
				return true;
			}
		});
		initControllerView(controlRoot);
		return controlRoot;
	}

	public View getControllView() {
		return controlRoot;
	}

	@Override
	public void show() {
		super.show(sDefaultTimeout);
		updatePausePlay();
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
	}

	@Override
	public void setMediaPlayer(MediaPlayerControl player) {
		this.mPlayer = player;
		super.setMediaPlayer(player);
	}

	private void initControllerView(View v) {
		mPauseButton = (ImageView) v.findViewById(R.id.mediacontroller_pause);
		if (mPauseButton != null) {
			mPauseButton.requestFocus();
			mPauseButton.setOnClickListener(mPauseListener);
		}

		mFfwdButton = (ImageButton) v.findViewById(R.id.mediacontroller_next);
		if (mFfwdButton != null) {
			mFfwdButton.setOnClickListener(mFfwdListener);
		}

		mRewButton = (ImageButton) v
				.findViewById(R.id.mediacontroller_previous);
		if (mRewButton != null) {
			mRewButton.setOnClickListener(mRewListener);
		}

		mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_progress);
		if (mProgress != null) {
			mProgress.setOnSeekBarChangeListener(mSeekListener);
			mProgress.setMax(1000);
		}
//		controllerTopLayout = v.findViewById(R.id.top);
//		controllerBottomLayout = v.findViewById(R.id.bottom);

		mEndTime = (TextView) v.findViewById(R.id.mediacontroller_time_total);
		mCurrentTime = (TextView) v
				.findViewById(R.id.mediacontroller_time_current);
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		full_screen = (ImageView) v.findViewById(R.id.full_screen);
		full_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!full_screen_flag) {
					setFullScreen();
				} else {
					setNormalScreen();
				}
				setCommentViewState(mCommentLayout, full_screen_flag);
			}
		});

		// Button quality=(Button)v.findViewById(R.id.quality);
		// if (quality != null )
		// quality.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(videoView==null){
		// IMediaPlayerActivity activity =(IMediaPlayerActivity)context;
		// videoView=activity.getVideoView();
		// }
		// videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
		// }
		// });
		// --
		showPower = (ImageView) v.findViewById(R.id.showpower);
		showTime = (TextView) v.findViewById(R.id.showtime);
		backButton = (ImageView) v.findViewById(R.id.medial_back);
		TextView fileName = (TextView) v
				.findViewById(R.id.mediacontroller_filename);
		if (fileName != null)
			fileName.setText(this.fileName);
		if (backButton != null)
			backButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doBackHandler();
				}
			});
		IntentFilter batteryChangedReceiverFilter = new IntentFilter();
		batteryChangedReceiverFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(batteryChangedReceiver,
				batteryChangedReceiverFilter);
		format = new SimpleDateFormat("HH:mm");
		if (showPower != null)
			showTime.setText(getTime());
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = Message.obtain(handler);
				message.what = 1;
				message.sendToTarget();
			}
		}, 0, 15000);
	}

	public void doBackHandler() {
//		setNormalScreen();
		 if (full_screen_flag) {
		 setNormalScreen();
		 } else {
		 context.finish();
		 }
	}

	private void setFullScreen() {
		full_screen
				.setBackgroundResource(R.drawable.medial_button_normalscreen_selector);
		// videoView.setVideoLayout(2, 0);
//		IMediaController.this.setScreenFull(true);
		windowSet(true);
		videoView.setVideoLayout(1, 0);
		updatePausePlay();
		setProgress();
		full_screen_flag = true;
	}

	private void setNormalScreen() {
		full_screen
				.setBackgroundResource(R.drawable.medial_button_fullscreen_selector);
//		IMediaController.this.setScreenFull(false);
		windowSet(false);
		videoView.setVideoLayout(1, 0);
		full_screen_flag = false;
		updatePausePlay();
		setProgress();
	}

	/**
	 * 窗体设置
	 * 
	 * @param isFull
	 */
	private void windowSet(boolean isFull) {
		Window window = context.getWindow();
		// window.setFormat(PixelFormat.RGB_565);// 设置窗口透明度
		window.clearFlags(WindowManager.LayoutParams.FLAG_DITHER);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕唤醒
		if (isFull) {
			window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	/**
	 * 为控制器添加信息监听
	 */
	public void addInfoListener() {
		if (videoView == null) {
			IMediaPlayerActivity activity = (IMediaPlayerActivity) context;
			videoView = activity.getVideoView();
		}
		videoView.setOnInfoListener(infoListener);
	}

	private String getTime() {
		return format.format(new Date());
	}

	private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int level = intent.getIntExtra("level", 0);
				/*
				 * int scale = intent.getIntExtra("scale", 100); int status =
				 * intent.getIntExtra("status", 0);
				 */
				// System.out.println("level:"+level+"scale:"+scale+",status:"+status);
				if (showPower != null)
					showPower.setImageResource(getPowerLevel(level));
			}
		}
	};

	private int getPowerLevel(int level) {
		if (level >= 100) {
			return R.drawable.medial_fullplayer_battery_10;
		} else if (level >= 90) {
			return R.drawable.medial_fullplayer_battery_9;
		} else if (level >= 80) {
			return R.drawable.medial_fullplayer_battery_8;
		} else if (level >= 70) {
			return R.drawable.medial_fullplayer_battery_7;
		} else if (level >= 60) {
			return R.drawable.medial_fullplayer_battery_6;
		} else if (level >= 50) {
			return R.drawable.medial_fullplayer_battery_5;
		} else if (level >= 40) {
			return R.drawable.medial_fullplayer_battery_4;
		} else if (level >= 30) {
			return R.drawable.medial_fullplayer_battery_3;
		} else if (level >= 20) {
			return R.drawable.medial_fullplayer_battery_2;
		} else if (level >= 10) {
			return R.drawable.medial_fullplayer_battery_1;
		} else {
			return R.drawable.medial_fullplayer_battery_0;
		}
	}

	private OnClickListener mRewListener = new OnClickListener() {
		public void onClick(View v) {
			int pos = (int) mPlayer.getCurrentPosition();
			pos -= 5000; // milliseconds
			mPlayer.seekTo(pos);
			setProgress();
			show(sDefaultTimeout);
		}
	};
	private OnClickListener mFfwdListener = new OnClickListener() {
		public void onClick(View v) {
			int pos = (int) mPlayer.getCurrentPosition();
			pos += 15000; // milliseconds
			mPlayer.seekTo(pos);
			setProgress();

			show(sDefaultTimeout);
		}
	};
	private OnInfoListener infoListener = new OnInfoListener() {
		@Override
		public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
			return false;
		}
	};

	/**
	 * 释放medial的资源
	 */
	public void release() {
		context.unregisterReceiver(batteryChangedReceiver);
		timer.cancel();
		timer = null;
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private OnClickListener mPauseListener = new OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
			show(sDefaultTimeout);
		}
	};

	private int setProgress() {
		if (mDragging) {
			return 0;
		}
		if (mPlayer != null) {
			int position = (int) mPlayer.getCurrentPosition();
			int duration = (int) mPlayer.getDuration();
			if (duration > 0) {
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			mDuration = duration;
			int percent = mPlayer.getBufferPercentage();
			mProgress.setSecondaryProgress(percent * 10);
			mEndTime.setText(stringForTime(duration));
			mCurrentTime.setText(stringForTime(position));
			return position;
		}
		return 0;
	}

	private void updatePausePlay() {
		if (mPauseButton != null && mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPauseButton
						.setImageResource(R.drawable.medial_button_pause_selector);
			} else {
				mPauseButton
						.setImageResource(R.drawable.medial_button_play_selector);
			}
		}
	}

	public void startMedialPlaying() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mPlayer.start();
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
		updatePausePlay();
	}

	private void doPauseResume() {
		// 已经播放完毕
		if (isPlayFinish) {
			mPlayer.seekTo(0);
			mPlayer.start();
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
			isPlayFinish = false;
		} else if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mPlayer.start();
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
		updatePausePlay();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
//				if (!mDragging && mShowing && mPlayer != null &&mPlayer.isPlaying()) {
//					msg = obtainMessage(SHOW_PROGRESS);
//					sendMessageDelayed(msg, 1000 - (pos % 1000));
//					updatePausePlay();
//				}
				break;
			}
		}
	};
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);
			mDragging = true;
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (fromuser) {
				long newposition = (mDuration * progress) / 1000L;
				mCurrentTime.setText(stringForTime((int) newposition));
			}
		}

		public void onStopTrackingTouch(SeekBar bar) {
			mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
			show(sDefaultTimeout);
			mHandler.removeMessages(SHOW_PROGRESS);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
			updatePausePlay();
		}
	};

	// 已经播放完毕
	public void setPlayerFinish() {
		isPlayFinish = true;
	}

	public void setCommentViewState(View v, boolean b) {
		if ( v == null ) {
			return;
		}
		if (b) {
			v.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
		}
	}
}
