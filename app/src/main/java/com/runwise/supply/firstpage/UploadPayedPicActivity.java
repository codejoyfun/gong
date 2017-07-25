package com.runwise.supply.firstpage;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomBottomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.message.entity.ImageFileUrlResult;
import com.runwise.supply.pictakelist.PicTake;
import com.runwise.supply.tools.StatusBarUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by libin on 2017/7/23.
 */

public class UploadPayedPicActivity extends NetWorkActivity {
    //上传图片通用类
    private final int RET_CAMERA = 101;
    private final int RET_GALLERY = 102;
    private final int WHAT_MUIN = 5;
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    private UploadAdapter adapter;
    private List<String> picList = new ArrayList<>();
    private int what;
    private List<PicTake> uploadList = new ArrayList<>();//上传集合
    private int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.upload_payed_layout);
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
    @OnClick({R.id.title_iv_left})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
        }
    }
    @Override
    public void onSuccess(BaseEntity result, int where) {

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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder itemHolder = (ViewHolder) holder;
            String content = datas.get(position);
            if (content.equals("default")){
                Uri imageUri = CommonUtils.getUriFromDrawableRes(mContext,R.drawable.icon_payorder);
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
                        what = WHAT_MUIN;
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
                    }
                    if (!picList.contains(filePath) && picList.size() <= 3){
                        picList.add(0,filePath);
                    }
                    break;
                // 相册回调
                case RET_GALLERY:
                    if( what == WHAT_MUIN ) {
                        filePath = CommonUtils.getImageAbsolutePath(this, data.getData());
                        PicTake picTake = getUploadPic(currentPosition);
                        picTake.setPicPath(filePath);
//                        picTakeAdapter.setData(uploadList);
                    }
                    else {
                        filePath = CommonUtils.getImageAbsolutePath(this, data.getData());
//						globalTextView.setText(filePath);
//                        FrecoFactory.getInstance(mContext).disPlay(globalDraweeView, Uri.fromFile(new File(ImageUtils.getScaledImage(this,filePath))));
                    }
//                    currentPath = filePath;
                    if (!picList.contains(filePath) && picList.size() <= 3){
                        picList.add(0,filePath);
                    }
                    break;
            }
            //更新列表
            adapter.notifyDataSetChanged();
        }
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
}
