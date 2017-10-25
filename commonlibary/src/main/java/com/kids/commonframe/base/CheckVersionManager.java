package com.kids.commonframe.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.kids.commonframe.R;
import com.kids.commonframe.base.bean.CheckVersionRequest;
import com.kids.commonframe.base.bean.CheckVersionResult;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.kids.commonframe.base.view.CustomUpdateDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.kids.commonframe.base.util.net.NetWorkHelper.DEFAULT_DATABASE_NAME;

/**
 * 版本管理器
 */
public class CheckVersionManager implements NetWorkHelper.NetWorkCallBack<BaseEntity> {
    public interface CheckVersionListener{
    }

    private final int REQUEST_CHECK_VERSION = 1;
    private NetWorkHelper<BaseEntity> netWorkHelper;
    boolean showToast;
    private static CheckVersionManager instance;
    private BaseActivity baseActivity;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int notificationId = 1;
    private File localFile;

    public static final String downUrl = "http://gdown.baidu.com/data/wisegame/bd47bd249440eb5f/shenmiaotaowang2.apk";

    public CheckVersionManager(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        mNotificationManager = (NotificationManager) baseActivity.getSystemService(NOTIFICATION_SERVICE);
        netWorkHelper = new NetWorkHelper<BaseEntity>(baseActivity, this);
    }

    /**
     * 检查版本更新
     */
    public void checkVersion(boolean showToast) {
        this.showToast = showToast;
        CheckVersionRequest checkVersionRequest = new CheckVersionRequest();
        checkVersionRequest.setVersion(CommonUtils.getVersionCode(baseActivity));
        checkVersionRequest.setTag("Android");
        netWorkHelper.sendConnection("/api/app/release/latest/version",checkVersionRequest,REQUEST_CHECK_VERSION,false,VersionUpdateResponse.class);
        if (showToast) {
            ToastUtil.show(baseActivity,"检查更新中...");
        }
    }
    public void startDownloadFile(String remoteUrl) {
        File remoteFile = new File(remoteUrl);
        localFile = new File(CommonUtils.getCachePath(baseActivity),remoteFile.getName());
        BaseDownloadTask downloadTask = FileDownloader.getImpl().create(remoteUrl);
        downloadTask.setPath(localFile.getAbsolutePath())
                .addHeader("X-Odoo-Db", (String) SPUtils.get(baseActivity, "X-Odoo-Db", DEFAULT_DATABASE_NAME))
                .setCallbackProgressMinInterval(1000)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        updateNotification((int) (soFarBytes* 100f) / totalBytes);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        deleteNotification();
                        CommonUtils.installApk(baseActivity,localFile.getAbsolutePath());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        deleteNotification();
                        ToastUtil.show(baseActivity,"下载错误,请重试");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                });
        if(downloadTask.isReusedOldFile()) {
            CommonUtils.installApk(baseActivity,localFile.getAbsolutePath());
        }
        else {
            downloadTask.start();
            ToastUtil.show(baseActivity,"更新下载中...");
        }
    }
    private void initNofBuilder() {
        if( mBuilder == null ) {
            mBuilder = new NotificationCompat.Builder(baseActivity);
            mBuilder.setTicker("下载通知") //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        }
    }

    private void updateNotification(int progress) {
        initNofBuilder();
        mBuilder.setContentTitle("正在下载更新");
        if(progress == 100) {
            mBuilder.setProgress(0,0,false)
                    .setContentText("下载完成");
        }
        else {
            mBuilder.setContentText("完成"+progress+"%")
                    .setProgress(100,progress,false);
        }
        mNotificationManager.notify(notificationId,mBuilder.build());
    }

    private void deleteNotification() {
        mNotificationManager.cancel(notificationId);
    }

    @Override
    public BaseEntity onParse(int where, Class<?> targerClass, String result) {
        return null;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CHECK_VERSION:
                VersionUpdateResponse updateResponse = (VersionUpdateResponse) result.getResult().getData();
                String latestVersion = updateResponse.getVersionName();
                if(TextUtils.isEmpty(latestVersion) || TextUtils.isEmpty(updateResponse.getUrl()))return;

                try{
                    int intLatestVersion = Integer.valueOf(latestVersion);
                    int intCurrentVersion = Integer.valueOf(CommonUtils.getVersionCode(baseActivity));
                    if(intLatestVersion > intCurrentVersion){
                        CustomUpdateDialog customUpdateDialog = new CustomUpdateDialog(baseActivity,updateResponse,CheckVersionManager.this);
                        if(!baseActivity.isFinishing()) {
                            customUpdateDialog.show();
                        }
                    }else {
                        if(showToast) {
                            ToastUtil.show(baseActivity,"已是最新版本");
                        }
                    }
                    return;
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(!latestVersion.equals(CommonUtils.getVersionCode(baseActivity))) {
                    CustomUpdateDialog customUpdateDialog = new CustomUpdateDialog(baseActivity,updateResponse,CheckVersionManager.this);
                    if(!baseActivity.isFinishing()) {
                        customUpdateDialog.show();
                    }
                }
                else {
                    if(showToast) {
                        ToastUtil.show(baseActivity,"已是最新版本");
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
