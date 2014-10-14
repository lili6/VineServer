/**
 * 
 */
package vine.core.net.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.mrd.dolphin.data.cache.MemCachedTool;
import com.mrd.dolphin.disruptor.Disruptor;
import com.mrd.dolphin.disruptor.DisruptorFactory;
import com.mrd.dolphin.net.session.event.RemoveSessionDisruptorEvent;
import com.mrd.dolphin.net.session.event.UpdateSessionDisruptorEvent;

/**
 * MemCached数据缓存方式处理
 * @author PanChao
 */
public class MemSessionCache implements SessionCache {
	private static final Logger log = LoggerFactory.getLogger(MemSessionCache.class);
	public static final String USERSESSION_BASEKEY = "UserSession_";
	public static final String USERSESSION_LOCKERKEY = "UserSession_Locker_";
	public static final String USERSESSION_COUNTER_KEY = "UserSession_Counter";
	public static final String USERSESSIONIDS_KEY = "UserSessionIds_Key";
	/** UserSession的缓存数据副本，保存被取过的用户缓存数据 */
	protected Map<String, UserSession> sessionIdMap = new ConcurrentHashMap<>();
	protected Map<String, UserSession> userSessionMap = new ConcurrentHashMap<>();

	MemSessionCache(){
		MemCachedTool.client.storeCounter(USERSESSION_COUNTER_KEY, 0L);
		// 初始化删除缓存异步处理器
		RemoveSessionDisruptorEventHandler removeSessionHandler = new RemoveSessionDisruptorEventHandler();
		Disruptor<RemoveSessionDisruptorEvent> removeDis = new Disruptor<>(RemoveSessionDisruptorEvent.EVENT_FACTORY, removeSessionHandler);
		DisruptorFactory.registerDisruptor(RemoveSessionDisruptorEvent.class, removeDis);
		// 初始化更新缓存异步处理器
		UpdateSessionDisruptorEventHandler updateSessionHandler = new UpdateSessionDisruptorEventHandler();
		Disruptor<UpdateSessionDisruptorEvent> updateDis = new Disruptor<>(UpdateSessionDisruptorEvent.EVENT_FACTORY, updateSessionHandler);
		DisruptorFactory.registerDisruptor(UpdateSessionDisruptorEvent.class, updateDis);
	}
	
	/** 删除会话数据的异步处理器 */
	private class RemoveSessionDisruptorEventHandler implements EventHandler<RemoveSessionDisruptorEvent>{
		@Override
		public void onEvent(RemoveSessionDisruptorEvent event, long sequence,
				boolean endOfBatch) throws Exception {
			UserSession session = event.getSession();
			if (session == null) {
				if (log.isDebugEnabled()) { log.debug("移除用户会话失败，会话为null"); }
				return;
			}
			if (session.getUserId() != null && !session.getUserId().equals("")) {
				MemCachedTool.client.delete(USERSESSION_BASEKEY + session.getUserId());
				MemCachedTool.client.decr(USERSESSION_COUNTER_KEY, 1);
				userSessionMap.remove(session.getUserId());
				if (log.isDebugEnabled()) { log.debug("从userSessionMap中移除用户会话：" + session.getUserId()); }
			}
			if (session.getSessionId() != null && !session.getSessionId().equals("")) {
				MemCachedTool.client.delete(USERSESSION_BASEKEY + session.getSessionId());
				Set<String> ids = (Set<String>) MemCachedTool.client.get(USERSESSIONIDS_KEY);
				if (ids != null) {
					ids.remove(session.getSessionId());
					MemCachedTool.client.set(USERSESSIONIDS_KEY, ids);
				}
				sessionIdMap.remove(session.getSessionId());
				if (log.isDebugEnabled()) { log.debug("从sessionIdMap中移除用户会话：" + session.getSessionId()); }
			}
		}
	}
	
	/** 更新会话数据的异步处理器 */
	private class UpdateSessionDisruptorEventHandler implements EventHandler<UpdateSessionDisruptorEvent>{
		@Override
		public void onEvent(UpdateSessionDisruptorEvent event, long sequence,
				boolean endOfBatch) throws Exception {
			UserSession session = event.getSession();
			MemCachedTool.client.set(USERSESSION_BASEKEY + session.getSessionId(), session);
			sessionIdMap.remove(session.getSessionId());
			if (session.getUserId() != null && !session.getUserId().equals("")) {
				MemCachedTool.client.set(USERSESSION_BASEKEY + session.getUserId(), session);
				userSessionMap.remove(session.getUserId());
			}
		}
	}
	
	@Override
	public synchronized void add(UserSession session) {
		MemCachedTool.client.add(USERSESSION_BASEKEY + session.getSessionId(), session);
		Set<String> ids = (Set<String>) MemCachedTool.client.get(USERSESSIONIDS_KEY);
		if (ids == null) {
			ids = new HashSet<>();
		}
		ids.add(session.getSessionId());
		MemCachedTool.client.set(USERSESSIONIDS_KEY, ids);
		sessionIdMap.put(session.getSessionId(), session);
	}

