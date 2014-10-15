package vine.core.net.packet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.utils.StringUtil;

/**
 * 定义数据包抽象类
 * 支持Protobuf，Json，String固定格式的数据
 * 添加是否可被丢弃 isDiscarded
 * @author liguofang
 *
 */
public abstract class Packet{
    private static final Logger log = LoggerFactory.getLogger(Packet.class);
    
    /*消息包类型*/
    protected static PacketType packetType = null;
    /*消息对象类型*/
    public enum PacketType{
        JSON ,PB
    }
    /*为消息ID 用来路由Action*/
  	private int packetId = 0;
  	/*客户端消息的唯一标识，通常用客户端的时间毫秒来标示*/
  	private long stamp = 0l;
  	/*框架层标识的返回代码。0表示正常。其他表示错误代码*/
  	private int retCode = 0;	
  	/*预留，暂时没有作用*/
  	private int flag = 0;
  	
  	/*非网络层数据，包是否可被丢弃*/
  	private boolean isDiscarded = false;
  	
	/**
	 * 抽象方法子类实现，返回不同数据格式对象
	 * @return ProtoBuf, String ，JSONObject
	 */
  	public abstract Object getData(); 
	
  	public abstract void setData(Object data);
	
	public int getRetCode() {
		return retCode;
	}
	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
	
