/**
 * 
 */
package vine.core.net.session;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.dolphin.net.packet.Packet;
import com.mrd.dolphin.net.packet.Packet.PacketType;
import com.mrd.dolphin.net.packet.PacketConst;
import com.mrd.dolphin.stat.IOFlowBean;
import com.mrd.dolphin.utils.StringUtil;

/**
 * 基于Http的用户会话实现类
 * @author PanChao
 * @author liguofang
 */
public class UserHttpSession extends UserSession implements Serializable {
	private static final long serialVersionUID = 5233110833583718220L;
	private static final Logger log = LoggerFactory.getLogger(UserHttpSession.class);
	private transient HttpSession httpSession;
	/*每个packetId对应一个stream*/
	private transient Map<String, OutputStream> streams = new HashMap<>();
	private String remoteAddr;

	UserHttpSession(HttpSession httpSession){
		this.httpSession = httpSession;
		this.sessionId = httpSession.getId();
	}
	
	@Override
	public void responseImmediately(Packet packet) {
		log.info("responseImmediately.....");
		if(packet == null) {
			log.error("返回packet=null,处理忽略!!!");
			return;
		}
		// 不在这里检查是否在线，外面已做检查
		//分情况来考虑响应数据的输出
		byte[] willSendMsg = null;
		if (packet.getPacketType() == PacketType.PB) {
			willSendMsg = Packet.packHttpResponse(packet, PacketType.PB);
		} else if (packet.getPacketType() == PacketType.JSON) {			
			willSendMsg = Packet.packHttpResponse(packet, PacketType.JSON);
		} else if (packet.getPacketType() == PacketType.STRING) {
			willSendMsg = Packet.packHttpResponse(packet, PacketType.STRING);
		}
		 
		int command = packet.getPacketId();// 使用请求的命令编号作为获取缓存的OutputStream的ID//		
		if (command > 0) {
//			OutputStream stream = streams.get(command);
			OutputStream stream = streams.get(this.sessionId); //根据sessionId获取缓存的OutputStream			
			if(log.isDebugEnabled()) {log.debug("==packetId[{}],sessionId[{}],stream:{},",command,this.sessionId, stream);}
			try {
				stream.write(willSendMsg);
				log.info("PacketId[{}]发送数据完毕! \n 响应buffer:\n[{}]",command,StringUtil.bytes2HexStr(willSendMsg));
			} catch (Exception e) {
				log.error("响应数据出错：MessageId[{}]" , command); // TODO 设置响应数据
				e.printStackTrace();
			} finally{
				try {
					if(stream!=null)
						stream.close();
				} catch (Exception e) {	
					log.error("packetId[{}]响应数据关闭输出流异常：{},{}",command,e.getCause(),e.getMessage());
					
				}
				if(log.isDebugEnabled()) {log.debug("移除缓存的OutputStream,SessionId[{}]",this.sessionId);}
				streams.remove(this.sessionId);
			}			
		}
	}

	/*
	 * HTTP类的push消息全部将其缓存入用户的数据待取对象中，等待客户端自动检查是否有新数据
	 */
	@Override
	public void response(Packet packet) {
		beforeResponse(packet);
		responseImmediately(packet);
	}
	
	@Override
	public void push(Packet packet) {
		if(packet == null) return;
		// 不在这里检查是否在线，外面已做检查
		if (waitMessage == null) return;
		waitMessage.add(packet);
		messageLastAddTime = System.currentTimeMillis();
	}

	@Override
	protected boolean destroy(boolean isPassive) {
		if (connected) {
			connected = false;
			if(!isPassive){//如果是主动销毁
				try {
					if (httpSession != null) httpSession.invalidate();
				} catch (Exception e) {
					if (log.isDebugEnabled()) {
						log.debug("HTTP会话已关闭，不能重复关闭，HttpSessionId：" + sessionId);
					}
				}
			}
			if (closedListeners != null) {
				for (UserSessionClosedListener l : closedListeners) {
					l.onClosed(this);
				}
			}
		}
		return true;
	}
	@Override
	protected boolean create() {
		if (connected) {
			if (createdListeners != null) {
				if (log.isDebugEnabled()) {
					log.debug("HTTP用户会话创建监听器开始执行：" );
				}
				for (UserSessionCreatedListener l : createdListeners) {
					l.onCreated(this);
				}
			}
		}
		return true;
	}

	@Override
	public String getRemoteAddress() {
		return this.remoteAddr;
	}
	
	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.remoteAddr = httpRequest.getRemoteAddr();
		String encoding = httpRequest.getHeader("Accept-Encoding");
		if (encoding != null && encoding.indexOf("compress") > -1) {
			this.compressed = true;
		}else{
			this.compressed = false;
		}
	}
	
		
	/**
	 * 缓存HttpServletResponse对象
	 * @param sessionId 使用请求命令标识请求的唯一ID，用于向客户端发送数据时从缓存中查找response
	 * @param httpResponse
	 */
	public void putHttpResponse(String sessionId, HttpServletResponse httpResponse) {
		if (httpResponse != null) {
			try {
				httpResponse.setCharacterEncoding(PacketConst.MESSAGE_DEFAULT_ENCODING);
				httpResponse.setContentType("text/html;charset=" + PacketConst.MESSAGE_DEFAULT_ENCODING);
				httpResponse.setHeader("Pragma","No-cache");
				httpResponse.setDateHeader("Expires",0);
				httpResponse.setHeader("Cache-Control","no-cache");
				OutputStream stream = httpResponse.getOutputStream();
				if (streams == null) streams = new HashMap<>();
				if(log.isDebugEnabled()) {log.debug("设置输出流缓存:SessionId[{}]",sessionId);}
				streams.put(sessionId, stream);
				if(log.isDebugEnabled()) {log.debug("设置输出流缓存Streams:[{}]",streams);}
			} catch (Exception e) {
				log.error("获取用于发送数据的Http PrintWriter出错!",e);
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserHttpSession [httpSession=");
		builder.append(httpSession);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", attributes=");
		builder.append(attributes);
		builder.append(", waitMessage=");
		builder.append(waitMessage);
		builder.append("]");
		return builder.toString();
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	@Override
	public Map<Integer, IOFlowBean> getIOFlow(int flag) {		
		return null;
	}
		
}
