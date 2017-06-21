package com.kids.commonframe.base.bean;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.VersionUpdateResponse;

/**
 * Created by myChaoFile on 16/12/7.
 */

public class CheckVersionResult extends BaseEntity{
    private VersionUpdateResponse data;

    public VersionUpdateResponse getData() {
        return data;
    }

    public void setData(VersionUpdateResponse data) {
        this.data = data;
    }
}
