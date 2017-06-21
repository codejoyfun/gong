package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by libin on 2017/1/24.
 */

public class CarResponse extends BaseEntity {

    /**
     * err_code : 0
     * data : {"entity":{"car_id":1,"series_id":1,"title":"2017款宝马3系 330Li xDrive豪华设计套装","sub_title":"330Li xDrive豪华设计套装","brand_id":1,"image_id":1,"sale_price":"485900.00","period_id":1,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":1,"first_period":"10000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"attribute":{"attribute_id":1,"car_id":1,"detail":{"baseinfo":{"name":"基本参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]},"carleninfo":{"name":"车长参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]}}},"images":[{"image_id":1,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164405249740506597437.jpg"},{"image_id":2,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164406754639316916595.jpg"}]},"entities":[{"dealer_id":4,"dealer_name":"北京博瑞祥云汽车销售服务有限公司","contact_phone":"4008192814","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市大兴区西红门镇中鼎路19号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":3,"dealer_name":"北京国服信奥兴汽车有限公司","contact_phone":"4008192814","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市石景山区古城大街国际汽车贸易服务园区F区9号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":2,"dealer_name":"北京运通兴宝汽车销售服务有限公司","contact_phone":"4008193095","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市大兴区西红门镇中鼎路19号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":1,"dealer_name":"北京星德宝汽车销售服务有限公司","contact_phone":"4006136766","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市朝阳区高碑店乡白家楼8号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1}]}
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
         * entity : {"car_id":1,"series_id":1,"title":"2017款宝马3系 330Li xDrive豪华设计套装","sub_title":"330Li xDrive豪华设计套装","brand_id":1,"image_id":1,"sale_price":"485900.00","period_id":1,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":1,"first_period":"10000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"attribute":{"attribute_id":1,"car_id":1,"detail":{"baseinfo":{"name":"基本参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)","name":"发动机2","value":"2.0漩涡2"},{"name":"发动机2","value":"2.0漩涡2"}]},"carleninfo":{"name":"车长参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)","name":"发动机2","value":"2.0漩涡2"},{"name":"发动机2","value":"2.0漩涡2"}]}}},"images":[{"image_id":1,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164405249740506597437.jpg"},{"image_id":2,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164406754639316916595.jpg"}]}
         * entities : [{"dealer_id":4,"dealer_name":"北京博瑞祥云汽车销售服务有限公司","contact_phone":"4008192814","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市大兴区西红门镇中鼎路19号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":3,"dealer_name":"北京国服信奥兴汽车有限公司","contact_phone":"4008192814","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市石景山区古城大街国际汽车贸易服务园区F区9号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":2,"dealer_name":"北京运通兴宝汽车销售服务有限公司","contact_phone":"4008193095","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市大兴区西红门镇中鼎路19号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1},{"dealer_id":1,"dealer_name":"北京星德宝汽车销售服务有限公司","contact_phone":"4006136766","score":0,"lat":"39.961957","lng":"116.380729","address":"北京市朝阳区高碑店乡白家楼8号","attribute_id":1,"attribute":{"attribute_id":1,"att_name":"4S店"},"car_id":1}]
         */

        private EntityBean entity;
        private List<EntitiesBean> entities;

        public EntityBean getEntity() {
            return entity;
        }

        public void setEntity(EntityBean entity) {
            this.entity = entity;
        }

        public List<EntitiesBean> getEntities() {
            return entities;
        }

        public void setEntities(List<EntitiesBean> entities) {
            this.entities = entities;
        }

        public static class EntityBean {
            /**
             * car_id : 1
             * series_id : 1
             * title : 2017款宝马3系 330Li xDrive豪华设计套装
             * sub_title : 330Li xDrive豪华设计套装
             * brand_id : 1
             * image_id : 1
             * sale_price : 485900.00
             * period_id : 1
             * num : 1000
             * brand : {"brand_id":1,"name":"宝马"}
             * period : {"period_id":1,"first_period":"10000.00","month_period":"5000.00"}
             * series : {"series_id":1,"series_name":"宝马3系"}
             * attribute : {"attribute_id":1,"car_id":1,"detail":{"baseinfo":{"name":"基本参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]},"carleninfo":{"name":"车长参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]}}}
             * images : [{"image_id":1,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164405249740506597437.jpg"},{"image_id":2,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164406754639316916595.jpg"}]
             * is_collect : 0
             */

            private int car_id;
            private int series_id;
            private String title;
            private String sub_title;
            private int brand_id;
            private int image_id;
            private String sale_price;
            private int period_id;
            private int num;
            private BrandBean brand;
            private PeriodBean period;
            private SeriesBean series;
            private AttributeBean attribute;
            private List<ImagesBean> images;

            public int getIs_collect() {
                return is_collect;
            }

            public void setIs_collect(int is_collect) {
                this.is_collect = is_collect;
            }

            private int is_collect;
            private String share_url;
            private String apply_info_url;

            public String getApply_info_url() {
                return apply_info_url;
            }

            public void setApply_info_url(String apply_info_url) {
                this.apply_info_url = apply_info_url;
            }

            public String getShare_url() {
                return share_url;
            }

            public void setShare_url(String share_url) {
                this.share_url = share_url;
            }

            public int getCar_id() {
                return car_id;
            }

            public void setCar_id(int car_id) {
                this.car_id = car_id;
            }

            public int getSeries_id() {
                return series_id;
            }

