package com.runwise.supply.orderpage;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.runwise.supply.entity.RemUser;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2017/7/6.
 */

public class ProductBasicUtils {
    //productId -> 基本信息对象,便于后续使用查询
    private static HashMap<String,ProductBasicList.ListBean> basicMap = new HashMap<>();
    //退货单map,return id -> name
    private static HashMap<String,String> returnMap = new HashMap<>();

    public static HashMap<String, String> getReturnMap() {
        return returnMap;
    }

    private static List<ProductBasicList.ListBean> basicArr = new ArrayList<>();

    public static List<ProductBasicList.ListBean> getBasicArr() {
        return basicArr;
    }
    public static void setBasicArr(List<ProductBasicList.ListBean> basicArr) {
        ProductBasicUtils.basicArr = basicArr;
    }

    public static HashMap<String, ProductBasicList.ListBean> getBasicMap(Context context) {
        //如果缓存中没有，去DB查一遍
        if (basicMap.size() == 0){
            DbUtils dbUitls = DbUtils.create(context);
            try {
                List<ProductBasicList.ListBean> list = dbUitls.findAll(ProductBasicList.ListBean.class);
                if (list != null){
                    for (ProductBasicList.ListBean bean : list){
                        basicMap.put(String.valueOf(bean.getProductID()),bean);
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return basicMap;
        }else
            return basicMap;
    }

    public static void setBasicMap(HashMap<String, ProductBasicList.ListBean> basicMap) {
        ProductBasicUtils.basicMap = basicMap;
    }
    public static void clearCache(Context context){
        basicMap.clear();
        basicArr.clear();
        DbUtils dbUitls = DbUtils.create(context);
        try {
            dbUitls.dropDb();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveProductInfoAsync(final Context context,final List<ProductBasicList.ListBean> listBeanList){
//        AsyncTask<String,String,String> task = new AsyncTask<String, String, String>() {
//            @Override
//            protected String doInBackground(String... strings) {
//                try{
//                    DbUtils dbUtils = DbUtils.create(context);
//                    Log.d("haha","start save!");
//                    for(ProductBasicList.ListBean listBean:listBeanList){
//                        dbUtils.replace(listBean);
//                    }
//                    Log.d("haha","save!");
//                }catch (DbException e){
//                    e.printStackTrace();
//                    return null;
//                }
//                return null;
//            }
//        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DbUtils dbUtils = DbUtils.create(context);
                //Log.d("haha","start save!");
                for(ProductBasicList.ListBean listBean:listBeanList){
                    try {
                        dbUtils.saveOrUpdate(listBean);
                    }catch (DbException e){
                        e.printStackTrace();
                    }
                }
                //Log.d("haha","save!");
            }
        };
        new Thread(runnable).start();
    }
}
