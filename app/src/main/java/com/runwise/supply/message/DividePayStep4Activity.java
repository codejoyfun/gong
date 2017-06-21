package com.runwise.supply.message;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ImageUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.IWebViewActivity;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;
import com.runwise.supply.message.entity.ApplyResult;
import com.runwise.supply.message.entity.ImageFileUrl;
import com.runwise.supply.message.entity.ImageFileUrlData;
import com.runwise.supply.message.entity.ImageFileUrlResult;
import com.runwise.supply.message.entity.PhoneContact;
import com.runwise.supply.message.entity.PhoneContactList;
import com.runwise.supply.message.entity.PhoneContactRequest;
import com.runwise.supply.message.entity.Step4Request;
import com.runwise.supply.mine.entity.UrlResult;
import com.runwise.supply.pictakelist.PicTake;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 申请分期第4步
 */
public class DividePayStep4Activity extends NetWorkActivity {
	private final int REQUEST_HELP = 5;

	private int PERMISSION_READ_CONTACT = 1;
	private final int REQUEST_COMMIT_DATA = 2;
	private final int REQUEST_CONTACT_DATA = 3;
	private final int REQUEST_UPLOAD_IMAGEFILE = 4;
	private final int WHAT_MUIN = 5;

	@ViewInject(R.id.paystep4_text1)
	private TextView paystep4_text1;
	@ViewInject(R.id.paystep4_text2)
	private TextView paystep4_text2;
	@ViewInject(R.id.paystep4_text3)
	private TextView paystep4_text3;
	@ViewInject(R.id.paystep4_text4)
	private TextView paystep4_text4;
	@ViewInject(R.id.paystep4_text5)
	private TextView paystep4_text5;
	@ViewInject(R.id.paystep4_text6)
	private TextView paystep4_text6;
	@ViewInject(R.id.paystep4_text7)
	private TextView paystep4_text7;
	@ViewInject(R.id.paystep4_text8)
	private TextView paystep4_text8;

	@ViewInject(R.id.paystep4_pic1)
	private SimpleDraweeView paystep4_pic1;
	@ViewInject(R.id.paystep4_pic2)
	private SimpleDraweeView paystep4_pic2;
	@ViewInject(R.id.paystep4_pic3)
	private SimpleDraweeView paystep4_pic3;
	@ViewInject(R.id.paystep4_pic4)
	private SimpleDraweeView paystep4_pic4;
	@ViewInject(R.id.paystep4_pic5)
	private SimpleDraweeView paystep4_pic5;
	@ViewInject(R.id.paystep4_pic6)
	private SimpleDraweeView paystep4_pic6;
	@ViewInject(R.id.paystep4_pic7)
	private SimpleDraweeView paystep4_pic7;
	@ViewInject(R.id.paystep4_pic8)
	private SimpleDraweeView paystep4_pic8;
//	@ViewInject(R.id.proceSeeLayout)
//	private View proceSeeLayout7;

	@ViewInject(R.id.paystep4_button)
	private Button paystep4_button;
	@ViewInject(R.id.paystep4_checkbox)
	private CheckBox paystep4_checkbox;

	private TextView globalTextView;
	private SimpleDraweeView globalDraweeView;

	private List<PhoneContact> contactInfoList = new ArrayList<>();;

	private Map<String,String> uploadMap = new HashMap<>();
	private String currentPath;
	private List<PicTake> uploadList = new ArrayList<>();//上传集合
	@ViewInject(R.id.classcircle_send_gridview)
	private GridView gridView;
	PicTakeAdapter picTakeAdapter;
	private int currentPosition;
	private int what;

