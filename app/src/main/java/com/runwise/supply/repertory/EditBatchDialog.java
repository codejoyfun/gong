package com.runwise.supply.repertory;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.entity.BatchEntity;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.firstpage.EditBatchActivity;
import com.runwise.supply.firstpage.entity.ReceiveRequest;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.NoWatchEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Dong on 2017/12/8.
 */

public class EditBatchDialog extends BaseActivity {

    public static final String INTENT_KEY_BATCH_ENTITIES = "batch_entity";
    public static final String INTENT_KEY_INIT_BATCH = "init_batch";

    ArrayList<BatchEntity> mBatchEntities;
    @BindView(R.id.rl_edit_batch_root)
    View mViewBg;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.iv_cancle)
    ImageView mIvCancle;
    @BindView(R.id.rl_head)
    RelativeLayout mRlHead;
    @BindView(R.id.lv_batch)
    ListView mLvBatch;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    WheelView wheelView;
    TimePickerView pvCustomTime;
    InventoryResponse.InventoryProduct mBean;
    boolean canSeePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_batch2);
        setTranslucentStatus(true);
        ButterKnife.bind(this);
        mBatchEntities = new ArrayList<>();

        final BatchListAdapter batchListAdapter = new BatchListAdapter();
        View addBatchView = LayoutInflater.from(this).inflate(R.layout.edit_batch_foot_view, null);

        //增加按钮
        addBatchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatchEntity batchEntity = new BatchEntity();
                batchEntity.setProductDate(TimeUtils.getYMD(Calendar.getInstance().getTime()));
                batchEntity.setProductDate(true);
                batchEntity.setNewAdded(true);
                mBatchEntities.add(batchEntity);
                batchListAdapter.notifyDataSetChanged();
            }
        });
        mLvBatch.addFooterView(addBatchView);
        mLvBatch.setAdapter(batchListAdapter);
        mBatchEntities = initBatchData();
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        mViewBg.postDelayed(()->mViewBg.setBackgroundColor(getResources().getColor(R.color.translucent)),600);
    }

    /**
     * 设置初始的批次列表，子类可override
     * @return
     */
    protected ArrayList<BatchEntity> initBatchData(){

        mBean = (InventoryResponse.InventoryProduct)getIntent().getSerializableExtra(INTENT_KEY_INIT_BATCH);
        ArrayList<BatchEntity> batchEntities = new ArrayList<>();
        String pId = String.valueOf(mBean.getProductID());
        final ProductBasicList.ListBean basicBean = mBean.getProduct();

        mName.setText(basicBean.getName());

        List<InventoryResponse.InventoryLot> lotBeens = mBean.getLotList();
        if(lotBeens!=null){
            for (InventoryResponse.InventoryLot lotBean : lotBeens) {
                BatchEntity batchEntity = new BatchEntity();
                batchEntity.setBatchNum(lotBean.getLotNum());
                batchEntity.setProductCount(String.valueOf((int)lotBean.getEditNum()));
                batchEntity.setProductDate(lotBean.getProductDate());
                batchEntity.setProductDate(lotBean.isProductDate());
                batchEntity.setNewAdded(lotBean.isNewAdded());
                batchEntities.add(batchEntity);
            }
        }

        return batchEntities;
    }

    /**
     * 设置返回的数据，子类可按需要自己定义
     * @param intent
     */
    protected void setReturnData(Intent intent){
        intent.putExtra(INTENT_KEY_INIT_BATCH,mBean);
    }


    @OnClick({R.id.iv_cancle, R.id.btn_confirm, R.id.rl_edit_batch_root})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancle:
                dialog.setMessageGravity();
                dialog.setMessage("还没保存哦,确认取消?");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        mBatchEntities.clear();
                        finish();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.btn_confirm:
                for (BatchEntity batchEntity : mBatchEntities) {
                    if (batchEntity.isNewAdded() && TextUtils.isEmpty(batchEntity.getProductDate())) {
                        ToastUtil.show(mContext, "你还没有填写日期!");
                        return;
                    }
                    if (TextUtils.isEmpty(batchEntity.getProductCount())) {
                        ToastUtil.show(mContext, "你还没有填写商品数量!");
                        return;
                    }
                }
                Intent intent = new Intent();
                intent.putExtra(INTENT_KEY_BATCH_ENTITIES, mBatchEntities);
                setReturnData(intent);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.rl_edit_batch_root:
