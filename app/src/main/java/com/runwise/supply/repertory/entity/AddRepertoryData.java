package com.runwise.supply.repertory.entity;

/**
 * Created by myChaoFile on 2017/8/25.
 */

public class AddRepertoryData {

    /**
     * lotNews : {"lifeEndDate":"2017-12-03 00:00:00","lotID":264,"lotName":"Z170825000016","productID":20}
     */

    private LotNewsBean lotNews;

    public LotNewsBean getLotNews() {
        return lotNews;
    }

    public void setLotNews(LotNewsBean lotNews) {
        this.lotNews = lotNews;
    }

    public static class LotNewsBean {
        /**
         * lifeEndDate : 2017-12-03 00:00:00
         * lotID : 264
         * lotName : Z170825000016
         * productID : 20
         */

        private String lifeEndDate;
        private int lotID;
        private String lotName;
        private int productID;

        public String getLifeEndDate() {
            return lifeEndDate;
        }

        public void setLifeEndDate(String lifeEndDate) {
            this.lifeEndDate = lifeEndDate;
        }

        public int getLotID() {
            return lotID;
        }

        public void setLotID(int lotID) {
            this.lotID = lotID;
        }

        public String getLotName() {
            return lotName;
        }

        public void setLotName(String lotName) {
            this.lotName = lotName;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }
    }
}
