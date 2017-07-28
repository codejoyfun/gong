package com.runwise.supply.message.entity;

/**
 * Created by mychao on 2017/7/28.
 */

public class MessageListEntity {
    private int type;
    private MessageResult.ChannelBean channelBean;
    private MessageResult.OrderBean orderBean;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MessageResult.ChannelBean getChannelBean() {
        return channelBean;
    }

    public void setChannelBean(MessageResult.ChannelBean channelBean) {
        this.channelBean = channelBean;
    }

    public MessageResult.OrderBean getOrderBean() {
        return orderBean;
    }

    public void setOrderBean(MessageResult.OrderBean orderBean) {
        this.orderBean = orderBean;
    }
}
