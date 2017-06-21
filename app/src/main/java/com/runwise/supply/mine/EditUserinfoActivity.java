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
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
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
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.FilterItem;
import com.runwise.supply.business.entity.FilterList;
import com.runwise.supply.business.entity.Province;
import com.runwise.supply.business.entity.ProvinceList;
import com.runwise.supply.business.entity.ProvinceRequest;
import com.runwise.supply.entity.QnTokenRepEntity;
import com.runwise.supply.entity.UserInfo;
import com.runwise.supply.mine.entity.EditUserInfoRequest;
import com.runwise.supply.mine.entity.UpdateUserInfo;
import com.runwise.supply.mine.entity.UpdateUserInfoRep;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EditUserinfoActivity extends NetWorkActivity {
    private final int REQUEST_CLASS_CIRCLE_TOKEN = 4;
    private final int REQUEST_GROWTH_RUL = 5;
    private final int REQUEST_UPDATE_NAME = 10;
    private final int REQUEST_UPDATE_AGE = 11;
    private final int REQUEST_UPDATE_SEX = 12;
    private final int REQUEST_UPDATE_PHONENUMBER = 13;

    private final int RET_CAMERA = 101;
    private final int RET_GALLERY = 102;
    private Uri capTempPhotoUrl;
    private CustomUploadDialog uploadDialog;
    private Uri updateImgUri;

    private String token;
    private String growthUrl;

    private static final int RESPONSE_WEIZI = 7;

    private static final int RESPONSE_JIGOU = 8;
    private static final int RESPONSE_LINGYU = 9;
    //位置
    private List<Province> provinceList;
    private final int REQUEST_UPDATE_PROVINCE = 13;
    //机构类型
    private List<FilterItem> orgList;
    private final int REQUEST_UPDATE_ORG = 14;
    //专业领域
    private List<FilterItem> typeList;
    private final int REQUEST_UPDATE_TYPE = 15;

    @ViewInject(R.id.shenfenText)
    private TextView shenfenText;
    @ViewInject(R.id.leixinText)
    private TextView leixinText;
    @ViewInject(R.id.lingyuText)
    private TextView lingyuText;
    private UserInfo userInfo;

    @ViewInject(R.id.userHead)
    private SimpleDraweeView userHead;

    @ViewInject(R.id.userInfoName)
    private TextView userInfoName;
    @ViewInject(R.id.userInfoAge)
    private TextView userInfoAge;
    @ViewInject(R.id.userInfoSex)
    private TextView userInfoSex;
    @ViewInject(R.id.userInfoPhone)
    private TextView userInfoPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userinfo);
        capTempPhotoUrl = Uri.fromFile(new File(CommonUtils.getCachePath(mContext),"temp.jpg"));
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        //专业领域
        sendConnection("/Appapi/lingyu/getList",RESPONSE_LINGYU,true,FilterList.class);
        //机构类型
        sendConnection("/Appapi/zhuanjia/getSuoshuList",RESPONSE_JIGOU,true,FilterList.class);
        //位置
        ProvinceRequest province = new ProvinceRequest();
        province.setType("all");
        sendConnection("/Appapi/user/getProvinceList",province,RESPONSE_WEIZI,true,ProvinceList.class);

        FrecoFactory.getInstance(this).disPlay(userHead,userInfo.getAvatar());
        userInfoName.setText(userInfo.getNickname());
