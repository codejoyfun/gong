package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateLineRequest {

    private List<OrderBean> order;

    public List<OrderBean> getOrder() {
        return order;
    }

    public void setOrder(List<OrderBean> order) {
        this.order = order;
    }

    public static class OrderBean {
        /**
         * line_id : 562
         * quality_score : 5
         */

        private int line_id;
        private int quality_score;

        public int getLine_id() {
            return line_id;
        }

        public void setLine_id(int line_id) {
            this.line_id = line_id;
        }

        public int getQuality_score() {
            return quality_score;
        }

        public void setQuality_score(int quality_score) {
            this.quality_score = quality_score;
        }
    }
}
