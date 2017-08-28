package com.runwise.supply.mine;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ImageUtils;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.kids.commonframe.base.view.CustomSelectDialog;
import com.kids.commonframe.base.view.CustomUploadDialog;
import com.kids.commonframe.base.view.widget.PickerClickListener;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.runwise.supply.ChangePhoneActivity;
import com.runwise.supply.FindPasswordActivity;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.FilterItem;
import com.runwise.supply.business.entity.FilterList;
import com.runwise.supply.business.entity.Province;
import com.runwise.supply.business.entity.ProvinceList;
import com.runwise.supply.business.entity.ProvinceRequest;
import com.runwise.supply.entity.QnTokenRepEntity;
import com.runwise.supply.mine.entity.EditUserInfoRequest;
import com.runwise.supply.mine.entity.UpdateUserInfo;
import com.runwise.supply.mine.entity.UpdateUserInfoRep;
import com.runwise.supply.mine.entity.UploadImg;
import com.runwise.supply.tools.StatusBarUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.runwise.supply.R.id.orderRed;


public class EditUserinfoActivity extends NetWorkActivity {
    private final int REQUEST_UPDATE_NAME = 10;
    private final int REQUEST_UPDATE_AGE = 11;
    private final int REQUEST_UPDATE_SEX = 12;
    private final int REQUEST_UPDATE_PHONENUMBER = 13;
    private final int REQUEST_UPLOAD_IMAGEFILE = 14;
    private final int REQUEST_LOGINOUT = 15;
    private static final int REQUEST_USERINFO = 16;


    private final int RET_CAMERA = 101;
    private final int RET_GALLERY = 102;
    private Uri capTempPhotoUrl;
    private CustomUploadDialog uploadDialog;
    private Uri updateImgUri;

    private String token;
    private String growthUrl;

    private UserInfo userInfo;

    @ViewInject(R.id.userHead)
    private SimpleDraweeView userHead;

    @ViewInject(R.id.userInfoName)
    private TextView userInfoName;
    @ViewInject(R.id.userInfoId)
    private TextView userInfoId;
    @ViewInject(R.id.userInfoPlace)
    private TextView userInfoPlace;
    @ViewInject(R.id.userInfoStore)
    private TextView userInfoStore;
    @ViewInject(R.id.userInfoPhone)
    private TextView userInfoPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_edit_userinfo);
        capTempPhotoUrl = Uri.fromFile(new File(CommonUtils.getCachePath(mContext),"temp.jpg"));
        userInfo = GlobalApplication.getInstance().loadUserInfo();

        if( userInfo != null) {
            FrecoFactory.getInstance(this).disPlay(userHead, Constant.BASE_URL + userInfo.getAvatarUrl());
            userInfoName.setText(userInfo.getUsername());
            userInfoId.setText(userInfo.getLogin());
            if (TextUtils.isEmpty(userInfo.getRegion())) {
                userInfoPlace.setText("暂无");
            } else {
                userInfoPlace.setText(userInfo.getRegion());
            }
            userInfoStore.setText(userInfo.getMendian());
            userInfoPhone.setText(userInfo.getMobile());
        }
        this.setTitleText(true,"个人信息");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_UPLOAD_IMAGEFILE:
                UploadImg uploadImg = (UploadImg)result.getResult();
                LogUtils.e(uploadImg.getAvatar_url());
                requestUserInfo();
                break;
            case REQUEST_LOGINOUT:
                SPUtils.loginOut(mContext);
                GlobalApplication.getInstance().cleanUesrInfo();
                //退出登录
                EventBus.getDefault().post(new UserLogoutEvent());
                finish();
                break;
            case REQUEST_USERINFO:
                UserInfo userInfo = (UserInfo) result.getResult().getData();
