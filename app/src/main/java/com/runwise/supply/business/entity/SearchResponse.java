package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by libin on 2017/2/8.
 */

public class SearchResponse extends BaseEntity {

    /**
     * err_code : 0
     * data : {"entities":[{"dealer_id":2,"dealer_name":"北京运通兴宝汽车销售服务有限公司","contact_phone":"4008193095","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市大兴区西红门镇中鼎路19号","image_id":3,"attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"image":{"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"}},{"dealer_id":1,"dealer_name":"北京星德宝汽车销售服务有限公司","contact_phone":"4006136766","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市朝阳区高碑店乡白家楼8号","image_id":1,"attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"image":{"image_id":1,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164405249740506597437.jpg"}}]}
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
             * dealer_id : 2
             * dealer_name : 北京运通兴宝汽车销售服务有限公司
             * contact_phone : 4008193095
             * score : 0
             * lat : 39.961957
             * lng : 116.380729
             * address : 北京市大兴区西红门镇中鼎路19号
             * image_id : 3
             * attribute_id : 1
             * attribute : {"attribute_id":1,"att_name":"4S店"}
             * image : {"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"}
             */

            private int dealer_id;
            private String dealer_name;
            private String contact_phone;
            private float score;
            private String lat;
            private String lng;
            private String address;
            private int image_id;
            private int attribute_id;
            private AttributeBean attribute;
            private ImageBean image;

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

            public String getContact_phone() {
                return contact_phone;
            }

            public void setContact_phone(String contact_phone) {
                this.contact_phone = contact_phone;
            }

            public float getScore() {
                return score;
            }

            public void setScore(float score) {
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
                 * image_id : 3
                 * img_path : http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg
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
    }
}
