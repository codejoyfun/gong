package com.runwise.supply.repertory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.event.InventoryEditEvent;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.EditRepertoryResult;
import com.runwise.supply.repertory.entity.EditRequest;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.InventoryCacheManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;

import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_ADD_INVENTORY_PRODUCT;
import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_SUBMIT_THE_INVENTORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.repertory.EditRepertoryAddActivity.INTENT_FILTER;
import static com.runwise.supply.repertory.InventoryFragment.INTENT_CATEGORY;

/**
 * 新界面的盘点
 *
 * Created by Dong on 2017/12/8.
 */

public class InventoryActivity extends NetWorkActivity {
    private static final int INVENTORY_COMMIT = 0x34;
    public static final String INTENT_KEY_INVENTORY_BEAN = "inventory_bean";//传入盘点对象
    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.drag_layout)
    public DragTopLayout dragLayout;
    @ViewInject(R.id.viewpager)
    private ViewPager viewpager;
    @ViewInject(R.id.tv_inventory_id)
    private TextView mTvInventoryId;
    @ViewInject(R.id.tv_inventory_person)
    private TextView mTvInventoryPerson;
    @ViewInject(R.id.tv_inventory_date)
    private TextView mTvInventoryDate;
    InventoryResponse.InventoryBean mInventoryBean;
    CategoryRespone categoryRespone;
    List<Fragment> orderProductFragmentList;
    private double mInventoryTotal = 0;//盘点后总数
    private boolean isSubmitted = false;//是否提交，提交了则onStop不保存
    boolean mAddProduct = false;
    public static final int REQUEST_CHECK_DETAIL = 1 << 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);
        setTitleText(true,"盘点单");
        showBackBtn();
        setTitleRightText(true,"添加");
        dragLayout.setOverDrag(false);
        mInventoryBean = (InventoryResponse.InventoryBean) getIntent().getSerializableExtra(INTENT_KEY_INVENTORY_BEAN);

        mTvInventoryId.setText(mInventoryBean.getName());
        mTvInventoryPerson.setText(mInventoryBean.getCreateUser());
        mTvInventoryDate.setText(mInventoryBean.getCreateDate());
