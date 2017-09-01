package com.runwise.supply.message.entity;

import java.util.List;

/**
 * Created by myChaoFile on 2017/8/26.
 */

public class OrderMsgDetail {


    /**
     * order : {"isPaid":false,"stateTracker":["2017-08-25 11:20 订单已收货","2017-08-23 20:58 订单已发货","2017-08-23 20:25 订单已确认","2017-08-23 20:24 订单已提交"],"settleAmountTotal":0,"waybill":{"deliverUser":{"mobile":"15542154698","userID":292,"name":"黄飞","avatarUrl":""},"waybillID":81,"name":"SP17082300005","deliverVehicle":{"licensePlate":"999999999999","name":"Audi/A4/999999999999","vehicleID":73}},"hasAttachment":1,"deliveryType":"fresh","isFinishTallying":false,"isThirdPartLog":false,"amountTotal":50,"createUserName":"卢宝","hasReturn":0,"endUnloadDatetime":"2017-08-23 20:58:43","publicAmountTotal":0,"isDoubleReceive":false,"deliveredQty":5,"appraisalUserName":"","thirdPartLog":null,"estimatedDate":"2017-08-24","isTwoUnit":true,"orderID":723,"confirmationDate":"2017-08-23 20:25:28","loadingTime":"2017-08-24 00:00:00","name":"SO17082300015","estimatedTime":"2017-08-24 10:00:00","amount":5,"createDate":"2017-08-23 20:24:03","lines":[{"productUom":"条","unloadAmount":5,"priceUnit":10,"discount":0,"returnAmount":0,"deliveredQty":5,"priceSubtotal":50,"productID":42,"tallyingAmount":0,"saleOrderProductID":953,"lotIDs":[],"stockType":"donghuo","settleAmount":0,"lotList":[],"productUomQty":5}],"startUnloadDatetime":"2017-08-23 20:58:41","returnOrders":[],"state":"done","orderSettleName":"单次结算","isToday":false,"tallyingUserName":"","receiveUserName":"文天寻","doneDatetime":"2017-08-25 11:20:25","store":{"mobile":"13456324531","partner":"卢宝","partnerID":378,"name":"【老班长】华农店","address":"华南农业大学花山区07号"}}
     */

    private OrderBean order;
    private OrderBean returnOrder;

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public OrderBean getReturnOrder() {
        return returnOrder;
    }

    public void setReturnOrder(OrderBean returnOrder) {
        this.returnOrder = returnOrder;
    }

    public static class OrderBean {
        /**
         * isPaid : false
         * stateTracker : ["2017-08-25 11:20 订单已收货","2017-08-23 20:58 订单已发货","2017-08-23 20:25 订单已确认","2017-08-23 20:24 订单已提交"]
         * settleAmountTotal : 0
         * waybill : {"deliverUser":{"mobile":"15542154698","userID":292,"name":"黄飞","avatarUrl":""},"waybillID":81,"name":"SP17082300005","deliverVehicle":{"licensePlate":"999999999999","name":"Audi/A4/999999999999","vehicleID":73}}
         * hasAttachment : 1
         * deliveryType : fresh
         * isFinishTallying : false
         * isThirdPartLog : false
         * amountTotal : 50
         * createUserName : 卢宝
         * hasReturn : 0
         * endUnloadDatetime : 2017-08-23 20:58:43
         * publicAmountTotal : 0
         * isDoubleReceive : false
         * deliveredQty : 5
         * appraisalUserName :
         * thirdPartLog : null
         * estimatedDate : 2017-08-24
         * isTwoUnit : true
         * orderID : 723
         * confirmationDate : 2017-08-23 20:25:28
         * loadingTime : 2017-08-24 00:00:00
         * name : SO17082300015
         * estimatedTime : 2017-08-24 10:00:00
         * amount : 5
         * createDate : 2017-08-23 20:24:03
         * lines : [{"productUom":"条","unloadAmount":5,"priceUnit":10,"discount":0,"returnAmount":0,"deliveredQty":5,"priceSubtotal":50,"productID":42,"tallyingAmount":0,"saleOrderProductID":953,"lotIDs":[],"stockType":"donghuo","settleAmount":0,"lotList":[],"productUomQty":5}]
         * startUnloadDatetime : 2017-08-23 20:58:41
         * returnOrders : []
         * state : done
         * orderSettleName : 单次结算
         * isToday : false
         * tallyingUserName :
         * receiveUserName : 文天寻
         * doneDatetime : 2017-08-25 11:20:25
         * store : {"mobile":"13456324531","partner":"卢宝","partnerID":378,"name":"【老班长】华农店","address":"华南农业大学花山区07号"}
         */

