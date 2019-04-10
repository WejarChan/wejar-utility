package org.wejar.net.http.utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private Properties properties = new Properties();
	private String fileName;
	
	public static PropertiesUtil newInstance(String fileName){
		return new PropertiesUtil(fileName);
	}
	
	public PropertiesUtil(String fileName) {
		this.fileName = fileName;
		reload();
	}
	
	public String getValue(String key) {
		String value = properties.getProperty(key);
		return value == null ? value : value.trim();
	}
	
	public String getValue(String key, String defaultValue) {
		String value = properties.getProperty(key, defaultValue);
		return value == null ? value : value.trim();
	}
	
	public void reload(){
		try {
			// key.properties
//			InputStream is = ClassLoader.getSystemResourceAsStream(fileName); 
			// need /key.properties
			if(fileName != null && fileName.length() > 0){
				if(!fileName.startsWith("/")){
					fileName = "/"+fileName;
				}
				InputStream is = PropertiesUtil.class.getResourceAsStream(fileName);
				properties.load(is);
			}
		} catch (Exception e) {
			logger.error("Properties file load failed, file name is ："+fileName);
		}
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Set<Object> keySet(){
		return this.properties.keySet();
	}

}
