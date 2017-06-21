package com.kids.commonframe.base;

import java.io.Serializable;

public class VersionUpdateResponse extends BaseEntity implements Serializable {

	private String id;//": "1",
	private String version_name;//": "Android",
	private String version_id;//": "1.0.6",
	private String description;//": "",
	private String updatetype;//": "0",
	private String url;//": "",
	private String createtime;//": "1467624028",
	private String cdate;//": "1970-01-01 08:00:00"

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public String getVersion_id() {
		return version_id;
	}

	public void setVersion_id(String version_id) {
		this.version_id = version_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpdatetype() {
		return updatetype;
	}

	public void setUpdatetype(String updatetype) {
		this.updatetype = updatetype;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getCdate() {
		return cdate;
	}

	public void setCdate(String cdate) {
		this.cdate = cdate;
	}
}
