package com.kids.commonframe.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.kids.commonframe.R;
import com.kids.commonframe.base.bean.CheckVersionRequest;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.UmengUtil;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.kids.commonframe.base.view.CustomUpdateDialog;
import com.kids.commonframe.config.Constant;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

import io.vov.vitamio.utils.FileUtils;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_COMPANY_NAME;

/**
 * 版本管理器
 */
public class CheckVersionManager implements NetWorkHelper.NetWorkCallBack<BaseEntity> {
    public interface CheckVersionListener {
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
    private static final String WEB_DOWNLOAD = "https://www.pgyer.com/cAvB";

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
        checkVersionRequest.setCompanyName((String) SPUtils.get(baseActivity, FILE_KEY_COMPANY_NAME, ""));

        netWorkHelper.sendConnection(Constant.UNLOGIN_URL, "/api/app/release/latest/version", checkVersionRequest, REQUEST_CHECK_VERSION, false, VersionUpdateResponse.class, true);
        if (showToast) {
            ToastUtil.show(baseActivity, "检查更新中...");
        }
    }

    public void startDownloadFile(String remoteUrl) {
//        remoteUrl = netWorkHelper.getHost(remoteUrl)+remoteUrl;
        File remoteFile = new File(remoteUrl);
        File cacheFile = new File(CommonUtils.getCachePath(baseActivity));
        FileUtils.deleteDir(cacheFile);

        localFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis()+".apk");
        BaseDownloadTask downloadTask = FileDownloader.getImpl().create(remoteUrl);

//        String header = (String) SPUtils.get(baseActivity, FILE_KEY_DB_NAME, "");
//        if (!TextUtils.isEmpty(header)) downloadTask.addHeader("X-Odoo-Db", header);


        downloadTask.setPath(localFile.getAbsolutePath())
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
                        updateNotification((int) (soFarBytes * 100f) / totalBytes);
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
                        try {
                            installApk();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //友盟报告错误
                            UmengUtil.reportError(baseActivity, "安装apk报错: " + e.toString());
                            goToBrowser();
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        deleteNotification();
                        goToBrowser();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                });
        if (downloadTask.isReusedOldFile()) {
            if (localFile.length() <= 10 * 1048576) {
                UmengUtil.reportError(baseActivity, "安装apk报错: 安裝包下載不完整");
                goToBrowser();
                return;
            }
            installApk();
        } else {
            downloadTask.start();
            ToastUtil.show(baseActivity, "更新下载中...");
        }
    }

    private void installApk(){
        //                            1M=1024k=1048576字节
        if (localFile.length() <= 10 * 1048576) {
            UmengUtil.reportError(baseActivity, "安装apk报错: 安裝包下載不完整");
            goToBrowser();
            return;
        }
        if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(baseActivity, "com.runwise.supply.fileprovider", localFile);//在AndroidManifest中的android:authorities值
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            baseActivity.startActivity(install);
        } else{
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(localFile), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            baseActivity.startActivity(install);
        }
    }

    private void goToBrowser() {
        ToastUtil.show(baseActivity, "下载错误,请重试或在网页下载安装");
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_DOWNLOAD));
        baseActivity.startActivity(it);
    }

    private void initNofBuilder() {
        if (mBuilder == null) {
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
        if (progress == 100) {
            mBuilder.setProgress(0, 0, false)
                    .setContentText("下载完成");
        } else {
            mBuilder.setContentText("完成" + progress + "%")
                    .setProgress(100, progress, false);
        }
        mNotificationManager.notify(notificationId, mBuilder.build());
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
                if (TextUtils.isEmpty(latestVersion) || TextUtils.isEmpty(updateResponse.getUrl()))
                    return;

                try {
                    int intLatestVersion = Integer.valueOf(latestVersion);
                    int intCurrentVersion = Integer.valueOf(CommonUtils.getVersionCode(baseActivity));
                    if (intLatestVersion > intCurrentVersion) {
                        CustomUpdateDialog customUpdateDialog = new CustomUpdateDialog(baseActivity, updateResponse, CheckVersionManager.this);
                        if (!baseActivity.isFinishing()) {
                            customUpdateDialog.show();
                        }
                    } else {
                        if (showToast) {
                            ToastUtil.show(baseActivity, "已是最新版本");
                        }
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!latestVersion.equals(CommonUtils.getVersionCode(baseActivity))) {
                    CustomUpdateDialog customUpdateDialog = new CustomUpdateDialog(baseActivity, updateResponse, CheckVersionManager.this);
                    if (!baseActivity.isFinishing()) {
                        customUpdateDialog.show();
                    }
                } else {
                    if (showToast) {
                        ToastUtil.show(baseActivity, "已是最新版本");
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
