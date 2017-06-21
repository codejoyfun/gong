package com.runwise.supply.mine.entity;

/**
 * Created by myChaoFile on 17/1/19.
 */

public class ImageEntity {
    private String image_id;//": 1,
    private String name;//": "",
    private String img_path;//": "http://car2.autoimg.cn/cardfs/product/g15/M13/5B/EE/t_autohomecar__wKjByFXe3OeAd6M2AASXkHD20UE021.jpg"    //图片封面

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
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
