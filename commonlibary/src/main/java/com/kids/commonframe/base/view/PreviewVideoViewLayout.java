package com.kids.commonframe.base.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import com.kids.commonframe.R;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.config.GlobalConstant;

import java.io.File;
import java.util.List;

/**
 * Created by ql on 2016/6/15.
 */
public class PreviewVideoViewLayout extends FrameLayout implements TextureView.SurfaceTextureListener{

    public static float density = 1;
    private Context context;
    protected View contentView;
    private boolean created = false;
    private MediaPlayer videoPlayer = null;
    private View videoContainerView = null;
    private ImageView playButton = null;
    private TextureView textureView = null;
    private boolean playerPrepared = false;

    private String videoPath = null;
    private float lastProgress = 0;
    private boolean needSeek = false;

    private int rotationValue = 0;
    private int originalWidth = 0;
    private int originalHeight = 0;
    private int resultWidth = 0;
    private int resultHeight = 0;
    private int bitrate = 0;
    private int originalBitrate = 0;
    private float videoDuration = 0;
    private long videoFramesSize = 0;
    private long originalSize = 0;
    private long audioFramesSize = 0;
    private VideoPlayListenter videoPlayListenter ;

    public PreviewVideoViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }


    public PreviewVideoViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public PreviewVideoViewLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        addView(initVideoViewLayout());
    }

    public void setOnVideoPlayListenter(VideoPlayListenter listenter){
        this.videoPlayListenter = listenter;
    }

    private View initVideoViewLayout() {

        contentView = LayoutInflater.from(context).inflate(R.layout.video_layout, null, false);
        videoContainerView = contentView.findViewById(R.id.video_container);

        playButton = (ImageView) contentView.findViewById(R.id.play_button);
        textureView = (TextureView) contentView.findViewById(R.id.video_view);

        playButton.setOnClickListener(playClick);
        textureView.setOnClickListener(playClick);
        textureView.setSurfaceTextureListener(this);
        return contentView;
    }

    private View.OnClickListener playClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            play();
            if (videoPlayListenter != null) {
                videoPlayListenter.onVideoPlay();
            }
        }
    };

    public boolean onVideoViewInit(String vPath) {
        this.videoPath = vPath;
        if (created) {
            return true;
        }

        processOpenVideo();
        if (videoPath == null) {
            videoPlayListenter.onVideoPlayFail();
            return false;
        }
        videoPlayer = new MediaPlayer();
        videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (playButton != null) {
                    playButton.setImageResource(R.drawable.video_play);
                }
                if(videoPlayListenter != null){
                    videoPlayListenter.onVideoPlayComplet();
                }
            }
        });
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerPrepared = true;
                if (videoPlayer != null) {
                    videoPlayer.seekTo(0);
                }
            }
        });
        try {
            videoPlayer.setDataSource(videoPath);
            videoPlayer.prepareAsync();
        } catch (Exception e) {
            if (videoPlayListenter != null) {
                videoPlayListenter.onVideoPlayFail();
            }
            LogUtil.e("tmessages", e.getMessage());
            return false;
        }
        created = true;
        return true;
    }

    public void onVideoViewDestroy() {
        if (videoPlayer != null) {
            try {
                videoPlayer.stop();
                videoPlayer.release();
                videoPlayer = null;
            } catch (Exception e) {
                LogUtil.e("tmessages", e.getMessage());
            }
        }
    }

    private void play() {
        if (videoPlayer == null || !playerPrepared) {
            return;
        }
        if (videoPlayer.isPlaying()) {
            videoPlayer.pause();
            playButton.setImageResource(R.drawable.video_play);
            if(videoPlayListenter != null){
                videoPlayListenter.onVideoPause();
            }
        } else {
            try {
                playButton.setImageDrawable(null);
                lastProgress = 0;
                if (needSeek) {
                    videoPlayer.seekTo((int) (videoDuration));
                    needSeek = false;
                }
                videoPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                    }
                });
                videoPlayer.start();
            } catch (Exception e) {
                LogUtil.e("tmessages", e.getMessage());
            }
        }
    }

    private boolean processOpenVideo() {
        try {
            File file = new File(videoPath);
            originalSize = file.length();

            IsoFile isoFile = new IsoFile(videoPath);
            LogUtil.e("tmessages", "videoPath :" + videoPath);
            List<Box> boxes = Path.getPaths(isoFile, "/moov/trak/");
            TrackHeaderBox trackHeaderBox = null;
            boolean isAvc = true;
            boolean isMp4A = true;

            Box boxTest = Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/");
            if (boxTest == null) {
                isMp4A = false;
            }

            if (!isMp4A) {
                resultWidth = originalWidth = 480;
                resultHeight = originalHeight = 480;
                LogUtil.e("tmessages", "isMp4A : false");
                return false;
            }

            boxTest = Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/");
            if (boxTest == null) {
                LogUtil.e("tmessages", "isAvc : false");
                isAvc = false;
            }

            for (Box box : boxes) {
                TrackBox trackBox = (TrackBox) box;
                long sampleSizes = 0;
                long trackBitrate = 0;
                try {
                    MediaBox mediaBox = trackBox.getMediaBox();
                    MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                    SampleSizeBox sampleSizeBox = mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox();
                    for (long size : sampleSizeBox.getSampleSizes()) {
                        sampleSizes += size;
                    }
                    videoDuration = (float) mediaHeaderBox.getDuration() / (float) mediaHeaderBox.getTimescale();
                    trackBitrate = (int) (sampleSizes * 8 / videoDuration);
                } catch (Exception e) {
                    LogUtil.e("tmessages", e.getMessage());
                }
                TrackHeaderBox headerBox = trackBox.getTrackHeaderBox();
                if (headerBox.getWidth() != 0 && headerBox.getHeight() != 0) {
                    trackHeaderBox = headerBox;
                    originalBitrate = bitrate = (int) (trackBitrate / 100000 * 100000);
                    if (bitrate > 900000) {
                        bitrate = 900000;
                    }
                    videoFramesSize += sampleSizes;
                } else {
                    audioFramesSize += sampleSizes;
                }
                LogUtil.e("tmessages", "audioFramesSize : " + originalBitrate + "-- videoFramesSize:" + videoFramesSize + " -- videoDuration :" + videoDuration);
            }
            if (trackHeaderBox == null) {
                LogUtil.e("tmessages", "trackHeaderBox : null" );
                return false;
            }

            Matrix matrix = trackHeaderBox.getMatrix();
            if (matrix.equals(Matrix.ROTATE_90)) {
                rotationValue = 90;
            } else if (matrix.equals(Matrix.ROTATE_180)) {
                rotationValue = 180;
            } else if (matrix.equals(Matrix.ROTATE_270)) {
                rotationValue = 270;
            }

            resultWidth = originalWidth = (int) trackHeaderBox.getWidth();
            resultHeight = originalHeight = (int) trackHeaderBox.getHeight();
            LogUtil.e("tmessages", "originalWidth : " + originalWidth + "-- originalHeight:" + originalHeight );

            if (resultWidth > 640 || resultHeight > 640) {
                if(rotationValue != 0){
                    float scale = resultWidth > resultHeight ? 640.0f / resultWidth : 640.0f / resultHeight;
                    resultWidth *= scale;
                    resultHeight *= scale;
                    if (bitrate != 0) {
                        bitrate *= Math.max(0.5f, scale);
                        videoFramesSize = (long) (bitrate / 8 * videoDuration);
                    }
                }
            }
            if (!isAvc && (resultWidth == originalWidth || resultHeight == originalHeight)) {
                return false;
            }
            LogUtil.e("tmessages", "rotationValue : " + rotationValue + "-- resultWidth:" + resultWidth + " -- resultHeight :" + resultHeight + " -- videoFramesSize :" + videoFramesSize);

        } catch (Exception e) {
            LogUtil.e("tmessages", e.getMessage());
            return false;
        }
        videoDuration *= 1000;
        return true;
    }

    public void fixLayout(final int orientation) {
        if (contentView == null) {
            return;
        }
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fixLayoutInternal(orientation);
                if (contentView != null) {
                    if (Build.VERSION.SDK_INT < 16) {
                        contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    public void fixLayoutInternal(int orientation) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoContainerView.getLayoutParams();
        layoutParams.topMargin = dp(16);
        layoutParams.bottomMargin = dp(16);
        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
        videoContainerView.setLayoutParams(layoutParams);
        fixVideoSize(orientation);
    }

    private void fixVideoSize(int orientation) {
        if (contentView == null) {
            return;
        }
        int width ;
        int height ;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            width = GlobalConstant.screenH;
            height = GlobalConstant.screenW;
        }else {
            width = GlobalConstant.screenW;
            height = GlobalConstant.screenH;
        }
        //视频实际宽高
        int vwidth = rotationValue == 90 || rotationValue == 270 ? originalHeight : originalWidth;
        int vheight = rotationValue == 90 || rotationValue == 270 ? originalWidth : originalHeight;

        LogUtil.e("tmessages", "width -- " + width + ";;; height -- " + height + " --- " + " vwidth : " + vwidth + " vheight : " + vheight);

        float wr = (float) width / (float) vwidth;
        float hr = (float) height / (float) vheight;
        float ar = (float) vwidth / (float) vheight;

        if (wr > hr) {
            width = (int) (height * ar);
        } else {
            height = (int) (width / ar);
        }

        LogUtil.e("tmessages", "B_width -- " + width + ";;; B_height -- " + height);

        if(rotationValue == 0 && vwidth > vheight && vwidth > width && vheight > vwidth ){
            height = GlobalConstant.screenH;
        }
        LogUtil.e("tmessages", "over_width -- " + width + ";;; over_height -- " + height);
        if (textureView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textureView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            textureView.setLayoutParams(layoutParams);
        }
    }

    private void setPlayerSurface() {
        if (textureView == null || !textureView.isAvailable() || videoPlayer == null) {
            return;
        }
        try {
            Surface s = new Surface(textureView.getSurfaceTexture());
            videoPlayer.setSurface(s);
            if (playerPrepared) {
//                videoPlayer.seekTo(1);
            }
        } catch (Exception e) {
            LogUtil.e("tmessages", e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        setPlayerSurface();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (videoPlayer == null) {
            return true;
        }
        videoPlayer.setDisplay(null);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public interface VideoPlayListenter{
        void onVideoPlay();
        void onVideoPause();
        void onVideoPlayComplet();
        void onVideoPlayFail();
    }
}
