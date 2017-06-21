package com.kids.commonframe.base.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.R;
import com.kids.commonframe.base.util.CommonUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CustomBottomDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private FrameLayout contentView;
    private OnBottomDialogClick onBottomDialogClick;

    public CustomBottomDialog(Context context) {
        super(context, R.style.CustomProgressDialog);
        this.context = context;
        setContentView(R.layout.bottom_dialog_layout);
        Window window = getWindow();
        window.getAttributes().gravity = Gravity.BOTTOM;
        window.setWindowAnimations(R.style.MyPopwindow_anim_style);
        window.setLayout(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(true);
        contentView = (FrameLayout) this.findViewById(R.id.content_bottom_dialog);
        View chance = this.findViewById(R.id.cancle_button);
        chance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void addContentView(View view) {
        if(contentView != null && view != null) {
            contentView.removeAllViews();
            contentView.addView(view);
        }
    }

    public void addItemViews(ArrayMap<Integer,String> items) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.removeAllViews();
        for (Integer id:items.keySet()) {
            linearLayout.addView(addItemView(id, items.get(id)));
        }
        addContentView(linearLayout);
    }

    private View addItemView(int rid,String text) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(rid);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(context, 44f)));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.base_item_bg_selector);
        linearLayout.setOnClickListener(this);
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        textView.setText(text);
        textView.setTextColor(context.getResources().getColor(R.color.bottom_dialog_textcolor));
        textView.setTextSize(16f);
        textView.setGravity(Gravity.CENTER);
        linearLayout.removeAllViews();
        linearLayout.addView(textView);
        return  linearLayout;
    }

    @Override
    public void onClick(View v) {
        onBottomDialogClick.onItemClick(v);
    }

    public interface OnBottomDialogClick {
        void onItemClick(View view);
    }

    public void setOnBottomDialogClick(OnBottomDialogClick onBottomDialogClick) {
        this.onBottomDialogClick = onBottomDialogClick;
    }

}
