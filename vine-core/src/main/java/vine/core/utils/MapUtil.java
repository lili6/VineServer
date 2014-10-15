/**
 * 
 */
package vine.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 林详棋
 */
public class MapUtil {
	
	public static String mapToStr(Map<Long,Long> map){
		if(map == null) return "";
		StringBuffer sb = new StringBuffer();
		for(Object obj : map.keySet()){
			sb.append(obj).append(",").append(map.get(obj)).append(";");
		}
		return sb.toString();
	}
	
	public static Map<Long,Long> strToMap(String str){
		if(str == null || str.equals("")) return null;
		Map<Long,Long> map = new HashMap<Long,Long>();
		String[] arr = str.split(";");
		for (int i = 0; i < arr.length; i++) {
			String[] arr1 = arr[i].split(",");
			map.put(Long.valueOf(arr1[0]), Long.valueOf(arr1[1]));
		}
		return map;
	}

}
