package com.runwise.supply.index.entity;

/**
 * Created by myChaoFile on 16/10/24.
 */

public class IndexPager {

    /**
     * banner_id : 2
     * title : image2
     * url_type : 1
     * url_link : http://114.215.40.244:8083/series/detail.json?series_id=9
     * image_path : http://114.215.40.244:8082/uploads/5f4405429798c5950995d685b486c268.png
     * sort : 0
     * api_path : /series/detail.json
     * api_param : {"series_id":"9"}
     */

    private String banner_id;
    private String title;
    private String url_type;
    private String url_link;
    private String image_path;
    private String sort;
    private String api_path;
    private ApiParamBean api_param;

    public String getBanner_id() {
        return banner_id;
    }

    public void setBanner_id(String banner_id) {
        this.banner_id = banner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl_type() {
        return url_type;
    }

    public void setUrl_type(String url_type) {
        this.url_type = url_type;
    }

    public String getUrl_link() {
        return url_link;
    }

    public void setUrl_link(String url_link) {
        this.url_link = url_link;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getApi_path() {
        return api_path;
    }

    public void setApi_path(String api_path) {
        this.api_path = api_path;
    }

    public ApiParamBean getApi_param() {
        return api_param;
    }

    public void setApi_param(ApiParamBean api_param) {
        this.api_param = api_param;
    }

    public static class ApiParamBean {
        /**
         * series_id : 9
         */

        private String series_id;
        private String car_id;
        private String dealer_id;

        public String getSeries_id() {
            return series_id;
        }

        public void setSeries_id(String series_id) {
            this.series_id = series_id;
        }

        public String getCar_id() {
            return car_id;
        }

        public void setCar_id(String car_id) {
            this.car_id = car_id;
        }

        public String getDealer_id() {
            return dealer_id;
        }

        public void setDealer_id(String dealer_id) {
            this.dealer_id = dealer_id;
        }
    }
}
