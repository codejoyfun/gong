package com.kids.commonframe.base.bean;

/**
 * Created by libin on 2017/7/18.
 */

public class ReceiveProEvent {
    private boolean notifyDataSetChange;

    public ReceiveProEvent(boolean notifyDataSetChange) {
        this.notifyDataSetChange = notifyDataSetChange;
    }

    public ReceiveProEvent() {
    }

    public boolean isNotifyDataSetChange() {
        return notifyDataSetChange;
    }

    public void setNotifyDataSetChange(boolean notifyDataSetChange) {
        this.notifyDataSetChange = notifyDataSetChange;
    }
}
