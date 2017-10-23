package com.kids.commonframe.base;

import android.os.Bundle;

import com.kids.commonframe.base.util.SystemUpgradeHelper;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.kids.commonframe.base.util.net.NetWorkHelper.NetWorkCallBack;

/**
 * 基类Fragment 要联网操作的Fragment继承该类
 */
public abstract class NetWorkFragment extends BaseFragment implements NetWorkCallBack<BaseEntity>{
	protected NetWorkHelper<BaseEntity> netWorkHelper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		netWorkHelper = new NetWorkHelper<BaseEntity>((BaseActivity)mContext, this);
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

	public void setBodyParams (String [] bodyKey, String [] bodyValue) {
		netWorkHelper.setBodyParams(bodyKey, bodyValue);
	}
	public void sendConnection(int method , String url, String[] argsKeys,
			String[] argsValues, int where, boolean showDialog ,Class<?> targerClass) {
		netWorkHelper.sendConnection(method, url, argsKeys, argsValues, where, showDialog, targerClass,null);
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
	public void sendConnection(String bizName,Object params, int where, boolean showDialog, Class<?> targerClass) {
		netWorkHelper.sendConnection(bizName, params , where, showDialog, targerClass);
	}

	// -------------------------需要重写或实现的方法---------------------
	//	/**
	//	 * 
	//	 * 可根据需求重写该方法，优先调用该方法解析
	//	 * 该方法在线程中调用不要作ui操作
	//	 * @param where
	//	 */
	@Override
	public BaseEntity onParse(int where, Class<?> targerClass, String result) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		netWorkHelper.onStopAllRequest();
	}
}