//                userInfo.setAvatarUrl(userInfo.getAvatarUrl());
                GlobalApplication.getInstance().saveUserInfo(userInfo);
                Fresco.getImagePipeline().evictFromCache(updateImgUri);
                FrecoFactory.getInstance(this).disPlay(userHead, updateImgUri);
                dismissUploadDialog("头像上传成功!");
                break;
        }

    }
    private void requestUserInfo() {
        Object paramBean = null;
        this.sendConnection("/gongfu/v2/user/information",paramBean ,REQUEST_USERINFO, false, UserInfo.class);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(this,errMsg);
    }
    @OnClick({R.id.userInfoHead,R.id.left_layout,R.id.userInfoPhoneLayout,R.id.userInfoChangePhoneLayout,R.id.exit_user})
    public void handlerClickEvent(View view) {
        switch (view.getId()) {
            case R.id.userInfoHead:
                uploadDialog = new CustomUploadDialog(mContext);
                final CustomBottomDialog customBottomDialog = new CustomBottomDialog(mContext);
                capTempPhotoUrl = Uri.fromFile(new File(CommonUtils.getCachePath(mContext),"temp.jpg"));
                ArrayMap<Integer, String> menus = new ArrayMap<>();
                menus.put(0, "从相册选择");
                menus.put(1, "拍照");
                customBottomDialog.addItemViews(menus);
                customBottomDialog.setOnBottomDialogClick(new CustomBottomDialog.OnBottomDialogClick() {
                    @Override
                    public void onItemClick(View view) {
                        switch (view.getId()) {
                            case 0:
                                startAlbum();
                                break;
                            case 1:
                                startCapture();
                                break;

                        }
                        customBottomDialog.dismiss();
                    }
                });
                customBottomDialog.show();
                break;
            case R.id.left_layout:
                finish();
                break;
            //退出登录
            case R.id.exit_user:
                showLogoutDialog();
                break;
            //重置密码
            case R.id.userInfoChangePhoneLayout:
                this.startActivity(new Intent(this, FindPasswordActivity.class));
                break;
            //手机号
            case R.id.userInfoPhoneLayout:
                Intent intentPhone = new Intent(this, ChangePhoneActivity.class);
                startActivity(intentPhone);
//                Intent intentPhone = new Intent(this, UpdateUserInfoActivity.class);
//                intentPhone.putExtra("type",UpdateUserInfoActivity.UPDATE_PHONE_NUMBER);
//                startActivityForResult(intentPhone,REQUEST_UPDATE_PHONENUMBER);
                break;

        }
    }

    /**
     * 进入相册
     */
    private void startAlbum() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            startActivityForResult(intent, RET_GALLERY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, RET_GALLERY);
            } catch (Exception e2) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开启相机
     */
    private void startCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capTempPhotoUrl);
        startActivityForResult(intent, RET_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //相机
                case RET_CAMERA:
                    startCropActivity(capTempPhotoUrl);
                    break;
                // 相册回调
                case RET_GALLERY:
                    startCropActivity(data.getData());
                    break;
                //裁切完成
                case UCrop.REQUEST_CROP:
                    updateImgUri = UCrop.getOutput(data);
                    showUploadDialog();
                    List<Part> partList = new ArrayList<>();
                    try {
                        String path1Scaled = ImageUtils.getScaledImage(this,updateImgUri.getPath());
                        partList.add(new FilePart("avatar_file", new File(path1Scaled)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    sendConnection("/gongfu/user/avatar", partList, REQUEST_UPLOAD_IMAGEFILE, false, UploadImg.class);
                    break;
                case REQUEST_UPDATE_NAME:
                    userInfo = GlobalApplication.getInstance().loadUserInfo();
//                    userInfoName.setText(userInfo.getNickname());
                    break;
                case REQUEST_UPDATE_AGE:
                    userInfo = GlobalApplication.getInstance().loadUserInfo();
//                    userInfoAge.setText(userInfo.getAge());
                    break;
                case REQUEST_UPDATE_SEX:
                    userInfo = GlobalApplication.getInstance().loadUserInfo();
//                    userInfoSex.setText("2".equals(userInfo.getSex()) ? "女" : "男");
                    break;
                case REQUEST_UPDATE_PHONENUMBER:
                    userInfo = GlobalApplication.getInstance().loadUserInfo();
//                    userInfoPhone.setText(userInfo.getPhone());
                    break;
            }
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(CommonUtils.getCachePath(mContext), "temp2.jpg")));
        uCrop = advancedConfig(uCrop);
        uCrop.start(this);
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setShowCropGrid(false);
        options.setImageToCropBoundsAnimDuration(100);
        options.setOvalDimmedLayer(false);
        options.setCropFrameColor(Color.GREEN);
        options.setHideBottomControls(true);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.NONE);
        options.withAspectRatio(1, 1);
        return uCrop.withOptions(options);
    }

    private void dismissUploadDialog(String hint) {
        if (uploadDialog != null && !this.isFinishing()) {
            uploadDialog.setViewVisibility(false, true);
            uploadDialog.setHint(hint);
            uploadDialog.holdDismiss(1);
        }
    }

    private void showUploadDialog() {
        uploadDialog.setMessage("正在上传");
        uploadDialog.setViewVisibility(true, false);
        if (!this.isFinishing() && !uploadDialog.isShowing()) {
            uploadDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new UpdateUserInfo());
    }

    private void showLogoutDialog() {
        final CustomBottomDialog mLogoutDialog = new CustomBottomDialog(this);
        ArrayMap<Integer, String> menus = new ArrayMap<Integer, String>();
        menus.put(0, "退出登录");
        mLogoutDialog.addItemViews(menus);
        mLogoutDialog.setOnBottomDialogClick(new CustomBottomDialog.OnBottomDialogClick() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()) {
                    case 0:
                        Object param = null;
                        sendConnection("/gongfu/logout",param,REQUEST_LOGINOUT,true,null);
                        break;
                }
                mLogoutDialog.dismiss();
            }
        });
        mLogoutDialog.show();
    }
}
