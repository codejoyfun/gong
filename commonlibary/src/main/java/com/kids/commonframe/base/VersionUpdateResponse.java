package com.kids.commonframe.base;

import java.io.Serializable;

public class VersionUpdateResponse extends BaseEntity implements Serializable {

	private String description;//": "",
	private String url;//": "",
	private boolean isMandatory;
	private String versionName;


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setMandatory(boolean mandatory) {
		isMandatory = mandatory;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
}
