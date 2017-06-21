package com.runwise.supply.business;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocationClient;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.DealerEnity;
import com.runwise.supply.business.entity.DealerRequest;
import com.runwise.supply.business.entity.DealerResponse;
import com.runwise.supply.business.entity.DealerEnity;

import java.util.ArrayList;
import java.util.List;

/**
 * 附近地图页面
 *　
 */
public class NearFragment extends NetWorkFragment{
    @ViewInject(R.id.bmapView)
    private TextureMapView mMapView;


    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;                      //底部横向滑动栏

    private BaiduMap mBaiduMap;
    private MyLocationData locData;                         //当前系统回调的定位点

    //定位
    private AMapLocationClient locationClient = null;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private static BitmapDescriptor bitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.near_near);
    private Marker mCustomMarker;
    //地图状态更新,地图居中
    //滑动栏
    private  List<DealerEnity> dataList;                              //滑动数据源
    //适配器
    private RecyclerViewAdapter adapter;
    //
    private LinearLayoutManager layoutManager;
    OverlayManager overlayManager;
    private ArrayList overlayOptionsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true, "附近" );
        this.setTitleTextColor(Color.WHITE);
        //开启定位
        initLocationPoint();
        this.setTitleRigthIcon(true, R.drawable.near_search);

        //初始化滑动列表
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //经销商列表
        dataList = new ArrayList<DealerEnity>();
        overlayOptionsList = new ArrayList();
        //创建并设置Adapter
        adapter = new RecyclerViewAdapter(dataList,getActivity());
        adapter.setOnItemClickerListener(new RecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, DealerEnity dataModel) {
                //跳转到经销商详情
                Log.i("tag","跳转到经销商详情");
                Intent intent = new Intent(getContext(),DealerDetainActivity.class);
                intent.putExtra("dealer_id",dataModel.getDealer_id());
                intent.putExtra("distance",dataModel.getDistance());
                startActivity(intent);
            }
            @Override
            public void onCallClick(DealerEnity dataModel) {
                Log.i("tag","跳转到打电话页面");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + dataModel.getContact_phone());
                intent.setData(data);
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//
//            }
//        });
    }
    private void requestInfoByLngLat(double lng, double lat){
        DealerRequest request = new DealerRequest(String.valueOf(lng),String.valueOf(lat));
        sendConnection("/dealers/about.json",request,1000,true, DealerResponse.class);
    }
    @OnClick(R.id.title_iv_rigth)
    public void rightClick(View view){
        //TODO：跳转到搜索页
        startActivity(new Intent(getContext(),DealerSearchActivity.class));
//        Intent intent = new Intent(getContext(),DealerMapActivity.class);
//        intent.putExtra("lat",locData.latitude);
//        intent.putExtra("lng",locData.longitude);
//        startActivity(intent);
//        startActivity(new Intent(getContext(),DealerDetainActivity.class));
//        startActivity(new Intent(getContext(),SignActivity.class));
//        Intent intent = new Intent(getContext(),CarSettingFragmentContainer.class);
//        intent.putExtra("carid","1");
//        startActivity(intent);
    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onPause() {
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
        mLocationClient.stop();
    }

    @Override
    public void onResume() {
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        super.onResume();
        mLocationClient.start();
    }
    //请求回值
    @Override
    public void onSuccess(BaseEntity result, int where) {
        DealerResponse response = (DealerResponse)result;
        //处理列表数据
        dataList.clear();
        dataList.addAll(response.getData().getEntities());
        adapter.notifyDataSetChanged();
        //地图上加marker
        addSomePointsToMap();
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg+where);
    }

    //初始化定位监听
    private void initLocationPoint(){
        mLocationClient = new LocationClient(getContext().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //隐藏放大缩小按钮
        mMapView.removeViewAt(2);
        overlayManager = new OverlayManager(mBaiduMap) {
            @Override
            public List<OverlayOptions> getOverlayOptions() {
                return overlayOptionsList;
            }

            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }

            @Override
            public boolean onPolylineClick(Polyline polyline) {
                return false;
            }
        };

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=5000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);

        //marker监听
        setMapListener();

    }
    //设置marker图标
    private void setMyMaker(BDLocation location){
       locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
               .latitude(location.getLatitude()).longitude(location.getLongitude()).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);

        //mCurrentMarker 是首次根据定位来,其它时候手动拖动
        if (mCustomMarker == null){
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions cOption = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .icon(bitmap).zIndex(20).draggable(true);
            mCustomMarker = (Marker)(mBaiduMap.addOverlay(cOption));
            mCustomMarker.setPerspective(true);
            //首次发送请求，其它时候，根据操作
            requestInfoByLngLat(location.getLongitude(),location.getLatitude());
        }
    }
    private void setMapListener(){
        //拖动监听
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mCustomMarker = addPointToMap(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                if (marker != mCustomMarker && mCustomMarker != null){
                    mCustomMarker.remove();
                    mCustomMarker = null;
                }
                marker.remove();
            }
        });
        //当前位置点点击
        mBaiduMap.setOnMyLocationClickListener(new BaiduMap.OnMyLocationClickListener() {
            /**
             * 地图定位图标点击事件监听函数
             */
            public boolean onMyLocationClick() {
                //将当前marder到定位点上
                mCustomMarker.remove();
                mCustomMarker = addPointToMap(new LatLng(locData.latitude, locData.longitude));
                //居中地图
                centerInMapByLatLng(new LatLng(locData.latitude,locData.longitude));
                return true;
            }
        });
        //除当前位置点外的点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             * 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng latLng) {
                mCustomMarker.remove();
                mCustomMarker = addPointToMap(latLng);
            }
            /**
             * 地图内 Poi 单击事件回调函数
             * 点击的 poi 信息
             */
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                mCustomMarker.remove();
                mCustomMarker = addPointToMap(new LatLng(mapPoi.getPosition().latitude,mapPoi.getPosition().longitude));
