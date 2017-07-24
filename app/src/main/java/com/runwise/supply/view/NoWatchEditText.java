package com.runwise.supply.view;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mychao on 2017/7/19.
 */
public class NoWatchEditText extends EditText {

    private List<TextWatcher> watchers = new ArrayList<TextWatcher>();

    public NoWatchEditText(Context context) {
        super(context);
    }

    public NoWatchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoWatchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        watchers.add(watcher);
        super.addTextChangedListener(watcher);
    }

    public void removeTextChangedListener() {
        for (int i = 0; i < watchers.size(); i++) {
            removeTextChangedListener(watchers.get(i));
        }
    }

}