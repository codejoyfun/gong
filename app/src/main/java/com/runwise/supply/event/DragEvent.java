package com.runwise.supply.event;

/**
 * Created by mike on 2017/9/6.
 */

public class DragEvent {
    private Object mObj;
    private String mMsg;
    public DragEvent(Object obj, String msg) {
        // TODO Auto-generated constructor stub
        mObj = obj;
        mMsg = msg;
    }
    public String getMsg(){
        return mMsg;
    }

    public Object getObj(){
        return mObj;
    }
}
