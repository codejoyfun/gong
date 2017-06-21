package com.runwise.supply.mine.entity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class MsgDetailRequest {
    private String message_id;

    public MsgDetailRequest(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
