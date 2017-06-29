package com.runwise.supply.firstpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.NewsResponse;

import java.io.File;

/**
 * Created by libin on 2017/6/28.
 */

public class NewsAdapter extends IBaseAdapter {
    private Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.unlogin_list_item, null);
            ViewUtils.inject(viewHolder,convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        NewsResponse.ListBean bean = (NewsResponse.ListBean)getItem(position);
        viewHolder.titleTv.setText(bean.getTitle());
        FrecoFactory.getInstance(context).disPlay(viewHolder.imgIv,Constant.BASE_URL + bean.getAvatorUrl());
        viewHolder.fromTv.setText(bean.getTagsList().toString());
        return convertView;
    }

    public class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.imageIv)
        SimpleDraweeView imgIv;
        @ViewInject(R.id.fromTv)
        TextView fromTv;
    }
}
