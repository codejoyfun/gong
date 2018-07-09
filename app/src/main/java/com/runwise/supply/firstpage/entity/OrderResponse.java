package com.runwise.supply.firstpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.runwise.supply.entity.TransferEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * 由于首页需要混合显示SO单和调拨单，所以接口的列表返回两种对象，这里继承TransferEntity用于获取调拨单
     */
    public static class ListBean extends TransferEntity implements Parcelable, Serializable {
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
         * returnOrders:["164","163"]
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
        private boolean unApplyService;
        private StoreBean store;
        //退货记录里加的字段，貌似收货人
        private String driver;
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
        private List<String> returnOrders;
        private boolean isNewType;//订单信息是否包含所有的商品信息
        private boolean canAlter;
        private boolean isAsyncOrder;
        private String orderUserIDs;
        private String receiveError;//是否收货失败
        private boolean isActual;
        private List<ProductAlteredBean> productAltered;
        private boolean isActualSendOrder;
        private int typeQty;
        private String time;
        private String firstLineName;
        private int linesAmount;
        private String remark;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getLinesAmount() {
            return linesAmount;
        }

        public void setLinesAmount(int linesAmount) {
            this.linesAmount = linesAmount;
        }



        public String getFirstLineName() {
            return firstLineName;
        }

        public void setFirstLineName(String firstLineName) {
            this.firstLineName = firstLineName;
        }



        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getTypeQty() {
            return typeQty;
        }

        public void setTypeQty(int typeQty) {
            this.typeQty = typeQty;
        }
        public List<ProductAlteredBean> getProductAltered() {
            return productAltered;
        }

        public void setProductAltered(List<ProductAlteredBean> productAltered) {
            this.productAltered = productAltered;
        }


        public boolean isActual() {
            return isActual;
        }

        public void setIsActual(boolean actual) {
            isActual = actual;
        }
        public boolean isActualSendOrder() {
            return isActualSendOrder;
        }

        public void setIsActualSendOrder(boolean actualSendOrder) {
            isActualSendOrder = actualSendOrder;
        }

        public static final String TYPE_STANDARD = "standard";// 标准订单
        public static final String TYPE_VENDOR_DELIVERY = "vendor_delivery";// 直运订单
        public static final String TYPE_THIRD_PART_DELIVERY = "third_part_delivery";// 第三方物流订单
        public static final String TYPE_FRESH = "fresh";//鲜活订单
        public static final String TYPE_FRESH_VENDOR_DELIVERY = "fresh_vendor_delivery";// 鲜活直运订单
        public static final String TYPE_FRESH_THIRD_PART_DELIVERY = "fresh_third_part_delivery";// 鲜活第三方物流订单
        public static final String TYPE_TRANSFER_IN = "transferIn";//调入单
        public static final String TYPE_TRANSFER_OUT = "transferOut";//调出单

        public String getDeliveryType() {
            return deliveryType;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }

        private String deliveryType;

        public ListBean() {
        }

        protected ListBean(Parcel in) {
            super(in);
            amountTotal = in.readDouble();
            endUnloadDatetime = in.readString();
            driver = in.readString();
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
            unApplyService = in.readByte() != 0;
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
            deliveryType = in.readString();
            lines = in.createTypedArrayList(LinesBean.CREATOR);
            stateTracker = in.createStringArrayList();
            returnOrders = in.createStringArrayList();
            isNewType = in.readByte() != 0;
            canAlter = in.readByte() != 0;
            isAsyncOrder = in.readByte() != 0;
            orderUserIDs = in.readString();
            receiveError = in.readString();
            isActual = in.readByte() != 0;
            productAltered = in.createTypedArrayList(ProductAlteredBean.CREATOR);
            isActualSendOrder = in.readByte() != 0;
            typeQty = in.readInt();
            time = in.readString();

            firstLineName = in.readString();
            linesAmount = in.readInt();
            remark = in.readString();
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

        public boolean isAsyncOrder() {
            return isAsyncOrder;
        }

        public String getOrderUserIDs() {
            return orderUserIDs;
        }

        public void setIsAsyncOrder(boolean asyncOrder) {
            isAsyncOrder = asyncOrder;
        }

        public void setOrderUserIDs(String orderUserIDs) {
            this.orderUserIDs = orderUserIDs;
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

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
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

        public List<String> getReturnOrders() {
            return returnOrders;
        }

        public void setReturnOrders(List<String> returnOrders) {
            this.returnOrders = returnOrders;
        }

        public String getReceiveError() {
            return receiveError;
        }

        public void setReceiveError(String receiveError) {
            this.receiveError = receiveError;
        }

        /**
         * 是否是调拨单
         *
         * @return
         */
        public boolean isTransfer() {
            return getPickingName() != null && getPickingName().trim().length() > 0;
        }

        /**
         * 记录已经读过这条订单的用户
         */
        private List<String> readUsers;

        private void initReadUsers() {
            if (readUsers == null) {
                readUsers = new ArrayList<>();
                if (orderUserIDs != null) readUsers.addAll(Arrays.asList(orderUserIDs.split(",")));
            }
        }

        /**
         * 判断用户是否已经读了这条订单
         *
         * @param userId
         * @return
         */
        public boolean isUserRead(String userId) {
            initReadUsers();
            return readUsers.contains(userId);
        }

        public void setUserRead(String userId) {
            initReadUsers();
            readUsers.add(userId);
        }

        public boolean isUnApplyService() {
            return unApplyService;
        }

        public void setUnApplyService(boolean unApplyService) {
            this.unApplyService = unApplyService;
        }

        public boolean isCanAlter() {
            return canAlter;
        }

        public void setCanAlter(boolean canAlter) {
            this.canAlter = canAlter;
        }

        public boolean isNewType() {
            return isNewType;
        }

        public void setIsNewType(boolean newType) {
            isNewType = newType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeDouble(amountTotal);
            dest.writeString(driver);
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
            dest.writeByte((byte) (unApplyService ? 1 : 0));
            dest.writeDouble(settleAmountTotal);
            dest.writeParcelable(waybill, flags);
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
            dest.writeString(deliveryType);
            dest.writeTypedList(lines);
            dest.writeStringList(stateTracker);
            dest.writeStringList(returnOrders);
            dest.writeByte((byte) (isNewType ? 1 : 0));
            dest.writeByte((byte) (canAlter ? 1 : 0));
            dest.writeByte((byte) (isAsyncOrder ? 1 : 0));
            dest.writeString(orderUserIDs);
            dest.writeString(receiveError);
            dest.writeByte((byte) (isActual ? 1 : 0));
            dest.writeTypedList(productAltered);
            dest.writeByte((byte) (isActualSendOrder ? 1 : 0));
            dest.writeInt(typeQty);
            dest.writeString(time);

            dest.writeString(firstLineName);
            dest.writeInt(linesAmount);
            dest.writeString(remark);
        }



        public static class ProductAlteredBean implements Parcelable {
            String alterDate;
            String alterUserName;
            List<AlterProductBean> alterProducts;

            public ProductAlteredBean() {

            }

            protected ProductAlteredBean(Parcel in) {
                alterDate = in.readString();
                alterUserName = in.readString();
                alterProducts = in.createTypedArrayList(AlterProductBean.CREATOR);
            }

            public static final Creator<ProductAlteredBean> CREATOR = new Creator<ProductAlteredBean>() {
                @Override
                public ProductAlteredBean createFromParcel(Parcel in) {
                    return new ProductAlteredBean(in);
                }

                @Override
                public ProductAlteredBean[] newArray(int size) {
                    return new ProductAlteredBean[size];
                }
            };

            public String getAlterDate() {
                return alterDate;
            }

            public void setAlterDate(String alterDate) {
                this.alterDate = alterDate;
            }

            public String getAlterUserName() {
                return alterUserName;
            }

            public void setAlterUserName(String alterUserName) {
                this.alterUserName = alterUserName;
            }

            public List<AlterProductBean> getAlterProducts() {
                return alterProducts;
            }

            public void setAlterProducts(List<AlterProductBean> alterProducts) {
                this.alterProducts = alterProducts;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(alterDate);
                dest.writeString(alterUserName);
                dest.writeTypedList(alterProducts);
            }

            public static class AlterProductBean implements Parcelable,Serializable {
                String name;
                String defaultCode;
                String unit;
                String uom;
                double originNum;
                double alterNum;
                String imageMedium;
                double price;

                public AlterProductBean() {

                }

                protected AlterProductBean(Parcel in) {
                    name = in.readString();
                    defaultCode = in.readString();
                    unit = in.readString();

                    uom = in.readString();
                    originNum = in.readDouble();
                    alterNum = in.readDouble();

                    imageMedium = in.readString();
                    price = in.readDouble();
                }

                public static final Creator<AlterProductBean> CREATOR = new Creator<AlterProductBean>() {
                    @Override
                    public AlterProductBean createFromParcel(Parcel in) {
                        return new AlterProductBean(in);
                    }

                    @Override
                    public AlterProductBean[] newArray(int size) {
                        return new AlterProductBean[size];
                    }
                };
                public double getPrice() {
                    return price;
                }

                public void setPrice(double price) {
                    this.price = price;
                }

                public String getImageMedium() {
                    return imageMedium;
                }

                public void setImageMedium(String imageMedium) {
                    this.imageMedium = imageMedium;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getDefaultCode() {
                    return defaultCode;
                }

                public void setDefaultCode(String defaultCode) {
                    this.defaultCode = defaultCode;
                }

                public String getUnit() {
                    return unit;
                }

                public void setUnit(String unit) {
                    this.unit = unit;
                }

                public String getUom() {
                    return uom;
                }

                public void setUom(String uom) {
                    this.uom = uom;
                }

                public double getOriginNum() {
                    return originNum;
                }

                public void setOriginNum(double originNum) {
                    this.originNum = originNum;
                }

                public double getAlterNum() {
                    return alterNum;
                }

                public void setAlterNum(double alterNum) {
                    this.alterNum = alterNum;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(name);
                    dest.writeString(defaultCode);
                    dest.writeString(unit);

                    dest.writeString(uom);
                    dest.writeDouble(originNum);
                    dest.writeDouble(alterNum);

                    dest.writeString(imageMedium);
                    dest.writeDouble(price);
                }
            }

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

        public static class WaybillBean implements Parcelable {
            /**
             * deliverUser : {"mobile":"15778177356","userID":30,"name":"李明","avatarUrl":"/gongfu/user/avatar/30/6691999026166866162.png"}
             * waybillID : 188
             * name : PS20170714188
             * deliverVehicle : {"licensePlate":"沪A 0409D","name":"江淮汽车/机型重卡/沪A 0409D","vehicleID":10}
             */

            private DeliverUserBean deliverUser;
            private String waybillID;
            private String name;
            private DeliverVehicleBean deliverVehicle;

            public WaybillBean() {
            }

            protected WaybillBean(Parcel in) {
                deliverUser = in.readParcelable(DeliverUserBean.class.getClassLoader());
                waybillID = in.readString();
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

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeParcelable(deliverUser, flags);
                dest.writeString(waybillID);
                dest.writeString(name);
                dest.writeParcelable(deliverVehicle, flags);
            }

            public static class DeliverUserBean implements Parcelable {
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

            public static class DeliverVehicleBean implements Parcelable {
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

        /**
         * 订单的商品信息
         * 如果订单是isNewType,则包含详细的下单时保存的商品信息
         * 否则则需要按商品ID查当前的商品信息
         */
        public static class LinesBean implements Parcelable, Serializable {
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
            private String category;
            private double settleAmount;
            private double productUomQty;
            private List<String> lotIDs;
            private List<LotListBean> lotList;
            //详细商品信息
            private String barcode;
            private String defaultCode;
            private String imageMedium;
            private boolean isTwoUnit;
            private String name;
            private double productPrice;
            private double productSettlePrice;
            private int settleUomId;
            private String tracking;
            private String unit;
            private int unloadAmount;
            private String remark;//备注
            private double actualSendNum;//实际发货数量
            private String saleUom;//实际发货数量
            private String categoryParent;
            private String categoryChild;
            private String description;


            //自定义字段
            private boolean isChanged;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public boolean isChanged() {
                return isChanged;
            }

            public void setChanged(boolean changed) {
                isChanged = changed;
            }

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

            public String getBarcode() {
                return barcode;
            }

            public String getDefaultCode() {
                return defaultCode;
            }

            public String getImageMedium() {
                return imageMedium;
            }

            public boolean isTwoUnit() {
                return isTwoUnit;
            }

            public String getName() {
                return name;
            }

            public double getProductPrice() {
                return productPrice;
            }

            public double getProductSettlePrice() {
                return productSettlePrice;
            }

            public int getSettleUomId() {
                return settleUomId;
            }

            public String getUnit() {
                return unit;
            }

            public int getUnloadAmount() {
                return unloadAmount;
            }

            public void setBarcode(String barcode) {
                this.barcode = barcode;
            }

            public void setDefaultCode(String defaultCode) {
                this.defaultCode = defaultCode;
            }

            public void setImageMedium(String imageMedium) {
                this.imageMedium = imageMedium;
            }

            public void setIsTwoUnit(boolean twoUnit) {
                isTwoUnit = twoUnit;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setProductPrice(double productPrice) {
                this.productPrice = productPrice;
            }

            public void setProductSettlePrice(double productSettlePrice) {
                this.productSettlePrice = productSettlePrice;
            }

            public void setSettleUomId(int settleUomId) {
                this.settleUomId = settleUomId;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public void setUnloadAmount(int unloadAmount) {
                this.unloadAmount = unloadAmount;
            }

            public String getTracking() {
                return tracking;
            }

            public void setTwoUnit(boolean twoUnit) {
                isTwoUnit = twoUnit;
            }

            public void setTracking(String tracking) {
                this.tracking = tracking;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public double getActualSendNum() {
                return actualSendNum;
            }

            public void setActualSendNum(double actualSendNum) {
                this.actualSendNum = actualSendNum;
            }
            public String getSaleUom() {
                return saleUom;
            }

            public void setSaleUom(String saleUom) {
                this.saleUom = saleUom;
            }
            public String getCategoryParent() {
                return categoryParent;
            }

            public void setCategoryParent(String categoryParent) {
                this.categoryParent = categoryParent;
            }

            public String getCategoryChild() {
                return categoryChild;
            }

            public void setCategoryChild(String categoryChild) {
                this.categoryChild = categoryChild;
            }

            public static class LotListBean implements Parcelable {
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
                private double height;
                private String produce_datetime;
                private String life_datetime;

                public String getProduce_datetime() {
                    return produce_datetime;
                }

                public void setProduce_datetime(String produce_datetime) {
                    this.produce_datetime = produce_datetime;
                }

                public String getLife_datetime() {
                    return life_datetime;
                }

                public void setLife_datetime(String life_datetime) {
                    this.life_datetime = life_datetime;
                }

                public double getHeight() {
                    return height;
                }

                public void setHeight(double height) {
                    this.height = height;
                }


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

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.lotPk);
                    dest.writeInt(this.lotID);
                    dest.writeString(this.name);
                    dest.writeDouble(this.qty);
                    dest.writeDouble(this.height);
                    dest.writeString(this.produce_datetime);
                    dest.writeString(this.life_datetime);
                }

                public LotListBean() {
                }

                protected LotListBean(Parcel in) {
                    this.lotPk = in.readString();
                    this.lotID = in.readInt();
                    this.name = in.readString();
                    this.qty = in.readDouble();
                    this.height = in.readDouble();
                    this.produce_datetime = in.readString();
                    this.life_datetime = in.readString();
                }

                public static final Creator<LotListBean> CREATOR = new Creator<LotListBean>() {
                    @Override
                    public LotListBean createFromParcel(Parcel source) {
                        return new LotListBean(source);
                    }

                    @Override
                    public LotListBean[] newArray(int size) {
                        return new LotListBean[size];
                    }
                };
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.productUom);
                dest.writeDouble(this.priceUnit);
                dest.writeDouble(this.discount);
                dest.writeDouble(this.returnAmount);
                dest.writeDouble(this.deliveredQty);
                dest.writeDouble(this.priceSubtotal);
                dest.writeInt(this.productID);
                dest.writeDouble(this.tallyingAmount);
                dest.writeInt(this.saleOrderProductID);
                dest.writeString(this.stockType);
                dest.writeString(this.category);
                dest.writeDouble(this.settleAmount);
                dest.writeDouble(this.productUomQty);
                dest.writeStringList(this.lotIDs);
                dest.writeTypedList(this.lotList);
                dest.writeByte(this.isChanged ? (byte) 1 : (byte) 0);

                dest.writeString(barcode);
                dest.writeString(defaultCode);
                dest.writeString(imageMedium);
                dest.writeByte((byte) (isTwoUnit ? 1 : 0));
                dest.writeString(name);
                dest.writeDouble(productPrice);
                dest.writeDouble(productSettlePrice);
                dest.writeInt(settleUomId);
                dest.writeString(tracking);
                dest.writeString(unit);
                dest.writeInt(unloadAmount);
                dest.writeString(remark);
                dest.writeDouble(actualSendNum);
                dest.writeString(saleUom);
                dest.writeString(categoryParent);
                dest.writeString(categoryChild);

            }

            public LinesBean() {
            }

            protected LinesBean(Parcel in) {
                this.productUom = in.readString();
                this.priceUnit = in.readDouble();
                this.discount = in.readDouble();
                this.returnAmount = in.readDouble();
                this.deliveredQty = in.readDouble();
                this.priceSubtotal = in.readDouble();
                this.productID = in.readInt();
                this.tallyingAmount = in.readDouble();
                this.saleOrderProductID = in.readInt();
                this.stockType = in.readString();
                this.category = in.readString();
                this.settleAmount = in.readDouble();
                this.productUomQty = in.readDouble();
                this.lotIDs = in.createStringArrayList();
                this.lotList = in.createTypedArrayList(LotListBean.CREATOR);
                this.isChanged = in.readByte() != 0;

                this.barcode = in.readString();
                defaultCode = in.readString();
                imageMedium = in.readString();
                isTwoUnit = in.readByte() != 0;
                name = in.readString();
                productPrice = in.readDouble();
                productSettlePrice = in.readDouble();
                settleUomId = in.readInt();
                tracking = in.readString();
                unit = in.readString();
                unloadAmount = in.readInt();
                remark = in.readString();
                actualSendNum = in.readDouble();
                saleUom = in.readString();
                categoryParent = in.readString();
                categoryChild = in.readString();
            }

            public static final Creator<LinesBean> CREATOR = new Creator<LinesBean>() {
                @Override
                public LinesBean createFromParcel(Parcel source) {
                    return new LinesBean(source);
                }

                @Override
                public LinesBean[] newArray(int size) {
                    return new LinesBean[size];
                }
            };
        }
    }
}
