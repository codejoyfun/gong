package com.runwise.supply.entity;

import java.util.List;

/**
 * Created by Dong on 2017/10/14.
 */

public class TransferStateResponse {
    private List<TransferStateEntity> stateList;

    public List<TransferStateEntity> getStateList() {
        return stateList;
    }

    public void setStateList(List<TransferStateEntity> stateList) {
        this.stateList = stateList;
    }
}
