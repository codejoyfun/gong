package com.runwise.supply.index.entity;

/**
 * Created by myChaoFile on 17/1/6.
 */

public class IndexCarSys {
    private String brand_id;//": 3,    //车系id
    private String name;//": "朗境",    //车系名称
    private String logo;//": "http://r1.ykimg.com/0542010158633DA5641DA418248D3737"    //车系图片
    private String series_id;

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSeries_id() {
        return series_id;
    }

    public void setSeries_id(String series_id) {
        this.series_id = series_id;
    }
}
