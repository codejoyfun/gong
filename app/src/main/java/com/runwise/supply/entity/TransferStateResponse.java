package com.runwise.supply.entity;

import java.util.List;

/**
 * Created by Dong on 2017/10/14.
 */

public class TransferStateResponse {
    private List<TransferStateEntitiy> stateList;

    public List<TransferStateEntitiy> getStateList() {
        return stateList;
    }

    public void setStateList(List<TransferStateEntitiy> stateList) {
        this.stateList = stateList;
    }
}
