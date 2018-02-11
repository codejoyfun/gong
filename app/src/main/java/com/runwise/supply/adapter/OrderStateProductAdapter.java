package com.runwise.supply.adapter;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.tools.UserUtils;

import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/1/16.
 */

public class OrderStateProductAdapter extends IBaseAdapter<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> {



    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetail_list_item, parent, false);
            vh = new ViewHolder(convertView);
            vh.oldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        final OrderResponse.ListBean.ProductAlteredBean.AlterProductBean bean = mList.get(position);

        if (!TextUtils.isEmpty(bean.getImageMedium())) {
            FrecoFactory.getInstance(parent.getContext()).displayWithoutHost(vh.productImage, bean.getImageMedium());
        }

        double puq = bean.getOriginNum();
        double dq = bean.getAlterNum();
//            实收
        vh.oldPriceTv.setText("x" + NumberUtil.getIOrD(puq));
        vh.oldPriceTv.setVisibility(View.VISIBLE);
        vh.nowPriceTv.setText("x" + NumberUtil.getIOrD(dq));

        vh.name.setText(bean.getName());
        StringBuffer sb = new StringBuffer(bean.getDefaultCode());
        sb.append(" | ").append(bean.getUnit());
                sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getPrice()))).append("元/").append(bean.getUom());
        vh.unit1.setText(bean.getUom());
        vh.content.setText(sb.toString());
            vh.weightTv.setVisibility(View.INVISIBLE);

        return convertView;
    }

    public class ViewHolder {
        public SimpleDraweeView productImage;
        public TextView name;
        public TextView content;
        public TextView oldPriceTv;
        public TextView nowPriceTv;
        public TextView weightTv;
        public TextView unit1;
        public View rootView;

        public ViewHolder(View itemView) {
            rootView = itemView;
            productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView) itemView.findViewById(R.id.content);
            oldPriceTv = (TextView) itemView.findViewById(R.id.oldPriceTv);
            nowPriceTv = (TextView) itemView.findViewById(R.id.nowPriceTv);
            weightTv = (TextView) itemView.findViewById(R.id.weightTv);
            unit1 = (TextView) itemView.findViewById(R.id.unit1);
        }
    }
}
