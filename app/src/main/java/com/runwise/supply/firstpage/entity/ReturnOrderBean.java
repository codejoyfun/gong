package com.runwise.supply.firstpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.runwise.supply.message.entity.OrderMsgDetail;

import java.io.Serializable;
import java.util.List;

/**
 * Created by libin on 2017/8/1.
 */

public class ReturnOrderBean implements Serializable {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Parcelable, Serializable {
        public ListBean() {
        }

        /**
         * orderID : 561
         * doneDate :
         * name : RSO247
         * isTwoUnit : false
         * doneDtate :
         * driver : ""
         * createDate : 2017-07-31 18:15:19
         * createUser : 胡勇
         * lines : [{"productUom":"条","priceUnit":8,"tax":17,"discount":0,"deliveredQty":2,"priceSubtotal":16,"productID":13,"saleOrderProductID":310,"lotIDs":["42"],"pickupWeight":0,"pickupNum":0,"stockType":"lengcanghuo","lotList":[{"lotPk":"31042","lotID":42,"qty":3}],"productUomQty":2}]
         * amount : 2.0
         * amountTotal : 18.72
         * state : process
         * loadingDate : null
         * vehicle : ""
         * isDispatch : false
         * returnOrderID : 247
         * driveMobile : ""
         * stateTracker : ["2017-07-31 18:15 退货单退货中"]
         */

        private int orderID;
        private String doneDate;
        private String name;
        private boolean isTwoUnit;
        private String doneDtate;
        private String driver;
        private String createDate;
        private String createUser;
        private double amount;
        private double amountTotal;
        private String state;
        private String loadingDate;

        public WaybillBean getWaybill() {
            return waybill;
        }

        public void setWaybill(WaybillBean waybill) {
            this.waybill = waybill;
        }

        private WaybillBean waybill;
        private String vehicle;
        private boolean isDispatch;
        private int returnOrderID;
        private String driveMobile;
        private List<LinesBean> lines;
        private List<String> stateTracker;
        private boolean returnThirdPartLog;

        public boolean isReturnThirdPartLog() {
            return returnThirdPartLog;
        }

        public void setReturnThirdPartLog(boolean returnThirdPartLog) {
            this.returnThirdPartLog = returnThirdPartLog;
        }


        public String getDeliveryType() {
            return deliveryType;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }

        private String deliveryType;

        public int getHasAttachment() {
            return hasAttachment;
        }

        public void setHasAttachment(int hasAttachment) {
            this.hasAttachment = hasAttachment;
        }

        private int hasAttachment;

        public static class WaybillBean implements Serializable {
            /**
             * deliverUser : {"mobile":"15542154698","userID":292,"name":"黄飞","avatarUrl":""}
             * waybillID : 81
             * name : SP17082300005
             * deliverVehicle : {"licensePlate":"999999999999","name":"Audi/A4/999999999999","vehicleID":73}
             */

            private DeliverUserBean deliverUser;
            private int waybillID;
            private String name;
            private OrderMsgDetail.OrderBean.WaybillBean.DeliverVehicleBean deliverVehicle;

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

            public OrderMsgDetail.OrderBean.WaybillBean.DeliverVehicleBean getDeliverVehicle() {
                return deliverVehicle;
            }

            public void setDeliverVehicle(OrderMsgDetail.OrderBean.WaybillBean.DeliverVehicleBean deliverVehicle) {
                this.deliverVehicle = deliverVehicle;
            }

            public static class DeliverUserBean implements Serializable {
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
        }


