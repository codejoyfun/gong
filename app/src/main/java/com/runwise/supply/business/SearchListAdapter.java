package com.runwise.supply.business;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.SlidingLayer;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.SearchResponse;

/**
 * Created by libin on 2017/1/6.
 */

public class SearchListAdapter extends IBaseAdapter{
    private BaseActivity context;
    public SearchListAdapter(BaseActivity context) {
        this.context = context;
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_layout_item, null);
            //viewHolder.shopType = (ImageView)convertView.findViewById(R.id.shoptype);
            ViewUtils.inject(viewHolder,convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置值
        SearchResponse.DataBean.EntitiesBean bean = (SearchResponse.DataBean.EntitiesBean)getItem(position);
        viewHolder.title.setText(bean.getDealer_name());
        viewHolder.scroe.setText(bean.getScore()+"分");
        viewHolder.ratingBar.setRating(bean.getScore());
        viewHolder.address.setText(bean.getAddress());
        viewHolder.title.setText(bean.getDealer_name());
        if (bean.getAttribute() != null && bean.getAttribute().getAttribute_id() == 1){
            //viewHolder.shopType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.s4));
            Drawable s4 = ContextCompat.getDrawable(context,R.drawable.s4);
            s4.setBounds(0, 0, s4.getMinimumWidth(), s4.getMinimumHeight());
            viewHolder.title.setCompoundDrawables(null,null,s4,null);
        }else {
            //viewHolder.shopType.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.comprehensive));
            Drawable comprehensive = ContextCompat.getDrawable(context,R.drawable.comprehensive);
            comprehensive.setBounds(0, 0, comprehensive.getMinimumWidth(), comprehensive.getMinimumHeight());
            viewHolder.title.setCompoundDrawables(null,null,comprehensive,null);
        }
        return convertView;
    }

    public class ViewHolder {
        @ViewInject(R.id.dealername)
        TextView    title;
        @ViewInject(R.id.ratingbar)
        RatingBar   ratingBar;
        @ViewInject(R.id.ratingscore)
        TextView    scroe;
        @ViewInject(R.id.address)
        TextView    address;
        //ImageView   shopType;
        @ViewInject(R.id.km)
        TextView    km;
    }
}
