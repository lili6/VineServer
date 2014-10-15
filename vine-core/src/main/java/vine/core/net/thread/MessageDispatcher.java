package vine.core.net.thread;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.net.packet.MessageFilterPool;
import vine.core.net.packet.MessageFilterResult;
import vine.core.net.packet.MessageInFilter;
import vine.core.net.packet.Packet;
import vine.core.net.session.GameActionTaskResultListener;
import vine.core.net.session.UserSession;

/**
 * 消息调度器
 * 
 * @author liguofang
 *
 */
public class MessageDispatcher {
	
	private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);
	public static void main(String[] args) {
		
	}
	
	public static void dispatch(UserSession session, Packet packet) {
		List<MessageInFilter> filters = MessageFilterPool.getMessageInFilters();
		if (filters != null && filters.size() > 0) {
			for (MessageInFilter filter : filters) {
				MessageFilterResult result = filter.filter(session, packet);
				if (null != result && !result.isNext()) { //如果不需要继续处理业务	
					if (log.isWarnEnabled()) { log.warn("sessionId[{}],packetid:[{}]处理终止!!",session.getSessionId(), packet.getPacketId());}
					GameActionTaskResultListener.sendData(new UserSession[]{session}, null, packet);
					// 解锁 session.unlock(packet.getPacketId()); //不需要再此解锁
					return;
				}
				
			}
		}		
		if (session.getHandleThreadId() != null) {
			if (log.isDebugEnabled()) { log.debug("会话绑定的ThreadId:[{}]packetid:[{}]",session.getHandleThreadId(),packet.getPacketId());}
			IMessageHandler threadHandler = new ThreadMessageHandler();
			threadHandler.execute(session, packet);
		} else {
			if (log.isDebugEnabled()) { log.debug("会话没有绑定处理的Thread,按正常Action流程处理...");}
			IMessageHandler actionHandler = new ActionMessageHandler();
			actionHandler.execute(session, packet);
		}		
	}

}