        protected ListBean(Parcel in) {
            orderID = in.readInt();
            doneDate = in.readString();
            name = in.readString();
            isTwoUnit = in.readByte() != 0;
            doneDtate = in.readString();
            driver = in.readString();
            createDate = in.readString();
            createUser = in.readString();
            amount = in.readDouble();
            amountTotal = in.readDouble();
            state = in.readString();
            loadingDate = in.readString();
            vehicle = in.readString();
            isDispatch = in.readByte() != 0;
            returnOrderID = in.readInt();
            driveMobile = in.readString();
            deliveryType = in.readString();
            hasAttachment = in.readInt();
            lines = in.createTypedArrayList(LinesBean.CREATOR);
            stateTracker = in.createStringArrayList();
            returnThirdPartLog = in.readByte() != 0;
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

        public int getOrderID() {
            return orderID;
        }

        public void setOrderID(int orderID) {
            this.orderID = orderID;
        }

        public String getDoneDate() {
            return doneDate;
        }

        public void setDoneDate(String doneDate) {
            this.doneDate = doneDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isIsTwoUnit() {
            return isTwoUnit;
        }

        public void setIsTwoUnit(boolean isTwoUnit) {
            this.isTwoUnit = isTwoUnit;
        }

        public String getDoneDtate() {
            return doneDtate;
        }

        public void setDoneDtate(String doneDtate) {
            this.doneDtate = doneDtate;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getAmountTotal() {
            return amountTotal;
        }

        public void setAmountTotal(double amountTotal) {
            this.amountTotal = amountTotal;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getLoadingDate() {
            return loadingDate;
        }

        public void setLoadingDate(String loadingDate) {
            this.loadingDate = loadingDate;
        }

        public String getVehicle() {
            return vehicle;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public boolean isIsDispatch() {
            return isDispatch;
        }

        public void setIsDispatch(boolean isDispatch) {
            this.isDispatch = isDispatch;
        }

        public int getReturnOrderID() {
            return returnOrderID;
        }

        public void setReturnOrderID(int returnOrderID) {
            this.returnOrderID = returnOrderID;
        }

        public String getDriveMobile() {
            return driveMobile;
        }

        public void setDriveMobile(String driveMobile) {
            this.driveMobile = driveMobile;
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
            dest.writeInt(orderID);
            dest.writeString(doneDate);
            dest.writeString(name);
            dest.writeByte((byte) (isTwoUnit ? 1 : 0));
            dest.writeString(doneDtate);
            dest.writeString(driver);
            dest.writeString(createDate);
            dest.writeString(createUser);
            dest.writeDouble(amount);
            dest.writeDouble(amountTotal);
            dest.writeString(state);
            dest.writeString(loadingDate);
            dest.writeString(vehicle);
            dest.writeByte((byte) (isDispatch ? 1 : 0));
            dest.writeInt(returnOrderID);
            dest.writeString(driveMobile);
            dest.writeString(deliveryType);
            dest.writeInt(hasAttachment);
            dest.writeByte((byte)(returnThirdPartLog?1:0));
            dest.writeTypedList(lines);
            dest.writeStringList(stateTracker);
        }


        public static class LinesBean implements Parcelable, Serializable {
            public LinesBean() {
            }

            /**
             * productUom : 条
             * priceUnit : 8.0
             * tax : 17.0
             * discount : 0.0
             * deliveredQty : 2.0
             * priceSubtotal : 16.0
             * productID : 13
             * saleOrderProductID : 310
             * lotIDs : ["42"]
             * pickupWeight : 0.0
             * pickupNum : 0.0
             * stockType : lengcanghuo
             * lotList : [{"lotPk":"31042","lotID":42,"qty":3}]
             * productUomQty : 2.0
             */

            private String productUom;
            private double priceUnit;
            private double tax;
            private double discount;
            private double deliveredQty;
            private double priceSubtotal;
            private int productID;
            private int saleOrderProductID;
            private double pickupWeight;
            private double pickupNum;
            private String stockType;
            private String category;
            private double productUomQty;
            private List<String> lotIDs;
            private List<LotListBean> lotList;

            protected LinesBean(Parcel in) {
                productUom = in.readString();
                priceUnit = in.readDouble();
                tax = in.readDouble();
                discount = in.readDouble();
                deliveredQty = in.readDouble();
                priceSubtotal = in.readDouble();
                productID = in.readInt();
                saleOrderProductID = in.readInt();
                pickupWeight = in.readDouble();
                pickupNum = in.readDouble();
                stockType = in.readString();
                category = in.readString();
                productUomQty = in.readDouble();
                lotIDs = in.createStringArrayList();
                lotList = in.createTypedArrayList(LotListBean.CREATOR);
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


            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
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

            public double getTax() {
                return tax;
            }

            public void setTax(double tax) {
                this.tax = tax;
            }

            public double getDiscount() {
                return discount;
            }

            public void setDiscount(double discount) {
                this.discount = discount;
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

            public int getSaleOrderProductID() {
                return saleOrderProductID;
            }

            public void setSaleOrderProductID(int saleOrderProductID) {
                this.saleOrderProductID = saleOrderProductID;
            }

            public double getPickupWeight() {
                return pickupWeight;
            }

            public void setPickupWeight(double pickupWeight) {
                this.pickupWeight = pickupWeight;
            }

            public double getPickupNum() {
                return pickupNum;
            }

            public void setPickupNum(double pickupNum) {
                this.pickupNum = pickupNum;
            }

            public String getStockType() {
                return stockType;
            }

            public void setStockType(String stockType) {
                this.stockType = stockType;
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
                dest.writeDouble(tax);
                dest.writeDouble(discount);
                dest.writeDouble(deliveredQty);
                dest.writeDouble(priceSubtotal);
                dest.writeInt(productID);
                dest.writeInt(saleOrderProductID);
                dest.writeDouble(pickupWeight);
                dest.writeDouble(pickupNum);
                dest.writeString(stockType);
                dest.writeString(category);
                dest.writeDouble(productUomQty);
                dest.writeStringList(lotIDs);
                dest.writeTypedList(lotList);
            }

            public static class LotListBean implements Parcelable, Serializable {
                public LotListBean() {
                }

                /**
                 * lotPk : 31042
                 * lotID : 42
                 * qty : 3.0
                 */

                private String lotPk;
                private int lotID;
                private double qty;

                protected LotListBean(Parcel in) {
                    lotPk = in.readString();
                    lotID = in.readInt();
                    qty = in.readDouble();
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(lotPk);
                    dest.writeInt(lotID);
                    dest.writeDouble(qty);
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                public static final Creator<LotListBean> CREATOR = new Creator<LotListBean>() {
                    @Override
                    public LotListBean createFromParcel(Parcel in) {
                        return new LotListBean(in);
                    }

                    @Override
                    public LotListBean[] newArray(int size) {
                        return new LotListBean[size];
                    }
                };

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
