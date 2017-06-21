package com.kids.commonframe.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.R;
import com.kids.commonframe.base.util.img.FrecoFactory;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;


@SuppressLint("NewApi")
public class BigVideoViewHelper {
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration = 200;

    private View videoLayout;
    private View rootView;
    private VideoView videoView;
    private View videoRootLayout;
    private SimpleDraweeView firstDraweeView;

    private AnimHolder holder;
    private Context mContext;

    private VideoStatusListener picStatusListener;
    private int currentPosition;

    private String firstImgUrl;
    private boolean isShow;

    public BigVideoViewHelper(BaseActivity mContext , View videoRootLayout, View videoLayout, VideoView videoView, View rootView) {
        this.videoLayout = videoLayout;
    	this.mContext = mContext;
    	this.rootView = rootView;
        this.videoView = videoView;
        this.videoRootLayout = videoRootLayout;
        firstDraweeView = (SimpleDraweeView) videoRootLayout.findViewById(R.id.videoFirstUrl);

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
//                    firstDraweeView.setVisibility(View.GONE);
//                    BigVideoViewHelper.this.videoView.start();
                }
                return false;
            }
        });
    }
    public void setOnVideoStatusListener(VideoStatusListener picStatusListener) {
        this.picStatusListener = picStatusListener;
    }

    private void initAnim (final View thumbView) {
       	// Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
    	// the zoomed-in view (the default is the center of the view).
        firstDraweeView.setPivotX(0f);
        firstDraweeView.setPivotY(0f);
        // Load the high-resolution "zoomed-in" image.
//        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
//        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        rootView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        holder = new AnimHolder();
        holder.finalBounds = finalBounds;
        holder.startBounds = startBounds;
        holder.startScale = startScale;

    }
    /**
     *放大图片
     */
    public void zoomImageFromThumb(final View thumbView,int position,final String localUrl,String firstImgUrl) {
        if (isShow) {
            return;
        }

        this.currentPosition = position;
        this.firstImgUrl = firstImgUrl;
        FrecoFactory.getInstance(mContext).disPlay(firstDraweeView,firstImgUrl);
        firstDraweeView.setVisibility(View.VISIBLE);
        if (picStatusListener != null ) {
            picStatusListener.videoViewOpen(currentPosition,0);
        }
    	if ( videoView != null) {
            videoView.setVideoPath(localUrl);
//            videoView.setLooper(true);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.setVideoPath(localUrl);
                    videoView.start();
                }
            });
    	}
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
    	initAnim(thumbView);
    	// Hide the thumbnail and show the zoomed-in view. When the animation begins,
    	// it will position the zoomed-in view in the place of the thumbnail.
//    	thumbView.setAlpha(0f);
        videoRootLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        videoLayout.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.VISIBLE);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isShow) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            zoomInHome(v);
                        }
                    },200);
                }
            }
        });

    	// Construct and run the parallel animation of the four translation and scale properties
    	// (X, Y, SCALE_X, and SCALE_Y).
    	AnimatorSet set = new AnimatorSet();
    	set
    	.play(ObjectAnimator.ofFloat(firstDraweeView, View.X, holder.startBounds.left,
    			holder.finalBounds.left))
    			.with(ObjectAnimator.ofFloat(firstDraweeView, View.Y, holder.startBounds.top,
    					holder.finalBounds.top))
    					.with(ObjectAnimator.ofFloat(firstDraweeView, View.SCALE_X, holder.startScale, 1f))
    					.with(ObjectAnimator.ofFloat(firstDraweeView, View.SCALE_Y, holder.startScale, 1f));
    	set.setDuration(mShortAnimationDuration);
    	set.setInterpolator(new DecelerateInterpolator());
    	set.addListener(new AnimatorListenerAdapter() {
    		@Override
    		public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                isShow = true;
                firstDraweeView.setVisibility(View.GONE);
                    BigVideoViewHelper.this.videoView.start();
                if (picStatusListener != null ) {
                    picStatusListener.videoViewOpened(currentPosition,0);
                }
            }

    		@Override
    		public void onAnimationCancel(Animator animation) {
    			mCurrentAnimator = null;
    		}
    	});
    	set.start();
    	mCurrentAnimator = set;

    	// Upon clicking the zoomed-in image, it should zoom back down to the original bounds
    	// and show the thumbnail instead of the expanded image.
    	final float startScaleFinal = holder.startScale;
    	holder.startScaleFinal = startScaleFinal;
    }
   /**
    * 缩小图片
    */
    public void zoomInHome(final View thumbView) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        if (picStatusListener != null ) {
            picStatusListener.videoViewClose(currentPosition,0);
        }
        if ( videoView != null) {
            videoView.stopPlayback();
//            videoView.setPlayUrlNull();
        }
        videoView.setVisibility(View.GONE);
//        firstDraweeView.setVisibility(View.VISIBLE);
//        videoLayout.setVisibility(View.GONE);
        videoRootLayout.setBackgroundColor( mContext.getResources().getColor(android.R.color.transparent));
        videoLayout.setVisibility(View.GONE);
        firstDraweeView.setVisibility(View.GONE);
        isShow = false;
        // Animate the four positioning/sizing properties in parallel, back to their
        // original values.
//        AnimatorSet set = new AnimatorSet();
//        set.play(ObjectAnimator.ofFloat(firstDraweeView, View.X, holder.startBounds.left))
//                .with(ObjectAnimator.ofFloat(firstDraweeView, View.Y, holder.startBounds.top))
//                .with(ObjectAnimator
//                        .ofFloat(firstDraweeView, View.SCALE_X, holder.startScale))
//                .with(ObjectAnimator
//                        .ofFloat(firstDraweeView, View.SCALE_Y, holder.startScale));
//        set.setDuration(mShortAnimationDuration);
//        set.setInterpolator(new DecelerateInterpolator());
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                videoLayout.setVisibility(View.GONE);
//                videoView.setVisibility(View.GONE);
//                firstDraweeView.setVisibility(View.GONE);
//                mCurrentAnimator = null;
//                if (picStatusListener != null ) {
//                    picStatusListener.videoViewClosed(currentPosition,0);
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                videoLayout.setVisibility(View.GONE);
//                videoView.setVisibility(View.GONE);
//                firstDraweeView.setVisibility(View.GONE);
//                mCurrentAnimator = null;
//            }
//        });
//        set.start();
//        mCurrentAnimator = set;
    }
    class AnimHolder{
    	   Rect startBounds ;
           Rect finalBounds;
           float startScale;
           float startScaleFinal;
    }


    public boolean showPager() {
    	if ( videoLayout.getVisibility() == View.VISIBLE) {
    		zoomInHome(videoView);
    		return true;
    	}
    	return false;
    }

    public interface VideoStatusListener{
        /**
         * 大图展示 关闭监听
         */
        void videoViewOpen(int position,long currentTime);
        void videoViewClose(int position,long currentTime);

        void videoViewOpened(int position,long currentTime);
        void videoViewClosed(int position,long currentTime);
    }
}
