package vine.core.net.thread;

import vine.core.net.packet.Packet;
import vine.core.net.session.UserSession;

/**
 * 获取ThreadId，根据id获取线程
 * 将消息放到线程对应的Queue中
 * @author liguofang
 * 2014-7-23 将消息放到该session对应的队列中
 *
 */
public class ThreadMessageHandler implements IMessageHandler {

	@Override
	public void execute(UserSession session, Packet packet) {
		Long threadId = session.getHandleThreadId();
		LogicThread lt = ThreadPoolManager.selectThread(threadId);
//		lt.addMessage(session, packet);
		session.addMessage(session, packet);
	}

}
