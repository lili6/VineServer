package vine.core.net.packet;



/**
 * protobuf 格式的packet对象
 * Created by liguofang on 2014/5/12.
 */
public class PbPacket extends Packet {
	/*数据包内容*/
	private byte[] data;
	
	public PbPacket() {
		
	}
	public PbPacket(byte[] message) {
		super();
		this.packetType = PacketType.PB;
		this.setData(message);
	}
	
	@Override
	public byte[] getData() {
		return data;
	}
	@Override
	public void setData(Object data) {
		this.data = (byte[])data;
	}
}
