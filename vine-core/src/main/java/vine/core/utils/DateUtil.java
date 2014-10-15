package vine.core.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;

public class DateUtil {
	/** 默认日期格式字符串 */
	public static final String TIME_FORMAT_DATE = "yyyy-MM-dd";
	public static final String TIME_FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final SimpleDateFormat TIME_FORMAT_DATE_FORMATER = new SimpleDateFormat(TIME_FORMAT_DATE);
	public static final SimpleDateFormat TIME_FORMAT_DATE_TIME_FORMATER = new SimpleDateFormat(TIME_FORMAT_DATE_TIME);
	
	/**
	 * formatTime: 将当前日期转换为默认格式的字符串
	 * @return
	 */
	public static String formatTime() {
		return TIME_FORMAT_DATE_TIME_FORMATER.format(System.currentTimeMillis());
	}
	
	/**
	 * formatTime：将指定日期转换为默认格式的字符串
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		return TIME_FORMAT_DATE_TIME_FORMATER.format(time);
	}
	
	/**
	 * formatTime: 将指定日期转换为指定格式的字符串
	 * @param time
	 * @param formatString yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String formatTime(long time, String formatString) {
		return new SimpleDateFormat(formatString).format(time);
	}
	
	/**
	 * formatDate：将日期字符串转换为日期
	 * @param timeString
	 * @return
	 */
	public static Date formatDate(String timeString) {
		if (timeString == null || "".equals(timeString))
			return null;
		try {
			if (timeString.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) {
				return TIME_FORMAT_DATE_FORMATER.parse(timeString);
			} else if(timeString.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2} [0-9]{2}:[0-9]{2}:[0-9]{2}")){
				return TIME_FORMAT_DATE_TIME_FORMATER.parse(timeString);
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * getNewDate：获取当前时间之前或之后的时间
	 * @param time 之后(正整数) 或 之前(负整数)的时间, 单位可以是天、小时、分钟、秒
	 * @param mark 0-天, 1-时, 2-分, 3-秒
	 * @return
	 */
	public static Date getNewDate(int time, int mark){
		Calendar cal = Calendar.getInstance();
		int second = 0;
		switch(mark){
			case 0: //天
				second = time * 24 * 60 * 60;
				break;
			case 1: //小时
				second = time * 60 * 60;
				break;
			case 2: //分钟
				second = time * 60;
				break;	
			case 3: //秒
				second = time;
				break;	
			default:
				return null;
		}
		cal.add(Calendar.SECOND, second);
		return new Date(cal.getTimeInMillis());
	}
	
	/**
	 * 返回零点的时间
	 * @return 单位：毫秒
	 */
	public static Date getZeroTime(){
		String time = formatTime(System.currentTimeMillis(), TIME_FORMAT_DATE);
		return formatDate(time);
	}
	
	/**
	 * 返回零点的时间
	 * @param time
	 * @return 单位：毫秒
	 */
	public static Date getZeroTime(String time){
		String zeroTime = formatTime(formatDate(time).getTime(), TIME_FORMAT_DATE);
		return formatDate(zeroTime);
	}
	/**
	 * 返回零点的时间
	 * @param offset 偏移量 0-当天  1 下一天  -1 前一天
	 * @return
	 */
	public static Date getZeroTime(int offset){
		Date date = getZeroTime();
		return DateUtils.addDays(date, offset);
	}
	
	/**
	 * getNewDate：获取当前时间之前或之后的时间
	 * @param time 之后(正整数) 或 之前(负整数)的时间, 单位可以是天、小时、分钟、秒
	 * @param mark 0-天, 1-时, 2-分, 3-秒
	 * @return
	 */
	public static String getNewDateToString(int time, int mark){
		Calendar cal = Calendar.getInstance();
		int second = 0;
		switch(mark){
			case 0: //天
				second = time * 24 * 60 * 60;
				break;
			case 1: //小时
				second = time * 60 * 60;
				break;
			case 2: //分钟
				second = time * 60;
				break;	
			case 3: //秒
				second = time;
				break;	
			default:
				return null;
		}
		cal.add(Calendar.SECOND, second);
		Date date = new Date(cal.getTimeInMillis());
		return DateUtil.formatTime(date.getTime());
	}
	
	/**
	 * 获取时间
	 * @param sign 0-无 1-年 2-月 3-日 4-时 5-分 6-秒 7-星期
	 * @return
	 */
	public static int getTime(int sign){
		return getTime(sign, new Date());
	}
	/**
	 * 获取时间
	 * @param sign 0-无 1-年 2-月 3-日 4-时 5-分 6-秒 7-星期
	 * @return
	 */
	public static int getTime(int sign, Date date){
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(date.getTime());
		switch(sign){
			case 1:
				return c.get(Calendar.YEAR);
			case 2:
				return c.get(Calendar.MONTH) + 1;
			case 3:
				return c.get(Calendar.DATE);
			case 4:
				return c.get(Calendar.HOUR_OF_DAY);
			case 5:
				return c.get(Calendar.MINUTE);
			case 6:
				return c.get(Calendar.SECOND);
			case 7:
				return c.get(Calendar.DAY_OF_WEEK) - 1;
			default:
				return 0;
		}
	}
	/**
	 * 获取当前时间的偏移时间<br>
	 * 例:获取明天此时此刻的时间，field=3 amount=1
	 * @param field 0-无 1-年 2-月 3-日 4-时 5-分 6-秒 7-星期
	 * @return
	 */
	public static Date specifyDate(int field, int amount){
		return specifyDate(field, amount, new Date());
	}
	/**
	 * 获取当前时间的偏移时间<br>
	 * 例1:获取明天此时此刻的时间，field=3 amount=1<br>
	 * 例2:获取昨天此时此刻的时间，field=3 amount=-1<br>
	 * @param field 0-无 1-年 2-月 3-日 4-时 5-分 6-秒 7-星期
	 * @return
	 */
	public static Date specifyDate(int field, int amount, Date date){
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(date.getTime());
		switch(field){
			case 1:	c.add(Calendar.YEAR, amount);			break;
			case 2:	c.add(Calendar.MONTH, amount);			break;
			case 3:	c.add(Calendar.DATE, amount);			break;
			case 4:	c.add(Calendar.HOUR_OF_DAY, amount);	break;
			case 5:	c.add(Calendar.MINUTE, amount);		break;
			case 6:	c.add(Calendar.SECOND, amount);		break;
			case 7:	c.add(Calendar.WEEK_OF_YEAR, amount);	break;
			default:;
		}
		return c.getTime();
	}
	
	/**
	 * 判断是否在同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameDay(long time1, long time2){
		return isSameDay(formatTime(time1), formatTime(time2));
	}
	/**
	 * 判断是否在同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameDay(String time1, String time2){
		time1 = time1.substring(0, 10);
		time2 = time2.substring(0, 10);
		if(time1.equals(time2)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 *  subtract: 计算两个时间的差值（单位毫秒）
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return
	 */
	public static long subtract(Date date1, Date date2){
		if(date1 == null || date2 == null) return 0;
		return date1.getTime() - date2.getTime();
	}
	
	/**
	 * subtract: 计算两个时间的差值（单位毫秒）
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static long subtract(String s1, String s2){
		Date date1 = formatDate(s1);
		Date date2 = formatDate(s2);
		if(date1 == null || date2 == null) return 0;
		return date1.getTime() - date2.getTime();
	}
}
