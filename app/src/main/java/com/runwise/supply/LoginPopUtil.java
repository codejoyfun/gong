package com.runwise.supply;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * Created by myChaoFile on 16/5/19.
 */
public class LoginPopUtil {
    private PopupWindow popupWindow;
    private LayoutInflater inflater;
    private int popWidth,popHeight;
    private AdapterView.OnItemClickListener onClickListener;
    private boolean hideSweetOption;
    private float preWid;

    private LoginActivity.RemListAdapter remListAdapter;
    public LoginPopUtil(Context context) {
        inflater = LayoutInflater.from(context);
        Resources resources = context.getResources();
//        popWidth = (int) (GlobalConstant.screenW - resources.getDimension(R.dimen.classcircle_item_img_left) + resources.getDimension(R.dimen.classcircle_item_img_right));
        popHeight = (int) (resources.getDimension(R.dimen.classcircle_option_layout_hieght));
    }
    public void setAdapter(LoginActivity.RemListAdapter remListAdapter) {
        this.remListAdapter = remListAdapter;
    }

    private void initPop() {
        View layout = inflater.inflate(R.layout.popwin_layout, null);
        if (hideSweetOption) {
            preWid = 0.6f;
        } else {
            preWid = 0.7f;
        }
        popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, popHeight, false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.setAnimationStyle(R.style.classActionPopStyle);
//        classActionZanLayout =  layout.findViewById(R.id.classActionZanLayout);
        ListView listView = (ListView) layout.findViewById(R.id.listView);
        listView.setAdapter(remListAdapter);
//        classActionZanLayout.setTag(zanView);
        if (onClickListener != null) {
            listView.setOnItemClickListener(onClickListener);
        }
    }

    /**
     * 为按钮添加事件
     * @param listener
     */
    public void addOnItemClickListener(AdapterView.OnItemClickListener listener) {
        onClickListener = listener;
    }

    public void showPop(View parentView,final ImageView imageView) {
        initPop();
        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(parentView,0,0);
            imageView.setImageResource(R.drawable.login_btn_dropup);
        }
        else{
            popupWindow.dismiss();
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imageView.setImageResource(R.drawable.login_btn_dropdown);
            }
        });

    }
    public void hidePop() {
        if ( popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
