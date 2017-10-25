package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 * 订单提交成功中间页
 *
 * Created by Dong on 2017/10/25.
 */

public class OrderCommitSuccessActivity extends BaseActivity {

    @ViewInject(R.id.rv_order_commit_sucess)
    private RecyclerView mRvOrders;
    private List<OrderResponse.ListBean> mListOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_commit_success);
    }

    class OrderAdapter extends RecyclerView.Adapter{
        final int TYPE_HEADER = 0;
        final int TYPE_ITEM = 1;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)){
                case TYPE_HEADER:
                    return;
                case TYPE_ITEM:
                    return;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position==0?TYPE_HEADER:TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return mListOrders==null?0:mListOrders.size()+1;
        }

        class ViewHolder{
            TextView tvTitle;
            TextView tvOrderNum;
            TextView tvOrderDesc;
            TextView tvOrderState;
            Button tvAction;
        }
    }

}
