/**
 * 
 */
package vine.core.net.session;

import java.util.Collection;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.dolphin.data.cache.CacheType;
import com.mrd.dolphin.net.ServerType;

/**
 * 用户会话管理抽象类，包含mina类长连接会话管理和http类短连接会话管理两种
 * @author PanChao
 * @author liguofang
 */
public abstract class UserSessionManager {
	private static final Logger log = LoggerFactory.getLogger(UserSessionManager.class);
	private static UserSessionManager manager = null;
	private static SessionCache cache = null;
	
	/**
	 * 初始化用户会话管理器，并指定服务器类型和数据缓存方式
	 * @param type
	 * @param cacheType
	 */
	public static void init(ServerType type, CacheType cacheType) {
		if (type == ServerType.MINA) {
			manager = new UserMinaSessionManager();
		} else if (type == ServerType.HTTP) {
			manager = new UserHttpSessionManager();
		}
		if (cacheType == CacheType.CLASS) {
			cache = new ClassSessionCache();
		} else if (cacheType == CacheType.MEMCACHED) {
			cache = new MemSessionCache();
		}		
	}
	
	/**
	 * 获取用户会话管理器。
	 * @return
	 */
	public static UserSessionManager getManager() {
		if (manager == null) {
			throw new RuntimeErrorException(new Error("用户会话管理器未初始化"));
		}
		return manager;
	}
	
	/**
	 * 获取用户数据缓存管理
	 * @return
	 */
	public static SessionCache getCache() {
		if (cache == null) {
			throw new RuntimeErrorException(new Error("用户会话管理器未初始化"));
		}
		return cache;
	}
		
	/**
	 * 将session与userId关联，一般在创建连接后，用户登录时调用
	 * @param sessionId
	 * @param userId
	 */
	public synchronized void registerByUserId(String sessionId, String userId){
		if (sessionId == null) return;
		if (userId == null) {
			log.error("userId不能为空，userId：{}", userId);
			return;
		}
		UserSession session = cache.getBySessionId(sessionId);
		if(session == null){
			log.error("session关联userId失败，sessionId:[{}],userId:[{}]", sessionId, userId);
			return;
		}
		session.setUserId(userId);
		cache.bind(userId, session);
		if (log.isDebugEnabled()) {
			log.debug("[SessionId:" + sessionId + "][IP:" + session.getRemoteAddress() + 
					"]：将Session与UserId关联，并保存入UserSessionMap，可登录：sessionId:" + sessionId + ",userId:" + userId);
		}
	}
	
	public UserSession getUserSessionByUserId(String userId){
		if (userId == null) {
			log.error("userId不能为空，userId:[{}]" , userId);
			return null;
		}
		UserSession session = cache.getByUserId(userId);
		return session; 
	}
	
	public UserSession getUserSessionBySessionId(String sessionId){
		return cache.getBySessionId(sessionId);
	}
	/**
	 * 关闭所有用户会话
	 */
	public void closeAllSession(){
		List<UserSession> list = cache.getAllSession();
		for(UserSession session : list){
			destroySession(session, false);
		}
	}
	
	/**
	 * 获取所有在线用户会话
	 * @return
	 */
	public Collection<UserSession> getAllOnlineSessions(){
		return cache.getAllOnlineSessions();
	}
	/**
	 * 获取所有Session
	 * */
	public List<UserSession> getAllSession(){
		return cache.getAllSession();
	}
	/**
	 * 统计当前缓存的所有用户会话数量
	 * @return
	 */
	public int countAllOnlineSessions(){
		return cache.countAllOnline();
	}
	
	/**
	 * 统计当前管理的会话数量
	 */
	public int countAllSessions(){
		return cache.countAll();
	}
	
	/**
	 * 正常销毁UserSession
	 * @param session
	 * @param isPassive 是否被动销毁会话
	 */
	public void destroySession(UserSession session, boolean isPassive){
		if (session == null) return;
		try {
			log.debug("UserSessionManager:destroySession:{}",isPassive);
			session.destroy(isPassive);
		} catch (Exception e) {
			log.error("销毁用户会话出错", e);
		} finally {
			cache.remove(session);
		}
	}
	
	/**
	 * 连接建立时触发
	 * @param connectSession
	 * @return
	 */
	public abstract UserSession createSession(Object connectSession);

	/**
	 * 用户会话被关闭时调用，清除相关资源，通过系统原始会话去查找UserSession，并调用destroySession
	 * @param connectSession
	 */
	public abstract void closeSession(Object connectSession);

}
