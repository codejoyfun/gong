package com.runwise.supply.mine.entity;

import java.util.List;

/**
 * Created by myChaoFile on 2017/8/31.
 */

public class RequestQuestion {

    /**
     * user_input_id : 37
     * question_ids : [{"value":2,"id":1},{"value":4,"id":2},{"value":2,"id":3},{"value":3,"id":5},{"id":8,"value":"你明明"}]
     */

    private int user_input_id;
    private List<QuestionIdsBean> question_ids;

    public int getUser_input_id() {
        return user_input_id;
    }

    public void setUser_input_id(int user_input_id) {
        this.user_input_id = user_input_id;
    }

    public List<QuestionIdsBean> getQuestion_ids() {
        return question_ids;
    }

    public void setQuestion_ids(List<QuestionIdsBean> question_ids) {
        this.question_ids = question_ids;
    }

    public static class QuestionIdsBean {
        /**
         * value : 2
         * id : 1
         */

        private String value;
        private int id;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
