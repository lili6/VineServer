/**
 * 
 */
package vine.core.net.http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.config.Configuration;


/**
 * 防火墙工具类
 * @author PanChao
 */
public class Firewall {
	private static final Logger log = LoggerFactory.getLogger(Firewall.class);
	/** 默认计入防火墙警告次数的时间范围，单位：秒 */
	public static long WARN_TIME_INTERVAL = 60;
	/** 默认最大警告次数（时间范围内） */
	public static int WARN_COUNT = 100;
	/** 记录首次警告的时间 */
	private static Map<String, Long> firstWarnTimeMap = new HashMap<>();
	/** 记录时间范围内的警告次数 */
	private static Map<String, Long> warnCountMap = new HashMap<>();
	
	static {
		WARN_TIME_INTERVAL = Configuration.getInstance().getLong("firewall_warn_time_interval", WARN_TIME_INTERVAL);
		WARN_COUNT = Configuration.getInstance().getInteger("firewall_warn_count", WARN_COUNT);
	}
	
	/**
	 * 执行防火墙防护判断，以客户端IP为单位
	 * @param ip
	 * @return true－已执行防护，被拦截；false－未被拦截
	 */
	public static boolean defend(String ip) {
		long warnTime = WARN_TIME_INTERVAL;// 记录警告次数的时间范围
		int warnCount = WARN_COUNT;// 最大警告次数
		
		Long time = firstWarnTimeMap.get(ip);
		if (time == null || System.currentTimeMillis() >= time + warnTime * 1000) {// 第一次访问或超过时间范围
			firstWarnTimeMap.put(ip, System.currentTimeMillis());// 重设警告时间范围开始时间
			warnCountMap.put(ip, 0L);// 重设警告次数
		} else {// 还在上次的时间范围内
			Long count = warnCountMap.get(ip);
			if (count == null) count = 0L;
			if (count >= warnCount) {// 达到时间范围内要警告的最大次数
				log.warn(new StringBuffer("超过防火墙允许的[").append(warnTime).append("]秒内通讯[")
						.append(warnCount).append("]次，被拦截，IP：").append(ip).toString());
				return true;
			}
			count++;
			warnCountMap.put(ip, count);
		}
		return false;
	}
}
