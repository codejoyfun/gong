package com.runwise.supply.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.LauncherActivity;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.CarSeriesResponse;
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.business.entity.SeriesRequest;
import com.runwise.supply.message.DividePayStep1Activity;
import com.runwise.supply.tools.ShareUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by libin on 2017/1/13.
 */

public class CarSeriesActivity extends NetWorkActivity {
    private static final String LogoUrl = "http://114.215.40.244:8085/images/icon-logo.png";
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private LayoutInflater layoutInflater;
    private CarAdapter carAdapter;
    private ConvenientBanner banner;
    private TextView pageIndicator;
    private View headView;
    private String shareUrl;

    private String carTitle;
    private Boolean isWebFrom;          //true的话，定制回退键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.dealer_detain_layout);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)){
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }
        setTitleText(true,"车系详情");
        setTitleRigthIcon(true, R.drawable.partook);
        setTitleLeftIcon(true, R.drawable.returned);
        isWebFrom = getIntent().getBooleanExtra("isWebFrom",false);
        initViews();
        requestData();
    }
    private void initViews() {
        pullListView.setVisibility(View.INVISIBLE);
        carAdapter = new CarAdapter();
        pullListView.setAdapter(carAdapter);
        layoutInflater = LayoutInflater.from(mContext);
        headView = layoutInflater.inflate(R.layout.carseries_head_layout, null);
        banner = (ConvenientBanner) headView.findViewById(R.id.banner);
        //通过图片比例，计算banner大小 750:500 = w:x
        int height = 500 * CommonUtils.getScreenWidth(this) / 750;
        banner.getLayoutParams().height = height;
        pageIndicator = (TextView) headView.findViewById(R.id.textview);
        pageIndicator.setText("...");
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullListView.getRefreshableView().addHeaderView(headView);
    }
    //从上页传进车系id,发起请求
    private void requestData(){
        String series_id = getIntent().getStringExtra("series_id");
        SeriesRequest request = new SeriesRequest(series_id);
        sendConnection("/series/detail.json",request,1000,true, CarSeriesResponse.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        CarSeriesResponse response = (CarSeriesResponse)result;
        if (response == null || response.getData() == null  || response.getData().getEntity() == null){
            ToastUtil.show(mContext,"没有该车系信息");
        }else{
            pullListView.setVisibility(View.VISIBLE);
            updateUIByData(response);
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        pullListView.setVisibility(View.INVISIBLE);
        ToastUtil.show(mContext,"请求异常，请稍侯重试");
    }

    //解析请求返回的数据
    private void updateUIByData(CarSeriesResponse response){
        //轮播图
        CarSeriesResponse.DataBean.EntityBean bean = response.getData().getEntity();
        if (bean == null){
            return;
        }
        shareUrl = bean.getShare_url();
        final List<ImagesBean> images = bean.getImages();
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
        carTitle = bean.getTitle();
        updateHeaderInfo(bean);
        //更新列表,CarSeriesResponse.DataBean.EntitiesBean
        if (response.getData() != null && response.getData().getEntities() != null){
            carAdapter.setData(response.getData().getEntities());
        }

    }
    private void updateHeaderInfo(CarSeriesResponse.DataBean.EntityBean bean){
        ((TextView)headView.findViewById(R.id.carname)).setText(bean.getTitle());
        ((TextView)headView.findViewById(R.id.guideprice)).setText(bean.getSale_price());
    }
    @OnClick({R.id.title_iv_left, R.id.title_iv_rigth})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_rigth:
                ShareUtil.showShare(mContext,"车系-详情",carTitle,LogoUrl,shareUrl);
                break;
            case R.id.title_iv_left:
                if (isWebFrom){
                    finish();
                    startActivity(new Intent(this, LauncherActivity.class));
                    return;
                }
                finish();
                break;
            default:
                break;
        }
    }
    public class CarAdapter extends IBaseAdapter {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.carseries_car_item, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.carseriesname);
                viewHolder.firstPeriod = (TextView) convertView.findViewById(R.id.firstPeriod);
                viewHolder.monthPeriod = (TextView) convertView.findViewById(R.id.monthPeriod);
                viewHolder.guidePrice = (TextView) convertView.findViewById(R.id.pricevalue);
                viewHolder.applyBtn = (Button) convertView.findViewById(R.id.applybtn);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //进行赋值操作
            final CarSeriesResponse.DataBean.EntitiesBean bean = (CarSeriesResponse.DataBean.EntitiesBean)mList.get(position);
            //解决字体大小不一
            HashMap<String,String> fpMap;
            HashMap<String,String> mpMap;
            if (bean != null && bean.getPeriod() != null){
               fpMap = CommonUtils.formatMoney(bean.getPeriod().getFirst_period());
               mpMap = CommonUtils.formatMoney(bean.getPeriod().getMonth_period());
            }else{
                fpMap = CommonUtils.formatMoney(null);
                mpMap = CommonUtils.formatMoney(null);
            }
            HashMap<String,String> spMap = CommonUtils.formatMoney(bean.getSale_price());
            StringBuffer s1 =  new StringBuffer();
            s1.append("首付:").append(fpMap.get(CommonUtils.MONEY_VALUE)).append(fpMap.get(CommonUtils.MONEY_UNIT));
            StringBuffer s2 = new StringBuffer();
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
            viewHolder.name.setText(bean.getTitle());
            viewHolder.guidePrice.setText(spMap.get(CommonUtils.MONEY_VALUE)+spMap.get(CommonUtils.MONEY_UNIT));
            //动作
            viewHolder.applyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转
                    Log.i("tag","跳转到个人申请页面");
                    if (UserUtils.checkLogin(null,CarSeriesActivity.this)) {
                        //先统一改为选择经销商
                        Intent intent = new Intent(mContext,SelectDealerActivity.class);
                        intent.putExtra("carId",String.valueOf(bean.getCar_id()));
                        startActivity(intent);
//                        Intent intent = new Intent(mContext, DividePayStep1Activity.class);
//                        intent.putExtra("dealerId",bean.getDealer_id());
//                        intent.putExtra("carId",bean.getCar_id());
//                        startActivity(intent);
                    };
                }
            });

            return convertView;
        }

        class ViewHolder {

            TextView name;              //名称
            TextView firstPeriod;       //首付
            TextView monthPeriod;       //月付
            TextView guidePrice;    //指导价
            Button   applyBtn;      //申请
        }
    }
}
