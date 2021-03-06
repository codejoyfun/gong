package com.runwise.supply.repertory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.RegisterActivity;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.entity.ShowInventoryNoticeEvent;
import com.runwise.supply.event.RefreshEvent;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.InventoryCacheManager;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static com.runwise.supply.repertory.InventoryActivity.INTENT_KEY_INVENTORY_BEAN;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_START_THE_INVENTORY;

/**
 * 库存
 */
public class MainRepertoryFragment extends NetWorkFragment {
    private final int REQUEST_EXIT = 1;
    private final int REQUEST_INVENTORY = 2;
    @ViewInject(R.id.tipLayout)
    private View tipLayout;

    @ViewInject(R.id.titleLayout)
    private View titleLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true, "库存" );
        this.setTitleLeftIcon(true, R.drawable.searchbar_ico_search);
        setTitleRightText(true,"开始盘点");
        FragmentManager manager = this.getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contextLayout,new StockFragment()).commitAllowingStateLoss();
//        manager.beginTransaction().replace(R.id.contextLayout,new RepertoryFragment()).commitAllowingStateLoss();
        manager.beginTransaction().addToBackStack(null);//add the transaction to the back stack so the user can navigate back
// Commit the transaction
        manager.beginTransaction().commit();
        boolean isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            tipLayout.setVisibility(View.GONE);
        }
        else{
            tipLayout.setVisibility(View.VISIBLE);
        }
        titleLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            EventBus.getDefault().post(new RefreshEvent());
        }
    }


    @OnClick(R.id.left_layout)
    public void leftClick(View view){
        startActivity(new Intent(mContext,DealerSearchActivity.class));
    }
    boolean mCreateInventoryIng = false;

    @OnClick(R.id.right_layout)
    public void rightClick(View view){
        MobclickAgent.onEvent(getActivity(), EVENT_ID_START_THE_INVENTORY);
        if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
        boolean isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            if (mCreateInventoryIng){
                return;
            }
            mCreateInventoryIng = true;
            Object parma = null;
//            sendConnection("/api/inventory/create", parma, REQUEST_EXIT, true, PandianResult.class);
            sendConnection("/api/inventory/create",parma,REQUEST_INVENTORY,true,InventoryResponse.class);
        }
        else{
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }
    @OnClick({R.id.tipLayout,R.id.closeTip})
    public void loginTipLayout(View view){
        switch (view.getId()) {
            case R.id.tipLayout:
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.closeTip:
                tipLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        tipLayout.setVisibility(View.GONE);
    }

    @Override
    public void onUserLoginout() {
        tipLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_EXIT:
                PandianResult repertoryEntity = (PandianResult)result.getResult().getData();
                Intent intent =  new Intent(getContext(), EditRepertoryListActivity.class);
                intent.putExtra("bean",repertoryEntity);
                startActivity(intent);
                break;
            case REQUEST_INVENTORY:
                InventoryResponse inventoryResponse = (InventoryResponse)result.getResult().getData();
                Intent intent1 = new Intent(getActivity(),InventoryActivity.class);
                InventoryResponse.InventoryBean inventoryBean = inventoryResponse.getInventory();
                //检查是否有缓存
                InventoryResponse.InventoryBean cacheBean = InventoryCacheManager.getInstance(getActivity()).loadInventory(inventoryBean.getInventoryID());
                if(cacheBean!=null)inventoryBean = cacheBean;
                //有确认中的盘点单，则显示盘点通知
                boolean isInProgresss = "confirm".equals(inventoryBean.getState());
                InventoryCacheManager.getInstance(getActivity()).setCurrentInventoryId(inventoryBean.getInventoryID());
                if(isInProgresss) {
                    if(getActivity()!=null)InventoryCacheManager.getInstance(getActivity()).setIsInventory(true);//记录，不可其它入库出库操作了
                }else{
                    if(getActivity()!=null)InventoryCacheManager.getInstance(getActivity()).setIsInventory(false);
                }
                intent1.putExtra(INTENT_KEY_INVENTORY_BEAN,inventoryBean);
                startActivity(intent1);
                mCreateInventoryIng = false;
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where) {
            case REQUEST_EXIT:
            case REQUEST_INVENTORY:
                mCreateInventoryIng = false;
                if (errMsg.equals(getResources().getString(com.kids.commonframe.R.string.network_error))){
                    ToastUtil.show(getActivity(),errMsg);
                    return;
                }
                dialog.setModel(CustomDialog.LEFT);
                dialog.setMessage(errMsg);
                dialog.setLeftBtnListener("我知道了", null);
                dialog.show();
                break;
        }
    }

    /**
     * 当前是否正在盘点中
     * @param inventoryInProgressEvent
     */
    @Subscribe
    public void onInventoryInProgress(ShowInventoryNoticeEvent inventoryInProgressEvent){

    }

    private InventoryResponse.InventoryBean testData(){
        InventoryResponse inventoryResponse = new InventoryResponse();
        InventoryResponse.InventoryBean inventoryBean = new InventoryResponse.InventoryBean();
        inventoryBean.setCreateDate("2017-12-12");
        inventoryBean.setCreateUser(SampleApplicationLike.getInstance().getUserName());
        inventoryBean.setInventoryID(2312);
        inventoryResponse.setInventory(inventoryBean);
        inventoryBean.setLines(new ArrayList<>());
        for(int i=0;i<10;i++){
            InventoryResponse.InventoryProduct inventoryProduct = new InventoryResponse.InventoryProduct();
            inventoryProduct.setProductID(651);
            inventoryBean.getLines().add(inventoryProduct);
            inventoryProduct.setLotList(new ArrayList<>());
            inventoryProduct.setProduct(ProductBasicUtils.getBasicMap(getActivity()).get(inventoryProduct.getProductID()+""));
            for(int j=1;j<i+2;j++){
                InventoryResponse.InventoryLot lot = new InventoryResponse.InventoryLot();
                lot.setLifeEndDate("2017-12-1"+(i%5)+" 00:00:00");
                lot.setTheoreticalQty(j);
                lot.setEditNum(j);
                lot.setUom("包");
                lot.setLotNum("313251235"+i+j);
                inventoryProduct.getLotList().add(lot);
            }
        }
        return inventoryResponse.getInventory();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("库存首页");
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("库存首页");
    }
}

