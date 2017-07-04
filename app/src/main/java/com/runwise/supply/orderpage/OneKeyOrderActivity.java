package com.runwise.supply.orderpage;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.tools.TimeUtils;

import java.sql.Time;

import me.shaohui.bottomdialog.BottomDialog;


/**
 * Created by libin on 2017/6/30.
 */

public class OneKeyOrderActivity extends NetWorkActivity {
    int[] loadingImgs = new int[31];
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.dateTv)
    private TextView dateTv;
    @ViewInject(R.id.loadingImg)
    private ImageView loadingImg;
    private int currentIndex;
    @ViewInject(R.id.loadingTv)
    private TextView loadingTv;
    //弹窗星期的View集合
    private TextView[] wArr = new TextView[3];
    private TextView[] dArr = new TextView[3];
    //记录当前是选中的哪个送货时期，默认明天, 0今天，1明天，2后天
    private int selectedDate = 1;
    //缓存外部显示用的日期周几
    private String cachedDWStr = TimeUtils.getABFormatDate(1).substring(5) + " " + TimeUtils.getWeekStr(1);
    private BottomDialog dialog = BottomDialog.create(getSupportFragmentManager())
            .setViewListener(new BottomDialog.ViewListener(){
                @Override
                public void bindView(View v) {
                    initDefaultDate(v);
                }
            }).setLayoutRes(R.layout.date_layout)
            .setCancelOutside(true)
            .setDimAmount(0.5f);
    //    private BottomSheetDialog showDialog = new BottomSheetDialog(mContext);
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loadingImg.setImageResource(loadingImgs[currentIndex++]);
            if (currentIndex >= 31){
                currentIndex = 0;
            }
            handler.postDelayed(runnable,30);
        }
    };
    @OnClick({R.id.dateTv,R.id.title_iv_left,R.id.title_tv_rigth})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.dateTv:
                //弹出日期选择控件
                if (dialog.isVisible()){
                    dialog.dismiss();
                }else{
                    dialog.show();
                }
                break;
            case R.id.title_iv_left:
                break;
            case R.id.title_tv_rigth:
                Intent intent = new Intent(mContext,ProductActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initDefaultDate(View v) {
        RelativeLayout rll1 = (RelativeLayout) v.findViewById(R.id.rll1);
        RelativeLayout rll2 = (RelativeLayout) v.findViewById(R.id.rll2);
        RelativeLayout rll3 = (RelativeLayout) v.findViewById(R.id.rll3);
        TextView wTv1 = (TextView) v.findViewById(R.id.wTv1);
        TextView dTv1 = (TextView) v.findViewById(R.id.dTv1);
        TextView wTv2 = (TextView) v.findViewById(R.id.wTv2);
        TextView dTv2 = (TextView) v.findViewById(R.id.dTv2);
        TextView wTv3 = (TextView) v.findViewById(R.id.wTv3);
        TextView dTv3 = (TextView) v.findViewById(R.id.dTv3);
        wArr[0] = wTv1;
        wArr[1] = wTv2;
        wArr[2] = wTv3;
        dArr[0] = dTv1;
        dArr[1] = dTv2;
        dArr[2] = dTv3;
        //选中哪个，通过selectedDate来判断
        wArr[selectedDate].setTextColor(Color.parseColor("#6BB400"));
        dArr[selectedDate].setTextColor(Color.parseColor("#6BB400"));
        //计算当前日期起，明后天的星期几+号数
        wTv1.setText(TimeUtils.getWeekStr(0));
        String[] t = TimeUtils.getABFormatDate(0).split("-");
        if (t.length > 2){
            dTv1.setText(t[1]+"-"+t[2]);
        }
        wTv2.setText(TimeUtils.getWeekStr(1));
        t = TimeUtils.getABFormatDate(1).split("-");
        if (t.length > 2){
            dTv2.setText(t[1]+"-"+t[2]);
        }
        wTv3.setText(TimeUtils.getWeekStr(2));
        t = TimeUtils.getABFormatDate(2).split("-");
        if (t.length > 2){
            dTv3.setText(t[1]+"-"+t[2]);
        }
        //初始化点击事件
        rll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空颜色
                setSelectedColor(0);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectedDate = 0;
                        dialog.dismiss();
                        dateTv.setText(TimeUtils.getABFormatDate(0).substring(5)+" "+TimeUtils.getWeekStr(0));
                    }
                },500);
            }
        });
        rll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空颜色
                setSelectedColor(1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectedDate = 1;
                        dialog.dismiss();
                        dateTv.setText(TimeUtils.getABFormatDate(1).substring(5)+" "+TimeUtils.getWeekStr(1));
                    }
                },500);
            }
        });
        rll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空颜色
                setSelectedColor(2);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectedDate = 2;
                        dialog.dismiss();
                        dateTv.setText(TimeUtils.getABFormatDate(2).substring(5)+" "+TimeUtils.getWeekStr(2));
                    }
                },500);
            }
        });
    }
    //参数从0开始
    private void setSelectedColor(int i) {
        for (TextView tv : wArr){
            tv.setTextColor(Color.parseColor("#2E2E2E"));
        }
        for (TextView tv : dArr){
            tv.setTextColor(Color.parseColor("#2E2E2E"));
        }
        wArr[i].setTextColor(Color.parseColor("#6BB400"));
        dArr[i].setTextColor(Color.parseColor("#6BB400"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onekey_order_layout);
        setTitleText(true,"智能下单");
        setTitleLeftIcon(true,R.drawable.nav_back);
        setTitleRightText(true,"编辑");
        pullListView.setVisibility(View.INVISIBLE);
        initLoadingImgs();
        handler.postDelayed(runnable,0);
        dateTv.setText(cachedDWStr);
//        showDialog.setTitle("选择送达日期");
//        showDialog.setCancelable(true);
    }

    private void initLoadingImgs() {
        StringBuffer sb;
        for (int i = 0; i < 31;i++){
            sb = new StringBuffer("order_loading_");
            sb.append(i);
            loadingImgs[i] = getResIdByDrawableName(sb.toString());
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    private int getResIdByDrawableName(String name){
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }

}
