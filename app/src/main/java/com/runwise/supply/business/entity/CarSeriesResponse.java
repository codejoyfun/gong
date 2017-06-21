package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by libin on 2017/1/23.
 */

public class CarSeriesResponse extends BaseEntity {

    /**
     * err_code : 0
     * data : {"entity":{"car_id":2,"series_id":1,"title":"2017款宝马3系 330Li M运动型\t","sub_title":"330Li M运动型","brand_id":1,"image_id":3,"sale_price":"429900.00","period_id":3,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":3,"first_period":"30000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"images":[{"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"},{"image_id":4,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164527920916075073482.jpg"}]},"entities":[{"car_id":2,"series_id":1,"title":"2017款宝马3系 330Li M运动型\t","sub_title":"330Li M运动型","brand_id":1,"image_id":3,"sale_price":"429900.00","period_id":3,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":3,"first_period":"30000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"image":{"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"},"dealer_id":0},{"car_id":1,"series_id":1,"title":"2017款宝马3系 330Li xDrive豪华设计套装","sub_title":"330Li xDrive豪华设计套装","brand_id":1,"image_id":1,"sale_price":"485900.00","period_id":1,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":1,"first_period":"10000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"image":{"image_id":1,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164405249740506597437.jpg"},"dealer_id":0}]}
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
         * entity : {"car_id":2,"series_id":1,"title":"2017款宝马3系 330Li M运动型\t","sub_title":"330Li M运动型","brand_id":1,"image_id":3,"sale_price":"429900.00","period_id":3,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":3,"first_period":"30000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"images":[{"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"},{"image_id":4,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164527920916075073482.jpg"}]}
         * entities : [{"car_id":2,"series_id":1,"title":"2017款宝马3系 330Li M运动型\t","sub_title":"330Li M运动型","brand_id":1,"image_id":3,"sale_price":"429900.00","period_id":3,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":3,"first_period":"30000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"image":{"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"},"dealer_id":0},{"car_id":1,"series_id":1,"title":"2017款宝马3系 330Li xDrive豪华设计套装","sub_title":"330Li xDrive豪华设计套装","brand_id":1,"image_id":1,"sale_price":"485900.00","period_id":1,"num":1000,"brand":{"brand_id":1,"name":"宝马"},"period":{"period_id":1,"first_period":"10000.00","month_period":"5000.00"},"series":{"series_id":1,"series_name":"宝马3系"},"image":{"image_id":1,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164405249740506597437.jpg"},"dealer_id":0}]
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
             * car_id : 2
             * series_id : 1
             * title : 2017款宝马3系 330Li M运动型
             * sub_title : 330Li M运动型
             * brand_id : 1
             * image_id : 3
             * sale_price : 429900.00
             * period_id : 3
             * num : 1000
             * brand : {"brand_id":1,"name":"宝马"}
             * period : {"period_id":3,"first_period":"30000.00","month_period":"5000.00"}
             * series : {"series_id":1,"series_name":"宝马3系"}
             * share_url:
             * images : [{"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"},{"image_id":4,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164527920916075073482.jpg"}]
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
            private String share_url;
            private List<ImagesBean> images;

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
                 * period_id : 3
                 * first_period : 30000.00
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
        }

        public static class EntitiesBean {
            /**
             * car_id : 2
             * series_id : 1
             * title : 2017款宝马3系 330Li M运动型
             * sub_title : 330Li M运动型
             * brand_id : 1
             * image_id : 3
             * sale_price : 429900.00
             * period_id : 3
             * num : 1000
             * brand : {"brand_id":1,"name":"宝马"}
             * period : {"period_id":3,"first_period":"30000.00","month_period":"5000.00"}
             * series : {"series_id":1,"series_name":"宝马3系"}
             * image : {"image_id":3,"img_path":"http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg"}
             * dealer_id : 0
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
            private BrandBeanX brand;
            private PeriodBeanX period;
            private SeriesBeanX series;
            private ImageBean image;
            private int dealer_id;

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

            public BrandBeanX getBrand() {
                return brand;
            }

            public void setBrand(BrandBeanX brand) {
                this.brand = brand;
            }

            public PeriodBeanX getPeriod() {
                return period;
            }

            public void setPeriod(PeriodBeanX period) {
                this.period = period;
            }

            public SeriesBeanX getSeries() {
                return series;
            }

            public void setSeries(SeriesBeanX series) {
                this.series = series;
            }

            public ImageBean getImage() {
                return image;
            }

            public void setImage(ImageBean image) {
                this.image = image;
            }

            public int getDealer_id() {
                return dealer_id;
            }

            public void setDealer_id(int dealer_id) {
                this.dealer_id = dealer_id;
            }

            public static class BrandBeanX {
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

            public static class PeriodBeanX {
                /**
                 * period_id : 3
                 * first_period : 30000.00
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

            public static class SeriesBeanX {
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

            public static class ImageBean {
                /**
                 * image_id : 3
                 * img_path : http://img1.xcarimg.com/b25/s8329/c_20161111164408593002296966325.jpg
                 */

                private int image_id;
                private String img_path;

                public int getImage_id() {
                    return image_id;
                }

                public void setImage_id(int image_id) {
                    this.image_id = image_id;
                }

                public String getImg_path() {
                    return img_path;
                }

                public void setImg_path(String img_path) {
                    this.img_path = img_path;
                }
            }
        }
    }
}