        private boolean isPaid;
        private int settleAmountTotal;
        private WaybillBean waybill;
        private int hasAttachment;
        private String deliveryType;
        private boolean isFinishTallying;
        private boolean isThirdPartLog;
        private int amountTotal;
        private String createUserName;
        private int hasReturn;
        private String endUnloadDatetime;
        private int publicAmountTotal;
        private boolean isDoubleReceive;
        private int deliveredQty;
        private String appraisalUserName;
        private Object thirdPartLog;
        private String estimatedDate;
        private boolean isTwoUnit;
        private int orderID;
        private String confirmationDate;
        private String loadingTime;
        private String name;
        private String estimatedTime;
        private int amount;
        private String createDate;
        private String startUnloadDatetime;
        private String state;
        private String orderSettleName;
        private boolean isToday;
        private String tallyingUserName;
        private String receiveUserName;
        private String doneDatetime;
        private StoreBean store;
        private List<String> stateTracker;
        private List<LinesBean> lines;
        private List<?> returnOrders;

        public boolean isIsPaid() {
            return isPaid;
        }

        public void setIsPaid(boolean isPaid) {
            this.isPaid = isPaid;
        }

        public int getSettleAmountTotal() {
            return settleAmountTotal;
        }

        public void setSettleAmountTotal(int settleAmountTotal) {
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

        public String getDeliveryType() {
            return deliveryType;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }

        public boolean isIsFinishTallying() {
            return isFinishTallying;
        }

        public void setIsFinishTallying(boolean isFinishTallying) {
            this.isFinishTallying = isFinishTallying;
        }

        public boolean isIsThirdPartLog() {
            return isThirdPartLog;
        }

        public void setIsThirdPartLog(boolean isThirdPartLog) {
            this.isThirdPartLog = isThirdPartLog;
        }

        public int getAmountTotal() {
            return amountTotal;
        }

        public void setAmountTotal(int amountTotal) {
            this.amountTotal = amountTotal;
        }

        public String getCreateUserName() {
            return createUserName;
        }

        public void setCreateUserName(String createUserName) {
            this.createUserName = createUserName;
        }

        public int getHasReturn() {
            return hasReturn;
        }

        public void setHasReturn(int hasReturn) {
            this.hasReturn = hasReturn;
        }

        public String getEndUnloadDatetime() {
            return endUnloadDatetime;
        }

        public void setEndUnloadDatetime(String endUnloadDatetime) {
            this.endUnloadDatetime = endUnloadDatetime;
        }

        public int getPublicAmountTotal() {
            return publicAmountTotal;
        }

        public void setPublicAmountTotal(int publicAmountTotal) {
            this.publicAmountTotal = publicAmountTotal;
        }

        public boolean isIsDoubleReceive() {
            return isDoubleReceive;
        }

        public void setIsDoubleReceive(boolean isDoubleReceive) {
            this.isDoubleReceive = isDoubleReceive;
        }

        public int getDeliveredQty() {
            return deliveredQty;
        }

        public void setDeliveredQty(int deliveredQty) {
            this.deliveredQty = deliveredQty;
        }

        public String getAppraisalUserName() {
            return appraisalUserName;
        }

        public void setAppraisalUserName(String appraisalUserName) {
            this.appraisalUserName = appraisalUserName;
        }

        public Object getThirdPartLog() {
            return thirdPartLog;
        }

        public void setThirdPartLog(Object thirdPartLog) {
            this.thirdPartLog = thirdPartLog;
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

        public int getOrderID() {
            return orderID;
        }

        public void setOrderID(int orderID) {
            this.orderID = orderID;
        }

        public String getConfirmationDate() {
            return confirmationDate;
        }

        public void setConfirmationDate(String confirmationDate) {
            this.confirmationDate = confirmationDate;
        }

        public String getLoadingTime() {
            return loadingTime;
        }

        public void setLoadingTime(String loadingTime) {
            this.loadingTime = loadingTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEstimatedTime() {
            return estimatedTime;
        }

        public void setEstimatedTime(String estimatedTime) {
            this.estimatedTime = estimatedTime;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
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

        public String getOrderSettleName() {
            return orderSettleName;
        }

        public void setOrderSettleName(String orderSettleName) {
            this.orderSettleName = orderSettleName;
        }

        public boolean isIsToday() {
            return isToday;
        }

        public void setIsToday(boolean isToday) {
            this.isToday = isToday;
        }

        public String getTallyingUserName() {
            return tallyingUserName;
        }

        public void setTallyingUserName(String tallyingUserName) {
            this.tallyingUserName = tallyingUserName;
        }

        public String getReceiveUserName() {
            return receiveUserName;
        }

        public void setReceiveUserName(String receiveUserName) {
            this.receiveUserName = receiveUserName;
        }

        public String getDoneDatetime() {
            return doneDatetime;
        }

        public void setDoneDatetime(String doneDatetime) {
            this.doneDatetime = doneDatetime;
        }

        public StoreBean getStore() {
            return store;
        }

        public void setStore(StoreBean store) {
            this.store = store;
        }

        public List<String> getStateTracker() {
            return stateTracker;
        }

        public void setStateTracker(List<String> stateTracker) {
            this.stateTracker = stateTracker;
        }

        public List<LinesBean> getLines() {
            return lines;
        }

        public void setLines(List<LinesBean> lines) {
            this.lines = lines;
        }

        public List<?> getReturnOrders() {
            return returnOrders;
        }

        public void setReturnOrders(List<?> returnOrders) {
            this.returnOrders = returnOrders;
        }

        public static class WaybillBean {
            /**
             * deliverUser : {"mobile":"15542154698","userID":292,"name":"黄飞","avatarUrl":""}
             * waybillID : 81
             * name : SP17082300005
             * deliverVehicle : {"licensePlate":"999999999999","name":"Audi/A4/999999999999","vehicleID":73}
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
                 * mobile : 15542154698
                 * userID : 292
                 * name : 黄飞
                 * avatarUrl :
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
                 * licensePlate : 999999999999
                 * name : Audi/A4/999999999999
                 * vehicleID : 73
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

        public static class StoreBean {
            /**
             * mobile : 13456324531
             * partner : 卢宝
             * partnerID : 378
             * name : 【老班长】华农店
             * address : 华南农业大学花山区07号
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

        public static class LinesBean {
            /**
             * productUom : 条
             * unloadAmount : 5
             * priceUnit : 10
             * discount : 0
             * returnAmount : 0
             * deliveredQty : 5
             * priceSubtotal : 50
             * productID : 42
             * tallyingAmount : 0
             * saleOrderProductID : 953
             * lotIDs : []
             * stockType : donghuo
             * settleAmount : 0
             * lotList : []
             * productUomQty : 5
             */

            private String productUom;
            private int unloadAmount;
            private int priceUnit;
            private int discount;
            private int returnAmount;
            private int deliveredQty;
            private int priceSubtotal;
            private int productID;
            private int tallyingAmount;
            private int saleOrderProductID;
            private String stockType;
            private int settleAmount;
            private int productUomQty;
            private List<?> lotIDs;
            private List<?> lotList;

            public String getProductUom() {
                return productUom;
            }

            public void setProductUom(String productUom) {
                this.productUom = productUom;
            }

            public int getUnloadAmount() {
                return unloadAmount;
            }

            public void setUnloadAmount(int unloadAmount) {
                this.unloadAmount = unloadAmount;
            }

            public int getPriceUnit() {
                return priceUnit;
            }

            public void setPriceUnit(int priceUnit) {
                this.priceUnit = priceUnit;
            }

            public int getDiscount() {
                return discount;
            }

            public void setDiscount(int discount) {
                this.discount = discount;
            }

            public int getReturnAmount() {
                return returnAmount;
            }

            public void setReturnAmount(int returnAmount) {
                this.returnAmount = returnAmount;
            }

            public int getDeliveredQty() {
                return deliveredQty;
            }

            public void setDeliveredQty(int deliveredQty) {
                this.deliveredQty = deliveredQty;
            }

            public int getPriceSubtotal() {
                return priceSubtotal;
            }

            public void setPriceSubtotal(int priceSubtotal) {
                this.priceSubtotal = priceSubtotal;
            }

            public int getProductID() {
                return productID;
            }

            public void setProductID(int productID) {
                this.productID = productID;
            }

            public int getTallyingAmount() {
                return tallyingAmount;
            }

            public void setTallyingAmount(int tallyingAmount) {
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

            public int getSettleAmount() {
                return settleAmount;
            }

            public void setSettleAmount(int settleAmount) {
                this.settleAmount = settleAmount;
            }

            public int getProductUomQty() {
                return productUomQty;
            }

            public void setProductUomQty(int productUomQty) {
                this.productUomQty = productUomQty;
            }

            public List<?> getLotIDs() {
                return lotIDs;
            }

            public void setLotIDs(List<?> lotIDs) {
                this.lotIDs = lotIDs;
            }

            public List<?> getLotList() {
                return lotList;
            }

            public void setLotList(List<?> lotList) {
                this.lotList = lotList;
            }
        }
    }
}
