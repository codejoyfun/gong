package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 2017/6/29.
 */

public class LoginTestEntity {

    /**
     * jsonrpc : 2.0
     * id : null
     * result : {"state":"A0006","data":{"isSuccess":true,"user":{"username":"黄天","deliveryOverallRank":0.9,"deliveryApplauseRate":0.9,"uid":14,"deliveryPunctualityRate":0.9,"avatarUrl":"/gongfu/user/avatar/14/8815124028446180871.png","mobile":"13737574566","region":"","mendian":"","isDianzhang":false,"cateringServiceScore":0,"cateringQualityTrend":0,"street":"","partnerId":23,"cateringServiceTrend":0,"loginStatus":"normal","cateringQualityScore":0,"position":"配送员","login":"PS60007","customerServicePhone":false,"clerk":true}}}
     */

    private String jsonrpc;
    private Object id;
    private ResultBean result;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * state : A0006
         * data : {"isSuccess":true,"user":{"username":"黄天","deliveryOverallRank":0.9,"deliveryApplauseRate":0.9,"uid":14,"deliveryPunctualityRate":0.9,"avatarUrl":"/gongfu/user/avatar/14/8815124028446180871.png","mobile":"13737574566","region":"","mendian":"","isDianzhang":false,"cateringServiceScore":0,"cateringQualityTrend":0,"street":"","partnerId":23,"cateringServiceTrend":0,"loginStatus":"normal","cateringQualityScore":0,"position":"配送员","login":"PS60007","customerServicePhone":false,"clerk":true}}
         */

        private String state;
        private DataBean data;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * isSuccess : true
             * user : {"username":"黄天","deliveryOverallRank":0.9,"deliveryApplauseRate":0.9,"uid":14,"deliveryPunctualityRate":0.9,"avatarUrl":"/gongfu/user/avatar/14/8815124028446180871.png","mobile":"13737574566","region":"","mendian":"","isDianzhang":false,"cateringServiceScore":0,"cateringQualityTrend":0,"street":"","partnerId":23,"cateringServiceTrend":0,"loginStatus":"normal","cateringQualityScore":0,"position":"配送员","login":"PS60007","customerServicePhone":false,"clerk":true}
             */

            private boolean isSuccess;
            private UserBean user;

            public boolean isIsSuccess() {
                return isSuccess;
            }

            public void setIsSuccess(boolean isSuccess) {
                this.isSuccess = isSuccess;
            }

            public UserBean getUser() {
                return user;
            }

            public void setUser(UserBean user) {
                this.user = user;
            }

            public static class UserBean {
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
                private int uid;
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

                public int getUid() {
                    return uid;
                }

                public void setUid(int uid) {
                    this.uid = uid;
                }

                public double getDeliveryPunctualityRate() {
                    return deliveryPunctualityRate;
                }

                public void setDeliveryPunctualityRate(double deliveryPunctualityRate) {
                    this.deliveryPunctualityRate = deliveryPunctualityRate;
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
            }
        }
    }
}
