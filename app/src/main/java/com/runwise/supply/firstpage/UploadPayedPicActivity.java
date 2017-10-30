package com.runwise.supply.firstpage;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ImageUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.ImageActivity;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.AttachmentResponse;
import com.runwise.supply.tools.StatusBarUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.runwise.supply.ImageActivity.INTENT_KEY_IMG_URL;

/**
 * Created by libin on 2017/7/23.
 */

public class UploadPayedPicActivity extends NetWorkActivity implements UploadInterface {
    private final static int RET_CAMERA = 101;
    private final static int RET_GALLERY = 102;
    private final static int CROP_CODE = 103;
    private final static int UPLOAD_FILE = 104;
    private static final int ATTACHMENTLIST = 105;
    private static final int ATTACHMENTDELETE = 106;
    private int upLoadCount = 0;                //上传计数，因为传图片是分开传的。只能粗略估计成功与否

    private static final String ADDBUTTON = "ADD";
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    @ViewInject(R.id.uploadBtn)
    private Button upLoadBtn;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    private UploadAdapter adapter;
    private List<String> picList = new ArrayList<>();
    private Uri capTempPhotoUrl;
    private Uri updateImgUri;
    private int uploadCount;
    private int orderId;
    private int currentDelete;      //记录当前要删除的位置
    public static final String INTENT_KEY_CANN_NO_EDIT = "intent_key_cann_no_edit";

    private int indexInt;
    private Handler mHandler = new Handler() {
    };
    private Runnable mRunnalbe = new Runnable() {
        @Override
        public void run() {
            dismissIProgressDialog();
            ToastUtil.show(mContext, "网络超时，可能上传失败，请查看确认");
            Intent intent = new Intent();
            intent.putExtra("has", true);
            setResult(200, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.upload_payed_layout);
        String fileName = UUID.randomUUID() + ".jpg";
        capTempPhotoUrl = Uri.fromFile(new File(CommonUtils.getCachePath(mContext), fileName));
        orderId = getIntent().getIntExtra("orderid", 0);
        String orderName = getIntent().getStringExtra("ordername");
        boolean hasAttachment = getIntent().getBooleanExtra("hasattachment", false);
        setTitleText(true, orderName);
        if (hasAttachment) {
            setTitleRightText(true, "修改");
//            upLoadBtn.setVisibility(View.GONE);
        } else {
            //没有才需要有默认
            if (picList.size() == 0) {
                picList.add(ADDBUTTON);
            }
        }
        setTitleLeftIcon(true, R.drawable.nav_back);
        adapter = new UploadAdapter(mContext);
        adapter.setDatas(picList);
        adapter.setDeleteCallback(this);
        GridLayoutManager mgr = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mgr);
        recyclerView.setAdapter(adapter);
        if (hasAttachment) {
            //发送请求
            getAttachmentList();
        }
        if (getIntent().getBooleanExtra(INTENT_KEY_CANN_NO_EDIT, false)) {
            setTitleRightText(false, "修改");
            showUpLoadBtn(false);
            if (!hasAttachment){
                mLoadingLayout.onSuccess(0,"没有支付凭证");
            }
        }
    }

    public void showUpLoadBtn(boolean isShow) {
        if (getIntent().getBooleanExtra(INTENT_KEY_CANN_NO_EDIT, false)) {
            upLoadBtn.setVisibility(View.INVISIBLE);
        }
        if (isShow) {
            upLoadBtn.setVisibility(View.VISIBLE);
        } else {
            upLoadBtn.setVisibility(View.INVISIBLE);
        }

    }

    private void getAttachmentList() {
        Object request = null;
        StringBuffer urlSb = new StringBuffer("/gongfu/order/");
        urlSb.append(orderId).append("/attachment/list/");
        sendConnection(urlSb.toString(), request, ATTACHMENTLIST, true, AttachmentResponse.class);
    }

    @OnClick({R.id.title_iv_left, R.id.uploadBtn, R.id.title_tv_rigth})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                if (adapter.isHasUnCommit()) {
                    dialog.setMessage("确认取消修改");
                    dialog.setMessageGravity();
                    dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {
                            goLastPage();
                        }
                    });
                    dialog.show();
                } else {
                    goLastPage();
                }
                break;
            case R.id.uploadBtn:
                //发送上传请求
                if (picList.size() == 0) {
                    ToastUtil.show(mContext, "请先选择图片");
                } else {
                    uploadAttachmentRequest();
                }
                break;
            case R.id.title_tv_rigth:
                setTitleRightText(false, "");
                //上传按钮只有提交新图片后才有