	@Override
	public void bind(String userId, UserSession session) {
		if (session.getUserId() == null) {
			session.setUserId(userId);
		}
		// 如果不是踢人，而是第一次登录，在线玩家计数器加1
		if (!MemCachedTool.client.keyExists(USERSESSION_BASEKEY + userId)) {
			MemCachedTool.client.incr(USERSESSION_COUNTER_KEY, 1);
			MemCachedTool.client.add(USERSESSION_BASEKEY + userId, session);
		} else {
			MemCachedTool.client.set(USERSESSION_BASEKEY + userId, session);
		}
		userSessionMap.put(session.getUserId(), session);
	}

	@Override
	public void update(UserSession session) {
		Disruptor<UpdateSessionDisruptorEvent> dis = DisruptorFactory.getDisruptor(UpdateSessionDisruptorEvent.class);
		UpdateSessionDisruptorEvent event = dis.createNewEvent();
		event.setSession(session);
		dis.dispatchEvent(event);
	}
	
	@Override
	public synchronized void remove(UserSession session) {
		Disruptor<RemoveSessionDisruptorEvent> dis = DisruptorFactory.getDisruptor(RemoveSessionDisruptorEvent.class);
		RemoveSessionDisruptorEvent event = dis.createNewEvent();
		event.setSession(session);
		dis.dispatchEvent(event);
	}

	@Override
	public UserSession getByUserId(String userId) {
		if (log.isDebugEnabled()) {
			log.debug("userSessionMap中存在用户会话数量：" + 
					MemCachedTool.client.getCounter(USERSESSION_COUNTER_KEY, 0));
		}
		if (log.isDebugEnabled()) { log.debug("查找用户会话，UserId：" + userId); }
		if (userId == null) return null;
		UserSession session = userSessionMap.get(userId);
		if (session == null) {
			session = (UserSession) MemCachedTool.client.get(USERSESSION_BASEKEY + userId);
			if (session != null) {
				sessionIdMap.put(session.getSessionId(), session);
				userSessionMap.put(session.getUserId(), session);
			}
		}
		if (log.isDebugEnabled() && session != null) {
			log.debug("[SessionId:" + session.getSessionId() + "][IP:" + session.getRemoteAddress() + 
					"]：查找到的用户会话，UserId：" + userId);
		}
		return session;
	}

	@Override
	public UserSession getBySessionId(String sessionId) {
		UserSession session = sessionIdMap.get(sessionId);
		if (session == null) {
			session = (UserSession) MemCachedTool.client.get(USERSESSION_BASEKEY + sessionId);
			if (session != null) {
				sessionIdMap.put(session.getSessionId(), session);
				if (session.getUserId() != null) {
					userSessionMap.put(session.getUserId(), session);
				}
			}
		}
		return session;
	}

	@Override
	public List<UserSession> getAllSession() {
		Set<String> ids = (Set<String>) MemCachedTool.client.get(USERSESSIONIDS_KEY);
		List list = new ArrayList();
		if (ids != null) {
			String[] idArr = new String[ids.size()];
			int i = 0;
			for (String id : ids) {
				idArr[i] = USERSESSION_BASEKEY + id;
				i++;
			}
			Map map = MemCachedTool.client.getMulti(idArr);
			if (map == null) return (List<UserSession>) list;
			list.addAll(map.values());
		}
		return (List<UserSession>) list;
	}

	@Override
	public List<UserSession> getAllOnlineSession() {
		List<String> idList = new ArrayList<>();
		for (UserSession session : getAllSession()) {
			if (session == null || session.getUserId() == null) continue;
			idList.add(USERSESSION_BASEKEY + session.getUserId());
		}
		List list = new ArrayList();
		if (idList.size() > 0) {
			Map map = MemCachedTool.client.getMulti(idList.toArray(new String[0]));
			if (map == null) return (List<UserSession>) list;
			list.addAll(map.values());
		}
		return (List<UserSession>) list;
	}
	
	@Override
	public Collection<UserSession> getAllOnlineSessions() {
		List<String> idList = new ArrayList<>();
		for (UserSession session : getAllSession()) {
			if (session == null || session.getUserId() == null) continue;
			idList.add(USERSESSION_BASEKEY + session.getUserId());
		}
		Collection<UserSession> list = new ArrayList<>();
		if (idList.size() > 0) {
			Map map = MemCachedTool.client.getMulti(idList.toArray(new String[0]));
			if (map == null) return list;
			return map.values();
		}
		return (List<UserSession>) list;
	}

	@Override
	public int countAll() {
		return ((Set) MemCachedTool.client.get(USERSESSIONIDS_KEY)).size();
	}

	@Override
	public int countAllOnline() {
		return (int) MemCachedTool.client.getCounter(USERSESSION_COUNTER_KEY, 0);
	}
}
