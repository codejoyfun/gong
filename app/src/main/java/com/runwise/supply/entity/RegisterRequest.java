package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/3.
 */

public class RegisterRequest {
    /**
     * mobile : 12355557779
     * name : Tom03
     * email :
     * company :
     */

    private String mobile;
    private String name;
    private String email;
    private String company;

    public RegisterRequest(String mobile, String name, String email,String company) {
        this.mobile = mobile;
        this.name = name;
        this.email = email;
        this.company = company;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
