package vine.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串处理工具类
 * @author liguofang
 */
public class StringUtil {
	private static final Logger log = LoggerFactory.getLogger(StringUtil.class);
	/**
	 * 将字符串转换为指定的类型
	 * @param typeClass 要转换的目标类型
	 * @param value 字符串值
	 * @return
	 */
	public static <T> T cast(Class<T> typeClass, String value) {
		Object castValue = value;
		if ((value == null || value.equals("")) && typeClass != String.class) {
			value = "0";
		}
		if (typeClass == int.class || typeClass == Integer.class) {
			castValue = Integer.valueOf(value);
		} else if (typeClass == float.class || typeClass == Float.class) {
			castValue = Float.valueOf(value);
		} else if (typeClass == long.class || typeClass == Long.class) {
			castValue = Long.valueOf(value);
		} else if (typeClass == double.class || typeClass == Double.class) {
			castValue = Double.valueOf(value);
		}
		return (T) castValue;
	}
	
	public static String castString(Number number){
		return number.toString();
	}
	
	public static int castInt(String s){
		if(Validator.isEmpty(s) || Validator.isEmpty(s.trim())) return 0;
		s = s.trim();
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static long castLong(String s){
		if(Validator.isEmpty(s) || Validator.isEmpty(s.trim())) return 0;
		s = s.trim();
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static float castFloat(String s){
		if(Validator.isEmpty(s) || Validator.isEmpty(s.trim())) return 0;
		s = s.trim();
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static double castDouble(String s){
		if(Validator.isEmpty(s) || Validator.isEmpty(s.trim())) return 0;
		s = s.trim();
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int[] castIntegerArray(String s, String sign){
		try {
			if(Validator.isEmpty(s, sign)) return null;
			String[] ss = s.split(sign);
			if(ss == null || ss.length == 0) return null;
			int[] ia = new int[ss.length];
			for (int i = 0; i < ia.length; i++) {
				ia[i] = castInt(ss[i]);
			}
			return ia;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final String getUUID(){
		return UUID.randomUUID().toString();
	}
	
	public static final String getMD5(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			log.error("getMD5 error:",e);
			e.printStackTrace();
			return null;
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = messageDigest.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString().toUpperCase();
	}
	
	public static void getToStringCodes(Class<?> clazz){
		System.out.println("StringBuffer sb = new StringBuffer();");
		System.out.println("sb.append(super.toString())");
		Field[] fields = clazz.getDeclaredFields();
		Field field = null;
		String fieldName = null;
		int length = fields.length;
		for (int i = 0; i < length; i++) {
			field = fields[i];
			fieldName = field.getName();
			if(i == 0) {
				System.out.println(".append(\". " + fieldName +":\").append(" + fieldName + ")");
			} else {
				System.out.println(".append(\"," + fieldName +":\").append(" + fieldName + ")");
			}
		}
		System.out.println(";");
		System.out.println("return sb.toString();");
	}
	
	public static String toString(Object object) {
		StringBuffer sb = new StringBuffer();
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();
				sb.append(fieldName).append(":");
				String firstStr = fieldName.substring(0, 1).toUpperCase();
				fieldName = firstStr + fieldName.substring(1);
				Method method = object.getClass().getMethod("get" + fieldName );
				Object rs = method.invoke(object);
				sb.append(rs).append(",");
			}
		} catch (Exception e) {
			sb.append(e);
		}
		return sb.toString();
	}
	
	/**
	 * 将多个数据链接成一个字符串
	 * @return
	 */
	public static String chain(String[] args) {
		StringBuffer str = new StringBuffer();
		for (String arg : args) {
			str.append(arg);
		}
		return str.toString();
	}
	/**
	 * 将字节数组转换为String输出
	 * @return
	 */
	public static String bytes2HexStr(byte[] src){
		StringBuffer sb = new StringBuffer();
		String stmp ="";
		int i = 1;		
		for (byte arg : src) {		
			stmp = Integer.toHexString(arg & 0xFF);
			sb.append((stmp.length() == 1 )? "0"+stmp:stmp); 
			if((i%10) != 0) {
				sb.append(" ");
			} else {
				sb.append("\n");
			}
			i++;
		}
		return sb.toString().toUpperCase().trim();
	}
	/**
	 * byte转换为int
	 * @param b
	 * @return
	 */
	public static int byteArrayToInt(byte[] b) {		
	       int value= 0;
	       for (int i = 0; i < 4; i++) {
	           int shift= (4 - 1 - i) * 8;
	           value +=(b[i] & 0x000000FF) << shift;//往高位游
	       }
	       return value;
	}
	/**
	 * byte数组转成long     
	 * @param b
	 * @return long
	 */
	public static long byteArrayToLong(byte[] b) {
		long s = 0; 
		long s0 = b[0] & 0xff;// 最低位    
		long s1 = b[1] & 0xff;      
		long s2 = b[2] & 0xff;     
		long s3 = b[3] & 0xff;     
		long s4 = b[4] & 0xff;// 最低位    
		long s5 = b[5] & 0xff;       
		long s6 = b[6] & 0xff;     
		long s7 = b[7] & 0xff;      
		// s0不变  
		s1 <<= 8;     
		s2 <<= 16;    
		s3 <<= 24;     
		s4 <<= 8 * 4;   
		s5 <<= 8 * 5;     
		s6 <<= 8 * 6;     
		s7 <<= 8 * 7;    
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;  
		return s;  
		
	}
	
	/**
	 * long 转换为byteArray
	 * @param s
	 * @return byte[]
	 */
	public static byte[] longToByteArray(long s) {
		byte[] result = new byte[8];
		for (int i=0; i<8; i++) {
			int offset =(result.length -1 -i)*8;
			result[i] =  (byte) ((s >>> offset) & 0xff);  
		}
		return result;
	}
	/**
	 * int 转换为byteArray
	 * @param i
	 * @return byte[]
	 */	
	public static byte[] intToByteArray(int i) {   
		  byte[] result = new byte[4];   
		  result[0] = (byte)((i >> 24) & 0xFF);
		  result[1] = (byte)((i >> 16) & 0xFF);
		  result[2] = (byte)((i >> 8) & 0xFF); 
		  result[3] = (byte)(i & 0xFF);
		  return result;
		 }
	
	 /* ------------------------------------------------------------ */
    /**
     * Test if a string is null or only has whitespace characters in it.
     * <p>
     * Note: uses codepoint version of {@link Character#isWhitespace(int)} to support Unicode better.
     * 
     * <pre>
     *   isBlank(null)   == true
     *   isBlank("")     == true
     *   isBlank("\r\n") == true
     *   isBlank("\t")   == true
     *   isBlank("   ")  == true
     *   isBlank("a")    == false
     *   isBlank(".")    == false
     *   isBlank(";\n")  == false
     * </pre>
     * 
     * @param str
     *            the string to test.
     * @return true if string is null or only whitespace characters, false if non-whitespace characters encountered.
     */
    public static boolean isBlank(String str)
    {
        if (str == null) {
            return true;
        }
        int len = str.length();
        for (int i = 0; i < len; i++){
            if (!Character.isWhitespace(str.codePointAt(i))) {
                // found a non-whitespace, we can stop searching  now
                return false;
            }
        }
        // only whitespace
        return true;
    }
}
