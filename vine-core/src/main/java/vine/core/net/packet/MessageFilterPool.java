/**
 * 
 */
package vine.core.net.packet;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息过滤器缓存池
 * @author liguofang
 * @author PanChao
 */
public class MessageFilterPool {
	private static List<MessageInFilter> messageInFilters = new ArrayList<MessageInFilter>();
	private static List<MessageOutFilter> messageOutFilters = new ArrayList<MessageOutFilter>();
	
	/**
	 * 添加消息过滤器
	 * @param filter
	 */
	public static void addMessageFilter(MessageFilter filter) {
		if (filter instanceof MessageInFilter) {
			messageInFilters.add((MessageInFilter) filter);
		} else if (filter instanceof MessageOutFilter) {
			messageOutFilters.add((MessageOutFilter) filter);
		}
	}
	public static List<MessageInFilter> getMessageInFilters() {
		return messageInFilters;
	}

	public static List<MessageOutFilter> getMessageOutFilters() {
		return messageOutFilters;
	}
}
