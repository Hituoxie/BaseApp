package com.lostliz.common.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static final String DEFAULT_DATE_REGEX = "yyyy-MM-dd HH:mm:ss";
	
	public static Date format(String dateStr){
		return format(dateStr, DEFAULT_DATE_REGEX);
	}
	
	public static Date format(String dateStr,String regex){
		SimpleDateFormat format = new SimpleDateFormat(regex);
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String formatStr(Date date){
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_REGEX);
		return format.format(date);
	}

}
