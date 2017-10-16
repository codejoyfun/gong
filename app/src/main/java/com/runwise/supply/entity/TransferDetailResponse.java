package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
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
        private List<TransferBatchLot> productInfo;

        public List<TransferBatchLot> getProductInfo() {
            return productInfo;
        }

        public void setProductInfo(List<TransferBatchLot> productInfo) {
            this.productInfo = productInfo;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeTypedList(this.productInfo);
        }

        public LinesBean() {
        }

        protected LinesBean(Parcel in) {
            super(in);
            this.productInfo = in.createTypedArrayList(TransferBatchLot.CREATOR);
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
