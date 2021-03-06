package com.runwise.supply.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderProductAdapter;
import com.runwise.supply.event.IntEvent;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.ProductImageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.R.id.recyclerView;

/**
 * Created by mike on 2017/9/5.
 */

public class OrderProductFragment extends BaseFragment {

    @BindView(recyclerView)
    ListView mRecyclerView;
    @BindView(R.id.loadingLayout)
    RelativeLayout loadingLayout;
    Unbinder unbinder;

    OrderProductAdapter mOrderDtailAdapter;
    private List<OrderResponse.ListBean.LinesBean> listDatas;

    public static final String BUNDLE_KEY_LIST = "bundle_key_list";
    public static final String BUNDLE_KEY_NAME = "bundle_key_name";
    public static final String BUNDLE_KEY_STATE = "bundle_key_state";
    public static final String BUNDLE_KEY_RETURN = "bundle_key_return";
    public static final String BUNDLE_KEY_TWO_UNIT = "bundle_key_two_unit";
    public static final String BUNDLE_KEY_ORDER_DATA = "bundle_key_order_data";
    int mHeight;
    OrderResponse.ListBean listBean;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_order_product;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOrderDtailAdapter = new OrderProductAdapter(getActivity());
        mRecyclerView.setAdapter(mOrderDtailAdapter);
        listDatas = (List<OrderResponse.ListBean.LinesBean>) getArguments().getSerializable(BUNDLE_KEY_LIST);
        String name = getArguments().getString(BUNDLE_KEY_NAME);
        String state = getArguments().getString(BUNDLE_KEY_STATE);
        listBean = (OrderResponse.ListBean) getArguments().getParcelable(BUNDLE_KEY_ORDER_DATA);

        mOrderDtailAdapter.setProductList(listDatas);
        mOrderDtailAdapter.setStatus(name,state,listBean);
        mOrderDtailAdapter.setHasReturn(getArguments().getBoolean(BUNDLE_KEY_RETURN));
        mOrderDtailAdapter.setTwoUnit(getArguments().getBoolean(BUNDLE_KEY_TWO_UNIT));

        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductImageDialog productImageDialog = new ProductImageDialog(getActivity());
                OrderResponse.ListBean.LinesBean linesBean = listDatas.get(position);
                ProductBasicList.ListBean listBean = new ProductBasicList.ListBean();

                ImageBean image = new ImageBean();
                image.setImage(linesBean.getImageBig());
                listBean.setImage(image);

                listBean.setName(linesBean.getName());
                listBean.setDefaultCode(linesBean.getDefaultCode());
                listBean.setPrice(linesBean.getProductPrice());

                listBean.setExplain(linesBean.getExplain());
                listBean.setSaleUom(linesBean.getSaleUom());
                listBean.setUnit(linesBean.getUnit());

                productImageDialog.setListBean(listBean);
                productImageDialog.show();
            }
        });


        boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
//        setUpBottomView();
    }

    private void setUpBottomView(){
        if (listDatas.size() == 0){
            mRecyclerView.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.orderdetail_bottom_item).setVisibility(View.GONE);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.orderdetail_bottom_item,null);
            boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
            if (!canSeePrice) {
                view.findViewById(R.id.priceLL).setVisibility(View.GONE);
            }
            //实收判断
            if ((Constant.ORDER_STATE_DONE.equals(listBean.getState()) || Constant.ORDER_STATE_RATED.equals(listBean.getState())) && listBean.getDeliveredQty() != listBean.getAmount()) {
                ((TextView)view.findViewById(R.id.countTv)).setText(NumberUtil.getIOrD(listBean.getDeliveredQty()) + "件");
            } else {
                ((TextView)view.findViewById(R.id.countTv)).setText(NumberUtil.getIOrD(listBean.getAmount()) + "件");
            }
            //商品数量/预估金额
            DecimalFormat df = new DecimalFormat("#.##");
            ((TextView)view.findViewById(R.id.ygMoneyTv)).setText("¥" + df.format(listBean.getAmountTotal()));
            mRecyclerView.addFooterView(view);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetHeight(IntEvent intEvent) {
        mHeight = intEvent.getHeight();
        if (loadingLayout != null){
//            int defaultPageHeight = DensityUtil.dip2px(getContext(),200);
//            ViewGroup.LayoutParams layoutParams = loadingLayout.getLayoutParams();
//            layoutParams.height = defaultPageHeight;
//            loadingLayout.setLayoutParams(layoutParams);
//            loadingLayout.invalidate();
//            loadingLayout.requestLayout();
            if (listDatas.size() == 0){
                loadingLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
