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

/**
 * MemCached数据缓存方式处理
 * @author liguofang
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

	}
	

	@Override
	public synchronized void add(UserSession session) {

	}

	@Override
	public void bind(String userId, UserSession session) {

	}

	@Override
	public void update(UserSession session) {

	}
	
	@Override
	public synchronized void remove(UserSession session) {

	}

	@Override
	public UserSession getByUserId(String userId) {

		return null;
	}

	@Override
	public UserSession getBySessionId(String sessionId) {

		return null;
	}

	@Override
	public List<UserSession> getAllSession() {
		List list = new ArrayList();

		return (List<UserSession>) list;
	}

	@Override
	public List<UserSession> getAllOnlineSession() {
		List<String> idList = new ArrayList<>();

		return null;
	}
	
	@Override
	public Collection<UserSession> getAllOnlineSessions() {
        return null;
	}

	@Override
	public int countAll() {
        return 0;
	}

	@Override
	public int countAllOnline(){
        return 0;
	}
}
