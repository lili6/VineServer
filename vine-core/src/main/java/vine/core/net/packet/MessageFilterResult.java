/**
 * 
 */
package vine.core.net.packet;


/**
 * 消息过滤结果
 * @author liguofang
 * @author PanChao
 * 
 */
public class MessageFilterResult {
	/** 过滤器收到的数据 */
	private Packet packet;
	/** 过滤后要返回给客户端的数据，仅在停止后面的业务处理时才使用 */
	/** 是否继续业务处理，true为继续，false为停止 */
	private boolean next = true;	
	/**
	 * 构造过滤结果对象，并设置为继续处理下一步业务
	 * @param packet 过滤器收到的数据
	 */
	public MessageFilterResult(Packet packet) {
		super();
		this.packet = packet;
	}
	/**
	 * 构造过滤结果对象
	 * @param packet 过滤器收到的数据
	 * @param next 是否继续业务处理，true为继续，false为停止
	 */
	public MessageFilterResult(Packet packet, boolean next) {
		super();
		this.packet = packet;
		this.next = next;
	}
	/** 过滤器收到的数据 */
	public Packet getPacket() {
		return packet;
	}
	public void setPacket(Packet packet) {
		this.packet = packet;
	}
	/** 是否继续业务处理，true为继续，false为停止 */
	public boolean isNext() {
		return next;
	}
	public void setNext(boolean next) {
		this.next = next;
	}

	/** 过滤后要返回给客户端的数据，仅在停止后面的业务处理时才使用 */

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageFilterResult [message=");
		builder.append(packet);		
		builder.append(", next=");
		builder.append(next);
		builder.append("]");
		return builder.toString();
	}
}
