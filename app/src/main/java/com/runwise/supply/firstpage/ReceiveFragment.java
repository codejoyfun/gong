package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.ProductListFragment;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;

/**
 * Created by libin on 2017/7/16.
 */

public class ReceiveFragment extends BaseFragment {
    public DataType type;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ReceiveAdapter adapter;
    //跟自己类型配对的数据即可。
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ReceiveAdapter();
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);
        ArrayList<OrderResponse.ListBean.LinesBean> linesList = getArguments().getParcelableArrayList("datas");
        if (type == DataType.ALL){
            datas.addAll(linesList);
        }else {
            for (OrderResponse.ListBean.LinesBean bean : linesList){
                if (bean.getStockType().equals(type.getType())){
                    datas.add(bean);
                }
            }
        }
        adapter.setData(datas);

    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.reveive_fragment;
    }

    public class ReceiveAdapter extends IBaseAdapter {

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.receive_list_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            String pId = String.valueOf(bean.getProductID());
            ProductBasicList.ListBean basicBean= ProductBasicUtils.getBasicMap().get(pId);
            if (basicBean != null){
                viewHolder.name.setText(basicBean.getName());
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sdv, Constant.BASE_URL+basicBean.getImage().getImageSmall());
            }
            return convertView;
        }
        class ViewHolder{
            @ViewInject(R.id.productImage)
            SimpleDraweeView sdv;
            @ViewInject(R.id.name)
            TextView    name;
            @ViewInject(R.id.content)
            TextView    content;
            @ViewInject(R.id.countTv)
            TextView    countTv;
            @ViewInject(R.id.receivedTv)
            TextView    receivedTv;
            @ViewInject(R.id.weightTv)
            TextView    weightTv;
            @ViewInject(R.id.doBtn)
            Button      doBtn;



        }
    }
}
