package com.runwise.supply.orderpage;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

import cn.jpush.android.api.JPushInterface;
import io.vov.vitamio.utils.ContextUtils;

/**
 * Created by libin on 2017/6/29.
 */

public class OrderFragment extends NetWorkFragment {
    @ViewInject(R.id.dayWeekTv)
    private TextView dwTv;
    private ListPopupWindow popupWindow;
    String[] times = {"天","周"};

    @Override
    protected int createViewByLayoutId() {
        return R.layout.order_fragment_layout;
    }
    @OnClick({R.id.dayWeekTv,R.id.sureBtn})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.dayWeekTv:
                if (popupWindow  == null){
                    popupWindow = new ListPopupWindow(mContext);
                    popupWindow.setAdapter(new ArrayAdapter<String>(mContext,R.layout.pop_item,times));
                    popupWindow.setAnchorView(view);
                    popupWindow.setDropDownGravity(Gravity.CENTER);
                    popupWindow.setWidth(CommonUtils.dip2px(mContext,81));
                    popupWindow.setHeight(CommonUtils.dip2px(mContext,93));
                    popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            dwTv.setText(adapterView.getAdapter().getItem(i).toString());
                            popupWindow.dismiss();

                        }
                    });
                    popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.order_whitebg));
                }
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();;
                }else{
                    popupWindow.show();
                }
                break;
            case R.id.sureBtn:
                //跳转到智能下单页面
                Log.i("Jp", JPushInterface.getRegistrationID(mContext.getApplicationContext()));
                Intent intent = new Intent(mContext,OneKeyOrderActivity.class);
                startActivity(intent);
                break;
        }


    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
