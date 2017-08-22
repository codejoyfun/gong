package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by myChaoFile on 17/1/19.
 */

public class OrderResult{
    private List<ListBean> list;
    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * lines : [{"productUom":"条","priceUnit":3.8,"discount":0,"returnAmount":0,"deliveredQty":0,"priceSubtotal":11.4,"productID":12,"tallyingAmount":0,"saleOrderProductID":793,"lotIDs":["41"],"stockType":"lengcanghuo","settleAmount":0,"lotList":[{"lotPk":"79341","lotID":41,"name":"Z201707051791","qty":3}],"productUomQty":3}]
         * amountTotal : 11.4
         * endUnloadDatetime : 2017-07-12 16:40:26
         * estimatedDate : 2017-07-13
         * isTwoUnit : false
         * hasReturn : 0
         * loadingTime : 2017-07-13 10:10:00
         * estimatedTime : 2017-07-13 10:00:00
         * createDate : 2017-07-12 12:15:36
         * startUnloadDatetime : 2017-07-12 16:40:24
         * state : peisong
         * receiveUserName :
         * tallyingUserName :
         * isDoubleReceive : false
         * store : {"mobile":"13828239674","partner":"陈星","partnerID":35,"name":"【我家酸菜鱼】斜土路店","address":"上海市徐汇区斜土路508号之俊大厦3层"}
         * stateTracker : ["2017-07-12 16:40 订单已发货","2017-07-12 12:16 订单已确认","2017-07-12 12:15 订单已提交"]
         * settleAmountTotal : 0.0
         * waybill : {"deliverUser":{"mobile":"15521066078","userID":28,"name":"黄峰","avatarUrl":"/gongfu/user/avatar/28/7287890151749052348.png"},"waybillID":171,"name":"PS20170712171","deliverVehicle":{"licensePlate":"粤A AY712","name":"Audi/A6/粤A AY712","vehicleID":4}}
         * hasAttachment : 0
         * isFinishTallying : false
         * createUserName : 陈星
         * orderSettleName : 每周结算
         * publicAmountTotal : 13.34
         * deliveredQty : 0.0
         * confirmationDate : 2017-07-12 12:16:04
         * orderID : 396
         * name : SO396
         * appraisalUserName :
         * amount : 3.0
         * isToday : false
         * doneDatetime :
         */

        private double amountTotal;
        private String endUnloadDatetime;
        private String estimatedDate;
        private boolean isTwoUnit;
        private int hasReturn;
        private String loadingTime;
        private String estimatedTime;
        private String createDate;
        private String startUnloadDatetime;
        private String state;
        private String receiveUserName;
        private String tallyingUserName;
        private boolean isDoubleReceive;
        private StoreBean store;
        private double settleAmountTotal;
        private WaybillBean waybill;
        private int hasAttachment;
        private boolean isFinishTallying;
        private String createUserName;
        private String orderSettleName;
        private double publicAmountTotal;
        private double deliveredQty;
        private String confirmationDate;
        private int orderID;
        private String name;
        private String appraisalUserName;
        private double amount;
        private boolean isToday;
        private String doneDatetime;
        private String estimated_date;
        private List<LinesBean> lines;
        private List<String> stateTracker;

        public String getEstimated_date() {
            return estimated_date;
        }

        public void setEstimated_date(String estimated_date) {
            this.estimated_date = estimated_date;
        }

        public double getAmountTotal() {
            return amountTotal;
        }

        public void setAmountTotal(double amountTotal) {
            this.amountTotal = amountTotal;
        }

        public String getEndUnloadDatetime() {
            return endUnloadDatetime;
        }

        public void setEndUnloadDatetime(String endUnloadDatetime) {
            this.endUnloadDatetime = endUnloadDatetime;
        }

        public String getEstimatedDate() {
            return estimatedDate;
        }

        public void setEstimatedDate(String estimatedDate) {
            this.estimatedDate = estimatedDate;
        }

        public boolean isIsTwoUnit() {
            return isTwoUnit;
        }

        public void setIsTwoUnit(boolean isTwoUnit) {
            this.isTwoUnit = isTwoUnit;
        }

        public int getHasReturn() {
            return hasReturn;
        }

        public void setHasReturn(int hasReturn) {
            this.hasReturn = hasReturn;
        }

        public String getLoadingTime() {
            return loadingTime;
        }

        public void setLoadingTime(String loadingTime) {
            this.loadingTime = loadingTime;
        }

        public String getEstimatedTime() {
            return estimatedTime;
        }

