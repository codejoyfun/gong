package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateRequest {

    /**
     * service_evaluation :
     * tags : []
     * service_score : 5
     * quality_evaluation :
     */

    private String service_evaluation;
    private int service_score;
    private String quality_evaluation;
    private List<String> tags;

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
