package com.runwise.supply.tools;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.runwise.supply.R;
import com.runwise.supply.entity.ProductListResponse;
import com.runwise.supply.entity.ProductVersionRequest;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_PRODUCT_CATEGORY_LIST;
import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_VERSION_PRODUCT_LIST;

/**
 * Created by mike on 2018/2/27.
 */

public class RunwiseService extends IntentService implements NetWorkHelper.NetWorkCallBack<BaseEntity> {

    private LocalBroadcastManager mLocalBroadcastManager;
    protected NetWorkHelper<BaseEntity> netWorkHelper;
    private static final int REQUEST_CODE_PRODUCT_LIST = 1 << 0;
    public static final String INTENT_KEY_STATUS = "status";
    public static final String INTENT_KEY_PRODUCTS = "products";
    private static String mStatus;

    //    测试
    private long startRequestTime = 0L;
    private long startDatabaseTime = 0L;


    public RunwiseService() {
        super("下载商品列表服务");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(Integer.MAX_VALUE,new Notification()); //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
        }

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        netWorkHelper = new NetWorkHelper<BaseEntity>(this, this);
        sendServiceStatus(getString(R.string.service_start));
        if (SPUtils.isLogin(getApplicationContext())) {
            requestProductList();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendServiceStatus(getString(R.string.service_running));
    }

    public void requestProductList() {
        startRequestTime = System.currentTimeMillis();
        int version = (int) SPUtils.get(getApplicationContext(), FILE_KEY_VERSION_PRODUCT_LIST, 0);
        ProductVersionRequest productVersionRequest = new ProductVersionRequest();
        productVersionRequest.setVersion(version);
        netWorkHelper.sendConnection("/api/product/list/", productVersionRequest, REQUEST_CODE_PRODUCT_LIST, false, ProductListResponse.class);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 发送服务状态信息
    private void sendServiceStatus(String status) {
        mStatus = status;
        Intent intent = new Intent(ProductActivityV2.ACTION_TYPE_SERVICE);
        intent.putExtra(INTENT_KEY_STATUS, status);
        mLocalBroadcastManager.sendBroadcast(intent);
    }


    @Override
    public BaseEntity onParse(int where, Class<?> targerClass, String result) {
        return null;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        ProductListResponse productListResponse = null;
        switch (where) {
            case REQUEST_CODE_PRODUCT_LIST:
                BaseEntity.ResultBean resultBean = result.getResult();
                try {
                    productListResponse = (ProductListResponse) resultBean.getData();
                    if (productListResponse.getProducts() != null) {
                        ProductListResponse finalProductListResponse = productListResponse;
                        new Thread(){
                            @Override
                            public void run() {
                                deleteProductFromDB();
                                putProductsToDB(finalProductListResponse.getProducts());
                                sendServiceStatus(getString(R.string.service_finish));
                            }
                        }.start();
                        SPUtils.put(getApplicationContext(), FILE_KEY_VERSION_PRODUCT_LIST, productListResponse.getVersion());
                    }
                    if (productListResponse != null) {
                        if (productListResponse.getCategory() != null && productListResponse.getCategory().size() > 0) {
                            SPUtils.saveObject(getApplicationContext(), FILE_KEY_PRODUCT_CATEGORY_LIST, productListResponse.getCategory());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Thread(){
                        @Override
                        public void run() {
                            loadProducts();
                            sendServiceStatus(getString(R.string.service_finish));
                        }
                    }.start();
                }
                break;
        }
    }

    private void deleteProductFromDB() {
        DbUtils dbUtils = MyDbUtil.create(getApplicationContext());
        try {
            dbUtils.deleteAll(ProductBasicList.ListBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void putProductsToDB(List<ProductBasicList.ListBean> basicList) {
        DbUtils dbUtils = MyDbUtil.create(getApplicationContext());
        HashMap<String, ProductBasicList.ListBean> map = new HashMap<>();
        dbUtils.configAllowTransaction(true);
        try {
            dbUtils.saveOrUpdateAll(basicList);
            for (ProductBasicList.ListBean bean : basicList) {
                map.put(String.valueOf(bean.getProductID()), bean);
            }

//            全部商品(包括归档和未归档)
            Selector selector = Selector.from(ProductBasicList.ListBean.class);
            selector.orderBy("orderBy", false);
            List<ProductBasicList.ListBean> list = dbUtils.findAll(selector);
            for (ProductBasicList.ListBean bean : list) {
                String keyId = bean.getProductID() + "";
                if (!map.containsKey(keyId)) {
                    if (bean.getImage() == null) {//TODO:xutils的坑，没有load imagebean？
                        bean.setImage(new ImageBean());
                    }
                    map.put(keyId, bean);
                }
            }
            ProductBasicUtils.setBasicMap(map);
            //            查询未归档的商品
            List<ProductBasicList.ListBean> orderProductList = filterSubValid(list);
            ProductBasicUtils.setBasicArr(orderProductList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static List<ProductBasicList.ListBean> filterSubValid(List<ProductBasicList.ListBean> listBeans) {
        List<ProductBasicList.ListBean> tempListBeans = new ArrayList<>();
        for (ProductBasicList.ListBean listBean : listBeans) {
            if (listBean.isSubValid()) {
                tempListBeans.add(listBean);
            }
        }
        return tempListBeans;
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CODE_PRODUCT_LIST:
                ToastUtil.show(getBaseContext(), errMsg);
                if (!errMsg.equals(getString(R.string.network_error))){
                    sendServiceStatus(getString(R.string.service_fail_finish_protocol_close));
                    return;
                }
                sendServiceStatus(getString(R.string.service_fail_finish));
                break;
        }
    }

    private void loadProducts() {
        DbUtils dbUtils = MyDbUtil.create(getApplicationContext());
        HashMap<String, ProductBasicList.ListBean> map = new HashMap<>();
        dbUtils.configAllowTransaction(true);
        try {
            Selector selector1 = Selector.from(ProductBasicList.ListBean.class);
            selector1.orderBy("orderBy", false);
            List<ProductBasicList.ListBean> orderProductList = dbUtils.findAll(selector1);
            for (ProductBasicList.ListBean bean : orderProductList) {
                String keyId = bean.getProductID() + "";
                if (!map.containsKey(keyId)) {
                    if (bean.getImage() == null) {//TODO:xutils的坑，没有load imagebean？
                        bean.setImage(new ImageBean());
                    }
                    map.put(keyId, bean);
                }
            }
            ProductBasicUtils.setBasicMap(map);
            orderProductList = filterSubValid(orderProductList);
            ProductBasicUtils.setBasicArr(orderProductList);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static String getStatus() {
        return mStatus;
    }

    public static void setStatus(String status) {
        mStatus = status;
    }
}