//                addSomePointsToMap();
                return true;
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker == mCustomMarker){
                    return false;
                }
                //TOD：点击，直接进入经销商详情页面
                double lat = marker.getPosition().latitude;
                double lng = marker.getPosition().longitude;
                for(DealerEnity enity : dataList){
                    if (Double.valueOf(enity.getLat()) == lat && (Double.valueOf(enity.getLng())==lng)){
                        Intent intent = new Intent(getContext(),DealerDetainActivity.class);
                        intent.putExtra("dealer_id",enity.getDealer_id());
                        intent.putExtra("distance",enity.getDistance());
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });

    }
    //添加marker到地图上
    private Marker addPointToMap(LatLng latLng){
        OverlayOptions cOption = new MarkerOptions()
                .position(new LatLng(latLng.latitude,latLng.longitude))
                .icon(bitmap).zIndex(20).draggable(true);
        Marker marker = (Marker)mBaiduMap.addOverlay(cOption);
        overlayOptionsList.add(cOption);
        //居中
        centerInMapByLatLng(latLng);
        return marker;
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

    //添加多个点到地图上,list位置点列表OverlayManager
    private void addSomePointsToMap(){
        //清除地图上全部的点PoiOverlay
        clearMarkers();
        for(int j = 0; j < dataList.size(); j++){
            DealerEnity enity = dataList.get(j);
            BitmapDescriptor tmp = BitmapDescriptorFactory.fromBitmap(CommonUtils.drawTextToBitmap(getContext(),R.drawable.near_dealer,String.valueOf(j+1)));
            OverlayOptions cOption = new MarkerOptions()
                    .position(new LatLng(Double.valueOf(enity.getLat()),Double.valueOf(enity.getLng())))
                    .icon(tmp).zIndex(20).draggable(true);
            Marker marker =(Marker) mBaiduMap.addOverlay(cOption);
            overlayOptionsList.add(cOption);
        }
        overlayManager.addToMap();
        overlayManager.zoomToSpan();
        overlayManager.removeFromMap();

    }

    private void clearMarkers(){
        mBaiduMap.clear();
        //加当前marker上去
        if (mCustomMarker != null)
            mCustomMarker = addPointToMap(new LatLng(mCustomMarker.getPosition().latitude,mCustomMarker.getPosition().longitude));
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());

            //在这里实时改变位置点marker
            setMyMaker(location);
            centerInMapByLatLng(new LatLng(location.getLatitude(),location.getLongitude()));

        }
    }

}

