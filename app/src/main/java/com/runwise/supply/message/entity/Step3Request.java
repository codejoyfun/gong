package com.runwise.supply.message.entity;

/**
 * Created by myChaoFile on 17/2/7.
 */

public class Step3Request {
    private String realname;//	是	姓名
    private String phone;//	是	电话
    private String relation;//	是	关系
    private String realname_two;//	是	第二姓名
    private String phone_two;//	是	第二电话
    private String relation_two;//	是	第二关系
    private String apply_id;//	是	申请id

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRealname_two() {
        return realname_two;
    }

    public void setRealname_two(String realname_two) {
        this.realname_two = realname_two;
    }

    public String getPhone_two() {
        return phone_two;
    }

    public void setPhone_two(String phone_two) {
        this.phone_two = phone_two;
    }

    public String getRelation_two() {
        return relation_two;
    }

    public void setRelation_two(String relation_two) {
        this.relation_two = relation_two;
    }

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
    }
}
