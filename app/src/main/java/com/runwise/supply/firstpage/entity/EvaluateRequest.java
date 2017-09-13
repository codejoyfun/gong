package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateRequest {
    /**
     * service_evaluation : 未确认收货就离开，迟到且无提前告知
     * service_tags : ["迟到且无提前告知","未确认收货就离开"]
     * service_score : 3
     * quality_evaluation : 好的
     */
    private String service_evaluation;
    private int service_score;
    private String quality_evaluation;
    private List<String> service_tags;
    private List<String> quality_tags;

    public List<String> getQuality_tags() {
        return quality_tags;
    }

    public void setQuality_tags(List<String> quality_tags) {
        this.quality_tags = quality_tags;
    }

    public String getService_evaluation() {
        return service_evaluation;
    }

    public void setService_evaluation(String service_evaluation) {
        this.service_evaluation = service_evaluation;
    }

    public int getService_score() {
        return service_score;
    }

    public void setService_score(int service_score) {
        this.service_score = service_score;
    }

    public String getQuality_evaluation() {
        return quality_evaluation;
    }

    public void setQuality_evaluation(String quality_evaluation) {
        this.quality_evaluation = quality_evaluation;
    }

    public List<String> getService_tags() {
        return service_tags;
    }

    public void setService_tags(List<String> service_tags) {
        this.service_tags = service_tags;
    }
}
