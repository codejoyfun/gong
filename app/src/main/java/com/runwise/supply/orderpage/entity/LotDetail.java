package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by myChaoFile on 2017/8/30.
 */

public class LotDetail {

    /**
     * lot : {"lotID":284,"lotName":"Z170828000076","attList":[{"value":"哈哈哈哈哈","key":"LuckyTest"}],"attachmentList":[1225]}
     */

    private LotBean lot;

    public LotBean getLot() {
        return lot;
    }

    public void setLot(LotBean lot) {
        this.lot = lot;
    }

    public static class LotBean {
        /**
         * lotID : 284
         * lotName : Z170828000076
         * attList : [{"value":"哈哈哈哈哈","key":"LuckyTest"}]
         * attachmentList : [1225]
         */

        private int lotID;
        private String lotName;
        private List<AttListBean> attList;
        private List<Integer> attachmentList;

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

        public List<AttListBean> getAttList() {
            return attList;
        }

        public void setAttList(List<AttListBean> attList) {
            this.attList = attList;
        }

        public List<Integer> getAttachmentList() {
            return attachmentList;
        }

        public void setAttachmentList(List<Integer> attachmentList) {
            this.attachmentList = attachmentList;
        }

        public static class AttListBean {
            /**
             * value : 哈哈哈哈哈
             * key : LuckyTest
             */

            private String value;
            private String key;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }
}
