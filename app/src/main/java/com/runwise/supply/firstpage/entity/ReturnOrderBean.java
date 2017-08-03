package com.runwise.supply.firstpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by libin on 2017/8/1.
 */

public class ReturnOrderBean{

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Parcelable{
        public ListBean() {
        }

        /**
         * orderID : 561
         * doneDate :
         * name : RSO247
         * isTwoUnit : false
         * doneDtate :
         * driver : null
         * createDate : 2017-07-31 18:15:19
         * createUser : 胡勇
         * lines : [{"productUom":"条","priceUnit":8,"tax":17,"discount":0,"deliveredQty":2,"priceSubtotal":16,"productID":13,"saleOrderProductID":310,"lotIDs":["42"],"pickupWeight":0,"pickupNum":0,"stockType":"lengcanghuo","lotList":[{"lotPk":"31042","lotID":42,"qty":3}],"productUomQty":2}]
         * amount : 2.0
         * amountTotal : 18.72
         * state : process
         * loadingDate : null
         * vehicle : null
         * isDispatch : false
         * returnOrderID : 247
         * driveMobile : null
         * stateTracker : ["2017-07-31 18:15 退货单退货中"]
         */

        private int orderID;
        private String doneDate;
        private String name;
        private boolean isTwoUnit;
        private String doneDtate;
        private Object driver;
        private String createDate;
        private String createUser;
        private double amount;
        private double amountTotal;
        private String state;
        private Object loadingDate;
        private Object vehicle;
        private boolean isDispatch;
        private int returnOrderID;
        private Object driveMobile;
        private List<LinesBean> lines;
        private List<String> stateTracker;

        protected ListBean(Parcel in) {
            orderID = in.readInt();
            doneDate = in.readString();
            name = in.readString();
            isTwoUnit = in.readByte() != 0;
            doneDtate = in.readString();
            createDate = in.readString();
            createUser = in.readString();
            amount = in.readDouble();
            amountTotal = in.readDouble();
            state = in.readString();
            isDispatch = in.readByte() != 0;
            returnOrderID = in.readInt();
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

        public Object getDriver() {
            return driver;
        }

        public void setDriver(Object driver) {
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

        public Object getLoadingDate() {
            return loadingDate;
        }

        public void setLoadingDate(Object loadingDate) {
            this.loadingDate = loadingDate;
        }

        public Object getVehicle() {
            return vehicle;
        }

        public void setVehicle(Object vehicle) {
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

        public Object getDriveMobile() {
            return driveMobile;
        }

        public void setDriveMobile(Object driveMobile) {
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
            dest.writeString(createDate);
            dest.writeString(createUser);
            dest.writeDouble(amount);
            dest.writeDouble(amountTotal);
            dest.writeString(state);
            dest.writeByte((byte) (isDispatch ? 1 : 0));
            dest.writeInt(returnOrderID);
            dest.writeTypedList(lines);
            dest.writeStringList(stateTracker);
        }

        public static class LinesBean implements Parcelable{
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
                dest.writeDouble(productUomQty);
                dest.writeStringList(lotIDs);
                dest.writeTypedList(lotList);
            }

            public static class LotListBean implements Parcelable{
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
