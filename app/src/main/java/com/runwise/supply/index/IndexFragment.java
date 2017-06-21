package com.runwise.supply.index;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.WebViewActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.IWebViewActivity;
import com.runwise.supply.R;
import com.runwise.supply.business.CarSeriesActivity;
import com.runwise.supply.business.CarSettingFragmentContainer;
import com.runwise.supply.business.DealerDetainActivity;
import com.runwise.supply.business.DealerSearchActivity;
import com.runwise.supply.business.SelectDealerActivity;
import com.runwise.supply.index.entity.CarImage;
import com.runwise.supply.index.entity.CarPeriod;
import com.runwise.supply.index.entity.HomeData;
import com.runwise.supply.index.entity.HomeList;
import com.runwise.supply.index.entity.HomeResult;
import com.runwise.supply.index.entity.IndexAllListEntity;
import com.runwise.supply.index.entity.IndexCarInfo;
import com.runwise.supply.index.entity.IndexCarSys;
import com.runwise.supply.index.entity.IndexDealers;
import com.runwise.supply.index.entity.IndexPager;
import com.runwise.supply.index.entity.LocationRequest;
import com.runwise.supply.mine.MsgListActivity;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.runwise.supply.R.id.recyclerView;

/**
 * 首页
 */

public class IndexFragment extends NetWorkFragment implements View.OnClickListener,LoadingLayoutInterface {
    private final int REQUEST_BANNER = 2;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private LayoutInflater layoutInflater;

    private IndexListAdapter indexListAdapter;
    private ConvenientBanner convenientBanner;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;

    @ViewInject(R.id.index_flow_layout)
    private View index_flow_layout;

    @ViewInject(R.id.headEditLayout_flow)
    private View headEditLayout_flow;
    @ViewInject(R.id.headMsgLayout_flow)
    private View headMsgLayout_flow;

    private boolean flowGone = true;

