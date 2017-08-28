package com.kids.commonframe.base;

import java.io.Serializable;

/**
 * Created by mychao on 2016/10/30.
 */

public class UserInfo implements Serializable{

    /**
     * username : 黄天
     * deliveryOverallRank : 0.9
     * deliveryApplauseRate : 0.9
     * uid : 14
     * deliveryPunctualityRate : 0.9
     * avatarUrl : /gongfu/user/avatar/14/8815124028446180871.png
     * mobile : 13737574566
     * region :
     * mendian :
     * isDianzhang : false
     * cateringServiceScore : 0
     * cateringQualityTrend : 0
     * street :
     * partnerId : 23
     * cateringServiceTrend : 0
     * loginStatus : normal
     * cateringQualityScore : 0
     * position : 配送员
     * login : PS60007
     * customerServicePhone : false
     * clerk : true
     */

    private String username;
    private double deliveryOverallRank;
    private double deliveryApplauseRate;
    private String uid;
    private double deliveryPunctualityRate;
    private String avatarUrl;
    private String mobile;
    private String region;
    private String mendian;
    private boolean isDianzhang;
    private int cateringServiceScore;
    private int cateringQualityTrend;
    private String street;
    private int partnerId;
    private int cateringServiceTrend;
    private String loginStatus;
    private int cateringQualityScore;
    private String position;
    private String login;
    private boolean customerServicePhone;
    private boolean clerk;
    private boolean canSeePrice;
    private String companyHotLine;
    private String company;
    private String isAgreeItem;
    private boolean hasNewInvoice;
    private String company;
    public boolean isHasNewInvoice() {
        return hasNewInvoice;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setHasNewInvoice(boolean hasNewInvoice) {
        this.hasNewInvoice = hasNewInvoice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getDeliveryOverallRank() {
        return deliveryOverallRank;
    }

    public void setDeliveryOverallRank(double deliveryOverallRank) {
        this.deliveryOverallRank = deliveryOverallRank;
    }

    public double getDeliveryApplauseRate() {
        return deliveryApplauseRate;
    }

    public void setDeliveryApplauseRate(double deliveryApplauseRate) {
        this.deliveryApplauseRate = deliveryApplauseRate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getDeliveryPunctualityRate() {
        return deliveryPunctualityRate;
    }

    public void setDeliveryPunctualityRate(double deliveryPunctualityRate) {
        this.deliveryPunctualityRate = deliveryPunctualityRate;
    }

    public String getCompanyHotLine() {
        return companyHotLine;
    }

    public void setCompanyHotLine(String companyHotLine) {
        this.companyHotLine = companyHotLine;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMendian() {
        return mendian;
    }

    public void setMendian(String mendian) {
        this.mendian = mendian;
    }

    public boolean isIsDianzhang() {
        return isDianzhang;
    }

    public void setIsDianzhang(boolean isDianzhang) {
        this.isDianzhang = isDianzhang;
    }

    public int getCateringServiceScore() {
        return cateringServiceScore;
    }

    public void setCateringServiceScore(int cateringServiceScore) {
        this.cateringServiceScore = cateringServiceScore;
    }

    public int getCateringQualityTrend() {
        return cateringQualityTrend;
    }

    public void setCateringQualityTrend(int cateringQualityTrend) {
        this.cateringQualityTrend = cateringQualityTrend;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public int getCateringServiceTrend() {
        return cateringServiceTrend;
    }

    public void setCateringServiceTrend(int cateringServiceTrend) {
        this.cateringServiceTrend = cateringServiceTrend;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public int getCateringQualityScore() {
        return cateringQualityScore;
    }

    public void setCateringQualityScore(int cateringQualityScore) {
        this.cateringQualityScore = cateringQualityScore;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isCustomerServicePhone() {
        return customerServicePhone;
    }

    public void setCustomerServicePhone(boolean customerServicePhone) {
        this.customerServicePhone = customerServicePhone;
    }

    public boolean isClerk() {
        return clerk;
    }

    public void setClerk(boolean clerk) {
        this.clerk = clerk;
    }

    public boolean isCanSeePrice() {
        return canSeePrice;
    }

    public void setCanSeePrice(boolean canSeePrice) {
        this.canSeePrice = canSeePrice;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getIsAgreeItem() {
        return isAgreeItem;
    }

    public void setIsAgreeItem(String isAgreeItem) {
        this.isAgreeItem = isAgreeItem;
    }
}