//        initDialog();
        if (mInventoryBean.getLines() == null){
            Object request = null;
            sendConnection("/api/inventory/line/"+mInventoryBean.getInventoryID(),request,REQUEST_CHECK_DETAIL,true,CheckResult.class);
//            toast("该盘点单没有任何商品");
//            finish();
        }else{
            getCategory();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isSubmitted) {
            InventoryCacheManager.getInstance(this).saveInventory(mInventoryBean);
            InventoryCacheManager.getInstance(this).shouldShowInventoryInProgress(true);
        }
    }

    /**
     * 查询类别
     */
    private void getCategory(){
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage();
                break;
            case INVENTORY_COMMIT:
                isSubmitted = true;
                InventoryCacheManager.getInstance(this).removeInventory(mInventoryBean.getInventoryID());
                ToastUtil.show(mContext,"盘点成功");
                //计算原库存数量
                double total = 0;
//                for(InventoryResponse.InventoryProduct product:mInventoryBean.getLines()){
//                    if(product.getLotList()!=null){
//                        for(InventoryResponse.InventoryLot lot:product.getLotList()){
//                            total = total + lot.getTheoreticalQty();
//                        }
//                    }else{
//                        total = total + product.getTheoreticalQty();
//                    }
//                }
                //计算盘点后数量
                EditRepertoryFinishActivity.start(this,total,mInventoryTotal);
                InventoryCacheManager.getInstance(this).shouldShowInventoryInProgress(false);
                finish();
            case REQUEST_CHECK_DETAIL:
                BaseEntity.ResultBean resultBean3 = result.getResult();
                CheckResult checkResult = (CheckResult) resultBean3.getData();
                if (checkResult.getList()!=null&&checkResult.getList().size()>0){
                    mInventoryBean.setLines(transfrom(checkResult.getList().get(0).getLines()));
                    getCategory();
                }

                break;
        }
    }

    private List<InventoryResponse.InventoryProduct>  transfrom(List<PandianResult.InventoryBean.LinesBean> lines){
        List<InventoryResponse.InventoryProduct> inventoryProducts = new ArrayList<>();
        for (PandianResult.InventoryBean.LinesBean linesBean:lines){
            InventoryResponse.InventoryProduct inventoryProduct = new InventoryResponse.InventoryProduct();

            inventoryProduct.setUom(linesBean.getUom());
            inventoryProduct.setActualQty(linesBean.getActualQty());
            inventoryProduct.setCode(linesBean.getCode());

            inventoryProduct.setDiff((int) linesBean.getDiff());
            inventoryProduct.setEditNum(linesBean.getEditNum());
            inventoryProduct.setInventoryLineID(linesBean.getInventoryLineID());

            inventoryProduct.setLifeEndDate(linesBean.getLifeEndDate());
            inventoryProduct.setLotID(linesBean.getLotID());
            inventoryProduct.setLotNum(linesBean.getLotNum());

            inventoryProduct.setProduct(linesBean.getProduct());
            inventoryProduct.setProductID(linesBean.getProductID());
            inventoryProduct.setTheoreticalQty(linesBean.getTheoreticalQty());

            inventoryProduct.setLotList(linesBean.getLotList());
            inventoryProduct.setUnitPrice(linesBean.getUnitPrice());

            inventoryProducts.add(inventoryProduct);
        }
        return inventoryProducts;
    }


    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(this,errMsg);
    }

    private void setUpDataForViewPage() {
        if(categoryRespone == null){
            return;
        }
        orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<InventoryResponse.InventoryProduct>> map = new HashMap<>();
        titles.add("全部");
        for(String category:categoryRespone.getCategoryList()){
            titles.add(category);
            map.put(category,new ArrayList<>());
        }

        for (InventoryResponse.InventoryProduct inventoryProduct : mInventoryBean.getLines()) {
            ProductBasicList.ListBean listBean = inventoryProduct.getProduct();
            if (!TextUtils.isEmpty(listBean.getCategory())){
                ArrayList<InventoryResponse.InventoryProduct> productByCategory = map.get(listBean.getCategory());
                if (productByCategory == null) {
                    productByCategory = new ArrayList<>();
                    map.put(listBean.getCategory(), productByCategory);
                }
                productByCategory.add(inventoryProduct);
            }
        }

        for(String category:categoryRespone.getCategoryList()){
            ArrayList<InventoryResponse.InventoryProduct> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(category,value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        //加入全部
        orderProductFragmentList.add(0, newProductFragment("",mInventoryBean.getLines()));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        viewpager.setOffscreenPageLimit(orderProductFragmentList.size());
        tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewpager.setCurrentItem(position,false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public Fragment newProductFragment(String category, List<InventoryResponse.InventoryProduct> value) {
        InventoryFragment editRepertoryListFragment = new InventoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_CATEGORY,category);
        editRepertoryListFragment.setArguments(bundle);
        editRepertoryListFragment.setData(value);
        return editRepertoryListFragment;
    }

    @OnClick({R.id.tv_inventory_commit,R.id.tv_inventory_cache,R.id.title_tv_rigth})
    public void onBtnClicked(View v){
        switch (v.getId()){
            case R.id.tv_inventory_commit:
                MobclickAgent.onEvent(getActivityContext(), EVENT_ID_SUBMIT_THE_INVENTORY);
                CustomDialog dialog = new CustomDialog(this);
                dialog.setMessage("盘点成功，确认更新库存？");
                dialog.setTitleGone();
                dialog.setRightBtnListener("确认", (btn,dialg)->commit());
                dialog.show();
                break;
            case R.id.tv_inventory_cache:
                finish();
                break;
            case R.id.title_tv_rigth:
                //增加新商品，传入已有商品进行过滤
                Intent intent = new Intent(this,EditRepertoryAddActivity.class);
                ArrayList<Integer> filters = new ArrayList<>();
                for(InventoryResponse.InventoryProduct product:mInventoryBean.getLines()){
                    filters.add(product.getProductID());
                }
                intent.putExtra(INTENT_FILTER,filters);
                startActivity(intent);
                break;
        }
    }

    /**
     * 添加新商品
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewBean(NewAdd newBean) {
        if (!mAddProduct){
            mAddProduct = true;
            MobclickAgent.onEvent(getActivityContext(), EVENT_ID_ADD_INVENTORY_PRODUCT);
        }

        if (newBean.getType() == 1) {
            boolean isFind = false;
            for (InventoryResponse.InventoryProduct bean : mInventoryBean.getLines()) {
                if (bean.getProductID()==newBean.getInventoryProduct().getProductID()) {
                    bean.setEditNum(bean.getEditNum() + newBean.getBean().getEditNum());
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                mInventoryBean.getLines().add(0, newBean.getInventoryProduct());
            }
        } else {
            mInventoryBean.getLines().add(0, newBean.getInventoryProduct());
        }
        //adapter.notifyDataSetChanged();
        EventBus.getDefault().post(new InventoryEditEvent());
    }

    /**
     * 初始化修改商品数量dialog
     */
//    @ViewInject(R.id.name1)
//    private TextView mTvTitle;
//    @ViewInject(R.id.colseIcon1)
//    private ImageView mIvClose;
//    @ViewInject(R.id.et_product_amount1)
//    private EditText mEtCount;
//    @ViewInject(R.id.rl_dialog_add_sum)
//    private View mIncludeSumDialog;
//    @ViewInject(R.id.finalButton1)
//    private TextView mTvButton;
//    private void initDialog(){
//        mIvClose.setOnClickListener(v->{
//            finishDialog();
//        });
//        mIncludeSumDialog.setOnClickListener(v->{
//            finishDialog();
//        });
//    }
//
//    /**
//     * 显示修改没有批次的商品数量
//     * @param inventoryProduct
//     */
//    public void showAddSumDialog(InventoryResponse.InventoryProduct inventoryProduct){
//        mIncludeSumDialog.setVisibility(View.VISIBLE);
//        mTvTitle.setText(inventoryProduct.getProduct().getName());
//        mEtCount.setText(NumberUtil.getIOrD(inventoryProduct.getEditNum()));
//        mEtCount.selectAll();
//        mTvButton.setOnClickListener(v->{
//            String etValue = mEtCount.getText().toString();
//            if(TextUtils.isDigitsOnly(etValue)){
//                inventoryProduct.setEditNum(Double.valueOf(etValue));
//                EventBus.getDefault().post(new InventoryEditEvent());
//            }
//            finishDialog();
//        });
//    }
//
//    private void finishDialog(){
//        hideKeyboard();
//        mIncludeSumDialog.setVisibility(View.GONE);
//    }
//
//
//    InputMethodManager imm;
//    private void hideKeyboard(){
//        if(imm==null)imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        View v = getCurrentFocus();
//        if(imm!=null && v!=null)imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//    }

    /**
     * 提交盘点
     */
    private void commit(){
        EditRequest editRequest = new EditRequest();
        editRequest.setId(mInventoryBean.getInventoryID());
        editRequest.setState("done");
        List<EditRequest.ProductBean> editListBean = new ArrayList<>();
        mInventoryTotal = 0;
        for(InventoryResponse.InventoryProduct bean : mInventoryBean.getLines()) {
            if(bean.getLotList()==null){//无批次
                EditRequest.ProductBean productBean = new EditRequest.ProductBean();
                productBean.setProduct_id(bean.getProductID());
                productBean.setId((int)bean.getInventoryLineID());
                productBean.setActual_qty(bean.getEditNum());
                productBean.setLot_id((int)bean.getLotID());
                productBean.setLot_num(bean.getLotNum());
                editListBean.add(productBean);
                mInventoryTotal = mInventoryTotal + bean.getEditNum();//计算盘点后总数
                continue;
            }
            for(InventoryResponse.InventoryLot lot:bean.getLotList()){
                EditRequest.ProductBean productBean = new EditRequest.ProductBean();
                productBean.setProduct_id(bean.getProductID());
                productBean.setId(lot.getInventoryLineID());
                productBean.setActual_qty(lot.getEditNum());
                productBean.setLot_id(lot.getLotID());
                productBean.setLot_num(lot.getLotNum());
                mInventoryTotal = mInventoryTotal + lot.getEditNum();//计算盘点后总数
                if(lot.isProductDate() && lot.isNewAdded())productBean.setProduce_datetime(lot.getProductDate());
                else if(!lot.isProductDate() && lot.isNewAdded())productBean.setLife_datetime(lot.getProductDate());
                editListBean.add(productBean);
            }

        }
        editRequest.setInventory_lines(editListBean);

//							sendConnection("/gongfu/shop/inventory/state",editRequest,PRODUCT_COMMIT,true, EditRepertoryResult.class);
        sendConnection("/api/v2/inventory/state",editRequest,INVENTORY_COMMIT,true, EditRepertoryResult.class);
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("盘点单详情页");
        MobclickAgent.onResume(this);          //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("盘点单详情页");
        MobclickAgent.onPause(this);          //统计时长
    }
}