	private boolean isMerry;
	@ViewInject(R.id.merryLayout)
	private View merryLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paystep_4);
		this.setTitleText(true,"(4/4)上传资料");
		this.setTitleLeftIcon(true,R.drawable.back_btn);
		setFinishStatus();
		picTakeAdapter = new PicTakeAdapter();
		gridView.setAdapter(picTakeAdapter);
		picTakeAdapter.setData(uploadList);

		String ismerry = this.getIntent().getStringExtra("ismerry");
		if("已婚".equals(ismerry)) {
			isMerry = true;
			merryLayout.setVisibility(View.VISIBLE);
		}
		else {
			isMerry = false;
			merryLayout.setVisibility(View.GONE);
		}
		paystep4_text1.addTextChangedListener(new TextWatchListener());
		paystep4_text2.addTextChangedListener(new TextWatchListener());
		paystep4_text3.addTextChangedListener(new TextWatchListener());
		paystep4_text4.addTextChangedListener(new TextWatchListener());
		paystep4_text5.addTextChangedListener(new TextWatchListener());
		paystep4_text6.addTextChangedListener(new TextWatchListener());
		paystep4_text7.addTextChangedListener(new TextWatchListener());
		paystep4_text8.addTextChangedListener(new TextWatchListener());

		addUploadListener(paystep4_pic1,paystep4_text1);
		addUploadListener(paystep4_pic2,paystep4_text2);
		addUploadListener(paystep4_pic3,paystep4_text3);
		addUploadListener(paystep4_pic4,paystep4_text4);
		addUploadListener(paystep4_pic5,paystep4_text5);
		addUploadListener(paystep4_pic6,paystep4_text6);
		addUploadListener(paystep4_pic7,paystep4_text7);
		addUploadListener(paystep4_pic8,paystep4_text8);
