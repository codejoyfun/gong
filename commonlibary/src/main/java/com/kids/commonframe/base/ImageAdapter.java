package com.kids.commonframe.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.kids.commonframe.base.view.CustomSavePicDialog;
import com.kids.commonframe.base.view.CustomSavePicDialog.SaveInterface;

import java.util.LinkedList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

public abstract class ImageAdapter<T> extends PagerAdapter {
	protected List<T> mList = new LinkedList<T>();
	 private Context mContext;
	public ImageAdapter(Context context) {
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
         PhotoView view = new PhotoView( mContext);
        String imgUrl =  getImgUrl(position);
         view.setTag(imgUrl);
//         ImageLoader.getInstance().displayImage(imgUrl,new ImageViewAware(view) ,displayOption, imgeSize,null,null);
		((ViewPager) container).addView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		view.setOnViewTapListener(new OnViewTapListener() {
			@Override
			public void onViewTap(View view, float x, float y) {
				zoomInHome(view);
			}
		});
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				CustomSavePicDialog dialog = new CustomSavePicDialog(mContext);
				dialog.setOnSaveClickItem(new SaveInterface() {
					@Override
					public void doSave() {
						String imageUrl = getImgUrl(position);
//						File targerFile = ImageLoader.getInstance().getDiskCache().get(imageUrl);
//						try {
//							MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
//									targerFile.getAbsolutePath(),targerFile.getName(), null);
//							mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//									Uri.fromFile(targerFile.getParentFile())));
//							ToastUtil.show(mContext, "保存成功!");
//						} catch (FileNotFoundException e) {
//							ToastUtil.show(mContext, "保存失败!");
//							e.printStackTrace();
//						}
						
//						HttpUtils utils = new HttpUtils();
//						final File targerFile = new File(Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_DCIM,imageUrl);
//						HomeContectsDetailActivity.this.showIProgressDialog();
//						utils.download(imageUrl, targerFile.getAbsolutePath(), true, new RequestCallBack<File>() {
//							@Override
//							public void onLoading(long total, long current, boolean isUploading) {
//							}
//							@Override
//							public void onSuccess(ResponseInfo<File> arg0) {
//								try {
//									MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
//											targerFile.getAbsolutePath(),targerFile.getName(), null);
//									mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//											Uri.fromFile(targerFile.getParentFile())));
//									ToastUtil.show(mContext, "保存成功!");
//								} catch (FileNotFoundException e) {
//									ToastUtil.show(mContext, "保存失败!");
//									e.printStackTrace();
//								}
//								HomeContectsDetailActivity.this.dismissIProgressDialog();
//								com.tongfang.teacher.utils.ToastUtil.show(mContext, "相册保存成功");
//							}
//							@Override
//							public void onFailure(HttpException arg0, String arg1) {
//							}
//						});
					}
				});
				dialog.show();
				return true;
			}
		});
		return view;
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	public abstract String getImgUrl(final int position);
	public abstract void zoomInHome(View view);
}
