package com.runwise.supply.firstpage.entity;

/**
 * Created by libin on 2017/7/21.
 */

public enum OrderState {
        DRAFT("draft","待确认"),SALE("sale","已确认"),PEISONG("peisong","已发货"),
        DONE("done","待评价"),RATED("rated","已评价"),CANCEL("cancel","已取消");
        private String name;
        private String value;
        private OrderState(String name,String value){
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        public static String getValueByName(String name){
            for(OrderState s : OrderState.values()){
                if (s.getName().equals(name)){
                    return s.getValue();
                }
            }
            return "未知状态";
        }
}
