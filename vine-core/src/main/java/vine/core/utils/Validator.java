package vine.core.utils;

import java.util.Map;

/**
 * Validator：字符串验证工具类
 * @author Liyuan
 */

public class Validator {
	
	/**
	 * isEmpty
	 * @param s
	 * @return 字符串为空返回true
	 */
	public static boolean isEmpty(String s){
		return s == null || "".equals(s) ? true : false;
	}
	
	/**
	 * isEmpty
	 * @param sa
	 * @return 字符串数组中有空字符串返回true
	 */
	public static boolean isEmpty(String... sa){
		if(sa == null) return true;
		for(String s : sa){
			if(isEmpty(s)) return true;
		}
		return false;
	}
	
	/**
	 * isEmpty
	 * @param args
	 * @return 对象数组中有空对象返回true
	 */
	public static boolean isEmpty(Object... args){
		if(args == null) return true;
		for(Object o : args){
			if(o == null) return true;
		}
		return false;
	}
	
	/**
	 * isEmpty
	 * @param map
	 * @return map有元素值为null，则返回true
	 */
	public static boolean isEmpty(Map<Object, Object> map){
		if(map == null) return true;
		for(Object o : map.values()){
			if(o == null) return true;
		}
		return false;
	}
	
	/**
	 * checkField
	 * @param s
	 * @param length
	 * @return 字符串位数等于length返回true
	 */
	public static boolean checkField(String s, int length) {
		if(isEmpty(s)) return false;
		return s.length() == length ? true : false;
	}
	
	/**
	 * checkField
	 * @param s
	 * @param minLength
	 * @param maxLength
	 * @return 字符串位数在指定区间之内返回true
	 */
	public static boolean checkField(String s, int minLength, int maxLength) {
		if(isEmpty(s)) return false;
		return s.length() < minLength || s.length() > maxLength ? false : true;
	}
	
	/**
	 * checkField
	 * @param minLength
	 * @param maxLength
	 * @param ss
	 * @return 字符串数组中，所有字符串都在指定区间之内返回true
	 */
	public static boolean checkField(int minLength, int maxLength, String... ss){
		if(ss == null || ss.length == 0) return false;
		for(String s: ss){
			if(s.length() < minLength || s.length() > maxLength) {
				return false;
			}
		}
		return true;
	}
	
	//check else
	/**
	 * checkEquals
	 * @param s1
	 * @param s2
	 * @return 检查两个字符串是否相等
	 */
	public static boolean checkEquals(String s1, String s2) {
		if(isEmpty(s1) || isEmpty(s2)) return false;
		return s1.equals(s2) ? true : false;
	}
	
	/**
	 * checkNumber
	 * @param s
	 * @return 字符串所有字符都是数字返回true
	 */
	public static boolean checkNumber(String s) {
		if(isEmpty(s)) return false;
		if( s.matches("[0-9]+") ) {
			return true;
		}
		return false;
	}
	
	/**
	 * checkIn
	 * @param number
	 * @param args
	 * @return
	 */
	public static boolean checkIn(int number, int... args){
		if(args == null || args.length ==  0) return false;
		for(int i : args){
			if(number == i) return true;
		}
		return false;
	}
	
	/**
	 * checkString
	 * @param s
	 * @return 字符串是字母、数字或其组合返回true
	 */
	public static boolean checkString(String s) {
		if(isEmpty(s)) return false;
		if( s.matches("[a-zA-Z0-9_]+") ) {
			return true;
		}
		return false;
	}
	
	/**
	 * checkShortDate
	 * @param date
	 * @return 字符串是短日期格式返回true
	 */
	public static boolean checkShortDate(String date){
		if(isEmpty(date)) return false;
		if (date.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) {
			return true;
		}
		return false;
	}
	
	/**
	 * checkLongDate
	 * @param date
	 * @return 字符串是长日期格式返回true
	 */
	public static boolean checkLongDate(String date){
		if(isEmpty(date)) return false;
		if (date.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2} [0-9]{2}:[0-9]{2}:[0-9]{2}")) {
			return true;
		}
		return false;
	}
	
	/**
	 * checkIDCard
	 * @param s
	 * @return 身份证格式有效返回true
	 */
	public static boolean checkIDCard(String s){
		if(isEmpty(s)) return false;
		if( s.matches("[0-9]{15}") || s.matches("[0-9]{18}") || s.matches("[0-9]{17}[a-zA-Z]")) {
			return true;
		}
		return false;
	}
	
	/**
	 * checkEmail
	 * @param s
	 * @return  邮件格式有效返回true
	 */
	public static boolean checkEmail(String s) {
		if(isEmpty(s)) return false;
		if( s.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*") ){
			return true;
		}
		return false;
	}
	
	 /**
     * 获取字符串的长度：如果有中文，则每个中文字符计为2位
     * @param str 指定的字符串
     * @return 字符串长度
     */
    public static int getChineseLength(String str) {
    	if(str == null) return 0;
        int length = 0;
        String chinese = "[\u0391-\uFFE5]";
        String temp = null;
        for (int i = 0; i < str.length(); i++) {
            temp = str.substring(i, i + 1); //获取一个字符
            length += temp.matches(chinese) ? 2 : 1; //中文字符长度2,英文字符长度1
        }
        return length;
    }
	
}
