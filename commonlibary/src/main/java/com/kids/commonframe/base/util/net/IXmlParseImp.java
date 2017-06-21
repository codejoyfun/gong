package com.kids.commonframe.base.util.net;

import com.kids.commonframe.base.BaseEntity;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.kids.commonframe.base.util.net.NetWorkHelper.PaseInterface;

/**
 * 解析xml文件
 * @param <T>
 * @param <T>
 */
public class IXmlParseImp<T extends BaseEntity> implements PaseInterface<T>{
	private XStream xStream;
	@Override
	public T paseResult(Class<?> targerClass, String resultStr) {
		xStream = new XStream(new Dom4JDriver());
		xStream.autodetectAnnotations(true);
		xStream.ignoreUnknownElements();
		xStream.alias("Response", targerClass);
		return  (T) xStream.fromXML(resultStr);
	}
}