//		proceSeeLayout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent data = new Intent(mContext, DevidePayProtocolActivity.class);
//				startActivityForResult(data,RET_SIGN);
//			}
//		});

		SpannableString spannableString = new SpannableString(paystep4_checkbox.getText());
		spannableString.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				ds.setColor(getResources().getColor(R.color.base_color));       //设置文件颜色
			}
			@Override
			public void onClick(View widget) {
				sendConnection("protocol/detail.json",REQUEST_HELP,true, UrlResult.class);
			}
		}, 4, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		paystep4_checkbox.setText(spannableString);
		paystep4_checkbox.setHighlightColor(Color.TRANSPARENT);
		paystep4_checkbox.setMovementMethod(LinkMovementMethod.getInstance());

		if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_CONTACTS }, PERMISSION_READ_CONTACT);
		}
		getContactList();

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentPosition = position;
				globalTextView = null;
						final CustomBottomDialog customBottomDialog = new CustomBottomDialog(mContext);
						ArrayMap<Integer, String> menus = new ArrayMap<>();
						menus.put(0, "从相册选择");
						menus.put(1, "拍照");
						customBottomDialog.addItemViews(menus);
						customBottomDialog.setOnBottomDialogClick(new CustomBottomDialog.OnBottomDialogClick() {
							@Override
							public void onItemClick(View view) {
								what = WHAT_MUIN;
								switch (view.getId()) {
									case 0:
										try {
											Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
											intent.setType("image/*");
											startActivityForResult(intent, RET_GALLERY);
										} catch (ActivityNotFoundException e) {
											e.printStackTrace();
											try {
												Intent intent = new Intent(Intent.ACTION_PICK, null);
												intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
												startActivityForResult(intent, RET_GALLERY);
											} catch (Exception e2) {
												e.printStackTrace();
											}
										}
										break;
									case 1:
										File cameFile = new File(CommonUtils.getCachePath(mContext),"temp"+ UUID.randomUUID()+".jpg");
										PicTake picTake = getUploadPic(currentPosition);
										picTake.setPicPath(cameFile.getAbsolutePath());
										Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
										intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameFile));
										startActivityForResult(intent, RET_CAMERA);
										break;

								}
								customBottomDialog.dismiss();
							}
						});
						customBottomDialog.show();
			}
		});
	}

	public PicTake getUploadPic(int position) {
		if(position < uploadList.size()) {
			return uploadList.get(position);
		}
		PicTake picTake = new PicTake();
		picTake.setType(PicTake.PIC);
		uploadList.add(picTake);
		return picTake;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSuccess(BaseEntity result, int where) {
		switch (where) {
			case REQUEST_COMMIT_DATA:
				ApplyResult data = (ApplyResult) result;
				if(contactInfoList != null) {
//					List<PhoneContact> tempList = new ArrayList<>();
//					tempList.add(contactInfoList.get(0));
//					tempList.add(contactInfoList.get(1));

					PhoneContactList contactList = new PhoneContactList();
					contactList.setList(contactInfoList);

					PhoneContactRequest request = new PhoneContactRequest();
					request.setContact(JSON.toJSONString(contactList));
//ToastUtil.show(mContext,"申请成功");
					sendConnection("contacts/store.json", request, REQUEST_CONTACT_DATA, true, BaseEntity.class);
				}
				break;
			case REQUEST_CONTACT_DATA:
				Intent finalIntent = new Intent(this,DividePayStepFinishActivity.class);
				startActivity(finalIntent);
				break;

			case REQUEST_UPLOAD_IMAGEFILE:
				ImageFileUrlResult fileUrlResult = (ImageFileUrlResult)result;
				ImageFileUrlData fileUrlData = fileUrlResult.getData();
				ImageFileUrl fileUrl = fileUrlData.getEntity();
				if ( globalTextView != null ) {
					globalTextView.setText(currentPath);
				}
				uploadMap.put(currentPath,fileUrl.getImage_url());
				break;

			case REQUEST_HELP:
				UrlResult helperResult = (UrlResult)result;
				Intent intent = new Intent(mContext, IWebViewActivity.class);
				intent.putExtra(WebViewActivity.WEB_TITLE,"贷款协议");
				intent.putExtra(WebViewActivity.WEB_URL,helperResult.getData().getEntity().getUrl_addr());
				startActivity(intent);
				break;

		}
	}

	@Override
	public void onFailure(String errMsg, BaseEntity result, int where) {
		ToastUtil.show(mContext,errMsg);
	}

	@OnClick(R.id.paystep4_button)
	public void doNext (View view) {
		if (!paystep4_checkbox.isChecked()) {
			dialog.setModel(CustomDialog.LEFT);
			dialog.setLeftBtnListener("知道啦", null);
			dialog.setMessage("请同意《用户使用协议》");
			dialog.show();
			return;
		}
		String paystep4_text1 = this.paystep4_text1.getText().toString().trim();
		String paystep4_text2 = this.paystep4_text2.getText().toString().trim();
		String paystep4_text3 = this.paystep4_text3.getText().toString().trim();
		String paystep4_text4 = this.paystep4_text4.getText().toString().trim();
		String paystep4_text5 = this.paystep4_text5.getText().toString().trim();
		String paystep4_text6 = this.paystep4_text6.getText().toString().trim();
		String paystep4_text7 = this.paystep4_text7.getText().toString().trim();
		String paystep4_text8 = this.paystep4_text8.getText().toString().trim();

		Intent intent = getIntent();
		String applyId = intent.getStringExtra("applyId");

		Step4Request step4Request = new Step4Request();
		step4Request.setApply_id(applyId);
		step4Request.setIdentity_front_img(uploadMap.get(paystep4_text1));
		step4Request.setIdentity_back_img(uploadMap.get(paystep4_text2));
		step4Request.setIdentity_hand_img(uploadMap.get(paystep4_text3));
		step4Request.setDebit_card_img(uploadMap.get(paystep4_text4));
		step4Request.setScene_img(uploadMap.get(paystep4_text5));
		step4Request.setDriving_licence_img(uploadMap.get(paystep4_text6));
		if (isMerry) {
			step4Request.setId_spouse_front(uploadMap.get(paystep4_text7));  //配偶正面
			step4Request.setId_spouse_back(uploadMap.get(paystep4_text8));   //配偶反面
		}
		for (int i =0 ; i< uploadList.size(); i ++) {
			PicTake picTake = uploadList.get(i);
			LogUtils.e(uploadMap.get(picTake.getPicPath()));
			switch (i) {
				case 0:
					step4Request.setReport_img_0(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 1:
					step4Request.setReport_img_1(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 2:
					step4Request.setReport_img_2(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 3:
					step4Request.setReport_img_3(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 4:
					step4Request.setReport_img_4(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 5:
					step4Request.setReport_img_5(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 6:
					step4Request.setReport_img_6(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 7:
					step4Request.setReport_img_7(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 8:
					step4Request.setReport_img_8(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
				case 9:
					step4Request.setReport_img_9(uploadMap.get(picTake.getPicPath()));   //信用报告
					break;
			}
		}
		sendConnection("apply/file_info.json", step4Request, REQUEST_COMMIT_DATA, true, ApplyResult.class);
	}

	@OnClick(R.id.left_layout)
	public void doBack (View view) {
		this.finish();
	}

	private class TextWatchListener implements TextWatcher{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			setFinishStatus();
		}
	}

	private void setFinishStatus () {
		String paystep4_text1 = this.paystep4_text1.getText().toString().trim();
		String paystep4_text2 = this.paystep4_text2.getText().toString().trim();
		String paystep4_text3 = this.paystep4_text3.getText().toString().trim();
		String paystep4_text4 = this.paystep4_text4.getText().toString().trim();
		String paystep4_text5 = this.paystep4_text5.getText().toString().trim();
		String paystep4_text6 = this.paystep4_text6.getText().toString().trim();
		String paystep4_text7 = this.paystep4_text7.getText().toString().trim();
		String paystep4_text8 = this.paystep4_text8.getText().toString().trim();
		boolean requetGo  = false;
		if (isMerry) {
			if (!TextUtils.isEmpty(paystep4_text7) && !TextUtils.isEmpty(paystep4_text8)) {
				requetGo = true;
			}
			else {
				requetGo = false;
			}
		}
		else {
			requetGo = true;
		}
		if ( !TextUtils.isEmpty(paystep4_text1) && !TextUtils.isEmpty(paystep4_text2)
				&& !TextUtils.isEmpty(paystep4_text3) && !TextUtils.isEmpty(paystep4_text4)
				&& !TextUtils.isEmpty(paystep4_text5) && !TextUtils.isEmpty(paystep4_text6)
				&& requetGo
				&& !uploadList.isEmpty()) {
			paystep4_button.setEnabled(true);
		}
		else {
			paystep4_button.setEnabled(false);
		}
	}

	//上传图片通用类
	private final int RET_CAMERA = 101;
	private final int RET_GALLERY = 102;
	private final int RET_SIGN = 103;

	private void addUploadListener(final SimpleDraweeView paystepPic,final TextView textView) {
		paystepPic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CustomBottomDialog customBottomDialog = new CustomBottomDialog(mContext);
				ArrayMap<Integer, String> menus = new ArrayMap<>();
				menus.put(0, "从相册选择");
				menus.put(1, "拍照");
				customBottomDialog.addItemViews(menus);
				customBottomDialog.setOnBottomDialogClick(new CustomBottomDialog.OnBottomDialogClick() {
					@Override
					public void onItemClick(View view) {
						switch (view.getId()) {
							case 0:
								startAlbum(paystepPic,textView);
								break;
							case 1:
								startCapture(paystepPic,textView);
								break;

						}
						customBottomDialog.dismiss();
					}
				});
				customBottomDialog.show();
			}
		});

	}
	/**
	 * 进入相册
	 */
	private void startAlbum(final SimpleDraweeView paystepPic,final TextView textView) {
		globalTextView = textView;
		globalDraweeView = paystepPic;
		what = 0;
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			startActivityForResult(intent, RET_GALLERY);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			try {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, RET_GALLERY);
			} catch (Exception e2) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开启相机
	 */
	private void startCapture(final SimpleDraweeView paystepPic,final TextView textView) {
		globalTextView = textView;
		globalDraweeView = paystepPic;
		what = 0;
		File cameFile = new File(CommonUtils.getCachePath(mContext),"temp"+ UUID.randomUUID()+".jpg");
		globalTextView.setTag(cameFile.getAbsolutePath());
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameFile));
		startActivityForResult(intent, RET_CAMERA);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String filePath = null;
			switch (requestCode) {
				//相机
				case RET_CAMERA:
					if( what == WHAT_MUIN ) {
						PicTake picTake = getUploadPic(currentPosition);
						filePath = picTake.getPicPath();
						picTakeAdapter.setData(uploadList);
					}
					else {
						filePath = (String) globalTextView.getTag();
//						globalTextView.setText(filePath);
						FrecoFactory.getInstance(mContext).disPlay(globalDraweeView,Uri.fromFile(new File(ImageUtils.getScaledImage(this,filePath))));
					}
					currentPath = filePath;
					break;
				// 相册回调
				case RET_GALLERY:
					if( what == WHAT_MUIN ) {
						filePath = CommonUtils.getImageAbsolutePath(this, data.getData());
						PicTake picTake = getUploadPic(currentPosition);
						picTake.setPicPath(filePath);
						picTakeAdapter.setData(uploadList);
					}
					else {
						filePath = CommonUtils.getImageAbsolutePath(this, data.getData());
//						globalTextView.setText(filePath);
						FrecoFactory.getInstance(mContext).disPlay(globalDraweeView, Uri.fromFile(new File(ImageUtils.getScaledImage(this,filePath))));
					}
					currentPath = filePath;
					break;
//				//签名
//				case RET_SIGN:
//					String signPath = data.getStringExtra("signPath");
//					paystep4_text7.setText(signPath);
//					currentPath = signPath;
//					break;
			}
			List<Part> partList = new ArrayList<>();
			try {
				String path1Scaled = ImageUtils.getScaledImage(this,currentPath);
				partList.add(new FilePart("picture", new File(path1Scaled)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			sendConnection("upload/image.json", partList, REQUEST_UPLOAD_IMAGEFILE, true, ImageFileUrlResult.class);
//			ToastUtil.show(mContext,currentPath);
		}
	}
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinishActEvent(FinishActEvent event) {
		this.finish();
	}

	/**
	 * 获取联系人信息
	 * @return
     */
	private List<PhoneContact> getContactList() {

		try {
			Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
			while (cursor.moveToNext()) {
				PhoneContact contactInfo = new PhoneContact();
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				contactInfo.setRealname(contactName);
				contactInfo.setTelephone(contactNumber);
//				LogUtils.e(contactName);
				contactInfoList.add(contactInfo);
			}
			cursor.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
		}
		return contactInfoList;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] paramArrayOfInt) {
		if (requestCode == PERMISSION_READ_CONTACT) {
			//禁用了权限
			if (!verifyPermissions(paramArrayOfInt)) {
				dialog.setModel(CustomDialog.RIGHT);
				dialog.setMessage("您禁用了读取联系人权限,请在设置中开启");
				dialog.setRightBtnListener("知道啦",null);
				dialog.show();
			}
			else {
				getContactList();
			}
		}
	}
	/**
	 * 检测是否说有的权限都已经授权
	 * @param grantResults
	 * @return
	 * @since 2.5.0
	 *
	 */
	private boolean verifyPermissions(int[] grantResults) {
		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 上传文件 适配器
	 */
	private class PicTakeAdapter extends IBaseAdapter<PicTake> {

		int MAX_COUNT = 8;//允许添加的最大个数
		private int itemHeight;
		private LayoutInflater inflater;
		private ResizeOptions imageSize;

		public PicTakeAdapter() {
			int spacing = (int) DividePayStep4Activity.this.getResources().getDimension(R.dimen.verticalSpacing);
			itemHeight = (GlobalConstant.screenW/2) / 4;
			inflater = LayoutInflater.from(DividePayStep4Activity.this);
			this.imageSize = new ResizeOptions(itemHeight / 2, itemHeight / 2);
		}

		@Override
		public void setData(List<PicTake> list) {
			if (list == null) {
				return;
			}
			mList.clear();
			mList.addAll(list);

			if (mList.size() > MAX_COUNT) {
				int removeCount = mList.size() - MAX_COUNT;
				for (int i = mList.size() - 1, j = 0; j < removeCount; i--, j++) {
					mList.remove(i);
				}
			}
			addLastItem();
			this.notifyDataSetChanged();
		}

		@Override
		public List<PicTake> getList() {
			List<PicTake> selectList = new ArrayList<PicTake>();
			for (int i = 0; i < mList.size() - 1; i++) {
				selectList.add(mList.get(i));
			}
			return selectList;
		}

		private void addLastItem() {
			PicTake add = new PicTake();
			if (mList.size() < MAX_COUNT) {
				mList.add(add);
			}
		}

		@Override
		protected View getExView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.takepic_add_item, null);
				holder = new ViewHolder();
				ViewUtils.inject(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final PicTake bean = mList.get(position);
			//设置纵横比
			holder.addImg.setAspectRatio(1);
			holder.addImg.setTag(bean.getPicPath());
			holder.playVideoTag.setVisibility(View.GONE);
			if (bean.getType() == 0) {
				holder.addImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
				holder.addImg.setImageResource(R.drawable.fill_in_the_picture);
				holder.deleteImg.setVisibility(View.GONE);
			}
			else {
				holder.deleteImg.setVisibility(View.VISIBLE);
				holder.addImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
				FrecoFactory.getInstance(mContext).disPlay(holder.addImg, Uri.fromFile(new File(bean.getPicPath())), imageSize);
			}
			holder.deleteImg.setVisibility(View.GONE);
			holder.deleteImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					uploadList.remove(position);
					mList.remove(position);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}

		class ViewHolder {
			@ViewInject(R.id.addImg)
			SimpleDraweeView addImg;
			@ViewInject(R.id.playVideoTag)
			ImageView playVideoTag;
			@ViewInject(R.id.deleteImg)
			ImageView deleteImg;
		}
	}

}
