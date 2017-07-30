package com.runwise.supply.firstpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderResponse {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Parcelable{
        /**
         * lines : [{"productUom":"条","priceUnit":8,"discount":0,"returnAmount":0,
         * "deliveredQty":5,"priceSubtotal":40,"productID":13,"tallyingAmount":0,
         * "saleOrderProductID":822,"lotIDs":["42"],"stockType":"lengcanghuo",
         * "settleAmount":5,"lotList":[{"lotPk":"82242","lotID":42,
         * "name":"Z201707051792","qty":5}],"productUomQty":5}]
         * amountTotal : 40.0
         * endUnloadDatetime : 2017-07-14 11:13:16
         * estimatedDate : 2017-07-15
         * isTwoUnit : false
         * hasReturn : 0
         * loadingTime : 2017-07-15 07:10:00
         * estimatedTime : 2017-07-15 07:00:00
         * createDate : 2017-07-14 11:10:14
         * startUnloadDatetime : 2017-07-14 11:13:15
         * state : done
         * receiveUserName : 陆鸣
         * tallyingUserName :
         * isDoubleReceive : false
         * store : {"mobile":"13829781371","partner":"陆鸣","partnerID":30,"name":"【我家酸菜鱼】北京东路店","address":"上海市徐汇区北京东路403号首层"}
         * stateTracker : ["2017-07-14 11:14 订单已收货","2017-07-14 11:13 订单已发货","2017-07-14 11:10 订单已确认","2017-07-14 11:10 订单已提交"]
         * settleAmountTotal : 0.0
         * waybill : {"deliverUser":{"mobile":"15778177356","userID":30,"name":"李明","avatarUrl":"/gongfu/user/avatar/30/6691999026166866162.png"},"waybillID":188,"name":"PS20170714188","deliverVehicle":{"licensePlate":"沪A 0409D","name":"江淮汽车/机型重卡/沪A 0409D","vehicleID":10}}
         * hasAttachment : 0
         * isFinishTallying : false
         * createUserName : 陆鸣
         * orderSettleName : 每周结算
         * publicAmountTotal : 46.800000000000004
         * deliveredQty : 5.0
         * confirmationDate : 2017-07-14 11:10:38
         * orderID : 422
         * name : SO422
         * appraisalUserName :
         * amount : 5.0
         * isToday : false
         * doneDatetime : 2017-07-14 11:14:02
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
        private List<LinesBean> lines;
        private List<String> stateTracker;

        public ListBean() {
        }

        protected ListBean(Parcel in) {
            amountTotal = in.readDouble();
            endUnloadDatetime = in.readString();
            estimatedDate = in.readString();
            isTwoUnit = in.readByte() != 0;
            hasReturn = in.readInt();
            loadingTime = in.readString();
            estimatedTime = in.readString();
            createDate = in.readString();
            startUnloadDatetime = in.readString();
            state = in.readString();
            receiveUserName = in.readString();
            tallyingUserName = in.readString();
            isDoubleReceive = in.readByte() != 0;
            settleAmountTotal = in.readDouble();
            waybill = in.readParcelable(WaybillBean.class.getClassLoader());
            hasAttachment = in.readInt();
            isFinishTallying = in.readByte() != 0;
            createUserName = in.readString();
            orderSettleName = in.readString();
            publicAmountTotal = in.readDouble();
            deliveredQty = in.readDouble();
            confirmationDate = in.readString();
            orderID = in.readInt();
            name = in.readString();
            appraisalUserName = in.readString();
            amount = in.readDouble();
            isToday = in.readByte() != 0;
            doneDatetime = in.readString();
            lines = in.createTypedArrayList(LinesBean.CREATOR);
            stateTracker = in.createStringArrayList();
        }

        public static final Creator<ListBean> CREATOR = new Creator<ListBean>() {
            @Override
            public ListBean createFromParcel(Parcel in) {
                return new ListBean(in);
            }

            @Override
            public ListBean[] newArray(int size) {
                return new ListBean[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(amountTotal);
            dest.writeString(endUnloadDatetime);
            dest.writeString(estimatedDate);
            dest.writeByte((byte) (isTwoUnit ? 1 : 0));
            dest.writeInt(hasReturn);
            dest.writeString(loadingTime);
            dest.writeString(estimatedTime);
            dest.writeString(createDate);
            dest.writeString(startUnloadDatetime);
            dest.writeString(state);
            dest.writeString(receiveUserName);
            dest.writeString(tallyingUserName);
            dest.writeByte((byte) (isDoubleReceive ? 1 : 0));
            dest.writeDouble(settleAmountTotal);
            dest.writeParcelable(waybill,flags);
            dest.writeInt(hasAttachment);
            dest.writeByte((byte) (isFinishTallying ? 1 : 0));
            dest.writeString(createUserName);
            dest.writeString(orderSettleName);
            dest.writeDouble(publicAmountTotal);
            dest.writeDouble(deliveredQty);
            dest.writeString(confirmationDate);
            dest.writeInt(orderID);
            dest.writeString(name);
            dest.writeString(appraisalUserName);
            dest.writeDouble(amount);
            dest.writeByte((byte) (isToday ? 1 : 0));
            dest.writeString(doneDatetime);
            dest.writeTypedList(lines);
            dest.writeStringList(stateTracker);
        }

        public static class StoreBean {
            /**
             * mobile : 13829781371
             * partner : 陆鸣
             * partnerID : 30
             * name : 【我家酸菜鱼】北京东路店
             * address : 上海市徐汇区北京东路403号首层
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

        public static class WaybillBean implements Parcelable{
            /**
             * deliverUser : {"mobile":"15778177356","userID":30,"name":"李明","avatarUrl":"/gongfu/user/avatar/30/6691999026166866162.png"}
             * waybillID : 188
             * name : PS20170714188
             * deliverVehicle : {"licensePlate":"沪A 0409D","name":"江淮汽车/机型重卡/沪A 0409D","vehicleID":10}
             */

            private DeliverUserBean deliverUser;
            private int waybillID;
            private String name;
            private DeliverVehicleBean deliverVehicle;

            public WaybillBean() {
            }

            protected WaybillBean(Parcel in) {
                deliverUser = in.readParcelable(DeliverUserBean.class.getClassLoader());
                waybillID = in.readInt();
                name = in.readString();
                deliverVehicle = in.readParcelable(DeliverVehicleBean.class.getClassLoader());
            }

            public static final Creator<WaybillBean> CREATOR = new Creator<WaybillBean>() {
                @Override
                public WaybillBean createFromParcel(Parcel in) {
                    return new WaybillBean(in);
                }

                @Override
                public WaybillBean[] newArray(int size) {
                    return new WaybillBean[size];
                }
            };

            public DeliverUserBean getDeliverUser() {
                return deliverUser;
            }

            public void setDeliverUser(DeliverUserBean deliverUser) {
                this.deliverUser = deliverUser;
            }

            public int getWaybillID() {
                return waybillID;
            }

            public void setWaybillID(int waybillID) {
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

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeParcelable(deliverUser,flags);
                dest.writeInt(waybillID);
                dest.writeString(name);
                dest.writeParcelable(deliverVehicle,flags);
            }

            public static class DeliverUserBean implements Parcelable{
                /**
                 * mobile : 15778177356
                 * userID : 30
                 * name : 李明
                 * avatarUrl : /gongfu/user/avatar/30/6691999026166866162.png
                 */

                private String mobile;
                private int userID;
                private String name;
                private String avatarUrl;

                public DeliverUserBean() {
                }

                protected DeliverUserBean(Parcel in) {
                    mobile = in.readString();
                    userID = in.readInt();
                    name = in.readString();
                    avatarUrl = in.readString();
                }

                public static final Creator<DeliverUserBean> CREATOR = new Creator<DeliverUserBean>() {
                    @Override
                    public DeliverUserBean createFromParcel(Parcel in) {
                        return new DeliverUserBean(in);
                    }

                    @Override
                    public DeliverUserBean[] newArray(int size) {
                        return new DeliverUserBean[size];
                    }
                };

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

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(mobile);
                    dest.writeInt(userID);
                    dest.writeString(name);
                    dest.writeString(avatarUrl);
                }
            }

            public static class DeliverVehicleBean implements  Parcelable{
                /**
                 * licensePlate : 沪A 0409D
                 * name : 江淮汽车/机型重卡/沪A 0409D
                 * vehicleID : 10
                 */

                private String licensePlate;
                private String name;
                private int vehicleID;

                public DeliverVehicleBean() {
                }

                protected DeliverVehicleBean(Parcel in) {
                    licensePlate = in.readString();
                    name = in.readString();
                    vehicleID = in.readInt();
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(licensePlate);
                    dest.writeString(name);
                    dest.writeInt(vehicleID);
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                public static final Creator<DeliverVehicleBean> CREATOR = new Creator<DeliverVehicleBean>() {
                    @Override
                    public DeliverVehicleBean createFromParcel(Parcel in) {
                        return new DeliverVehicleBean(in);
                    }

                    @Override
                    public DeliverVehicleBean[] newArray(int size) {
                        return new DeliverVehicleBean[size];
                    }
                };

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

        public static class LinesBean implements Parcelable{
            /**
             * productUom : 条
             * priceUnit : 8.0
             * discount : 0.0
             * returnAmount : 0.0
             * deliveredQty : 5.0
             * priceSubtotal : 40.0
             * productID : 13
             * tallyingAmount : 0.0
             * saleOrderProductID : 822
             * lotIDs : ["42"]
             * stockType : lengcanghuo
             * settleAmount : 5.0
             * lotList : [{"lotPk":"82242","lotID":42,"name":"Z201707051792","qty":5}]
             * productUomQty : 5.0
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
            public LinesBean(){}
            protected LinesBean(Parcel in) {
                productUom = in.readString();
                priceUnit = in.readDouble();
                discount = in.readDouble();
                returnAmount = in.readDouble();
                deliveredQty = in.readDouble();
                priceSubtotal = in.readDouble();
                productID = in.readInt();
                tallyingAmount = in.readDouble();
                saleOrderProductID = in.readInt();
                stockType = in.readString();
                settleAmount = in.readDouble();
                productUomQty = in.readDouble();
                lotIDs = in.createStringArrayList();
            }

            public static final Creator<LinesBean> CREATOR = new Creator<LinesBean>() {
                @Override
                public LinesBean createFromParcel(Parcel in) {
                    return new LinesBean(in);
                }

                @Override
                public LinesBean[] newArray(int size) {
                    return new LinesBean[size];
                }
            };

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

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(productUom);
                dest.writeDouble(priceUnit);
                dest.writeDouble(discount);
                dest.writeDouble(returnAmount);
                dest.writeDouble(deliveredQty);
                dest.writeDouble(priceSubtotal);
                dest.writeInt(productID);
                dest.writeDouble(tallyingAmount);
                dest.writeInt(saleOrderProductID);
                dest.writeString(stockType);
                dest.writeDouble(settleAmount);
                dest.writeDouble(productUomQty);
                dest.writeStringList(lotIDs);
            }

            public static class LotListBean {
                /**
                 * lotPk : 82242
                 * lotID : 42
                 * name : Z201707051792
                 * qty : 5.0
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