//        userInfoAge.setText(userInfo.getAge());
//        userInfoSex.setText("2".equals(userInfo.getSex()) ? "女" : "男");
        userInfoPhone.setText(userInfo.getPhone());
        this.setTitleText(true,"个人信息");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CLASS_CIRCLE_TOKEN:
                QnTokenRepEntity tokenResponse = (QnTokenRepEntity) result;
                if (tokenResponse != null) {
                    token = tokenResponse.getData();
                }
                // 获取时间戳
                String key = UUID.randomUUID() + ".png";
                UploadManager uploadManager = new UploadManager();
                showUploadDialog();
                uploadManager.put(updateImgUri.getPath(), key, token, new UpCompletionHandler() {
                    @Override
                    public void complete(String arg0, ResponseInfo arg1, JSONObject arg2) {
                        String imageUrl = Constant.QINIU_URL + arg0;
                        updateGrowthUrl(imageUrl);
                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {

                    @Override
                    public void progress(String key, double percent) {
                        int progress = (int) (percent * 100);
                        LogUtils.i("PersonalInfoActivity_UploadOptions : " + percent
                                + "/" + progress);
                        // setDialogProgress(progress);
                    }
                }, null));
                break;
            case REQUEST_GROWTH_RUL:
                if (userInfo != null) {
                    userInfo.setAvatar(growthUrl);
                }
                GlobalApplication.getInstance().saveUserInfo(userInfo);
                Fresco.getImagePipeline().evictFromCache(updateImgUri);
                FrecoFactory.getInstance(this).disPlay(userHead, updateImgUri);
                dismissUploadDialog("头像上传成功!");
                break;
            //专业领域
            case RESPONSE_LINGYU:
                FilterList filterList = (FilterList)result;
                typeList = filterList.getData();
                if (typeList != null)
                    for(FilterItem bean:typeList) {
//                        if(bean.getId().equals(this.userInfo.getLingyu_id())) {
//                            bean.setSelect(true);
//                            lingyuText.setText(bean.getName());
//                            break;
//                        }
                    }
                break;
            //位置
            case RESPONSE_WEIZI:
                ProvinceList resultList = (ProvinceList)result;
                provinceList = resultList.getData();
                if (provinceList != null )
                    for(Province bean : provinceList) {
//                        if(bean.getProvince_id().equals(this.userInfo.getProvince_id())) {
//                            bean.setSelect(true);
//                            shenfenText.setText(bean.getName());
//                            break;
//                        }
                    }
                break;
            //机构类型
            case RESPONSE_JIGOU:
                FilterList filterList1 = (FilterList)result;
                orgList = filterList1.getData();
                if(orgList != null)
                    for(FilterItem bean:orgList) {
//                        if(bean.getId().equals(this.userInfo.getJigou_type())) {
//                            bean.setSelect(true);
//                            leixinText.setText(bean.getName());
//                            break;
//                        }
                    }
                break;
            //位置
            case REQUEST_UPDATE_PROVINCE:
                UpdateUserInfoRep proviBean = (UpdateUserInfoRep) result;
//                this.userInfo.setProvince_id(proviBean.getData().getProvince_id());
                GlobalApplication.getInstance().saveUserInfo(this.userInfo);
                ToastUtil.show(mContext,"修改完成");
                break;
            //机构类型
            case REQUEST_UPDATE_ORG:
                UpdateUserInfoRep orgBean = (UpdateUserInfoRep) result;
//                this.userInfo.setJigou_type(orgBean.getData().getJigou_type());
                GlobalApplication.getInstance().saveUserInfo(this.userInfo);
                ToastUtil.show(mContext,"修改完成");
                break;
            //专业领域
            case REQUEST_UPDATE_TYPE:
                UpdateUserInfoRep lingyuBean = (UpdateUserInfoRep) result;
//                this.userInfo.setLingyu_id(lingyuBean.getData().getLingyu_id());
                GlobalApplication.getInstance().saveUserInfo(this.userInfo);
                ToastUtil.show(mContext,"修改完成");
                break;

        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(this,errMsg);
    }
    /**
     * 更新头像
     */
    public void updateGrowthUrl(String growthUrl) {
        this.growthUrl = growthUrl;
        EditUserInfoRequest request = new EditUserInfoRequest();
        request.setHeadsmall(growthUrl);
        request.setUid(userInfo.getMember_id());
        sendConnection("/Appapi/user/edit_info", request, REQUEST_GROWTH_RUL, true, UpdateUserInfoRep.class);
    }
    @OnClick({R.id.userInfoHead,R.id.shenfenLayout,R.id.leixinLayout,R.id.lingyuLayout,R.id.left_layout,R.id.userInfoAgeLayout
            ,R.id.userInfoNameLayout,R.id.userInfoSexLayout,R.id.userInfoPhoneLayout})
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
            //专业领域
            case R.id.lingyuLayout:
                List<String> items = new ArrayList<String>();
                int selectInt = 0;
                if(typeList != null) {
                    for(int i = 0;i < typeList.size(); i ++) {
                        FilterItem bean = typeList.get(i);
                        items.add(bean.getName());
                        if(bean.isSelect()) {
                            selectInt = i;
                        }
                    }
                }
                CustomSelectDialog dataDialog = new CustomSelectDialog(this, items);
                dataDialog.setCurrentItem(selectInt);
                dataDialog.addPickerListener("确定", new PickerClickListener() {
                    @Override
                    public void doPickClick(String currentStr, int currentPosition) {
                        lingyuText.setText(currentStr);
                        for(FilterItem bean:typeList) {
                            bean.setSelect(false);
                        }
                        FilterItem bean = typeList.get(currentPosition);
                        bean.setSelect(true);
                        EditUserInfoRequest request = new EditUserInfoRequest();
                        request.setLingyu_id(bean.getId());
                        request.setUid(userInfo.getMember_id());
                        sendConnection("/Appapi/user/edit_info", request, REQUEST_UPDATE_TYPE, true, UpdateUserInfoRep.class);
                    }
                });
                dataDialog.show();
                break;
            //机构类型
            case R.id.leixinLayout:
                List<String> items1 = new ArrayList<String>();
                int selectInt1 = 0;
                if(orgList != null) {
                    for(int i = 0;i < orgList.size(); i ++) {
                        FilterItem bean = orgList.get(i);
                        items1.add(bean.getName());
                        if(bean.isSelect()) {
                            selectInt1 = i;
                        }
                    }
                }
                CustomSelectDialog dataDialog1 = new CustomSelectDialog(this, items1);
                dataDialog1.setCurrentItem(selectInt1);
                dataDialog1.addPickerListener("确定", new PickerClickListener() {
                    @Override
                    public void doPickClick(String currentStr, int currentPosition) {
                        leixinText.setText(currentStr);
                        for(FilterItem bean:orgList) {
                            bean.setSelect(false);
                        }
                        FilterItem bean = orgList.get(currentPosition);
                        bean.setSelect(true);
                        EditUserInfoRequest request = new EditUserInfoRequest();
                        request.setJigou_type(bean.getId());
                        request.setUid(userInfo.getMember_id());
                        sendConnection("/Appapi/user/edit_info", request, REQUEST_UPDATE_ORG, true, UpdateUserInfoRep.class);
                    }
                });
                dataDialog1.show();
                break;
            //省份
            case R.id.shenfenLayout:
                List<String> items2 = new ArrayList<String>();
                int selectInt2 = 0;
                if(provinceList != null) {
                    for(int i = 0;i < provinceList.size(); i ++) {
                        Province bean = provinceList.get(i);
                        items2.add(bean.getName());
                        if(bean.isSelect()) {
                            selectInt2 = i;
                        }
                    }
                }
                CustomSelectDialog dataDialog2 = new CustomSelectDialog(this, items2);
                dataDialog2.setCurrentItem(selectInt2);
                dataDialog2.addPickerListener("确定", new PickerClickListener() {
                    @Override
                    public void doPickClick(String currentStr, int currentPosition) {
                        shenfenText.setText(currentStr);
                        for(Province bean:provinceList) {
                            bean.setSelect(false);
                        }
                        Province bean = provinceList.get(currentPosition);
                        bean.setSelect(true);
                        EditUserInfoRequest request = new EditUserInfoRequest();
                        request.setProvince_id(bean.getProvince_id());
                        request.setUid(userInfo.getMember_id());
                        sendConnection("/Appapi/user/edit_info", request, REQUEST_UPDATE_PROVINCE, true, UpdateUserInfoRep.class);
                    }
                });
                dataDialog2.show();
                break;
            case R.id.left_layout:
                finish();
                break;
            //年龄
            case R.id.userInfoAgeLayout:
                Intent intentAge = new Intent(this, UpdateUserInfoActivity.class);
                intentAge.putExtra("type",UpdateUserInfoActivity.UPDATE_USERINFO_AGE);
                startActivityForResult(intentAge,REQUEST_UPDATE_AGE);
                break;
            //名字
            case R.id.userInfoNameLayout:
                Intent intentName = new Intent(this, UpdateUserInfoActivity.class);
                intentName.putExtra("type",UpdateUserInfoActivity.UPDATE_USERINFO_NICKNAME);
                startActivityForResult(intentName,REQUEST_UPDATE_NAME);
                break;
            //性别
            case R.id.userInfoSexLayout:
                Intent intentSex = new Intent(this, UpdateSexActivity.class);
