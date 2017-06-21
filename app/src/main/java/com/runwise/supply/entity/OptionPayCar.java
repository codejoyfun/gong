package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class OptionPayCar {
    private String title;//": "朗境 230TSI DSG 舒适版1111",    //标题
    private String sub_title;//": "朗境"    //短标题
    private String sale_price;//": "485900.00",
    private String market_price;//": "486900.00"

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

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }
}
