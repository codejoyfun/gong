package com.runwise.supply.tools;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.runwise.supply.entity.RemUser;
import com.runwise.supply.orderpage.entity.ProductBasicList;

/**
 * Created by mike on 2017/11/21.
 */

public class MyDbUtil {
    public static final int DB_VERSION = 8;

    public static DbUtils create(Context context) {
        DbUtils dbUtils = DbUtils.create(context, "runwise.db", DB_VERSION, new DbUtils.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils dbUtils, int oldVersion, int newVersion) {
                try {
                    //为数据库表ShoppingCar添加shopId字段
                    dbUtils.createTableIfNotExist(ProductBasicList.ListBean.class);
                    dbUtils.execNonQuery("alter table com_runwise_supply_orderpage_entity_ProductBasicList$ListBean add orderBy integer");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dbUtils.configAllowTransaction(true);// 开启事务
        dbUtils.configDebug(false);// debug，输出sql语句
        return dbUtils;
    }
}