//                dialog.setMessageGravity();
//                dialog.setMessage("还没保存哦,确认取消?");
//                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
//                    @Override
//                    public void doClickButton(Button btn, CustomDialog dialog) {
//                        mBatchEntities.clear();
//                        finish();
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//                finish();
                customFinish();
                break;
        }
    }

    public class BatchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBatchEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return mBatchEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(EditBatchDialog.this).inflate(R.layout.item_batch_new, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BatchEntity batch = mBatchEntities.get(position);
            viewHolder.etProductCount.setTag(position);
            viewHolder.ivDelete.setVisibility(batch.isNewAdded() ? View.VISIBLE : View.GONE);
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBatchEntities.remove(position);
                    notifyDataSetChanged();
                }
            });
            viewHolder.rlProductDate.setVisibility(batch.isNewAdded() ? View.VISIBLE:View.GONE);
            viewHolder.tvProductDateValue.setText(mBatchEntities.get(position).getProductDate());
            viewHolder.tvProductDateValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pvCustomTime = new TimePickerView.Builder(EditBatchDialog.this, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {//选中事件回调
                            mBatchEntities.get(position).setProductDate(TimeUtils.getYMD(date));
                            mBatchEntities.get(position).setProductDate(wheelView.getCurrentItem() == 0);
                            notifyDataSetChanged();
                        }
                    }).setLayoutRes(R.layout.custom_time_picker, new CustomListener() {

                        @Override
                        public void customLayout(View v) {
                            final Button btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
                            Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
                            wheelView = (WheelView) v.findViewById(R.id.options);
                            ArrayList<String> stringArrayList = new ArrayList<String>();
                            stringArrayList.add("生产日期");
                            stringArrayList.add("到期日期");
                            wheelView.setAdapter(new ArrayWheelAdapter(stringArrayList));
                            wheelView.setCyclic(false);
                            wheelView.setTextSize(18);
                            wheelView.setLineSpacingMultiplier(1.6F);

                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvCustomTime.returnData();
                                    pvCustomTime.dismiss();

                                }
                            });
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvCustomTime.dismiss();
                                }
                            });
                        }
                    })
                            .setType(new boolean[]{true, true, true, false, false, false})
                            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                            .build();
                    wheelView.setCurrentItem(0);
                    pvCustomTime.show();
                }
            });
            viewHolder.tvProductDate.setText(mBatchEntities.get(position).isProductDate() ? "生产日期" : "到期日期");
            viewHolder.etBatch.removeTextChangedListener();
            viewHolder.etBatch.setText(mBatchEntities.get(position).getBatchNum());
            viewHolder.etBatch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int position = (int) viewHolder.etProductCount.getTag();
                    mBatchEntities.get(position).setBatchNum(s.toString().trim());
                }
            });

            viewHolder.etProductCount.removeTextChangedListener();
            viewHolder.etProductCount.setText(mBatchEntities.get(position).getProductCount());
            viewHolder.etProductCount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mBatchEntities.get(position).setProductCount(s.toString().trim());
                }
            });

//            viewHolder.etProductCount.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                        view.setFocusableInTouchMode(true);
//                        view.requestFocus();
//                        ((EditText) view).selectAll();
//                    }
//                    return false;
//                }
//            });

            viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productCountStr = mBatchEntities.get(position).getProductCount();
                    if (TextUtils.isEmpty(productCountStr)) {
                        productCountStr = "0";
                    }
                    int productCount = Integer.parseInt(productCountStr);
                    productCount++;
                    mBatchEntities.get(position).setProductCount(String.valueOf(productCount));
                    viewHolder.etProductCount.setText(String.valueOf(productCount));
                }
            });
            viewHolder.ivReduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productCountStr = mBatchEntities.get(position).getProductCount();
                    if (TextUtils.isEmpty(productCountStr) || productCountStr.equals("0")) {
                        return;
                    }
                    int productCount = Integer.parseInt(productCountStr);
                    productCount--;
                    mBatchEntities.get(position).setProductCount(String.valueOf(productCount));
                    viewHolder.etProductCount.setText(String.valueOf(productCount));
                }
            });

            if (!TextUtils.isEmpty(mBatchEntities.get(position).getBatchNum()) && mBatchEntities.get(position).getBatchNum().length() > 0) {
                viewHolder.etBatch.setSelection(mBatchEntities.get(position).getBatchNum().length());
            }
            if (!TextUtils.isEmpty(mBatchEntities.get(position).getProductCount()) && mBatchEntities.get(position).getProductCount().length() > 0) {
                viewHolder.etProductCount.setSelection(mBatchEntities.get(position).getProductCount().length());
            }
            return convertView;
        }


    }

    static class ViewHolder {
        @BindView(R.id.tv_batch)
        TextView tvBatch;
        @BindView(R.id.et_batch)
        NoWatchEditText etBatch;
        @BindView(R.id.v_line)
        View vLine;
        @BindView(R.id.tv_product_date)
        TextView tvProductDate;
        @BindView(R.id.tv_product_date_value)
        TextView tvProductDateValue;
        @BindView(R.id.v_line1)
        View vLine1;
        @BindView(R.id.tv_product_count)
        TextView tvProductCount;
        @BindView(R.id.et_product_count)
        NoWatchEditText etProductCount;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;
        @BindView(R.id.iv_add)
        ImageView ivAdd;
        @BindView(R.id.iv_reduce)
        ImageView ivReduce;
        @BindView(R.id.rl_product_date)
        RelativeLayout rlProductDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    InputMethodManager imm;
    private void hideKeyboard(){
        if(imm==null)imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if(imm!=null && v!=null)imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 超级蛋疼的，键盘展示的时候回退到InventoryActivity，会造成draglayout显示错误，必须先收起键盘，暂时找不到原因
     * 先收起键盘，等待一小段时间，再finish
     */
    private void customFinish(){
        hideKeyboard();
//        mName.postDelayed(this::finish,800);
        finish();
    }
}
