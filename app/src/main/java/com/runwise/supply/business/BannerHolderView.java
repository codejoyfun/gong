package com.runwise.supply.business;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bigkoo.convenientbanner.holder.Holder;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.view.MySimpleDraweeView;

/**
 * Created by libin on 2017/1/13.
 */

public class BannerHolderView implements Holder<ImagesBean> {
    MySimpleDraweeView simpleDraweeView;
    private LayoutInflater layoutInflater;
    @Override
    public View createView(Context context) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.car_banner_item,null);
        simpleDraweeView = (MySimpleDraweeView)view.findViewById(R.id.simpleDraweeView);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position,final ImagesBean bean) {
        FrecoFactory.getInstance(context).disPlay(simpleDraweeView, Constant.BASE_URL+bean.getCover_url());
    }
}
