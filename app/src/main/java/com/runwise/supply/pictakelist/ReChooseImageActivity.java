package com.runwise.supply.pictakelist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.runwise.supply.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.IPhotoDraweeView;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;


public class ReChooseImageActivity extends NetWorkActivity {
	private BigPicAdapter adapter;
	@ViewInject(R.id.jazzyViewPager)
	private ViewPager vp;
	@ViewInject(R.id.titleLayout)
	private List<PicTake> images;
	private int currentIndex;
	private StringBuffer sb = new StringBuffer();
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_image);
		setTitleTitleBg(Color.parseColor("#77000000"));
		images = (ArrayList<PicTake>) this.getIntent().getSerializableExtra("bean");
		currentIndex = this.getIntent().getIntExtra("index", 0);
		if (images != null) {
			adapter = new BigPicAdapter(images);
			vp.setAdapter(adapter);
			vp.setPageMargin(40);
			vp.setCurrentItem(currentIndex);
		}
		this.setTitleText(true,(vp.getCurrentItem()+1)+"/"+adapter.getCount());
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				ReChooseImageActivity.this.setTitleText(true,(vp.getCurrentItem()+1)+"/"+adapter.getCount());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		this.setTitleRigthIcon(true, R.drawable.classcircle_delete_icon);
		this.setTitleLeftIcon(true, R.drawable.back_btn);
	}
	
	@OnClick(R.id.right_layout)
	public void onDeleteImge(View view){
		final int currentPosition = vp.getCurrentItem();
		if (adapter.getCount() > currentPosition) {
			if(dialog == null){
				dialog = new CustomDialog(ReChooseImageActivity.this);
			}
			dialog.setTitle("提示");
			dialog.setMessage("要删除这张照片吗？");
			dialog.setModel(CustomDialog.BOTH);
			dialog.setLeftBtnListener("取消", null);
			dialog.setRightBtnListener("确定", new CustomDialog.DialogListener() {
				@Override
				public void doClickButton(Button btn, CustomDialog dialog) {
					if (adapter.getCount() == 1) {
						images.clear();
						Intent delete = new Intent();
						delete.putExtra("bean", (ArrayList<PicTake>) images);
						PicTake picTake = adapter.getData().get(adapter.getCount()-1);
						if(picTake.isNetResouce()){
							sb.append(picTake.getMaterialId());
							sb.append(",");
						}
						delete.putExtra("materialids", sb.toString().trim());
						ReChooseImageActivity.this.setResult(RESULT_OK, delete);
						ReChooseImageActivity.this.finish();
						return;
					}
					adapter.deleteByIndex(currentPosition);
					ReChooseImageActivity.this.setTitleText(true, (vp.getCurrentItem() + 1) + "/" + adapter.getCount());
				}
			});
			if(!isFinishing()){
				dialog.show();
			}
		}
		else {
			ToastUtil.show(this,"没有可删除的图片");
		}
	}
	
	@Override
	public void onBackPressed() {
		handlerBack();
	}
	
	public void handlerBack() {
		Intent delete = new Intent();
		delete.putExtra("bean", (ArrayList<PicTake>)adapter.getData());
		delete.putExtra("materialids", sb.toString().trim());
		setResult(RESULT_OK, delete);
		finish();
	}
	@OnClick(R.id.left_layout)
	public void onFinish(View view){
		handlerBack();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {

	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {

	}

	private class BigPicAdapter extends PagerAdapter {
		private List<PicTake> images = new ArrayList<PicTake>();
		public BigPicAdapter(List<PicTake> images){
			if (images == null) {
				return ;
			}
			this.images.clear();
			this.images.addAll(images);
		}
		public List<PicTake> getData () {
			return images;
		}
		@Override
		public int getCount() {
			return images.size();
		}
		
		public void deleteByIndex (int position) {
			PicTake picTake = this.images.get(position);
			picTake.setSelect(false);
			if(picTake.isNetResouce()){
				sb.append(picTake.getMaterialId());
				sb.append(",");
			}
			images.remove(position);
			int index = 0;
			if (position >0){
				index = position-1;
			}
			vp.setAdapter(this);
			vp.setCurrentItem(index);
		}
		@Override
		public Object instantiateItem(ViewGroup container,final int position) {
			IPhotoDraweeView photoView = (IPhotoDraweeView) View.inflate(mContext, com.kids.commonframe.R.layout.item_simpledrawable_view,null);
			PicTake bean = images.get(position);
			if(bean.isNetResouce()){
				photoView.setTag(bean.getUrl());
			}else{
				photoView.setTag(bean.getPicPath());
			}

			Uri uri = Uri.parse(bean.getUrl());
			ResizeOptions resizeOptions = new ResizeOptions(GlobalConstant.screenW, GlobalConstant.screenH);

			Drawable defaultDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.pv_wait_color));
			FrecoFactory.getInstance(mContext).disPlay(photoView,uri,resizeOptions,defaultDrawable, ScalingUtils.ScaleType.CENTER_INSIDE);
			photoView.enable();
			container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
}
