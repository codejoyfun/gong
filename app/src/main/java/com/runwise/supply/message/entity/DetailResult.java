package com.runwise.supply.message.entity;

import com.kids.commonframe.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mychao on 2017/7/28.
 */

public class DetailResult extends BaseEntity.ResultBean{

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
        /**
         * body : 各门店请注意，“拍黄瓜”将会从6月8号到7月1号期间暂停供应，请留意，不便之处敬请原谅。
         * id : 14047
         * date : 2017-06-08 16:05:55
         * seen : true
         * model : mail.channel
         * author_id : {"avatar_url":"","id":3,"name":"Administrator"}
         */

        private String body;
        private int id;
        private String date;
        private boolean seen;
        private String model;
        private AuthorIdBean author_id;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public boolean isSeen() {
            return seen;
        }

        public void setSeen(boolean seen) {
            this.seen = seen;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public AuthorIdBean getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(AuthorIdBean author_id) {
            this.author_id = author_id;
        }

        public static class AuthorIdBean implements Serializable{
            /**
             * avatar_url :
             * id : 3
             * name : Administrator
             */

            private String avatar_url;
            private int id;
            private String name;

            public String getAvatar_url() {
                return avatar_url;
            }

            public void setAvatar_url(String avatar_url) {
                this.avatar_url = avatar_url;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
