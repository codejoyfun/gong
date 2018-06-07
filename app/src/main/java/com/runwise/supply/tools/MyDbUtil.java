package com.runwise.supply.tools;

import android.content.Context;
import android.content.Intent;

import com.kids.commonframe.base.util.SPUtils;
import com.lidroid.xutils.DbUtils;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.entity.RemUser;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_VERSION_PRODUCT_LIST;

/**
 * Created by mike on 2017/11/21.
 */

public class MyDbUtil {
    public static final int DB_VERSION = 16;

    public static DbUtils create(Context context) {
        DbUtils dbUtils = DbUtils.create(context, "runwise.db", DB_VERSION, new DbUtils.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils dbUtils, int oldVersion, int newVersion) {
                try {
                    //为数据库表ShoppingCar添加shopId字段
                    dbUtils.dropTable(ProductBasicList.ListBean.class);
                    dbUtils.createTableIfNotExist(ProductBasicList.ListBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SPUtils.put(context, FILE_KEY_VERSION_PRODUCT_LIST, 0);
                if (SPUtils.isLogin(context)){
                    SPUtils.loginOut(context);
                    context.startActivity(new Intent(context,LoginActivity.class));
                }
            }
        });
        dbUtils.configAllowTransaction(true);// 开启事务
        dbUtils.configDebug(false);// debug，输出sql语句
        return dbUtils;
    }
}
