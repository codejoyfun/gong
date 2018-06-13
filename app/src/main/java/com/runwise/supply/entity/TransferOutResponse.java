package com.runwise.supply.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2018/6/13.
 */

public class TransferOutResponse implements Serializable{

    List<TransferOut> list;

    public List<TransferOut> getList() {
        return list;
    }

    public void setList(List<TransferOut> list) {
        this.list = list;
    }

public class TransferOut implements Serializable{
    String pickingName;
    int pickingID;
    String dateExpected;
    String creatUID;

    public String getPickingName() {
        return pickingName;
    }

    public void setPickingName(String pickingName) {
        this.pickingName = pickingName;
    }

    public int getPickingID() {
        return pickingID;
    }

    public void setPickingID(int pickingID) {
        this.pickingID = pickingID;
    }

    public String getDateExpected() {
        return dateExpected;
    }

    public void setDateExpected(String dateExpected) {
        this.dateExpected = dateExpected;
    }

    public String getCreatUID() {
        return creatUID;
    }

    public void setCreatUID(String creatUID) {
        this.creatUID = creatUID;
    }
}
}
