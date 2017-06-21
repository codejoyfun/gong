package com.runwise.supply.business.entity;

/**
 * Created by libin on 2017/1/24.
 */

public class ImagesBean {

    /**
     * image_id : 10
     * name : asdfs
     * img_path : http://img1.xcarimg.com/b141/s6727/c_20160711161902856092123214503.jpg
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
