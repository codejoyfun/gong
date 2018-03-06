package com.runwise.supply.firstpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.TempOrderManager;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.UserUtils;

import io.vov.vitamio.utils.NumberUtil;

/**
 * 首页的提交中订单的商品列表
 *
 * Created by Dong on 2017/12/5.
 */

public class TempOrderActivity extends NetWorkActivity {

    public static final String INTENT_KEY_ORDER = "temp_order";
    @ViewInject(R.id.rv_temp_order_product_list)
    private RecyclerView mRvProducts;
    private TempOrderManager.TempOrder mTempOrder;
    private ProductsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_order_products);
        showBackBtn();
        setTitleText(true,"商品清单");
        mTempOrder = getIntent().getParcelableExtra(INTENT_KEY_ORDER);
        mAdapter = new ProductsAdapter();
        mRvProducts.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRvProducts.setAdapter(mAdapter);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private final class ProductsAdapter extends RecyclerView.Adapter{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(TempOrderActivity.this);
            return new ViewHolder(inflater.inflate(R.layout.orderdetail_list_item,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder vh;
            vh  = (ViewHolder) holder;
            final ProductBasicList.ListBean bean = mTempOrder.getProductList().get(position);
            int pId = bean.getProductID();
            if (bean.getImage() != null){
                FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(vh.productImage, bean.getImage().getImageSmall());
            }

            vh.nowPriceTv.setText("x"+ NumberUtil.getIOrD(bean.getActualQty()));

            vh.name.setText(bean.getName());
            StringBuffer sb = new StringBuffer(bean.getDefaultCode());
            sb.append(" | ").append(bean.getUnit());
            boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
            if (canSeePrice){
                sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getPrice()))).append("元/").append(bean.getProductUom());
            }
            vh.unit1.setText(bean.getProductUom());
            vh.content.setText(sb.toString());
        }

        @Override
        public int getItemCount() {
            return mTempOrder.getProductList()==null?0:mTempOrder.getProductList().size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView productImage;
        public TextView name;
        public TextView content;
        public TextView oldPriceTv;
        public TextView nowPriceTv;
        public TextView weightTv;
        public TextView unit1;
        public View rootView;
        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView)itemView.findViewById(R.id.content);
            oldPriceTv = (TextView)itemView.findViewById(R.id.oldPriceTv);
            nowPriceTv = (TextView)itemView.findViewById(R.id.nowPriceTv);
            weightTv = (TextView)itemView.findViewById(R.id.weightTv);
            unit1 = (TextView)itemView.findViewById(R.id.unit1);
        }
    }

    public static void start(Activity activity, TempOrderManager.TempOrder tempOrder){
        Intent intent = new Intent(activity,TempOrderActivity.class);
        intent.putExtra(INTENT_KEY_ORDER,(Parcelable) tempOrder);
        activity.startActivity(intent);
    }
}
