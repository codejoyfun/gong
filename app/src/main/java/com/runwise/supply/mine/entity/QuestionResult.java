package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by myChaoFile on 2017/8/30.
 */

public class QuestionResult extends BaseEntity.ResultBean implements Serializable{

    /**
     * page_list : [{"question_list":[{"titil":"你认为这个应用的交互能力怎么样？","id":1,"value":""},{"titil":"你认为这个应用的视觉效果怎么样？","id":2,"value":""},{"titil":"你觉得这个应用对你的工作带来多大的方便？","id":3,"value":""},{"titil":"你会对别人推荐这个应用吗？","id":5,"value":""}],"type":"numerical_box","id":1,"title":"用户体验度"},{"question_list":[{"titil":"你对这个应用有什么建议？","id":8,"value":""}],"type":"free_text","id":2,"title":"你对这个应用有什么建议？"}]
     * user_input_id : 30
     */

    private int user_input_id;
    private List<PageListBean> page_list;

    public int getUser_input_id() {
        return user_input_id;
    }

    public void setUser_input_id(int user_input_id) {
        this.user_input_id = user_input_id;
    }

    public List<PageListBean> getPage_list() {
        return page_list;
    }

    public void setPage_list(List<PageListBean> page_list) {
        this.page_list = page_list;
    }

    public static class PageListBean implements Serializable{
        /**
         * question_list : [{"titil":"你认为这个应用的交互能力怎么样？","id":1,"value":""},{"titil":"你认为这个应用的视觉效果怎么样？","id":2,"value":""},{"titil":"你觉得这个应用对你的工作带来多大的方便？","id":3,"value":""},{"titil":"你会对别人推荐这个应用吗？","id":5,"value":""}]
         * type : numerical_box
         * id : 1
         * title : 用户体验度
         */

        private String type;
        private int id;
        private String title;
        private List<QuestionListBean> question_list;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<QuestionListBean> getQuestion_list() {
            return question_list;
        }

        public void setQuestion_list(List<QuestionListBean> question_list) {
            this.question_list = question_list;
        }

        public static class QuestionListBean implements Serializable{
            /**
             * titil : 你认为这个应用的交互能力怎么样？
             * id : 1
             * value :
             */

            private String titil;
            private int id;
            private String value;
            private float raValue;

            public float getRaValue() {
                return raValue;
            }

            public void setRaValue(float raValue) {
                this.raValue = raValue;
            }

            public String getTitil() {
                return titil;
            }

            public void setTitil(String titil) {
                this.titil = titil;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
