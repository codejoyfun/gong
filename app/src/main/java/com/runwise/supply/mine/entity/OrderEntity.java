package com.runwise.supply.mine.entity;

/**
 * Created by myChaoFile on 17/1/19.
 */

public class OrderEntity {

    /**
     * order_id : 8
     * order_status : 10
     * title : 商品标题2
     * sub_title : 短标题2
     * complete_price : 0.00
     * wanting_price : 0.00
     * next_at : 0000-00-00 00:00:00
     * sale_price : 0.00
     * first_period : 0.00
     * period_price : 0.00
     * dealer_id : 1
     * created_at : 2015-06-20 11:08:20
     * image_id : 1
     * dealer : {"dealer_id":1,"dealer_name":"北京星德宝汽车销售服务有限公司"}
     * image : {"image_id":1,"name":"","img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164406754639316916595.jpg"}
     * status_name : 已通过
     */

    private int order_id;
    private int order_status;
    private String title;
    private String sub_title;
    private String complete_price;
    private String wanting_price;
    private String next_at;
    private String sale_price;
    private String first_period;
    private String period_price;
    private int dealer_id;
    private String created_at;
    private int image_id;
    private DealerBean dealer;
    private ImageBean image;
    private String status_name;
    private String period_url;
    private String apply_info_url;

    public String getApply_info_url() {
        return apply_info_url;
    }

    public void setApply_info_url(String apply_info_url) {
        this.apply_info_url = apply_info_url;
    }

    public String getPeriod_url() {
        return period_url;
    }

    public void setPeriod_url(String period_url) {
        this.period_url = period_url;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getComplete_price() {
        return complete_price;
    }

    public void setComplete_price(String complete_price) {
        this.complete_price = complete_price;
    }

    public String getWanting_price() {
        return wanting_price;
    }

    public void setWanting_price(String wanting_price) {
        this.wanting_price = wanting_price;
    }

    public String getNext_at() {
        return next_at;
    }

    public void setNext_at(String next_at) {
        this.next_at = next_at;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getFirst_period() {
        return first_period;
    }

    public void setFirst_period(String first_period) {
        this.first_period = first_period;
    }

    public String getPeriod_price() {
        return period_price;
    }

    public void setPeriod_price(String period_price) {
        this.period_price = period_price;
    }

    public int getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(int dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public DealerBean getDealer() {
        return dealer;
    }

    public void setDealer(DealerBean dealer) {
        this.dealer = dealer;
    }

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public static class DealerBean {
        /**
         * dealer_id : 1
         * dealer_name : 北京星德宝汽车销售服务有限公司
         */

        private int dealer_id;
        private String dealer_name;

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
    }

    public static class ImageBean {
        /**
         * image_id : 1
         * name :
         * img_path : http://img1.xcarimg.com/b25/s8329/c_20161111164406754639316916595.jpg
         */

        private int image_id;
        private String name;
        private String img_path;

        public int getImage_id() {
            return image_id;
        }

        public void setImage_id(int image_id) {
            this.image_id = image_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }
    }
}
