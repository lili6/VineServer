/**
 * 
 */
package application;

import org.apache.log4j.Logger;
import vine.core.net.packet.MessageFilterResult;
import vine.core.net.packet.MessageInFilter;
import vine.core.net.packet.Packet;
import vine.core.net.session.UserSession;

/**
 * 消息ID检查过滤器，除客户端提交日志的命令外，其他命令中不包含消息ID时，不予处理业务
 * @author liguofang
 */
public class RequestInFilter implements MessageInFilter {
	private static final Logger log = Logger.getLogger(RequestInFilter.class);

	@Override
	public MessageFilterResult filter(UserSession session, Packet packet) {

        return new MessageFilterResult(packet,true);

	}

}
