package com.runwise.supply.message;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ImageUtils;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.entity.QnTokenRepEntity;
import com.runwise.supply.pictakelist.PicTake;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


/**
 * 发送意见
 */
public class ActionRecordEditRequest implements  NetWorkHelper.NetWorkCallBack<BaseEntity> {

    public static final String TAG = ActionRecordEditRequest.class.getClass().getSimpleName();
    public static final int REQUEST_GROWTH_RECORD_TOKEN = 10001;
    public static final int REQUEST_GROWTH_RECORD_CONTENT_ADD = 10002;

    private Context context;
    private String token = null;
    private NetWorkHelper netWorkHelper;
    private UploadManager uploadManager = null;
    public volatile boolean isCancelled = false;
    private UploadOptions uploadOptions = null;
    private ArrayMap<String, PicTake> arrayPicMap = null;


//    private ActionSendRequest actionSendRequest = null;
    private String growType = ""; //4：带图片；5：不带图片，纯文字
    private List<PicTake> updateImgs = null;
    private ActionRecordRequestListenter recordRequestListenter;
    private List<PicTake> mUploadPicTakes = null;//上传图片集合

    private UserInfo userInfo;
    public ActionRecordEditRequest(Context context) {
        this.context = context;
        this.uploadManager = new UploadManager();
//        this.uploadOptions = new UploadOptions(null, null, false, this, this);
        this.netWorkHelper = new NetWorkHelper<>(context, this);
        this.arrayPicMap = new ArrayMap<>();
        userInfo = GlobalApplication.getInstance().loadUserInfo();
    }

    private void rest(){
        if(this.arrayPicMap != null){
            this.arrayPicMap.clear();
        }
    }
    /**
     * 添加资讯信息
     * @param recordContent 内容
     * @param picTakes 图片集合
     */
    public void addGrowthRecord(String title,String recordContent,String contact,List<PicTake> picTakes, ActionRecordRequestListenter recordRequestListenter){
        rest();
        this.updateImgs = picTakes;
        this.recordRequestListenter = recordRequestListenter;
        if(this.recordRequestListenter != null){
            recordRequestListenter.onRequestStart();
        }
        growType = "5";
        setBabyGrowthRecordData(title,recordContent,contact, growType,picTakes);
//        performAddGrowthRecord();
    }


