/**
 * 
 */
package vine.core.net.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于应用程序内部的数据缓存类
 * @author PanChao
 * @author liguofang
 */
public class ClassSessionCache implements SessionCache {
	private static final Logger log = LoggerFactory.getLogger(ClassSessionCache.class);
	protected Map<String, UserSession> sessionIdMap = new ConcurrentHashMap<String, UserSession>();
	protected Map<String, UserSession> userSessionMap = new ConcurrentHashMap<String, UserSession>();
	
	ClassSessionCache(){}
	
	@Override
	public void add(UserSession session) {
		sessionIdMap.put(session.getSessionId(), session);
	}
	
	@Override
	public void bind(String userId, UserSession session) {
		if (session.getUserId() == null) {
			session.setUserId(userId);
		}
		userSessionMap.put(userId, session);
	}

	@Override
	public void update(UserSession session) {
		return;
	}
	
	@Override
	public void remove(UserSession session) {
		if (session == null) {
			if (log.isDebugEnabled()) { log.debug("移除用户会话失败，会话为null"); }
			return;
		}
		if (session.getUserId() != null && !session.getUserId().equals("")) {
			userSessionMap.remove(session.getUserId());
			if (log.isDebugEnabled()) { log.debug("从userSessionMap中移除用户会话：[{}]" , session.getUserId()); }
		}
		if (session.getSessionId() != null && !session.getSessionId().equals("")) {
			sessionIdMap.remove(session.getSessionId());
			if (log.isDebugEnabled()) { log.debug("从sessionIdMap中移除用户会话：[{}]" , session.getSessionId()); }
		}
	}
	
	@Override
	public UserSession getByUserId(String userId) {
		if (userId == null) return null;
		UserSession session = userSessionMap.get(userId);
		return session;
	}
	
	@Override
	public UserSession getBySessionId(String sessionId) {
		return sessionIdMap.get(sessionId);
	}

	@Override
	public List<UserSession> getAllSession() {
		return new ArrayList<UserSession>(sessionIdMap.values());
	}

	@Override
	public List<UserSession> getAllOnlineSession() {
		return new ArrayList<UserSession>(userSessionMap.values());
	}
	
	@Override
	public Collection<UserSession> getAllOnlineSessions(){
		return userSessionMap.values();
	}
	
	@Override
	public int countAll() {
		return sessionIdMap.size();
	}

	@Override
	public int countAllOnline() {
		return userSessionMap.size();
	}
}
