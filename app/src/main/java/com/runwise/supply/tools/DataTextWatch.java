package com.runwise.supply.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by mike on 2017/10/24.
 */

public class DataTextWatch implements TextWatcher {

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public boolean change = true;

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    public Object mObject;

    public EditText getEditText() {
        return mEditText;
    }

    public void setEditText(EditText editText) {
        mEditText = editText;
    }

    public EditText mEditText;
    public String mLastValue;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mLastValue = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
