package com.runwise.supply.index.entity;

import java.util.List;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class HomeList {
    private List<IndexPager> banners;
    private List<IndexCarSys> brands;
    private List<IndexCarInfo> cars;
    private List<IndexDealers> dealers;

    public List<IndexPager> getBanners() {
        return banners;
    }

    public void setBanners(List<IndexPager> banners) {
        this.banners = banners;
    }

    public List<IndexCarSys> getBrands() {
        return brands;
    }

    public void setBrands(List<IndexCarSys> brands) {
        this.brands = brands;
    }

    public List<IndexCarInfo> getCars() {
        return cars;
    }

    public void setCars(List<IndexCarInfo> cars) {
        this.cars = cars;
    }

    public List<IndexDealers> getDealers() {
        return dealers;
    }

    public void setDealers(List<IndexDealers> dealers) {
        this.dealers = dealers;
    }
}
