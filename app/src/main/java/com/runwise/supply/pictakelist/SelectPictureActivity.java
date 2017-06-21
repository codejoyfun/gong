package com.runwise.supply.pictakelist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ImageUtils;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.SlidingLayer;
import com.kids.commonframe.config.Constant;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.tools.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class SelectPictureActivity extends BaseActivity implements OnItemClickListener {
    private PinnedRecyclerAdapter adapter;
    public static final int REQUEST_CODE_CAMERA = 8;
    public static final int REQUEST_CODE_LOCAL = 9;
    public static final int REQUEST_LOOK_VIEW = 10;
    public static final int REQUEST_USED_PIC = 11;

    private PicTake currentCammer;
    private ArrayList<PicTake> imageList;
    private List<PicFloder> floderList;
    @ViewInject(R.id.sliderRow)
    private ImageView sliderRow;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            setItemDecoration();
            folderAdapter.setData(floderList);
            setTakePicSelect();
            setSelectToLable();
            SelectPictureActivity.this.dismissIProgressDialog();
        }
    };

    public void setItemDecoration(){
        if (styleType == Constant.STYLE_TIMELINE) {
            if(itemDecoration != null){
                pictakeGv.removeItemDecoration(itemDecoration);
            }
            PinnedHeaderItemDecoration.Builder<String> itemDecorationBuilder = new PinnedHeaderItemDecoration.Builder<String>();
            itemDecoration = itemDecorationBuilder.create();
            pictakeGv.addItemDecoration(itemDecoration);
        }
    }

    @ViewInject(R.id.pictakeGv)
    private RecyclerView pictakeGv;
    //-------弹出层--------
    @ViewInject(R.id.slidingLayer)
    private SlidingLayer mSlidingLayer;
    @ViewInject(R.id.bgView)
    private View bgView;
    @ViewInject(R.id.popDirListView)
    private ListView popDirListView;
    @ViewInject(R.id.dirPath)
    private TextView dirPath;
    private PicFloderAdapter folderAdapter;

    private static SelectPictureActivity instance;
    @ViewInject(R.id.doFinish)
    private LinearLayout finishBtn;
    private List<PicTake> currentPicList;

    PinnedHeaderItemDecoration<String> itemDecoration = null;
    private int styleType = Constant.STYLE_CAMERA;

    private static Activity targetActivity = null;
    private static Class<?> cls= null ;
    private int extraCount = 0;

    private boolean openCam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        currentPicList = (ArrayList<PicTake>) this.getIntent().getSerializableExtra("selectList");
        extraCount = getIntent().getIntExtra("extracount",0);
        this.setContentView(R.layout.pic_take_activity);
        this.setTitleText(true, "选择图片");
        this.setTitleLeftIcon(true, R.drawable.back_btn);
        this.showIProgressDialog();
        imageList = new ArrayList<PicTake>();
        floderList = new ArrayList<PicFloder>();

        if(getIntent()!= null){
            styleType = getIntent().getIntExtra("albumstyle",Constant.STYLE_CAMERA);
        }
        adapter = new com.runwise.supply.pictakelist.PinnedRecyclerAdapter(this, styleType);
        pictakeGv.setLayoutManager(new GridLayoutManager(this, 3));
        if (styleType == Constant.STYLE_TIMELINE) {
            PinnedHeaderItemDecoration.Builder<String> itemDecorationBuilder = new PinnedHeaderItemDecoration.Builder<String>();
            itemDecoration = itemDecorationBuilder.create();
        }
        pictakeGv.setAdapter(adapter);

        folderAdapter = new PicFloderAdapter();
        popDirListView.setAdapter(folderAdapter);
        popDirListView.setOnItemClickListener(this);
        loadImages();
        mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
        mSlidingLayer.setCloseOnTapEnabled(true);
        mSlidingLayer.setSlidingEnabled(false);
        mSlidingLayer.getLayoutParams().height = GlobalConstant.screenH * 1 / 2;
        mSlidingLayer.setOffsetWidth(0);
        mSlidingLayer.closeLayer(true);
        mSlidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpened() {
            }

            @Override
            public void onClosed() {
            }

            @Override
            public void onOpen() {
                bgView.setVisibility(View.VISIBLE);
                sliderRow.setImageResource(R.drawable.dd_up);
            }

            @Override
            public void onClose() {
                bgView.setVisibility(View.GONE);
                sliderRow.setImageResource(R.drawable.dd_down);
            }
        });
        bgView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSlidingLayer.isOpened()) {
                    mSlidingLayer.closeLayer(true);
                }
                return true;
            }
        });
        openCam = getIntent().getBooleanExtra("openCam",false);
        if (openCam) {
            selectPicFromCamera();
        }
    }

    public static SelectPictureActivity getInstance() {
        return instance;
    }

    private void setTakePicSelect() {
        if (currentPicList != null) {
            for (int i = 0; i < currentPicList.size(); i++) {
                for (int j = 0; j < imageList.size(); j++) {
                    if (imageList.get(j).equals(currentPicList.get(i))) {
                        imageList.get(j).setSelect(true);
                        break;
                    }
                }
            }
        }
    }

    //返回选中的图片
    public List<PicTake> getSelectList() {
        List<PicTake> tempList = new ArrayList<PicTake>();
        int i = 0;
        for (PicTake temp : imageList) {
            if (temp.isSelect()) {
                temp.setPosition(i);
                temp.setType(PicTake.PIC);
                setWithOrHeight(temp);
                tempList.add(temp);
            }
            i++;
        }
        return tempList;
    }

    public int getSelectPic() {
        int count = 0;
        for (PicTake bean : imageList) {
            if (bean.isSelect() && !bean.isTakePic()) {
                count++;
            }
        }
        return count + getSelectCammerPic() + extraCount;
    }

    public int getSelectCammerPic() {
        int count = 0;
        if (currentPicList != null) {
            for (PicTake bean : currentPicList) {
                if (bean.isTakePic()) {
                    count++;
                }
            }
        }
        return count;
    }

    private void loadImages() {
        if (!isExitsSdcard()) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = SelectPictureActivity.this.getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Video.Media.DATE_ADDED + " desc");
                imageList.clear();
                floderList.clear();
                Map<String, List<PicTake>> filesMap = new HashMap<String, List<PicTake>>();
                try {
                    if(mCursor != null){
                        while (mCursor.moveToNext()) {
                            // 获取图片的路径
                            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            String imageId = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                            String time = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                            File parentFile = new File(path).getParentFile();
                            String parentPath = parentFile.getAbsolutePath();
                            if (filesMap.get(parentPath) == null) {
                                List<PicTake> imagelist = new ArrayList<PicTake>();
                                filesMap.put(parentPath, imagelist);
                            }
                            PicTake picTake = new PicTake();
                            Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                            picTake.setUrl(imageUri.toString());
                            picTake.setPicPath(path);
                            picTake.setCreateTime(time);
                            picTake.setPinnedType(Constant.TYPE_DATA);
                            filesMap.get(parentPath).add(picTake);
                        }
                    }
                } finally {
                    if (mCursor != null) {
                        mCursor.close();
                    }
                }
                Set<String> setList = filesMap.keySet();
                for (String key : setList) {
                    File file = new File(key);
                    List<PicTake> picList = filesMap.get(key);
                    String firstImageUri = "";
                    if (!picList.isEmpty()) {
                        firstImageUri = picList.get(0).getUrl();
                    }
                    PicFloder floder = new PicFloder(file.getName(), file.getAbsolutePath(), firstImageUri);
                    floder.setPicList(picList);
                    floder.setPicCount(picList.size());
                    imageList.addAll(picList);
                    floderList.add(floder);
                }
                if (!imageList.isEmpty()) {
                    PicFloder floder = new PicFloder("/所有图片", "", imageList.get(0).getUrl());
                    floder.setPicCount(imageList.size());
                    floder.setPicList(imageList);
                    floder.setSelect(true);
                    floderList.add(0, floder);
                }
                //
                if (adapter != null) {
                    adapter.setData(imageList);
                    if (styleType == Constant.STYLE_TIMELINE) {
                        sortImageList(adapter.getData());
                    }
                }
                Message msg = mHandler.obtainMessage();
                msg.sendToTarget();
            }
        }).start();
    }

    public void sortImageList(ArrayList<PicTake> imageList) {
        Collections.sort(imageList, new YMComparator());
        List<PicTake> picTakeList = (List<PicTake>) imageList.clone();
        String upTime = "";
        int space = 0;
        int section = 0;
        for (ListIterator<PicTake> it = picTakeList.listIterator(); it.hasNext(); ) {
            PicTake item = it.next();
            String ym = item.getCreateTime();
            String formatTime = TimeUtils.getTimeStamps2(Long.parseLong(ym) * 1000);

            if (!upTime.equals(formatTime)) {
                PicTake picTake = new PicTake();
                picTake.setPinnedTitle(formatTime);
                picTake.setPinnedType(Constant.TYPE_SECTION);
                imageList.add(space, picTake);
                space++;
                section++;
            }
            item.setPinnedTitle(formatTime);
            item.setSection(section);
            upTime = formatTime;
            space++;
        }
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    @OnClick(R.id.chooseDir)
    public void chooseDir(View view) {
        if (!mSlidingLayer.isOpened()) {
            mSlidingLayer.openLayer(true);
        } else {
            mSlidingLayer.closeLayer(true);
        }
    }


    public void setSelectToLable() {
        this.setTitleRightText(true, "完成(" + getSelectPic() + "/" + Constant.PIC_MAX_COUNT + ")");
    }

    /**
     * 选择适配器
     */
    private class PicFloderAdapter extends com.kids.commonframe.base.IBaseAdapter<PicFloder> {
        private int itemHeight;
        private int prePosition;
        private ResizeOptions imageSize;

        public PicFloderAdapter() {
            int spacing = (int) SelectPictureActivity.this.getResources().getDimension(R.dimen.verticalSpacing);
            itemHeight = (GlobalConstant.screenW - spacing * 3) / 4;
            imageSize = new ResizeOptions(itemHeight / 3, itemHeight / 3);
        }

        /**
         * 设置选中状态
         *
         * @param position
         */
        public void setSelect(int position) {
            if (mList != null && !mList.isEmpty()) {
                mList.get(prePosition).setSelect(false);
                mList.get(position).setSelect(true);
            }
            prePosition = position;
            this.notifyDataSetChanged();
        }

        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectPictureActivity.this).inflate(R.layout.list_dir_item, null);
                holder = new ViewHolder();
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
                Drawable defaultDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.pv_wait_color));
                FrecoFactory.getInstance(mContext).setDefaultHierarchy(holder.picFace, defaultDrawable, ScalingUtils.ScaleType.FOCUS_CROP);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final PicFloder bean = mList.get(position);
            Uri uri = Uri.parse(bean.getImageFace());
            FrecoFactory.getInstance(mContext).disPlay(holder.picFace, uri, imageSize);
            holder.dirPath.setText(bean.getShowName());
            holder.picCount.setText(bean.getPicCount() + "张");
            if (bean.isSelect()) {
                holder.selectFlag.setVisibility(View.VISIBLE);
            } else {
                holder.selectFlag.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.id_dir_item_image)
            SimpleDraweeView picFace;
            @ViewInject(R.id.id_dir_item_name)
            TextView dirPath;
            @ViewInject(R.id.id_dir_item_count)
            TextView picCount;
            @ViewInject(R.id.selectFlag)
            ImageView selectFlag;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        folderAdapter.setSelect(position);
        PicFloder picFloder = floderList.get(position);
        adapter.setData(picFloder.getPicList());
        if (styleType == Constant.STYLE_TIMELINE) {
            sortImageList(adapter.getData());
        }
        adapter.notifyDataSetChanged();
        setItemDecoration();
        dirPath.setText(picFloder.getShowName());
        mSlidingLayer.closeLayer(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("infosss", "onDestory..........");
        targetActivity = null;
        cls = null;
//		Fresco.shutDown();
    }

    /*点击完成*/
    @OnClick(R.id.right_layout)
    public void doFinish(View view) {
        clickOnFinish();
    }

    public void clickOnFinish() {
        if (getSelectPic() == 0) {
            if(dialog == null){
                dialog = new CustomDialog( mContext );
            }
            dialog.setModel(CustomDialog.LEFT);
            dialog.setMessage("请选择照片");
            dialog.setLeftBtnListener("确定",null);
            if(!isFinishing()){
                dialog.show();
            }
            return;
        }
        if (targetActivity == null) {
            Intent intent = new Intent(this, cls);
            intent.putExtra("selectpictype", Constant.PIC_SELECT_TYPE_ALBUM);
            intent.putExtra(Constant.ALBUM_PICTAKES, (ArrayList<PicTake>) this.getSelectList());
            this.startActivity(intent);
        } else {
            Intent dataIntent = new Intent();
            dataIntent.putExtra(Constant.ALBUM_PICTAKES, (ArrayList<PicTake>) getSelectList());
            this.setResult(Activity.RESULT_OK, dataIntent);
        }
        this.finish();
    }

    //打开相机
    public void selectPicFromCamera() {
        if (!isExitsSdcard()) {
            Toast.makeText(mContext, "SD卡不存在，不能拍照", Toast.LENGTH_LONG).show();
            return;
        }
        currentCammer = new PicTake();
        currentCammer.setIsTakePic(true);
        String randomName = UUID.randomUUID() + ".jpg";
        currentCammer.setRandomName(randomName);
        currentCammer.setType(PicTake.PIC);
        currentCammer.setSelect(true);
        String imagePath = CommonUtils.getImagePath(this);
        File cammerFile = new File(imagePath, randomName);
        Date date = new Date();
        Long time = date.getTime();
        currentCammer.setCreateTime(String.valueOf(time/1000));
        currentCammer.setPinnedTitle(TimeUtils.getTimeStamps2(time));
        currentCammer.setPicPath(cammerFile.getAbsolutePath());
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cammerFile)),
                REQUEST_CODE_CAMERA);
    }

    //浏览
    @OnClick(R.id.doFinish)
    public void titleLook(View view) {
        int select = getSelectList().size();
        if (select == 0) {
            if(dialog == null){
                dialog = new CustomDialog( mContext );
            }
            dialog.setModel(CustomDialog.LEFT);
            dialog.setMessage("请选择照片");
            dialog.setLeftBtnListener("确定", null);
            if(!isFinishing()){
                dialog.show();
            }
            return;
        }
        Intent intent = new Intent(this, LookPictureActivity.class);
        intent.putExtra(Constant.ALBUM_PICTAKES, (ArrayList<PicTake>) this.getSelectList());
        this.startActivityForResult(intent, REQUEST_LOOK_VIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //照相鸡
                case REQUEST_CODE_CAMERA:
                    File cammerFile = new File(currentCammer.getPicPath());
                    ImageUtils.getScaledImage(this, cammerFile);
                    Intent intent = new Intent(this, ShowPictureActivity.class);
                    intent.putExtra("capturePath", currentCammer);
                    this.startActivityForResult(intent, REQUEST_USED_PIC);
                    break;
                // 发送照片
                case REQUEST_USED_PIC:
                    PicTake bean = (PicTake) data.getSerializableExtra("capturePath");
                    bean.setUrl(Uri.fromFile(new File(bean.getPicPath())).toString());
                    setWithOrHeight(bean);
                    bean.setType(PicTake.PIC);
                    if (targetActivity == null && cls != null) {
                        Intent camearIntent = new Intent(this, cls);
                        camearIntent.putExtra(Constant.ALBUM_PICTAKE, bean);
                        this.startActivity(camearIntent);
                    } else {
                        Intent dataIntent = new Intent();
                        dataIntent.putExtra(Constant.ALBUM_PICTAKE, bean);
                        this.setResult(Activity.RESULT_OK, dataIntent);
                    }
                    this.finish();
                    break;
                case REQUEST_LOOK_VIEW:
                    if (getSelectPic() == 0) {
                        adapter.notifyDataSetChanged();
                    }
                    clickOnFinish();
                    break;
            }
        }
        else if (requestCode == REQUEST_LOOK_VIEW) {
            setSelectToLable();
            adapter.notifyDataSetChanged();
        }
        else {
            if (openCam) {
                finish();
            }
        }
    }

    public void setWithOrHeight(PicTake picTake) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picTake.getPicPath(), options);
        int j = ImageUtils.readPictureDegree(picTake.getPicPath()); //获取旋转角度
        if (j != 0) {  //需要旋转
            picTake.setWidth(options.outHeight);
            picTake.setHeight(options.outWidth);
        } else {
            picTake.setWidth(options.outWidth);
            picTake.setHeight(options.outHeight);
        }
    }

    public List<PicTake> getPicLists() {
        return adapter.getList();
    }

    public static void setHandleActivity(Activity targetAc,Class<?> cl){
        targetActivity = targetAc;
        cls = cl;
    }
}