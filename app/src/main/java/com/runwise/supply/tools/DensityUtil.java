package com.runwise.supply.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by myChaoFile on 16/12/29.
 */

public class DensityUtil {
    public DensityUtil() {
    }

    public static int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 * var2 + 0.5F);
    }

    public static int px2dip(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 / var2 + 0.5F);
    }

    public static int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 * var2 + 0.5F);
    }

    public static int px2sp(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 / var2 + 0.5F);
    }

    public static  int getScreenW(Activity context){
        DisplayMetrics metrics=new DisplayMetrics();
        WindowManager manager= context.getWindowManager();
        manager.getDefaultDisplay().getMetrics(metrics);
        int w=metrics.widthPixels;
        return w;
    }
    public static  int getScreenH(Activity context){
        DisplayMetrics metrics=new DisplayMetrics();
        WindowManager manager= context.getWindowManager();
        manager.getDefaultDisplay().getMetrics(metrics);
        int w=metrics.heightPixels;
        return w;
    }
}