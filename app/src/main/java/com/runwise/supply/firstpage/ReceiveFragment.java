package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.bean.ReceiveProEvent;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReceiveBean;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by libin on 2017/7/16.
 */

public class ReceiveFragment extends BaseFragment{
    private DoActionCallback callback;

    public void setCallback(DoActionCallback callback) {
        this.callback = callback;
    }

    public DataType type;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ReceiveAdapter adapter;
    //跟自己类型配对的数据即可。
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    //存放该类型下用户输入的值
    private Map<String,ReceiveBean> countMap = new HashMap<>();
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCountSynEvent(ReceiveProEvent event){
        ReceiveActivity activity = (ReceiveActivity) getActivity();
        Map<String,ReceiveBean> map = activity.getCountMap();
        if (map != null){
            countMap.clear();
            countMap.putAll(map);
        }
        adapter.notifyDataSetChanged();
    }

    public class ReceiveAdapter extends IBaseAdapter {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            String pId = String.valueOf(bean.getProductID());
            final ProductBasicList.ListBean basicBean= ProductBasicUtils.getBasicMap(mContext).get(pId);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.receive_list_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceiveBean rb = new ReceiveBean();
                    if (basicBean != null){
                        rb.setName(basicBean.getName());
                        rb.setCount((int)bean.getProductUomQty());
                        rb.setProductId(bean.getProductID());
                        if (basicBean.isTwoUnit() && TextUtils.isEmpty(basicBean.getSettleUomId())){
                            rb.setTwoUnit(true);
                            rb.setUnit(basicBean.getUnit());
                        }else{
                            rb.setTwoUnit(false);
                        }
                        if (callback != null){
                            callback.doAction(rb);
                        }
                    }

                }
            });
            if (basicBean != null){
                viewHolder.name.setText(basicBean.getName());
                if (basicBean.getImage() != null)
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.sdv, Constant.BASE_URL+basicBean.getImage().getImageSmall());
                StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
                sb.append("  ").append(basicBean.getUnit()).append("\n").append(bean.getPriceUnit()).append("元/").append(bean.getProductUom());
                viewHolder.content.setText(sb.toString());
                viewHolder.countTv.setText("/"+(int)bean.getProductUomQty()+basicBean.getUom());
                if (basicBean.isTwoUnit() && TextUtils.isEmpty(basicBean.getSettleUomId())){
                    //显示双单位信息
                    viewHolder.weightTv.setText("0"+basicBean.getSettleUomId());
                }else{
                    viewHolder.weightTv.setVisibility(View.INVISIBLE);
                }
            }
            if (countMap.containsKey(String.valueOf(bean.getProductID()))){
                ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
                viewHolder.receivedTv.setText(rb.getCount()+"");
            }else{
                viewHolder.receivedTv.setText("0");
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
