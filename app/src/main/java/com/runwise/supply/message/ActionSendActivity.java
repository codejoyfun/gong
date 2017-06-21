package com.runwise.supply.message;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.UserInfo;
import com.runwise.supply.pictakelist.PicTake;
import com.runwise.supply.pictakelist.ReChooseImageActivity;
import com.runwise.supply.pictakelist.SelectPictureActivity;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.UserUtils;
import com.runwise.supply.view.MyGridView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 意见反馈
 */
public class ActionSendActivity extends NetWorkActivity {
    public static final String TAG = ActionSendActivity.class.getSimpleName();
    private static ActionSendActivity classCircleSendAct = null;
    public static final int DELETE_IAMGE = 6;
    private UserInfo userInfo;

    private String content = null;
    private EditText filterEditText;
    private MyGridView sendGridView;

    private StringBuffer sbStudId = new StringBuffer();
    private StringBuffer sbClassId = new StringBuffer();

    @ViewInject(R.id.title_tv_rigth)
    private TextView tvRight;

    public int picTakeType = -1;
    private PicTakeAdapter picTakeAdapter = null;

    @ViewInject(R.id.contextTitle)
    private EditText contextTitle;

    @ViewInject(R.id.contextContact)
    private EditText contextContact;
//------------------------发送需要----------------------
    private List<PicTake> uploadList = null;//上传集合
    public static ActionSendActivity getInstance() {
        return classCircleSendAct;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classCircleSendAct = this;
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.action_send_layout);
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        initData();
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @OnClick({R.id.left_layout})
    public void doBtnHandler(View view) {
        switch (view.getId()) {
            case R.id.left_layout:
                onBack();
                break;
        }
    }

    public void onBack(){
        content = filterEditText.getText().toString().trim();
        if(uploadList.size() != 0 || !TextUtils.isEmpty(content)){
            if(dialog == null){
                dialog = new CustomDialog(this);
            }
            dialog.setTitle("提示");
            dialog.setMessage("退出此次编辑？");
            dialog.setModel(CustomDialog.BOTH);
            dialog.setLeftBtnListener("取消", null);
            dialog.setRightBtnListener("退出", new CustomDialog.DialogListener() {
                @Override
                public void doClickButton(Button btn, CustomDialog dialog) {
                    ActionSendActivity.this.finish();
                }
            });
            if(!isFinishing()){
                dialog.show();
            }
        }else {
            this.finish();
        }
    }

    public void initData() {
        Intent intent = this.getIntent();
        uploadList = (List<PicTake>) intent.getSerializableExtra(Constant.ALBUM_PICTAKES);
        if (uploadList == null) {
            this.uploadList = new ArrayList<PicTake>();
        }
        PicTake cammerTake = (PicTake) intent.getSerializableExtra(Constant.ALBUM_PICTAKE);
        if (cammerTake != null) {
            this.uploadList.add(cammerTake);
        }
        //资源类型
        if (uploadList.size() > 0) {
            picTakeType = uploadList.get(0).getType();
        }
        LogUtil.e(TAG, "发送类型：" + picTakeType);
    }

    private void initView() {
        this.setTitleText(true,"意见反馈");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        this.setTitleRightText(true, "提交");
        initTopView();
    }



