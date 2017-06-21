package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by libin on 2017/2/16.
 */

public class CollectResponse extends BaseEntity {

    /**
     * err_code : 0
     * data : {"collect_id":9}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * collect_id : 9
         */

        private int collect_id;

        public int getCollect_id() {
            return collect_id;
        }

        public void setCollect_id(int collect_id) {
            this.collect_id = collect_id;
        }
    }
}
