package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.bean.ReceiveProEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReceiveBean;
import com.runwise.supply.firstpage.entity.ReceiveRequest;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.swipmenu.SwipeMenu;
import com.runwise.supply.view.swipmenu.SwipeMenuCreator;
import com.runwise.supply.view.swipmenu.SwipeMenuItem;
import com.runwise.supply.view.swipmenu.SwipeMenuListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.runwise.supply.firstpage.ReceiveActivity.REQUEST_CODE_ADD_BATCH;

;

/**
 * Created by libin on 2017/7/16.
 */

public class ReceiveFragment extends BaseFragment {
    private DoActionCallback callback;

    public void setCallback(DoActionCallback callback) {
        this.callback = callback;
    }

    public DataType type;
    @ViewInject(R.id.pullListView)
    private SwipeMenuListView pullListView;
    private ReceiveAdapter adapter;
    //跟自己类型配对的数据即可。
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    //存放该类型下用户输入的值
    private Map<String, ReceiveBean> countMap = new HashMap<>();
    //收点货模式
    private int mode;
    OrderResponse.ListBean orderBean;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ReceiveAdapter();
        ReceiveActivity activity = (ReceiveActivity) getActivity();
        adapter.isSettle = activity.isSettle();

        pullListView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                openItem.setBackground(new ColorDrawable(Color.parseColor("#fec159")));
                openItem.setWidth(500);
                openItem.setTitle("确认数量一致");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        pullListView.setMenuCreator(creator);
        pullListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) datas.get(position);
                String pId = String.valueOf(bean.getProductID());
                ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(pId);
                adapter.setReceiveCount((int) bean.getProductUomQty(), basicBean, bean);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        ArrayList<OrderResponse.ListBean.LinesBean> linesList = getArguments().getParcelableArrayList("datas");
        orderBean = getArguments().getParcelable("order");
        mode = getArguments().getInt("mode");
        adapter.isSettle = orderBean.isIsTwoUnit() && !orderBean.getDeliveryType().equals("fresh_vendor_delivery");
        if (type == DataType.ALL) {
            datas.addAll(linesList);
        } else {
            for (OrderResponse.ListBean.LinesBean bean : linesList) {
                if (bean.getStockType().equals(type.getType())) {
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
    public void onCountSynEvent(ReceiveProEvent event) {
        ReceiveActivity activity = (ReceiveActivity) getActivity();
        Map<String, ReceiveBean> map = activity.getCountMap();
        if (map != null) {
            countMap.clear();
            countMap.putAll(map);
        }
        if (event.isNotifyDataSetChange()) {
            adapter.notifyDataSetChanged();
        }
    }

    public class ReceiveAdapter extends IBaseAdapter implements ListAdapter {
        private boolean isSettle;       //是不是双单位
        private boolean canSeePrice;

        public ReceiveAdapter() {
            super();
            canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        }

        @Override
        public boolean isEnabled(int position) {
            final OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            String pId = String.valueOf(bean.getProductID());
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(pId);
            if (orderBean.getDeliveryType().equals("vendor_delivery") && basicBean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)){
                return false;
            }
                return super.isEnabled(position);
        }

        @Override
        protected View getExView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            String pId = String.valueOf(bean.getProductID());
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(pId);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.receive_list_item, null);
                ViewUtils.inject(viewHolder, convertView);
                //双人收货模式下，按钮隐藏
//                if (mode == 2) {
//                    viewHolder.doBtn.setVisibility(View.INVISIBLE);
//                } else {
//                    viewHolder.doBtn.setVisibility(View.VISIBLE);
//                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceiveBean rb = new ReceiveBean();
                    if (basicBean != null) {
                        rb.setName(basicBean.getName());
//                        rb.setCount((int)bean.getProductUomQty());
                        rb.setCount(0);
                        rb.setProductId(bean.getProductID());
                        if (isSettle) {
                            rb.setTwoUnit(true);
                            rb.setUnit(basicBean.getSettleUomId());
                        } else {
                            rb.setTwoUnit(false);
                        }
                        if (callback != null) {
                            callback.doAction(rb);
                        }
                    }

                }
            });
            if (basicBean != null) {
                viewHolder.name.setText(basicBean.getName());
                if (basicBean.getImage() != null)
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.sdv, Constant.BASE_URL + basicBean.getImage().getImageSmall());
                StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
                if (canSeePrice) {
                    sb.append("  ").append(basicBean.getUnit()).append("\n").append(bean.getPriceUnit()).append("元/").append(bean.getProductUom());
                }
                viewHolder.content.setText(sb.toString());
                viewHolder.countTv.setText("/" + (int) bean.getProductUomQty() + basicBean.getUom());
                viewHolder.receivedTv.setSaveEnabled(false);
                if (orderBean.getDeliveryType().equals("vendor_delivery") && basicBean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)) {
                    viewHolder.receivedTv.setFocusable(false);
                    viewHolder.inputAdd.setVisibility(View.GONE);
                    viewHolder.countLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, EditBatchActivity.class);
                            intent.putExtra(EditBatchActivity.INTENT_KEY_PRODUCT, bean);
                            if (countMap.containsKey(String.valueOf(bean.getProductID()))) {
                                ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
                                ArrayList<ReceiveRequest.ProductsBean.LotBean> lotBeens = (ArrayList<ReceiveRequest.ProductsBean.LotBean>) rb.getLot_list();
                                intent.putExtra(EditBatchActivity.INTENT_KEY_BATCH_ENTITIES, lotBeens);
                            }
                            startActivityForResult(intent, REQUEST_CODE_ADD_BATCH);
                        }
                    });
                } else {
                    viewHolder.receivedTv.setFocusable(true);
                    viewHolder.receivedTv.setFocusableInTouchMode(true);
                    viewHolder.receivedTv.requestFocus();
                    viewHolder.receivedTv.findFocus();
                    viewHolder.countLL.setOnClickListener(null);
                    viewHolder.inputAdd.setVisibility(View.VISIBLE);
                }
                //优先用已输入的数据，没有，则用默认
                if (mode == 2) {
                    viewHolder.receivedTv.setText(bean.getTallyingAmount() + "");
                    Log.i("receivedTv", "022  " + bean.getTallyingAmount());
//                    viewHolder.weightTv.setText(bean.getSettleAmount() + basicBean.getSettleUomId());
                } else {
                    if (countMap.containsKey(String.valueOf(bean.getProductID()))) {
                        ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
                        viewHolder.receivedTv.setText(rb.getCount() + "");
//                        viewHolder.weightTv.setText(rb.getTwoUnitValue() + rb.getUnit());
                    } else {
                        viewHolder.receivedTv.setText("0");
//                        viewHolder.weightTv.setText("0" + basicBean.getSettleUomId());
                    }
                }

                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.inputAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int num = Integer.parseInt(finalViewHolder.receivedTv.getText().toString());
                        if (basicBean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT) && orderBean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_VENDOR_DELIVERY)) {
                            setReceiveCount(num, basicBean, bean);
                            return;
                        }
                        finalViewHolder.receivedTv.setText(String.valueOf(num + 1));
                    }
                });
