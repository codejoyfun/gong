package com.runwise.supply.firstpage;

import android.os.Bundle;
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
import com.kids.commonframe.base.bean.ReturnEvent;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnBean;
import com.runwise.supply.orderpage.DataType;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by libin on 2017/7/22.
 */

public class ReturnFragment extends BaseFragment {
    private ReturnCallback callback;            //点击退货的回调
    public DataType type;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ReturnAdapter adapter;
    private boolean canSeePrice;
    //跟自己类型配对的数据即可。
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    private Map<String, ReturnBean> countMap = new HashMap<>();

    public void setCallback(ReturnCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        adapter = new ReturnAdapter();
        ArrayList<OrderResponse.ListBean.LinesBean> linesList = getArguments().getParcelableArrayList("datas");
        datas.addAll(linesList);
        adapter.setData(datas);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);

    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.return_fragment;
    }

    public class ReturnAdapter extends IBaseAdapter {

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            String pId = String.valueOf(bean.getProductID());
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.return_list_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
                viewHolder.name.setText(bean.getName());
                if (!TextUtils.isEmpty(bean.getImageMedium()))
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + bean.getImageMedium());
                StringBuffer sb = new StringBuffer(bean.getDefaultCode());
                sb.append(" | ").append(bean.getUnit());
                if (canSeePrice) {
                    sb.append("\n").append(bean.getPriceUnit()).append("元/").append(bean.getProductUom());
                }
                viewHolder.content.setText(sb.toString());
            viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        ReturnBean rb = new ReturnBean();
                        rb.setName(bean.getName());
                        rb.setpId(bean.getProductID());
                        double max = bean.getDeliveredQty() - bean.getReturnAmount();
                        rb.setMaxReturnCount(max);
                        callback.returnBtnClick(rb);
                    }
                }
            });
            if (countMap != null && countMap.containsKey(String.valueOf(pId))) {
                viewHolder.doBtn.setText("退货(" + NumberUtil.getIOrD(countMap.get(String.valueOf(pId)).getReturnCount()) + ")");
            } else {
                viewHolder.doBtn.setText("退货");
            }
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView name;   //名称
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;    //头像
            @ViewInject(R.id.content)
            TextView content;//内容
            @ViewInject(R.id.doBtn)
            Button doBtn;

        }
    }

    public interface ReturnCallback {
        void returnBtnClick(ReturnBean rb);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCountSynEvent(ReturnEvent event) {
        ReturnActivity activity = (ReturnActivity) getActivity();
        Map<String, ReturnBean> map = activity.getCountMap();
        if (map != null) {
            countMap.clear();
            countMap.putAll(map);
        }
        adapter.notifyDataSetChanged();
    }

}