	public int getPacketId() {		
		return packetId;
	}
	public void setPacketId(int packetId) {
		this.packetId = packetId;
	}
	public long getStamp() {
		return stamp;
	}
	public void setStamp(long stamp) {
		this.stamp = stamp;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
			
	
	public boolean isDiscarded() {
		return isDiscarded;
	}

	public void setDiscarded(boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public static PacketType getPacketType() {
		return packetType;
	}
	
	/**
	 * 通讯数据byte[]数据流，解析为数据对象方法
	 * data是原始的通讯流数据
	 * @param data 原始数据
	 * @param type 包类型
	 * @return 封装好的数据对象
	 */
	public static Packet parseHttpRequest(byte[] data, PacketType type) {
		if (packetType == null) packetType = type;
		
		Packet packet = null;		 
		if (data.length < 24) {// bodyLength(4byte)+packetId(4byte) +stamp(8byte)+retCode(4byte)+flag(4byte)
			log.error("消息包长度必须大于24[bodyLen(4byte)+packetId(4byte) +stamp(8byte)+retCode(4byte)+flag(4byte)] 实际长度：[{}]",data.length);				
			return null;
		}
		//1、开始解析报文头数据
		byte[] bodyLen = new byte[4];
		System.arraycopy(data, 0, bodyLen, 0, 4);
		byte[] packetId = new byte[4];
		System.arraycopy(data, 4, packetId, 0, 4);
		byte[] stamp = new byte[8];
		System.arraycopy(data, 8, stamp, 0, 8);
		byte[] retCode = new byte[4];
		System.arraycopy(data, 16, retCode, 0, 4);
		byte[] flag = new byte[4];
		System.arraycopy(data, 20, flag, 0, 4);
		//消息体长度
		int len = StringUtil.byteArrayToInt(bodyLen);
		int _packetId = StringUtil.byteArrayToInt(packetId);
		long _stamp =StringUtil.byteArrayToLong(stamp);		
		int _retCode = StringUtil.byteArrayToInt(retCode);
		int _flag = StringUtil.byteArrayToInt(flag);		
		if (log.isInfoEnabled()) {log.info("消息头解码,dataLen:[{}],packetId:[{}],stamp:[{}],retCode:[{}],flag:[{}]", 
				len,_packetId,_stamp,_retCode,_flag);}		
		
		if (data.length < 24+len) {// bodyLength(4byte)+packetId(4byte) +stamp(8byte)+retCode(4byte)+flag(4byte)
			log.error("消息包长度必须大于:[{}],实际长度:[{}],拆包错误!",24+len,data.length);				
			return null;
		}
		byte[] body = new byte[len];
		System.arraycopy(data, 24, body, 0, len);
		if (type == PacketType.PB) { //protobuf类型			
			try {
				packet = new PbPacket(body);				
			} catch (Exception e) {
				log.error("数据解析出错:{}\n 消息:\n{}", e,StringUtil.bytes2HexStr(data));
				e.printStackTrace();
			}						
		} else {// 字符串类消息数据
			String str = new String(body);
			if (str == null || str.trim().length() == 0) {
				return null;
			}					
			try {
				if (packetType == PacketType.JSON) {// JSON字符串
					packet = new JsonPacket(str);					
				}
			} catch (Exception e) {
				log.error("数据解析出错:{}\n 消息:\n{}", e, str);
			}			
		}	
		//报文头公共部分处理
		packet.setPacketId(_packetId);
		packet.setStamp(_stamp);
		packet.setRetCode(_retCode);
		packet.setFlag(_flag);
		
		return packet;
	}
	/**
	 * 组包 HTTP Response
	 * @param packet
	 * @param type
	 * @return
	 * TODO　需要判断此包是正常包还是异常包，判断标准就是retCode＝0　均为正常包，否则为异常包。
	 * 异常包，均只返回报文头的信息，没有body信息
	 */
	public static byte[] packHttpResponse(Packet packet, PacketType type) {
		byte[] response = null;
		int retCode = packet.getRetCode();
		byte[] pid = StringUtil.intToByteArray(packet.getPacketId());
		byte[] retcode = StringUtil.intToByteArray(retCode);
		byte[] _stamp = StringUtil.longToByteArray(packet.getStamp());
		byte[] _flag = StringUtil.intToByteArray(packet.getFlag());
		//byte[]  head = new byte[24];
		
		
		if (type == PacketType.PB) { //protobuf类型			
			if (retCode == PacketConst.RETCODE_SUCCESS) { 
				byte[] body = (byte[])packet.getData();				
				byte[] result = new byte[24+body.length];// 不包含长度域。
				byte[] bodyLen = StringUtil.intToByteArray(body.length);
				System.arraycopy(bodyLen, 0, result,0 , 4);
				System.arraycopy(pid, 0, result,4 , 4);
				System.arraycopy(_stamp, 0, result,8 , 8);
				System.arraycopy(retcode, 0, result,16 , 4);
				System.arraycopy(_flag, 0, result,20 , 4);				
				System.arraycopy(body, 0, result,24 , body.length);	
				return result;
			} else { //对报文体不赋值
				byte[] result = new byte[24];// 包含长度域。				
				System.arraycopy(StringUtil.intToByteArray(0), 0, result,0 , 4); //报文长度为0
				System.arraycopy(pid, 0, result,4 , 4);
				System.arraycopy(_stamp, 0, result,8 , 8);
				System.arraycopy(retcode, 0, result,16 , 4);
				System.arraycopy(_flag, 0, result,20 , 4);	
				return result;
			}
		} else if(type == PacketType.JSON) {
			JSONObject json = new JSONObject(); 
			if (retCode == PacketConst.RETCODE_SUCCESS) {
				String message = (String)packet.getData();
				json = JSON.parseObject(message);
			}
			byte[] body = json.toJSONString().getBytes();
			byte[] result = new byte[24+body.length];// 不包含长度域。
			byte[] bodyLen = StringUtil.intToByteArray(body.length);
			System.arraycopy(bodyLen, 0, result,0 , 4);
			System.arraycopy(pid, 0, result,4 , 4);
			System.arraycopy(_stamp, 0, result,8 , 8);
			System.arraycopy(retcode, 0, result,16 , 4);
			System.arraycopy(_flag, 0, result,20 , 4);				
			System.arraycopy(body, 0, result,24 , body.length);	
			return result;						
		} else {
			log.error("PacketType[{}] 不存在，组包错误！", type);
		}		
		
		return response;
	}
	
	/**
	 * 根据消息包类型组错误响应包,只组报文头信息
	 * @param packetId
	 * @param retCode
	 * @param type
	 * @return Packet
	 */
	public static Packet packError(int packetId, int retCode, PacketType type) {
		Packet packet = null;		
		if (type == PacketType.PB) {
			packet = new PbPacket();						
		} else if (type == PacketType.JSON) {
			packet = new JsonPacket();
		} else {
			packet = new JsonPacket();
		}
		packet.setRetCode(retCode);
		packet.setFlag(PacketConst.VALUE_RESULT_PACKET_FLAG);
		packet.setStamp(System.currentTimeMillis()); //当前时间戳
		packet.setPacketId(packetId);		
		return packet;
	}
	/**
	 * 构造PbPacket对象
	 * @param packetId
	 * @param data
	 * @return
	 */
	public static Packet newPbPacket(int packetId, Object data) {
		Packet result = new PbPacket();
		result.setPacketId(packetId);
		result.setData(data);		
		return result;
	}
	/**
	 * 构造JsonPacket对象
	 * @param packetId
	 * @param data
	 * @return
	 */
	public static Packet newJsonPacket(int packetId, Object data) {
		Packet result = new JsonPacket();
		result.setPacketId(packetId);
		result.setData(data);		
		return result;
	}
	
	@Override
	public String toString() {
		return "Packet [packetId=" + packetId + ", stamp=" + stamp
				+ ", retCode=" + retCode + ", flag=" + flag + " data =" + getData() +"]";
	}

}
