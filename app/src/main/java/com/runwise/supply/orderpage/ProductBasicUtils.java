package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ReceiveInfo;
import com.runwise.supply.tools.MyDbUtil;
import com.runwise.supply.tools.RunwiseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_VERSION_PRODUCT_LIST;

/**
 * Created by libin on 2017/7/6.
 */

public class ProductBasicUtils {
    //productId -> 基本信息对象,便于后续使用查询
    private static HashMap<String, ProductBasicList.ListBean> basicMap = new HashMap<>();
    private static HashMap<String, ReceiveInfo> receiveInfoMap = new HashMap<>();
    //退货单map,return id -> name
    private static HashMap<String, String> returnMap = new HashMap<>();

    public static HashMap<String, String> getReturnMap() {
        return returnMap;
    }

    private static List<ProductBasicList.ListBean> basicArr = new ArrayList<>();
    private static List<ReceiveInfo> mReceiveInfoList = new ArrayList<>();

    public static List<ProductBasicList.ListBean> getBasicArr() {
        return basicArr;
    }

    public static void setBasicArr(List<ProductBasicList.ListBean> basicArr) {
        ProductBasicUtils.basicArr = basicArr;
    }

    public static HashMap<String, ProductBasicList.ListBean> getBasicMap(Context context) {
        //如果缓存中没有，去DB查一遍
        if (basicMap.size() == 0) {
            DbUtils dbUitls = MyDbUtil.create(context);
            try {
                List<ProductBasicList.ListBean> list = dbUitls.findAll(ProductBasicList.ListBean.class);
                if (list != null) {
                    for (ProductBasicList.ListBean bean : list) {
                        basicMap.put(String.valueOf(bean.getProductID()), bean);
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return basicMap;
        } else
            return basicMap;
    }

    public static List<ReceiveInfo> getReceiveInfo(Context context, int orderId, int productId) {
        DbUtils dbUitls = MyDbUtil.create(context);
        try {
            return dbUitls.findAll(Selector.from(ReceiveInfo.class)
                    .where("orderId", "=", orderId)
                    .where("productId", "=", productId));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setBasicMap(HashMap<String, ProductBasicList.ListBean> basicMap) {
        ProductBasicUtils.basicMap = basicMap;
    }

    public static boolean isInit(Context context) {
        if (getBasicArr().size() == 0){
            ToastUtil.show(context,"正在下载商品数据");
            if (RunwiseService.getStatus().equals(context.getString(R.string.service_fail_finish))||RunwiseService.getStatus().equals(context.getString(R.string.service_finish)) ||RunwiseService.getStatus().equals(context.getString(R.string.service_fail_finish_protocol_close))){
                SPUtils.put(context, FILE_KEY_VERSION_PRODUCT_LIST, 0);
                context.startService(new Intent(context,RunwiseService.class));
            }
            return false;
        }
        return true;
    }

    public static void clearBasicMap() {
        if (basicMap != null) {
            basicMap.clear();
        }
    }

    public static void clearCache(Context context) {
        basicMap.clear();
        basicArr.clear();
        DbUtils dbUitls = MyDbUtil.create(context);
        try {
            dbUitls.dropDb();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveProductInfoAsync(final Context context, final List<ProductBasicList.ListBean> listBeanList) {
        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    DbUtils dbUtils = MyDbUtil.create(context);
                    dbUtils.saveOrUpdateAll(listBeanList);
                    Log.d("haha", "save finished!");
                } catch (DbException e) {
                    e.printStackTrace();
                    return null;
                }
                return null;
            }
        };
        task.execute(new String[]{});
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                DbUtils dbUtils = MyDbUtil.create(context);
//                //Log.d("haha","start save!");
//                for (ProductBasicList.ListBean listBean : listBeanList) {
//                    try {
//                        dbUtils.saveOrUpdate(listBean);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//                }
//                //Log.d("haha","save!");
//            }
//        };
//        new Thread(runnable).start();
    }

    public static List<ReceiveInfo> getReceiveInfoList() {
        return mReceiveInfoList;
    }

    public static void setReceiveInfoList(List<ReceiveInfo> mReceiveInfoList) {
        ProductBasicUtils.mReceiveInfoList = mReceiveInfoList;
    }

    public static Set<Integer> check(Context context, List<? extends OrderResponse.ListBean.LinesBean> list) {
        Set<Integer> missingInfo = new HashSet<>();
        for (OrderResponse.ListBean.LinesBean linesBean : list) {
            if (getBasicMap(context).get(linesBean.getProductID() + "") == null) {
                missingInfo.add(linesBean.getProductID());
            }
        }
        return missingInfo;
    }

    public static void request(NetWorkHelper netWorkHelper, int where, Set<Integer> missingInfo) {

        for (Integer id : missingInfo) {
            Object request = null;
            StringBuffer sb = new StringBuffer("/gongfu/v2/product/");
            sb.append(id).append("/");
            netWorkHelper.sendConnection(sb.toString(), request, where, false, ProductOne.class);
        }
    }
}
