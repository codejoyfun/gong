package com.kids.commonframe.base;
/**
 * 没一个实体类都要继承该类
 */
public class BaseEntity {
	private String err_code;//": 0,
	private String msg;//": "success"

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
