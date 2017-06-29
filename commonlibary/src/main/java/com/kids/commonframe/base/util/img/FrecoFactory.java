package com.kids.commonframe.base.util.img;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * 所有facebook freco加载顶图片都应用该类
 * 方便以后统一管理
 */
public class FrecoFactory {
    private static FrecoFactory instance;
    private FrecoFactory(Context context) {
        this.mContext = context;
        initFresco();
    }
    private Context mContext;
    public static FrecoFactory getInstance(Context context) {
        if (instance == null) {
            synchronized (FrecoFactory.class) {
                if ( instance == null) {
                    instance = new FrecoFactory(context);
                }
            }
        }
        return instance;
    }
    public Drawable getDefDrawable(int drawId) {
        return mContext.getResources().getDrawable(drawId);
    }

    public Uri getUriFromStr(String url) {
        if (TextUtils.isEmpty(url)) {
            return Uri.parse("");
        }
        return Uri.parse(url);
    }
    /**
     * SimpleDrawableView调用改方法设置
     * 注意一个view只调用该方法一次
     * @param draweeView
     * @param defaultDrawable
     * @param scaleType
     */
    public void setDefaultHierarchy(DraweeView draweeView,Drawable defaultDrawable,ScalingUtils.ScaleType scaleType) {
        setDefaultHierarchy(draweeView,defaultDrawable,ScalingUtils.ScaleType.CENTER_CROP,scaleType,false);
    }
    public void setDefaultHierarchy(DraweeView draweeView,ScalingUtils.ScaleType scaleType,Drawable defaultDrawable,ScalingUtils.ScaleType defaultScaleType) {
        setDefaultHierarchy(draweeView,defaultDrawable,ScalingUtils.ScaleType.CENTER_CROP,scaleType,false);
    }
    public void setDefaultHierarchy(DraweeView draweeView,Drawable defaultDrawable,ScalingUtils.ScaleType defaultScaleType,ScalingUtils.ScaleType targerScaleType,boolean isCircle) {
        GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder( mContext.getResources())
                .setPlaceholderImage(defaultDrawable, defaultScaleType)
                .setFailureImage(defaultDrawable, ScalingUtils.ScaleType.CENTER_CROP)
//				.setProgressBarImage(new ProgressBarDrawable())
                .setActualImageScaleType(targerScaleType)
                .build();
        if ( isCircle ) {
            gdh.setRoundingParams(RoundingParams.asCircle());
        }
        draweeView.setHierarchy(gdh);
    }
    /**
     * SimpleDrawableView调用改方法显示图片
     * @param draweeView
     * @param uri
     * @param resizeOptions
     */
    public void disPlay(DraweeView draweeView,Uri uri,ResizeOptions resizeOptions) {
        disPlay(draweeView, uri,null, resizeOptions, null, null,null);
    }
    public void disPlay(DraweeView draweeView,String imgUrl) {
        disPlay(draweeView,getUriFromStr(imgUrl),null,null,null,null,null);
    }
    public void disPlay(DraweeView draweeView,String imgUrl,String lowResUri) {
        disPlay(draweeView,getUriFromStr(imgUrl),lowResUri,null,null,null,null);
    }
    public void disPlay(DraweeView draweeView,String imgUrl,String lowResUri,ControllerListener controllerListener) {
        disPlay(draweeView,getUriFromStr(imgUrl),lowResUri,null,null,null,controllerListener);
    }
    public void disPlay(DraweeView draweeView,String imgUrl,String lowResUri,ScalingUtils.ScaleType scaleType) {
        disPlay(draweeView,getUriFromStr(imgUrl),lowResUri,null,null,scaleType,null);
    }
    public void disPlay(DraweeView draweeView,Uri uri) {
        disPlay(draweeView,uri,null,null,null);
    }
    public void disPlay(DraweeView draweeView,Uri uri,ResizeOptions resizeOptions,Drawable defaultDrawable,ScalingUtils.ScaleType scaleType) {
        disPlay(draweeView, uri,null, resizeOptions,defaultDrawable,scaleType,null);
    }
    /**
     * 自定义控件调用该方法显示
     * @param draweeView
     * @param uri
     * @param resizeOptions
     * @param defaultDrawable
     * @param scaleType
     */
    public void disPlay(DraweeView draweeView, Uri uri, String lowResUri, ResizeOptions resizeOptions, Drawable defaultDrawable, ScalingUtils.ScaleType scaleType, ControllerListener controllerListener) {
//        System.out.println(draweeView.hasHierarchy());
        if (!draweeView.hasHierarchy()) {
            setDefaultHierarchy(draweeView,defaultDrawable,scaleType);
        }
        ImageRequestBuilder imageRequestBuilder =
                ImageRequestBuilder.newBuilderWithSource(uri);
        if (UriUtil.isNetworkUri(uri)) {
            imageRequestBuilder.setProgressiveRenderingEnabled(true);
        }
        else {
            imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true);
            imageRequestBuilder.setProgressiveRenderingEnabled(false);
            if ( resizeOptions != null ) {
                imageRequestBuilder.setResizeOptions(resizeOptions);
            }
        }
//        imageRequestBuilder.setAutoRotateEnabled(true);
//        imageRequestBuilder.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(draweeView.getController())
                .setLowResImageRequest(ImageRequest.fromUri(lowResUri))
                .setControllerListener(controllerListener)
                .build();
        draweeView.setController(draweeController);
    }
    private void initFresco(){
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(mContext)
                .setBaseDirectoryPath(mContext.getFilesDir())
                .setBaseDirectoryName("image_cache")
                .setMaxCacheSize(50 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(10 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(2 * ByteConstants.MB)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(mContext)
                .setNetworkFetcher(new ElnImageDownloaderFetcher())
                .setMainDiskCacheConfig(diskCacheConfig).build();
        Fresco.initialize(mContext, config);
    }
}
