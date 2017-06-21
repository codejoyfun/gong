package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by libin on 2017/2/17.
 */

public class SelectDealerResponse extends BaseEntity {

    /**
     * err_code : 0
     * data : {"entities":[{"dealer_id":4,"dealer_name":"北京博瑞祥云汽车销售服务有限公司","dealer_img":"http://img1.xcarimg.com/PicLib/logo/pl1_80.jpg","contact_phone":"4008192814","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市大兴区西红门镇中鼎路19号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":3,"dealer_name":"北京国服信奥兴汽车有限公司","dealer_img":"http://img1.xcarimg.com/PicLib/logo/pl1_80.jpg","contact_phone":"4008192814","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市石景山区古城大街国际汽车贸易服务园区F区9号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":2,"dealer_name":"北京运通兴宝汽车销售服务有限公司","dealer_img":"http://img1.xcarimg.com/PicLib/logo/pl2_80.jpg","contact_phone":"4008193095","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市大兴区西红门镇中鼎路19号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":1,"dealer_name":"北京星德宝汽车销售服务有限公司","dealer_img":"http://img1.xcarimg.com/PicLib/logo/pl2_80.jpg","contact_phone":"4006136766","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市朝阳区高碑店乡白家楼8号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<EntitiesBean> entities;

        public List<EntitiesBean> getEntities() {
            return entities;
        }

        public void setEntities(List<EntitiesBean> entities) {
            this.entities = entities;
        }

        public static class EntitiesBean {
            /**
             * dealer_id : 4
             * dealer_name : 北京博瑞祥云汽车销售服务有限公司
             * dealer_img : http://img1.xcarimg.com/PicLib/logo/pl1_80.jpg
             * contact_phone : 4008192814
             * score : 0
             * lat : 39.961957
             * lng : 116.380729
             * address : 北京市大兴区西红门镇中鼎路19号
             * attribute_id : 1
             * attribute : {"attribute_id":1,"att_name":"4S店"}
             * car_id : 1
             */

            private int dealer_id;
            private String dealer_name;
            private String dealer_img;
            private String contact_phone;
            private int score;
            private String lat;
            private String lng;
            private String address;
            private int attribute_id;
            private AttributeBean attribute;
            private int car_id;

            public int getDealer_id() {
                return dealer_id;
            }

            public void setDealer_id(int dealer_id) {
                this.dealer_id = dealer_id;
            }

            public String getDealer_name() {
                return dealer_name;
            }

            public void setDealer_name(String dealer_name) {
                this.dealer_name = dealer_name;
            }

            public String getDealer_img() {
                return dealer_img;
            }

            public void setDealer_img(String dealer_img) {
                this.dealer_img = dealer_img;
            }

            public String getContact_phone() {
                return contact_phone;
            }

            public void setContact_phone(String contact_phone) {
                this.contact_phone = contact_phone;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getAttribute_id() {
                return attribute_id;
            }

            public void setAttribute_id(int attribute_id) {
                this.attribute_id = attribute_id;
            }

            public AttributeBean getAttribute() {
                return attribute;
            }

            public void setAttribute(AttributeBean attribute) {
                this.attribute = attribute;
            }

            public int getCar_id() {
                return car_id;
            }

            public void setCar_id(int car_id) {
                this.car_id = car_id;
            }

            public static class AttributeBean {
                /**
                 * attribute_id : 1
                 * att_name : 4S店
                 */

                private int attribute_id;
                private String att_name;

                public int getAttribute_id() {
                    return attribute_id;
                }

                public void setAttribute_id(int attribute_id) {
                    this.attribute_id = attribute_id;
                }

                public String getAtt_name() {
                    return att_name;
                }

                public void setAtt_name(String att_name) {
                    this.att_name = att_name;
                }
            }
        }
    }
}
