package com.runwise.supply.tools;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * Created by mike on 2017/9/27.
 */

public class Utility {

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * https://stackoverflow.com/questions/35874001/dim-the-background-using-popupwindow-in-android
     * @param popupWindow
     */
//    public static void dim(PopupWindow popupWindow, float alpha) {
//        View container;
//        if (popupWindow.getBackground() == null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                container = (View) popupWindow.getContentView().getParent();
//            } else {
//                container = popupWindow.getContentView();
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                container = (View) popupWindow.getContentView().getParent().getParent();
//            } else {
//                container = (View) popupWindow.getContentView().getParent();
//            }
//        }
//        Context context = popupWindow.getContentView().getContext();
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
//        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        p.dimAmount = alpha;
//        wm.updateViewLayout(container, p);
//    }
}
