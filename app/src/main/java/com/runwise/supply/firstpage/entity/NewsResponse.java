package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/6/28.
 */

public class NewsResponse {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * url : https://mp.weixin.qq.com/s?__biz=MzI0MTgwMTE1NA==&mid=2247483831&idx=1&sn=e36bab188314fdc17ff8e65cd0a558e8&chksm=e907431ade70ca0ccf3392dcc821aa63f767ae4f272cac40db0a3d5e5855797d231c6df9a1f5#rd
         * wechatID : 22
         * tagsList : ["餐饮创业"]
         * avatorUrl : /gongfu/v2/wechat/22/image/
         * title : 玩转IP餐厅，这几招就够了！
         */

        private String url;
        private int wechatID;
        private String avatorUrl;
        private String title;
        private List<String> tagsList;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWechatID() {
            return wechatID;
        }

        public void setWechatID(int wechatID) {
            this.wechatID = wechatID;
        }

        public String getAvatorUrl() {
            return avatorUrl;
        }

        public void setAvatorUrl(String avatorUrl) {
            this.avatorUrl = avatorUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getTagsList() {
            return tagsList;
        }

        public void setTagsList(List<String> tagsList) {
            this.tagsList = tagsList;
        }
    }
}
