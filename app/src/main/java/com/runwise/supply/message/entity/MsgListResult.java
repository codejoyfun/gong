package com.runwise.supply.message.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by mychao on 2017/8/1.
 */

public class MsgListResult extends BaseEntity.ResultBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * body : 热门问题列表
         * faq : [{"question":"支付方式有哪些","id":3},{"question":"配送时间","id":2},{"question":"是否可以进行退货？","id":1}]
         * id : 26944
         * date : 2017-07-17 17:07:53
         * seen : true
         * model : question
         */

        private String body;
        private int id;
        private String date;
        private boolean seen;
        private String model;
        private List<FaqBean> faq;
        private AuthorId author_id;

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

        public List<FaqBean> getFaq() {
            return faq;
        }

        public void setFaq(List<FaqBean> faq) {
            this.faq = faq;
        }

        public AuthorId getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(AuthorId author_id) {
            this.author_id = author_id;
        }

        public static class FaqBean {
            /**
             * question : 支付方式有哪些
             * id : 3
             */

            private String question;
            private int id;

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
        public static class AuthorId{

            /**
             * avatar_url : /gongfu/user/avatar/18/2145901519106070062.png
             * id : 27
             * name : 赵京
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