            public void setSeries_id(int series_id) {
                this.series_id = series_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSub_title() {
                return sub_title;
            }

            public void setSub_title(String sub_title) {
                this.sub_title = sub_title;
            }

            public int getBrand_id() {
                return brand_id;
            }

            public void setBrand_id(int brand_id) {
                this.brand_id = brand_id;
            }

            public int getImage_id() {
                return image_id;
            }

            public void setImage_id(int image_id) {
                this.image_id = image_id;
            }

            public String getSale_price() {
                return sale_price;
            }

            public void setSale_price(String sale_price) {
                this.sale_price = sale_price;
            }

            public int getPeriod_id() {
                return period_id;
            }

            public void setPeriod_id(int period_id) {
                this.period_id = period_id;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public BrandBean getBrand() {
                return brand;
            }

            public void setBrand(BrandBean brand) {
                this.brand = brand;
            }

            public PeriodBean getPeriod() {
                return period;
            }

            public void setPeriod(PeriodBean period) {
                this.period = period;
            }

            public SeriesBean getSeries() {
                return series;
            }

            public void setSeries(SeriesBean series) {
                this.series = series;
            }

            public AttributeBean getAttribute() {
                return attribute;
            }

            public void setAttribute(AttributeBean attribute) {
                this.attribute = attribute;
            }

            public List<ImagesBean> getImages() {
                return images;
            }

            public void setImages(List<ImagesBean> images) {
                this.images = images;
            }

            public static class BrandBean {
                /**
                 * brand_id : 1
                 * name : 宝马
                 */

                private int brand_id;
                private String name;

                public int getBrand_id() {
                    return brand_id;
                }

                public void setBrand_id(int brand_id) {
                    this.brand_id = brand_id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            public static class PeriodBean {
                /**
                 * period_id : 1
                 * first_period : 10000.00
                 * month_period : 5000.00
                 */

                private int period_id;
                private String first_period;
                private String month_period;

                public int getPeriod_id() {
                    return period_id;
                }

                public void setPeriod_id(int period_id) {
                    this.period_id = period_id;
                }

                public String getFirst_period() {
                    return first_period;
                }

                public void setFirst_period(String first_period) {
                    this.first_period = first_period;
                }

                public String getMonth_period() {
                    return month_period;
                }

                public void setMonth_period(String month_period) {
                    this.month_period = month_period;
                }
            }

            public static class SeriesBean {
                /**
                 * series_id : 1
                 * series_name : 宝马3系
                 */

                private int series_id;
                private String series_name;

                public int getSeries_id() {
                    return series_id;
                }

                public void setSeries_id(int series_id) {
                    this.series_id = series_id;
                }

                public String getSeries_name() {
                    return series_name;
                }

                public void setSeries_name(String series_name) {
                    this.series_name = series_name;
                }
            }

            public static class AttributeBean {
                /**
                 * attribute_id : 1
                 * car_id : 1
                 * detail : {"baseinfo":{"name":"基本参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]},"carleninfo":{"name":"车长参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]}}
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
                     * baseinfo : {"name":"基本参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)","name":"发动机2","value":"2.0漩涡2"},{"name":"发动机2","value":"2.0漩涡2"}]}
                     * carleninfo : {"name":"车长参数","items":[{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]}
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
                         * items : [{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]
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
                             * field_name : 发动机
                             * field_value : 140kW(2.0L涡轮增压)
                             * name : 发动机2
                             * value : 2.0漩涡2
                             */

                            private String field_name;
                            private String field_value;
                            private String name;
                            private String value;

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
                        }
                    }

                    public static class CarleninfoBean {
                        /**
                         * name : 车长参数
                         * items : [{"field_name":"发动机","field_value":"140kW(2.0L涡轮增压)"},{"name":"发动机2","value":"2.0漩涡2"}]
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
                             * field_value : 140kW(2.0L涡轮增压)
                             * name : 发动机2
                             * value : 2.0漩涡2
                             */

                            private String field_name;
                            private String field_value;
                            private String name;
                            private String value;

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
                        }
                    }
                }
            }
        }

        public static class EntitiesBean {
            /**
             * dealer_id : 4
             * dealer_name : 北京博瑞祥云汽车销售服务有限公司
             * contact_phone : 4008192814
             * score : 0
             * lat : 39.961957
             * lng : 116.380729
             * address : 北京市大兴区西红门镇中鼎路19号
             * attribute_id : 1
             * attribute : {"attribute_id":1,"att_name":"4S店"}
             * car_id : 1
             */

            private int dealer_id;
            private String dealer_name;
            private String contact_phone;
            private int score;
            private String lat;
            private String lng;
            private String address;
            private int attribute_id;
            private AttributeBeanX attribute;
            private int car_id;

            public int getDealer_id() {
                return dealer_id;
            }

            public void setDealer_id(int dealer_id) {
                this.dealer_id = dealer_id;
            }

            public String getDealer_name() {
                return dealer_name;
            }

            public void setDealer_name(String dealer_name) {
                this.dealer_name = dealer_name;
            }

            public String getContact_phone() {
                return contact_phone;
            }

            public void setContact_phone(String contact_phone) {
                this.contact_phone = contact_phone;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getAttribute_id() {
                return attribute_id;
            }

            public void setAttribute_id(int attribute_id) {
                this.attribute_id = attribute_id;
            }

            public AttributeBeanX getAttribute() {
                return attribute;
            }

            public void setAttribute(AttributeBeanX attribute) {
                this.attribute = attribute;
            }

            public int getCar_id() {
                return car_id;
            }

            public void setCar_id(int car_id) {
                this.car_id = car_id;
            }

            public static class AttributeBeanX {
                /**
                 * attribute_id : 1
                 * att_name : 4S店
                 */

                private int attribute_id;
                private String att_name;

                public int getAttribute_id() {
                    return attribute_id;
                }

                public void setAttribute_id(int attribute_id) {
                    this.attribute_id = attribute_id;
                }

                public String getAtt_name() {
                    return att_name;
                }

                public void setAtt_name(String att_name) {
                    this.att_name = att_name;
                }
            }
        }
    }
}
