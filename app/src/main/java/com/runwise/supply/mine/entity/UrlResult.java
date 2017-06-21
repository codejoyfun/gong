package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 2017/3/20.
 */

public class UrlResult extends BaseEntity{

    /**
     * err_code : 0
     * msg : success
     * data : {"entity":{"url_addr":"http://114.215.40.244:8085/about-us"}}
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
         * entity : {"url_addr":"http://114.215.40.244:8085/about-us"}
         */

        private EntityBean entity;

        public EntityBean getEntity() {
            return entity;
        }

        public void setEntity(EntityBean entity) {
            this.entity = entity;
        }

        public static class EntityBean {
            /**
             * url_addr : http://114.215.40.244:8085/about-us
             */

            private String url_addr;

            public String getUrl_addr() {
                return url_addr;
            }

            public void setUrl_addr(String url_addr) {
                this.url_addr = url_addr;
            }
        }
    }
}
