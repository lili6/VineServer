/**
 * 
 */
package vine.core.net.session;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.net.packet.Packet;
import vine.core.net.packet.PacketConst;


/**
 * 基于Mian框架的用户会话实现类
 * @author PanChao
 * 添加对JSON，String包类型的支持
 * @author liguofang
 */
public class UserMinaSession extends UserSession implements Serializable {
	private static final long serialVersionUID = -2687594963507002321L;
	private static final Logger log = LoggerFactory.getLogger(UserMinaSession.class);
	/** mina底层会话对象 */
	private IoSession ioSession;
	//private ConcurrentHashMap<String, IOFlow>  pbCounts = new ConcurrentHashMap<>();
	/** 接收 消息包统计 */
	private Map<Integer, IOFlowBean>  inMap = new ConcurrentHashMap<Integer, IOFlowBean>();
	/** 响应消息包统计 */
	private Map<Integer, IOFlowBean>  outMap = new ConcurrentHashMap<Integer, IOFlowBean>();

	public Map<Integer, IOFlowBean> getIOFlow(int flag) {
		if (flag==1) {
			return inMap;
		} else {
			return outMap;
		}
	}

	/**
	 * 创建用户会话，用于用户成功登录系统时
	 * @param ioSession mina底层会话对象
	 */
	UserMinaSession(IoSession ioSession) {
		super();
		this.ioSession = ioSession;
		this.sessionId = String.valueOf(ioSession.getId());
	}
	
	@Override
	protected void setUserId(String userId) {
		super.setUserId(userId);
		MinaMessageCacheTask.getInstance().addListenSession(this);
	}
	
	@Override
	public String getRemoteAddress(){
		if (ioSession == null) return "";
		InetSocketAddress inetAddress = (InetSocketAddress) ioSession.getRemoteAddress();
		if(inetAddress == null || inetAddress.getAddress() == null) return "";
		
		return inetAddress.getAddress().getHostAddress();
	}

	
	
	@Override
	protected boolean destroy(boolean isPassive) {
		log.debug("会话开始销毁,isPassive:[{}],连接状态:[{}]",isPassive,connected);
		if (connected) {
			connected = false;
			if(!isPassive){//如果是主动销毁
				log.debug("服务器主动销毁：{}",!isPassive);
				if (ioSession != null && ioSession.isConnected()) {
					try {
						if(log.isDebugEnabled()){log.debug("服务器关闭Session true!");}
						ioSession.close(true);
					} catch (Exception e) {
						if (log.isWarnEnabled()) {
							log.warn("关闭MINA会话失败", e);
						}
					}
				}
			}
			log.debug("IoSession已经销毁，应用处理关闭前监听处理....");
			/**/
			if(log.isDebugEnabled()){log.debug("用户退出后从监听队列中移除该用户会话!");}
//			MinaMessageCacheTask.getInstance().removeListenSession(this);
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
					log.debug("MINA用户会话创建监听器开始执行：" );
				}
				for (UserSessionCreatedListener l : createdListeners) {
					l.onCreated(this);
				}
			}
		}
		return true;
	}

	
	@Override
	protected void responseImmediately(Packet packet){
		if(packet == null) return;
		
		packet.setFlag(PacketConst.VALUE_RESULT_PACKET_FLAG);
		packet.setStamp(System.currentTimeMillis());
		
		lastResponseMessage = packet;		
		//分情况来考虑响应数据的输出
		//willSendMsg = null;		 
		if (log.isDebugEnabled()) {
			responseTime = System.currentTimeMillis();
			log.debug("响应[" + (responseTime - receiveTime) + "ms]" + 
					"[MinaSessionId:" + sessionId + "][IP:" + getRemoteAddress() + "] before send Packet:" 
					+ lastResponseMessage);
		}
		//TODO 需要考虑PBHeader数据的拷贝
		if (ioSession != null) {
			
			//ioSession.write(lastResponseMessage);	
			WriteFuture wf = ioSession.write(lastResponseMessage);		
			wf.addListener(new ResponseListener(wf,packet));			
		}
	}
	
	@Override
	public void response(Packet packet){
		//outfilter处理
		beforeResponse(packet);
		
		//先发送等待队列中的消息，然后发送当前消息
		while (!waitMessageQueue.isEmpty()) {
			Packet p = waitMessageQueue.poll();
			responseImmediately(p); 
		}
		//发送当前消息包
		responseImmediately(packet); 
		
		
		/* TODO 需要改为异步返送
		if(packet == null) return;
		if (waitMessage == null) return;			
		//放入等待队列 
		if (log.isDebugEnabled()) { log.debug("加入等待队列，响应消息:\n[{}]" + packet); }	
		waitMessage.add(packet);
		if (log.isDebugEnabled()) { log.debug("等待队列长度[{}]", waitMessage.size()); }
		messageLastAddTime = System.currentTimeMillis();
		*/
	}
	
	@Override
	public void push(Packet message) {
		response(message);
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserMinaSession [ioSession=");
		builder.append(ioSession);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", attributes=");
		builder.append(attributes);
		builder.append(", waitMessage=");
		builder.append(waitMessage);
		builder.append("]");
		return builder.toString();
	}
	
	class ResponseListener implements IoFutureListener {
		WriteFuture wf;
		Packet packet;
		
		public ResponseListener(WriteFuture wf ,Packet packet) {
			this.wf = wf;
			this.packet = packet;
		}
		@Override
		public void operationComplete(IoFuture future) {
			log.debug("===============isWritten:{}",wf.isWritten());
			log.debug("===============isDone:{}",wf.isDone());
			if (wf.isDone()) { //判断是否真正将包发出去
				log.debug("===============消息发送完毕!");
				int packetId = packet.getPacketId();
				byte[] data = (byte[])packet.getData();
				if( !outMap.containsKey(packetId) ){
					IOFlowBean bean = new IOFlowBean();	
					bean.setCount(1);
					if(null != data){
						bean.setTotalRate(data.length);
					}
					outMap.put(packetId, bean);
				}else{
					IOFlowBean bean = outMap.get(packetId);
					bean.setCount(bean.getCount()+1);
					if(null != data){
						bean.setTotalRate(bean.getTotalRate()+data.length);
					}
					outMap.put(packetId, bean);
				}
			} else {
				if (!packet.isDiscarded()) {
					log.debug("网络拥堵，消息包[packetId={}]发送失败,存入队列稍后发送!",packet.getPacketId());				
					waitMessageQueue.offer(packet);
				} else {
					log.debug("网络拥堵，消息包[packetId={}]发送失败，已丢弃!",packet.getPacketId());
				}
			}
			
		}
		
	}
}
