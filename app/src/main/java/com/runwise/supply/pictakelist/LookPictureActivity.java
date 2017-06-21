package com.runwise.supply.pictakelist;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.IPhotoDraweeView;
import com.kids.commonframe.config.Constant;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

import java.util.ArrayList;
import java.util.List;


public class LookPictureActivity extends BaseActivity {
	private BigPicAdapter adapter;
	@ViewInject(R.id.jazzyViewPager)
	private ViewPager vp;

	@ViewInject(R.id.bottomLayout)
	private View bottomLayout;

	@ViewInject(R.id.doFinish)
	private LinearLayout finishBtn;

	private SelectPictureActivity bean;

	@ViewInject(R.id.selectCount)
	private TextView selectCount;

	private boolean isAll = false;

	private int currentPosition;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(com.runwise.supply.R.layout.activity_look_picture);
//		setTitleTitleBg(Color.parseColor("#77000000"));
		List<PicTake> takeList = (List<PicTake>) this.getIntent().getSerializableExtra(Constant.ALBUM_PICTAKES);
		int position = this.getIntent().getIntExtra("position", 0);

		bean = SelectPictureActivity.getInstance();
		if(takeList == null){
			if(bean != null){
				isAll = true;
				takeList = bean.getPicLists();
			}
		}
		if(takeList != null){
			adapter = new BigPicAdapter(takeList);
			vp.setAdapter(adapter);
			vp.setPageMargin(40);
			position -= 1;
			if ( position > 0 ) {
				currentPosition = position;
				vp.setCurrentItem(position, false);
			}
		}

		this.setTitleText(true,(vp.getCurrentItem()+1)+"/"+adapter.getCount());
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentPosition = position;
				setChecBoxSelect();
				LookPictureActivity.this.setTitleText(true, (vp.getCurrentItem() + 1) + "/" + adapter.getCount());
				adapter.setPhotoViewReset(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		this.setTitleLeftIcon(true, R.drawable.back_btn);
		setSelectToLable();
		setChecBoxSelect();
	}
	private void setSelectToLable () {
		if (bean == null) {
			return;
		}
		selectCount.setText("" + bean.getSelectPic());
		if( bean.getSelectPic() == 0) {
			selectCount.setVisibility(View.GONE);
		}
		else {
			selectCount.setVisibility(View.VISIBLE);
		}
	}

	@OnClick(R.id.right_layout)
	public void selectImg (View view) {
		PicTake picTake = adapter.getData().get(currentPosition);
		if (picTake.isSelect()) {
			picTake.setSelect(false);
			if(!isAll) { //非全部图片
				bean.getPicLists().get(picTake.getPosition()).setSelect(false);
			}
			this.setTitleRigthIcon(true, R.drawable.list_checkbox_uncheckx);
		}
		else {
			if (bean == null) {
				return;
			}
			if (bean.getSelectPic() >= Constant.PIC_MAX_COUNT) {
				ToastUtil.show(LookPictureActivity.this, "最多选择" + Constant.PIC_MAX_COUNT + "张图片");
				return;
			}
			picTake.setSelect(true);
			if(!isAll) { //非全部图片
				bean.getPicLists().get(picTake.getPosition()).setSelect(true);
			}
			this.setTitleRigthIcon(true, R.drawable.list_checkbox_checkx);
		}
		setSelectToLable();
	}
	private void setChecBoxSelect () {
		PicTake bean = adapter.getData().get(currentPosition);
		if ( bean.isSelect() ) {
			this.setTitleRigthIcon(true, R.drawable.list_checkbox_checkx);
		}
		else {
			this.setTitleRigthIcon(true, R.drawable.list_checkbox_uncheckx);
		}
	}

	/*点击完成*/
	@OnClick(R.id.doFinish)
	public void doFinish (View view) {
		Intent intent = new Intent();
		this.setResult(Activity.RESULT_OK, intent);
		this.finish();
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
			this.images.get(position).setSelect(false);
			images.remove(position);
			int index = 0;
			if (position >0){
				index = position-1;
			}
			vp.setAdapter(this);
			vp.setCurrentItem(index);
		}
		public void setUnselect (int position) {
			this.images.get(position).setSelect(false);
		}
		@Override
		public Object instantiateItem(ViewGroup container,final int position) {
			IPhotoDraweeView photoView = (IPhotoDraweeView) View.inflate(mContext, R.layout.item_simpledrawable_view,null);
			PicTake bean = images.get(position);
			photoView.setTag(bean.getPicPath());

			Uri uri = Uri.parse(bean.getUrl());
			ResizeOptions resizeOptions = new ResizeOptions(GlobalConstant.screenW, GlobalConstant.screenH);

			Drawable defaultDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.pv_wait_color));
			FrecoFactory.getInstance(mContext).disPlay(photoView,uri,resizeOptions,defaultDrawable, ScalingUtils.ScaleType.CENTER_INSIDE);
			photoView.enable();
			container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			return photoView;
		}
		public void setPhotoViewReset(int position) {
			if ( position < images.size()) {
				PicTake bean = images.get(position);
				IPhotoDraweeView photoView = (IPhotoDraweeView)vp.findViewWithTag(bean.getPicPath());
				if ( photoView != null ) {
					photoView.reset();
				}
			}
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
	@OnClick(R.id.left_layout)
	public void doBack (View view) {
		this.finish();
	}

}