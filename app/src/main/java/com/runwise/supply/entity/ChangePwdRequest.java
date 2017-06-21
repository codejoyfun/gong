package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/4.
 */

public class ChangePwdRequest {
    private String raw_pwd;//	是	原始密码
    private String new_pwd;//

    public String getNew_pwd() {
        return new_pwd;
    }

    public void setNew_pwd(String new_pwd) {
        this.new_pwd = new_pwd;
    }

    public String getRaw_pwd() {
        return raw_pwd;
    }

    public void setRaw_pwd(String raw_pwd) {
        this.raw_pwd = raw_pwd;
    }
}