    private void initTopView() {
        filterEditText = (EditText) findViewById(R.id.classcircle_send_content);
        sendGridView = (MyGridView) findViewById(R.id.classcircle_send_gridview);
        filterEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        picTakeAdapter = new PicTakeAdapter(picTakeType);
        sendGridView.setAdapter(picTakeAdapter);
        sendGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        sendGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PicTake picTake = (PicTake) picTakeAdapter.getItem(position);
//                if (picTakeType == PicTake.PIC) {
                    // 添加
                    if (picTake.getType() == 0) {
                        Intent intent = new Intent(mContext, SelectPictureActivity.class);
                        SelectPictureActivity.setHandleActivity(ActionSendActivity.this,ActionSendActivity.class);
                        intent.putExtra("selectList", (ArrayList<PicTake>) picTakeAdapter.getList());
                        ActionSendActivity.this.startActivityForResult(intent, SelectPictureActivity.REQUEST_CODE_LOCAL);
                    } else {
                        Intent first = new Intent(ActionSendActivity.this, ReChooseImageActivity.class);
                        first.putExtra("bean", (ArrayList<PicTake>) uploadList);
                        first.putExtra("index", position);
                        ActionSendActivity.this.startActivityForResult(first, DELETE_IAMGE);
                    }
//                }
            }
        });
        picTakeAdapter.setData(uploadList);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext, errMsg);
    }

    @Override
    protected void onDestroy() {
        classCircleSendAct = null;
        super.onDestroy();
    }

    /**
     * 上传文件 适配器
     */
    private class PicTakeAdapter extends IBaseAdapter<PicTake> {

        int MAX_COUNT = 0;//允许添加的最大个数
        private int itemHeight;
        private LayoutInflater inflater;
        private ResizeOptions imageSize;
        private int picTakeType;

        public PicTakeAdapter(int picTakeType) {
            int spacing = (int) ActionSendActivity.this.getResources().getDimension(R.dimen.verticalSpacing);
            itemHeight = (GlobalConstant.screenW - spacing * 4) / 4;
            inflater = LayoutInflater.from(ActionSendActivity.this);
            this.imageSize = new ResizeOptions(itemHeight / 2, itemHeight / 2);
            this.picTakeType = picTakeType;
        }

        @Override
        public void setData(List<PicTake> list) {
            if (list == null) {
                return;
            }
            mList.clear();
            mList.addAll(list);

            MAX_COUNT = Constant.PIC_MAX_COUNT;

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
                holder.addImg.getLayoutParams().height = itemHeight;
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
                Uri uri = Uri.parse(bean.getUrl());
                FrecoFactory.getInstance(mContext).disPlay(holder.addImg, uri, imageSize);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 发送相册图片
                case SelectPictureActivity.REQUEST_CODE_LOCAL:
                    if (data != null) {
                        Object resultList = data.getSerializableExtra(Constant.ALBUM_PICTAKES); //集合
                        PicTake result = (PicTake) data.getSerializableExtra(Constant.ALBUM_PICTAKE);//使用相机 照片、video
                        if (resultList != null) {
                            List<PicTake> list = (List<PicTake>) resultList;
                            if (list != null) {
                                // 排重复
                                Iterator<PicTake> it = uploadList.iterator();
                                while (it.hasNext()) {
                                    PicTake uploadBean = it.next();
                                    if (uploadBean.isTakePic()) {
                                        continue;
                                    }
                                    if (list.contains(uploadBean)) {
                                        list.remove(uploadBean);
                                    } else {
                                        it.remove();
                                    }
                                }
                                uploadList.addAll(list);
                                picTakeAdapter.setData(uploadList);
                            }
                        }
                        if (result != null) {
                            uploadList.add(result);
                            picTakeAdapter.setData(uploadList);
                        }
                    }
                    break;
                case DELETE_IAMGE: // 浏览删除
                    if (data != null) {
                        List<PicTake> pathTemp = (ArrayList<PicTake>) data
                                .getSerializableExtra("bean");
                        uploadList.clear();
                        uploadList.addAll(pathTemp);
                        picTakeAdapter.setData(uploadList);
                    }
                    break;
            }
        }
    }


    /**
     * 是否可以发送
     *
     * @return
     */
    public boolean onChangeCheck() {
        if (uploadList.size() == 0) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.right_layout)
    public void onSubmit(View view) {
        content = getFetContent();
        String title = contextTitle.getText().toString();
        String contact = contextContact.getText().toString();
        if(TextUtils.isEmpty(title)) {
            ToastUtil.show(mContext,"请输入标题");
        }
        if (TextUtils.isEmpty(content) && uploadList.isEmpty()) {
            ToastUtil.show(mContext,"请输入发送内容");
        }
        if(TextUtils.isEmpty(contact)) {
            ToastUtil.show(mContext,"请输入联系方式");
        }
        else {
            if (UserUtils.checkLogin(null,this)) {
                ActionRecordEditRequest addRequest = new ActionRecordEditRequest(this);
                addRequest.addGrowthRecord(title,getFetContent(),contact,uploadList, new ActionRecordEditRequest.ActionRecordRequestListenter() {
                    @Override
                    public void onRequestStart() {
                        showIProgressDialog();
                    }

                    @Override
                    public void onRequestSuccess(BaseEntity result) {
                        dismissIProgressDialog();
                        dialog.setModel(CustomDialog.LEFT);
                        dialog.setCancelable(false);
                        dialog.setTitle("感谢您的反馈");
                        dialog.setMessage("我们将尽快为您解决,请您耐心等待");
                        dialog.setMessageGravity();
                        dialog.setLeftBtnListener("确定", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                finish();
                            }
                        });
                        dialog.show();
                    }
                    @Override
                    public void onRequestFail() {
                        dismissIProgressDialog();
                    }
                });
            }
        }
    }


    public String getFetContent(){
        if(filterEditText != null){
            String contextText = filterEditText.getText().toString();
            return contextText;
        }
        return "";
    }

    public void onBackPressed() {
        onBack();
    }
}
