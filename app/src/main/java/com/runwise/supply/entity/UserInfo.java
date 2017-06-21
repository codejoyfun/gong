package com.runwise.supply.entity;

import java.io.Serializable;

/**
 * Created by mychao on 2016/10/30.
 */

public class UserInfo implements Serializable{
    private String member_id;   //id
    private String avatar;//": "",
    private String nickname;//": "lazy",
    private String phone;//": "13810914154",
    public UserInfo() {
    }

    public UserInfo(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
