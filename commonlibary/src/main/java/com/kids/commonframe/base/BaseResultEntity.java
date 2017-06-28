package com.kids.commonframe.base;

/**
 * Created by myChaoFile on 2017/6/27.
 */

public class BaseResultEntity {
    private String state;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
