package com.runwise.supply.orderpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.OrderDetailActivity;
import com.runwise.supply.firstpage.UploadPayedPicActivity;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.vov.vitamio.utils.NumberUtil;

/**
 * 订单提交成功中间页
 *
 * Created by Dong on 2017/10/25.
 */

public class OrderCommitSuccessActivity extends BaseActivity {
    private static final int REQ_ACT_UPLOAD = 0x1234;
    public static final String INTENT_KEY_ORDERS = "intent_key_orders";
    public static final String INTENT_KEY_TYPE = "type";
    private OrderAdapter mOrderAdapter;
    @ViewInject(R.id.rv_order_commit_sucess)
    private RecyclerView mRvOrders;
    private List<OrderResponse.ListBean> mListOrders;
    LayoutInflater inflater;
    private boolean showUploadButton;
    OrderResponse.ListBean bean;//暂存第一个订单
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_commit_success);
        setTitleLeftIcon(true,R.drawable.nav_closed);
        type = getIntent().getIntExtra(INTENT_KEY_TYPE,-1);
        setTitleText(true,type==0?"修改成功":"下单成功");
        mOrderAdapter = new OrderAdapter();
        mRvOrders.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRvOrders.setAdapter(mOrderAdapter);
        mListOrders = getIntent().getParcelableArrayListExtra(INTENT_KEY_ORDERS);
        inflater = LayoutInflater.from(this);

        bean = mListOrders.get(0);
        if (bean.getOrderSettleName().contains("单次结算")){
            showUploadButton = true;
        }else{
            showUploadButton = false;
        }
        if (bean.getOrderSettleName().contains("先付款后收货")&&bean.getOrderSettleName().contains("单次结算")){
            showUploadButton = true;
        }
    }

    @OnClick(R.id.title_iv_left)
    public void onBtnClick(View v){
        finish();
    }

    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
        final int TYPE_HEADER = 0;
        final int TYPE_ITEM = 1;
        @Override
        public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case TYPE_HEADER:
                    return new ViewHolder(inflater.inflate(R.layout.item_order_success_header,parent,false));
                case TYPE_ITEM:
                    return new ViewHolder(inflater.inflate(R.layout.item_order_success,parent,false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (getItemViewType(position)){
                case TYPE_HEADER:
                    if(type==0){//修改
                        if(mListOrders.size()>1){
                            holder.tvHeadContent.setText("由于商品种类不同，您的订单已被拆分为以下几个订单");
                        }else{
                            holder.tvHeadContent.setText("修改成功，我们将尽快确认您的订单");
                        }
                        holder.tvHeadTitle.setText("修改成功");
                        holder.ivIcon.setImageResource(R.drawable.default_ico_eidt_successed);
                    }else{//下单成功
                        if(mListOrders.size()>1){
                            holder.tvHeadContent.setText("由于商品种类不同，您的订单已被拆分为以下几个订单");
                        }else{
                            holder.tvHeadContent.setText("提交成功，我们将尽快确认您的订单");
                        }
                        holder.tvHeadTitle.setText("下单成功");
                        holder.ivIcon.setImageResource(R.drawable.default_ico_order_successed);
                    }
                    holder.tvUpload.setVisibility(showUploadButton?View.VISIBLE:View.GONE);
                    holder.tvUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent uIntent = new Intent(mContext,UploadPayedPicActivity.class);
                            uIntent.putExtra("orderid",bean.getOrderID());
                            uIntent.putExtra("ordername",bean.getName());
                            uIntent.putExtra("hasattachment",type==0);
                            startActivityForResult(uIntent,REQ_ACT_UPLOAD);
                        }
                    });
                    return;
                case TYPE_ITEM:
                    int realPos = position - 1;//有头部
                    OrderResponse.ListBean order = mListOrders.get(realPos);
                    holder.listBean = order;
                    holder.tvOrderNum.setText(order.getName());
                    holder.tvOrderState.setText(order.getState());
                    if(realPos==mListOrders.size()-1){
                        holder.llRoot.setBackgroundResource(R.drawable.background_order_bottom);
                    }else{
                        holder.llRoot.setBackgroundResource(R.drawable.background_order_mid);
                    }
                    //预计时间
                    StringBuffer etSb = new StringBuffer();
                    if (order.getState().equals(OrderState.DRAFT.getName()) || order.getState().equals(OrderState.SALE.getName())) {
                        etSb.append("预计").append(formatTimeStr(order.getEstimatedDate())).append("送达");
                    } else if (order.getState().equals(OrderState.PEISONG.getName())) {
                        etSb.append("预计").append(formatTimeStr(order.getEstimatedDate())).append("送达");
                    } else {
                        etSb.append(order.getDoneDatetime()).append("已送达");
                    }
                    holder.tvTitle.setText(etSb.toString());
                    holder.tvOrderState.setText(OrderState.getValueByName(order.getState()));
                    //描述
                    boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
                    StringBuilder sb = new StringBuilder();
                    if(canSeePrice)sb.append("￥").append(NumberUtil.getIOrD(order.getAmountTotal())).append("，");
                    sb.append(NumberUtil.getIOrD(order.getAmount())).append("件商品");
                    holder.tvOrderDesc.setText(sb.toString());
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

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView)itemView.findViewById(R.id.tv_order_title);
                tvOrderDesc = (TextView)itemView.findViewById(R.id.tv_order_desc);
                tvAction = (TextView)itemView.findViewById(R.id.tv_order_action);
                tvOrderState = (TextView)itemView.findViewById(R.id.tv_order_state);
                tvOrderNum = (TextView)itemView.findViewById(R.id.tv_order_name);
                tvUpload = (Button)itemView.findViewById(R.id.btn_upload);
                llRoot = itemView.findViewById(R.id.rl_root);

                ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
                tvHeadTitle = (TextView)itemView.findViewById(R.id.tv_title);
                tvHeadContent = (TextView)itemView.findViewById(R.id.tv_content);
                if(tvAction!=null)tvAction.setOnClickListener(this);//防止头部
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", listBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            OrderResponse.ListBean listBean;
            TextView tvTitle;
            TextView tvOrderNum;
            TextView tvOrderDesc;
            TextView tvOrderState;
            TextView tvAction;
            Button tvUpload;

            ImageView ivIcon;
            TextView tvHeadTitle;
            TextView tvHeadContent;
            View llRoot;
        }
    }
    SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat sdfTarget = new SimpleDateFormat("MM月dd日",Locale.getDefault());
    private String formatTimeStr(String str){
        try{
            return sdfTarget.format(sdfSource.parse(str));
        }catch (ParseException e){
            e.printStackTrace();
            return str;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_ACT_UPLOAD && resultCode==200 && data.getBooleanExtra("upload_success",false)){
            finish();
        }
    }
}
