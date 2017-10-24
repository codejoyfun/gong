package com.runwise.supply.tools;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by mike on 2017/10/24.
 */

public class DataTextWatch implements TextWatcher {

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    public Object mObject;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
