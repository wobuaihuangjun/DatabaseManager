package com.huangzj.databaseupgrade.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间日期工具类
 *
 * Created by lhd on 2015/09/26.
 */
public class DateFormatUtil {

	public static final String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";

	public static final String FORMAT_2 = "yyyyMMdd";

	public static final String FORMAT_3 = "MM-dd";

	public static final String FORMAT_4 = "HH:mm:ss";

	public static String format() {
		return format(FORMAT_1);
	}

	public static String formatMouthAndDay(long ms){
		return format(FORMAT_3,ms);
	}

	public static String format(String format, Date date) {
		String formatDate = new SimpleDateFormat(format).format(date);
		return formatDate;
	}

	public static String format(String format, long ms) {
		return format(format, new Date(ms));
	}

	public static String format(String format) {
		return format(format, new Date());
	}

	/**
	 * 获取当天00:00时间
	 * @return 00:00的毫秒数
	 */
	public static long getTodayZeroTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime().getTime();
	}

	public static String transferLongToDate(String dateFormat, Long millSec){
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat(dateFormat);
		Date date=new Date(millSec);
		return  simpleDateFormat.format(date);
	}
}
