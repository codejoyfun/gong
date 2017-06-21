package com.kids.commonframe.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kids.commonframe.R;
import com.kids.commonframe.base.view.IPhotoDraweeView;

@SuppressLint("NewApi")
public class BigPicViewHelper {
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration = 200;

    private ViewPager viewPager;
    private View viewPagerLayout;
    private View rootView;
    private TextView indicator;

    private BigPicBaseAdapter viewPagerAdapter;
    private AnimHolder holder;
    private Context mContext;

    private View clickView;
    private int currentPosition;

    private PicStatusListener picStatusListener;
    public BigPicViewHelper(BaseActivity mContext , FrameLayout pagerLayout, BigPicBaseAdapter viewPagerAdapter, View clickView, View rootView) {
    	viewPagerLayout = pagerLayout;
    	viewPager = (ViewPager) pagerLayout.findViewById(R.id.viewPager);
    	indicator = (TextView) pagerLayout.findViewById(R.id.indicator);
    	viewPager.setPageMargin(40);
    	this.mContext = mContext;
    	this.viewPagerAdapter = viewPagerAdapter;
    	this.clickView = clickView;
    	this.rootView = rootView;

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                // TODO Auto-generated method stub
                View view  = BigPicViewHelper.this.clickView.findViewWithTag(BigPicViewHelper.this.viewPagerAdapter.getImgUrl(position));
                if ( view != null ) {
                    initAnim(view);
                }
                IPhotoDraweeView photoView = getViewByPosition(position);
                if ( photoView != null ) {
                    photoView.reset();
                }
                setIndicator();
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void setClickView(View clickView) {
        this.clickView = clickView;
    }

    private void initAnim (final View thumbView) {
       	// Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
    	// the zoomed-in view (the default is the center of the view).
        viewPager.setPivotX(0f);
        viewPager.setPivotY(0f);
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
    public void zoomImageFromThumb(final View thumbView,int position) {
        if ( picStatusListener != null ) {
            picStatusListener.bigViewOpen();
        }
    	if ( viewPager != null) {
    		viewPager.setAdapter(viewPagerAdapter);
//    		viewPagerAdapter = new BitImgAdapter();
//    		viewPager.setAdapter(viewPagerAdapter);
            currentPosition = position;
    		if ( viewPager.getCurrentItem() != position ) {
    		viewPager.setCurrentItem(position,false);
    		}
    		setIndicator();
    	}
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
    	initAnim(thumbView);
    	// Hide the thumbnail and show the zoomed-in view. When the animation begins,
    	// it will position the zoomed-in view in the place of the thumbnail.
//    	thumbView.setAlpha(0f);
    	viewPagerLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
    	viewPager.setVisibility(View.VISIBLE);
    	indicator.setVisibility(View.VISIBLE);

    	// Construct and run the parallel animation of the four translation and scale properties
    	// (X, Y, SCALE_X, and SCALE_Y).
    	AnimatorSet set = new AnimatorSet();
    	set
    	.play(ObjectAnimator.ofFloat(viewPager, View.X, holder.startBounds.left,
    			holder.finalBounds.left))
    			.with(ObjectAnimator.ofFloat(viewPager, View.Y, holder.startBounds.top,
    					holder.finalBounds.top))
    					.with(ObjectAnimator.ofFloat(viewPager, View.SCALE_X, holder.startScale, 1f))
    					.with(ObjectAnimator.ofFloat(viewPager, View.SCALE_Y, holder.startScale, 1f));
    	set.setDuration(mShortAnimationDuration);
    	set.setInterpolator(new DecelerateInterpolator());
    	set.addListener(new AnimatorListenerAdapter() {
    		@Override
    		public void onAnimationEnd(Animator animation) {
    			mCurrentAnimator = null;
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
    private void setIndicator() {
    	indicator.setText((viewPager.getCurrentItem()+1)+"/"+viewPagerAdapter.getCount());
    }
    
   /**
    * 缩小图片
    */
    public void zoomInHome(final View thumbView) {
        if ( picStatusListener != null ) {
            picStatusListener.bigViewClose();
        }
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        viewPagerLayout.setBackgroundColor( mContext.getResources().getColor(android.R.color.transparent));
        // Animate the four positioning/sizing properties in parallel, back to their
        // original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(viewPager, View.X, holder.startBounds.left))
                .with(ObjectAnimator.ofFloat(viewPager, View.Y, holder.startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(viewPager, View.SCALE_X, holder.startScale))
                .with(ObjectAnimator
                        .ofFloat(viewPager, View.SCALE_Y, holder.startScale));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewPager.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                viewPager.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }
    class AnimHolder{
    	   Rect startBounds ;
           Rect finalBounds;
           float startScale;
           float startScaleFinal;
    }
   
    private IPhotoDraweeView getViewByPosition(int position) {
    	if ( viewPagerAdapter != null && viewPager != null ) {
    	String url = viewPagerAdapter.getImgUrl(position);
    	return (IPhotoDraweeView)viewPager.findViewWithTag(url);
    	}
    	return null;
    }
    
    public boolean showPager() {
    	if ( viewPager.getVisibility() == View.VISIBLE) {
    		View view = getViewByPosition(currentPosition);
    		zoomInHome(view);
    		return true;
    	}
    	return false;
    }
    public void setOnPicStatusListener(PicStatusListener picStatusListener){
        this.picStatusListener = picStatusListener;
    }

    public interface PicStatusListener{
        /**
         * 大图展示 关闭监听
         */
         void bigViewOpen();
         void bigViewClose();
    }
}
