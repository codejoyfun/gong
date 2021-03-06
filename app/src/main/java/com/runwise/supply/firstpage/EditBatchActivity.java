package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.googlecode.mp4parser.authoring.Edit;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.entity.BatchEntity;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReceiveRequest;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.NoWatchEditText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.NumberUtil;

public class EditBatchActivity extends NetWorkActivity {


    ArrayList<BatchEntity> mBatchEntities;
    @BindView(R.id.productImage)
    SimpleDraweeView mProductImage;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.content)
    TextView mContent;
    @BindView(R.id.iv_cancle)
    ImageView mIvCancle;
    @BindView(R.id.rl_head)
    RelativeLayout mRlHead;
    @BindView(R.id.lv_batch)
    ListView mLvBatch;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.tv_count)
    TextView mTvCount;
    @BindView(R.id.unit1)
    TextView mTvUnit1;

    WheelView wheelView;
    TimePickerView pvCustomTime;

    public static final String INTENT_KEY_PRODUCT = "intent_key_product";
    public static final String INTENT_KEY_BATCH_ENTITIES = "intent_key_batch_entities";
    private OrderResponse.ListBean.LinesBean mBean;

    interface CallBack {
        void onCall(int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_batch);
        ButterKnife.bind(this);
        mBatchEntities = new ArrayList<>();

        final BatchListAdapter batchListAdapter = new BatchListAdapter();
        View addBatchView = LayoutInflater.from(EditBatchActivity.this).inflate(R.layout.edit_batch_foot_view, null);
        addBatchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatchEntity batchEntity = new BatchEntity();
                batchEntity.setProductDate(TimeUtils.getYMD(Calendar.getInstance().getTime()));
                batchEntity.setProductDate(true);
                mBatchEntities.add(batchEntity);
                batchListAdapter.notifyDataSetChanged();
            }
        });
        mLvBatch.addFooterView(addBatchView);
        mLvBatch.setAdapter(batchListAdapter);
        mBean = getIntent().getParcelableExtra(INTENT_KEY_PRODUCT);
        mTvCount.setText(NumberUtil.getIOrD(mBean.getProductUomQty()));
        mTvUnit1.setText(mBean.getProductUom());
        String pId = String.valueOf(mBean.getProductID());
        final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(pId);
        if (basicBean.getImage() != null) {
            FrecoFactory.getInstance(mContext).disPlay(mProductImage, Constant.BASE_URL + basicBean.getImage().getImageSmall());
        }
        mName.setText(basicBean.getName());
        StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
        boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        if (canSeePrice) {
            sb.append("  ").append(basicBean.getUnit()).append("\n").append(mBean.getPriceUnit()).append("元/").append(mBean.getProductUom());
        }
        mContent.setText(sb.toString());
        CallBack callBack = new CallBack() {
            @Override
            public void onCall(final int position) {
                pvCustomTime = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        mBatchEntities.get(position).setProductDate(TimeUtils.getYMD(date));
                        mBatchEntities.get(position).setProductDate(wheelView.getCurrentItem() == 0);
                        batchListAdapter.notifyDataSetChanged();
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
        };
        batchListAdapter.setCallback(callBack);
        Object o = getIntent().getSerializableExtra(INTENT_KEY_BATCH_ENTITIES);
        if (o != null) {
            ArrayList<ReceiveRequest.ProductsBean.LotBean> lotBeens = (ArrayList<ReceiveRequest.ProductsBean.LotBean>) o;
            for (ReceiveRequest.ProductsBean.LotBean lotBean : lotBeens) {
                BatchEntity batchEntity = new BatchEntity();
                batchEntity.setBatchNum(lotBean.getLot_name());
                if (!TextUtils.isEmpty(lotBean.getLife_datetime())) {
                    batchEntity.setProductDate(lotBean.getLife_datetime());
                    batchEntity.setProductDate(false);
                } else {
                    batchEntity.setProductDate(lotBean.getProduce_datetime());
                    batchEntity.setProductDate(true);
                }
                batchEntity.setProductCount(String.valueOf(lotBean.getHeight()));
                mBatchEntities.add(batchEntity);
            }
        } else {
            BatchEntity batchEntity = new BatchEntity();
            batchEntity.setProductDate(TimeUtils.getYMD(Calendar.getInstance().getTime()));
            batchEntity.setProductDate(true);
            batchEntity.setProductCount(String.valueOf((int) mBean.getProductUomQty()));
            mBatchEntities.add(batchEntity);
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @OnClick({R.id.iv_cancle, R.id.btn_confirm})
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
                    if (TextUtils.isEmpty(batchEntity.getProductDate())) {
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
                intent.putExtra(INTENT_KEY_PRODUCT, (Parcelable) mBean);
                setResult(RESULT_OK, intent);
                finish();
                break;


        }
    }


    public class BatchListAdapter extends BaseAdapter {
        CallBack mCallBack;

        public void setCallback(CallBack callBack) {
            mCallBack = callBack;
        }

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_edit_batch_new, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.etProductCount.setTag(position);
            viewHolder.ivDelete.setVisibility(position != 0 ? View.VISIBLE : View.GONE);
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBatchEntities.remove(position);
                    notifyDataSetChanged();
                }
            });
            viewHolder.tvProductDateValue.setText(mBatchEntities.get(position).getProductDate());
            viewHolder.tvProductDateValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack != null) {
                        mCallBack.onCall(position);
                    }
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
                    String strSource = s.toString();
                    int dotIndex = strSource.indexOf(".");
                    if(dotIndex>=0){
                        int length = strSource.substring(dotIndex,strSource.length()-1).length();
                        if(length>2){
                            String dest = strSource.substring(0,dotIndex+3);
                            viewHolder.etProductCount.setText(dest);
                            viewHolder.etProductCount.setSelection(dest.length());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mBatchEntities.get(position).setProductCount(s.toString().trim());
                }
            });

            viewHolder.etProductCount.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.setFocusableInTouchMode(true);
                        view.requestFocus();
                        ((EditText) view).selectAll();
                    }
                    return false;
                }
            });

            viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productCountStr = mBatchEntities.get(position).getProductCount();
                    if (TextUtils.isEmpty(productCountStr)) {
                        productCountStr = "0";
                    }
                    double productCount = Double.parseDouble(productCountStr);
                    productCount = BigDecimal.valueOf(productCount).add(BigDecimal.ONE).doubleValue();
                    mBatchEntities.get(position).setProductCount(NumberUtil.getIOrD(productCount));
                    viewHolder.etProductCount.setText(NumberUtil.getIOrD(productCount));
                }
            });
            viewHolder.ivReduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productCountStr = mBatchEntities.get(position).getProductCount();
                    if (TextUtils.isEmpty(productCountStr) || productCountStr.equals("0")) {
                        return;
                    }
                    double productCount = Double.parseDouble(productCountStr);
                    productCount = BigDecimal.valueOf(productCount).subtract(BigDecimal.ONE).doubleValue();
                    if(productCount<0)productCount = 0;
                    mBatchEntities.get(position).setProductCount(NumberUtil.getIOrD(productCount));
                    viewHolder.etProductCount.setText(NumberUtil.getIOrD(productCount));
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
