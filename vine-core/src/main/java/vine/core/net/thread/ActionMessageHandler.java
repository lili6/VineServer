package vine.core.net.thread;

import vine.core.net.action.ActionRunner;
import vine.core.net.packet.Packet;
import vine.core.net.session.UserSession;

/**
 * Action直接处理类
 * 调用异步线程进行处理，无状态
 * @author liguofang
 *
 */
public class ActionMessageHandler implements IMessageHandler  {

	@Override
	public void execute(UserSession session, Packet packet) {
		ActionRunner.runAction(session, packet);
	}

}