    /**
     * 发送设置发送内容
     */
    public void setBabyGrowthRecordData(String title,String recordContent,String contact,String type,List<PicTake> picTakes){
        List<Part> partList = new ArrayList<>();
        partList.add(new StringPart("title", title));
        partList.add(new StringPart("content", recordContent));
        partList.add(new StringPart("contact", contact));
        if(picTakes != null && !picTakes.isEmpty()) {
            for(int i = 0; i < picTakes.size(); i++ ){
                PicTake picTake = picTakes.get(i);
                try {
                    String imagePath = ImageUtils.getScaledImage(context, picTake.getPicPath());
                    partList.add(new FilePart("imgfile["+ i +"]", new File(imagePath)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        this.netWorkHelper.sendConnection("feedback/store.json", partList, REQUEST_GROWTH_RECORD_CONTENT_ADD, false, BaseEntity.class);
    }

//    private ActionSendRequest getSendAction(){
//        if(actionSendRequest == null){
//            actionSendRequest = new ActionSendRequest();
//            List<MaterialEntity> materialEntityList = new ArrayList<>();
//            actionSendRequest.setImage_list(materialEntityList);
//        }
//        return  actionSendRequest;
//    }


    /**
     * 再编辑时 判断是否有图片修改
     * @return
     */
    public boolean getUploadListIsChange(){
        if(updateImgs != null){
            for (PicTake picTake:updateImgs){
                if(!picTake.isNetResouce()){
                    return  true;
                }
            }
        }
        return  false;
    }

    /**
     * 需要上传的图片集合
     */
    public List<PicTake>  getImageUploadList(){
        if(mUploadPicTakes == null){
            mUploadPicTakes = new ArrayList<>();
        }
        if(updateImgs != null){
            mUploadPicTakes.clear();
            for (PicTake pic:updateImgs){
                if(!pic.isNetResouce()){
                    mUploadPicTakes.add(pic);
                }
            }
        }
        return mUploadPicTakes;
    }

    /**
     * 使用七牛组件上传图片
     */
    private void performUpload() {
        LogUtil.e(TAG, "---开始上传图片-----");
        List<PicTake> uploadList = getImageUploadList();
        try {
            if (uploadList != null && uploadList.size() > 0 ) {
                for (PicTake picTake : uploadList) {
                    if (uploadManager != null) {
                        String filekey = picTake.getQiniuFileName();
                        String filePath = ImageUtils.getScaledImage(context, picTake.getPicPath());
                        if (filekey == null) {
                            filekey = CommonUtils.generateImgFileName();
                        }
                        LogUtil.e(TAG, "----上传..." + filePath + "------" + filekey);
                        arrayPicMap.put(filekey, picTake);
//                        long ptime = Long.parseLong(picTake.getCreateTime());
//                        if(!picTake.isNetResouce()){
//                            ptime = ptime * 1000;
//                        }
//                        uploadManager.put(filePath, filekey, token, this, uploadOptions);
                    }
                }
            }else{
                isCancelled = true;
                if(this.recordRequestListenter != null){
                    recordRequestListenter.onRequestFail();
                }
                ToastUtil.show(context,"图片上传失败");
            }
        } catch (Exception e) {
            isCancelled = true;
            if(this.recordRequestListenter != null){
                recordRequestListenter.onRequestFail();
            }
            ToastUtil.show(context,"图片上传失败");
        }
    }

//    /**
//     * 上传完成（主线程中执行)
//     *
//     * @param key      文件上传保存名称
//     * @param info     上传完成返回日志信息
//     * @param response 上传完成的回复内容
//     */
////    @Override
//    public void complete(String key, ResponseInfo info, JSONObject response) {
////        if (isCancelled()) {
////            LogUtil.e(TAG, "发送已取消！");
////            return;
////        }
//        if (info != null && info.isOK()) { //上传成功
//            LogUtil.e(TAG, JSON.toJSON(info) + ";" + response.toString());
//            if (materialInfos == null) {
//                materialInfos = new ArrayList<MaterialEntity>();
//            }
//            MaterialEntity materialInfo = materialMap.get(key);
//            String remoteUrl = null;
//            PicTake picTake = arrayPicMap.get(key);
//            remoteUrl = key;
//            materialInfo.setImage_url(remoteUrl);
//            if (picTake != null) {
//                materialInfo.setWidth(String.valueOf(picTake.getWidth()));
//                materialInfo.setHeight(String.valueOf(picTake.getHeight()));
//            }
//            LogUtil.e(TAG, "上传成功后地址:" + remoteUrl);
//            materialInfos.add(materialInfo);
//            if (materialInfos != null && materialInfos.size() == getImageUploadList().size()) {
//                List<MaterialEntity> materialInfos = new ArrayList<>(materialMap.values());
////                getSendAction().setImage_list(materialInfos);
////                LogUtil.e(TAG, "图片全部上传成功,开始发送班级圈：" + materialInfos.size());
////                //开始发送班级圈
////                if (!isCancelled()) {
////                    LogUtil.e(TAG, "添加成长记录...");
////                    performAddGrowthRecord();
////                } else {
////                    LogUtil.e(TAG, "已取消发送...");
////                }
//            }
//        } else {
//            String infoss = "";
//            String responsess = "";
//            if (info != null) {
//                infoss = JSON.toJSON(info).toString();
//            }
//            if (response != null) {
//                responsess = response.toString();
//            }
//            if(this.recordRequestListenter != null){
//                recordRequestListenter.onRequestFail();
//            }
//            LogUtil.e(TAG, "上传失败:" + key + " info :" + infoss + " response:" + responsess);
//            isCancelled = true;
//        }
//    }

    /**
     * token获取
     * @param fileType
     * @param fileName
     */
    public void performGetToken(String fileType, String fileName) {
        requestQiniuToken(fileType, fileName);
    }

    /**
     * 执行成长记录发送
     */
    public void performAddGrowthRecord() {
//        requestGrowthRecordAdd(getSendAction());
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_GROWTH_RECORD_TOKEN:
                if (result != null) {
                    token = ((QnTokenRepEntity) result).getData();
                }
                isCancelled = false;
                performUpload();//开始执行上传
                break;
            case REQUEST_GROWTH_RECORD_CONTENT_ADD:
//                if (isCancelled()) {//如果发送成功了
//                    //发送请求删除这条任务
//                }
                if(this.recordRequestListenter != null){
                    recordRequestListenter.onRequestSuccess(result);
                }
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where) {
            case REQUEST_GROWTH_RECORD_TOKEN:
            case REQUEST_GROWTH_RECORD_CONTENT_ADD:
                if(this.recordRequestListenter != null){
                    recordRequestListenter.onRequestFail();
                }
                break;
        }
        ToastUtil.show(context,errMsg);
    }

    @Override
    public BaseEntity onParse(int where, Class<?> targerClass, String result) {
        return null;
    }

    /**
     * 获取Token
     *
     * @param fileType 数据类型 上传文件类型, 0图片，1视频，2音频，3文档(非指定)，不填默认文件类型如（doc等）
     */
    public void requestQiniuToken(String fileType,String fileName) {
        this.netWorkHelper.sendConnection("/Appapi/qiniu/gettoken", REQUEST_GROWTH_RECORD_TOKEN, false, QnTokenRepEntity.class);
    }

    /**
     *
//     * @param record 发送内容
     */
//    public void requestGrowthRecordAdd(ActionSendRequest record) {
//        ActionSendRequestParam param = new ActionSendRequestParam();
//        param.setParams(JSON.toJSONString(record));
//        this.netWorkHelper.sendConnection("/Appapi/circle/add", param, REQUEST_GROWTH_RECORD_CONTENT_ADD, false, ActionAddRespone.class);
//    }


    protected void cancelRequest() {
        if(netWorkHelper != null){
            netWorkHelper.onStopAllRequest();
        }
    }

    public interface ActionRecordRequestListenter{
        void onRequestStart();
        void onRequestSuccess(BaseEntity baseEntity);
        void onRequestFail();
    }

}

