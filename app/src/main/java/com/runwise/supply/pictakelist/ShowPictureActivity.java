/**
 * 
 */
package com.runwise.supply.pictakelist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.runwise.supply.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.ImageUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.GlobalConstant;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * @ClassName: 使用照片
 */
public class ShowPictureActivity extends BaseActivity {
	private final static int  RETRY_PHONE = 6;
	
	private SimpleDraweeView imageView;
	private Button btn_Remake, btn_save_picture, btn_use_picture;// 重拍 保存照片 使用照片
	private PicTake  takePic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpicture);
		takePic = (PicTake) this.getIntent().getSerializableExtra("capturePath");
		findId();
		setListener();
        File capturePath = new File (takePic.getPicPath());
	    ImageUtils.getScaledImage( mContext , capturePath);
		ResizeOptions resizeOptions = new ResizeOptions(GlobalConstant.screenW, GlobalConstant.screenH);
		FrecoFactory.getInstance(mContext).disPlay(imageView, Uri.fromFile(capturePath), resizeOptions, mContext.getResources().getDrawable(R.drawable.pictures_no), ScalingUtils.ScaleType.FIT_CENTER);
	}
	/**
	 * 查找id
	 */
	private void findId() {
		imageView = (SimpleDraweeView) findViewById(R.id.iv);
		btn_Remake = (Button) findViewById(R.id.btn_Remake);
		btn_save_picture = (Button) findViewById(R.id.btn_save_picture);
		btn_use_picture = (Button) findViewById(R.id.btn_use_picture);
	}

	/**
	 * 
	 */
	private void setListener() {
		// 重拍
		btn_Remake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File (takePic.getPicPath());
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, RETRY_PHONE);
			}
		});

		// 保存照片
		btn_save_picture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					File imgfile = new File(takePic.getPicPath());
					MediaStore.Images.Media.insertImage(ShowPictureActivity.this.getContentResolver(),
							imgfile.getAbsolutePath(),imgfile.getName(), null);
					ShowPictureActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
							Uri.fromFile(imgfile.getParentFile())));
					ToastUtil.show(ShowPictureActivity.this, "保存成功!");
				} catch (FileNotFoundException e) {
					ToastUtil.show(ShowPictureActivity.this, "保存失败!");
					e.printStackTrace();
				}
				ToastUtil.show(mContext, "照片已保存");
			}
		});

		// 使用照片
		btn_use_picture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 发送照片
			    Intent dataIntent = new Intent();
			    dataIntent.putExtra("capturePath", takePic);
				ShowPictureActivity.this.setResult(Activity.RESULT_OK , dataIntent);
				ShowPictureActivity.this.finish();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == RETRY_PHONE) {
				File targerFile = new File (takePic.getPicPath());
				Fresco.getImagePipeline().evictFromCache(Uri.fromFile(targerFile));
				ImageUtils.getScaledImage( mContext ,targerFile);
				ResizeOptions resizeOptions = new ResizeOptions(GlobalConstant.screenW, GlobalConstant.screenH);
				Drawable defaultDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.pv_wait_color));
				FrecoFactory.getInstance(mContext).disPlay(imageView, Uri.fromFile(targerFile), resizeOptions, defaultDrawable, ScalingUtils.ScaleType.FIT_CENTER);
			}
		}
	}

}
