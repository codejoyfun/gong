package com.runwise.supply.message.entity;

/**
 * Created by myChaoFile on 17/2/7.
 */

public class Step2Request {
    private String realname;//	是	姓名
    private String identity_id;//	否	身份证
    private String identity_start_at;//	是	身份证生效时间
    private String identity_end_at;//	是	失效时间
    private String company;//	是	单位
    private String phone;//	是	手机
    private String wechat_id;//	是	微信
    private String email;//	是	邮箱
    private String bank_name;//	是	银行
    private String card_id;//	是	银行卡号
    private String work_status;//	是	工作状态
    private String company_phone;//	是	公司电话
    private String profession;//	是	职业
    private String company_address;//	是	单位地址
    private String live_address;//	是	居住地址
    private String dealer_id;//	是	代理商id
    private String car_id;//	是	车id
    private String period_id;//	是	分期id
    private String is_marry;//	是否已婚

    private String apply_id;
    private String sms_code;

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getPeriod_id() {
        return period_id;
    }

    public void setPeriod_id(String period_id) {
        this.period_id = period_id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getIdentity_start_at() {
        return identity_start_at;
    }

    public void setIdentity_start_at(String identity_start_at) {
        this.identity_start_at = identity_start_at;
    }

    public String getIdentity_end_at() {
        return identity_end_at;
    }

    public void setIdentity_end_at(String identity_end_at) {
        this.identity_end_at = identity_end_at;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWechat_id() {
        return wechat_id;
    }

    public void setWechat_id(String wechat_id) {
        this.wechat_id = wechat_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getWork_status() {
        return work_status;
    }

    public void setWork_status(String work_status) {
        this.work_status = work_status;
    }

    public String getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(String company_phone) {
        this.company_phone = company_phone;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getLive_address() {
        return live_address;
    }

    public void setLive_address(String live_address) {
        this.live_address = live_address;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getIs_marry() {
        return is_marry;
    }

    public void setIs_marry(String is_marry) {
        this.is_marry = is_marry;
    }
}
