package vine.core.net.thread;


import vine.core.net.packet.Packet;
import vine.core.net.session.UserSession;

/**
 * 消息处理器接口，由ActionRunner进行调用
 * @author liguofang
 *
 */
public interface IMessageHandler {
	/**
	 * 执行消息
	 * @param session
	 * @param packet
	 */
	public void execute(UserSession session, Packet packet);
}
