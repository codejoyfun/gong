package com.kids.commonframe.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.kids.commonframe.R;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomSavePicDialog;
import com.kids.commonframe.base.view.CustomSavePicDialog.SaveInterface;
import com.kids.commonframe.base.view.IPhotoDraweeView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public abstract class BigPicBaseAdapter<T> extends PagerAdapter {
	protected List<T> mList = new LinkedList<T>();
	 private Context mContext;
	public BigPicBaseAdapter(Context context) {
		this.mContext = context;
	}
	public List<T> getList() {
		return mList;
	}
	public void setData(List<T> list) {
		if (list == null) {
			return;
		}
		mList.clear();
		mList.addAll(list);
	}
	public void clear() {
		mList.clear();
	}
	@Override
	public int getCount() {
		return mList.size();
	}
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == (View) obj;
	}
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View rootView =  View.inflate(mContext,R.layout.item_bigpic_view,null);
		IPhotoDraweeView simpleDraweeView = (IPhotoDraweeView)rootView.findViewById(R.id.imgDraweeView);
		ProgressBar progressBar = (ProgressBar)rootView.findViewById(R.id.imgProgress);
		simpleDraweeView.enable();
        String imgUrl =  getImgUrl(position);
		simpleDraweeView.setTag(imgUrl);
		FrecoFactory.getInstance(mContext).disPlay(simpleDraweeView,imgUrl,getImglowResUri(position),new ImgController(progressBar));
		((ViewPager) container).addView(rootView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		simpleDraweeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomInHome(view);
			}
		});
		simpleDraweeView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				CustomSavePicDialog dialog = new CustomSavePicDialog(mContext);
				dialog.setOnSaveClickItem(new SaveInterface() {
					@Override
					public void doSave() {
						String imageUrl = getImgUrl(position);
						HttpUtils utils = new HttpUtils();
						String imagePenPath = CommonUtils.getCachePath( mContext);
						final File targerFile = new File(imagePenPath,new File(imageUrl).getName());
//						mContext.this.showIProgressDialog();
						utils.download(imageUrl, targerFile.getAbsolutePath(), true, new RequestCallBack<File>() {
							@Override
							public void onLoading(long total, long current, boolean isUploading) {
							}
							@Override
							public void onSuccess(ResponseInfo<File> arg0) {
								try {
									MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
											targerFile.getAbsolutePath(),targerFile.getName(), null);
									mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
											Uri.fromFile(targerFile.getParentFile())));
//									ToastUtil.show(mContext, "保存成功!");
								} catch (FileNotFoundException e) {
									ToastUtil.show(mContext, "保存失败!");
									e.printStackTrace();
								}
//								HomeContectsDetailActivity.this.dismissIProgressDialog();
								ToastUtil.show(mContext, "图片保存成功");
								targerFile.delete();
							}
							@Override
							public void onFailure(HttpException arg0, String arg1) {
							}
						});
					}
				});
				dialog.show();
				return true;
			}
		});
		return rootView;
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	public abstract String getImgUrl(final int position);
	public abstract String getImglowResUri(final int position);
	public abstract void zoomInHome(View view);

	private class ImgController extends BaseControllerListener<ImageInfo>{
		private ProgressBar progressBar;
		public ImgController(ProgressBar progressBar) {
			this.progressBar = progressBar;
		}
		@Override
		public void onFinalImageSet(
				String id,
				@Nullable ImageInfo imageInfo,
				@Nullable Animatable anim) {
//			if (imageInfo == null) {
//				return;
//			}
			QualityInfo qualityInfo = imageInfo.getQualityInfo();
			FLog.d("Final image received! " +
							"Size %d x %d",
					"Quality level %d, good enough: %s, full quality: %s",
					imageInfo.getWidth(),
					imageInfo.getHeight(),
					qualityInfo.getQuality(),
					qualityInfo.isOfGoodEnoughQuality(),
					qualityInfo.isOfFullQuality());
			if (progressBar != null) {
				progressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
		}

		@Override
		public void onFailure(String id, Throwable throwable) {
			FLog.e(getClass(), throwable, "Error loading %s", id);
			if (progressBar != null) {
				progressBar.setVisibility(View.GONE);
			}
		}
	}
}
