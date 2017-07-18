package com.runwise.supply.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomSelectDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.base.view.widget.AbstractWheelTextAdapter;
import com.kids.commonframe.base.view.widget.OnWheelScrollListener;
import com.kids.commonframe.base.view.widget.PickerClickListener;
import com.kids.commonframe.base.view.widget.WheelView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.CarSettingFragmentContainer;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.index.entity.CarImage;
import com.runwise.supply.mine.entity.CollectCar;
import com.runwise.supply.mine.entity.CollectCarInfo;
import com.runwise.supply.mine.entity.CollectData;
import com.runwise.supply.mine.entity.CollectResult;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 下单提醒推送
 */
public class NotiySettingDateActivity extends NetWorkActivity implements AdapterView.OnItemClickListener {
    private static final int REQUEST_MAIN = 1;

    @ViewInject(R.id.selectView1)
    private WheelView wheelViewHour;
    private DataAdapter wheelAdapter1;

    @ViewInject(R.id.selectView2)
    private WheelView wheelViewMin;
    private DataAdapter wheelAdapter2;

    @ViewInject(R.id.selectView3)
    private WheelView wheelViewAmPm;
    private DataAdapter wheelAdapter3;


    //TOD设置选中项
    public void setCurrentItem(int hourPot,int minPot,int pot){
//        wheelView.setCurrentItem(index);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_notiydate_list);
        this.setTitleText(true,"下单提醒推送");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        this.setTitleRightText(true,"保存");

        wheelAdapter1 = new DataAdapter(this);
        List<String> hourStr = new ArrayList<>();
        for (int i = 0; i <12 ; i++) {
            hourStr.add(String.valueOf(i+1));
        }
        wheelAdapter1.setData(hourStr);
        wheelViewHour.setViewAdapter(wheelAdapter1);
        wheelViewHour.setCyclic(true);
        wheelViewHour.setWheelBackground(com.kids.commonframe.R.color.white);
        wheelViewHour.setDrawShadows(false);
        wheelViewHour.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = wheelAdapter1.getItemText(wheel.getCurrentItem()).toString();
                LinearLayout itemLaout = wheelViewHour.getItemsLayout();
                for (int i=0; i< itemLaout.getChildCount(); i++) {
                    View child = itemLaout.getChildAt(i);
                    if ( child instanceof  TextView) {
                        TextView item = (TextView) child;
                        if (currentText.equals(item.getText())) {
                            item.setTextColor(wheelAdapter1.getSelectColor());
                        }
                        else {
                            item.setTextColor(wheelAdapter1.getUnSelectColor());
                        }
                    }
                }
            }
        });

        wheelAdapter2 = new DataAdapter(this);
        List<String> minStr = new ArrayList<>();
        for (int i=0; i <60 ; i++) {
            minStr.add(String.valueOf(i+1));
        }
        wheelAdapter2.setData(minStr);
        wheelViewMin.setViewAdapter(wheelAdapter2);
        wheelViewMin.setCyclic(true);
        wheelViewMin.setWheelBackground(com.kids.commonframe.R.color.white);
        wheelViewMin.setDrawShadows(false);
        wheelViewMin.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = wheelAdapter2.getItemText(wheel.getCurrentItem()).toString();
                LinearLayout itemLaout = wheelViewMin.getItemsLayout();
                for (int i=0; i< itemLaout.getChildCount(); i++) {
                    View child = itemLaout.getChildAt(i);
                    if ( child instanceof  TextView) {
                        TextView item = (TextView) child;
                        if (currentText.equals(item.getText())) {
                            item.setTextColor(wheelAdapter2.getSelectColor());
                        }
                        else {
                            item.setTextColor(wheelAdapter2.getUnSelectColor());
                        }
                    }
                }
            }
        });


        wheelAdapter3 = new DataAdapter(this);
        List<String> amStr = new ArrayList<>();
        amStr.add("上午");
        amStr.add("下午");
        wheelAdapter3.setData(amStr);
        wheelViewAmPm.setViewAdapter(wheelAdapter3);
        wheelViewAmPm.setWheelBackground(com.kids.commonframe.R.color.white);
        wheelViewAmPm.setDrawShadows(false);
        wheelViewAmPm.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = wheelAdapter3.getItemText(wheel.getCurrentItem()).toString();
                LinearLayout itemLaout = wheelViewAmPm.getItemsLayout();
                for (int i=0; i< itemLaout.getChildCount(); i++) {
                    View child = itemLaout.getChildAt(i);
                    if ( child instanceof  TextView) {
                        TextView item = (TextView) child;
                        if (currentText.equals(item.getText())) {
                            item.setTextColor(wheelAdapter3.getSelectColor());
                        }
                        else {
                            item.setTextColor(wheelAdapter3.getUnSelectColor());
                        }
                    }
                }
            }
        });
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        sendConnection("collect/list.json",request,where,showDialog,CollectResult.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                break;
        }
    }
    @OnClick(R.id.catType)
    public void doSelectType(View view ) {
        Intent intent = new Intent(this,NotiySettingTypeActivity.class);
        startActivity(intent);
    }
    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CollectCarInfo bean = (CollectCarInfo)parent.getAdapter().getItem(position);
        Intent intent = new Intent(this,CarSettingFragmentContainer.class);
        intent.putExtra("carid",bean.getCar_id());
        startActivity(intent);
    }

    private class DataAdapter extends AbstractWheelTextAdapter {
        private List<String> dataList = new ArrayList<>();
        protected DataAdapter(Context context) {
            super(context, com.kids.commonframe.R.layout.wheel_item_layout, NO_RESOURCE);
            setItemTextResource(com.kids.commonframe.R.id.item_name);
        }
        public void setData(List<String> dataList) {
            this.dataList.addAll(dataList);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent, int currentItem) {
            View view = super.getItem(index, cachedView, parent,currentItem);
            return view;
        }
        @Override
        public int getItemsCount() {
            return dataList.size();
        }
        @Override
        protected CharSequence getItemText(int index) {
            return dataList.get(index);
        }
        @Override
        public int getSelectColor() {
            return Color.parseColor("#333333");
        }
        @Override
        public int getUnSelectColor() {
            return Color.parseColor("#d9d9d9");
        }
    }

}
