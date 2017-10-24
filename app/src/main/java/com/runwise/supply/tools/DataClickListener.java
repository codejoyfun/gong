package com.runwise.supply.tools;

import android.view.View;

/**
 * Created by mike on 2017/10/24.
 */

public class DataClickListener implements View.OnClickListener {

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    public Object mObject;

    public Object getSecondObject() {
        return mSecondObject;
    }

    public void setSecondObject(Object secondObject) {
        mSecondObject = secondObject;
    }

    public Object mSecondObject;

    @Override
    public void onClick(View v) {

    }
}