    public BDLocationListener myListener = new MyLocationListener();
    public LocationClient mLocationClient = null;
    private String latitude,longitude;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = LayoutInflater.from(mContext);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData();
            }
        });
        View headView = layoutInflater.inflate(R.layout.index_head_layout,null);

        pullListView.getRefreshableView().addHeaderView(headView);
        indexListAdapter = new IndexListAdapter();
        pullListView.setAdapter(indexListAdapter);
        loadingLayout.setStatusLoading();
        loadingLayout.setOnRetryClickListener(this);
        mLocationClient = new LocationClient(getContext().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=5000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        convenientBanner = (ConvenientBanner) headView.findViewById(R.id.ConvenientBanner);
        int height = GlobalConstant.screenW /2;
        if (height == 0) {
            GlobalConstant.screenW = CommonUtils.getScreenWidth(mContext);
            height = GlobalConstant.screenW /2;
        }
        convenientBanner.getLayoutParams().height = height;

        //设置两个点图片作为翻页指示器
        convenientBanner.setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
        //设置指示器的方向
        convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

        headEditLayout_flow.setOnClickListener(this);
        headMsgLayout_flow.setOnClickListener(this);

        View headMsgLayout = headView.findViewById(R.id.headMsgLayout);
        headMsgLayout.setOnClickListener(this);
        View headEditLayout = headView.findViewById(R.id.headEditLayout);
        headEditLayout.setOnClickListener(this);

        final AlphaAnimation hideAnim = new AlphaAnimation(1.0f,0.0f);
        hideAnim.setDuration(500);
        final AlphaAnimation showAnim = new AlphaAnimation(0f,1.0f);
        showAnim.setDuration(500);
        pullListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0) {
                    if( !flowGone) {
                        index_flow_layout.startAnimation(hideAnim);
                        index_flow_layout.setVisibility(View.GONE);
                        flowGone = true;
                    }
                }
                else if ( firstVisibleItem >= 1 ){
                    if( flowGone ) {
                        index_flow_layout.startAnimation(showAnim);
                        index_flow_layout.setVisibility(View.VISIBLE);
                        flowGone = false;
                    }
                }
            }
        });

    }
    //初始化定位监听
    private void initLocationPoint(){

        mLocationClient.start();
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
            latitude = location.getLatitude()+"";
            longitude = location.getLongitude()+"";
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setLat(latitude);
            locationRequest.setLng(longitude);
            sendConnection("home/list.json",locationRequest,REQUEST_BANNER,false,HomeResult.class);
        }
    }
    private void requestData() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setLat(latitude);
        locationRequest.setLng(longitude);
        sendConnection("home/list.json",locationRequest,REQUEST_BANNER,false,HomeResult.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( convenientBanner != null ) {
            convenientBanner.startTurning(3000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if ( convenientBanner != null ) {
//            convenientBanner.stopTurning();
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( convenientBanner != null ) {
            convenientBanner.stopTurning();
        }
        mLocationClient.stop();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_index;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_BANNER:
                HomeResult pagerList = (HomeResult)result;
                HomeData homeData = pagerList.getData();
                HomeList indexResultList = homeData.getEntities();
                List<IndexPager> indexPagerList = indexResultList.getBanners();
                convenientBanner.setPages(
                        new CBViewHolderCreator<MenuHolderView>() {
                            @Override
                            public MenuHolderView createHolder() {
                                return new MenuHolderView();
                            }
                        }, indexPagerList);
                handlerMainData(indexResultList.getBrands(),indexResultList.getCars(),indexResultList.getDealers());
                loadingLayout.onSuccess(1,"暂时没有数据");
                break;
        }
    }
    private void handlerMainData(List<IndexCarSys> carList,List<IndexCarInfo> cars,List<IndexDealers> dealerList) {
        List<IndexAllListEntity> indexAllList = new ArrayList<>();
        int size;
        if (carList.size() % 2 == 0) {
            size = carList.size() / 2;
        }
        else {
            size = carList.size() / 2 + 1;
        }
        for(int i = 0 ;i < size ; i ++) {
            IndexAllListEntity indexbean = new IndexAllListEntity();
            indexbean.setType(IndexAllListEntity.CAR_SYS);
            int position = i * 2;
            int newPosition = i * 2 + 1;
            IndexCarSys carSys = carList.get(position);
            indexbean.setCarSys1(carSys);
            if(newPosition < carList.size()) {
                IndexCarSys carSys1 = carList.get(newPosition);
                indexbean.setCarSys2(carSys1);
            }
            if(position == carList.size()-1 || newPosition == carList.size()-1) {
                indexbean.setTypeLast(true);
            }
            indexAllList.add(indexbean);
        }

        IndexAllListEntity indexbeanText = new IndexAllListEntity();
        indexbeanText.setType(IndexAllListEntity.CAR_FLOOW);
        indexbeanText.setName("推荐好车");
        indexbeanText.setWhat(IndexAllListEntity.GOTO_CAR);
        indexAllList.add(indexbeanText);

        IndexAllListEntity indexbeanCar = new IndexAllListEntity();
        indexbeanCar.setCarInfoList(cars);
        indexbeanCar.setType(IndexAllListEntity.CAR_INFO);
        indexAllList.add(indexbeanCar);

        IndexAllListEntity indexbeanDealers = new IndexAllListEntity();
        indexbeanDealers.setType(IndexAllListEntity.CAR_FLOOW);
        indexbeanDealers.setName("推荐经销商");
        indexbeanDealers.setWhat(IndexAllListEntity.GOTO_COM);
        indexAllList.add(indexbeanDealers);

        if(dealerList != null)
            for(IndexDealers bean:dealerList) {
                IndexAllListEntity indexbean = new IndexAllListEntity();
                indexbean.setCarDealer(bean);
                indexbean.setType(IndexAllListEntity.CAR_COM);
                indexAllList.add(indexbean);
            }
        indexListAdapter.setData(indexAllList);
        pullListView.onRefreshComplete(indexListAdapter.getCount());

        loadingLayout.setOnRetryClickListener(this);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        loadingLayout.onFailure("",R.drawable.no_network);
        pullListView.onRefreshComplete(indexListAdapter.getCount());
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        requestData();
    }

    public class IndexListAdapter extends IBaseAdapter<IndexAllListEntity> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            IndexAllListEntity indexBean = mList.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null ) {
                viewHolder = new ViewHolder();
                switch (type) {
                    case IndexAllListEntity.CAR_SYS:
                        convertView = View.inflate(mContext, R.layout.layout_indextype_sys, null);
                        viewHolder.carSysRootLayout = (LinearLayout) convertView.findViewById(R.id.carSysRootLayout);
                        if(indexBean.isTypeLast()) {
                            ((LinearLayout.LayoutParams)viewHolder.carSysRootLayout.getLayoutParams()).bottomMargin = CommonUtils.dip2px(mContext,10);
                        }
                        else {
                            ((LinearLayout.LayoutParams)viewHolder.carSysRootLayout.getLayoutParams()).bottomMargin = 0;
                        }
                        viewHolder.carSysLayout1 =  convertView.findViewById(R.id.carSysLayout1);
                        viewHolder.carSysName1 = (TextView) convertView.findViewById(R.id.carSysName1);
                        viewHolder.carSysIcon1 = (SimpleDraweeView) convertView.findViewById(R.id.carSysIcon1);

                        viewHolder.carSysLayout2 =  convertView.findViewById(R.id.carSysLayout2);
                        viewHolder.carSysName2 = (TextView) convertView.findViewById(R.id.carSysName2);
                        viewHolder.carSysIcon2 = (SimpleDraweeView) convertView.findViewById(R.id.carSysIcon2);
                        break;
                    case IndexAllListEntity.CAR_INFO:
                        convertView = View.inflate(mContext, R.layout.layout_indextype_hview, null);
                        CarInfoViewAdapter adapter = new CarInfoViewAdapter(indexBean);
                        viewHolder.recyclerView = (RecyclerView) convertView.findViewById(recyclerView);
                        viewHolder.carInfoViewAdapter = adapter;
                        viewHolder.recyclerView.getLayoutParams().height = (int)(0.81* GlobalConstant.screenW);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        viewHolder.recyclerView.setLayoutManager(layoutManager);
                        viewHolder.recyclerView.setHasFixedSize(true);
                        viewHolder.recyclerView.setAdapter(adapter);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        break;
                    case IndexAllListEntity.CAR_COM:
                        convertView = View.inflate(mContext, R.layout.layout_indextype_com, null);
                        viewHolder.dealerName = (TextView) convertView.findViewById(R.id.dealerName);
                        viewHolder.dealerKm = (TextView) convertView.findViewById(R.id.dealerKm);
                        viewHolder.dealerRatingbar = (RatingBar) convertView.findViewById(R.id.dealerRatingbar);
                        viewHolder.dealerRatingscore = (TextView) convertView.findViewById(R.id.dealerRatingscore);
                        viewHolder.dealerAddress = (TextView) convertView.findViewById(R.id.dealerAddress);
                        viewHolder.dealerCell = (TextView) convertView.findViewById(R.id.dealerCell);
                        break;
                    case IndexAllListEntity.CAR_FLOOW:
                        convertView = View.inflate(mContext, R.layout.layout_indextype_more, null);
                        viewHolder.moreName = (TextView) convertView.findViewById(R.id.moreName);
                        viewHolder.moreLayout = convertView.findViewById(R.id.moreLayout);
                        break;
                }
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            switch (type) {
                case IndexAllListEntity.CAR_SYS:
                    IndexCarSys carSys1 = indexBean.getCarSys1();
//                    viewHolder.carSysName1.setText(carSys1.getName());
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.carSysIcon1,carSys1.getLogo());

                    IndexCarSys carSys2 = indexBean.getCarSys2();
                    if( carSys2 != null ) {
                        viewHolder.carSysLayout2.setVisibility(View.VISIBLE);
//                        viewHolder.carSysName2.setText(carSys2.getName());
                        FrecoFactory.getInstance(mContext).disPlay(viewHolder.carSysIcon2, carSys2.getLogo());
                    }
                    else {
                        viewHolder.carSysLayout2.setVisibility(View.INVISIBLE);
                    }
//                    switch (position % 4){
//                        case 0:
//                            viewHolder.carSysLayout1.setBackgroundColor(Color.parseColor("#ff3e3a"));
//                            viewHolder.carSysLayout2.setBackgroundColor(Color.parseColor("#ff9803"));
//                            break;
//                        case 1:
//                            viewHolder.carSysLayout1.setBackgroundColor(Color.parseColor("#02e360"));
//                            viewHolder.carSysLayout2.setBackgroundColor(Color.parseColor("#00afff"));
//                            break;
//                        case 2:
//                            viewHolder.carSysLayout1.setBackgroundColor(Color.parseColor("#ff3e3a"));
//                            viewHolder.carSysLayout2.setBackgroundColor(Color.parseColor("#ff9803"));
//                            break;
//                        case 3:
//                            viewHolder.carSysLayout1.setBackgroundColor(Color.parseColor("#02e360"));
//                            viewHolder.carSysLayout2.setBackgroundColor(Color.parseColor("#00afff"));
//                            break;
//                    }
                    viewHolder.carSysLayout1.setOnClickListener(new IndexMenuClickListener(position,indexBean,0));
                    viewHolder.carSysLayout2.setOnClickListener(new IndexMenuClickListener(position,indexBean,1));
                    break;
                case IndexAllListEntity.CAR_INFO:
                    viewHolder.carInfoViewAdapter.setData(indexBean.getCarInfoList());
                    break;
                case IndexAllListEntity.CAR_COM:
                    IndexDealers dealer = indexBean.getCarDealer();
                    viewHolder.dealerName.setText(dealer.getDealer_name());
                    if (dealer.getAttribute() != null && dealer.getAttribute().getAttribute_id() == 1){
                        Drawable s4 = ContextCompat.getDrawable(mContext,R.drawable.s4);
                        s4.setBounds(0, 0, s4.getMinimumWidth(), s4.getMinimumHeight());
                        viewHolder.dealerName.setCompoundDrawables(null,null,s4,null);
                    }else {
                        //viewHolder.shopType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.comprehensive));
                        Drawable comprehensive = ContextCompat.getDrawable(mContext,R.drawable.comprehensive);
                        comprehensive.setBounds(0, 0, comprehensive.getMinimumWidth(), comprehensive.getMinimumHeight());
                        viewHolder.dealerName.setCompoundDrawables(null,null,comprehensive,null);
                    }
                    viewHolder.dealerKm.setText(dealer.getDistance());
                    viewHolder.dealerRatingbar.setRating(dealer.getScore());
                    viewHolder.dealerRatingscore.setText(dealer.getScore()+"分");
                    viewHolder.dealerAddress.setText(dealer.getAddress());
                    viewHolder.dealerCell.setOnClickListener(new IndexMenuClickListener(position,indexBean,0));
                    convertView.setOnClickListener(new IndexMenuClickListener(position,indexBean,0));
                    break;
                case IndexAllListEntity.CAR_FLOOW:
                    viewHolder.moreName.setText(indexBean.getName());
                    viewHolder.moreLayout.setOnClickListener(new IndexMenuClickListener(position,indexBean,0));
                    //推荐好车,更多
                    if(IndexAllListEntity.GOTO_CAR == indexBean.getWhat()) {
                        viewHolder.moreLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        viewHolder.moreLayout.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
            return convertView;
        }
        class ViewHolder{
            LinearLayout carSysRootLayout;
            View carSysLayout1;
            TextView carSysName1;
            SimpleDraweeView carSysIcon1;
            View carSysLayout2;
            TextView carSysName2;
            SimpleDraweeView carSysIcon2;

            RecyclerView recyclerView;
            CarInfoViewAdapter carInfoViewAdapter;

            TextView dealerName;
            TextView dealerKm;
            RatingBar dealerRatingbar;
            TextView dealerRatingscore;
            TextView dealerAddress;
            TextView dealerCell;

            TextView moreName;
            View moreLayout;
        }

        /**
         * 获取item类型数
         */
        public int getViewTypeCount() {
            return 4;
        }

        /**
         * 获取item类型
         */
        @Override
        public int getItemViewType(int position) {
            IndexAllListEntity activity = mList.get(position);
            return activity.getType();
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }


    private class MenuHolderView implements Holder<IndexPager> {
        SimpleDraweeView simpleDraweeView;
        @Override
        public View createView(Context context) {
            View view = layoutInflater.inflate(R.layout.item_pager_layout,null);
            simpleDraweeView = (SimpleDraweeView)view.findViewById(R.id.simpleDraweeView);
            return view;
        }
        @Override
        public void UpdateUI(Context context, int position,final IndexPager data) {
            FrecoFactory.getInstance(mContext).disPlay(simpleDraweeView,data.getImage_path());
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if("1".equals(data.getUrl_type())) {
                        Intent intent = new Intent(mContext,CarSeriesActivity.class);
                        intent.putExtra("series_id",data.getApi_param().getSeries_id());
                        startActivity(intent);
                    }
                    else if("2".equals(data.getUrl_type())) {
                        Intent intent = new Intent(getContext(),CarSettingFragmentContainer.class);
                        intent.putExtra("carid",data.getApi_param().getCar_id());
                        startActivity(intent);

                    }
                    else if("3".equals(data.getUrl_type())) {
                        Intent intent = new Intent(mContext,DealerDetainActivity.class);
                        intent.putExtra("dealer_id",data.getApi_param().getDealer_id());
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(mContext, IWebViewActivity.class);
                        intent.putExtra(WebViewActivity.WEB_TITLE,data.getTitle());
                        intent.putExtra(WebViewActivity.WEB_URL,data.getUrl_link());
                        startActivity(intent);
                    }

                }
            });
        }
    }

    private class CarInfoViewAdapter extends RecyclerView.Adapter{
        private IndexAllListEntity indexAllListEntity;
        private List<IndexCarInfo> carList = new ArrayList<>();
        public CarInfoViewAdapter (IndexAllListEntity indexAllListEntity) {
            this.indexAllListEntity = indexAllListEntity;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_indextype_car,parent,false);
            View parentLayout = view.findViewById(R.id.parentLayout);
            parentLayout.getLayoutParams().width = (int)(0.41* GlobalConstant.screenW);
            ViewHolder vh = new ViewHolder(view,parentLayout);
            return vh;
        }

        public void setData(List<IndexCarInfo> carList) {
            this.carList.clear();
            this.carList.addAll(carList);
            this.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = ((ViewHolder)holder);
            final IndexCarInfo carInfo = carList.get(position);
            CarImage carImage = carInfo.getImage();
            if( carImage != null ) {
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.carPic, carImage.getImg_path());
            }
            viewHolder.carName.setText(carInfo.getTitle());
            CarPeriod perIod = carInfo.getPeriod();

            if( perIod != null) {
                HashMap<String,String> spMap = CommonUtils.formatMoney(perIod.getFirst_period());
                String firstStr = "首付:" + spMap.get(CommonUtils.MONEY_VALUE) + spMap.get(CommonUtils.MONEY_UNIT)+" 月供:";

                HashMap<String,String> mMap = CommonUtils.formatMoney(perIod.getMonth_period());
                String twoStr = mMap.get(CommonUtils.MONEY_VALUE) + mMap.get(CommonUtils.MONEY_UNIT);

                SpannableString spannableString1 = new SpannableString(firstStr+twoStr);
                spannableString1.setSpan(new AbsoluteSizeSpan(14,true), 3, 3 + spMap.get(CommonUtils.MONEY_VALUE).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                int starIndex = firstStr.length();
                spannableString1.setSpan(new AbsoluteSizeSpan(14,true), starIndex, starIndex + mMap.get(CommonUtils.MONEY_VALUE).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.carPrice.setText(spannableString1);
            }
            HashMap<String,String> spMap = CommonUtils.formatMoney(carInfo.getSale_price());
            viewHolder.carRealPrice.setText(spMap.get(CommonUtils.MONEY_VALUE) + spMap.get(CommonUtils.MONEY_UNIT));
            viewHolder.parentLayout.setOnClickListener(new IndexMenuClickListener(position,indexAllListEntity,0));
            viewHolder.carDoit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,SelectDealerActivity.class);
                    intent.putExtra("carId",String.valueOf(carInfo.getCar_id()));
                    if (UserUtils.checkLogin(intent,mContext)) {
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return carList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public SimpleDraweeView carPic;
            public TextView carName;
            public TextView carPrice;
            public TextView carRealPrice;
            public TextView carDoit;
            public View parentLayout;
            public ViewHolder(View itemView,View parentLayout){
                super(itemView);
                carPic = (SimpleDraweeView) itemView.findViewById(R.id.carPic);
                carName = (TextView) itemView.findViewById(R.id.carName);
                carPrice = (TextView) itemView.findViewById(R.id.carPrice);
                carRealPrice = (TextView) itemView.findViewById(R.id.carRealPrice);
                carName = (TextView) itemView.findViewById(R.id.carName);
                carDoit = (TextView) itemView.findViewById(R.id.carDoit);
                this.parentLayout = parentLayout;
            }
        }
    }
    /**
     * 首页各个项目点击事件处理
     */
    private class IndexMenuClickListener implements  View.OnClickListener{
        private int position,childIndex;
        private IndexAllListEntity indexBean;
        public IndexMenuClickListener(int position,IndexAllListEntity indexBean,int childIndex) {
            this.position = position;
            this.childIndex = childIndex;
            this.indexBean = indexBean;
        }
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (indexBean.getType()) {
                //车系
                case IndexAllListEntity.CAR_SYS:
                    IndexCarSys carSys;
                    if( 0 == childIndex) {
                        carSys = indexBean.getCarSys1();
                    }
                    else {
                        carSys = indexBean.getCarSys2();
                    }
                    intent = new Intent(mContext,CarSeriesActivity.class);
                    intent.putExtra("series_id",String.valueOf(carSys.getSeries_id()));
                    startActivity(intent);
                    break;
                case IndexAllListEntity.CAR_FLOOW:
                    //推荐好车,更多
                    if(IndexAllListEntity.GOTO_CAR == indexBean.getWhat()) {
                        intent = new Intent(mContext, MoreCarListActivity.class);
                        startActivity(intent);
                    }
                    //推荐经销商,更多
                    else {

                    }
                    break;
                //推荐经销商
                case IndexAllListEntity.CAR_COM:
                    IndexDealers dealers = indexBean.getCarDealer();
                    intent = new Intent(mContext,DealerDetainActivity.class);
                    intent.putExtra("dealer_id",String.valueOf(dealers.getDealer_id()));
                    startActivity(intent);
                    break;
                //推荐好车
                case IndexAllListEntity.CAR_INFO:
                    List<IndexCarInfo> carInfos = indexBean.getCarInfoList();
                    IndexCarInfo carInfo = carInfos.get(position);
                    intent = new Intent(getContext(),CarSettingFragmentContainer.class);
                    intent.putExtra("carid",carInfo.getCar_id());
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //消息
            case R.id.headMsgLayout_flow:
            case R.id.headMsgLayout:
                Intent intent = new Intent(mContext, MsgListActivity.class);
                if (UserUtils.checkLogin(intent,mContext)) {
                    startActivity(intent);
                }
                break;
            //搜索
            case R.id.headEditLayout_flow:
            case R.id.headEditLayout:
                startActivity(new Intent(mContext, DealerSearchActivity.class));
                break;
        }
    }
}
