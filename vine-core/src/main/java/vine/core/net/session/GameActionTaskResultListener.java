/**
 * 
 */
package vine.core.net.session;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.dolphin.net.action.ActionHandler;
import com.mrd.dolphin.net.action.ActionTaskResultListener;
import com.mrd.dolphin.net.packet.MessageFilterPool;
import com.mrd.dolphin.net.packet.MessageFilterResult;
import com.mrd.dolphin.net.packet.MessageOutFilter;
import com.mrd.dolphin.net.packet.Packet;
import com.mrd.dolphin.net.packet.PacketConst;

/**
 * 异步类业务执行结果处理监听器，用于向客户端返回消息
 * @author PanChao
 * @author liguofang
 */
public class GameActionTaskResultListener implements ActionTaskResultListener {
	private static final Logger log = LoggerFactory.getLogger(GameActionTaskResultListener.class);
	private UserSession[] sessions;
	public GameActionTaskResultListener(UserSession[] sessions) {
		this.sessions = sessions;
	}
	
	@Override
	public void receiveResult(ActionHandler handler, Packet data) {
		sendData(sessions, handler, data);
	}
	
	public static void sendData(UserSession[] sessions, ActionHandler handler, Packet packet) {
		int requestCmd = 1;
		if (handler != null) requestCmd = handler.getCommand();		
		boolean isPush = false;
		
		for (UserSession session : sessions) {
			if (packet == null ) {		//解锁，处理忽略	
				if(log.isDebugEnabled()) {log.debug("返回数据为null,[packetId={}]已解锁,处理忽略!!!", requestCmd);}
				session.unlock(requestCmd);
				return;
			}
			if (isPush) {
				if(log.isDebugEnabled()) {log.debug("是否推送消息:true");}
				session.push(packet);
			} else {
				//对输出消息的过滤处理，放到了UserSession中，此处不再处理 2014-7-30
				/*
				if (log.isDebugEnabled()) {log.debug("SendData 响应数据过滤前：[{}]", packet);}		
				// 对输出消息过滤
					List<MessageOutFilter> outFilters = MessageFilterPool.getMessageOutFilters();
					for (MessageOutFilter filter : outFilters) {
						MessageFilterResult result = filter.filter(session, packet);
						if (!result.isNext()) {
							if (log.isDebugEnabled()) { 
								log.debug("[IP:" + session.getRemoteAddress() + "]过滤器处理：" + filter);
							}
							packet = result.getPacket();
							if (log.isDebugEnabled()) { log.debug("过滤器处理返回数据：" + packet); }
							break;
						}
				}

				if (log.isDebugEnabled()) { log.debug("SendData 响应数据过滤后：[{}]", packet); }
				*/
				session.response(packet);
				// 不是因为命令执行中被锁定的过滤消息，解除锁定
				//当返回值不为-104时，则释放锁
				if (packet.getRetCode() != PacketConst.RETCODE_PACKETID_LOCKED) {
					session.unlock(requestCmd);
				}
			}
		}
	}
}
