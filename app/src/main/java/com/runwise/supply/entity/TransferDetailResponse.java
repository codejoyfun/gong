package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 *  调拨单详情
 *
 * Created by Dong on 2017/10/12.
 */

public class TransferDetailResponse {
    private List<LinesBean> lines;
    private TransferEntity info;

    public void setLines(List<LinesBean> lines) {
        this.lines = lines;
    }

    public List<LinesBean> getLines() {
        return lines;
    }

    public TransferEntity getInfo() {
        return info;
    }

    public void setInfo(TransferEntity info) {
        this.info = info;
    }

    public static class LinesBean extends OrderResponse.ListBean.LinesBean implements Parcelable{
        public static final String TRACKING_LOT = "lot";
        private String productTracking;
        private List<TransferBatchLot> productLotInfo;

        public List<TransferBatchLot> getProductLotInfo() {
            return productLotInfo;
        }

        public void setProductLotInfo(List<TransferBatchLot> productLotInfo) {
            this.productLotInfo = productLotInfo;
        }

        public String getProductTracking() {
            return productTracking;
        }

        public void setProductTracking(String productTracking) {
            this.productTracking = productTracking;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeTypedList(this.productLotInfo);
            dest.writeString(productTracking);
        }

        public LinesBean() {
        }

        protected LinesBean(Parcel in) {
            super(in);
            this.productLotInfo = in.createTypedArrayList(TransferBatchLot.CREATOR);
            this.productTracking = in.readString();
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

        public boolean isLotTracking(){
            return TRACKING_LOT.equals(productTracking);
        }
    }
}
