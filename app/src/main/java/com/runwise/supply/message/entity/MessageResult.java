package com.runwise.supply.message.entity;

import com.kids.commonframe.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mychao on 2017/7/28.
 */

public class MessageResult extends BaseEntity.ResultBean implements Serializable{

    private List<OrderBean> order;
    private List<ChannelBean> channel;

    public List<OrderBean> getOrder() {
        return order;
    }

    public void setOrder(List<OrderBean> order) {
        this.order = order;
    }

    public List<ChannelBean> getChannel() {
        return channel;
    }

    public void setChannel(List<ChannelBean> channel) {
        this.channel = channel;
    }

    public static class OrderBean implements Serializable{
        /**
         * is_today : false
         * done_datetime : 2017-08-25 11:20:25
         * create_date : 2017-08-23 20:24:03
         * name : SO17082300015
         * confirmation_date : 2017-08-23 20:25:28
         * estimated_time : 2017-08-24 10:00:00
         * waybill : {"user":{"mobile":"15542154698","avatar_url":"","name":"黄飞"},"vehicle":{"name":"Audi/A4/999999999999","license_plate":"999999999999"},"name":"SP17082300005","id":81}
         * deliveryType : fresh
         * amount : 5
         * end_unload_datetime : 2017-08-23 12:58:43
         * isThirdPartLog : false
         * last_message : {"body":"Hello ","id":22908,"date":"2017-08-25 18:17:08","seen":true,"model":"sale.order","author_id":{"avatar_url":"/gongfu/user/avatar/331/4822439085110259747.png","id":374,"name":"卢小宝"}}
         * state : done
         * order_type_id : null
         * recent_trends : 2017-08-25 11:20:25
         * start_unload_datetime : 2017-08-23 12:58:41
         * create_user_name : 卢宝
         * loading_time : 2017-08-24 00:00:00
         * id : 723
         * amount_total : 58.5
         */
        private String orderID;
        private boolean is_today;
        private String done_datetime;
        private String create_date;
        private String name;
        private String confirmation_date;
        private String estimated_time;
        private WaybillBean waybill;

        private String deliveryType;
        private int amount;
        private String end_unload_datetime;
        private boolean isThirdPartLog;
        private LastMessageBean last_message;
        private String state;
        private String order_type_id;
        private String recent_trends;
        private String start_unload_datetime;
        private String create_user_name;
        private String loading_time;
        private int id;
        private double amount_total;

        public String getDeliveryType() {
            return deliveryType;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }

        public String getOrderID() {
            return orderID;
        }

        public void setOrderID(String orderID) {
            this.orderID = orderID;
        }

        public boolean isIs_today() {
            return is_today;
        }

        public void setIs_today(boolean is_today) {
            this.is_today = is_today;
        }

        public String getDone_datetime() {
            return done_datetime;
        }

        public void setDone_datetime(String done_datetime) {
            this.done_datetime = done_datetime;
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

        public String getConfirmation_date() {
            return confirmation_date;
        }

        public void setConfirmation_date(String confirmation_date) {
            this.confirmation_date = confirmation_date;
        }

        public String getEstimated_time() {
            return estimated_time;
        }

        public void setEstimated_time(String estimated_time) {
            this.estimated_time = estimated_time;
        }

        public WaybillBean getWaybill() {
            return waybill;
        }

        public void setWaybill(WaybillBean waybill) {
            this.waybill = waybill;
        }


        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getEnd_unload_datetime() {
            return end_unload_datetime;
        }

        public void setEnd_unload_datetime(String end_unload_datetime) {
            this.end_unload_datetime = end_unload_datetime;
        }

        public boolean isIsThirdPartLog() {
            return isThirdPartLog;
        }

        public void setIsThirdPartLog(boolean isThirdPartLog) {
            this.isThirdPartLog = isThirdPartLog;
        }

        public LastMessageBean getLast_message() {
            return last_message;
        }

        public void setLast_message(LastMessageBean last_message) {
            this.last_message = last_message;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getOrder_type_id() {
            return order_type_id;
        }

        public void setOrder_type_id(String order_type_id) {
            this.order_type_id = order_type_id;
        }

        public String getRecent_trends() {
            return recent_trends;
        }

        public void setRecent_trends(String recent_trends) {
            this.recent_trends = recent_trends;
        }

        public String getStart_unload_datetime() {
            return start_unload_datetime;
        }

        public void setStart_unload_datetime(String start_unload_datetime) {
            this.start_unload_datetime = start_unload_datetime;
        }

        public String getCreate_user_name() {
            return create_user_name;
        }

        public void setCreate_user_name(String create_user_name) {
            this.create_user_name = create_user_name;
        }

        public String getLoading_time() {
            return loading_time;
        }

        public void setLoading_time(String loading_time) {
            this.loading_time = loading_time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getAmount_total() {
            return amount_total;
        }

        public void setAmount_total(double amount_total) {
            this.amount_total = amount_total;
        }

        public static class WaybillBean implements Serializable{
            /**
             * user : {"mobile":"15542154698","avatar_url":"","name":"黄飞"}
             * vehicle : {"name":"Audi/A4/999999999999","license_plate":"999999999999"}
             * name : SP17082300005
             * id : 81
             */

            private UserBean user;
            private VehicleBean vehicle;
            private String name;
            private String id;

            public UserBean getUser() {
                return user;
            }

            public void setUser(UserBean user) {
                this.user = user;
            }

            public VehicleBean getVehicle() {
                return vehicle;
            }

            public void setVehicle(VehicleBean vehicle) {
                this.vehicle = vehicle;
            }

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

            public static class UserBean implements Serializable{
                /**
                 * mobile : 15542154698
                 * avatar_url :
                 * name : 黄飞
                 */

                private String mobile;
                private String avatar_url;
                private String name;

                public String getMobile() {
                    return mobile;
                }

                public void setMobile(String mobile) {
                    this.mobile = mobile;
                }

                public String getAvatar_url() {
                    return avatar_url;
                }

                public void setAvatar_url(String avatar_url) {
                    this.avatar_url = avatar_url;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            public static class VehicleBean implements Serializable{
                /**
                 * name : Audi/A4/999999999999
                 * license_plate : 999999999999
                 */

                private String name;
                private String license_plate;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getLicense_plate() {
                    return license_plate;
                }

                public void setLicense_plate(String license_plate) {
                    this.license_plate = license_plate;
                }
            }
        }

        public static class LastMessageBean implements Serializable{
            /**
             * body : Hello
             * id : 22908
             * date : 2017-08-25 18:17:08
             * seen : true
             * model : sale.order
             * author_id : {"avatar_url":"/gongfu/user/avatar/331/4822439085110259747.png","id":374,"name":"卢小宝"}
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
                 * avatar_url : /gongfu/user/avatar/331/4822439085110259747.png
                 * id : 374
                 * name : 卢小宝
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

    public static class ChannelBean {
        /**
         * read : true
         * last_message : {}
         * id : 21
         * name : 新品上市
         */

        private boolean read;
        private LastMessageBeanX last_message;
        private int id;
        private String name;

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public LastMessageBeanX getLast_message() {
            return last_message;
        }

        public void setLast_message(LastMessageBeanX last_message) {
            this.last_message = last_message;
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

        public static class LastMessageBeanX {

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

            public static class AuthorIdBean {
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
}