//                finalViewHolder.receivedTv.removeTextChangedListener();
                finalViewHolder.receivedTv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (orderBean.getDeliveryType().equals("vendor_delivery") && basicBean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)) {
                            return;
                        }
                        if (TextUtils.isEmpty(s.toString())) {
                            setReceiveCount(0, basicBean, bean);
                            return;
                        }
                        int editCount = Integer.parseInt(s.toString());
                        ReceiveBean receiveBean = countMap.get(String.valueOf(bean.getProductID()));
                        if (receiveBean != null && receiveBean.getCount() == editCount) {
                            return;
                        }
                        setReceiveCount(editCount, basicBean, bean);
                    }
                });
            }
            //双单位是跟订单相关，所以拿订单里面的字段判断.
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.line.getLayoutParams();
            int paddingLeft = CommonUtils.dip2px(mContext,15);
            if (isSettle) {
                //显示双单位信息，加号按钮隐藏

                viewHolder.countLL.setVisibility(View.GONE);
                viewHolder.settleLL.setVisibility(View.VISIBLE);
                params.setMargins(paddingLeft,CommonUtils.dip2px(mContext,42),0,0);

            } else {
                viewHolder.countLL.setVisibility(View.VISIBLE);
                viewHolder.settleLL.setVisibility(View.GONE);
                params.setMargins(paddingLeft,paddingLeft,0,0);

            }
            return convertView;
        }

        private void setReceiveCount(int count, ProductBasicList.ListBean basicBean, OrderResponse.ListBean.LinesBean bean) {
            ReceiveBean rb = new ReceiveBean();
            if (basicBean != null) {
                rb.setName(basicBean.getName());
                rb.setTracking(basicBean.getTracking());
//                        rb.setCount((int)bean.getProductUomQty());
                rb.setCount(count);
                rb.setProductId(bean.getProductID());
                rb.setImageBean(basicBean.getImage());
                rb.setDefaultCode(basicBean.getDefaultCode());
                rb.setUnit(basicBean.getUnit());
                rb.setStockType(bean.getStockType());
                if (isSettle) {
                    rb.setTwoUnit(true);
                    rb.setUnit(basicBean.getSettleUomId());
                } else {
                    rb.setTwoUnit(false);
                }
                countMap.put(String.valueOf(bean.getProductID()), rb);
                if (callback != null) {
                    callback.doAction(rb);
                }
            }
        }

        class ViewHolder {
            @ViewInject(R.id.productImage)
            SimpleDraweeView sdv;
            @ViewInject(R.id.name)
            TextView name;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.countTv)
            TextView countTv;
            @ViewInject(R.id.receivedTv)
            EditText receivedTv;
            @ViewInject(R.id.settleLL)
            LinearLayout settleLL;
            @ViewInject(R.id.countLL)
            LinearLayout countLL;
            @ViewInject(R.id.doBtn)
            TextView doBtn;
            @ViewInject(R.id.input_add)
            ImageButton inputAdd;
            @ViewInject(R.id.line)
            View line;


        }
    }
}
