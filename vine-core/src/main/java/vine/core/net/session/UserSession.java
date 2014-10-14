/**
 * 
 */
package vine.core.net.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.dolphin.net.packet.MessageFilterPool;
import com.mrd.dolphin.net.packet.MessageFilterResult;
import com.mrd.dolphin.net.packet.MessageOutFilter;
import com.mrd.dolphin.net.packet.Packet;
import com.mrd.dolphin.stat.IOFlowBean;
import com.mrd.dolphin.thread.ThreadMessage;
/**
 * 用户会话状态管理类
 * 
 * @author liguofang
 */
public abstract class UserSession implements Serializable {
	private static final long serialVersionUID = 1350826943045479613L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);
	
	protected String sessionId;
	/** User Account ID for the common users or Service ID for the servers */
	protected String userId;
	
	protected boolean isRealPlayer = true;
	/** 连接状态 */
	protected boolean connected = true;
	/** 是否压缩发送的数据，默认不压缩 */
	protected boolean compressed = false;
	/** 所有需要放到会话中属性列表*/
	protected Map<String, Object> attributes = new HashMap<>();
	
	protected transient Set<Integer> requestLocker = new ConcurrentSkipListSet<>();
	/** 上次添加消息到缓存的时间 */
	protected transient long messageLastAddTime = 0;
	/** 上次实际发送给客户端的消息 */
	protected transient Packet lastResponseMessage = null;	
	/** 缓存中等待的消息 */
	protected transient List<Packet> waitMessage = Collections.synchronizedList(new ArrayList<Packet>());
	protected ConcurrentLinkedQueue<Packet> waitMessageQueue = new ConcurrentLinkedQueue<Packet>();
	
	/** Action发送的消息计数 */
	protected long sentCount = 0;
	
	/** 用户会话关闭监听器 */
	protected List<UserSessionClosedListener> closedListeners = new ArrayList<>();
	/** 用户会话创建监听器 */
	protected List<UserSessionCreatedListener> createdListeners = new ArrayList<>();
	/** 接收到消息的时间 */
	protected long receiveTime = 0;
	/** 响应消息的时间 */
	protected long responseTime = 0;
	/** 服务器标识，标识所属集群节点 */
	protected String serverTag = "";
	/** 最后一次收到用户请求的时间，用于判断会话是否过期 */
	protected long lastRequestTime = 0;
	/** 会话绑定的线程ID，缺省是ActionMessageHandler */
	protected Long handleThreadId = null;
	
	/**
	 * 每个session对应的请求消息队列
	 */
	protected ConcurrentLinkedQueue<ThreadMessage> msgQueue = null;
	
	public ConcurrentLinkedQueue<ThreadMessage> getMsgQueue() {
		return msgQueue;
	}
	
	
	/**
	 * 添加消息到对应的回话队列中
	 * @param session
	 * @param packet
	 */
	public void addMessage(UserSession session, Packet packet) {
		ThreadMessage tm = new ThreadMessage(session,packet);
		Long id = session.getHandleThreadId();			
		if (msgQueue == null) {
			msgQueue = new ConcurrentLinkedQueue<ThreadMessage>();
		}
		msgQueue.add(tm);
		log.info("会话中[{}]线程[{}]加入新的消息[{}],当前队列消息数量：[{}]",session.getSessionId(),id,packet.getPacketId(), msgQueue.size());
	}
	/**
	 * 删除queue中存在的所有消息
	 */
	public void removeAll(UserSession session){		
		log.info("清除msgQueue中所有消息,当前长度Size:[{}]!",msgQueue.size());
		msgQueue.clear();
	}
	
	public Long getHandleThreadId() {
		return handleThreadId;
	}

	public void setHandleThreadId(Long handleThreadId) {
		this.handleThreadId = handleThreadId;
	}

	/**
	 * 获取用户业务ID
	 * @return
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * 设置用户唯一业务ID，用于识别用户身份
	 * @param userId 用户登录系统的唯一ID
	 */
	protected void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * 为用户会话设置属性值
	 * @param key
	 * @param value
	 * @return
	 */
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	/**
	 * 获取用户会话中保存的属性值
	 * @param key
	 */
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	/**
	 * 获取用户会话中保存的属性值，并转换为指定对象
	 * @param key
	 * @param clazz 
	 */
	public <T> T getAttribute(String key, Class<T> clazz) {
		Object obj = attributes.get(key);
		if (obj == null) {
			return null;
		}
		Class objClazz = obj.getClass();
		Class subClazz = null;
		try {
			subClazz = objClazz.asSubclass(clazz);
			return (T) obj;
		} catch (ClassCastException ex) {
			log.error("获取用户属性失败，key：" + key, ex);
		}
		return null;
	}
	
	/**
	 * 锁定用户请求的处理，防止重复请求
	 * @param packetId 请求的消息包ID
	 */
	public void lock(int packetId) {
		if (requestLocker == null) requestLocker = new ConcurrentSkipListSet<>();
		requestLocker.add(packetId);
	}
	
	/**
	 * 解锁用户请求的处理
	 * @param packetId 请求的消息包ID
	 */
	public void unlock(int packetId) {
		if (requestLocker != null) {
			requestLocker.remove(packetId);
		}
	}
	
	/**
	 * 检查用户请求处理是否已锁定
	 * @param packetId 请求的消息包ID
	 * @return
	 */
	public boolean isLocked(int packetId) {
		if (requestLocker == null) return false;
		return requestLocker.contains(packetId);
	}
	
	/**
	 * 清除所有已锁定的请求
	 */
	public void clearLocks() {
		if (requestLocker != null)
			requestLocker.clear();
	}
	
	/**
	 * 判断用户是否在线(以已登录为在线依据)
	 * @return
	 */
	public boolean isOnline() {
		if (sessionId == null) return false;
		if (userId == null) return false;
		boolean isOnline = true;
		isOnline &= UserSessionManager.getManager().getUserSessionByUserId(userId) != null; 
		return isOnline;
	}
	
	
	/**
	 * 立即响应消息，不缓冲
	 * @param message 要发送的消息对象
	 */
	protected abstract void responseImmediately(Packet packet);
	
	/**
	 * 向客户端响应消息，缓冲处理
	 * @param message 要发送的消息对象
	 */
	public abstract void response(Packet packet);
	
	/**
	 * 获取请求和响应的的统计信息
	 * @param flag 1:input ,0: output
	 * @return
	 */
	public abstract Map<Integer, IOFlowBean> getIOFlow(int flag) ;
	/**
	 * 向客户端推送消息，缓冲处理
	 * @param message
	 */
	public abstract void push(Packet packet);
	/**
	 * 消息发送前处理
	 * @param packet
	 */
	protected void beforeResponse(Packet packet) {
		if (log.isDebugEnabled()) {log.debug("beforeResponse响应数据过滤前[{}]", packet);}		
		// 对输出消息过滤
			List<MessageOutFilter> outFilters = MessageFilterPool.getMessageOutFilters();
			for (MessageOutFilter filter : outFilters) {
				MessageFilterResult result = filter.filter(this, packet);
				if (!result.isNext()) {
					if (log.isDebugEnabled()) { 
						log.debug("[IP:" + this.getRemoteAddress() + "]过滤器处理：" + filter);
					}
					packet = result.getPacket();
					if (log.isDebugEnabled()) { log.debug("beforeResponse过滤器处理返回数据[{}]", packet); }
					break;
				}
		}
		if (log.isDebugEnabled()) { log.debug("beforeResponse响应数据过滤后[{}]", packet); }
	}
	/**
	 * 获取等待发送的消息，并清空消息等待队列
	 * TODO 需要检查正确性
	 * @return
	 */
	public Packet popWaitMessage() {
		if (waitMessage == null) return null;
		int size = waitMessage.size();
		if (size == 0) return null;
		Packet data = null;
		if(size == 1){
			data = waitMessage.get(0);
			waitMessage.clear();
			return data;
		}
		List<Packet> dataList = new ArrayList<>();
		for (Packet data2 : waitMessage) {
			dataList.add(data2);
		}
		waitMessage.clear();
		return data;
	}
	/**
	 * 销毁用户会话
	 * @param isPassive 是否被动销毁，如断开连接，超时等
	 * @return
	 */
	protected abstract boolean destroy(boolean isPassive);
	
	/**
	 * 新建用户会话
	 * @return
	 */
	protected abstract boolean create();
	
	/**
	 * 获取用户会话ID
	 * @return
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * 获取远程客户端IP地址
	 * @return
	 */
	public abstract String getRemoteAddress();
	
	/**
	 * 添加用户会话关闭监听器，将在用户会话被关闭后触发
	 * @param listener
	 */
	public void addClosedListener(UserSessionClosedListener listener) {
		closedListeners.add(listener);
	}
	
	/**
	 * 移除用户会话关闭监听器
	 * @param listener
	 * @return
	 */
	public boolean removeClosedListener(UserSessionClosedListener listener) {
		return closedListeners.remove(listener);
	}
	
	/**
	 * 获取所有会话关闭监听器
	 * @return
	 */
	public List<UserSessionClosedListener> getClosedListeners(){
		return closedListeners;
	}
	
	/**
	 * 清除所有用户会话关闭监听器
	 */
	public void clearClosedListener() {
		closedListeners.clear();
	}
	
	/**
	 * 添加用户会话创建监听器，将在用户会话被创建后触发
	 * @param listener
	 */
	public void addCreatedListener(UserSessionCreatedListener listener) {
		createdListeners.add(listener);
	}
	
	/**
	 * 移除用户会话创建监听器
	 * @param listener
	 * @return
	 */
	public boolean removeCreatedListener(UserSessionCreatedListener listener) {
		return closedListeners.remove(listener);
	}
	
	/**
	 * 获取所有会话创建监听器
	 * @return
	 */
	public List<UserSessionCreatedListener> getCreatedListeners(){
		return createdListeners;
	}
	
	/**
	 * 清除所有用户会话创建监听器
	 */
	public void clearCreatedListener() {
		createdListeners.clear();
	}
	

	/**
	 * 获取缓存中等待的消息
	 * @return
	 */
	public  List<Packet> getWaitMessage() {
		return waitMessage;
	}

	/**
	 * 上次添加消息到缓存的时间
	 * @return
	 */
	public long getMessageLastAddTime() {
		return messageLastAddTime;
	}

	/**
	 * 上次实际发送给客户端的消息
	 * @return
	 */
	public Packet getLastResponseMessage() {
		return lastResponseMessage;
	}

	/**
	 * 获取所有用户会话中保存的信息
	 * @return
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * 重设用户会话中所有保存的信息
	 * @param attributes
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes.putAll(attributes);
	}
	
	/**
	 * 判断该UserSession是否由真实玩家客户端创建的。
	 * 如果是服务器通讯的UserSession，该值为false.
	 * @return
	 */
	public boolean isRealPlayer(){
		return this.isRealPlayer;
	}
	
	public void setIsRealPlayer(boolean isReal){
		this.isRealPlayer = isReal;
	}	
	
	
	public boolean isConnected() {
		return connected;
	}

	public boolean isCompressed() {
		return compressed;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getServerTag() {
		return serverTag;
	}

	public void setServerTag(String serverTag) {
		this.serverTag = serverTag;
	}

	public long getLastRequestTime() {
		return lastRequestTime;
	}

	public void setLastRequestTime(long lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("Session, From:").append(this.getRemoteAddress());
		buff.append(", sessionId:").append(this.getSessionId());
		buff.append(", user:").append(this.userId);
		buff.append(", type:").append(this.getClass().getSimpleName());
		return buff.toString();
	}
}
