package com.kids.commonframe.base.bean;

/**
 * Created by myChaoFile on 16/12/21.
 */

public class CheckVersionRequest {
    private String version_name;
    private String version_code;
    private String tag;

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
