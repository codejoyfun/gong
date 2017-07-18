package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by mychao on 2017/7/17.
 */

public class CheckResult extends BaseEntity.ResultBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * delta_value : 0
         * create_date : 2017-06-29 13:54:45
         * name : PD37-2017-06-29
         * create_partner : {"name":"陈星","id":22}
         * state : confirm
         * id : 82
         */

        private double delta_value;
        private String create_date;
        private String name;
        private CreatePartnerBean create_partner;
        private String state;
        private int id;

        public double getDelta_value() {
            return delta_value;
        }

        public void setDelta_value(double delta_value) {
            this.delta_value = delta_value;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CreatePartnerBean getCreate_partner() {
            return create_partner;
        }

        public void setCreate_partner(CreatePartnerBean create_partner) {
            this.create_partner = create_partner;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public static class CreatePartnerBean {
            /**
             * name : 陈星
             * id : 22
             */

            private String name;
            private String id;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
