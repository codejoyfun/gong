package com.runwise.supply.index.entity;

/**
 * Created by myChaoFile on 17/1/6.
 */

public class IndexDealers {

    /**
     * dealer_id : 3
     * dealer_name : 北京国服信奥兴汽车有限公司
     * contact_phone : 4008192814
     * score : 0
     * lat : 39.961957
     * lng : 116.380729
     * address : 北京市石景山区古城大街国际汽车贸易服务园区F区9号
     * image_id : 5
     * attribute_id : 1
     * attribute : {"attribute_id":1,"att_name":"4S店"}
     * image : {"image_id":5,"img_path":"http://img1.xcarimg.com/b25/s8195/c_20160726111010245158934756020.jpg"}
     */

    private String dealer_id;
    private String dealer_name;
    private String contact_phone;
    private int score;
    private String lat;
    private String lng;
    private String address;
    private int image_id;
    private int attribute_id;
    private AttributeBean attribute;
    private ImageBean image;
    private String distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getDealer_name() {
        return dealer_name;
    }

    public void setDealer_name(String dealer_name) {
        this.dealer_name = dealer_name;
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

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
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

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
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

    public static class ImageBean {
        /**
         * image_id : 5
         * img_path : http://img1.xcarimg.com/b25/s8195/c_20160726111010245158934756020.jpg
         */

        private int image_id;
        private String img_path;

        public int getImage_id() {
            return image_id;
        }

        public void setImage_id(int image_id) {
            this.image_id = image_id;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }
    }
}
