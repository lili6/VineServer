/**
 * 
 */
package vine.core.net.packet;

import vine.core.net.session.UserSession;

/**
 * 进入消息数据过滤器接口
 * 将在接收到消息并解析后，业务代码调用前被执行，可以有多个消息数据过滤器
 * @author liguofang
 * @author PanChao
 */
public interface MessageFilter {
	
	/**
	 * 处理过滤
	 * @param session
	 * @param packet
     *
	 * @return 过滤结果对象
	 */
	public MessageFilterResult filter(UserSession session, Packet packet);
}
