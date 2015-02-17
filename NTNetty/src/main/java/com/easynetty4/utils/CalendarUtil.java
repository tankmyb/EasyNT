package com.easynetty4.utils;

import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarUtil {
	/**
	 * 日期转换成日历
	 * 
	 * @param date
	 * @return
	 */
	public static GregorianCalendar getCalendar(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}
}
