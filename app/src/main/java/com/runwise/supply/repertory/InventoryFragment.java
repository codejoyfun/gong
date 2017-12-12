package com.runwise.supply.repertory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.AddInventoryBatchRequest;
import com.runwise.supply.entity.AddInventoryBatchResponse;
import com.runwise.supply.entity.BatchEntity;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import io.vov.vitamio.utils.NumberUtil;

import static android.app.Activity.RESULT_OK;
import static com.runwise.supply.repertory.EditBatchDialog.INTENT_KEY_BATCH_ENTITIES;
import static com.runwise.supply.repertory.EditBatchDialog.INTENT_KEY_INIT_BATCH;

/**
 * 盘点Fragment，数据和父Activity共用引用，方便修改后共享数据
 *
 * Created by Dong on 2017/12/8.
 */

public class InventoryFragment extends NetWorkFragment {
    private static final int REQ_MODIFY_BATCH = 0x234;
    private static final int REQUEST_ADD_INVENTORY_LINE = 0x2355;
    @ViewInject(R.id.pullListView)
    private ListView pullToRefreshListView;
    private List<InventoryResponse.InventoryProduct> mInventoryProductList;
    private InventoryAdapter mInventoryAdapter = new InventoryAdapter();
    private LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pullToRefreshListView.setAdapter(mInventoryAdapter);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_inventory;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch(where){
            case REQUEST_ADD_INVENTORY_LINE:
                AddInventoryBatchResponse response = (AddInventoryBatchResponse) result.getResult().getData();
                InventoryResponse.InventoryProduct inventoryProduct = response.getInventoryProduct();
                //找到对应的要修改的产品,直接替换批次信息
                for (InventoryResponse.InventoryProduct product:mInventoryProductList){
                    if(product.getProductID()==inventoryProduct.getProductID()){
                        product.setLotList(inventoryProduct.getLotList());
                        break;
                    }
                }
                mInventoryAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_MODIFY_BATCH:
                if(resultCode == RESULT_OK){
                    //修改批次信息返回
                    List<BatchEntity> batchEntities = (List<BatchEntity>)data.getSerializableExtra(INTENT_KEY_BATCH_ENTITIES);

                    //寻找对应修改的商品
                    InventoryResponse.InventoryProduct modifyProduct = (InventoryResponse.InventoryProduct)data.getSerializableExtra(INTENT_KEY_INIT_BATCH);
                    for (InventoryResponse.InventoryProduct product:mInventoryProductList){
                        if(product.getProductID()==modifyProduct.getProductID()){
                            modifyProduct = product;
                            break;
                        }
                    }

                    //汇总修改信息
                    Map<String,BatchEntity> batchMap = new HashMap<>();
                    for(BatchEntity batchEntity:batchEntities){
                        BatchEntity previousBatch;
                        if(batchEntity.getBatchNum()!=null)previousBatch = batchMap.get(batchEntity.getBatchNum());
                        else previousBatch = batchMap.get(batchEntity.getProductDate()+batchEntity.isProductDate());
                        if(previousBatch!=null){
                            double total = Double.valueOf(batchEntity.getProductCount()) + Double.valueOf(previousBatch.getProductCount());
                            previousBatch.setProductCount(String.valueOf(total));
                        }else{
                            String key = batchEntity.getBatchNum()==null ? batchEntity.getProductDate()+batchEntity.isProductDate() : batchEntity.getBatchNum();
                            batchMap.put(key,batchEntity);
                        }
                    }

                    //设置已有的
                    Iterator<InventoryResponse.InventoryLot> iterator = modifyProduct.getLotList().iterator();
                    while (iterator.hasNext()){
                        InventoryResponse.InventoryLot lot = iterator.next();
                        String key;
                        BatchEntity batchEntity;
                        if(lot.getLotNum()!=null)key = lot.getLotNum();
                        else key = lot.isProductDate()+lot.getProductDate();
                        batchEntity = batchMap.get(key);
                        if(batchEntity!=null){
                            lot.setEditNum(Double.valueOf(batchEntity.getProductCount()));
                            lot.setProductDate(batchEntity.getProductDate());
                            lot.setProductDate(batchEntity.isProductDate());
                            batchMap.remove(key);//已经设置的，从map中去掉，最后剩下的就是新加的
                        }else {
                            //回传中找不到，表示已经删除，适用于新加的批次
                            iterator.remove();
                        }
                    }


                    //加入新加的
                    if(batchMap.size()>0){
                        for(BatchEntity batchEntity:batchMap.values()){
                            InventoryResponse.InventoryLot lot = new InventoryResponse.InventoryLot();
                            lot.setLotNum(batchEntity.getBatchNum());
                            lot.setProductDate(batchEntity.getProductDate());
                            lot.setProductDate(batchEntity.isProductDate());
                            lot.setNewAdded(true);
                            lot.setQty(0);
                            lot.setEditNum(Double.valueOf(batchEntity.getProductCount()));
                            modifyProduct.getLotList().add(lot);
                        }
                    }

                    mInventoryAdapter.notifyDataSetChanged();

                    //查询接口，修改对应商品的盘点行
//                    AddInventoryBatchRequest request = new AddInventoryBatchRequest();
//                    request.setProductID(modifyProduct.getProductID());
//                    List<AddInventoryBatchRequest.BatchData> batchDataList = new ArrayList<>();
//                    for(BatchEntity batchEntity:batchEntities){
//                        AddInventoryBatchRequest.BatchData batchData = new AddInventoryBatchRequest.BatchData();
//                        batchData.setBatchName(batchEntity.getBatchNum());
//                        if(TextUtils.isDigitsOnly(batchEntity.getProductCount())){
//                            batchData.setCount(Integer.valueOf(batchEntity.getProductCount()));
//                        }
//                        batchDataList.add(batchData);
//                    }
//                    request.setBatchDataList(batchDataList);
//                    sendConnection("/test",request,REQUEST_ADD_INVENTORY_LINE,false, AddInventoryBatchResponse.class);
                }
                break;
        }
    }

    /**
     * 供父activity设置数据
     */
    public void setData(List<InventoryResponse.InventoryProduct> inventoryProducts){
        mInventoryProductList = inventoryProducts;
        mInventoryAdapter.setData(inventoryProducts);
    }

    /**
     * 盘点商品列表的adapter
     */
    private class InventoryAdapter extends IBaseAdapter<InventoryResponse.InventoryProduct>{

        Queue<View> cacheLotView = new ArrayDeque<>();

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            InventoryResponse.InventoryProduct inventoryProduct = mInventoryProductList.get(position);
            ViewHolder viewHolder;
            if(convertView==null){
                convertView = mInflater.inflate(R.layout.item_inventory,parent,false);
                viewHolder = new ViewHolder();
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder)convertView.getTag();
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),EditBatchDialog.class);
                    intent.putExtra(INTENT_KEY_INIT_BATCH,inventoryProduct);
                    startActivityForResult(intent,REQ_MODIFY_BATCH);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_bottom,R.anim.activity_close_exit);
                }
            });

            //添加批次信息
            //有批次
            if(inventoryProduct.getLotList()!=null && inventoryProduct.getLotList().size()>0){
                viewHolder.mmEtCount.setVisibility(View.GONE);
                viewHolder.mmTvUom.setVisibility(View.GONE);
                viewHolder.mmIvArrow.setVisibility(View.GONE);
                viewHolder.mmTvTheoretical.setVisibility(View.GONE);
                for(int i=0;i<inventoryProduct.getLotList().size();i++){
                    View view = viewHolder.mmLayoutContainer.getChildAt(i);
                    LotViewHolder lotViewHolder;
                    if(view!=null){
                        lotViewHolder = (LotViewHolder) view.getTag();
                    }else{
                        //need to add view
                        //get from cache first
                        view = cacheLotView.poll();
                        if(view == null){//cache is not available, need inflate
                            view = mInflater.inflate(R.layout.item_inventory_lot,viewHolder.mmLayoutContainer,false);
                            lotViewHolder = new LotViewHolder();
                            ViewUtils.inject(lotViewHolder,view);
                            view.setTag(lotViewHolder);
                        }
                        lotViewHolder = (LotViewHolder)view.getTag();
                        viewHolder.mmLayoutContainer.addView(view);
                    }
                    InventoryResponse.InventoryLot inventoryLot = inventoryProduct.getLotList().get(i);
                    long dayDiff = DateFormateUtil.getDaysToExpire(inventoryLot.getLifeEndDate());
                    if(dayDiff == 0){
                        //今天到期
                        lotViewHolder.mmTvExpire.setText("今天到期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.inventory_expire));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.inventory_expire_bg));
                    }else if(dayDiff<0){
                        //过期
                        lotViewHolder.mmTvExpire.setText("已过期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.inventory_expire));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.inventory_expire_bg));
                    }else if(dayDiff<=3){
                        //小于3天
                        lotViewHolder.mmTvExpire.setText(dayDiff+"天到期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.stock_3_days));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.stock_3_days_bg));
                    }else{
                        //大于4天
                        lotViewHolder.mmTvExpire.setText(dayDiff+"天到期");
                        lotViewHolder.mmTvExpire.setTextColor(getResources().getColor(R.color.stock_4_days));
                        lotViewHolder.mmTvExpire.setBackgroundColor(getResources().getColor(R.color.stock_4_days_bg));
                    }
                    lotViewHolder.mmTvLotName.setText(inventoryLot.getLotNum()!=null?"批次："+ inventoryLot.getLotNum():"");
                    lotViewHolder.mmTvTheoretical.setText("/"+NumberUtil.getIOrD(inventoryLot.getQty()));
                    lotViewHolder.mmTvLotCount.setText(NumberUtil.getIOrD(inventoryLot.getEditNum()));
                    lotViewHolder.mmTvLotUom.setText(inventoryProduct.getUom());
                    lotViewHolder.inventoryLot = inventoryLot;
                }
                //remove and cache unused child view
                int currentSize = inventoryProduct.getLotList().size();
                int totalChild = viewHolder.mmLayoutContainer.getChildCount();
                Log.d("haha","pos:"+position+" totalChild:"+totalChild);
                for(int i = totalChild - 1;i>=currentSize;i--){//由后往前删，防止index乱
                    Log.d("haha","pos:"+position+" remove child at:"+i);
                    View view = viewHolder.mmLayoutContainer.getChildAt(i);
                    if(view==null)break;
                    viewHolder.mmLayoutContainer.removeViewAt(i);
                    cacheLotView.offer(view);
                }

//                for(InventoryResponse.InventoryLot inventoryLot:inventoryProduct.getLotList()){
//                    View lotView = mInflater.inflate(R.layout.item_stock_lot,viewHolder.mmLayoutContainer,false);
//                    //TODO：复用保存LotViewHolder
//                    LotViewHolder lotViewHolder = new LotViewHolder();
//
//                    ViewUtils.inject(lotViewHolder,lotView);
//                    lotViewHolder.mmTvExpire.setText(inventoryLot.getLifeEndDate());
//                    lotViewHolder.mmTvLotName.setText(inventoryLot.getLotNum());
//                    lotViewHolder.inventoryLot = inventoryLot;
//                    viewHolder.mmLayoutContainer.addView(lotView);
//                }
            }else{//无批次
                viewHolder.mmEtCount.setVisibility(View.VISIBLE);
                viewHolder.mmTvUom.setVisibility(View.VISIBLE);
                viewHolder.mmIvArrow.setVisibility(View.VISIBLE);
                viewHolder.mmTvTheoretical.setVisibility(View.VISIBLE);
                viewHolder.mmEtCount.setText(NumberUtil.getIOrD(inventoryProduct.getEditNum()));
                viewHolder.mmTvTheoretical.setText("/"+NumberUtil.getIOrD(inventoryProduct.getQty()));
                viewHolder.mmTvUom.setText(inventoryProduct.getUom());
                viewHolder.mmEtCount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(TextUtils.isDigitsOnly(s)){
                            inventoryProduct.setEditNum(Integer.valueOf(s.toString()));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            viewHolder.mmTvTitle.setText(inventoryProduct.getProduct().getName());
            viewHolder.mmTvCode.setText(inventoryProduct.getCode());
            viewHolder.mmTvUnit.setText(inventoryProduct.getProduct().getUnit());
            //TODO:图片
            FrecoFactory.getInstance(getActivity()).disPlay(viewHolder.mmSdvImage,inventoryProduct.getProduct().getImage().getImage());

            return convertView;
        }
    }

    /**
     * 盘点商品列表viewholder
     */
    private class ViewHolder{
        @ViewInject(R.id.sdv_stock_image)
        SimpleDraweeView mmSdvImage;
        @ViewInject(R.id.tv_stock_title)
        TextView mmTvTitle;
        @ViewInject(R.id.tv_stock_code)
        TextView mmTvCode;
        @ViewInject(R.id.tv_stock_unit)
        TextView mmTvUnit;
        @ViewInject(R.id.ll_stock_container)
        LinearLayout mmLayoutContainer;
        @ViewInject(R.id.tv_stock_count)
        TextView mmEtCount;
        @ViewInject(R.id.tv_stock_uom)
        TextView mmTvUom;
        @ViewInject(R.id.tv_stock_theoretical)
        TextView mmTvTheoretical;
        @ViewInject(R.id.iv_callin_icon)
        ImageView mmIvArrow;
    }

    private class LotViewHolder{
        InventoryResponse.InventoryLot inventoryLot;
        @ViewInject(R.id.tv_stock_lot_expire)
        TextView mmTvExpire;
        @ViewInject(R.id.tv_stock_lot_name)
        TextView mmTvLotName;
        @ViewInject(R.id.tv_stock_lot_count)
        TextView mmTvLotCount;
        @ViewInject(R.id.tv_stock_lot_uom)
        TextView mmTvLotUom;
        @ViewInject(R.id.tv_stock_theoretical)
        TextView mmTvTheoretical;
    }

    /**
     * 收到库存修改的广播，要更新界面
     */
    @Subscribe
    public void onInventoryEdit(){
        //TODO
        mInventoryAdapter.notifyDataSetChanged();
    }
}
