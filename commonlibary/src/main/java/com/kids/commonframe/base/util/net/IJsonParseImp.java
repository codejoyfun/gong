package com.kids.commonframe.base.util.net;

import com.alibaba.fastjson.JSON;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.util.net.NetWorkHelper.PaseInterface;
import com.thoughtworks.xstream.XStream;

/**
 * 解析JSON文件
 * @param <T>
 * @param <T>
 */
public class IJsonParseImp<T extends BaseEntity> implements PaseInterface<T>{
	@Override
	public T paseResult(Class<?> targerClass, String resultStr) {
		return (T)JSON.parseObject(resultStr,targerClass);
	}
}
