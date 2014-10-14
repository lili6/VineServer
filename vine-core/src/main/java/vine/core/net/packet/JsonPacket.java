package vine.core.net.packet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * JSON 格式的packet对象
 * Created by liguofang on 2014/5/12.
 */
public class JsonPacket extends Packet {
	
	/*JSON对象内容*/
	private String data;
	public JsonPacket() {
		
	}
	public JsonPacket(String message) {
		super();
		this.packetType = PacketType.JSON;
		this.data = message;
//		com.alibaba.fastjson.JSONObject json = JSON.parseObject(message);
//		//解析PACKET 公共信息
//		Integer packetId = json.getInteger(PacketConst.PACKET_ID);// 解析得到请求消息ID		
//		if (packetId != null) {	this.setPacketId(packetId);	}
//		Long stamp = json.getLong(PacketConst.PACKET_STAMP);// 解析得到请求时间戳
//		if (stamp != null) {this.setStamp(stamp);}		
//		Integer retCode = json.getInteger(PacketConst.PACKET_RET_CODE);
//		if (retCode != null) {this.setRetCode(retCode);}
//		Integer flag = json.getInteger(PacketConst.PACKET_FLAG);
//		if (flag != null) {this.setFlag(flag);}
	}
	@Override
	public String getData() {
		return data;
	}
	@Override
	public void setData(Object data) {
		this.data = (String)data;
	}

	

}