//                upLoadBtn.setVisibility(View.VISIBLE);
//                upLoadBtn.setText("确认修改");
                adapter.setModifyMode(true);
                if (picList.size() < 3 && !picList.contains(ADDBUTTON)) {
                    picList.add(ADDBUTTON);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void goLastPage() {
        Intent intent = new Intent();
        intent.putExtra("has", adapter.hasNetPic);
        setResult(200, intent);
        finish();
    }

    private void uploadAttachmentRequest() {
        if (orderId == 0) {
            ToastUtil.show(mContext, "数据异常，请退出重进");
        } else {
            StringBuffer urlSb = new StringBuffer("/gongfu/order/");
            urlSb.append(orderId).append("/attachment/");
            showIProgressDialog();
            //如果集合中没有本地图片，则退出
            boolean hasNewAddPic = false;
            for (String path : picList) {
                if (!path.contains(Constant.BASE_URL) && !path.contains(ADDBUTTON)) {
                    hasNewAddPic = true;
                }
            }
            if (!hasNewAddPic) {
                dismissIProgressDialog();
                ToastUtil.show(mContext, "请添加新图片");
                return;
            }
            for (String path : picList) {
                if (path.contains(Constant.BASE_URL) || path.contains(ADDBUTTON)) {
                    continue;
                }
                try {
                    List<Part> partList = new ArrayList<>();
                    FilePart fp = new FilePart("attachment_file", new File(path));
                    partList.add(fp);
                    LogUtils.e("上传支付凭证路径：" + path);
                    sendConnection(urlSb.toString(), partList, UPLOAD_FILE, false, BaseEntity.ResultBean.class);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case UPLOAD_FILE:
                //@libin，因为上传接口暂支持单张，只能简单用次数统计。这里加个延时，如果超过5秒，则认为失败，提示用户
                uploadCount++;
                if (uploadCount == 1 && uploadCount < adapter.getLocalPic()) {
                    mHandler.postDelayed(mRunnalbe, 5000);
                }
                if (uploadCount == adapter.getLocalPic()) {
                    mHandler.removeCallbacks(mRunnalbe);
                    dismissIProgressDialog();
                    ToastUtil.show(mContext, "上传成功");
                    Intent intent = new Intent();
                    intent.putExtra("has", true);
                    intent.putExtra("upload_success",true);
                    setResult(200, intent);
                    finish();
                }
                break;
            case ATTACHMENTLIST:
                StringBuffer url = new StringBuffer(Constant.BASE_URL);
                url.append("/web/content/");
                BaseEntity.ResultBean rb = result.getResult();
                AttachmentResponse ar = (AttachmentResponse) rb.getData();
                if (ar.getAttachments() != null && ar.getAttachments().size() > 0) {
                    for (Integer i : ar.getAttachments()) {
                        String urlString = url.toString() + i;
                        picList.add(urlString);
                    }
                }
                if (picList.size() < 3) {
                    picList.add(ADDBUTTON);
                }
                if (getIntent().getBooleanExtra(INTENT_KEY_CANN_NO_EDIT, false)) {
                    picList.remove(ADDBUTTON);
                }
                adapter.setDatas(picList);
                return;
            case ATTACHMENTDELETE:
                //删除成功了，从本地集合中删除
                ToastUtil.show(mContext, "删除成功");
                picList.remove(currentDelete);
                if (picList.size() < 3 && !picList.contains(ADDBUTTON)) {
                    picList.add(ADDBUTTON);
                }
                adapter.setDatas(picList);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void deleteClick(final int position) {
        if (picList.size() > position) {
            //本地图片随便删
            //删除请求
            dialog.setMessage("确认删除图片");
            dialog.setMessageGravity();
            dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                @Override
                public void doClickButton(Button btn, CustomDialog dialog) {
                    indexInt--;
                    if (picList.get(position).contains(Constant.BASE_URL)) {
                        currentDelete = position;
                        deletePicRequest(position);
                    } else {
                        //本地的随便删除
                        picList.remove(position);
                        adapter.setDatas(picList);
                    }
                    //如果没有本地图片，隐藏按钮
                    boolean hasLocalPic = false;
                    for (String path : picList) {
                        if (!path.contains(Constant.BASE_URL)) {
                            hasLocalPic = true;
                            break;
                        }
                    }
                    if (!hasLocalPic) {
                        showUpLoadBtn(false);
                    }

                }
            });
            dialog.show();


        }
    }

    private void deletePicRequest(int position) {
        String attachmentUrl = picList.get(position);
        int index = attachmentUrl.lastIndexOf("/");
        String attachmentId = attachmentUrl.substring(index);
        StringBuffer sb = new StringBuffer("/gongfu/attachment");
        sb.append(attachmentId).append("/delete/");
        Object request = null;
        sendConnection(sb.toString(), request, ATTACHMENTDELETE, true, BaseEntity.ResultBean.class);
    }

    public class UploadAdapter extends RecyclerView.Adapter {
        private UploadInterface deleteCallback;
        private LayoutInflater inflater;

        public void setDeleteCallback(UploadInterface deleteCallback) {
            this.deleteCallback = deleteCallback;
        }

        //存放本地图片地址，或者网络地址
        private List<String> datas = new ArrayList<>();
        private boolean isModifyMode = false;        //默认不在修改模式下
        private boolean hasUnCommit = false;        //有末提交的文件
        private boolean hasNetPic = false;          //有存在的图片

        public boolean isHasUnCommit() {
            return hasUnCommit;
        }


        public void setModifyMode(boolean modifyMode) {
            isModifyMode = modifyMode;
        }

        public boolean isModifyMode() {
            return isModifyMode;
        }

        public UploadAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setDatas(List<String> datas) {
            this.datas.clear();
            if (datas != null) {
                this.datas.addAll(datas);
            }
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.upload_payed_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder itemHolder = (ViewHolder) holder;
            final String content = datas.get(position);
            itemHolder.deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteCallback != null) {
                        deleteCallback.deleteClick(position);
                    }
                }
            });
            if (position == 0) {
                hasUnCommit = false;
                hasNetPic = false;
            }
            if (content.equals(ADDBUTTON)) {
//                Uri imageUri = CommonUtils.getUriFromDrawableRes(mContext,R.drawable.icon_payorder);
                itemHolder.addIb.setImageResource(R.drawable.icon_payorder);
                itemHolder.deleteIv.setVisibility(View.INVISIBLE);
                itemHolder.sdv.setVisibility(View.INVISIBLE);
                itemHolder.addIb.setVisibility(View.VISIBLE);
                itemHolder.addIb.setOnClickListener(new View.OnClickListener() {
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
                    }
                });
            } else if (content.contains(Constant.BASE_URL)) {
                hasNetPic = true;
                itemHolder.sdv.setVisibility(View.VISIBLE);
                if (isModifyMode) {
                    itemHolder.deleteIv.setVisibility(View.VISIBLE);
                    itemHolder.addIb.setVisibility(View.INVISIBLE);
                } else {
                    itemHolder.deleteIv.setVisibility(View.INVISIBLE);
                    itemHolder.addIb.setVisibility(View.INVISIBLE);
                }

                FrecoFactory.getInstance(mContext).disPlay(itemHolder.sdv, content);

                itemHolder.sdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UploadPayedPicActivity.this, ImageActivity.class);
                        intent.putExtra(INTENT_KEY_IMG_URL,content);
                        startActivity(intent);
                    }
                });
            } else {
                itemHolder.sdv.setVisibility(View.VISIBLE);
                itemHolder.addIb.setVisibility(View.INVISIBLE);
                itemHolder.deleteIv.setVisibility(View.VISIBLE);
                final Uri uri = Uri.fromFile(new File(content));
                FrecoFactory.getInstance(mContext).disPlay(itemHolder.sdv, uri);
                //有没有需要提交的，肯定只有adapter知道，自已数据啥样
                hasUnCommit = true;
                //只有添加了本地图片，才用上传
                showUpLoadBtn(true);

                itemHolder.sdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UploadPayedPicActivity.this, ImageActivity.class);
                        intent.putExtra(INTENT_KEY_IMG_URL,uri.toString());
                        startActivity(intent);
                    }
                });
            }


        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        //含有本地图片的张数
        public int getLocalPic() {
            int localCount = 0;
            for (String path : datas) {
                if (!path.contains(Constant.BASE_URL) && !path.equals(ADDBUTTON)) {
                    localCount++;
                }
            }
            return localCount;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView sdv;
        public ImageView deleteIv;
        public ImageButton addIb;

        public ViewHolder(View itemView) {
            super(itemView);
            sdv = (SimpleDraweeView) itemView.findViewById(R.id.sdv);
            deleteIv = (ImageView) itemView.findViewById(R.id.deleteIv);
            addIb = (ImageButton) itemView.findViewById(R.id.addIb);
        }
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
//                    showUploadDialog();
                    String path1Scaled = ImageUtils.getScaledImage(this, updateImgUri.getPath());
                    try {
                        indexInt = indexInt + 1;
                        File localFile = new File(mContext.getFilesDir().getPath(), "支付凭证(" + indexInt + ").jpg");
                        InputStream in = new FileInputStream(path1Scaled);
                        ImageUtils.writeToFile(localFile, in);
                        path1Scaled = localFile.getAbsolutePath();
                        //因为重复用了文件名，所以要清fresco缓存
                        Fresco.getImagePipeline().evictFromCache(Uri.fromFile(new File(path1Scaled)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //在这里改变添加框个数
                    if (picList.size() < 3) {
                        picList.add(0, path1Scaled);
                    } else if (picList.size() == 3) {
                        picList.add(0, path1Scaled);
                        picList.remove(3);
                    }
                    break;
            }
            adapter.setDatas(picList);
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

    private void startCropActivity(@NonNull Uri uri) {
        String fileName = UUID.randomUUID() + ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(CommonUtils.getCachePath(mContext), fileName)));
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
}
