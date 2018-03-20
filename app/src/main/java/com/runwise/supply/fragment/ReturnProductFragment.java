package com.runwise.supply.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ReturnProductAdapter;
import com.runwise.supply.event.IntEvent;
import com.runwise.supply.firstpage.ReturnDetailAdapter;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;

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
 * Created by mike on 2017/9/6.
 */

public class ReturnProductFragment extends BaseFragment {

//    @BindView(recyclerView)
//    RecyclerView mRecyclerView;
    @BindView(recyclerView)
    ListView mRecyclerView;
    @BindView(R.id.loadingLayout)
    RelativeLayout loadingLayout;
    Unbinder unbinder;
    int mHeight;

    @BindView(R.id.priceLL)
    View priceLL;
    @BindView(R.id.countTv)
    TextView countTv;
    @BindView(R.id.ygMoneyTv)
    TextView ygMoneyTv;

    ReturnDetailAdapter mReturnDetailAdapter;

    private ReturnProductAdapter mReturnProductAdapter;
    private List<ReturnOrderBean.ListBean.LinesBean> listDatas;
    private ReturnOrderBean.ListBean bean;

    public static final String BUNDLE_KEY_LIST = "bundle_key_list";
    public static final String BUNDLE_KEY_BEAN = "bundle_key_bean";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_return_product;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mReturnDetailAdapter = new ReturnDetailAdapter(getActivity());
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mRecyclerView.setAdapter(mReturnDetailAdapter);

        listDatas = (List<ReturnOrderBean.ListBean.LinesBean>) getArguments().getSerializable(BUNDLE_KEY_LIST);
        bean = getArguments().getParcelable(BUNDLE_KEY_BEAN);
//        mReturnDetailAdapter.setReturnList(listDatas);

        mReturnProductAdapter = new ReturnProductAdapter(getActivity());
        mRecyclerView.setAdapter(mReturnProductAdapter);

        mReturnProductAdapter.setProductList(listDatas);
        mReturnProductAdapter.setStatus(bean.getName(),bean.getState(),bean);
        mReturnProductAdapter.setTwoUnit(bean.isIsTwoUnit());

        boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        if (!canSeePrice) {
            priceLL.setVisibility(View.GONE);
        }

        countTv.setText(NumberUtil.getIOrD(bean.getAmount()) + "件");
        //商品数量/预估金额
        DecimalFormat df = new DecimalFormat("#.##");
        ygMoneyTv.setText("¥" + df.format(bean.getAmountTotal()));
        setUpBottomView();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGetHeight(IntEvent intEvent) {
//        mHeight = intEvent.getHeight();
//        if (loadingLayout != null){
//            ViewGroup.LayoutParams layoutParams = loadingLayout.getLayoutParams();
//            layoutParams.height = mHeight;
//            loadingLayout.setLayoutParams(layoutParams);
//            loadingLayout.invalidate();
//            loadingLayout.requestLayout();
//            if (listDatas.size() == 0){
//                loadingLayout.setVisibility(View.VISIBLE);
//            }
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            ((TextView)view.findViewById(R.id.countTv)).setText(NumberUtil.getIOrD(bean.getAmount()) + "件");
            //商品数量/预估金额
            DecimalFormat df = new DecimalFormat("#.##");
            ((TextView)view.findViewById(R.id.ygMoneyTv)).setText("¥" + df.format(bean.getAmountTotal()));
            mRecyclerView.addFooterView(view);
        }
    }
}