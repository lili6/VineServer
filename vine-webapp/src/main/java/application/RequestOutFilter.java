/**
 * 
 */
package application;

import org.apache.log4j.Logger;

import vine.core.net.packet.MessageFilterResult;
import vine.core.net.packet.MessageOutFilter;
import vine.core.net.packet.Packet;
import vine.core.net.session.UserSession;

/**
 * 标记用户数据是否作持久化处理的过滤器，只要玩家有过一次操作，就标记为要持久化
 * @author liguofang
 */
public class RequestOutFilter implements MessageOutFilter {
	private static final Logger log = Logger.getLogger(RequestOutFilter.class);

	@Override
	public MessageFilterResult filter(UserSession session, Packet packet) {
        return new MessageFilterResult(packet,true);
	}

}
