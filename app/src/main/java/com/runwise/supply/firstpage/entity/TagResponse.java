package com.runwise.supply.firstpage.entity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mike on 2017/11/6.
 */

public class TagResponse {

    public HashMap<String, List<String>> getQuantity_tags() {
        return quantity_tags;
    }

    public void setQuantity_tags(HashMap<String, List<String>> quantity_tags) {
        this.quantity_tags = quantity_tags;
    }

    public HashMap<String, List<String>> getService_tags() {
        return service_tags;
    }

    public void setService_tags(HashMap<String, List<String>> service_tags) {
        this.service_tags = service_tags;
    }

    HashMap<String,List<String>> quantity_tags;
    HashMap<String,List<String>> service_tags;

    public List<String> getReturnOrderTags() {
        return returnOrderTags;
    }

    public void setReturnOrderTags(List<String> returnOrderTags) {
        this.returnOrderTags = returnOrderTags;
    }

    List<String> returnOrderTags;

}
