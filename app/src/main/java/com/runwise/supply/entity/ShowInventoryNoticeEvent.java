package com.runwise.supply.entity;

/**
 * 正在盘点中的通知事件
 *
 * Created by Dong on 2017/12/18.
 */

public class ShowInventoryNoticeEvent {
    public boolean isShow = false;
    public ShowInventoryNoticeEvent(boolean isShow){
        this.isShow = isShow;
    }
}
