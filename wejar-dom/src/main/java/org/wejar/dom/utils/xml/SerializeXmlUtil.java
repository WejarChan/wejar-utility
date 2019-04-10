package org.wejar.dom.utils.xml;

import java.io.Writer;
import java.lang.reflect.Field;

import org.wejar.dom.annotation.XStreamCDATA;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
 
public class SerializeXmlUtil {
	
	public static XStream createXstream() {
		return new XStream(new DomDriver() {
			@Override
			public HierarchicalStreamWriter createWriter(Writer out) {
				return new PrettyPrintWriter(out) {
					boolean cdata = false;
					Class<?> targetClass = null;

					@Override
					public void startNode(String name, @SuppressWarnings("rawtypes") Class clazz) {
						super.startNode(name, clazz);
						// 业务处理，对于用XStreamCDATA标记的Field，需要加上CDATA标签
						if (!name.equals("xml")) {// 代表当前处理节点是class，用XstreamAlias把class的别名改成xml，下面的代码片段有提到
							cdata = needCDATA(targetClass, name);
						} else {
							targetClass = clazz;
						}
					}

					@Override
					protected void writeText(QuickWriter writer, String text) {
						if (cdata) {
							writer.write(cDATA(text));
						} else {
							writer.write(text);
						}
					}

					private String cDATA(String text) {
						return "<![CDATA["+text+"]]>";
					}
				};
			}
		});
	}
 
	private static boolean needCDATA(Class<?> targetClass, String fieldAlias){
		boolean cdata = false;
		//first, scan self
		cdata = existsCDATA(targetClass, fieldAlias);
		if(cdata) return cdata;
		//if cdata is false, scan supperClass until java.lang.Object
		Class<?> superClass = targetClass.getSuperclass();
		while(!superClass.equals(Object.class)){
			cdata = existsCDATA(superClass, fieldAlias);
			if(cdata) return cdata;
			superClass = superClass.getClass().getSuperclass();
		}
		return false;
	}
	
	private static boolean existsCDATA(Class<?> clazz, String fieldAlias){
		//scan fields
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			//1. exists XStreamCDATA
			if(field.getAnnotation(XStreamCDATA.class) != null ){
				XStreamAlias xStreamAlias = field.getAnnotation(XStreamAlias.class);
				//2. exists XStreamAlias
				if(null != xStreamAlias){
					if(fieldAlias.equals(xStreamAlias.value()))//matched
						return true;
				}else{// not exists XStreamAlias
					if(fieldAlias.equals(field.getName()))
						return true;
				}
			}
		}
		return false;
	}
	
}