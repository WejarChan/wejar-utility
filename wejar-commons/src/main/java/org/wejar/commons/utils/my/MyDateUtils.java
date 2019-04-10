package org.wejar.commons.utils.my;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyDateUtils {

	public static String formatDate(Date date,String pattern){
		return new SimpleDateFormat(pattern).format(date);
	}
	
	public static Date parseDate(String str,String pattern) throws ParseException{
		SimpleDateFormat smf = new SimpleDateFormat(pattern);
		return smf.parse(str);
	}
	
	public static Date todayZero(){
		return new Date(System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset());
	}
	
	public static Date nextDateZero(){
		long current = System.currentTimeMillis();
		current = current /(1000*3600*24)*(1000*3600*24) ;
		long nextDate = current + (1000*3600*24);
		return new Date(nextDate);
	}
}
