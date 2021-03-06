package com.kids.commonframe.base;

import android.os.Bundle;

import com.android.internal.http.multipart.Part;
import com.kids.commonframe.base.util.net.NetWorkHelper;

import java.util.List;
import java.util.Map;


/**
 * 带有网络操作的activity
 */
public abstract class NetWorkActivity extends BaseActivity implements NetWorkHelper.NetWorkCallBack<BaseEntity>{
	protected NetWorkHelper<BaseEntity> netWorkHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		netWorkHelper = new NetWorkHelper<BaseEntity>(this ,this);
	}
	/**
	 * 在发送请求之前调用改方法设置解析类型
	 * 设置解析类型为JSON
	 */
	public void setJsonParseType () {
		netWorkHelper.setJsonParseType();
	}
	/**
	* 在发送请求之前调用改方法设置解析类型
	* 设置解析类型为XML
	*/
	public void setXmlParseType () {
		netWorkHelper.setXmlParseType();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		setupData();
	}

	public void setBodyParams (String [] bodyKey, String [] bodyValue) {
		netWorkHelper.setBodyParams(bodyKey, bodyValue);
	}
	public  void sendConnection(int method , String url, String[] argsKeys,
			String[] argsValues, int where, boolean showDialog ,Class<BaseEntity> targerClass) {
		netWorkHelper.sendConnection(method, url, argsKeys, argsValues, where, showDialog, targerClass,null,false);
	}

	/**
	 * xml请求接口
     */
	public void sendConnection(String bizCode,String[] bodyKeys,String[] bodyValues, int where, boolean showDialog, Class<?> targerClass) {
//		netWorkHelper.sendConnection(bizCode, bodyKeys, bodyValues, where, showDialog, targerClass);
	}
	/**
	 * json请求接口
	 */
	public long sendConnection(String bizName,Object params, int where, boolean showDialog, Class<?> targerClass) {
		return netWorkHelper.sendConnection(bizName, params , where, showDialog, targerClass);
	}

	/**
	 * json请求接口
	 */
	public void sendConnection(String host,String bizName,Object params, int where, boolean showDialog, Class<?> targerClass, boolean useUnLoginDB) {
		netWorkHelper.sendConnection(host,bizName, params , where, showDialog, targerClass,useUnLoginDB);
	}

	public void sendConnection(String bizName, int where, boolean showDialog, Class<?> targerClass) {
		netWorkHelper.sendConnection(bizName,where, showDialog, targerClass);
	}

	public void sendConnection(String bizName, List<Part> partList, int where, boolean showDialog, Class<?> targerClass) {
		netWorkHelper.sendConnection(bizName,partList,where,showDialog,targerClass);
	}

	public void sendConnection(String bizName, Map<String,String> paramMap, int where, boolean showDialog, Class<?> targerClass) {
		netWorkHelper.sendConnection(bizName,paramMap,where,showDialog,targerClass);
	}

		/**
         * 可以在此方法中配置一些基本数据
         */
	protected  void setupData(){
	}
	@Override
	public BaseEntity onParse(int where, Class<?> targerClass, String result) {
		return null;
	}
	@Override
	protected void onStop() {
		super.onStop();
		netWorkHelper.onStopAllRequest();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
}
