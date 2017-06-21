package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by libin on 2017/2/22.
 */

public class ConfigResponse extends BaseEntity {

    /**
     * err_code : 0
     * data : {"entity":{"attribute_id":1,"car_id":1,"detail":{"baseinfo":{"name":"基本参数","items":[{"field_name":"变速箱","field_value":"ii140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]},"carleninfo":{"name":"车长参数","items":[{"field_name":"发动机","field_value":"xxxx140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]}}}}
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
         * entity : {"attribute_id":1,"car_id":1,"detail":{"baseinfo":{"name":"基本参数","items":[{"field_name":"变速箱","field_value":"ii140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]},"carleninfo":{"name":"车长参数","items":[{"field_name":"发动机","field_value":"xxxx140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]}}}
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
             * attribute_id : 1
             * car_id : 1
             * detail : {"baseinfo":{"name":"基本参数","items":[{"field_name":"变速箱","field_value":"ii140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]},"carleninfo":{"name":"车长参数","items":[{"field_name":"发动机","field_value":"xxxx140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]}}
             */

            private int attribute_id;
            private int car_id;
            private DetailBean detail;

            public int getAttribute_id() {
                return attribute_id;
            }

            public void setAttribute_id(int attribute_id) {
                this.attribute_id = attribute_id;
            }

            public int getCar_id() {
                return car_id;
            }

            public void setCar_id(int car_id) {
                this.car_id = car_id;
            }

            public DetailBean getDetail() {
                return detail;
            }

            public void setDetail(DetailBean detail) {
                this.detail = detail;
            }

            public static class DetailBean {
                /**
                 * baseinfo : {"name":"基本参数","items":[{"field_name":"变速箱","field_value":"ii140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]}
                 * carleninfo : {"name":"车长参数","items":[{"field_name":"发动机","field_value":"xxxx140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]}
                 */

                private BaseinfoBean baseinfo;
                private CarleninfoBean carleninfo;

                public BaseinfoBean getBaseinfo() {
                    return baseinfo;
                }

                public void setBaseinfo(BaseinfoBean baseinfo) {
                    this.baseinfo = baseinfo;
                }

                public CarleninfoBean getCarleninfo() {
                    return carleninfo;
                }

                public void setCarleninfo(CarleninfoBean carleninfo) {
                    this.carleninfo = carleninfo;
                }

                public static class BaseinfoBean {
                    /**
                     * name : 基本参数
                     * items : [{"field_name":"变速箱","field_value":"ii140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]
                     */

                    private String name;
                    private List<ItemsBean> items;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public List<ItemsBean> getItems() {
                        return items;
                    }

                    public void setItems(List<ItemsBean> items) {
                        this.items = items;
                    }

                    public static class ItemsBean {
                        /**
                         * field_name : 变速箱
                         * field_value : ii140kW(2.0L涡轮增压)
                         */

                        private String field_name;
                        private String field_value;

                        public String getField_name() {
                            return field_name;
                        }

                        public void setField_name(String field_name) {
                            this.field_name = field_name;
                        }

                        public String getField_value() {
                            return field_value;
                        }

                        public void setField_value(String field_value) {
                            this.field_value = field_value;
                        }
                    }
                }

                public static class CarleninfoBean {
                    /**
                     * name : 车长参数
                     * items : [{"field_name":"发动机","field_value":"xxxx140kW(2.0L涡轮增压)"},{"field_name":"发动机2","field_value":"2.0漩涡2"}]
                     */

                    private String name;
                    private List<ItemsBeanX> items;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public List<ItemsBeanX> getItems() {
                        return items;
                    }

                    public void setItems(List<ItemsBeanX> items) {
                        this.items = items;
                    }

                    public static class ItemsBeanX {
                        /**
                         * field_name : 发动机
                         * field_value : xxxx140kW(2.0L涡轮增压)
                         */

                        private String field_name;
                        private String field_value;

                        public String getField_name() {
                            return field_name;
                        }

                        public void setField_name(String field_name) {
                            this.field_name = field_name;
                        }

                        public String getField_value() {
                            return field_value;
                        }

                        public void setField_value(String field_value) {
                            this.field_value = field_value;
                        }
                    }
                }
            }
        }
    }
}
