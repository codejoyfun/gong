package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.bean.ReceiveProEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReceiveBean;
import com.runwise.supply.firstpage.entity.ReceiveRequest;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.swipmenu.SwipeMenu;
import com.runwise.supply.view.swipmenu.SwipeMenuCreator;
import com.runwise.supply.view.swipmenu.SwipeMenuItem;
import com.runwise.supply.view.swipmenu.SwipeMenuListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.firstpage.ReceiveActivity.REQUEST_CODE_ADD_BATCH;

;

/**
 * Created by libin on 2017/7/16.
 */

public class ReceiveFragment extends BaseFragment {
    private DoActionCallback callback;
    public static final String BUNDLE_KEY_LIST = "bundle_key_list";

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
                adapter.setReceiveCount(bean.getProductUomQty(),bean);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        final ArrayList<OrderResponse.ListBean.LinesBean> linesList = getArguments().getParcelableArrayList("datas");
        orderBean = getArguments().getParcelable("order");
        mode = getArguments().getInt("mode");
        adapter.isSettle = orderBean.isIsTwoUnit() && !orderBean.getDeliveryType().equals("fresh_vendor_delivery");
        datas.addAll(linesList);
        adapter.setData(datas);
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startEditBatch(linesList.get(position));
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ProductQueryEvent event) {
        String word = event.getSearchWord();
        //只在当前类型下面找名称包括的元素
        List<OrderResponse.ListBean.LinesBean> findArray = findArrayByWord(word);
        adapter.setData(findArray);
    }

    //返回当前标签下名称包含的
    private List<OrderResponse.ListBean.LinesBean> findArrayByWord(String word) {
        List<OrderResponse.ListBean.LinesBean> findList = new ArrayList<>();
        for (OrderResponse.ListBean.LinesBean bean : datas) {
            if (bean.getName().contains(word)) {
                findList.add(bean);
            }
        }
        return findList;
    }

    private void startEditBatch(OrderResponse.ListBean.LinesBean bean) {
        Intent intent = new Intent(mContext, EditBatchActivity.class);
        intent.putExtra(EditBatchActivity.INTENT_KEY_PRODUCT, (Parcelable) bean);
        if (countMap.containsKey(String.valueOf(bean.getProductID()))) {
            ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
            ArrayList<ReceiveRequest.ProductsBean.LotBean> lotBeens = (ArrayList<ReceiveRequest.ProductsBean.LotBean>) rb.getLot_list();
            intent.putExtra(EditBatchActivity.INTENT_KEY_BATCH_ENTITIES, lotBeens);
        }
        startActivityForResult(intent, REQUEST_CODE_ADD_BATCH);
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
        private int index = -1;

        public ReceiveAdapter() {
            super();
            canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        }

        @Override
        public boolean isEnabled(int position) {
            if (((ReceiveActivity) getActivity()).ShuangRensShouHuoQueRen) {
                return false;
            }
            if (position < 0) {
                return false;
            }
            final OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            String pId = String.valueOf(bean.getProductID());

            String tracking = null;
                tracking = bean.getTracking();
//          if (orderBean.getDeliveryType().equals("vendor_delivery") && basicBean != null && basicBean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)) {
            if (orderBean.getDeliveryType().equals("vendor_delivery") && ProductBasicList.ListBean.TRACKING_TYPE_LOT.equals(tracking)) {
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        protected View getExView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (orderBean.isNewType()) {//新版订单信息
                return getNewTypeExView(position, convertView, parent);
            }
            final OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.receive_list_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                viewHolder.receivedTv.setTag(position);
            }
            viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceiveBean rb = new ReceiveBean();
                    rb.setName(bean.getName());
//                        rb.setCount((int)bean.getProductUomQty());
                    rb.setCount(0);
                    rb.setProductId(bean.getProductID());
                    if (isSettle) {
                        rb.setTwoUnit(true);
                        rb.setUnit(bean.getUnit());
                    } else {
                        rb.setTwoUnit(false);
                    }
                    if (callback != null) {
                        callback.doAction(rb);
                    }

                }
            });
                viewHolder.name.setText(bean.getName());
                if (bean.getImageMedium() != null)
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.sdv, Constant.BASE_URL + bean.getImageMedium());
                StringBuffer sb = new StringBuffer(bean.getDefaultCode());
                sb.append(" | ").append(bean.getUnit());
                String uom = bean.getProductUom();
                if (!TextUtils.isEmpty(bean.getSaleUom())) {
                    uom = bean.getSaleUom();
                }
                if (canSeePrice) {
                    sb.append("\n").append(bean.getPriceUnit()).append("元/").append(uom);
                }
                viewHolder.content.setText(sb.toString());
                viewHolder.countTv.setText(" " + uom);
                viewHolder.mTvPurchaseCount.setText("订" + NumberUtil.getIOrD(bean.getProductUomQty()) + uom);

                if (orderBean.getDeliveryType().equals("vendor_delivery") && bean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)) {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startEditBatch(bean);
                        }
                    });
                } else {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String numText = viewHolder.receivedTv.getText().toString();
                            int num;
                            if (TextUtils.isEmpty(numText)) {
                                num = 0;
                            } else {
                                num = Integer.parseInt(numText);
                            }
                            setReceiveCount(num,bean);
                        }
                    });
                }

                //优先用已输入的数据，没有，则用默认
                if (mode == 2) {
                    viewHolder.receivedTv.setText(bean.getTallyingAmount() + "");
//                    Log.i("receivedTv", "022  " + bean.getTallyingAmount());
//                    viewHolder.weightTv.setText(bean.getSettleAmount() + basicBean.getSettleUomId());
                } else {
                    if (countMap.containsKey(String.valueOf(bean.getProductID()))) {
                        ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
//                        if (rb.getCount() != 0){
                        viewHolder.receivedTv.setText(NumberUtil.getIOrD(rb.getCount()));
//                        }
//                        viewHolder.weightTv.setText(rb.getTwoUnitValue() + rb.getUnit());
                    } else {
                        viewHolder.receivedTv.setText("0");
//                        Log.i("receivedTv", "011");
//                        viewHolder.weightTv.setText("0" + basicBean.getSettleUomId());
                    }
                }
            String str = viewHolder.receivedTv.getText().toString();
            if (!TextUtils.isEmpty(str)) {
                double count = Double.parseDouble(str);
                setUpBackground(convertView, count, bean.getProductUomQty());
            }
            //双单位相关
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.line.getLayoutParams();
            int margin = CommonUtils.dip2px(mContext, 15);
            if (isSettle) {
                //显示双单位信息，加号按钮隐藏
                StringBuffer receiveStr = new StringBuffer();
                StringBuffer weightStr = new StringBuffer();
                if (mode == 2) {
                    viewHolder.doBtn.setVisibility(View.INVISIBLE);
                    receiveStr.append(bean.getTallyingAmount());
                    weightStr.append(bean.getSettleAmount()).append(bean.getSettleUomId());
                } else {
                    viewHolder.doBtn.setVisibility(View.VISIBLE);
                    if (mode == 1) {
                        viewHolder.doBtn.setText("点货");
                    } else {
                        viewHolder.doBtn.setText("收货");
                    }
                    if (countMap.containsKey(String.valueOf(bean.getProductID()))) {
                        ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
                        receiveStr.append(NumberUtil.getIOrD(rb.getCount()));
                        weightStr.append(rb.getTwoUnitValue()).append(rb.getUnit());
                    } else {
                        receiveStr.append("0");
                        weightStr.append("0" + bean.getSettleUomId());
                    }
                }
                receiveStr.append(" /" + NumberUtil.getIOrD(bean.getProductUomQty()) + bean.getSaleUom());
                SpannableString builder = new SpannableString(receiveStr.toString());
                int end = receiveStr.indexOf(" ");
                builder.setSpan(new AbsoluteSizeSpan(16, true), 0, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                viewHolder.settleTv.setText(builder);
                viewHolder.weightTv.setText(weightStr.toString());
                viewHolder.countLL.setVisibility(View.GONE);
                viewHolder.settleLL.setVisibility(View.VISIBLE);
                params.setMargins(0, CommonUtils.dip2px(mContext, 42), margin, 0);

            } else {
                viewHolder.countLL.setVisibility(View.VISIBLE);
                viewHolder.settleLL.setVisibility(View.GONE);
                params.setMargins(0, margin, margin, 0);

            }
            return convertView;
        }

        private void setReceiveCount(double count, ProductBasicList.ListBean basicBean, OrderResponse.ListBean.LinesBean bean) {
            ReceiveBean rb = new ReceiveBean();
            if (basicBean != null) {
                rb.setName(basicBean.getName());
                rb.setTracking(basicBean.getTracking());
//                        rb.setCount((int)bean.getProductUomQty());
                rb.setCount(count);
                rb.setProductUomQty(bean.getProductUomQty());
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

        public void setUpBackground(View convertView, double actualCount, double sourceCount) {
            if (actualCount != sourceCount) {
                convertView.setBackgroundColor(getResources().getColor(R.color.receive_differ));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
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
            TextView receivedTv;
            @ViewInject(R.id.settleLL)
            LinearLayout settleLL;
            @ViewInject(R.id.countLL)
            RelativeLayout countLL;
            @ViewInject(R.id.doBtn)
            TextView doBtn;
            @ViewInject(R.id.line)
            View line;
            @ViewInject(R.id.settleTv)
            TextView settleTv;
            @ViewInject(R.id.weightTv)
            TextView weightTv;
            @ViewInject(R.id.tv_purchase_count)
            TextView mTvPurchaseCount;
        }

        /**
         * 新版订单，可以直接从订单信息获取商品详细信息
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        protected View getNewTypeExView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final OrderResponse.ListBean.LinesBean bean = (OrderResponse.ListBean.LinesBean) mList.get(position);
            String pId = String.valueOf(bean.getProductID());
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.receive_list_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                viewHolder.receivedTv.setTag(position);
            }
            viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceiveBean rb = new ReceiveBean();
                    rb.setName(bean.getName());
                    rb.setCount(0);
                    rb.setProductId(bean.getProductID());
                    if (isSettle) {
                        rb.setTwoUnit(true);
                        rb.setUnit("" + bean.getSettleUomId());
                    } else {
                        rb.setTwoUnit(false);
                    }
                    if (callback != null) {
                        callback.doAction(rb);
                    }
                }
            });
            viewHolder.name.setText(bean.getName());
            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sdv, Constant.BASE_URL + bean.getImageMedium());
            StringBuffer sb = new StringBuffer(bean.getDefaultCode());
            sb.append(" | ").append(bean.getUnit());
            String uom = bean.getProductUom();
            if (!TextUtils.isEmpty(bean.getSaleUom())) {
                uom = bean.getSaleUom();
            }

            if (canSeePrice) {
                sb.append("\n").append(bean.getPriceUnit()).append("元/").append(uom);
            }
            viewHolder.content.setText(sb.toString());
            viewHolder.countTv.setText(" " + uom);
            viewHolder.mTvPurchaseCount.setText("订" + NumberUtil.getIOrD(bean.getProductUomQty()) + uom);


            if (orderBean.getDeliveryType().equals("vendor_delivery") && bean.getTracking().equals(ProductBasicList.ListBean.TRACKING_TYPE_LOT)) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startEditBatch(bean);
                    }
                });
            } else {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String numText = viewHolder.receivedTv.getText().toString();
                        double num;
                        if (TextUtils.isEmpty(numText)) {
                            num = 0;
                        } else {
                            num = Double.parseDouble(numText);
                        }
                        setReceiveCount(num, bean);
                    }
                });
            }

            //优先用已输入的数据，没有，则用默认
            if (mode == 2) {
                viewHolder.receivedTv.setText(NumberUtil.getIOrD(bean.getTallyingAmount()));
//                    Log.i("receivedTv", "022  " + bean.getTallyingAmount());
//                    viewHolder.weightTv.setText(bean.getSettleAmount() + basicBean.getSettleUomId());
            } else {
                if (countMap.containsKey(String.valueOf(bean.getProductID()))) {
                    ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
//                        if (rb.getCount() != 0){
                    viewHolder.receivedTv.setText(NumberUtil.getIOrD(rb.getCount()));
                    if (!rb.isChange()) {
                        if (bean.getActualSendNum() != bean.getProductUomQty()) {
                            viewHolder.receivedTv.setTextColor(getResources().getColor(R.color.receive_count));
                        } else {
                            viewHolder.receivedTv.setTextColor(getResources().getColor(R.color.receive_input_count));
                        }
                    } else {
                        viewHolder.receivedTv.setTextColor(getResources().getColor(R.color.receive_input_count));
                    }
//                        }
//                        viewHolder.weightTv.setText(rb.getTwoUnitValue() + rb.getUnit());
                } else {
                    viewHolder.receivedTv.setText("0");
//                        Log.i("receivedTv", "011");
//                        viewHolder.weightTv.setText("0" + basicBean.getSettleUomId());
                }
            }


            String str = viewHolder.receivedTv.getText().toString();
            if (!TextUtils.isEmpty(str)) {
                double count = Double.parseDouble(str);
                setUpBackground(convertView, count, bean.getProductUomQty());
            }
            //双单位相关
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.line.getLayoutParams();
            int margin = CommonUtils.dip2px(mContext, 15);
            if (isSettle) {
                //显示双单位信息，加号按钮隐藏
                StringBuffer receiveStr = new StringBuffer();
                StringBuffer weightStr = new StringBuffer();
                if (mode == 2) {
                    viewHolder.doBtn.setVisibility(View.INVISIBLE);
                    receiveStr.append(bean.getTallyingAmount());
                    weightStr.append(bean.getSettleAmount()).append(bean.getSettleUomId());
                } else {
                    viewHolder.doBtn.setVisibility(View.VISIBLE);
                    if (mode == 1) {
                        viewHolder.doBtn.setText("点货");
                    } else {
                        viewHolder.doBtn.setText("收货");
                    }
                    if (countMap.containsKey(String.valueOf(bean.getProductID()))) {
                        ReceiveBean rb = countMap.get(String.valueOf(bean.getProductID()));
                        receiveStr.append(rb.getCount());
                        weightStr.append(rb.getTwoUnitValue()).append(rb.getUnit());
                    } else {
                        receiveStr.append("0");
                        weightStr.append("0" + bean.getSettleUomId());
                    }
                }
                receiveStr.append(" /" + NumberUtil.getIOrD(bean.getProductUomQty()) + uom);
                SpannableString builder = new SpannableString(receiveStr.toString());
                int end = receiveStr.indexOf(" ");
                builder.setSpan(new AbsoluteSizeSpan(16, true), 0, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                viewHolder.settleTv.setText(builder);
                viewHolder.weightTv.setText(weightStr.toString());
                viewHolder.countLL.setVisibility(View.GONE);
                viewHolder.settleLL.setVisibility(View.VISIBLE);
                params.setMargins(0, CommonUtils.dip2px(mContext, 42), margin, 0);

            } else {
                viewHolder.countLL.setVisibility(View.VISIBLE);
                viewHolder.settleLL.setVisibility(View.GONE);
                params.setMargins(0, margin, margin, 0);

            }
            return convertView;
        }

        /**
         * 新版订单，可以直接从订单信息获取商品详细信息
         */
        private void setReceiveCount(double count, OrderResponse.ListBean.LinesBean bean) {
            ReceiveBean rb = new ReceiveBean();
            rb.setName(bean.getName());
            rb.setTracking(bean.getTracking());
//                        rb.setCount((int)bean.getProductUomQty());
            rb.setCount(count);
            rb.setProductUomQty(bean.getProductUomQty());
            rb.setProductId(bean.getProductID());
            rb.setDefaultCode(bean.getDefaultCode());
            rb.setUnit(bean.getUnit());
            rb.setStockType(bean.getStockType());
            if (isSettle) {
                rb.setTwoUnit(true);
                rb.setUnit("" + bean.getSettleUomId());
            } else {
                rb.setTwoUnit(false);
            }
            countMap.put(String.valueOf(bean.getProductID()), rb);
            if (callback != null) {
                callback.doAction(rb);
            }
        }
    }
}

