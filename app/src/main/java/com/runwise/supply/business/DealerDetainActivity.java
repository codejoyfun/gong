package com.runwise.supply.business;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.DealerDetailRequest;
import com.runwise.supply.business.entity.DealerDetainResponse;
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.tools.AndroidWorkaround;
import com.runwise.supply.tools.ShareUtil;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2017/1/9.
 */

public class DealerDetainActivity extends NetWorkActivity implements AdapterView.OnItemClickListener{
    private static final String LogoUrl = "http://114.215.40.244:8085/images/icon-logo.png";
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView   pullListView;
    private LayoutInflater          layoutInflater;
    private CarAdapter              carAdapter;
    private ConvenientBanner        banner;
    private TextView                pageIndicator;
    private List<DealerDetainResponse.DataBean.EntitiesBean> carsList;
    private RelativeLayout          headerDealerInfo;
    private String                  dealerName;         //经销商名称
    private String                  dealerPhone;        //经销商电话
    private double                  dealerLat;
    private double                  dealerLng;          //经纬度
    private String                  shareUrl;           //分享链接
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dealer_detain_layout);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)){
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }
        StatusBarUtil.StatusBarLightMode(this);
        setTitleText(true,"经销商详情");
        setTitleLeftIcon(true,R.drawable.returned);
        setTitleRightIcon2(true,R.drawable.distributor_map);
        setTitleRigthIcon(true,R.drawable.partook);
        setTitleTextColor(RIGHTTEXT,"#333333");
        initViews();
        //跳转进来，需要带经销商ID
        requestData();
    }
    //从上个页面传来的id，发起网络请求
    private void requestData(){
        String dealerId = getIntent().getStringExtra("dealer_id");
        DealerDetailRequest request = new DealerDetailRequest(dealerId);
        sendConnection("/dealers/detail.json",request,1000,true, DealerDetainResponse.class);
    }
    private void initViews(){
        pullListView.setVisibility(View.INVISIBLE);
        carsList = new ArrayList<DealerDetainResponse.DataBean.EntitiesBean>();
        carAdapter = new CarAdapter();
        carAdapter.setData(carsList);
        pullListView.setAdapter(carAdapter);
        layoutInflater = LayoutInflater.from(mContext);
        View headView = layoutInflater.inflate(R.layout.dealer_head_layout,null);
        TextView km = (TextView) headView.findViewById(R.id.km);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("distance"))){
            km.setText(getIntent().getStringExtra("distance"));
        }
        banner = (ConvenientBanner)headView.findViewById(R.id.banner);
        //通过图片比例，计算banner大小 750:500 = w:x
        int height = 500*CommonUtils.getScreenWidth(this)/750;
        banner.getLayoutParams().height = height;
        pageIndicator = (TextView)headView.findViewById(R.id.textview);
        pageIndicator.setText("...");
        headerDealerInfo = (RelativeLayout)headView.findViewById(R.id.dealerItem);

        Button callBtn = (Button) headView.findViewById(R.id.callbtn);
        callBtn.setVisibility(View.VISIBLE);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dealerPhone != null){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + dealerPhone);
                    intent.setData(data);
                    startActivity(intent);
                }
            }
        });
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullListView.getRefreshableView().addHeaderView(headView);
        //item点击事件
        pullListView.setOnItemClickListener(this);

    }

    @OnClick({R.id.title_iv_left,R.id.title_iv_rigth,R.id.title_iv_rigth2})
    public void mapClick(View view){
        switch(view.getId()){
            case R.id.title_iv_rigth2:
                //跳转到地图的页面
                Intent intent = new Intent(mContext, DealerMapActivity.class);
                intent.putExtra("lat",dealerLat);
                intent.putExtra("lng",dealerLng);
                intent.putExtra("name",dealerName);
                startActivity(intent);
                break;
            case R.id.title_iv_rigth:
                ShareUtil.showShare(mContext,"经销商-详情",dealerName,LogoUrl,shareUrl);
                break;
            case R.id.title_iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        DealerDetainResponse response = (DealerDetainResponse)result;
        if (response == null || response.getData() == null || response.getData().getEntity() == null){
            ToastUtil.show(mContext,"没有该经销商信息");
        }else{
            pullListView.setVisibility(View.VISIBLE);
            updateUIByData(response);
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DealerDetainResponse.DataBean.EntitiesBean bean = (DealerDetainResponse.DataBean.EntitiesBean)adapterView.getAdapter().getItem(i);
        Intent intent = new Intent(mContext,CarSeriesActivity.class);
        String seriesId = String.valueOf(bean.getSeries_id());
        intent.putExtra("series_id",seriesId);
        startActivity(intent);
    }

    public class CarAdapter extends IBaseAdapter{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext,R.layout.detain_car_item,null);
                viewHolder.icon = (SimpleDraweeView) convertView.findViewById(R.id.icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.firstPeriod = (TextView)convertView.findViewById(R.id.firstPeriod);
                viewHolder.monthPeriod = (TextView)convertView.findViewById(R.id.monthPeriod);
                viewHolder.guidePrice = (TextView)convertView.findViewById(R.id.guidePrice);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            //TODO:进行赋值操作
            DealerDetainResponse.DataBean.EntitiesBean bean = carsList.get(position);
            //解决字体大小不一
            StringBuffer s1 =  new StringBuffer();
            HashMap<String,String> fpMap;
            if (bean.getPeriod() != null && bean.getPeriod().getFirst_period() != null){
                fpMap = CommonUtils.formatMoney(bean.getPeriod().getFirst_period());
            }else{
                fpMap = CommonUtils.formatMoney(null);
            }

            s1.append("首付:").append(fpMap.get(CommonUtils.MONEY_VALUE)).append(fpMap.get(CommonUtils.MONEY_UNIT));
            StringBuffer s2 = new StringBuffer();
            HashMap<String,String> mpMap;
            if (bean.getPeriod() != null && bean.getPeriod().getMonth_period() != null){
                mpMap = CommonUtils.formatMoney(bean.getPeriod().getMonth_period());
            }else {
                mpMap = CommonUtils.formatMoney(null);
            }
            s2.append("月供:").append(mpMap.get(CommonUtils.MONEY_VALUE)).append(mpMap.get(CommonUtils.MONEY_UNIT));
            int s1StartIndex = s1.indexOf(":");
            int s1LastIndex = s1.lastIndexOf(fpMap.get(CommonUtils.MONEY_UNIT));
            int s2StartIndex = s2.indexOf(":");
            int s2LastIndex = s2.lastIndexOf(mpMap.get(CommonUtils.MONEY_UNIT));
            SpannableString ss = new SpannableString(s1);
            SpannableString ss2 = new SpannableString(s2);
            ss.setSpan(new AbsoluteSizeSpan(15, true), s1StartIndex, s1LastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体大小
            ss2.setSpan(new AbsoluteSizeSpan(15, true), s2StartIndex, s2LastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.firstPeriod.setText(ss);
            viewHolder.monthPeriod.setText(ss2);
            FrecoFactory.getInstance(mContext).disPlay(viewHolder.icon,"");
            viewHolder.name.setText(bean.getTitle());
            HashMap<String,String> spMap = CommonUtils.formatMoney(bean.getSale_price());
            viewHolder.guidePrice.setText(spMap.get(CommonUtils.MONEY_VALUE)+spMap.get(CommonUtils.MONEY_UNIT));

            return convertView;
        }

        class ViewHolder{
            SimpleDraweeView icon;
            TextView name;
            TextView firstPeriod;
            TextView monthPeriod;
            TextView guidePrice;
        }
    }

    //解析请求返回的数据
    private void updateUIByData(DealerDetainResponse response){
        //轮播图
        DealerDetainResponse.DataBean.EntityBean dealerBean = response.getData().getEntity();
        dealerPhone = dealerBean.getContact_phone();
        shareUrl = dealerBean.getShare_url();
        final List<ImagesBean> images = dealerBean.getImages();
        if (images != null && images.size() > 0){
            pageIndicator.setText("1/"+images.size());
            banner.setPages(new CBViewHolderCreator<BannerHolderView>() {
                @Override
                public BannerHolderView createHolder() {
                    return new BannerHolderView();
                }
            }, images).setPointViewVisible(false)
                    .startTurning(5000)
                    .setManualPageable(true);  //设置手动影响;
            banner.setCanLoop(true);
            banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    pageIndicator.setText(position+1 +"/"+images.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        //头部经销商信息
        updateHeaderInfo(dealerBean);

        //更新列表
        if(response.getData().getEntities() != null && response.getData().getEntities().size() > 0) {
            carsList.addAll(response.getData().getEntities());
        }
        carAdapter.setData(carsList);
    }
    private void updateHeaderInfo(DealerDetainResponse.DataBean.EntityBean bean){
        TextView nameTv = (TextView) headerDealerInfo.findViewById(R.id.dealername);
        nameTv.setText(bean.getDealer_name());
        //4s或者综合图标
        if (bean.getAttribute_id() == 1){
            Drawable s4 = ContextCompat.getDrawable(mContext,R.drawable.s4);
            s4.setBounds(0, 0, s4.getMinimumWidth(), s4.getMinimumHeight());
            nameTv.setCompoundDrawables(null,null,s4,null);
        }else {
            Drawable comprehensive = ContextCompat.getDrawable(mContext,R.drawable.comprehensive);
            comprehensive.setBounds(0, 0, comprehensive.getMinimumWidth(), comprehensive.getMinimumHeight());
            nameTv.setCompoundDrawables(null,null,comprehensive,null);
        }
        TextView addressTv = (TextView) headerDealerInfo.findViewById(R.id.address);
        addressTv.setText(bean.getAddress());
        RatingBar bar = (RatingBar) headerDealerInfo.findViewById(R.id.ratingbar);
        bar.setRating(bean.getScore());
        TextView scoreTv = (TextView)headerDealerInfo.findViewById(R.id.ratingscore);
        scoreTv.setText(bean.getScore()+"分");
//        TextView kmTv = (TextView)headerDealerInfo.findViewById(R.id.km);
        dealerPhone = bean.getContact_phone();
        dealerLat = Double.parseDouble(bean.getLat());
        dealerLng = Double.parseDouble(bean.getLng());
        dealerName = bean.getDealer_name();
    }
}