//                intentSex.putExtra("sex",userInfo.getSex());
                startActivityForResult(intentSex,REQUEST_UPDATE_SEX);
                break;
            //手机号
            case R.id.userInfoPhoneLayout:
                Intent intentPhone = new Intent(this, UpdateUserInfoActivity.class);
                intentPhone.putExtra("type",UpdateUserInfoActivity.UPDATE_PHONE_NUMBER);
                startActivityForResult(intentPhone,REQUEST_UPDATE_PHONENUMBER);
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
                    getToken();
                    break;
                case REQUEST_UPDATE_NAME:
                    userInfo = GlobalApplication.getInstance().loadUserInfo();
                    userInfoName.setText(userInfo.getNickname());
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
                    userInfoPhone.setText(userInfo.getPhone());
                    break;
            }
        }
    }
    /**
     * 获取token
     */
    public void getToken() {
        getQiniuToken("0", "");
    }
    /**
     * 获取Token
     *
     * @param type 数据类型 //0图片，1视频，2音频，3文档(非指定)，不填默认文件类型如（doc等）
     */
    public void getQiniuToken(String type, String fileName) {
//        QnTokenRequest qnTokenRequest = new QnTokenRequest();
//        qnTokenRequest.setFileName(fileName);
//        qnTokenRequest.setFileType(type);
        this.sendConnection("/Appapi/qiniu/gettoken", REQUEST_CLASS_CIRCLE_TOKEN, true, QnTokenRepEntity.class);
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
}
