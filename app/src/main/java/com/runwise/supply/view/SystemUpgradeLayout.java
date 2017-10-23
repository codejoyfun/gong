package com.runwise.supply.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kids.commonframe.base.bean.SystemUpgradeNoticeEvent;
import com.runwise.supply.R;
import com.kids.commonframe.base.util.SystemUpgradeHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Denzel on 2017/10/19.
 */

public class SystemUpgradeLayout extends FrameLayout {
    private String mPageName;
    private ViewGroup mVgUpgradeNotice;
    private TextView mTvNotice;
    private View mVClose;
    public SystemUpgradeLayout(Context context) {
        super(context);
        init();
    }

    public SystemUpgradeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SystemUpgradeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_system_upgrade_notice,this,true);
        mVgUpgradeNotice = (ViewGroup)findViewById(R.id.rl_upgrade_notice);
        mVClose = findViewById(R.id.iv_warn_closed);
        mTvNotice = (TextView)findViewById(R.id.tv_upgrade_notice);
    }

    /**
     * 在onCreate调用
     * @param pageName
     */
    public void setPageName(String pageName){
        mPageName = pageName;//是否系统更新
    }

    private void check(){
        if(TextUtils.isEmpty(mPageName))return;
        SystemUpgradeHelper systemUpgradeHelper = SystemUpgradeHelper.getInstance(getContext());
        if(SystemUpgradeHelper.getInstance(getContext()).needShowNotice(mPageName)){
            mVgUpgradeNotice.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(systemUpgradeHelper.getStartTime()*1000);
            StringBuilder sb = new StringBuilder();
            sb.append(sdf.format(cal.getTime())).append("~");
            cal.setTimeInMillis(systemUpgradeHelper.getEndTime()*1000);
            sb.append(sdf.format(cal.getTime()));

            mTvNotice.setText(getContext().getString(R.string.system_upgrade_notice,sb.toString(),mPageName));
            mVClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemUpgradeHelper.getInstance(getContext()).setIsRead(mPageName);
                    mVgUpgradeNotice.setVisibility(View.GONE);
                }
            });
            mTvNotice.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTvNotice.setSelected(true);
                }
            },2000);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        check();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemUpgradeNotice(SystemUpgradeNoticeEvent receiverLogoutEvent) {
        check();
    }
}
