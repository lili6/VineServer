package vine.core.net.thread;

import vine.core.net.packet.Packet;
import vine.core.net.session.UserSession;

/**
 * 线程Queue中的消息对象
 * @author liguofang
 *
 */
public class ThreadMessage {

	private UserSession session;
	private Packet packet;
	
	public ThreadMessage(UserSession session,Packet packet) {
		this.setPacket(packet);
		this.setSession(session);
	}
	public UserSession getSession() {
		return session;
	}
	public void setSession(UserSession session) {
		this.session = session;
	}
	public Packet getPacket() {
		return packet;
	}
	public void setPacket(Packet packet) {
		this.packet = packet;
	}
	
	
}
