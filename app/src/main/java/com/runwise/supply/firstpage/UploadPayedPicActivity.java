package com.runwise.supply.firstpage;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ImageUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.message.entity.ImageFileUrlResult;
import com.runwise.supply.pictakelist.PicTake;
import com.runwise.supply.tools.StatusBarUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by libin on 2017/7/23.
 */

public class UploadPayedPicActivity extends NetWorkActivity {
    //上传图片通用类
    private final static int RET_CAMERA = 101;
    private final static int RET_GALLERY = 102;
    private final static int CROP_CODE = 103;
    private final static int UPLOAD_FILE = 104;
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    private UploadAdapter adapter;
    private List<String> picList = new ArrayList<>();
    private Uri capTempPhotoUrl;
    private Uri updateImgUri;
    private int uploadCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.upload_payed_layout);
        String fileName = UUID.randomUUID()+".jpg";
        capTempPhotoUrl = Uri.fromFile(new File(CommonUtils.getCachePath(mContext),fileName));
        setTitleText(true,"SO123455");
        setTitleLeftIcon(true,R.drawable.nav_back);
        if (picList.size() == 0){
            picList.add("default");
        }
        adapter = new UploadAdapter(mContext,picList);
        GridLayoutManager mgr=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(mgr);
        recyclerView.setAdapter(adapter);
    }
    @OnClick({R.id.title_iv_left,R.id.uploadBtn})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.uploadBtn:
                //发送上传请求
                if (picList.size() == 0){
                    ToastUtil.show(mContext,"请先选择图片");
                }else{
                    uploadAttachmentRequest();
                }
                break;
        }
    }

    private void uploadAttachmentRequest() {
        int orderId = getIntent().getIntExtra("orderid",0);
        if (orderId == 0){
            ToastUtil.show(mContext,"数据异常，请退出重进");
        }else{
            StringBuffer urlSb = new StringBuffer("/gongfu/order/");
            urlSb.append(orderId).append("/attachment/");
            showIProgressDialog();
            for (String path : picList){
                try {
                    List<Part> partList = new ArrayList<>();
                    FilePart fp = new FilePart("attachment_file",new File(path));
                    partList.add(fp);
                    sendConnection(urlSb.toString(),partList,UPLOAD_FILE,false,BaseEntity.ResultBean.class);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case UPLOAD_FILE:
                uploadCount++;
                break;
        }
        if (uploadCount == 3 || (uploadCount < 3 &&uploadCount == picList.size()-1)){
            dismissIProgressDialog();
            ToastUtil.show(mContext,"上传成功");
            finish();
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    public class UploadAdapter extends RecyclerView.Adapter{
        private LayoutInflater inflater;
        //存放本地图片地址，或者网络地址
        private List<String> datas = new ArrayList<>();

        public UploadAdapter(Context context, List<String> datas) {
            this.datas = datas;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.upload_payed_item,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder itemHolder = (ViewHolder) holder;
            String content = datas.get(position);
            itemHolder.deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (picList.size() > position){
                        picList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
            if (content.equals("default")){
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
            }else{
                itemHolder.sdv.setVisibility(View.VISIBLE);
                itemHolder.addIb.setVisibility(View.INVISIBLE);
                itemHolder.deleteIv.setVisibility(View.VISIBLE);
                Uri uri = Uri.fromFile(new File(content));
                FrecoFactory.getInstance(mContext).disPlay(itemHolder.sdv,uri);
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView sdv;
        public ImageView    deleteIv;
        public ImageButton  addIb;
        public ViewHolder(View itemView) {
            super(itemView);
            sdv = (SimpleDraweeView) itemView.findViewById(R.id.sdv);
            deleteIv = (ImageView) itemView.findViewById(R.id.deleteIv);
            addIb = (ImageButton)itemView.findViewById(R.id.addIb);
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
//                    showUploadDialog();
                    String path1Scaled = ImageUtils.getScaledImage(this, updateImgUri.getPath());
                    //在这里改变添加框个数
                    if (picList.size() < 3){
                        picList.add(0,path1Scaled);
                    }else if(picList.size() == 3){
                        picList.add(0,path1Scaled);
                        picList.remove(3);
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String fileName = UUID.randomUUID()+".jpg";
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
