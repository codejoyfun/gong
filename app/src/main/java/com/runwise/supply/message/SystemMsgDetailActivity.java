package com.runwise.supply.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.IBaseAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;
import com.runwise.supply.repertory.entity.EditRepertoryResult;

import org.greenrobot.eventbus.EventBus;

public class SystemMsgDetailActivity extends BaseActivity {
    @ViewInject(R.id.listView)
    private ListView listView;
    private MsgDetailAdapter detailAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        Intent intent = this.getIntent();
        detailAdapter = new MsgDetailAdapter();
        listView.setAdapter(detailAdapter);
        setTitleText(true,"申请");
    }

    @OnClick(R.id.stepPayFinish)
    public void doFinish(View view) {
        this.finish();
    }
    public class MsgDetailAdapter extends IBaseAdapter<EditRepertoryResult.InventoryBean.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            int viewType = getItemViewType(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_msg_detail, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final EditRepertoryResult.InventoryBean.ListBean bean =  mList.get(position);

            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.msgTime)
            TextView msgTime;
            @ViewInject(R.id.msgContext)
            TextView msgContext;
        }
    }
}
