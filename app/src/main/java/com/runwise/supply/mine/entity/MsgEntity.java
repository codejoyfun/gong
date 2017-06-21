package com.runwise.supply.mine.entity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class MsgEntity {
    private String message_id;//": 2,    //消息id
    private String title;//": "bbbbbbbbbbb",    //标题
    private String content;//": "我我我我我我我 ",    //内容
    private String is_read;//": 0,    //是否已读 （1已读 | 0未读）
    private String member_id;//": 1,    //用户id
    private String created_at;//": "2016-12-25 13:16:08"    //时间

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
