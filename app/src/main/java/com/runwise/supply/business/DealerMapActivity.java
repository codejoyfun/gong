package com.runwise.supply.business;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * Created by libin on 2017/1/7.
 */

public class DealerMapActivity extends BaseActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private static BitmapDescriptor bitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.map_dealer);

    //保存传进来的经纬度
    private LatLng latLng;
    private Marker myMarker;
    private String dealerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dealer_map_layout);
        StatusBarUtil.StatusBarLightMode(this);
        setTitleText(true,"地图");
        setTitleLeftIcon(true,R.drawable.returned);
        getDealerLatLng();
        initMap();
    }
    protected void onResume(){
        super.onResume();
        centerInMapByLatLng(latLng);
    }
    @OnClick({R.id.title_iv_left})
    public  void leftClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                this.finish();
                break;
            default:
                break;
        }
    }
    private void getDealerLatLng(){
        Intent fromIntent = getIntent();
        dealerName = fromIntent.getStringExtra("name");
        double lat = fromIntent.getDoubleExtra("lat",0);
        double lng = fromIntent.getDoubleExtra("lng",0);
        latLng = new LatLng(lat,lng);
    }
    private void initMap(){
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        //隐藏放大缩小按钮
        mMapView.removeViewAt(2);

        //显示Marker
        OverlayOptions cOption = new MarkerOptions()
                .position(latLng)
                .icon(bitmap).zIndex(20).draggable(false);
        myMarker = (Marker) mBaiduMap.addOverlay(cOption);

        //显示弹出窗
        //创建InfoWindow展示的view
        TextView textView = new TextView(getApplicationContext());
        textView.setText(dealerName);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(R.drawable.map_dealer_name);
        textView.setGravity(Gravity.CENTER);
        //定义用于显示该InfoWindow的坐标点
        LatLng pt = latLng;
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(textView, pt, -110);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);

    }
    //地图居中
    private void centerInMapByLatLng(LatLng centerLL){
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(centerLL)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }
}
