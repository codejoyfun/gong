package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/3.
 */

public class LoginResult {
    private String member_id;//": 5,
    private String user_token;//": "ead6e8bc1652b6c168fead8d0c171991"

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }
}