        public void setEstimatedTime(String estimatedTime) {
            this.estimatedTime = estimatedTime;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getStartUnloadDatetime() {
            return startUnloadDatetime;
        }

        public void setStartUnloadDatetime(String startUnloadDatetime) {
            this.startUnloadDatetime = startUnloadDatetime;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getReceiveUserName() {
            return receiveUserName;
        }

        public void setReceiveUserName(String receiveUserName) {
            this.receiveUserName = receiveUserName;
        }

        public String getTallyingUserName() {
            return tallyingUserName;
        }

        public void setTallyingUserName(String tallyingUserName) {
            this.tallyingUserName = tallyingUserName;
        }

        public boolean isIsDoubleReceive() {
            return isDoubleReceive;
        }

        public void setIsDoubleReceive(boolean isDoubleReceive) {
            this.isDoubleReceive = isDoubleReceive;
        }

        public StoreBean getStore() {
            return store;
        }

        public void setStore(StoreBean store) {
            this.store = store;
        }

        public double getSettleAmountTotal() {
            return settleAmountTotal;
        }

        public void setSettleAmountTotal(double settleAmountTotal) {
            this.settleAmountTotal = settleAmountTotal;
        }

        public WaybillBean getWaybill() {
            return waybill;
        }

        public void setWaybill(WaybillBean waybill) {
            this.waybill = waybill;
        }

        public int getHasAttachment() {
            return hasAttachment;
        }

        public void setHasAttachment(int hasAttachment) {
            this.hasAttachment = hasAttachment;
        }

        public boolean isIsFinishTallying() {
            return isFinishTallying;
        }

        public void setIsFinishTallying(boolean isFinishTallying) {
            this.isFinishTallying = isFinishTallying;
        }

        public String getCreateUserName() {
            return createUserName;
        }

        public void setCreateUserName(String createUserName) {
            this.createUserName = createUserName;
        }

        public String getOrderSettleName() {
            return orderSettleName;
        }

        public void setOrderSettleName(String orderSettleName) {
            this.orderSettleName = orderSettleName;
        }

        public double getPublicAmountTotal() {
            return publicAmountTotal;
        }

        public void setPublicAmountTotal(double publicAmountTotal) {
            this.publicAmountTotal = publicAmountTotal;
        }

        public double getDeliveredQty() {
            return deliveredQty;
        }

        public void setDeliveredQty(double deliveredQty) {
            this.deliveredQty = deliveredQty;
        }

        public String getConfirmationDate() {
            return confirmationDate;
        }

        public void setConfirmationDate(String confirmationDate) {
            this.confirmationDate = confirmationDate;
        }

        public int getOrderID() {
            return orderID;
        }

        public void setOrderID(int orderID) {
            this.orderID = orderID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAppraisalUserName() {
            return appraisalUserName;
        }

        public void setAppraisalUserName(String appraisalUserName) {
            this.appraisalUserName = appraisalUserName;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public boolean isIsToday() {
            return isToday;
        }

        public void setIsToday(boolean isToday) {
            this.isToday = isToday;
        }

        public String getDoneDatetime() {
            return doneDatetime;
        }

        public void setDoneDatetime(String doneDatetime) {
            this.doneDatetime = doneDatetime;
        }

        public List<LinesBean> getLines() {
            return lines;
        }

        public void setLines(List<LinesBean> lines) {
            this.lines = lines;
        }

        public List<String> getStateTracker() {
            return stateTracker;
        }

        public void setStateTracker(List<String> stateTracker) {
            this.stateTracker = stateTracker;
        }

        public static class StoreBean {
            /**
             * mobile : 13828239674
             * partner : 陈星
             * partnerID : 35
             * name : 【我家酸菜鱼】斜土路店
             * address : 上海市徐汇区斜土路508号之俊大厦3层
             */

            private String mobile;
            private String partner;
            private int partnerID;
            private String name;
            private String address;

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getPartner() {
                return partner;
            }

            public void setPartner(String partner) {
                this.partner = partner;
            }

            public int getPartnerID() {
                return partnerID;
            }

            public void setPartnerID(int partnerID) {
                this.partnerID = partnerID;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }

        public static class WaybillBean {
            /**
             * deliverUser : {"mobile":"15521066078","userID":28,"name":"黄峰","avatarUrl":"/gongfu/user/avatar/28/7287890151749052348.png"}
             * waybillID : 171
             * name : PS20170712171
             * deliverVehicle : {"licensePlate":"粤A AY712","name":"Audi/A6/粤A AY712","vehicleID":4}
             */

            private DeliverUserBean deliverUser;
            private String waybillID;
            private String name;
            private DeliverVehicleBean deliverVehicle;

            public DeliverUserBean getDeliverUser() {
                return deliverUser;
            }

            public void setDeliverUser(DeliverUserBean deliverUser) {
                this.deliverUser = deliverUser;
            }

            public String getWaybillID() {
                return waybillID;
            }

            public void setWaybillID(String waybillID) {
                this.waybillID = waybillID;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public DeliverVehicleBean getDeliverVehicle() {
                return deliverVehicle;
            }

            public void setDeliverVehicle(DeliverVehicleBean deliverVehicle) {
                this.deliverVehicle = deliverVehicle;
            }

            public static class DeliverUserBean {
                /**
                 * mobile : 15521066078
                 * userID : 28
                 * name : 黄峰
                 * avatarUrl : /gongfu/user/avatar/28/7287890151749052348.png
                 */

                private String mobile;
                private int userID;
                private String name;
                private String avatarUrl;

                public String getMobile() {
                    return mobile;
                }

                public void setMobile(String mobile) {
                    this.mobile = mobile;
                }

                public int getUserID() {
                    return userID;
                }

                public void setUserID(int userID) {
                    this.userID = userID;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getAvatarUrl() {
                    return avatarUrl;
                }

                public void setAvatarUrl(String avatarUrl) {
                    this.avatarUrl = avatarUrl;
                }
            }

            public static class DeliverVehicleBean {
                /**
                 * licensePlate : 粤A AY712
                 * name : Audi/A6/粤A AY712
                 * vehicleID : 4
                 */

                private String licensePlate;
                private String name;
                private int vehicleID;

                public String getLicensePlate() {
                    return licensePlate;
                }

                public void setLicensePlate(String licensePlate) {
                    this.licensePlate = licensePlate;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getVehicleID() {
                    return vehicleID;
                }

                public void setVehicleID(int vehicleID) {
                    this.vehicleID = vehicleID;
                }
            }
        }

        public static class LinesBean {
            /**
             * productUom : 条
             * priceUnit : 3.8
             * discount : 0.0
             * returnAmount : 0.0
             * deliveredQty : 0.0
             * priceSubtotal : 11.4
             * productID : 12
             * tallyingAmount : 0.0
             * saleOrderProductID : 793
             * lotIDs : ["41"]
             * stockType : lengcanghuo
             * settleAmount : 0.0
             * lotList : [{"lotPk":"79341","lotID":41,"name":"Z201707051791","qty":3}]
             * productUomQty : 3.0
             */

            private String productUom;
            private double priceUnit;
            private double discount;
            private double returnAmount;
            private double deliveredQty;
            private double priceSubtotal;
            private int productID;
            private double tallyingAmount;
            private int saleOrderProductID;
            private String stockType;
            private double settleAmount;
            private double productUomQty;
            private List<String> lotIDs;
            private List<LotListBean> lotList;

            public String getProductUom() {
                return productUom;
            }

            public void setProductUom(String productUom) {
                this.productUom = productUom;
            }

            public double getPriceUnit() {
                return priceUnit;
            }

            public void setPriceUnit(double priceUnit) {
                this.priceUnit = priceUnit;
            }

            public double getDiscount() {
                return discount;
            }

            public void setDiscount(double discount) {
                this.discount = discount;
            }

            public double getReturnAmount() {
                return returnAmount;
            }

            public void setReturnAmount(double returnAmount) {
                this.returnAmount = returnAmount;
            }

            public double getDeliveredQty() {
                return deliveredQty;
            }

            public void setDeliveredQty(double deliveredQty) {
                this.deliveredQty = deliveredQty;
            }

            public double getPriceSubtotal() {
                return priceSubtotal;
            }

            public void setPriceSubtotal(double priceSubtotal) {
                this.priceSubtotal = priceSubtotal;
            }

            public int getProductID() {
                return productID;
            }

            public void setProductID(int productID) {
                this.productID = productID;
            }

            public double getTallyingAmount() {
                return tallyingAmount;
            }

            public void setTallyingAmount(double tallyingAmount) {
                this.tallyingAmount = tallyingAmount;
            }

            public int getSaleOrderProductID() {
                return saleOrderProductID;
            }

            public void setSaleOrderProductID(int saleOrderProductID) {
                this.saleOrderProductID = saleOrderProductID;
            }

            public String getStockType() {
                return stockType;
            }

            public void setStockType(String stockType) {
                this.stockType = stockType;
            }

            public double getSettleAmount() {
                return settleAmount;
            }

            public void setSettleAmount(double settleAmount) {
                this.settleAmount = settleAmount;
            }

            public double getProductUomQty() {
                return productUomQty;
            }

            public void setProductUomQty(double productUomQty) {
                this.productUomQty = productUomQty;
            }

            public List<String> getLotIDs() {
                return lotIDs;
            }

            public void setLotIDs(List<String> lotIDs) {
                this.lotIDs = lotIDs;
            }

            public List<LotListBean> getLotList() {
                return lotList;
            }

            public void setLotList(List<LotListBean> lotList) {
                this.lotList = lotList;
            }

            public static class LotListBean {
                /**
                 * lotPk : 79341
                 * lotID : 41
                 * name : Z201707051791
                 * qty : 3.0
                 */

                private String lotPk;
                private int lotID;
                private String name;
                private double qty;

                public String getLotPk() {
                    return lotPk;
                }

                public void setLotPk(String lotPk) {
                    this.lotPk = lotPk;
                }

                public int getLotID() {
                    return lotID;
                }

                public void setLotID(int lotID) {
                    this.lotID = lotID;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public double getQty() {
                    return qty;
                }

                public void setQty(double qty) {
                    this.qty = qty;
                }
            }
        }
    }
}
